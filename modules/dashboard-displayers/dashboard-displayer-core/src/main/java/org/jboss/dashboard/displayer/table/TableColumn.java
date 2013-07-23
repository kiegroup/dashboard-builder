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
package org.jboss.dashboard.displayer.table;

import org.jboss.dashboard.LocaleManager;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

public class TableColumn {

    public static final String DEFAULT_HTMLVALUE = "{value}";

    protected String propertyId;
    protected Table table;
    protected Map<Locale,String> nameI18nMap;
    protected Map<Locale,String> hintI18nMap;
    protected String headerHtmlStyle;
    protected String cellHtmlStyle;
    protected String htmlValue;
    protected boolean selectable;
    protected boolean sortable;

    public TableColumn() {
        super();
        table = null;
        nameI18nMap = new HashMap<Locale,String>();
        hintI18nMap = new HashMap<Locale,String>();
        htmlValue = null;
        headerHtmlStyle = null;
        cellHtmlStyle = null;
        selectable = true;
        sortable = true;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getName(Locale l) {
        // Get name from view.
        if (nameI18nMap.containsKey(l)) return nameI18nMap.get(l);
        LocaleManager i18n = LocaleManager.lookup();
        String result = (String) i18n.localize(nameI18nMap);
        if (result != null) return result;

        return propertyId;
    }

    public void setName(String name, Locale l) {
        nameI18nMap.put(l, name);
    }

    public Map<Locale,String> getNameI18nMap() {
        return nameI18nMap;
    }

    public void setNameI18nMap(Map<Locale,String> nameI18nMap) {
        this.nameI18nMap = nameI18nMap;
    }

    public String getHint(Locale l) {
        if (hintI18nMap.containsKey(l)) return hintI18nMap.get(l);
        LocaleManager i18n = LocaleManager.lookup();
        String result = (String) i18n.localize(hintI18nMap);
        if (result != null) return result;

        return getName(l);
    }

    public void setHint(String name, Locale l) {
        hintI18nMap.put(l, name);
    }

    public Map<Locale,String> getHintI18nMap() {
        return hintI18nMap;
    }

    public void setHintI18nMap(Map<Locale,String> hintI18nMap) {
        this.hintI18nMap = hintI18nMap;
    }

    public String getCellHtmlStyle() {
        return cellHtmlStyle;
    }

    public void setCellHtmlStyle(String cellHtmlStyle) {
        this.cellHtmlStyle = cellHtmlStyle;
    }

    public String getHeaderHtmlStyle() {
        return headerHtmlStyle;
    }

    public void setHeaderHtmlStyle(String headerHtmlStyle) {
        this.headerHtmlStyle = headerHtmlStyle;
    }

    public String getHtmlValue() {
        return htmlValue;
    }

    public void setHtmlValue(String htmlValue) {
        this.htmlValue = htmlValue;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
