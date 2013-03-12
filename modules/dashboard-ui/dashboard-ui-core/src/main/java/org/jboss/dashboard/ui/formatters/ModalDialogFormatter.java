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

import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.components.PanelComponent;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Renders the Modal Components
 */
public class ModalDialogFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ModalDialogFormatter.class.getName());

    private ModalDialogComponent modalDialogComponent;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        if (modalDialogComponent != null && modalDialogComponent.isShowing()) {
            int width = ModalDialogComponent.DEFAULT_WIDTH;
            int height = ModalDialogComponent.DEFAULT_HEIGHT;

            if (modalDialogComponent.getCurrentComponent() instanceof PanelComponent) {
                PanelComponent panelComponent = (PanelComponent) modalDialogComponent.getCurrentComponent();
                width = panelComponent.getWidth();
                height = panelComponent.getHeight();
            }
            
            String title = modalDialogComponent.getTitle();
            setAttribute("title", StringUtils.defaultIfEmpty(title, ""));
            setAttribute("height", height);
            setAttribute("width", width);
            setAttribute("isDraggable", modalDialogComponent.isDraggable());
            renderFragment("outputHead");

            setAttribute("componentName", modalDialogComponent.getCurrentComponent().getName());
            renderFragment("output");

            setAttribute("isModal", modalDialogComponent.isModal());
            setAttribute("isDraggable", modalDialogComponent.isDraggable());
            renderFragment("outputEnd");
        }
    }

    public ModalDialogComponent getModalDialogComponent() {
        return modalDialogComponent;
    }

    public void setModalDialogComponent(ModalDialogComponent modalComponentsDialogComponent) {
        this.modalDialogComponent = modalComponentsDialogComponent;
    }
}
