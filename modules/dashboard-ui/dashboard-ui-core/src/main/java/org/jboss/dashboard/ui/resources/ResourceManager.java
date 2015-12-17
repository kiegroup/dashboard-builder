/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Properties;

public interface ResourceManager {

    public Properties getFileMappings();

    /**
     * Retrieve resource by resource identifier, using defaults for missing resources
     *
     * @param resourceId
     * @return resource by resource identifier
     */
    public Resource getResource(String resourceId) throws Exception;

    /**
     * Retrieve resource by resource identifier
     *
     * @param resourceId
     * @param useDefaults
     * @return resource by resource identifier
     * @throws Exception
     */
    public Resource getResource(String resourceId, boolean useDefaults) throws Exception;

}

