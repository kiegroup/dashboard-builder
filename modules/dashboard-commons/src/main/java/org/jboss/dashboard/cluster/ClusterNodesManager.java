/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.cluster;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.dashboard.annotation.Destroyable;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;

import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * A platform component that manages the nodes connected to the cluster, if any.
 *
 * NOTE: BZ-1014612
 */
@ApplicationScoped
public class ClusterNodesManager implements Startable, Destroyable {
    public static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClusterNodesManager.class.getName());

    /** The id for this node. **/
    private Long currentNodeId;

    /**
     * Use highest priority to register the nodes and their statuses as quick as possible on startup.
     *
     * It must has lower priority than startable <code>HibernateInitializer</code> in order to have a valid Hibernate session.
     * @return An urgent priority.
     */
    public Priority getPriority() {
        return Priority.HIGH;
    }

    /**
     * Register the node into the table.
     *
     * @throws Exception Error registering node.
     */
    @Override
    public void start() throws Exception {
        // Obtain node IP address from system.
        final String ip = getIPAddress();

        log.info("Registering cluster node with ip address " + ip);

        final ClusterNode[] result = new ClusterNode[1];
        new HibernateTxFragment(true) {
            protected void txFragment(Session session) throws Exception {

                // Create the cluster node instance.
                ClusterNode node = new ClusterNode();
                node.setNodeAddress(ip);
                node.setStartupTime(new Date());
                node.setNodeStatus(ClusterNode.ClusterNodeStatus.RUNNING.name());
                session.saveOrUpdate(node);

                session.flush();

                result[0] = node;
            }
        }.execute();

        if (result[0] != null) this.currentNodeId = result[0].getId();

        log.info("Successfuly resgister cluster node with ip address " + ip + " and identifier " + this.currentNodeId);
    }

    /**
     * Deregister the node from the table.
     *
     * @throws Exception Error deregistering node.
     */
    @Override
    public void destroy() throws Exception {
        final Long nodeId = currentNodeId;

        log.info("Deregistering cluster node with id " + nodeId);

        new HibernateTxFragment(true) {
            protected void txFragment(Session session) throws Exception {
                // Delete the cluster node register.
                Query query = session.createQuery("delete from " + ClusterNode.class.getName() +" cn  where cn.id = :idNode");
                query.setLong("idNode", nodeId);
                query.executeUpdate();

                session.flush();
            }
        }.execute();

        this.currentNodeId = null;

        log.info("Successfully deregistered cluster node with id " + nodeId);
    }


    /**
     * Returns a cluster node/s instance for a given status. Several nodes can have same ip address.
     *
     * @param status The status for the node.
     * @return The cluster node/s or <code>null</code> if there is no match.
     * @throws Exception Error searching for a cluster node by status.
     */
    public List<ClusterNode> getNodeByStatus(final ClusterNode.ClusterNodeStatus status) throws  Exception {
        final List<ClusterNode> result = new ArrayList<ClusterNode>();
        if (status != null) {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    Query query = session.createQuery("from " + ClusterNode.class.getName() +" cn  where cn.nodeStatus = :nStatus");
                    query.setString("nStatus", status.name());
                    List queryResult = query.list();

                    if (queryResult != null && !queryResult.isEmpty()) {
                        for (Object obj : queryResult) {
                            result.add((ClusterNode) obj);
                        }
                    }
                }
            }.execute();
        }

        return result;
    }

    /**
     * Returns a cluster node instance for a given identifier.
     *
     * @param id The identifier for the node.
     * @return The cluster node or <code>null</code> if there is no match.
     * @throws Exception Error searching for a cluster node by identifier.
     */
    public ClusterNode getNodeById(final Long id) throws  Exception {
        final ClusterNode[] result = new ClusterNode[1];
        if (id != null) {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    Query query = session.createQuery("from " + ClusterNode.class.getName() +"  cn  where cn.id = :idToSearch");
                    query.setLong("idToSearch", id);
                    List queryResult = query.list();

                    if (queryResult.size() > 1) log.error("There is more than one cluster node resgistered for identifier " + id + ".!!!");
                    if (!queryResult.isEmpty()) result[0] = (ClusterNode) queryResult.get(0);
                }
            }.execute();
        }

        return result[0];
    }

    /**
     * Return the IP address for the default interface.
     *
     * @return The IP address for the default interface.
     * @throws SocketException Error reading socket.
     */
    protected String getIPAddress() throws SocketException{
        String ip = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                ip = addr.getHostAddress();
            }
        }

        return ip;
    }

    /**
     * Sets a status for a node.
     *
     * @param nodeId The node identifier.
     * @param newStatus The new status to set.
     */
    public void setNodeStatus(final Long nodeId, final ClusterNode.ClusterNodeStatus newStatus) throws Exception{
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                ClusterNode node = getNodeById(nodeId);

                if (node == null) {
                    log.error("Cannot set status " + newStatus.name() + " to target node. Node with identifier " + nodeId + " not found.");
                } else {
                    node.setNodeStatus(newStatus.name());
                    session.saveOrUpdate(node);
                    session.flush();
                }
            }
        }.execute();
    }

    /**
     * Sets a status for current node.
     *
     * @param newStatus The new status to set.
     */
    public void setCurrentNodeStatus(ClusterNode.ClusterNodeStatus newStatus) throws Exception {
        setNodeStatus(this.currentNodeId, newStatus);
    }


}
