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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.ui.components.DashboardFilterHandler;
import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.filter.AbstractFilter;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;

import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * Dashboard filter.
 */
public class DashboardFilter extends AbstractFilter implements DataFilter {

    /** Logger */
    protected static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardFilter.class);

    /**
     * The dashboard.
     */
    protected Dashboard dashboard;

    /**
     * Default properties are always part of the filter, no matter which data the dashboard is handling.
     */
    protected Properties[] defaultProperties;

    /**
     * Decimal format for comparison in filter.
     */
    private static DecimalFormat _numberComparisonFormat = new DecimalFormat("00000000000000000000000000000000000000000000000000.0000000000");

    /**
     * Date format for comparison in filter.
     */
    public static final SimpleDateFormat _dateComparisonFormat = new SimpleDateFormat("yyyyMMddHHmm");

    public DashboardFilter() {
        dashboard = null;
    }

    public void init() {
        removeAllProperty();
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Properties[] getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(Properties[] defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public final static String FILTER_HANDLER_CODE = "handlercode";

    public DashboardFilterHandler getHandler(Panel panel) {
        
        if (!dashboard.belongsToDashboard(panel)) return null;
        if (!(panel.getInstance().getProvider().getDriver().getClass().getName().contains("DashboardFilterDriver"))) return null;

        String code = panel.getParameterValue(FILTER_HANDLER_CODE);

        // If no request context exists then do a standard lookup.
        RequestContext reqCtx = RequestContext.lookup();
        if (reqCtx == null) return DashboardFilterHandler.lookup(code);

        // Save the current panel instance and set the specified panel as current.
        RequestContext.lookup().activatePanel(panel);

        // Get the handler component within the scope of the specified panel
        try {
            return DashboardFilterHandler.lookup(code);
        } finally {
            RequestContext.lookup().deactivatePanel(panel);
        }
    }

    public DashboardFilterProperty getFilterPropertyById(String id) {
        try {
            // Static properties.
            DashboardFilterProperty prop = getStaticPropertyById(id);
            if (prop != null) return prop;

            // Dynamic properties.
            DataProperty dp = getDashboard().getDataPropertyById(id);
            if (dp != null) return new DashboardFilterProperty(dp, this);
            return new DashboardFilterProperty(id, this);
        } catch (Exception e) {
            log.error("Cannot get data provider results.", e);
            return null;
        }
    }

    public DashboardFilterProperty getPropertyInFilterComponents(String propertyId) {
        if (dashboard.getSection() == null) return null;
        for (Panel panel : dashboard.getSection().getPanels()) {
            DashboardFilterHandler handler = getHandler(panel);
            if (handler != null) {
                DashboardFilterProperty prop = handler.getDashboardFilterProperty(propertyId);
                if (prop != null) return prop;
            }
        }
        return null;
    }

    public DashboardFilterProperty getPropertyInParentDashboards(String propId) {
        Dashboard parent = getDashboard().getParent();
        while (parent != null) {
            DashboardFilterProperty parentProp = parent.getDashboardFilter().getFilterPropertyById(propId);
            if (parentProp != null) return parentProp;
            parent = parent.getParent();
        }
        return null;
    }

    public DashboardFilterProperty[] getAllFilterProperties() {
        List results = new ArrayList();
        try {
            // Static properties.
            DashboardFilterProperty[] staticProps = getStaticProperties();
            if (staticProps != null) results.addAll(Arrays.asList(staticProps));

            // Dynamic properties.
            Iterator it = getDashboard().getDataProviders().iterator();
            while (it.hasNext()) {
                DataProvider dataProvider = (DataProvider) it.next();
                DataProperty[] properties = dataProvider.getDataSet().getProperties();
                for (int i = 0; i < properties.length; i++) {
                    DataProperty property = properties[i];
                    results.add(new DashboardFilterProperty(property, this));
                }
            }
        } catch (Exception e) {
            log.error("Cannot get data provider results.", e);
        }
        return (DashboardFilterProperty[]) results.toArray(new DashboardFilterProperty[results.size()]);
    }

     public DashboardFilterProperty[] getStaticProperties() {
         if (defaultProperties == null) return null;
         DashboardFilterProperty[] properties = new DashboardFilterProperty[defaultProperties.length];
         // Static properties.
        for (int i = 0; i < defaultProperties.length; i++) {
            Properties staticProperty = defaultProperties[i];
            String propertyId = staticProperty.getProperty("propertyid");
            DashboardFilterProperty prop = new DashboardFilterProperty(propertyId,this);
            prop.setStaticProperty(true);
            properties[i] = prop;
        }
        return properties;
     }

     public DashboardFilterProperty getStaticPropertyById(String id) {
         if (id == null) return null;
         if (defaultProperties == null) return null;
         for (int i = 0; i < defaultProperties.length; i++) {
            Properties staticProperty = defaultProperties[i];
            String propertyId = staticProperty.getProperty("propertyid");
            if (id.equals(propertyId)) {
                DashboardFilterProperty prop = new DashboardFilterProperty(propertyId,this);
                prop.setStaticProperty(true);
                return prop;
            }
        }
        return null;
     }

     public List getStaticPropertyAllowedValuesById(String propertyId) {
         if (propertyId == null) return null;
         if (defaultProperties == null) return null;
         for (int i = 0; i < defaultProperties.length; i++) {
            Properties staticProperty = defaultProperties[i];
            String id = staticProperty.getProperty("propertyid");
            if (propertyId.equals(id)) {
                List allowedValues = new ArrayList();
                String[] allowedArray = StringUtils.split(staticProperty.getProperty("allowedvalues"), ",");
                for (int j = 0; allowedArray != null && j < allowedArray.length; j++) {
                    String allowedStr = allowedArray[j];
                    try {
                        DataPropertyFormatter dpf = DataFormatterRegistry.lookup().getPropertyFormatter(propertyId);
                        allowedValues.add(dpf.parsePropertyValue(getStaticPropertyClass(propertyId), allowedStr));
                    } catch (Exception e) {
                        log.error("Can not  parse static prpoerty allowed value: " + allowedStr);
                        continue;
                    }
                }
                return allowedValues;
            }
        }
        return null;
     }

    /**
     * Get max value for a static property
     * @param propertyId property identifier.
     * @return Object[]. On index 0 max value is returned, and in index 1 max value included Boolean is returned.
     */
    public Object[] getStaticPropertyMaxValueById(String propertyId) {
        if (propertyId == null) return null;
        if (defaultProperties == null) return null;
         for (int i = 0; i < defaultProperties.length; i++) {
            Properties staticProperty = defaultProperties[i];
            String id = staticProperty.getProperty("propertyid");
            if (propertyId.equals(id)) {
                String maxValue = staticProperty.getProperty("maxvalue");
                String maxIncluded = staticProperty.getProperty("maxincluded");
                Boolean maxInc = Boolean.valueOf(maxIncluded != null && maxIncluded.equals("true"));
                return new Object[] {maxValue, maxInc};
            }
        }
        return null;
    }

    /**
     * Get min value for a static property
     * @param propertyId property identifier.
     * @return Object[]. On index 0 min value is returned, and in index 1 min value included Boolean is returned.
     */
    public Object[] getStaticPropertyMinValueById(String propertyId) {
        if (propertyId == null) return null;
        if (defaultProperties == null) return null;
        for (int i = 0; i < defaultProperties.length; i++) {
            Properties staticProperty = defaultProperties[i];
            String id = staticProperty.getProperty("propertyid");
            if (propertyId.equals(id)) {
                String minValue = staticProperty.getProperty("minvalue");
                String minIncluded = staticProperty.getProperty("minincluded");
                Boolean minInc = Boolean.valueOf(minIncluded != null && minIncluded.equals("true"));
                return new Object[] {minValue, minInc};
            }
        }
        return null;
    }

    public Class getStaticPropertyClass(String propertyId) {
        if (defaultProperties == null) return null;
        try {
            for (int i = 0;i < defaultProperties.length; i++) {
                Properties staticProperty = defaultProperties[i];
                String staticPropertyId = staticProperty.getProperty("propertyid");
                if (propertyId.equals(staticPropertyId)) return Class.forName(staticProperty.getProperty("propertytype"));
            }
        } catch (ClassNotFoundException e) {
            log.error("Specified class for filter static property named " + propertyId + " not found.");
        }
        return null;
    }

    /**
     * Get the class for a given property.
     */
    public Class getPropertyClass(String propertyId) {
        DataPropertyFormatter dpf = DataFormatterRegistry.lookup().getPropertyFormatter(propertyId);
        DataProperty property = dashboard.getDataPropertyById(propertyId);
        if (property != null) return dpf.getPropertyClass(property);
        return getStaticPropertyClass(propertyId);
    }

    // AbstractFilter implementation

    protected String formatForDisplay(String propertyId, Object value) {
        DataPropertyFormatter formatter = DataFormatterRegistry.lookup().getPropertyFormatter(propertyId);
        return formatter.formatValue(propertyId, value, LocaleManager.currentLocale());
    }

    protected String formatForComparison(String propertyId, Object value) {
        // Some types need to be prepared for comparison.
        if (value instanceof Number) return _numberComparisonFormat.format(value);
        if (value instanceof Date) return _dateComparisonFormat.format((Date) value);

        // Format by default.
        return formatForDisplay(propertyId, value);
    }

    protected Object getPropertyValue(String propertyId, Object obj) {
        try {
            Object[] instance = (Object[]) obj;
            DataProperty property = dashboard.getDataPropertyById(propertyId);
            int column = property.getDataSet().getPropertyColumn(property);
            return instance[column];               
        } catch (ClassCastException e) {
            return null;
        }
    }
}
