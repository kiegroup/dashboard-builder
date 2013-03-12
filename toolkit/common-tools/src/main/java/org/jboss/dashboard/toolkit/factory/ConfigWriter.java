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

import org.apache.tools.ant.util.StringUtils;
import java.io.*;
import java.util.Properties;

public abstract class ConfigWriter {

    public static final String PROPERTY_SEPARATOR = "/";

    private String factoryPath;
    private String commentsPath;


    protected ConfigWriter(String appPath, String commentsPath) {
        this.factoryPath = appPath;
        this.commentsPath = commentsPath;
    }

    /**
     * Get the properties file associated with given component.
     *
     * @param componentName Factory component name to use
     * @return a File pointing to the properties file.
     */
    protected File getPropertiesFilePath(String componentName) {
        componentName = StringUtils.replace(componentName, ".", "/");
        File propertiesFileToModify = new File(factoryPath + "/" + componentName + ".factory");

        return propertiesFileToModify;
    }

    /**
     * Determines if there exists a custom configuration for given component
     *
     * @param component component name
     * @return true if there is a custom configuration for this component.
     */
    public boolean existsComponent(String component) {
        return getPropertiesFilePath(component).exists();
    }

    protected void writePropertiesToComponent(String componentName, Properties values) throws IOException {
        File propertiesFileToModify = getPropertiesFilePath(componentName);
        Properties existingProperties = new Properties();

        File parentDirectory = propertiesFileToModify.getParentFile();
        if (!parentDirectory.exists()) {
            createDirectoryPath(parentDirectory);
        }

        if (propertiesFileToModify.exists()) {
            FileInputStream fis = new FileInputStream(propertiesFileToModify);
            existingProperties.load(fis);
            fis.close();
        }

        existingProperties.putAll(values);
        FileOutputStream fos = new FileOutputStream(propertiesFileToModify);
        writeCommentsToStream(componentName, fos);
        existingProperties.store(fos, "");
        fos.close();
    }

    private void createDirectoryPath(File directory) throws IOException {
        if (directory != null && !directory.exists()) {
            File parentDirectory = directory.getParentFile();
            if (parentDirectory != null) {
                createDirectoryPath(parentDirectory);
            }
            directory.mkdir();
        }
    }

    private void writeCommentsToStream(String componentName, FileOutputStream fos) throws IOException {
        File commentsFile = new File(commentsPath + "/" + componentName + ".txt");
        if (commentsFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(commentsFile));
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (!first) {
                    fos.write("\n#".getBytes());
                } else {
                    first = false;
                }
                fos.write(line.getBytes());
            }
            reader.close();
        }
    }

}
