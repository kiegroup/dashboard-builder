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
package org.jboss.dashboard.toolkit.factory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.IOException;


public class ConfigFactoryTask extends Task {

    private String factoryProperties;
    
    private String factoryPath;

    private String ignoredPrefixes;

    public String getFactoryProperties() {
        return factoryProperties;
    }

    public void setFactoryProperties(String factoryProperties) {
        this.factoryProperties = factoryProperties;
    }

    public String getFactoryPath() {
        return factoryPath;
    }

    public void setFactoryPath(String factoryPath) {
        this.factoryPath = factoryPath;
    }

    public String getIgnoredPrefixes() {
        return ignoredPrefixes;
    }

    public void setIgnoredPrefixes(String ignoredPrefixes) {
        this.ignoredPrefixes = ignoredPrefixes;
    }

    @Override
    public void execute() throws BuildException {
        try {
            System.out.println("property: " + getFactoryProperties());
            System.out.println("property: " + getFactoryProperties());

            ConfigFactory configFactory = new ConfigFactory(getFactoryProperties(), getFactoryPath(), ignoredPrefixes);
            configFactory.modifyConfiguration();
        } catch (IOException e) {
            throw new BuildException("Error configuring factory: " + e.getMessage(), e);
        }
    }
}