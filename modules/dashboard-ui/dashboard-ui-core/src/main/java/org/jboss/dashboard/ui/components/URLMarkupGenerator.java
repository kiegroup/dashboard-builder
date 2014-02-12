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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenGenerator;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Generates the url markup for a Factory command.
 */
@ApplicationScoped
public class URLMarkupGenerator {

    @Inject
    private transient Logger log;

    private String handler = "factory";
    private String action = "set";
    public static final String COMMAND_RUNNER = "Controller";
    public static final String FRIENDLY_PREFIX = "workspace";
    public static final String PARAM_SEPARATOR = "\u0026";

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get a permanent link to a given action on a bean
     *
     * @param bean   Bean handler that will perform the action
     * @param action Bean's method that will be invoked
     * @param params Extra parameters for link
     * @return A link url to a bean action, independent on the page.
     */
    public String getPermanentLink(String bean, String action, Map params) {
        try {
            StringBuffer sb = new StringBuffer();
            String base = RequestContext.getCurrentContext().getRequest().getRequestObject().getContextPath() + "/" + COMMAND_RUNNER;
            sb.append(base).append("?");
            params.put(FactoryURL.PARAMETER_BEAN, bean);
            params.put(FactoryURL.PARAMETER_ACTION, action);
            sb.append(getParamsMarkup(params));
            BeanHandler element = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(bean);
            if (element != null) element.setEnabledForActionHandling(true);
            else log.debug("Bean @Named as '" + bean + "' not found.");
            return postProcessURL(sb).toString();
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a BeanHandler.");
            return "#";
        }
    }

    /**
     * Get the base URI for any markup, that is, the base path plus the servlet mapping. Any uri
     * constructed on top of it will go to the Controller servlet
     *
     * @return the base URI for any markup
     */
    public String getServletMapping() {
        StringBuffer buf = new StringBuffer(_getServletMapping());
        return postProcessURL(buf).toString();
    }

