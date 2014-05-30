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
package org.jboss.dashboard.ui.panel.navigation.breadCrumb;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BreadCrumbRenderingInfo {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BreadCrumbRenderingInfo.class.getName());

    public static final String TOKEN_LINK = "{LINK}";
    public static final String TOKEN_NAME = "{NAME}";

    private List<SectionBreadCrumbItem> renderingItems;
    private String separator;
    private int initialTrimDepth = 0;
    private String itemTemplate;

    public List<SectionBreadCrumbItem> getRenderingItems() {
        return renderingItems;
    }

    public void setRenderingItems(List<SectionBreadCrumbItem> renderingItems) {
        this.renderingItems = renderingItems;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getInitialTrimDepth() {
        return initialTrimDepth;
    }

    public void setInitialTrimDepth(int initialTrimDepth) {
        this.initialTrimDepth = initialTrimDepth;
    }

    public String getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(String itemTemplate) {
        this.itemTemplate = itemTemplate;
    }

    /**
     * Items to render, excluding initialTrimDepth first
     *
     * @return Items to render, excluding initialTrimDepth first
     */
    public List<SectionBreadCrumbItem> getItemsToRender() {
        List<SectionBreadCrumbItem> l = new ArrayList<SectionBreadCrumbItem>();
        for (int i = initialTrimDepth; i < renderingItems.size(); i++) {
            l.add(renderingItems.get(i));
        }
        return l;
    }

    /**
     * Get the text to print for a given item
     * @param item item to print
     * @return the text to print for a given item
     */
    public String getTextForItem(BreadCrumbItem item){
        return performReplacementsInPattern(itemTemplate, item.getURL(), item.getName());
    }

    protected String performReplacementsInPattern(String template, String link, String name) {
        template = StringUtils.replace(template, TOKEN_LINK, link);
        template = StringUtils.replace(template, TOKEN_NAME, name);
        return template;
    }
}

