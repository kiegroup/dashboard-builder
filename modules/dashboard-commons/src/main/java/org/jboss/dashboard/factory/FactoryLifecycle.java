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

/**
 * This interface is intended to be implemented by objects that, when set in the
 * Factory configuration, receive lifecycle notifications from the Factory.
 */
public interface FactoryLifecycle {
    /**
     * Called after constructing the instance and before setting the default
     * properties
     *
     * @throws Exception
     */
    public void init() throws Exception;

    /**
     * Called after constructing the instance and setting the default properties.
     * When the Factory is about to set a group of properties, it calls stop, sets
     * the properties and then calls start.
     *
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * Called by the factory before setting a group of properties. This way the component
     * can simulate a transactional behaviour.
     *
     * @throws Exception
     */
    public void stop() throws Exception;

    /**
     * Called when the Factory is going down, to allow the release of resources.
     *
     * @throws Exception
     */
    public void shutdown() throws Exception;
}
