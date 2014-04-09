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
public class ClusterNodesManager implements Startable {
    public static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClusterNodesManager.class.getName());

    /** The id for this node. **/
    private Long currentNodeId;

    /** The IP address for the node. **/
    private String currentNodeIpAddress;

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
        currentNodeIpAddress = getIPAddress();

        log.info("Registering cluster node with ip address " + currentNodeIpAddress);

        final ClusterNode[] result = new ClusterNode[1];
        new HibernateTxFragment(true, true) {
            protected void txFragment(Session session) throws Exception {

                // Check if the node is already regisitered from a previous execution.
                ClusterNode node = getNodeByIpAddress(currentNodeIpAddress);

                if (node == null) {
                    // Create the cluster node instance.
                    node = new ClusterNode();
                }
                node.setNodeAddress(currentNodeIpAddress);
                node.setStartupTime(new Date());
                node.setNodeStatus(ClusterNode.ClusterNodeStatus.REGISTERED.name());
                session.saveOrUpdate(node);

                session.flush();

                result[0] = node;
            }

            @Override
            protected void afterRollback() throws Throwable {
                super.afterRollback();
                log.error("This cluster node cannot be registered.");
            }

        }.execute();

        if (result[0] != null) this.currentNodeId = result[0].getId();

        log.info("Successfully registered cluster node with ip address " + currentNodeIpAddress + " and identifier " + this.currentNodeId);
    }

    /**
     * Deregister the node from the table.
     *
     * @throws Exception Error deregistering node.
     */
    public void deregister(final Long nodeId) throws Exception {
        if (nodeId == null) {
            log.error("This cluster node was not previously registered.");
            return;
        }

        log.info("Deregistering cluster node with id " + nodeId);

        new HibernateTxFragment(true, true) {
            protected void txFragment(Session session) throws Exception {
                // Delete the cluster node register.
                Query query = session.createQuery("delete from " + ClusterNode.class.getName() +" cn  where cn.id = :idNode");
                query.setLong("idNode", nodeId);
                query.executeUpdate();

                session.flush();
            }

            @Override
            protected void afterRollback() throws Throwable {
                super.afterRollback();
                log.error("This cluster node cannot be deregistered.");
            }

        }.execute();

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
     * Returns a cluster node instance for a given IP address.
     *
     * @param ip The ip address for the node.
     * @return The cluster node or <code>null</code> if there is no match.
     * @throws Exception Error searching for a cluster node by ip address.
     */
    public ClusterNode getNodeByIpAddress(final String ip) throws  Exception {
        final ClusterNode[] result = new ClusterNode[1];
        if (ip != null) {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    Query query = session.createQuery("from " + ClusterNode.class.getName() +"  cn  where cn.nodeAddress = :ipToSearch");
                    query.setString("ipToSearch", ip);
                    List queryResult = query.list();

                    if (queryResult.size() > 1) log.error("There is more than one cluster node resgistered for IP address: " + ip + ".");
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
     * Check if another node is currently installing initial modules.
     * If no other node is installing initial modules, install the modules and register node status to state: installing_modules.
     * If another node is installing initial modules, skip initial modules installation from this node.
     *
     * IMPORTANT NOTE: Perform the change node status in bbdd in a new transaction.
     *
     * NOTE: BZ-1014612
     *
     * @return If this node should install initial modules.
     */
    public boolean shouldInstallModules() throws Exception {
        final Boolean[] result = new Boolean[1];
        result[0] = true;
        new HibernateTxFragment(true, true) {
            protected void txFragment(Session session) throws Exception {
                List<ClusterNode> installingModulesNodes = getNodeByStatus(ClusterNode.ClusterNodeStatus.INSTALLING_MODULES);
                if (installingModulesNodes != null && !installingModulesNodes.isEmpty()) {
                    if (installingModulesNodes.size() > 1) {
                        log.warn("More than one cluster node status is INSTALLING_MODULES. This situation can be produced due to a failured previous installation.");
                    }
                    ClusterNode node = installingModulesNodes.get(0);

                    if (node.getNodeAddress() != null && node.getNodeAddress().equals(getCurrentNodeIpAddress())) {
                        // BZ-1047123
                        // If the node with status INSTALLING_MODULES is this one, probably the last installation was failed.
                        // So show a warning and re-install modules.
                        result[0] = true;
                    } else {
                        // Other node is installing. This node should NOT be the installer.
                        result[0] = false;
                    }
                } else {
                    // No other node is installing. This node should be the installer.
                    result[0] = true;
                    setCurrentNodeStatus(ClusterNode.ClusterNodeStatus.INSTALLING_MODULES);
                }
            }

        }.execute();

        return result[0];
    }

    /**
     * Sets a status for current node.
     *
     * @param newStatus The new status to set.
     */
    public void setCurrentNodeStatus(ClusterNode.ClusterNodeStatus newStatus) throws Exception {
        setNodeStatus(this.currentNodeId, newStatus);
    }

    public Long getCurrentNodeId() {
        return currentNodeId;
    }

    public String getCurrentNodeIpAddress() {
        return currentNodeIpAddress;
    }
}
