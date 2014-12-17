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
package org.jboss.dashboard;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.initialModule.DataSourceIInitialModule;
import org.jboss.dashboard.initialModule.InitialModuleRegistry;
import org.jboss.dashboard.kpi.KPIInitialModule;
import org.jboss.dashboard.workspace.export.ImportWorkspacesModule;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

/**
 * <p>This class implements the deployment scanner for dashbuilder webapp.</p>
 * <p>This class reads the files present in a given folder at startup and import all the assets found, such as:</p>
 * <ul>
 *     <li>datasources - Files with extension <code>datasource</code></li>
 *     <li>KPIs and data providers - Files with extension <code>kpis</code></li>
 *     <li>workspaces - Files with extension <code>workspace</code></li>
 * </ul>
 * 
 * <p>The default directory to scan at startup is <code>WEB-INF/deployments</code>.</p>
 * <p>You can change the default directory to scan by the system property <code>-DdeploymentFolder</code>.</p>
 * 
 */
@ApplicationScoped
public class DeploymentScanner implements Startable {

    protected static final String DATASOURCE_EXTENSION = "datasource";
    protected static final String KPIS_EXTENSION = "kpis";
    protected static final String WORKSPACE_EXTENSION = "workspace";
    protected static final String INITIAL_MODULES_NAMESPACE_KPI = "org.jboss.dashboard.deployments.kpi.";
    protected static final String INITIAL_MODULES_NAMESPACE_WORKSPACE = "org.jboss.dashboard.deployments.workspace.";
    protected static final String INITIAL_MODULES_NAMESPACE_DATASOURCE = "org.jboss.dashboard.deployments.datasource.";
    protected static final int VERSION = 1;

    @Inject
    protected transient Logger log;
    
    @Inject @Config("WEB-INF/deployments")
    protected String deploymentFolder;
    
    protected File baseAppDirectory;

    @Inject
    protected InitialModuleRegistry initialModuleRegistry;

    @PostConstruct
    public void init() {
        baseAppDirectory = new File(Application.lookup().getBaseAppDirectory());
    }
    
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public void start() throws Exception {

        // Deploy the workspaces found in the deployment folder.
        doDeployWorkspaces();

        // Deploy the KPIs and dataproviders found in the deployment folder.
        doDeployKPIs();

        // Deploy the datasources found in the deployment folder.
        doDeployDataSources();
    }

    /**
     * Look into the deployment directory and processes any datasource definition file found.
     */
    protected void doDeployDataSources() {
        if (StringUtils.isBlank(deploymentFolder)) return;
        DirectoriesScanner dirScanner = new DirectoriesScanner(DATASOURCE_EXTENSION);
        File[]  files = dirScanner.findFiles(new File(baseAppDirectory, deploymentFolder));
        if (files == null) return;

        for (File f : files) {
            try {
                String fileName = removeExtension(f.getName());
                StringBuilder dataSourceModuleName = new StringBuilder(INITIAL_MODULES_NAMESPACE_DATASOURCE).append(fileName);

                DataSourceIInitialModule dataSourceIInitialModule = new DataSourceIInitialModule();
                dataSourceIInitialModule.setName(dataSourceModuleName.toString());
                dataSourceIInitialModule.setImportFile(getRelativePathtoWebappBase(f.getAbsolutePath()));
                dataSourceIInitialModule.setVersion(VERSION);
                initialModuleRegistry.registerInitialModule(dataSourceIInitialModule);
            }
            catch (Exception e) {
                log.error("Cannot import the datasource definition file: " + f.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Look into the deployment directory and processes any kpi or datasource definition file found.
     */
    protected void doDeployKPIs() {
        if (StringUtils.isBlank(deploymentFolder)) return;
        DirectoriesScanner dirScanner = new DirectoriesScanner(KPIS_EXTENSION);
        File[]  files = dirScanner.findFiles(new File(baseAppDirectory, deploymentFolder));
        if (files == null) return;
        
        for (File f : files) {
            try {
                String fileName = removeExtension(f.getName());
                StringBuilder kpiModuleName = new StringBuilder(INITIAL_MODULES_NAMESPACE_KPI).append(fileName);
                
                KPIInitialModule kpis = new KPIInitialModule();
                kpis.setName(kpiModuleName.toString());
                kpis.setImportFile(getRelativePathtoWebappBase(f.getAbsolutePath()));
                kpis.setVersion(VERSION);
                initialModuleRegistry.registerInitialModule(kpis);
            }
            catch (Exception e) {
                log.error("Cannot import the KPIs definition file: " + f.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Look into the deployment directory and processes any workspace definition file found.
     */
    protected void doDeployWorkspaces() {
        if (StringUtils.isBlank(deploymentFolder)) return;
        DirectoriesScanner dirScanner = new DirectoriesScanner(WORKSPACE_EXTENSION);
        File[]  files = dirScanner.findFiles(new File(baseAppDirectory, deploymentFolder));
        if (files == null) return;

        for (File f : files) {
            try {
                String fileName = removeExtension(f.getName());
                StringBuilder workspaceModuleName = new StringBuilder(INITIAL_MODULES_NAMESPACE_WORKSPACE).append(fileName);

                ImportWorkspacesModule workspace = new ImportWorkspacesModule();
                workspace.setName(workspaceModuleName.toString());
                workspace.setImportFile(getRelativePathtoWebappBase(f.getAbsolutePath()));
                workspace.setVersion(VERSION);
                initialModuleRegistry.registerInitialModule(workspace);
            }
            catch (Exception e) {
                log.error("Cannot import the workspace definition file: " + f.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Return the file name without the extension suffix.
     * 
     * @param fileName The file name.
     * @return The file name without the extension suffix.
     */
    protected String removeExtension(String fileName) {
        if (fileName == null || fileName.trim().length() == 0) return null;
        
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Given an absolute path returns the relative path from the webapp base path.
     * @param path The absolute path for the file.
     * @return Relative path from the webapp base path.
     */
    protected String getRelativePathtoWebappBase(String path) {
        if (path == null || path.trim().length() == 0) return null;
        
        if (path.startsWith(baseAppDirectory.getAbsolutePath())) {
            return path.substring(baseAppDirectory.getAbsolutePath().length() + 1, path.length());
        }
        
        return path;
    }
}
