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

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Section;

import java.util.*;

public class DashboardFilterProperty {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardFilterProperty.class.getName());

    protected DashboardFilter filter;
    protected String dataProviderCode;
    protected String propertyId;
    protected Long sectionId;
    protected boolean visible;
    protected boolean isBeingFiltered;
    protected boolean global;

    public static final String STATIC_PROPERTY_CODE = "staticProperty";

    public DashboardFilterProperty(String propertyId, DashboardFilter filter) {
        this(null, propertyId, filter, null, false);
    }

    public DashboardFilterProperty(String dataProviderCode, String propertyId ,DashboardFilter filter) {
        this(dataProviderCode, propertyId, filter, null, false);
    }

    public DashboardFilterProperty(DataProperty property, DashboardFilter filter) {
        this(property.getDataSet().getDataProvider().getCode(), property.getPropertyId(), filter, null, false);
    }

    public DashboardFilterProperty(String dataProviderCode, String propertyId, DashboardFilter filter, Long sectionId, boolean isBeingFiltered) {
        this.filter = filter;
        this.dataProviderCode = dataProviderCode;
        this.propertyId = propertyId;
        this.sectionId = sectionId;
        this.isBeingFiltered = isBeingFiltered;
        this.visible = false;
    }

    public DashboardFilter getCurrentPageFilter() {
        return filter;
    }

    public void setCurrentPageFilter(DashboardFilter filter) {
        this.filter = filter;
    }

    public Dashboard getDrillDownDashboard() {
        return DashboardHandler.lookup().getDashboard(getDrillDownPage());
    }

    public Section getDrillDownPage() {
        if (!isDrillDownEnabled()) return null;
        NavigationManager navMgr = NavigationManager.lookup();
        return navMgr.getCurrentWorkspace().getSection(sectionId);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getDataProviderCode() {
        return dataProviderCode;
    }

    public void setDataProviderCode(String dataProviderCode) {
        this.dataProviderCode = dataProviderCode;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public boolean isBeingFiltered() {
        return isBeingFiltered;
    }

    public void setBeingFiltered(boolean isBeingFiltered) {
        this.isBeingFiltered = isBeingFiltered;
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;

            DashboardFilterProperty prop = (DashboardFilterProperty) obj;
            if (prop.getDataProviderCode().equals(dataProviderCode) && prop.getPropertyId().equals(propertyId)) return true;
        }
        catch (NullPointerException e) {}
        catch (ClassCastException e) {}
        return false;
    }

    public DataProperty getDataProperty() {
        if (isStaticProperty()) return null;
        try {
            DataProvider provider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(dataProviderCode);
            if (provider != null) return provider.getDataSet().getPropertyById(propertyId);
            return null;
        } catch (Exception e) {
            log.error("Cannot get property " + propertyId + " from data provider with code " + dataProviderCode);
            return null;
        }
    }

    public List getPropertyDistinctValues() {
        List allValues = getPropertyAllValues();
        Set distinctValues = new HashSet(allValues);
        List sortedValues = new ArrayList(distinctValues);
        Collections.sort(sortedValues);
        return sortedValues;
    }

    public List getPropertyAllValues() {
        List results = null;
        if (isStaticProperty()) {
            results = filter.getStaticPropertyAllowedValuesById(propertyId);
        } else {
            DataProperty dp = getDataProperty();
            if (dp != null) results = dp.getValues();
        }
        if (results == null) return new ArrayList();

        // Purge the null values before return.
        Iterator it = results.iterator();
        while (it.hasNext()) if (it.next() == null) it.remove();
        return results;
    }

    public List getPropertySelectedValues() {
        return filter.getPropertyAllowedValues(propertyId);
    }

    public Comparable getPropertyMinValue() {
        return filter.getPropertyMinValue(propertyId);
    }

    public Comparable getPropertyMaxValue() {
        return filter.getPropertyMaxValue(propertyId);
    }

    public boolean minValueIncluded() {
        return filter.minValueIncluded(propertyId);
    }

    public boolean maxValueIncluded() {
        return filter.maxValueIncluded(propertyId);
    }

    public boolean isDrillDownEnabled() {
        return sectionId != null;
    }

    public String getPropertyName(Locale l) {
        // If property exist in any dashboard's data provider, get it and get its name
        DataProperty property = getDataProperty();
        if (property != null) return property.getName(l);

        // If property is static, use data property formatter to get its name.
        DataPropertyFormatter df = getDataPropertyFormatter();
        if (df != null) return df.formatName(propertyId, l);
        return propertyId;
    }

    public String formatPropertyValue(Object value, Locale l) {
        DataProperty property = getDataProperty();
        DataPropertyFormatter df = getDataPropertyFormatter();
        if (df == null) return value == null ? "" : value.toString();
        if (property != null) return df.formatValue(property, value, l);
        return df.formatValue(propertyId,value,l);
    }

    public Object parsePropertyValue(String value) throws Exception {
        DataPropertyFormatter df = getDataPropertyFormatter();
        return isStaticProperty() ? df.parsePropertyValue(getPropertyClass(), value) : df.parsePropertyValue(getDataProperty(),value);
    }

    public Class getPropertyClass() {
        return filter.getPropertyClass(propertyId);
    }

    public boolean isStaticProperty() {
        return STATIC_PROPERTY_CODE.equals(dataProviderCode);
    }

    public DataPropertyFormatter getDataPropertyFormatter() {
        return DataFormatterRegistry.lookup().getPropertyFormatter(propertyId);
    }

    public void setStaticProperty(boolean isStatic) {
        if (isStatic) dataProviderCode = STATIC_PROPERTY_CODE;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isPropertyAlive() {
        return filter.getStaticPropertyById(propertyId)  != null || filter.getDashboard().getDataPropertyById(propertyId) != null;
    }

    public boolean isLabelProperty() {
        return String.class.isAssignableFrom(getPropertyClass());
    }

    public boolean isNumericProperty() {
        return Number.class.isAssignableFrom(getPropertyClass());
    }

    public boolean isDateProperty() {
        return Date.class.isAssignableFrom(getPropertyClass());
    }
}
