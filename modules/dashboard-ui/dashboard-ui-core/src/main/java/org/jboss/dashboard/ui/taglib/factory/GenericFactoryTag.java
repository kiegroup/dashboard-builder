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
package org.jboss.dashboard.ui.taglib.factory;

import org.jboss.dashboard.ui.components.UIComponentHandlerFactoryElement;

public class GenericFactoryTag extends javax.servlet.jsp.tagext.BodyTagSupport {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(GenericFactoryTag.class.getName());
    private String bean;
    private String action;
    private String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getBean() {
        if (bean == null) {
            UIComponentHandlerFactoryElement currentComponent = (UIComponentHandlerFactoryElement) pageContext.getRequest().getAttribute(UseComponentTag.COMPONENT_ATTR_NAME);
            if (currentComponent != null) {
                return currentComponent.getName();
            }
        }
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
