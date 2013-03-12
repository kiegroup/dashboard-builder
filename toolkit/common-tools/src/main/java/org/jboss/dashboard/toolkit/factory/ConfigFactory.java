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

import org.jboss.dashboard.toolkit.factory.util.TextConverterFunction;

import java.io.*;
import java.util.*;

public class ConfigFactory {

    private String factoryPath;
    
    private String propertyFile;

    private ArrayList<String> ignoredPrefixes = new ArrayList<String>();

    public ConfigFactory(String propertyFile, String factoryPath) {
        this(propertyFile, factoryPath, null);
    }

    public ConfigFactory(String propertyFile, String factoryPath, String ignoredPrefixesList) {
        this.factoryPath = factoryPath;
        this.propertyFile = propertyFile;
        
        if (ignoredPrefixesList != null) {
            StringTokenizer tokenizer = new StringTokenizer(ignoredPrefixesList, ",");
            String ignoredPrefix;
            while (tokenizer.hasMoreTokens()) {
                ignoredPrefix = tokenizer.nextToken().trim();
                ignoredPrefixes.add(ignoredPrefix);
            }
        }
    }

    public void modifyConfiguration() throws IOException {
        processPropertiesFile(propertyFile);
    }

    private void processPropertiesFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Properties props = new Properties();
        props.load(fis);
        fis.close();

        Set properties = props.keySet();
        for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
            String propertyName = (String) iterator.next();
            String propertyValue = props.getProperty(propertyName);
            if (isFactoryProperty(propertyName)) {
                processConfigValue(propertyName, propertyValue);
            }
        }
    }

    public boolean isFactoryProperty(String propertyName) {
        Iterator<String> it = ignoredPrefixes.iterator();
        while (it.hasNext()) {
            if (propertyName.startsWith(it.next())) return false;
        }
        return true;
    }
   
    public void processConfigValue(String propertyName, String propertyValue) throws IOException {
        int openParPos = propertyValue.indexOf('(');
        int closeParPos = propertyValue.indexOf(')');
        boolean usingFunction = openParPos > 0 && closeParPos > 0 && closeParPos > openParPos;
        if (usingFunction) {
            String functionName = propertyValue.substring(0, openParPos);
            String functionArg = propertyValue.substring(openParPos + 1, closeParPos);
            propertyValue = evaluateConverterFunction(functionName, functionArg);
        }
        new ConfigValueWriter(factoryPath).writeProperty(propertyName, propertyValue);
    }

    protected String evaluateConverterFunction(String functionName, String functionArg) {
        String className = this.getClass().getPackage().getName() + ".util." + functionName;
        try {
            TextConverterFunction function = (TextConverterFunction) Class.forName(className).newInstance();
            return function.convertValue(functionArg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return functionArg;
    }

    public void modifyComponent(String componentPath, Properties propsToSave) throws IOException {
        new ConfigValueWriter(factoryPath).writePropertiesToComponent(componentPath, propsToSave);
    }

    public boolean existsCustomConfigForComponent(String component) {
        return new ConfigValueWriter(factoryPath).existsComponent(component);
    }

    public String getConfigurationValue(String path) throws IOException {
        return new ConfigValueWriter(factoryPath).readProperty(path);
    }

    public String getFactoryPath() {
        return factoryPath;
    }

    public void setFactoryPath(String factoryPath) {
        this.factoryPath = factoryPath;
    }

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

}
