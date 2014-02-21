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

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.components.PanelComponent;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.slf4j.Logger;

public abstract class PanelManagementPanel extends PanelComponent {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    private int width;
    private int height;
    private String workspaceId;
    private Long panelInstanceId;
    private Long panelId;

    abstract Logger getLogger();

    public boolean closeDialog(CommandRequest request) {
        ModalDialogComponent mdc = getModalDialogComponent();

        if (mdc.isShowing()) {
            mdc.actionClose(request);
            return  true;
        }

        return false;
    }

    public boolean openDialog(final Panel panel, final CommandRequest request, String title) {
        return openDialog(panel, request, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public boolean openDialog(final Panel panel, final CommandRequest request, String title, int width, int height) {
        this.workspaceId = panel.getWorkspace().getId();
        this.panelInstanceId = panel.getInstanceId();
        this.panelId = panel.getDbid();
        this.width = width;
        this.height = height;

        if (isWellConfigured()) {

            ModalDialogComponent mdc = getModalDialogComponent();

            mdc.setTitle(title);
            mdc.setCurrentComponent(this);
            mdc.setCloseListener(new Runnable() {
                public void run() {
                    try {
                        panel.getProvider().getDriver().activateNormalMode(panel, request);
                    } catch (Exception e) {
                        getLogger().warn("Error closing panel popup: ", e);
                    }
                }
            });
            return mdc.show();
        }
        return false;
    }

    @Override
    public void afterRenderBean() {
        super.afterRenderBean();
        try {
            getPanel().getPanelSession().setAttribute(PanelDriver.PARAMETER_ACTION_EXECUTED_ENABLED, Boolean.TRUE);
        } catch (Exception e) {
            getLogger().warn("Error enabling ajax action execution: ", e);
        }
    }

    protected void reset() {
        workspaceId = null;
        panelInstanceId = null;
    }

    protected Panel getPanel() throws Exception {
        return UIServices.lookup().getPanelsManager().getPaneltByDbId(panelId);
    }

    protected WorkspaceImpl getWorkspace() throws Exception {
        return (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(getWorkspaceId());
    }

    protected PanelInstance getPanelInstance() throws Exception {
        return getWorkspace().getPanelInstance(getPanelInstanceId());
    }

    public boolean isWellConfigured() {
        return panelInstanceId != null && !StringUtils.isEmpty(workspaceId);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Long getPanelInstanceId() {
        return panelInstanceId;
    }

    public void setPanelInstanceId(Long panelInstanceId) {
        this.panelInstanceId = panelInstanceId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public ModalDialogComponent getModalDialogComponent() {
        return ModalDialogComponent.lookup();
    }
}
