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
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.ApplicationScoped;
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
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(URLMarkupGenerator.class.getName());

    private String handler = "factory";
    private String action = "set";
    public static final String COMMAND_RUNNER = "Controller";
    public static final String FRIENDLY_PREFIX = "workspace";

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
     * @param bean     Factory component that will perform the action
     * @param property Component's property (method) that will be invoked
     * @param params   Extra parameters for link
     * @return a link url to a factory component action, independent on the page
     */
    public String getPermanentLink(String bean, String property, Map params) {
        String base = /*this.getBasePath();
        while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        base = base + "/" +*/ RequestContext.getCurrentContext().getRequest().getRequestObject().getContextPath() + "/" + COMMAND_RUNNER;
        StringBuffer sb = new StringBuffer();
        sb.append(base).append("?");
        String alias = Factory.getAlias(bean);
        //HandlerFactoryElement _component = (HandlerFactoryElement) Factory.lookup(bean);
        params.put(FactoryURL.PARAMETER_BEAN, alias != null ? alias : bean);
        params.put(FactoryURL.PARAMETER_PROPERTY, property);
        sb.append(getParamsMarkup(params));
        try {
            HandlerFactoryElement element = (HandlerFactoryElement) Factory.lookup(bean);
            if (element != null) element.setEnabledForActionHandling(true);
            else log.debug("Bean '" + bean + "' not found on factory");
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a HandlerFactoryElement.");
        }
        return sb.toString();
    }

    /**
     * Get a permanent link to a given action on a bean
     *
     * @param bean     Factory component that will perform the action
     * @param property Component's property (method) that will be invoked
     * @param params   Extra parameters for link
     * @return a link url to a factory component action, independent on the page
     */
    public String getRemotePermanentLink(String baseUrl, String bean, String property, Map params) {
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        return baseUrl + getPermanentLink(bean, property, params);
    }

    public String getRemoteLink(String baseUrl, String path, Map params) {
        path = StringUtils.defaultIfEmpty(path, "");
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) baseUrl += "/";
        return baseUrl + path + "?" + getParamsMarkup(params);
    }

    /**
     * Get the base URI for any markup, that is, the base path plus the servlet mapping. Any uri
     * constructed on top of it will go to the Controller servlet
     *
     * @return the base URI for any markup
     */
    public String getServletMapping() {
        return COMMAND_RUNNER;
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
            return getRelativeLinkToPage(panel.getSection(), true);
        }
        return getServletMapping();
    }

    /**
     * Generate a link to a factory component action
     *
     * @param bean     Factory component that will perform the action
     * @param property Component's property (method) that will be invoked
     * @param params   Extra parameters for link
     * @return a link url to a factory component action
     */
    public String getMarkup(String bean, String property, Map params) {
        if (params == null) params = new HashMap();
        Panel panel = getCurrentPanel();
        if (panel != null) {
            params.put(Parameters.DISPATCH_IDPANEL, panel.getPanelId());
            params.put(Parameters.DISPATCH_ACTION, "_factory");
        }

        StringBuffer sb = new StringBuffer();
        sb.append(getServletMapping()).append("?");
        String alias = Factory.getAlias(bean);
        HandlerFactoryElement component = (HandlerFactoryElement) Factory.lookup(bean);
        params.put(FactoryURL.PARAMETER_BEAN, alias != null ? alias : bean);
        params.put(FactoryURL.PARAMETER_PROPERTY, component.getActionName(property));
        sb.append(getParamsMarkup(params));
        try {
            HandlerFactoryElement element = (HandlerFactoryElement) Factory.lookup(bean);
            element.setEnabledForActionHandling(true);
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a HandlerFactoryElement.");
        }
        return sb.toString();
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
            if (it.hasNext())
                sb.append("&amp;");
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

    /**
     * @param request
     * @return the context this application is running in, preceded by "/", or just "/" if the context is empty
     */
    public String getContext(ServletRequest request) {
        String context = ((HttpServletRequest) request).getContextPath();
        return context.length() > 0 ? context : "/";
    }

    public String getContextHost(ServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String context = ((HttpServletRequest) request).getContextPath();
        String protocol = request.getScheme();
        while (context.startsWith("/")) context = context.substring(1);
        sb.append(protocol.toLowerCase()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80)
            sb.append(":").append(request.getServerPort());
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
        while (context.startsWith("/")) context = context.substring(1);
        sb.append(protocol.toLowerCase()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80)
            sb.append(":").append(request.getServerPort());
        sb.append("/");
        if (!StringUtils.isEmpty(context))
            sb.append(context).append("/");
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
        return getRelativeLinkToWorkspace(workspace, allowFriendly);
    }

    /**
     * Get an absolute url that leads to a given workspace in a given lang
     *
     * @param workspace        Workspace to take to
     * @param allowFriendly allow friendly url
     * @return an url that leads to a given workspace
     */
    public String getLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly, String lang) {
        return getRelativeLinkToWorkspace(workspace, allowFriendly, lang);
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
        StringBuffer sb = new StringBuffer();
        sb.append(getLinkToWorkspace(page.getWorkspace(), allowFriendly, lang));
        String pageFriendlyUrl = page.getId().toString();
        if (allowFriendly) {
            pageFriendlyUrl = StringUtils.defaultIfEmpty(page.getFriendlyUrl(), page.getId().toString());
        }
        sb.append("/").append(pageFriendlyUrl);
        return sb.toString();
    }

    protected String getRelativeLinkToPage(Section page, boolean allowFriendly) {
        StringBuffer sb = new StringBuffer();
        sb.append(getRelativeLinkToWorkspace(page.getWorkspace(), allowFriendly));
        String pageFriendlyUrl = page.getId().toString();
        if (allowFriendly) {
            pageFriendlyUrl = StringUtils.defaultIfEmpty(page.getFriendlyUrl(), page.getId().toString());
        }
        sb.append("/").append(pageFriendlyUrl);
        return sb.toString();
    }

    protected String getRelativeLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly) {
        return getRelativeLinkToWorkspace(workspace, allowFriendly, LocaleManager.currentLang());
    }

    protected String getRelativeLinkToWorkspace(WorkspaceImpl workspace, boolean allowFriendly, String lang) {
        StringBuffer sb = new StringBuffer();
        String friendlyUrl = workspace.getId();
        if (allowFriendly) {
            friendlyUrl = StringUtils.defaultIfEmpty(workspace.getFriendlyUrl(), workspace.getId());
        }
        sb.append(RequestContext.getCurrentContext().getRequest().getRequestObject().getContextPath() + "/" + FRIENDLY_PREFIX + "/").append(lang).append("/").append(friendlyUrl);
        return sb.toString();
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
        sb.append(getLinkToPage(panel.getSection(), allowFriendly));
        sb.append("?");
        String paramsMarkup = getParamsMarkup(paramsMap);
        sb.append(paramsMarkup);
        if (extraParams != null)
            sb.append("&amp;").append(extraParams);
        return sb.toString();
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
}