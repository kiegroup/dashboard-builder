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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.controller.responses.ShowBeanAjaxResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.Panel;

/**
 * Catch the opening of the modal window an the successive requests over it and
 * give an appropriate response.
 */
@ApplicationScoped
public class ModalDialogRenderer extends AbstractChainProcessor {

    @Inject
    private ModalDialogComponent modalDialog;

    public boolean processRequest() throws Exception {
        HttpServletRequest request = getHttpRequest();

        // Check whether the modal window has been activated within the current request.
        ModalDialogStatusSaver modalStatus = ModalDialogStatusSaver.lookup();
        boolean modalOn = modalDialog.isShowing();
        boolean modalOnBeforeRequest = modalStatus.modalOnBeforeRequest();
        boolean modalSwitchedOn = !modalOnBeforeRequest && modalOn;
        boolean modalSwitchedOff = modalOnBeforeRequest && !modalOn;

         // Check if the navigation has changed (f.i: URL typing) and so auto-close the modal (if was opened).
        if (modalOn) {
            if (!modalSwitchedOn) {
                NavigationManager navMgr = NavigationManager.lookup();
                boolean navigationChanged = false;
                boolean configEnabled = navMgr.isShowingConfig();
                boolean wasConfigEnabled = modalStatus.isConfigEnabled();
                String currentWorkspaceId = navMgr.getCurrentWorkspaceId();
                String oldWorkspaceId = modalStatus.getCurrentWorkspaceId();
                Long currentSectionId = navMgr.getCurrentSectionId();
                Long oldSectionId = modalStatus.getCurrentSectionId();
                if (configEnabled != wasConfigEnabled) navigationChanged = true;
                if (ComparatorUtils.compare(currentWorkspaceId, oldWorkspaceId, 1) != 0) navigationChanged = true;
                if (ComparatorUtils.compare(currentSectionId, oldSectionId, 1) != 0) navigationChanged = true;

                if (navigationChanged) {
                    modalDialog.hide();
                    return true;
                }
            }
        }

        // If modal has been switched off, return new screen response without ajax so as to
        // force to repaint all screen without the component.
        if (modalSwitchedOff) {
            setResponse(new ShowCurrentScreenResponse());
            return true;
        }

        // If no AJAX then just return the current response
        // The modal window is embedded into content.jsp so it will be displayed if the whole screen is repainted.
        String ajaxParam = request.getParameter(Parameters.AJAX_ACTION);
        if (ajaxParam == null || !Boolean.valueOf(ajaxParam).booleanValue()) return true;

        // If the modal window is on then show it:
        // - The AJAX response varies depending whether the modal window is rendered for the first time or it's a response inside the modal window.
        // - If the request is being executed within a panel the pass such panel to the response.
        if (modalOn) {
            Panel panel = getCurrentPanel(request);
            if (modalSwitchedOn) setResponse(new ShowBeanAjaxResponse(modalDialog, panel));
            else setResponse(new ShowBeanAjaxResponse(modalDialog.getCurrentComponent(), panel));
        }
        return true;
    }

    protected Panel getCurrentPanel(HttpServletRequest request) throws Exception {
        final String idPanel = request.getParameter(Parameters.DISPATCH_IDPANEL);
        if (idPanel == null) return null;
        Long id = Long.decode(idPanel);

        NavigationManager navMgr = NavigationManager.lookup();
        Panel[] panels =  navMgr.getCurrentSection().getAllPanels();
        for (int i = 0; i < panels.length; i++) {
            Panel panel = panels[i];
            if (panel.getPanelId().equals(id)) return panel;
        }
        return null;
    }
}
