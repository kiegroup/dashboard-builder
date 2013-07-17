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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.Session;

/**
 * A core component addressed to initialize/update an application artifact at system start-up.
 */
public abstract class InitialModule {
    public static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InitialModule.class.getName());

    private String name;
    private long version = 1;
    private boolean installTransactional = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isInstallTransactional() {
        return installTransactional;
    }

    public void setInstallTransactional(boolean installTransactional) {
        this.installTransactional = installTransactional;
    }

    public boolean doTheInstall() {
        final boolean [] returnvalue = new boolean[]{false};
        if (installTransactional) {
            try {
                new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        returnvalue[0] = install();
                    }
                }.execute();
            } catch (Exception e) {
                log.error("Error: ", e);
                return false;
            }
        } else {
            return install();
        }
        return returnvalue[0];
    }

    public boolean doTheUpgrade(final long currentVersion) {
        final boolean [] returnvalue = new boolean[]{false};
        if (installTransactional) {
            try {
                new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        returnvalue[0] = upgrade(currentVersion);
                    }
                }.execute();
            } catch (Exception e) {
                log.error("Error: ", e);
                return false;
            }
        } else {
            return upgrade(currentVersion);
        }
        return returnvalue[0];
    }


    /**
     * Install this module
     *
     * @return true on success
     */
    protected abstract boolean install();

    /**
     * Upgrade from given version
     *
     * @return true on success
     */
    protected abstract boolean upgrade(long currentVersion);
}
