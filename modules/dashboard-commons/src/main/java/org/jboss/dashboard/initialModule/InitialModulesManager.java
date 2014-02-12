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
package org.jboss.dashboard.initialModule;

import org.hibernate.LockOptions;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.cluster.ClusterNode;
import org.jboss.dashboard.cluster.ClusterNodesManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.database.InstalledModule;
import org.hibernate.Session;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * A platform component that manages the loading and execution of
 * all the registered InitialModule components at start-up time.
 */
@ApplicationScoped
public class InitialModulesManager implements Startable {

    @Inject
    protected transient Logger log;

    @Inject
    private InitialModuleRegistry initialModuleRegistry;

    @Inject
    private ClusterNodesManager clusterNodesManager;

    @Inject  @Config("true")
    private boolean initialModulesEnabled;

    public Priority getPriority() {
        return Priority.LOW;
    }

    public void start() throws Exception {
        if (initialModulesEnabled) {

            // BZ-1014612: First check if another node is currently installing modules. If it is, do nothing, Otherwise, install initial modules.
            boolean doTheInstall = clusterNodesManager.shouldInstallModules();
            if (!doTheInstall) {
                log.info("Skipping initial modules installation as other node is currenly installing.");
                return;
            }

            List<InitialModule> modules = initialModuleRegistry.getInitialModulesRegistered();
            for (final InitialModule module : modules) {
                new HibernateTxFragment(true) {
                    protected void txFragment(Session session) throws Exception {
                        InstalledModule currentVersion = (InstalledModule) session.get(InstalledModule.class, module.getName(), LockOptions.UPGRADE);

                        // The module is not registered => Install it!
                        if (currentVersion == null) {
                            if (module.doTheInstall()) {
                                if (log.isDebugEnabled()) log.debug("Installed module " + module.getName() + " version " + module.getVersion());
                                currentVersion = new InstalledModule(module.getName(), 1);
                                session.save(currentVersion);
                                session.flush();
                            } else {
                                log.warn("Error installing module " + module.getName() + " version " + module.getVersion());
                            }
                        }
                        // The module's version has been increased => Upgrade it!
                        else if (currentVersion.getVersion() < module.getVersion()) {
                            if (module.doTheUpgrade(currentVersion.getVersion())) {
                                if (log.isDebugEnabled()) log.debug("Upgraded module " + module.getName() + " to version " + module.getVersion());
                                currentVersion.setVersion(module.getVersion());
                                session.saveOrUpdate(currentVersion);
                                session.flush();
                            } else {
                                log.warn("Error upgrading module " + module.getName() + " to version " + module.getVersion());
                            }
                        }
                        // Default => Do nothing.
                        else {
                            if (log.isDebugEnabled()) log.debug("Module " + module.getName() + " version " + module.getVersion() + "is already installed.");
                        }
                    }}.execute();
            }

            // BZ-1014612: Initial modules installation finished. Set node status to old one.
            finishInstallation();

        } else {
            log.info("Initial modules are NOT being installed.");
        }
    }

    public boolean isInitialModulesEnabled() {
        return initialModulesEnabled;
    }

    public void setInitialModulesEnabled(boolean initialModulesEnabled) {
        this.initialModulesEnabled = initialModulesEnabled;
    }

    /**
     * Finished node installing modules. Change node status into bbdd.
     *
     * IMPORTANT NOTE: Perform the change node status in bbdd in a new transaction.
     *
     * NOTE: BZ-1014612
     */
    protected void finishInstallation() throws Exception {
        new HibernateTxFragment(true) {
            protected void txFragment(Session session) throws Exception {
                clusterNodesManager.setCurrentNodeStatus(ClusterNode.ClusterNodeStatus.REGISTERED);
            }

        }.execute();
    }


}
