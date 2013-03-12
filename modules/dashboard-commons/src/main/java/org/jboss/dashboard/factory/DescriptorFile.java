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

import java.util.Properties;

/**
 * A Factory component definition file (the contents of its .factory file).
 */
public class DescriptorFile {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DescriptorFile.class.getName());
    private String componentName;
    private Properties mappedProperties;
    private String source;

    public DescriptorFile(String componentName, Properties mappedProperties, String source) {
        this.componentName = componentName;
        this.mappedProperties = mappedProperties;
        this.source = source;
    }

    public String getComponentName() {
        return componentName;
    }

    public Properties getMappedProperties() {
        return mappedProperties;
    }

    public String getSource() {
        return source;
    }

    public String toString() {
        return componentName + "->" + source + "(" + mappedProperties + ")";
    }
}

