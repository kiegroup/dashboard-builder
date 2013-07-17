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

import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.resources.Resource;
import org.jboss.dashboard.ui.resources.ResourceName;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
 * Custom Tag which is used to render a resource defined for a panel
 */
public class ResourceLinkTag extends BodyTagSupport {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceLinkTag.class.getName());

    /**
     * Category of the resource
     */
    private String category = null;

    /**
     * CategoryId of the resource (to be used as default when workspace info. is present, but insufficient or
     * to be taken as mandatory).
     */
    private String categoryId = null;

    /**
     * Name of the resource
     */
    private String resourceId = null;

    /**
     * Panel ID if set by hand, (workspace and section are taken from session)
     */
    private Long panelId = null;
    /**
     * Panel ID if set by hand, (workspace and section are taken from session)
     */
    private Long sectionId = null;
    /**
     * Panel ID if set by hand, (workspace and section are taken from session)
     */
    private String workspaceId = null;

    /**
     * Use default values when retrieving the resource (using resources inheritance). Set to false when previewing the
     * resource, as it is supposed the preview has to match exactly what the element has, without inheritance.
     */
    private boolean useDefaults = true;

    /**
     * Indicate if the url generated should be portable.
     */
    private boolean portableUrl = false;

    protected String linkUrl = null;


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

    public final int doStartTag()
            throws JspException {
        try {
            linkUrl = getResourceUrl();
        } catch (Exception e) {
            log.error("Error:", e);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            if (linkUrl == null) {
                log.debug("linkUrl is null (resource not found?). Clearing content.");
                if (super.bodyContent != null) {
                    linkUrl = super.bodyContent.getString();
                    super.bodyContent.clear();
                }
            }
            if (linkUrl != null) {
                log.debug("linkUrl = " + linkUrl + ". ");
                if (super.id != null) {
                    log.debug("Setting " + super.id + " to " + linkUrl);
                    super.pageContext.setAttribute(super.id, linkUrl, PageContext.PAGE_SCOPE);
                    return SKIP_BODY;
                } else {
                    log.debug("Printing link to " + linkUrl);
                    super.pageContext.getOut().print(linkUrl);
                }
            }
        } catch (Exception ex) {
            log.error("Error: ", ex);
            throw new JspException("Exception ", ex);
        }
        return EVAL_PAGE;
    }

    protected String getResourceName() throws Exception {
        /*Section section = null;
        WorkspaceImpl workspace = null;
        Panel panel = null;

        if (workspaceId != null)
            workspace = (WorkspaceImpl) Managers.workspacesManager().getWorkspace(workspaceId);
        if (workspace != null) {
            if (sectionId != null)
                section = workspace.getSection(sectionId);
            if (section != null) {
                if (panelId != null)
                    panel = section.getPanel(panelId.toString());
            }
        } */

        String resName = ResourceName.getName(workspaceId, sectionId, panelId, category, categoryId, resourceId);

        if (resName == null) {
            log.warn("Cannot retrieve resource " + resourceId + " in category " + category);
            return null;
        } else {
            log.debug("Resource " + resourceId + " in category " + category + " has path: " + resName);
        }
        return resName;
    }

    protected Resource getResource() {
        try {
            String resName = getResourceName();
            Resource resource = UIServices.lookup().getResourceManager().getResource(resName, useDefaults);
            if (resource == null) {
                try {
                    byte[] b = Base64.decode(resName);
                    log.warn("Cannot find resource with name " + resName + ". (" + new String(b) + ")");
                } catch (Exception e) {
                    log.warn("Cannot find resource with name " + resName + ". Invalid name. ");
                }
                return null;
            }
            return resource;
        } catch (Exception e) {
            log.debug("Error getting resource: ", e);
            return null;
        }
    }

    protected String getResourceUrl() throws Exception {
        Resource resource = getResource();
        if (resource == null)
            return null;
        String url = resource.getResourceUrl(pageContext.getRequest(), pageContext.getResponse(), isPortableUrl());
        log.debug("Generated resource url: " + url);
        return url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public boolean isUseDefaults() {
        return useDefaults;
    }

    public void setUseDefaults(boolean useDefaults) {
        this.useDefaults = useDefaults;
    }

    public boolean isPortableUrl() {
        return portableUrl;
    }

    public void setPortableUrl(boolean portableUrl) {
        this.portableUrl = portableUrl;
    }
}
