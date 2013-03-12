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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.controller.CommandRequest;

/**
 * Renders Components as Modal
 */
public class ModalDialogComponent extends UIComponentHandlerFactoryElement {

    public static ModalDialogComponent lookup() {
        return (ModalDialogComponent) Factory.lookup("org.jboss.dashboard.ui.components.ModalDialogComponent");
    }

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ModalDialogComponent.class.getName());

    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;

    private String componentIncludeJSP;
    private String modalDialogFormatter;
    private String title;
    private UIComponentHandlerFactoryElement currentComponent;
    private Runnable closeListener;
    private boolean isShow = false;
    private boolean isModal = true;
    private boolean isDraggable = false;

    public void actionClose(CommandRequest request) {
        if (closeListener != null) closeListener.run();
        reset();
    }

    public boolean show() {
        if (currentComponent!=null) {
            isShow = true;
        }
        return isShow;
    }

    public void hide() {
        reset();
    }

    public void reset() {
        isShow = false;
        isModal = true;
        isDraggable = false;
        currentComponent = null;
        title = null;
    }
    
    public String getModalDialogFormatter() {
        return modalDialogFormatter;
    }

    public void setModalDialogFormatter(String modalDialogFormatter) {
        this.modalDialogFormatter = modalDialogFormatter;
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public UIComponentHandlerFactoryElement getCurrentComponent() {
        return currentComponent;
    }

    public void setCurrentComponent(UIComponentHandlerFactoryElement currentComponent) {
        this.currentComponent = currentComponent;
    }

    public boolean isShowing() {
        return isShow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(Runnable closeListener) {
        this.closeListener = closeListener;
    }

    public void setModal(boolean modal) {
        isModal = modal;
    }

    public boolean isModal() {
        return isModal;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }
}
