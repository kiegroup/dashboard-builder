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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.ui.components.*;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.provider.DataProviderType;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.taglib.factory.GenericFactoryTag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.ServletRequest;

/**
 * The locator service for several UI beans.
 */
@ApplicationScoped
@Named("UIBeanLocator")
public class UIBeanLocator {

    public static UIBeanLocator lookup() {
        return (UIBeanLocator) CDIBeanLocator.getBeanByName("UIBeanLocator");
    }

    /**
     * Get the bean instance being rendered.
     * <p>When invoked from a JSP it returns the bean instance owner of that JSP.</p>
     */
    public UIBeanHandler getCurrentBean(ServletRequest request) {
        return (UIBeanHandler) request.getAttribute(GenericFactoryTag.CURRENT_BEAN);
    }

    /**
     * Get the editor component for the specified data provider.
     */
    public DataProviderEditor getEditor(DataProviderType target) {
        String beanName = target.getUid() + "_editor";
        return (DataProviderEditor) CDIBeanLocator.getBeanByName(beanName);
    }

    /**
     * Get the editor component for the specified data displayer.
     */
    public DataDisplayerEditor getEditor(DataDisplayer target) {
        DataDisplayerType type = target.getDataDisplayerType();
        String beanName = type.getUid() + "_editor";
        DataDisplayerEditor editor = (DataDisplayerEditor) CDIBeanLocator.getBeanByName(beanName);
        editor.setDataDisplayer(target);
        return editor;
    }

    /**
     * Get the viewer component for the specified data displayer.
     */
    public DataDisplayerViewer getViewer(DataDisplayer target) {
        DataDisplayerType type = target.getDataDisplayerType();
        DataDisplayerRenderer lib = target.getDataDisplayerRenderer();
        String beanName = lib.getUid() + "_" + type.getUid() + "_viewer";
        DataDisplayerViewer viewer = (DataDisplayerViewer) CDIBeanLocator.getBeanByName(beanName);
        viewer.setDataDisplayer(target);
        return viewer;
    }
}