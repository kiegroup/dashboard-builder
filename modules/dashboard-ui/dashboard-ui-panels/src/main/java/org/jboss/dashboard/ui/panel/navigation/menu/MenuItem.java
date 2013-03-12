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
package org.jboss.dashboard.ui.panel.navigation.menu;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Class description
 */
public abstract class MenuItem {
    private String id;
    private Map text;
    private String url;
    private Boolean selected;
    private Boolean visible;
    private Boolean current;

    //**************
    // CONSTUCTORES
    //**************

    public MenuItem() {
        id = null;
        text = new HashMap();
        url = "";
        selected = Boolean.FALSE;
        visible = Boolean.FALSE;
        current = Boolean.FALSE;
    }

    //*******************
    // GETTERS Y SETTERS
    //*******************

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map getText() {
        return this.text;
    }

    public String getText(String language) {
        String result = (String) this.text.get(language);
        if (result != null)
            return result;
        else
            return "";
    }

    public void setText(Map text) {
        if (text == null)
            this.text = new HashMap();
        else
            this.text = text;
    }

    public void setText(String language, String value) {
        if (text == null)
            text = new HashMap();

        if (value == null)
            value = "";

        text.put(language, value);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return this.selected != null && this.selected.booleanValue();
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public boolean isVisible() {
        return this.visible != null && this.visible.booleanValue();
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public boolean isCurrent() {
        return this.current != null && this.current.booleanValue();
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id=").append(getId()).append(", text=[").append(getText()).append("], url=").append(getUrl());
        sb.append(", selected=").append(isSelected()).append(", visible=").append(isVisible()).append(", current=").append(isCurrent());
        return sb.toString();
    }

    public abstract String getItemInputName();
}
