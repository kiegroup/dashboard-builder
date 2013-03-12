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

import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.export.ExportSessionInfo;
import org.jboss.dashboard.workspace.export.ExportVisitor;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.jboss.dashboard.security.BackOfficePermission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class RenderImportPreviewFormatter extends Formatter {

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output.
     * Method getParameter is intended to retrieve input parameters by name.
     * <p/>
     * Exceptions are to be catched inside the method, and not to be thrown, normally,
     * formatters could use a error fragment to be displayed when an error happens
     * in displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws org.jboss.dashboard.ui.taglib.formatter.FormatterException
     *          in case of an unexpected exception.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        ExportSessionInfo sessionInfo = ((ExportDriver) getDriver()).getSessionInfo();
        ImportResult[] results = sessionInfo.getImportResult();
        if (results == null) {
            renderFragment("fatalError");
        } else if (results.length == 0) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            boolean anyError = false;
            for (int i = 0; i < results.length; i++) {
                ImportResult result = results[i];
                if (!renderImportResult(result, i))
                    anyError = true;
            }
            if (!anyError) renderFragment("importButton");
            renderFragment("outputEnd");
        }
    }

    protected boolean renderImportResult(ImportResult result, int index) {
        Exception error = result.getException();
        List warnings = result.getWarnings();
        List warningArguments = result.getWarningArguments();
        setAttribute("entryName", result.getEntryName());
        renderFragment("entryStart");
        if (result.hasErrors()) {
            setAttribute("errorMessage", error.getMessage());
            setAttribute("exception", error);
            renderFragment("errors");
        } else {
            renderElementsInEntry(result, index);
            if (warnings != null && !warnings.isEmpty()) {
                renderFragment("warningOutputStart");
                for (int j = 0; j < warnings.size(); j++) {
                    String warningKey = (String) warnings.get(j);
                    Object[] warningKeyArguments = (Object[]) warningArguments.get(j);
                    setAttribute("warning", warningKey);
                    setAttribute("arguments", warningKeyArguments);
                    renderFragment("warningOutput");
                }
                renderFragment("warningOutputEnd");
            }
        }
        renderFragment("entryEnd");
        return !result.hasErrors();
    }

    protected void renderElementsInEntry(ImportResult result, int index) {
        XMLNode node = result.getRootNode();
        List children = node.getChildren();
        if (children == null || children.isEmpty()) {
            renderFragment("emptyEntry");
        } else {
            renderFragment("entryElementsOutputStart");
            BackOfficePermission createPerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_CREATE_WORKSPACE);
            boolean canCreate = UserStatus.lookup().hasPermission(createPerm);
            for (int j = 0; j < children.size(); j++) {
                XMLNode childNode = (XMLNode) children.get(j);
                setAttribute("inputName", ExportDriver.IMPORT_PREFFIX + index + " " + j);
                if (childNode.getObjectName().equals(ExportVisitor.WORKSPACE)) {
                    if (canCreate) {
                        setAttribute("entryElementName", childNode.getAttributes().getProperty("id"));
                        renderFragment("workspaceEntryElement");
                    }
                } else if (childNode.getObjectName().equals(ExportVisitor.RESOURCE)) {
                    setAttribute("entryElementName", childNode.getAttributes().getProperty("id"));
                    renderFragment("resourceEntryElement");
                } else {
                    setAttribute("entryElementName", childNode.getObjectName());
                    renderFragment("entryElement");
                }
            }
            renderFragment("entryElementsOutputEnd");
        }
    }
}
