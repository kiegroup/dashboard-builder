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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanDispatcher;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.profiler.*;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@ApplicationScoped
public class RequestDispatcher extends AbstractChainProcessor {

    @Inject
    private transient Logger log;

    @Inject
    private BeanDispatcher beanDispatcher;

    public boolean processRequest() throws Exception {
        RequestContext requestContext = getRequestContext();
        HttpServletRequest request = getHttpRequest();
        String pAction = request.getParameter(Parameters.DISPATCH_ACTION);
        String idPanel = request.getParameter(Parameters.DISPATCH_IDPANEL);
        if (StringUtils.isEmpty(pAction) || StringUtils.isEmpty(idPanel)) {
            log.debug("Running bean action.");
            CommandResponse res = beanDispatcher.handleRequest(getRequest());
            if (request.getServletPath().indexOf("/" + URLMarkupGenerator.COMMAND_RUNNER) != -1) {
                requestContext.consumeURIPart(requestContext.getURIToBeConsumed());
            }
            if (res != null) {
                requestContext.setResponse(res);
            }
            return true;
        }

        // Get the specified panel from the current page.
        Section currentPage = NavigationManager.lookup().getCurrentSection();
        Panel panel = currentPage.getPanel(idPanel);
        if (panel == null) {
            // If not found then try to get the panel from wherever the request comes from.
            panel = UIServices.lookup().getPanelsManager().getPanelById(new Long(idPanel));
            if (panel == null) {
                log.error("Cannot dispatch to panel " + idPanel + ". Panel not found.");
                return true;
            }
            // Ensure the panel's section is set as current.
            // This is needed to support requests coming from pages reached after clicking the browser's back button.
            NavigationManager.lookup().setCurrentSection(panel.getSection());
        }

        CodeBlockTrace trace = new PanelActionTrace(panel, pAction).begin();
        try {
            WorkspacePermission workspacePerm = WorkspacePermission.newInstance(panel.getWorkspace(), WorkspacePermission.ACTION_LOGIN);
            if (UserStatus.lookup().hasPermission(workspacePerm)) {
                SectionPermission sectionPerm = SectionPermission.newInstance(panel.getSection(), SectionPermission.ACTION_VIEW);
                if (UserStatus.lookup().hasPermission(sectionPerm)) {
                    PanelProvider provider = panel.getInstance().getProvider();
                    if (provider.isEnabled()) {
                        PanelDriver handler = provider.getDriver();
                        CommandResponse response = handler.execute(panel, getRequest());
                        if (response != null)
                            requestContext.setResponse(response);
                        if (request.getServletPath().indexOf("/" + URLMarkupGenerator.COMMAND_RUNNER) != -1) {
                            requestContext.consumeURIPart(requestContext.getURIToBeConsumed());
                        }
                    }
                }
            }
        } finally {
            trace.end();
        }
        return true;
    }

    /** Panel action trace */
    class PanelActionTrace extends CodeBlockTrace {

        protected Map<String,Object> context;

        public PanelActionTrace(Panel panel, String pAction) {
            super(panel.getInstanceId().toString());
            LocaleManager localeManager = LocaleManager.lookup();
            String title = (String) localeManager.localize(panel.getInstance().getTitle());
            if (title == null) title = panel.getPanelId().toString();
            Section section = panel.getSection();
            context = new LinkedHashMap<String,Object>();
            context.put("Workspace", localeManager.localize(section.getWorkspace().getTitle()));
            context.put("Section", localeManager.localize(section.getTitle()));
            context.put("Panel", title);
            context.put("Panel id.", panel.getPanelId().toString());
            context.put("Panel driver", panel.getProvider().getDriver().getClass().getName());
            context.put("Panel action", pAction);

            ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
            if (threadProfile != null) threadProfile.addContextProperties(context);
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.PANEL_ACTION;
        }

        public String getDescription() {
            return context.get("Panel") + " - " + context.get("Panel action");
        }

        public Map<String,Object> getContext() {
            return context;
        }
    }
}
