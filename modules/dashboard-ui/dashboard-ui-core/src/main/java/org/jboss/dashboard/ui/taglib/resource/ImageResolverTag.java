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
package org.jboss.dashboard.ui.taglib.resource;

import org.jboss.dashboard.ui.taglib.BaseTag;
import org.jboss.dashboard.ui.taglib.ContextTag;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class ImageResolverTag extends BaseTag {
    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageResolverTag.class.getName());

    private String imageURL;

    /**
     * Static image relativePath
     */
    private String relativePath;

    @Override
    public int doStartTag() throws JspException {
        if (!StringUtils.isEmpty(relativePath)) {
            imageURL = getImageResolver().getImagePath(relativePath);
            if (!StringUtils.isEmpty(imageURL)) {
                imageURL = ContextTag.getContextPath(imageURL, (HttpServletRequest) pageContext.getRequest());
            }
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        try {

            if (StringUtils.isEmpty(imageURL)) {
                if (log.isDebugEnabled()) log.debug("imageURL is null . Clearing content.");
                if (super.bodyContent != null) {
                    imageURL = super.bodyContent.getString();
                    super.bodyContent.clear();
                }
            } else {
                if (log.isDebugEnabled()) log.debug("imageURL = " + imageURL + ". ");
                if (super.id != null) {
                    if (log.isDebugEnabled()) log.debug("Setting " + super.id + " to " + imageURL);
                    super.pageContext.setAttribute(super.id, imageURL, PageContext.PAGE_SCOPE);
                    return SKIP_BODY;
                } else {
                    if (log.isDebugEnabled()) log.debug("Printing imageURL to " + imageURL);
                    super.pageContext.getOut().print(imageURL);
                }
            }
        } catch (Exception e) {
            handleError(e);
        }
        return EVAL_PAGE;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public StaticResourceResolver getImageResolver() {
        return (StaticResourceResolver) CDIBeanLocator.getBeanByName("staticImageResolver");
    }

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return new VariableInfo[0];
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.String", true, VariableInfo.AT_END)
                });
        }
    }
}
