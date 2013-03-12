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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.ui.resources.Layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

/**
 * Region description
 */
public class LayoutRegion implements Serializable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LayoutRegion.class.getName());

    /* Region types */
    public static final int COLUMN = 0;
    public static final int TABBED = 1;
    public static final int ROW = 4;

    /* Region attributes */
    private String id = null;

    /**
     * @label container
     */
    private Layout layout = null;

    private int type = COLUMN;

    /**
     * Attributes to be applied to region rendering
     */
    private Map renderAttributes = new HashMap();

    public LayoutRegion() {
    }

    /**
     * Copy constructor
     */
    public LayoutRegion(LayoutRegion region) {
        this.id = region.id;
        this.type = region.type;
        this.renderAttributes.putAll(region.getRenderAttributes());
    }

    /*
     * Getters / Setters
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout template) {
        this.layout = template;
    }

    public boolean isTabbedRegion() {
        return type == TABBED;
    }

    public boolean isColumnRegion() {
        return type == COLUMN;
    }

    public boolean isRowRegion() {
        return type == ROW;
    }

    public Map getRenderAttributes() {
        return renderAttributes;
    }

    public String getDescription() {
        return id.toUpperCase();
    }

    /**
     * Returns all render attributes as a string (these will usually be applied
     * to a TD HTML tag).
     */
    public String getRenderAttributesAsString() {
        // Build attributes string
        Map attributes = getRenderAttributes();
        StringBuffer attrString = new StringBuffer();
        if (attributes != null) {
            Iterator it = attributes.keySet().iterator();
            while (it.hasNext()) {
                String attr = (String) it.next();
                String value = (String) attributes.get(attr);
                attrString.append(" " + attr + "='" + value + "'");
            }
        }

        return attrString.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LayoutRegion)) {
            return false;
        }
        return id.equals(((LayoutRegion) obj).getId());
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Region: id=");
        sb.append(id);
        sb.append(" type=");
        sb.append(type);
        return sb.toString();
    }

}