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

import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.taglib.BaseTag;

public class GenericFactoryTag extends BaseTag {

    public static final String CURRENT_BEAN = "currentBean";

    protected Object bean;
    protected String action;
    protected String property;

    public Object getBean() {
        if (bean != null) return bean;
        return pageContext.getRequest().getAttribute(CURRENT_BEAN);
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getBeanName() {
        Object bean = getBean();
        if (bean == null) return null;
        if (bean instanceof BeanHandler) return ((BeanHandler) bean).getBeanName();
        return bean.toString();
    }
}
