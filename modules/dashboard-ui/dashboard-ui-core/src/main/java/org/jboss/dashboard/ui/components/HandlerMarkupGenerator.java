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

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Manager that generates markup for the handler taglib
 */
@ApplicationScoped
public class HandlerMarkupGenerator {

    public static HandlerMarkupGenerator lookup() {
        return CDIBeanLocator.getBeanByType(HandlerMarkupGenerator.class);
    }

    @Inject
    private transient Logger log;

    public String getMarkup(String bean, String property) {
        Panel panel = RequestContext.lookup().getActivePanel();
        if (panel != null) return getPanelUrlMarkup(bean, property, panel);
        else return _getMarkup(bean, property);
    }

    protected String getPanelUrlMarkup(String bean, String action, Panel panel) {
        StringBuffer sb = new StringBuffer();
        sb.append(getMarkupToPanelAction(panel, "_factory"));
        sb.append(_getMarkup(bean, action));
        return sb.toString();
    }

    protected String _getMarkup(String bean, String action) {
        StringBuffer sb = new StringBuffer();
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_BEAN, bean));
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_ACTION, action));
        try {
            BeanHandler element = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(bean);
            element.setEnabledForActionHandling(true);
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a BeanHandler.");
        }
        return sb.toString();
    }

    protected String getHiddenMarkup(String name, String value) {
        name = StringEscapeUtils.escapeHtml(name);
        value = StringEscapeUtils.escapeHtml(value);
        return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">";
    }

    public String getMarkupToPanelAction(Panel panel, String action) {
        StringBuffer sb = new StringBuffer();
        sb.append(getHiddenMarkup(Parameters.DISPATCH_IDPANEL, panel.getPanelId().toString()));
        sb.append(getHiddenMarkup(Parameters.DISPATCH_ACTION, action));
        return sb.toString();
    }
}
