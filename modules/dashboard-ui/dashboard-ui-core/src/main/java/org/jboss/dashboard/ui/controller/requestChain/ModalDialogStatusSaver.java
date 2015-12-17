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

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.components.ModalDialogComponent;

/**
 * Save the current status of the modal dialog component in order to return the
 * appropriate response later in the request rendering chain.
 */
@SessionScoped
public class ModalDialogStatusSaver extends AbstractChainProcessor implements Serializable {

    public static ModalDialogStatusSaver lookup() {
        return CDIBeanLocator.getBeanByType(ModalDialogStatusSaver.class);
    }

    @Inject
    private ModalDialogComponent modalDialogComponent;

    @Inject
    private NavigationManager navigationManager;

    protected String currentWorkspaceId;
    protected Long currentSectionId;
    protected boolean configEnabled;
    protected boolean wasModalOn;

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

    public boolean processRequest() throws Exception {
        wasModalOn = modalDialogComponent.isShowing();
        configEnabled = navigationManager.isShowingConfig();
        currentWorkspaceId = navigationManager.getCurrentWorkspaceId();
        currentSectionId = navigationManager.getCurrentSectionId();
        return true;
    }
}