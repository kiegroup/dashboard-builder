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

import javax.servlet.jsp.JspException;

public class ResourceImageTag extends ResourceLinkTag {

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ResourceImageTag.class.getName());

    protected String alt = null;
    protected String name = null;
    protected String width = null;
    protected String height = null;
    protected String border = null;
    protected String align = null;
    protected String title = null;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public int doEndTag() throws JspException {
        try {
            if (linkUrl == null) {
                log.debug("Link URL is null.");
                if (super.bodyContent != null)
                    super.pageContext.getOut().print(super.bodyContent.getString());
            } else {
                log.debug("Link URL is " + linkUrl);
                super.pageContext.getOut().print(getImageHtml(linkUrl));
            }
        } catch (Exception ex) {
            log.error("Error: ", ex);
            throw new JspException("Exception ", ex);
        }
        return EVAL_PAGE;
    }

    private String getImageHtml(String url) {
        StringBuffer sb = new StringBuffer();
        sb.append("<img src=\"");
        sb.append(url);
        sb.append("\"");
        if (height != null) {
            sb.append(" height=\"");
            sb.append(height);
            sb.append("\"");
        }
        if (width != null) {
            sb.append(" width=\"");
            sb.append(width);
            sb.append("\"");
        }
        if (alt != null) {
            sb.append(" alt=\"");
            sb.append(alt);
            sb.append("\"");
        }
        if (title != null) {
            sb.append(" title=\"");
            sb.append(title);
            sb.append("\"");
        }
        if (name != null) {
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        }
        if (border != null) {
            sb.append(" border=\"");
            sb.append(border);
            sb.append("\"");
        }
        if (align != null) {
            sb.append(" align=\"");
            sb.append(align);
            sb.append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

}
