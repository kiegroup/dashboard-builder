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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.profiler.ThreadProfile;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Main class for the initialization and lookup of components within the platform.
 * TODO: Replace by CDI
 */
public final class Factory {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Factory.class.getName());

    public static final String FACTORY_FILENAME = "factory.cfg";
    public static final String FACTORY_EXTENSION = "factory";
    public static final String FACTORY_COMMENT_CHAR = "#";

    protected ComponentsTree tree = null;
    protected List<String> modules = null;

    /**
     * Gets a factory for the specified configuration dir. This directory must contain a file
     * named FACTORY_FILENAME, with a list of relative directory names where configuration trees
     * are present.
     *
     * @param configDir starting directory
     * @return a Factory for this directory, or an empty factory if the directory is invalid.
     */
    public static Factory getFactory(File configDir) {
        Factory factory;
        try {
            factory = new Factory(configDir);
        } catch (IOException e) {
            log.error("Error creating the factory. An empty factory will be created. ", e);
            return new Factory();
        }
        return factory;
    }

    /**
     * Get a factory from the specified input stream, equivalent to the typical directory structure.
     *
     * @param zis Input stream to the zipped Factory config files
     * @return a Factory for this zip file, or an empty factory if the zip is invalid.
     */
    public static Factory getFactory(ZipInputStream zis) {
        Factory factory;
        try {
            factory = new Factory(zis);
        } catch (IOException e) {
            log.error("Error creating the factory. An empty factory will be created. ", e);
            return new Factory();
        }
        return factory;
    }

    protected Factory(File configDir) throws IOException {
        if (configDir.isDirectory()) {
            init(configDir);
        } else {
            init(new ZipInputStream(new BufferedInputStream(new FileInputStream(configDir))));
        }
    }

    protected Factory(ZipInputStream zis) throws IOException {
        init(zis);
    }

    protected Factory() {
    }

    protected synchronized void init(ZipInputStream zis) throws IOException {
        ArrayList entriesOrder = new ArrayList();
        HashMap entries = new HashMap();
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.isDirectory()) continue;

            String entryName = entry.getName().replace('\\', '/');
            if (entryName.equals(FACTORY_FILENAME)) {
                log.debug("Using config file: zip:/" + entryName);
                BufferedReader input = new BufferedReader(new InputStreamReader(zis));
                String line;
                while ((line = input.readLine()) != null) {
                    if (!checkModuleName(line)) continue;
                    entriesOrder.add(line);
                }
            } else if (entryName.indexOf('/') == -1) {
                log.warn("Ignoring entry inside ZIP " + entryName);
            } else {
                int index = entryName.indexOf('/');
                Properties prop = new Properties();
                try {
                    prop.load(zis);
                } catch (IOException e) {
                    log.error("Error processing entry zip:/" + entryName + ". It will be ignored.", e);
                    continue;
                }
                String product = entryName.substring(0, index);
                String component = entryName.substring(index + 1, entryName.length() - 1 - FACTORY_EXTENSION.length()).replace('/', '.').replace('\\', '.');
                Map m = (Map) entries.get(product);
                if (m == null) {
                    m = new HashMap();
                    entries.put(product, m);
                }
                m.put(component, prop);
            }
        }
        zis.close();
        List descriptorFiles = new ArrayList();
        for (int i = 0; i < entriesOrder.size(); i++) {
            String product = (String) entriesOrder.get(i);
            Map components = (Map) entries.get(product);
            if (components == null || components.isEmpty())
                continue;
            for (Iterator it = components.keySet().iterator(); it.hasNext();) {
                String componentName = (String) it.next();
                Properties componentProperties = (Properties) components.get(componentName);
                descriptorFiles.add(new DescriptorFile(componentName, componentProperties, "zip:/" + product + "/" + componentName.replace('.', '/') + "." + FACTORY_EXTENSION));
            }
        }
        addDescriptorFiles(descriptorFiles);
    }

    protected synchronized void init(File factoryDir) throws IOException {
        log.info("Configuring Factory from directory: " + factoryDir);
        File configFile = new File(factoryDir.getAbsolutePath() + "/" + FACTORY_FILENAME);
        log.debug("Using config file: " + configFile);
        BufferedReader input = new BufferedReader(new FileReader(configFile));
        List<DescriptorFile> descriptorFiles = new ArrayList();
        modules = new ArrayList<String>();
        String moduleName;
        while ((moduleName = input.readLine()) != null) {
            if (!checkModuleName(moduleName)) continue;

            modules.add(moduleName);

            // Read the .factory files from the module's JAR file.
            List moduleFiles = initModuleFromJar(moduleName);
            descriptorFiles.addAll(moduleFiles);

            // Read .factory files from the module's factory directory.
            File moduleDir = new File(factoryDir.getAbsolutePath() + "/" + moduleName);
            if (moduleDir.exists() && moduleDir.isDirectory()) {
                log.debug("Adding " + moduleDir + " to the list of tree roots.");
                moduleFiles = initModuleFromDir(moduleDir);
                descriptorFiles.addAll(moduleFiles);
            }
            // Read .factory files from the a zip file named "<moduleName>.zip"
            File moduleZip = new File(factoryDir.getAbsolutePath() + "/" + moduleName + ".zip");
            if (moduleZip.exists() && moduleZip.isFile()) {
                log.debug("Adding " + moduleZip + " to the list of tree roots.");
                moduleFiles = initModuleFromZip(moduleZip);
                descriptorFiles.addAll(moduleFiles);
            }
        }
        input.close();
        addDescriptorFiles(descriptorFiles);
    }

    protected List<DescriptorFile> initModuleFromDir(File moduleDir) throws IOException {
        List<DescriptorFile> descriptorFiles = new ArrayList<DescriptorFile>();
        DirectoriesScanner scanner = new DirectoriesScanner(FACTORY_EXTENSION);
        File[] descriptorsInside = scanner.findFiles(moduleDir);
        int preffixLength = moduleDir.getCanonicalPath().length() + 1;
        for (int i = 0; i < descriptorsInside.length; i++) {
            File file = descriptorsInside[i];
            String componentName = file.getCanonicalPath();
            componentName = componentName.substring(preffixLength, componentName.length() - 1 - FACTORY_EXTENSION.length());
            componentName = componentName.replace('/', '.');
            componentName = componentName.replace('\\', '.');
            Properties prop = new Properties();
            try {
                FileInputStream fis = new FileInputStream(file);
                prop.load(fis);
                fis.close();
                descriptorFiles.add(new DescriptorFile(componentName, prop, file.getCanonicalPath()));
            } catch (IOException e) {
                log.error("Error processing file " + file + ". It will be ignored.", e);
                continue;
            }
        }
        return descriptorFiles;
    }

    protected List<DescriptorFile> initModuleFromZip(File moduleZip) throws IOException {
        List<DescriptorFile> descriptorFiles = new ArrayList<DescriptorFile>();
        ZipFile zf = new ZipFile(moduleZip);
        for (Enumeration en = zf.entries(); en.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) en.nextElement();
            if (entry.getName().endsWith(FACTORY_EXTENSION) && !entry.isDirectory()) {
                InputStream is = zf.getInputStream(entry);
                String componentName = entry.getName();
                componentName = componentName.substring(0, componentName.length() - 1 - FACTORY_EXTENSION.length());
                componentName = componentName.replace('/', '.');
                componentName = componentName.replace('\\', '.');
                Properties prop = new Properties();
                try {
                    prop.load(is);
                    is.close();
                    descriptorFiles.add(new DescriptorFile(componentName, prop, moduleZip + "!" + entry.getName()));
                } catch (IOException e) {
                    log.error("Error processing file " + entry.getName() + " inside " + moduleZip + ". It will be ignored.", e);
                    continue;
                }
            }
        }
        return descriptorFiles;
    }

    protected List<DescriptorFile> initModuleFromJar(String moduleName) throws IOException {
        List<DescriptorFile> descriptorFiles = new ArrayList<DescriptorFile>();
        String libPath = Application.lookup().getBaseAppDirectory() + File.separator + "WEB-INF" + File.separator + "lib";
        File libDir = new File(libPath);
        DirectoriesScanner ds = new DirectoriesScanner("jar");
        File[] jars = ds.findFiles(libDir);
        for (int i = 0; i < jars.length; i++) {
            File jar = jars[i];
            if (jar.getName().startsWith(moduleName)) {
                List<DescriptorFile> jarDescriptors = initModuleFromZip(jar);
                descriptorFiles.addAll(jarDescriptors);
            }
        }
        return descriptorFiles;
    }

    protected void addDescriptorFiles(List descriptorFiles) {
        tree = new ComponentsTree(descriptorFiles);
        log.info("Factory configured.");
        if (log.isDebugEnabled())
            log.debug("Using factory components " + tree);
    }

    public void destroy() {
        getTree().destroy();
    }

    public List<String> getModules() {
        return modules;
    }

    public ComponentsTree getTree() {
        return tree;
    }

    protected Object doLookup(String path, String relativeToPath) {
        if (tree == null)
            return null;
        try {
            return tree.lookup(path, relativeToPath);
        } catch (LookupException e) {
            log.error("Error: ", e);
            return null;
        }
    }

    protected String doGetAlias(String path, String relativeToPath) {
        if (tree == null)
            return null;
        return tree.getAlias(path, relativeToPath);
    }

    protected void runWork(FactoryWork work, boolean profile) {
        boolean contextOwner = !ComponentsContextManager.isContextStarted();
        if (contextOwner) ComponentsContextManager.startContext();

        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        boolean profileOwner = threadProfile == null;
        if (profile && profileOwner) threadProfile = Profiler.lookup().beginThreadProfile();

        try {
            work.doWork();
        } catch (Throwable t) {
            log.error("Error: ", t);
        } finally {
            if (profile && profileOwner) Profiler.lookup().finishThreadProfile(threadProfile);
            if (contextOwner) ComponentsContextManager.clearContext();
        }
    }

    public static Object lookup(String path) {
        Factory factory = Application.lookup().getGlobalFactory();
        if (factory == null) return null;
        return factory.doLookup(path, null);
    }

    public static Object lookup(String path, String relativeToPath) {
        return Application.lookup().getGlobalFactory().doLookup(path, relativeToPath);
    }

    public static String getAlias(String path) {
        return Application.lookup().getGlobalFactory().doGetAlias(path, null);
    }

    public static Object getAlias(String path, String relativeToPath) {
        return Application.lookup().getGlobalFactory().doGetAlias(path, relativeToPath);
    }

    public static void doWork(FactoryWork work) {
        Application.lookup().getGlobalFactory().runWork(work, true);
    }

    public static void doWork(FactoryWork work, boolean profileWork) {
        Application.lookup().getGlobalFactory().runWork(work, profileWork);
    }

    protected boolean checkModuleName(String moduleName) {
        return !StringUtils.isEmpty(moduleName) && !moduleName.startsWith(FACTORY_COMMENT_CHAR);
    }
}
