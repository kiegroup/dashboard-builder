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
package org.jboss.dashboard.ui.panel.export;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.export.ExportSessionInfo;
import org.jboss.dashboard.workspace.export.structure.CreateResult;
import org.jboss.dashboard.ui.taglib.LinkToWorkspaceTag;
import org.jboss.dashboard.ui.resources.GraphicElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class RenderImportResultFormatter extends RenderImportPreviewFormatter {

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
        CreateResult[] results = sessionInfo.getCreateResult();
        if (results == null) {
            renderFragment("fatalError");
        } else if (results.length == 0) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            boolean anyErrors = false;
            for (int i = 0; i < results.length; i++) {
                CreateResult result = results[i];
                if (result.hasErrors()) anyErrors = true;
            }
            for (int i = 0; i < results.length; i++) {
                CreateResult result = results[i];
                renderCreateResult(request, response, result, i, anyErrors);
            }
            renderFragment("outputEnd");

            if (anyErrors)
                renderFragment("aborted");
        }
    }

    private void renderCreateResult(HttpServletRequest request, HttpServletResponse response, CreateResult result, int i, boolean anyErrors) {
        List warnings = result.getWarnings();
        List warningArguments = result.getWarningArguments();
        Exception error = result.getException();
        renderFragment("createResultStart");

        if (result.hasErrors()) {
            setAttribute("errorMessage", error.getMessage());
            setAttribute("exception", error);
            renderFragment("errors");
        } else {
            Object objectCreated = result.getObjectCreated();
            setAttribute("createdElement", objectCreated);
            if (objectCreated == null) {
                renderFragment("abortedErrorMessageStart");
                renderWarnings(warnings, warningArguments, i);
                renderFragment("abortedEnd");
            } else if (objectCreated instanceof Workspace) {
                Workspace p = (Workspace) objectCreated;
                setAttribute("name", StringEscapeUtils.ESCAPE_HTML4.translate(getLocalizedValue(p.getTitle())));
                setAttribute("url", LinkToWorkspaceTag.getLink(request, response, p.getId()));
                if (anyErrors) {
                    renderFragment("abortedSuccessMessageStart");
                    renderWarnings(warnings, warningArguments, i);
                    renderFragment("abortedEnd");
                } else {
                    renderFragment("workspaceSuccess");
                }

            } else if (objectCreated instanceof GraphicElement) {
                GraphicElement element = (GraphicElement) objectCreated;
                setAttribute("name", StringEscapeUtils.ESCAPE_HTML4.translate(getLocalizedValue(element.getDescription())));
                setAttribute("category", element.getCategoryName());
                //renderFragment(anyErrors ? "abortedSuccessMessage" : "resourceSuccess");
                if (anyErrors) {
                    renderFragment("abortedSuccessMessageStart");
                    renderWarnings(warnings, warningArguments, i);
                    renderFragment("abortedSuccessMessageEnd");
                } else {
                    renderFragment("resourceSuccess");
                }
            }
            //renderWarnings(warnings, warningArguments, i);
        }
        renderFragment("createResultEnd");
    }

    protected void renderWarnings(List warnings, List warningArguments, int index) {
        if (warnings != null && !warnings.isEmpty()) {
            setAttribute("uid", "warnings" + index);
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

}