    protected String _getServletMapping() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest().getRequestObject();
        if( request != null ) {
            return request.getContextPath()+"/"+COMMAND_RUNNER;
        } else {
            // Do the best we can, this is a relative URL that might not work if AJAX handler does not convert it to
            // absolute or a <base> tag is specified in the generated HTML
            return COMMAND_RUNNER;
        }
    }

    /**
     * Mapping to the controller servlet. By default it is Controller, but in case there is friendly url, can be
     * replaced by workspace/&lt;friendly_url&gt; It is NOT preceded by "/"
     *
     * @return Mapping to the controller servlet.
     */
    public String getBaseURI() {
        // Avoid an extra Controller in URL when it is already friendly
        Panel panel = getCurrentPanel();
        if (panel != null) {  // There will be a friendly url here
            return getLinkToPage(panel.getSection(), true);
        }
        return getServletMapping();
    }

    /**
     * Generate a link to a factory component action
     *
     * @param beanName Factory component that will perform the action
     * @param action Bean's property (method) that will be invoked
     * @param params   Extra parameters for link
     * @return A link url to a bean action
     */
    public String getMarkup(String beanName, String action, Map params) {
        try {
            if (params == null) params = new HashMap();
            Panel panel = getCurrentPanel();
            if (panel != null) {
                params.put(Parameters.DISPATCH_IDPANEL, panel.getPanelId());
                params.put(Parameters.DISPATCH_ACTION, "_factory");
            }

            StringBuffer sb = new StringBuffer();
            sb.append(_getServletMapping()).append("?");
            BeanHandler bean = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(beanName);
            params.put(FactoryURL.PARAMETER_BEAN, beanName);
            params.put(FactoryURL.PARAMETER_ACTION, bean.getActionName(action));
            sb.append(getParamsMarkup(params));
            bean.setEnabledForActionHandling(true);
            return postProcessURL(sb).toString();
        } catch (ClassCastException cce) {
            log.error("Bean " + beanName + " is not a BeanHandler.");
            return "#";
        }
    }

    /**
     * Convert a parameter map (string->string) to its URL form name1=value1&name2=value2 ...
     *
     * @param params parameter map to process.
     * @return A String representation for the received parameter map
     */
    public String getParamsMarkup(Map params) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it = params.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            Object paramValue = params.get(paramName);
            sb.append(getParameterMarkup(paramName, paramValue));
            if (it.hasNext()) {
                sb.append(PARAM_SEPARATOR);
            }
        }
        return sb.toString();
    }

    protected String getParameterMarkup(String name, Object value) {
        StringBuffer sb = new StringBuffer();
        try {
            HTTPSettings webSettings = HTTPSettings.lookup();
            sb.append(URLEncoder.encode(name, webSettings.getEncoding())).append("=").append(URLEncoder.encode(String.valueOf(value), webSettings.getEncoding()));
        } catch (UnsupportedEncodingException e) {
            log.error("Error: ", e);
        }
        return sb.toString();
    }

    public String getContextHost(ServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String context = ((HttpServletRequest) request).getContextPath();
        String protocol = request.getScheme();
        while (context.startsWith("/")) context = context.substring(1);
        sb.append(protocol.toLowerCase()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80) {
            sb.append(":").append(request.getServerPort());
        }
        return sb.toString();
    }

    /**
     * Get the base href for current request
     * @param request
     * @return a String with the form http://host[:port]/[context/]
     * @throws IOException
     */
    public String getBaseHref(ServletRequest request) throws IOException {
        StringBuffer sb = new StringBuffer();
        String context = ((HttpServletRequest) request).getContextPath();
        String protocol = request.getScheme();
        while (context.startsWith("/")) {
            context = context.substring(1);
        }
        sb.append(protocol.toLowerCase()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80) {
            sb.append(":").append(request.getServerPort());
        }
        sb.append("/");
        if (!StringUtils.isEmpty(context)) {
            sb.append(context).append("/");
        }
        return sb.toString();
    }

    protected Panel getCurrentPanel() {
        try {
            HttpServletRequest request = RequestContext.getCurrentContext().getRequest().getRequestObject();
            return (Panel) request.getAttribute(Parameters.RENDER_PANEL);
        }
        catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * Get an absolute url that leads to a given workspace
     *
     * @param workspace        Workspace to take to
     * @param allowFriendly allow friendly url
     * @return an url that leads to a given workspace
     */
    public String getLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly) {
        return getLinkToWorkspace(workspace, allowFriendly, LocaleManager.currentLang());
    }

    /**
     * Get an absolute url that leads to a given workspace in a given lang
     *
     * @param workspace        Workspace to take to
     * @param allowFriendly allow friendly url
     * @return an url that leads to a given workspace
     */
    public String getLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly, String lang) {
        StringBuffer link = getRelativeLinkToWorkspace(workspace, allowFriendly, lang);
        return postProcessURL(link).toString();
    }

    /**
     * Get an absolute url that leads to a given page
     *
     * @param page          Page to take to
     * @param allowFriendly allow friendly url
     * @return an url that leads to a given workspace
     */
    public String getLinkToPage(Section page, boolean allowFriendly) {
        return getLinkToPage(page, allowFriendly, LocaleManager.currentLang());
    }

    /**
     * Get an absolute url that leads to a given page in a given lang
     *
     * @param page          Page to take to
     * @param allowFriendly allow friendly url
     * @param lang          lang to switch to
     * @return an url that leads to a given workspace
     */
    public String getLinkToPage(Section page, boolean allowFriendly, String lang) {
        StringBuffer link = getRelativeLinkToPage(page, allowFriendly, lang);
        return postProcessURL(link).toString();
    }

    protected StringBuffer getRelativeLinkToPage(Section page, boolean allowFriendly, String lang) {
        StringBuffer sb = new StringBuffer();
        sb.append(getRelativeLinkToWorkspace(page.getWorkspace(), allowFriendly, lang));
        String pageFriendlyUrl = page.getId().toString();
        if (allowFriendly) {
            pageFriendlyUrl = StringUtils.defaultIfEmpty(page.getFriendlyUrl(), page.getId().toString());
        }
        sb.append("/").append(pageFriendlyUrl);
        return sb;
    }

    protected StringBuffer getRelativeLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly, String lang) {
        StringBuffer sb = new StringBuffer();
        String friendlyUrl = workspace.getId();
        if (allowFriendly) {
            friendlyUrl = StringUtils.defaultIfEmpty(workspace.getFriendlyUrl(), workspace.getId());
        }
        sb.append(RequestContext.getCurrentContext().getRequest().getRequestObject().getContextPath() + "/" + FRIENDLY_PREFIX + "/").append(lang).append("/").append(friendlyUrl);
        return sb;
    }

    /**
     * Get an url that leads to a given panel action
     *
     * @param panel       Page to take to
     * @param allowFriendly allow friendly url
     * @param action        action to perform on panel
     * @param extraParams   Aditional parameters
     * @return an url that leads to a given panel
     */
    public String getLinkToPanelAction(Panel panel, String action, String extraParams, boolean allowFriendly) {
        StringBuffer sb = new StringBuffer();
        Map paramsMap = new HashMap();
        paramsMap.put(Parameters.DISPATCH_IDPANEL, panel.getPanelId());
        paramsMap.put(Parameters.DISPATCH_ACTION, action);
        sb.append(getRelativeLinkToPage(panel.getSection(), allowFriendly, LocaleManager.currentLang()));
        sb.append("?");
        String paramsMarkup = getParamsMarkup(paramsMap);
        sb.append(paramsMarkup);
        if (extraParams != null) {
            sb.append(PARAM_SEPARATOR).append(extraParams);
        }
        return postProcessURL(sb).toString();
    }

    /**
     * Get an url that leads to a given panel action
     *
     * @param panel       Page to take to
     * @param allowFriendly allow friendly url
     * @param action        action to perform on panel
     * @return an url that leads to a given workspace
     */
    public String getLinkToPanelAction(Panel panel, String action, boolean allowFriendly) {
        return getLinkToPanelAction(panel, action, null, allowFriendly);
    }

    /**
     * Apply a final post-processing on generated URLs in order to add some extra information such as CSRF tokens or
     * propagate some behavioural parameters via URL-rewriting.
     */
    protected StringBuffer postProcessURL(StringBuffer url) {
        // Keep the embedded mode using URL rewriting.
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest().getRequestObject();
        if( request != null ) {
            boolean embeddedMode = Boolean.parseBoolean(request.getParameter(Parameters.PARAM_EMBEDDED));
            String embeddedParam = Parameters.PARAM_EMBEDDED + "=true";
            if (embeddedMode && url.indexOf(embeddedParam) == -1) {
                url.append(url.indexOf("?") != -1 ? PARAM_SEPARATOR : "?");
                url.append(embeddedParam);
            }
        }
        // Add the CSRF protection token
        CSRFTokenGenerator csrfTokenGenerator = CSRFTokenGenerator.lookup();
        String token = csrfTokenGenerator.getLastToken();
        url.append(url.indexOf("?") != -1 ? PARAM_SEPARATOR : "?");
        url.append(csrfTokenGenerator.getTokenName()).append("=").append(token);
        return url;
    }
}
