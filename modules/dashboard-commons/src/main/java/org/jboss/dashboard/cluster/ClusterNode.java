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

import java.util.Date;

/**
 * Hibernate mapping model class for <code>dashb_cluster_node</code> table.
 *
 * This table handles connected nodes and their status.
 *
 * NOTE: BZ-1014612
 */
public class ClusterNode {
    private Long id;
    private String nodeAddress;
    private Date startupTime;
    private String nodeStatus;

    /**
     * Default constructor.
     */
    public ClusterNode() {
    }

    /**
     * <p>Enum that hangles possible node status values.</p>
     * <p>Possible values</p>
     * <ul>
     *     <li>INSTALLING_MODULES - Application is currently installing initial modules.</li>
     *     <li>RUNNING - Application is currently started up and running.</li>
     * </ul>
     */
    public enum ClusterNodeStatus {
        INSTALLING_MODULES, REGISTERED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Date getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(Date startupTime) {
        this.startupTime = startupTime;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }
}
