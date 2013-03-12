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

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.export.ExportData;
import org.jboss.dashboard.workspace.export.ExportManager;
import org.jboss.dashboard.workspace.export.ExportSessionInfo;
import org.jboss.dashboard.workspace.export.structure.ExportResult;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.ui.panel.parameters.StringParameter;
import org.jboss.dashboard.ui.resources.GraphicElement;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class ExportDriver extends PanelDriver {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ExportDriver.class.getName());

    public final static String PAGE_SHOW = "show";
    public final static String PAGE_EXPORT_RESULT = "exportResult";
    public final static String PAGE_IMPORT_PREVIEW = "importPreview";
    public final static String PAGE_IMPORT_RESULT = "importResult";

    public static final String WORKSPACE_PREFFIX = "workspace_";
    public static final String RESOURCE_PREFFIX = "resource_";
    public static final String IMPORT_PREFFIX = "importItem_";


    public static final String PARAM_USE_BLANKS = "useBlanks";
    public static final String RETURNED_FILE_NAME = "returnedFileName";
    public static final String EXPORT_ENTRY_NAME = "entryName";
    public static final String PARAM_SHOW_EXPORT = "showExport";
    public static final String PARAM_SHOW_IMPORT = "showImport";

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new BooleanParameter(provider, PARAM_USE_BLANKS, true, false));
        addParameter(new StringParameter(provider, RETURNED_FILE_NAME, true, "export.cex", false));
        addParameter(new StringParameter(provider, EXPORT_ENTRY_NAME, true, "content", false));
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
        return (ExportSessionInfo) Factory.lookup("org.jboss.dashboard.workspace.export.ExportSessionInfo");
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
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        final String contentType = fileNameMap.getContentTypeFor(panel.getParameterValue(RETURNED_FILE_NAME));

        final String disposition = "inline; filename=" + panel.getParameterValue(RETURNED_FILE_NAME) + ";";
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        zos.putNextEntry(new ZipEntry(panel.getParameterValue(EXPORT_ENTRY_NAME) + "." + exportManager.getAllowedEntryExtensions()[0]));
        OutputStreamWriter wos = new OutputStreamWriter(zos);
        getSessionInfo().getExportResult().writeXMLversion(wos, BooleanParameter.value(panel.getParameterValue(PARAM_USE_BLANKS), false));
        wos.close();
        return new SendStreamResponse(new ByteArrayInputStream(bos.toByteArray()), disposition);*/
        final String entryName = panel.getParameterValue(EXPORT_ENTRY_NAME) + "." + getExportManager().getAllowedEntryExtensions()[0];
        final boolean useBlanks = BooleanParameter.value(panel.getParameterValue(PARAM_USE_BLANKS), false);
        final ExportResult exportResult = getSessionInfo().getExportResult();
        return new CommandResponse() {
            public boolean execute(CommandRequest cmdReq) throws Exception {
                HttpServletResponse response = cmdReq.getResponseObject();
                response.setHeader("Content-Disposition", disposition);
                response.setContentType(contentType != null ? contentType : "application/force-download");
                ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
                zos.putNextEntry(new ZipEntry(entryName));
                OutputStreamWriter wos = new OutputStreamWriter(zos);
                exportResult.writeXMLversion(wos, useBlanks);
                wos.close();
                return true;
            }
        };
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

    public CommandResponse actionStartImport(Panel panel, CommandRequest request) throws FileNotFoundException {
        if (request.getUploadedFilesCount() > 0) {
            File file = (File) request.getFilesByParamName().get("importFile");
            ImportResult[] results = getExportManager().load(new FileInputStream(file));
            getSessionInfo().setImportResult(results);
            return new ShowPanelPage(panel, request, PAGE_IMPORT_PREVIEW);
        }
        return new ShowCurrentScreenResponse();
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
