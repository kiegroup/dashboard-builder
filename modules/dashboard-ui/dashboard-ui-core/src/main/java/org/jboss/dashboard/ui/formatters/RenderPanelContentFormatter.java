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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class extends Formatter to provide support for the rendering of a panel's content.
 */
public class RenderPanelContentFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RenderPanelContentFormatter.class.getName());

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Panel panel = (Panel) getParameter("panel");
        if (panel != null) {
            SessionManager.setCurrentPanel(panel);
            request.setAttribute(Parameters.RENDER_PANEL, panel);
            PanelSession status = SessionManager.getPanelSession(panel);
            if (status.isMinimized()) {
                renderFragment("minimized");
            } else {
                setAttribute("tableClass", panel.isPaintBorder() ? "skn-table_border" : "");
                setAttribute("panelHeight", panel.getHeight());
                setAttribute("panel", panel);
                setAttribute("panelUID", HTTPSettings.AJAX_AREA_PREFFIX + "content_panel_" + panel.getPanelId());
                renderFragment("outputStart");
                try {
                    if (!panel.isWellConfigured()) {
                        renderFragment("outputNotWellConfigured");
                    } else if (!panel.getProvider().isEnabled()) {
                        renderFragment("outputNotRegistered");
                    } else {
                        panel.getProvider().getDriver().fireBeforeRenderPanel(panel, request, response);
                        PanelProvider provider = panel.getProvider();
                        String screen = status.getCurrentPageId();
                        if (!status.isEditMode()) {
                            if (screen != null) {
                                String jsp = panel.getProvider().getPage(screen);
                                if (jsp != null) {
                                    setAttribute("jsp", jsp);
                                    setAttribute("panelUID", HTTPSettings.AJAX_AREA_PREFFIX + "content_panel_" + panel.getPanelId());
                                    renderFragment("output");
                                } else {
                                    log.error("JSP not found for page " + screen + " in panel type " + provider.getId());
                                }
                            } else {
                                log.error("Page " + screen + " not defined for panel type " + provider.getId());
                            }
                        }
                    }
                } finally {
                    renderFragment("outputEnd");
                }
            }
            /*  Can't remove it, as it will cause the panel not to be present in the response
          SessionManager.setCurrentPanel((Panel) null);
            request.removeAttribute(Parameters.RENDER_IDPANEL);*/
        }
    }

    // Format Panel objects
    public String formatObject(Object obj) {
        if (obj instanceof Panel) {
            // Workspace de procesos > Tareas pendientes > Lista de tareas [id=13456]
            Panel panel = (Panel) obj;
            return panel.getFullDescription();
        }
        return super.formatObject(obj);
    }
}
