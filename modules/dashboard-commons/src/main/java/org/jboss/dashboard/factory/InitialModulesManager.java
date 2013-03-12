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

import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.database.InstalledModule;
import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.jboss.dashboard.CoreServices;

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

    @Inject @Config("false")
    private boolean clearCachesAfterLoading;

    public Priority getPriority() {
        return Priority.LOW;
    }

    public void start() throws Exception {
        List<InitialModule> modules = initialModuleRegistry.getInitialModulesRegistered();
        for (final InitialModule module : modules) {
            new HibernateTxFragment(true) {
            protected void txFragment(Session session) throws Exception {
                InstalledModule currentVersion = loadAndLockModule(module.getName());
                if (currentVersion != null) {
                    try {
                        if (currentVersion.getVersion() == module.getVersion()) {
                            if (log.isDebugEnabled())
                                log.debug("Module " + module.getName() + " version " + module.getVersion() + "is already installed.");
                        } else if (currentVersion.getVersion() == 0) {
                            if (module.doTheInstall()) {
                                if (log.isDebugEnabled())
                                    log.debug("Installed module " + module.getName() + " version " + module.getVersion());
                                storeModule(currentVersion, module);
                            } else {
                                log.warn("Error installing module " + module.getName() + " version " + module.getVersion());
                            }
                        } else if (module.doTheUpgrade(currentVersion.getVersion())) {
                            if (log.isDebugEnabled())
                                log.debug("Upgraded module " + module.getName() + " to version " + module.getVersion());
                            storeModule(currentVersion, module);
                        } else
                            log.warn("Error upgrading module " + module.getName() + " to version " + module.getVersion());
                    } finally {
                        unlockModule(currentVersion);
                    }
                }
            }}.execute();
        }
        if (clearCachesAfterLoading) {
            HibernateInitializer hin = CoreServices.lookup().getHibernateInitializer();
            hin.evictAllCaches();
        }
    }


    protected void unlockModule(final InstalledModule currentVersion) throws Exception {
        HibernateTxFragment fragment = new HibernateTxFragment() {
            public void txFragment(Session session) throws Exception {
                currentVersion.setStatus(null);
                session.update(currentVersion);
                session.flush();
            }
        };
        fragment.execute();
    }

    protected void storeModule(final InstalledModule currentVersion, final InitialModule newVersion) {
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    currentVersion.setVersion(newVersion.getVersion());
                    session.saveOrUpdate(currentVersion);
                    session.flush();
                }
            }.execute();
        } catch (Exception e) {
            log.error("Error saving InstalledModule " + newVersion.getName() + " version " + newVersion.getVersion(), e);
        }
    }

    protected InstalledModule loadAndLockModule(final String moduleName) {
        final InstalledModule[] module = new InstalledModule[1];
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    InstalledModule currentVersion = (InstalledModule) session.get(InstalledModule.class, moduleName, LockMode.UPGRADE);
                    if (currentVersion == null) {
                        module[0] = new InstalledModule(moduleName, 0);
                        module[0].setStatus(InstalledModule.STATUS_LOADING);
                        session.save(module[0]);
                    } else if (StringUtils.isEmpty(currentVersion.getStatus())) {
                        currentVersion.setStatus(InstalledModule.STATUS_LOADING);
                        session.update(currentVersion);
                        module[0] = currentVersion;
                    }
                    session.flush();
                }
            }.execute();
        } catch (Exception e) {
            log.error("Error loading InstalledModule " + moduleName, e);
        }
        return module[0];
    }
}
