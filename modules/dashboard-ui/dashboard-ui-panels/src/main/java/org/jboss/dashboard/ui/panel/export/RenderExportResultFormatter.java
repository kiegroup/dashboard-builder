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

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.export.ExportSessionInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class RenderExportResultFormatter extends Formatter {

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
        if (sessionInfo != null && sessionInfo.getExportResult() != null) {
            Exception error = sessionInfo.getExportResult().getException();
            List warnings = sessionInfo.getExportResult().getWarnings();
            List warningArguments = sessionInfo.getExportResult().getWarningArguments();
            if (sessionInfo.getExportResult().hasErrors()) {
                setAttribute("errorMessage", error.getMessage());
                setAttribute("exception", error);
                renderFragment("errors");
            } else {
                if (!warnings.isEmpty()) {
                    renderFragment("warningOutputStart");
                    for (int i = 0; i < warnings.size(); i++) {
                        String warningKey = (String) warnings.get(i);
                        Object[] warningKeyArguments = (Object[]) warningArguments.get(i);
                        setAttribute("warning", warningKey);
                        setAttribute("arguments", warningKeyArguments);
                        renderFragment("warningOutput");
                    }
                    renderFragment("warningOutputEnd");
                }
                String url = UIServices.lookup().getUrlMarkupGenerator().getLinkToPanelAction(getPanel(), "downloadExport", true);
                setAttribute("url", url);
                renderFragment("downloadResult");
            }
        }
    }
}
