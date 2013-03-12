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

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Base class that any Factory bean must extends.
 */
public abstract class BasicFactoryElement implements FactoryLifecycle, Serializable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BasicFactoryElement.class.getName());

    private String factoryComponentName;
    private String factoryComponentScope;
    private String factoryComponentDescription;
    private String factoryComponentAlias;
    private boolean componentKillFlag = false;

    /**
     * Called after constructing the instance and before setting the default
     * properties
     *
     * @throws Exception
     */
    public void init() throws Exception {
        log.debug("Init for component " + getComponentName());
    }

    /**
     * Called after constructing the instance and setting the default properties.
     * When the Factory is about to set a group of properties, it calls stop, sets
     * the properties and then calls start.
     *
     * @throws Exception
     */
    public void start() throws Exception {
        log.debug("Starting component " + getComponentName());
    }

    /**
     * Called by the factory before setting a group of properties. This way the component
     * can simulate a transactional behaviour.
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        log.debug("Stopping component " + getComponentName());
    }

    /**
     * Called when the Factory is going down, to allow the release of resources.
     *
     * @throws Exception
     */
    public void shutdown() throws Exception {
        log.debug("Shutdown for component " + getComponentName());
        componentKillFlag = true;
    }

    /**
     * @return The name inside the Factory tree where this component is being held.
     */
    public final String getComponentName() {
        return factoryComponentName;
    }

    final void setComponentName(String s) {
        factoryComponentName = s;
    }

    /**
     * @return The scope inside the Factory tree where this component is being held.
     */
    public final String getComponentScope() {
        return factoryComponentScope;
    }

    final void setComponentScope(String s) {
        factoryComponentScope = s;
    }

    /**
     * @return The description inside the Factory tree where this component is being held.
     */
    public final String getComponentDescription() {
        return factoryComponentDescription;
    }

    final void setComponentDescription(String s) {
        factoryComponentDescription = s;
    }

    public Object factoryLookup(String path) {
        return Factory.lookup(path, this.getComponentName());
    }

    public void setComponentAlias(String alias) {
        this.factoryComponentAlias = alias;
    }

    public String getComponentAlias() {
        return this.factoryComponentAlias;
    }

    protected void addPeriodicTask(String methodName, final long sleepInterval) {
        try {
            Method m = this.getClass().getDeclaredMethod(methodName);
            addPeriodicTask(m, sleepInterval);
        } catch (NoSuchMethodException e) {
            log.error("Error: ", e);
        }
    }

    protected void addPeriodicTask(final Method m, final long sleepInterval) {
        if (getComponentScope().equals(Component.SCOPE_GLOBAL)) {
            Runnable periodicRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            // Run here periodic task
                            m.invoke(BasicFactoryElement.this);
                            // Detect if we have to end the thread
                            if (componentKillFlag) return;
                        } catch (Throwable e) {
                            log.error("Error invoking periodic task: ", e);
                        }
                        try {
                            long totalSlept = 0;
                            while (totalSlept < sleepInterval) {
                                Thread.sleep(1000);
                                totalSlept += 1000;
                                if (componentKillFlag) return;
                            }
                        } catch (InterruptedException e) {
                            log.error("Error: ", e);
                        }
                    }
                }
            };
            Thread thr = new Thread(periodicRunnable, getComponentName() + " thread");
            thr.setPriority(Thread.MIN_PRIORITY);
            thr.setDaemon(true);
            thr.start();
        } else {
            log.error("Cannot add periodic task for component " + getComponentName() + ". Scope is " + getComponentScope());
        }
    }

}

