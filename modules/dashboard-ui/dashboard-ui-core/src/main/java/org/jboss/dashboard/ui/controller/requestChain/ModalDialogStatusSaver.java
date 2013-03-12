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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.components.ModalDialogComponent;

/**
 * Save the current status of the modal dialog component in order to return the
 * appropriate response later in the request rendering chain.
 */
public class ModalDialogStatusSaver extends RequestChainProcessor {

    public static ModalDialogStatusSaver lookup() {
        return (ModalDialogStatusSaver) Factory.lookup("org.jboss.dashboard.ui.controller.requestChain.ModalDialogStatusSaver");
    }

    protected String currentWorkspaceId;
    protected Long currentSectionId;
    protected boolean configEnabled;
    protected boolean wasModalOn;
    protected NavigationManager navigationManager;

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public boolean modalOnBeforeRequest() {
        return wasModalOn;
    }

    public String getCurrentWorkspaceId() {
        return currentWorkspaceId;
    }

    public Long getCurrentSectionId() {
        return currentSectionId;
    }

    public boolean isConfigEnabled() {
        return configEnabled;
    }

    public ModalDialogComponent getModalDialog() {
        return (ModalDialogComponent) Factory.lookup("org.jboss.dashboard.ui.components.ModalDialogComponent");
    }

    protected boolean processRequest() throws Exception {
        ModalDialogComponent modalDialog = (ModalDialogComponent) Factory.lookup("org.jboss.dashboard.ui.components.ModalDialogComponent");
        wasModalOn = modalDialog.isShowing();
        configEnabled = navigationManager.isShowingConfig();
        currentWorkspaceId = navigationManager.getCurrentWorkspaceId();
        currentSectionId = navigationManager.getCurrentSectionId();
        return true;
    }
}