/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.utils.forms.RenderUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * A resource name must have the following form:
 * /&lt;workspace&gt;/&lt;section&gt;/&lt;panel&gt;/&lt;category&gt;/&lt;categoryId&gt;/&lt;resourceId&gt;, for example:
 * <p>/workspace0/1/23/skin//DEFAULT_CSS.
 * <p>Workspace, section and panel are optional, sometimes the resource can be deducted without them. If they are present,
 * the categoryId parameter acts as a default value in case the category cannot be deducted from them:
 * <p>////skin/windows_xp/DEFAULT_CSS <p>is valid, and will attempt to retrieve the default css for the windows_xp
 * skin in the system.
 * If the categoryId parameter is not specified, it will be determined by the workspace, section and panel parameters.
 * Examples:
 * <li>/workspace0/1/23/skin/windows_xp/DEFAULT_CSS -> Take the default.css for panel 23 in section 1 in workspace0. If this
 * panel has no skin, then use the windows_xp skin.
 * <li>/workspace0/1/23/skin//DEFAULT_CSS -> Take the default.css for panel 23 in section 1 in workspace0. If this
 * panel has no skin, then use the system default skin.
 * <li>////skin/windows_xp/DEFAULT_CSS -> Take the default.css for windows_xp skin.
 * <li>////skin//DEFAULT_CSS -> Take the default.css for the default skin in the system.
 * <li>///3/skin//DEFAULT_CSS -> Wrong name.
 */
public class ResourceName {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceName.class.getName());
    public static final String SEPARATOR = "/";
    public static final String NAME_FORMAT = SEPARATOR + "{0}" + SEPARATOR + "{1}" + SEPARATOR + "{2}" + SEPARATOR + "{3}" + SEPARATOR + "{4}" + SEPARATOR + "{5}";
    protected static final MessageFormat msgf = new MessageFormat(NAME_FORMAT);
    public static final boolean useBase64Names = true;

    private String resName;
    private String portableResourceName;
    private String workspaceId;
    private Long sectionId;
    private Long panelId;
    private String category;
    private String categoryId;
    private String resourceId;
    private Class resourceClass;

    public static ResourceName getInstance(String resName) {
        ResourceName resource = new ResourceName();
        try {
            //Workspace
            ParsePosition pPos = new ParsePosition(0);
            String resourceName = useBase64Names ? new String(Base64.decode(resName)) : resName;
            Object[] o = msgf.parse(resourceName, pPos);
            if (o == null)
                throw new ParseException("Cannot parse " + resourceName + ". Error at position " + pPos.getErrorIndex(), pPos.getErrorIndex());
            //log.debug("Parsing result: " + Arrays.asList(o));

            //log.debug("Workspace name determined from resource name is " + o[0]);
            resource.workspaceId = (String) o[0];
            resource.workspaceId = "".equals(resource.workspaceId) ? null : resource.workspaceId;

            //Section
            //log.debug("Section determined from resource name is " + o[1]);
            String sectionId = (String) o[1];
            if (!"".equals(sectionId))
                resource.sectionId = new Long(sectionId);

            //Panel
            //log.debug("Panel determined from resource name is " + o[2]);
            String panelId = (String) o[2];
            if (!"".equals(panelId))
                resource.panelId = new Long(panelId);

            //Category
            //log.debug("Category determined from resource name is " + o[3]);
            resource.category = (String) o[3];
            if ("".equals(resource.category))
                throw new ParseException("Cannot find non-empty category name", 3);

            //CategoryId
            //log.debug("CategoryId determined from resource name is " + o[4]);
            resource.categoryId = (String) o[4];
            resource.categoryId = "".equals(resource.categoryId) ? null : resource.categoryId;

            //ResourceId
            //log.debug("Resource determined from resource name is " + o[5]);
            resource.resourceId = (String) o[5];
            if ("".equals(resource.resourceId))
                resource.resourceId = null;

            StringBuffer sb = new StringBuffer();
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.workspaceId));
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.sectionId));
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.panelId));
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.category));
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.categoryId));
            sb.append(SEPARATOR);
            sb.append(RenderUtils.noNull(resource.resourceId));
            resource.resName = useBase64Names ? Base64.encode(sb.toString().getBytes()) : sb.toString();

            /*if (resource.categoryId != null) {
                sb=new StringBuffer();
                sb.append(SEPARATOR);
                sb.append(SEPARATOR);
                sb.append(SEPARATOR);
                sb.append(SEPARATOR);
                sb.append(RenderUtils.noNull(resource.category));
                sb.append(SEPARATOR);
                sb.append(RenderUtils.noNull(resource.categoryId));
                sb.append(SEPARATOR);
                sb.append(RenderUtils.noNull(resource.resourceId));

                resource.portableResourceName = useBase64Names ? Base64.encode(sb.toString().getBytes()) : sb.toString();
            }
            else{
                resource.portableResourceName = resource.resName;
                log.debug("Resource "+resource.resName+" is not portable.");
            } */
            resource.portableResourceName = resource.resName;

            resource.resourceClass = Class.forName("org.jboss.dashboard.ui.resources." + resource.category.substring(0, 1).toUpperCase() + resource.category.substring(1));

        } catch (Exception e) {
            log.debug("Error processing resource name. ", e);
            resource = null;
        }
        return resource;
    }

    public static String getName(String workspaceId, Long sectionId, Long panelId, String category, String categoryId, String resourceId) {
        StringBuffer resNameSb = new StringBuffer();
        resNameSb.append(SEPARATOR);
        resNameSb.append(workspaceId != null ? workspaceId : "");
        resNameSb.append(SEPARATOR);
        resNameSb.append(sectionId != null ? sectionId.toString() : "");
        resNameSb.append(SEPARATOR);
        resNameSb.append(panelId != null ? panelId.toString() : "");
        resNameSb.append(SEPARATOR);
        resNameSb.append(category);
        resNameSb.append(SEPARATOR);
        resNameSb.append(categoryId != null ? categoryId : "");
        resNameSb.append(SEPARATOR);
        resNameSb.append(resourceId);
        //return resNameSb.toString();
        return useBase64Names ? Base64.encode(resNameSb.toString().getBytes()) : resNameSb.toString();
    }

    public String getWorkspaceId() {
        return workspaceId;
    }


    public Long getSectionId() {
        return sectionId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategory() {
        return category;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String toString() {
        return resName;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ResourceName))
            return false;
        return ((ResourceName) o).resName.equals(resName);
    }

    public int compareTo(Object o) {
        return ((ResourceName) o).resName.compareTo(resName);
    }

    public Class getResourceClass() {
        return resourceClass;
    }

    /**
     * The resource portable name (without workspace info)
     *
     * @return The resource portable name (without workspace info)
     */
    public String getPortableResourceName() {
        return portableResourceName;
    }

}
