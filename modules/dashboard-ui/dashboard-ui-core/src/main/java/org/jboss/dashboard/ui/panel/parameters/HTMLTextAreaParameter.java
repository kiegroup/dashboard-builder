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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.resources.Resource;
import org.jboss.dashboard.ui.resources.ResourceName;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.PanelProviderParameter;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;
import javax.servlet.http.HttpServletRequest;

/**
 * Parameter for a HTML text area
 */
public class HTMLTextAreaParameter extends StringParameter {

    /**
     * Logger
     */
    private static Log log = LogFactory.getLog(HTMLTextAreaParameter.class.getName());

    public static final String CKEDITOR_TEMPLATE = "" +
            "<textarea id='param_ID' name='param_ID'>VALUE</textarea>\n" +
            "<script type='text/javascript' language='Javascript' defer='true'>\n\n" +
            "   CKEDITOR.replace('param_ID', {\n" +
            "       language: 'LANG',\n" +
            "       contentsCss: 'CSS_LINK',\n" +
            "       extraPlugins: 'stylesheetparser',\n" +
            "       customConfig: '',\n" +
            "       allowedContext: true,\n" +
            "       baseFloatZIndex: 20000002, // greater than modal dialog's,\n" +
            "       width: '100%',\n" +
            "       height: 100,\n" +
            "       resize_enabled: false,\n" +
            "       startupMode: 'wysiwyg',\n" +
            "       startupShowBorders: false,\n" +
            "       toolbarLocation: 'top',\n" +
            "       toolbarCanCollapse: true,\n" +
            "       toolbarStartupExpanded: true,\n" +
            "       toolbarGroups: [\n" +
            "           { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },\n" +
            "           { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] },\n" +
            "           '/',\n" +
            "           { name: 'mode' },\n" +
            "           { name: 'styles' },\n" +
            "           { name: 'colors' },\n" +
            "           { name: 'others' }\n" +
            "           ]\n" +
            "   });\n" +
            "</script>\n";

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
        try {
            String html = CKEDITOR_TEMPLATE;
            html = html.replaceAll("ID", getId());
            html = html.replaceAll("VALUE", RenderUtils.noNull(value));
            html = html.replaceAll("LANG", LocaleManager.currentLang());
            html = html.replaceAll("CSS_LINK", getResourceURL("skin", "CSS"));
            return html.toString();
        } catch (Exception e) {
            log.error(e);
            return "";
        }
    }

    public String readFromRequest(HttpServletRequest req) {
        String param = req.getParameter("param_" + getId());
        if (param == null || param.equals("")) return null;
        return param;
    }

    public String getResourceURL(String category, String resourceId) throws Exception {
        String resName = ResourceName.getName(null, null, null, category, null, resourceId);
        if (resName == null) {
            log.warn("Cannot retrieve resource: " + resourceId);
            return null;
        }

        log.debug("Resource " + resourceId + " in category " + category + " has path: " + resName);
        Resource resource = UIServices.lookup().getResourceManager().getResource(resName, true);
        if (resource == null) {
            log.warn("Cannot retrieve resource named: " + resName);
            return null;
        }

        String url = resource.getResourceUrl(null, null, false);
        log.debug("Generated resource url: " + url);
        return url;
    }
}
