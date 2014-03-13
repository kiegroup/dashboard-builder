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

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.taglib.factory.GenericFactoryTag;
import org.jboss.dashboard.ui.taglib.factory.UseComponentTag;
import org.jboss.dashboard.workspace.Panel;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.jsp.PageContext;

@ApplicationScoped
public class FactoryUniqueIdEncoder {

    public static FactoryUniqueIdEncoder lookup() {
        return CDIBeanLocator.getBeanByType(FactoryUniqueIdEncoder.class);
    }

    public String encode(Object panel, UIBeanHandler uiBean, String name) {
        StringBuffer sb = new StringBuffer();
        if (panel != null) {
            sb.append("panel_").append(((Panel)panel).getPanelId()).append("_");
        }
        if (uiBean != null) {
            sb.append("uibean_").append(Math.abs(uiBean.getBeanName().hashCode())).append("_");
        }
        sb.append(StringEscapeUtils.escapeHtml(name));
        return sb.toString();
    }

    public String encodeFromContext(PageContext pageContext, String name) {
        Panel panel = RequestContext.lookup().getActivePanel();
        UIBeanHandler uiBean = UIBeanLocator.lookup().getCurrentBean(pageContext.getRequest());
        return encode(panel, uiBean, name);
    }
}
