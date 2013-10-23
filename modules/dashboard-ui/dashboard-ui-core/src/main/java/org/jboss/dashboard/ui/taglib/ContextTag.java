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
package org.jboss.dashboard.ui.taglib;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.io.IOException;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.factory.Factory;

public class ContextTag extends BaseTag {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ContextTag.class.getName());

    public static final String INCLUDE_HOST = "org.jboss.dashboard.ui.taglib.ContextTag/includeHost";

    private String uri;
    private boolean includeHost = false;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isIncludeHost() {
        return includeHost;
    }

    public void setIncludeHost(boolean includeHost) {
        this.includeHost = includeHost;
    }

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return (new VariableInfo[]{});
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.String", true, VariableInfo.NESTED)
                });
        }
    }

    public final int doStartTag() throws JspException {
        try {
            if (id != null) {
                String value = getContextPath(uri, (HttpServletRequest) pageContext.getRequest());
                pageContext.setAttribute(id, value);
            }
        } catch (Exception e) {
            log.error("Error:", e);
            throw new JspException(e);
        }
        return EVAL_BODY_AGAIN;
    }

    public int doEndTag() throws JspException {
        try {
            if (super.bodyContent == null) {
                String value = getContextPath(uri, (HttpServletRequest) pageContext.getRequest());
                if (includeHost || isGlobalIncludeHost())
                    pageContext.getOut().print(getContextHost());
                pageContext.getOut().print(value == null ? "" : value);
            } else
                pageContext.getOut().print(bodyContent.getString());
        } catch (IOException e) {
            log.error("Error:", e);
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    protected boolean isGlobalIncludeHost() {
        return Boolean.TRUE.equals(pageContext.getRequest().getAttribute(INCLUDE_HOST));
    }

    public String getContextHost() {
        URLMarkupGenerator urlmg = UIServices.lookup().getUrlMarkupGenerator();
        return urlmg.getContextHost(pageContext.getRequest());
    }

    public static String getContextPath(String uri, HttpServletRequest request) {
        uri = StringUtils.defaultString(uri);
        while (uri.startsWith("/")) uri = uri.substring(1);
        String cxPath = StringUtils.defaultString(request.getContextPath());
        while (cxPath.startsWith("/")) cxPath = cxPath.substring(1);
        while (cxPath.endsWith("/")) cxPath = cxPath.substring(0, cxPath.length() - 1);
        String fullUri = StringUtils.isEmpty(cxPath) ? ("/" + uri) : ("/" + cxPath + "/" + uri);
        return StringUtils.replace(fullUri, "//", "/");
    }
}
