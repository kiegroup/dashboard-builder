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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.DashboardSettings;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.components.DashboardFilterHandler;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.provider.DataProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardFilterFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardFilterFormatter.class.getName());

    public static final String PARAM_RENDER_TYPE = "renderType";
    public static final String PARAM_COMPONENT_CODE = "componentCode";
    public static final String RENDER_TYPE_SHOW = "renderShow";
    public static final String RENDER_TYPE_PROPERTIES = "renderProperties";
    public static final String RENDER_TYPE_EDIT_PROPERTIES = "renderEditProperties";

    protected static final String I18N_BUNDLE_NAME = "org.jboss.dashboard.ui.components.filter.messages";

    public DashboardFilterHandler getDashboardFilterHandler() {
        return DashboardFilterHandler.lookup((String) getParameter(PARAM_COMPONENT_CODE));
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        String renderType = (String) getParameter(PARAM_RENDER_TYPE);
        if (RENDER_TYPE_PROPERTIES.equals(renderType)) {
            serviceForProperties(httpServletRequest, httpServletResponse);
        } else if (RENDER_TYPE_SHOW.equals(renderType)) {
            if (getDashboardFilterHandler().isShowMode()) serviceForShowMode(httpServletRequest, httpServletResponse);
            else serviceForEditMode(httpServletRequest, httpServletResponse);
        } 
    }

    protected void serviceForProperties(HttpServletRequest request, HttpServletResponse response) {
        DashboardFilter filter = getDashboardFilterHandler().getFilter();
        List properties = getDashboardFilterHandler().getProperties();
        renderFragment("outputStart");

        // Show properties
        if (properties.size() ==0) renderFragment("outputEmpty");
        Iterator it = properties.iterator();
        while (it.hasNext()) {
            DashboardFilterProperty dashboardFilterProperty = (DashboardFilterProperty) it.next();
            if (dashboardFilterProperty.isBeingFiltered()) continue;
            if (!dashboardFilterProperty.isVisible()) continue;

            renderFragment("outputStartRow");
            renderPropertyName(dashboardFilterProperty);
            if (dashboardFilterProperty.isLabelProperty()) {

                // Get the property allowed values.
                String allowedValue = null;
                if (filter != null && filter.containsProperty(dashboardFilterProperty.getPropertyId())) {
                    List filterAllowedValues = filter.getPropertyAllowedValues(dashboardFilterProperty.getPropertyId());
                    if (filterAllowedValues.size() == 1) allowedValue = (String) filterAllowedValues.get(0);
                }

                // Get the list of distinct values for this label property. In order to avoid performance issues,
                // no combos of more than a given number of entries are allowed. In such cases the only way to enter
                // filter values is via the custom entry option.
                List allowedValues = dashboardFilterProperty.getPropertyDistinctValues();
                if (allowedValues.size() > DashboardSettings.lookup().getMaxEntriesInFilters()) allowedValues = new ArrayList();

                // Build the filter combo options.
                String[] keys = new String[allowedValues.size()+2];
                String[] values = new String[allowedValues.size()+2];
                keys[0] = DashboardFilterHandler.PARAM_NULL_VALUE;
                values[0] = " - " + getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "select") + " " + StringEscapeUtils.escapeHtml(dashboardFilterProperty.getPropertyName(getLocale())) + " - ";
                keys[1]=DashboardFilterHandler.PARAM_CUSTOM_VALUE;
                values[1]= " - " + getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "custom") + " - ";
                Iterator it1 = allowedValues.iterator();
                for (int i = 2; it1.hasNext(); i++) {
                    Object value = it1.next();
                    keys[i]= Integer.toString(i);
                    if (value != null && value.equals(allowedValue)) setAttribute("selected", Integer.toString(i));
                    values[i] = StringEscapeUtils.escapeHtml(dashboardFilterProperty.formatPropertyValue(value, getLocale()));
                }
                if (allowedValue == null) setAttribute("selected","0");
                setAttribute("keys",keys);
                setAttribute("values",values);
                setDefaultTypeAttributes(dashboardFilterProperty);
                setAttribute("submitOnChange",getDashboardFilterHandler().isShowSubmitOnChange());
                renderFragment("outputPropertyTypeLabel");
            } else if (dashboardFilterProperty.isNumericProperty()) {
                setDefaultTypeAttributes(dashboardFilterProperty);

                Object minValue = null;
                Object maxValue = null;
                if (filter != null && filter.containsProperty(dashboardFilterProperty.getPropertyId())) {
                    maxValue = dashboardFilterProperty.getPropertyMaxValue();
                    minValue = dashboardFilterProperty.getPropertyMinValue();
                }
                setAttribute("minValue",minValue);
                setAttribute("maxValue",maxValue);
                renderFragment("outputPropertyTypeNumeric");
            } else if (dashboardFilterProperty.isDateProperty()) {
                setDefaultTypeAttributes(dashboardFilterProperty);

                Object minValue = null;
                Object maxValue = null;
                if (filter != null && filter.containsProperty(dashboardFilterProperty.getPropertyId())) {
                    maxValue = dashboardFilterProperty.getPropertyMaxValue();
                    minValue = dashboardFilterProperty.getPropertyMinValue();
                }
                setAttribute("minValue",minValue);
                setAttribute("maxValue",maxValue);
                setAttribute("submitOnChange",getDashboardFilterHandler().isShowSubmitOnChange());
                renderFragment("outputPropertyTypeDate");
            } else {
                log.warn("Domain for property " + dashboardFilterProperty.getPropertyId()  + " is not supported.");
            }
            renderFragment("outputEndRow");
        }
        renderFragment("outputEnd");
    }

    protected void serviceForShowMode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // Render panel duplicated error.
        if (getDashboardFilterHandler().isPanelDuplicated()) {
            renderFragment("outputStart");
            renderFragment("outputPanelDuplicated");
            renderFragment("outputEnd");
            return;
        }

        // Render panel.
        renderFragment("outputStart");

        // Render if empty.
        if (checkEmpty()) {
            renderFragment("outputEmpty");
        } else {

            // Show legend.
            DashboardFilterProperty[] filteredProperties = getDashboardFilterHandler().getBeingFilteredProperties();
            if (getDashboardFilterHandler().isShowLegend() && filteredProperties.length > 0) {
                renderFragment("outputStartLegend");
                for (int i = 0; i < filteredProperties.length; i++) {
                    DashboardFilterProperty dashboardFilterProperty = filteredProperties[i];
                    setAttribute("propertyId", dashboardFilterProperty.getPropertyId());
                    setAttribute("propertyName", StringEscapeUtils.escapeHtml(dashboardFilterProperty.getPropertyName(getLocale())));
                    setAttribute("index", new Integer(i));
                    if (dashboardFilterProperty.isLabelProperty()) {
                        String filterValue = dashboardFilterProperty.formatPropertyValue(dashboardFilterProperty.getPropertySelectedValues(), getLocale());
                        setAttribute("propertyValue", StringEscapeUtils.escapeHtml(filterValue));
                        renderFragment("outputLegendStringProperty");
                    } else {
                        String minValue = dashboardFilterProperty.formatPropertyValue(dashboardFilterProperty.getPropertyMinValue(), getLocale());
                        String maxValue = dashboardFilterProperty.formatPropertyValue(dashboardFilterProperty.getPropertyMaxValue(), getLocale());
                        setAttribute("propertyMinValue", minValue);
                        setAttribute("propertyMaxValue", maxValue);
                        StringBuffer str = new StringBuffer();
                        str.append(getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "from")).append("  ");
                        str.append(minValue);
                        boolean existMaxValue = maxValue != null && maxValue.trim().length() > 0;
                        if (existMaxValue) {
                            str.append(" ").append(getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "to")).append("  ");
                            str.append(maxValue);
                        }
                        setAttribute("outputText", str.toString());
                        renderFragment("outputLegendToFromProperty");
                    }
                }
                renderFragment("outputEndLegend");
            }

            renderFragment("outputStartProperties");
            includePage(getDashboardFilterHandler().getJSPForProperties());
            renderFragment("outputEndProperties");
        }

        renderFragment("outputStartBottom");
        if (getDashboardFilterHandler().isShowAutoRefresh()) renderFragment("outputAutoRefresh");
        int colspan = getDashboardFilterHandler().isShowAutoRefresh() ? 1 : 2;
        setAttribute("colspan", colspan);
        renderFragment("outputStartButtons");
        if (getDashboardFilterHandler().isShowApplyButton()) renderFragment("outputApplyButton");
        if (getDashboardFilterHandler().isShowClearButton()) renderFragment("outputClearButton");
        if (getDashboardFilterHandler().isShowRefreshButton()) renderFragment("outputRefreshButton");
        renderFragment("outputEndButtons");
        renderFragment("outputEndBottom");
        renderFragment("outputEnd");
    }

    protected boolean checkEmpty() {
        DashboardFilterHandler handler = getDashboardFilterHandler();
        boolean filteredPropertiesVisible = handler.isShowLegend() && handler.getBeingFilteredProperties().length > 0;
        return (!filteredPropertiesVisible && handler.getVisibleProperties().isEmpty());
    }

    protected void serviceForEditMode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        renderFragment("outputStart");
        renderFragment("outputTableStart");

        // Render options.
        setAttribute("refreshChecked",getDashboardFilterHandler().isShowRefreshButton());
        setAttribute("pNamesChecked",getDashboardFilterHandler().isShowPropertyNames());
        setAttribute("applyChecked",getDashboardFilterHandler().isShowApplyButton());
        setAttribute("clearChecked",getDashboardFilterHandler().isShowClearButton());
        setAttribute("submitOnChangeChecked",getDashboardFilterHandler().isShowSubmitOnChange());
        setAttribute("shortModeChecked", getDashboardFilterHandler().isShortMode());
        setAttribute("showLegendChecked", getDashboardFilterHandler().isShowLegend());
        setAttribute("showAutoRefresh", getDashboardFilterHandler().isShowAutoRefresh());
        renderFragment("outputOptions");

        // Render avaliable properties.
        renderFragment("outputHeader");
        DashboardFilterProperty[] properties = getDashboardFilterHandler().getAllPropertiesForCurrentFilter();
        if (properties.length == 0) renderFragment("outputEmpty");
        else {
            for (int i = 0; i < properties.length; i++) {
                DashboardFilterProperty property = properties[i];

                String dataProviderName;
                try {
                    if (property.isStaticProperty())
                        dataProviderName = getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "staticProperty");
                    else {
                        DataProvider provider = getDashboardFilterHandler().getDashboard().getDataProviderByCode(property.getDataProviderCode());
                        dataProviderName = provider.getDescription(getLocale());
                    }
                } catch (Exception e) {
                    log.error("Cannot get data provider with code " + property.getDataProviderCode());
                    continue;
                }
                setAttribute("index",new Integer(i));
                setAttribute("dataProviderCode",property.getDataProviderCode());
                setAttribute("propertyId",property.getPropertyId());
                setAttribute("visibleChecked",Boolean.valueOf(property.isVisible()));
                setAttribute("drillDownChecked",Boolean.valueOf(property.isDrillDownEnabled()));
                setAttribute("sectionId",property.getSectionId());
                // Drill down page title.
                String currentSectionTitle = "-- " + getBundle().getString(DashboardFilterHandler.I18N_PREFFIX + "select") + " --";
                Section section = property.getDrillDownPage();
                if (section != null) currentSectionTitle = getLocalizedValue(section.getTitle());
                setAttribute("currentSectionTitle", StringEscapeUtils.escapeHtml(currentSectionTitle));
                setAttribute("dataProviderName", StringEscapeUtils.escapeHtml(dataProviderName));
                setAttribute("propertyName",StringEscapeUtils.escapeHtml(property.getPropertyName(getLocale())));
                renderFragment("outputTableElement");
            }
        }

        // Render not allowed proeprties.
        List notAllowedProps = getDashboardFilterHandler().getNotAllowedProperties();
        if (!notAllowedProps.isEmpty()) {
            renderFragment("outputNotAllowedPropertiesStart");
            Iterator it = notAllowedProps.iterator();
            while (it.hasNext()) {
                DashboardFilterProperty dashboardFilterProperty = (DashboardFilterProperty) it.next();
                DataProvider provider = getDashboardFilterHandler().getDashboard().getDataProviderByCode(dashboardFilterProperty.getDataProviderCode());
                String dataProviderName = StringEscapeUtils.escapeHtml(provider.getDescription(getLocale()));
                setAttribute("dataProviderName", dataProviderName);
                setAttribute("propertyName", StringEscapeUtils.escapeHtml(dashboardFilterProperty.getPropertyName(getLocale())));
                renderFragment("outputNotAllowedProperty");
            }
            renderFragment("outputNotAllowedPropertiesEnd");
        }
        getDashboardFilterHandler().getNotAllowedProperties().clear();

        renderFragment("outputTableEnd");
        renderFragment("outputEnd");
    }

    protected ResourceBundle getBundle() {
        return ResourceBundle.getBundle(I18N_BUNDLE_NAME, getLocale());
    }

    protected void renderPropertyName(DashboardFilterProperty property) {
        if (getDashboardFilterHandler().isShowPropertyNames()) {
            setAttribute("propertyName", StringEscapeUtils.escapeHtml(property.getPropertyName(getLocale())));
            if (getDashboardFilterHandler().hasError(property.getPropertyId())) renderFragment("outputErrorPropertyName");
            else renderFragment("outputPropertyName");
        } else {
            renderFragment("outputNewColumn");
        }
    }

    protected void setDefaultTypeAttributes(DashboardFilterProperty property) {
        setAttribute("dataProviderCode", property.getDataProviderCode());
        setAttribute("propertyId", property.getPropertyId());
        setAttribute("propertyName", property.getPropertyName(getLocale()));
    }
}
