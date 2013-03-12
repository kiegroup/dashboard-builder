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
package org.jboss.dashboard.ui.resources;

import java.util.Set;

public interface ResourceHolder {
    /**
     * Retrieve resource by resource identifier.
     *
     * @param resName Resource whose name is to be retrieved.
     * @param lang    Language for the resource.
     * @return
     */
    public Resource getResource(ResourceName resName, String lang) throws Exception;

    /**
     * Retrieve the set of resource id's hold by this ResourceHolder
     *
     * @return A set of resource id's (Strings)
     */
    public Set getResources();

    /**
     * Checks resource deployment status, and deploys if needed
     */
    public void checkDeployment();
}
