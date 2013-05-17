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
package org.jboss.dashboard.factory;

import org.hibernate.LockOptions;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.database.InstalledModule;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * A platform component that manages the loading and execution of
 * all the registered InitialModule components at start-up time.
 */
@ApplicationScoped
public class InitialModulesManager implements Startable {
    public static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(InitialModulesManager.class.getName());

    @Inject
    private InitialModuleRegistry initialModuleRegistry;

    public Priority getPriority() {
        return Priority.LOW;
    }

    public void start() throws Exception {
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
    }
}