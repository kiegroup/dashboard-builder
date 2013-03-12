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
package org.jboss.dashboard.ui.components.panelManagement;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.Panel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

public class ShowPanelPageComponent extends PanelManagementPanel {
    private static transient Log log = org.apache.commons.logging.LogFactory.getLog(ShowPanelPageComponent.class.getName());

    private String page;

    public void closePopup() {
        ModalDialogComponent mdc = getModalDialogComponent();
        if (mdc.isShowing()) mdc.hide();
    }

    public boolean openDialog(Panel panel, CommandRequest request, String page, String title, int width, int height) {
        this.page = page;
        return openDialog(panel, request, title, width, height);
    }

    public String getPanelPage() {
        try {
            if (isWellConfigured()) return getPanelInstance().getProvider().getPage(page);
        } catch (Exception e) {
            log.warn("Error getting Panel Page '" + page + "': ", e);
        }
        return "";
    }

    @Override
    protected void reset() {
        super.reset();
        page = null;
    }

    @Override
    public boolean isWellConfigured() {
        return super.isWellConfigured() && !StringUtils.isEmpty(page);
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public static ShowPanelPageComponent lookup() {
        return (ShowPanelPageComponent) Factory.lookup(ShowPanelPageComponent.class.getName());
    }

    @Override
    public Log getLog() {
        return log;
    }
}
