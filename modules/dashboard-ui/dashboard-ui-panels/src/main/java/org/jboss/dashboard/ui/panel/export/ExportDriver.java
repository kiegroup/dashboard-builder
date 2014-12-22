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
package org.jboss.dashboard.ui.panel.export;

import org.apache.commons.io.IOUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.export.ExportData;
import org.jboss.dashboard.workspace.export.ExportManager;
import org.jboss.dashboard.workspace.export.ExportSessionInfo;
import org.jboss.dashboard.workspace.export.structure.ExportResult;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.ui.resources.GraphicElement;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * Driver that handles the import/export procedures.
 */
public class ExportDriver extends PanelDriver {

    public final static String PAGE_SHOW = "show";
    public final static String PAGE_EXPORT_RESULT = "exportResult";
    public final static String PAGE_IMPORT_PREVIEW = "importPreview";
    public final static String PAGE_IMPORT_RESULT = "importResult";

    public static final String WORKSPACE_PREFFIX = "workspace_";
    public static final String RESOURCE_PREFFIX = "resource_";
    public static final String IMPORT_PREFFIX = "importItem_";


    public static final String PARAM_USE_BLANKS = "useBlanks";
    public static final String PARAM_SHOW_EXPORT = "showExport";
    public static final String PARAM_SHOW_IMPORT = "showImport";
    
