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

import org.jboss.dashboard.ui.formatters.FactoryUniqueIdEncoder;
import org.jboss.dashboard.ui.components.UIComponentHandlerFactoryElement;
import org.jboss.dashboard.ui.taglib.factory.UseComponentTag;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;

public class FactoryUniqueIdEncoderImpl extends FactoryUniqueIdEncoder {

    public String encode(Object panel, UIComponentHandlerFactoryElement factoryComponent, String name) {
        StringBuffer sb = new StringBuffer();
        if (panel != null) {
            sb.append("panel_").append(((Panel)panel).getPanelId()).append("_");
        }
        if (factoryComponent != null) {
            sb.append("component_").append(Math.abs(factoryComponent.getName().hashCode())).append("_");
        }
        sb.append(StringEscapeUtils.escapeHtml(name));
        return sb.toString();
    }

    public String encodeFromContext(PageContext pageContext, String name) {
        Panel panel = (Panel) pageContext.getRequest().getAttribute(Parameters.RENDER_PANEL);
        UIComponentHandlerFactoryElement factoryComponent = (UIComponentHandlerFactoryElement) pageContext.getRequest().getAttribute(UseComponentTag.COMPONENT_ATTR_NAME);
        return encode(panel, factoryComponent, name);
    }
}
