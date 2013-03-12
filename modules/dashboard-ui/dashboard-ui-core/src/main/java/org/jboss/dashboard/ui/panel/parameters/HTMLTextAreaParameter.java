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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.RedirectionHandler;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import org.jboss.dashboard.workspace.PanelProviderParameter;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter for a HTML text area
 */
public class HTMLTextAreaParameter extends StringParameter {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HTMLTextAreaParameter.class.getName());

    public HTMLTextAreaParameter(PanelProvider provider) {
        super(provider);
    }

    public HTMLTextAreaParameter(PanelProvider provider, String id, boolean required, boolean i18n) {
        super(provider, id, required, i18n);
    }

    public HTMLTextAreaParameter(PanelProvider provider, String id, boolean required, String defaultValue, boolean i18n) {
        super(provider, id, required, defaultValue, i18n);
    }

    public String renderHTML(HttpServletRequest req, PanelInstance instance, PanelProviderParameter param, String value) {
        StringBuffer html = new StringBuffer();
        html.append("<textarea id='param_");
        html.append(getId()).append("' " + " cols='120' rows='10' name='param_");
        html.append(getId()).append("'>").append(RenderUtils.noNull(value));
        html.append("</textarea>\n" + "<script language=\"Javascript\" defer=\"true\">\n" + "    var sBasePath = '");
        html.append(req.getContextPath()).append("/fckeditor/';\n" + "    var oFCKeditor = new FCKeditor('param_");
        html.append(getId()).append("', '100%', '150') ;\n"
                + "    oFCKeditor.BasePath = sBasePath;\n"
                + "    oFCKeditor.Config['CustomConfigurationsPath'] = '");

        URLMarkupGenerator markupGenerator = (URLMarkupGenerator) UIServices.lookup().getUrlMarkupGenerator();
        Map paramsMap = new HashMap();
        paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, "/fckeditor/custom/fckConfig.jsp");
        String uri = markupGenerator.getMarkup("org.jboss.dashboard.ui.components.RedirectionHandler", "redirectToSection", paramsMap);

        if (!uri.startsWith("/")) uri = "/" + uri;
        html.append(req.getContextPath()).append(uri).append("';"
                + "    oFCKeditor.Config[\"DefaultLanguage\"] = '").append(SessionManager.getLang()).append("';\n");
        html.append("    oFCKeditor.ReplaceTextarea();\n" + "</script>");
        return html.toString();
    }

    public String readFromRequest(HttpServletRequest req) {
        String param = req.getParameter("param_" + getId());
        if (param == null || param.equals(""))
            return null;

        return param;
    }
}