    private static final String EXPORT_FILE_NAME = "export." + ExportManager.WORKSPACE_EXTENSION;

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new BooleanParameter(provider, PARAM_USE_BLANKS, true, false));
        addParameter(new BooleanParameter(provider, PARAM_SHOW_EXPORT, true, true));
        addParameter(new BooleanParameter(provider, PARAM_SHOW_IMPORT, true, true));
    }

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public ExportManager getExportManager() {
        return UIServices.lookup().getExportManager();
    }

    public ExportSessionInfo getSessionInfo() {
        return CDIBeanLocator.getBeanByType(ExportSessionInfo.class);
    }

    /**
     * Called on panel initialization (when a new PanelSession instance is created attached to a given session)
     */
    public void initPanelSession(PanelSession panelSession, HttpSession session) {
        panelSession.setCurrentPageId(PAGE_SHOW);
    }

    public CommandResponse actionGoToStart(Panel panel, CommandRequest request) throws Exception {
        return new ShowPanelPage(panel, request, PAGE_SHOW);
    }

    public CommandResponse actionDownloadExport(final Panel panel, CommandRequest request) throws Exception {
        final boolean useBlanks = BooleanParameter.value(panel.getParameterValue(PARAM_USE_BLANKS), false);
        final ExportResult exportResult = getSessionInfo().getExportResult();

        super.fireAfterRenderPanel(panel,request.getRequestObject(), null);

        StringWriter writer = new StringWriter();
        exportResult.writeXMLversion(writer, useBlanks);
        writer.close();
        return new SendStreamResponse(new ByteArrayInputStream(writer.toString().getBytes("UTF-8")), new StringBuilder("inline;filename=").append(EXPORT_FILE_NAME).toString());
    }

    public CommandResponse actionStartExport(Panel panel, CommandRequest request) throws Exception {
        List workspaceIdsToExport = new ArrayList();
        List resourceIdsToExport = new ArrayList();
        Enumeration parameters = request.getRequestObject().getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameterName = (String) parameters.nextElement();
            if (parameterName.startsWith(WORKSPACE_PREFFIX)) {
                workspaceIdsToExport.add(request.getParameter(parameterName));
            } else if (parameterName.startsWith(RESOURCE_PREFFIX)) {
                resourceIdsToExport.add(request.getParameter(parameterName));
            }
        }

        final Workspace[] workspacesToExport = new Workspace[workspaceIdsToExport.size()];
        for (int i = 0; i < workspaceIdsToExport.size(); i++) {
            String workspaceId = (String) workspaceIdsToExport.get(i);
            workspacesToExport[i] = getWorkspacesManager().getWorkspace(workspaceId);
        }

        final GraphicElement[] resourcesToExport = new GraphicElement[resourceIdsToExport.size()];
        for (int i = 0; i < resourceIdsToExport.size(); i++) {
            String paramName = (String) resourceIdsToExport.get(i);
            int index = paramName.indexOf(' ');
            String className = paramName.substring(0, index);
            String id = paramName.substring(index + 1);
            GraphicElementManager[] resourcesManagers = UIServices.lookup().getGraphicElementManagers();
            for (int j = 0; j < resourcesManagers.length; j++) {
                GraphicElementManager resourcesManager = resourcesManagers[j];
                GraphicElement element = resourcesManager.getElement(id, null, null, null);
                if (element != null && element.getClass().getName().equals(className)) {
                    resourcesToExport[i] = element;
                    break;
                }
            }
        }

        ExportResult result = getExportManager().export(new ExportData() {
            public Workspace[] getWorkspacesToExport() {
                return workspacesToExport;
            }

            public GraphicElement[] getResourcesToExport() {
                return resourcesToExport;
            }
        });

        getSessionInfo().setExportResult(result);
        return new ShowPanelPage(panel, request, PAGE_EXPORT_RESULT);
    }

    /**
     * <p>Starts importing a workspace file.</p>
     * <p>For backwards compatibility, import allowed formats are <code>ZIP</code> and <code>XML</code></p>
     */
    public CommandResponse actionStartImport(Panel panel, CommandRequest request) throws FileNotFoundException, IOException {
        if (request.getUploadedFilesCount() > 0) {
            File file = (File) request.getFilesByParamName().get("importFile");
            ImportResult[] results = null;
            boolean isZipFile = isZipFile(file);
            if (isZipFile) results = getExportManager().load(new FileInputStream(file));
            else results = new ImportResult[] { getExportManager().loadXML(file.getName(), new FileInputStream(file)) };
            getSessionInfo().setImportResult(results);
            return new ShowPanelPage(panel, request, PAGE_IMPORT_PREVIEW);
        }
        return new ShowCurrentScreenResponse();
    }

    /**
     * <p>Determine whether a file is a ZIP File by reading the the magic bytes for the ZIP format, that must be <code>0x504b0304</code></p>
     */
    protected  boolean isZipFile(File file) throws IOException {
        if(file.isDirectory()) {
            return false;
        }
        if(!file.canRead()) {
            throw new IOException("Cannot read file "+file.getAbsolutePath());
        }
        if(file.length() < 4) {
            return false;
        }
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        int test = in.readInt();
        in.close();
        return test == 0x504b0304;
    }

    public CommandResponse actionImport(final Panel panel, final CommandRequest request) {
        ImportResult[] results = getSessionInfo().getImportResult();
        int entriesCount = 0;
        for (int i = 0; i < results.length; i++) {
            ImportResult result = results[i];
            entriesCount += result.getRootNode().getChildren().size();
        }
        int[][] indexes = new int[entriesCount][];
        int indexesCurrentPos = 0;
        Enumeration en = request.getRequestObject().getParameterNames();
        while (en.hasMoreElements()) {
            String parameterName = (String) en.nextElement();
            if (parameterName.startsWith(IMPORT_PREFFIX)) {
                parameterName = parameterName.substring(IMPORT_PREFFIX.length());
                int index = parameterName.indexOf(' ');
                int parentIndex = Integer.parseInt(parameterName.substring(0, index));
                int childIndex = Integer.parseInt(parameterName.substring(index + 1));
                indexes[indexesCurrentPos++] = new int[]{parentIndex, childIndex};
            }
        }
        getSessionInfo().setCreateResult(getExportManager().create(results, indexes));
        return new ShowPanelPage(panel, request, PAGE_IMPORT_RESULT);
    }
}
