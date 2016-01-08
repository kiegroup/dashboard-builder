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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.DOMParser;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import javax.inject.Inject;

@PanelScoped
public class DashboardFilterHandler extends UIBeanHandler {

    public static final String I18N_PREFFIX = "dashboardFilter.";

    public static final String PARAM_VALUE = "value";
    public static final String PARAM_VALUE_MIN = "minValue";
    public static final String PARAM_VALUE_MAX = "maxValue";
    public static final String PARAM_CUSTOM_VALUE = "customValue";
    public static final String PARAM_LAST_HOUR = "lastHour";
    public static final String PARAM_LAST_12HOURS = "last12Hours";
    public static final String PARAM_TODAY = "today";
    public static final String PARAM_YESTERDAY = "yesterday";
    public static final String PARAM_LAST_7DAYS = "last7Days";
    public static final String PARAM_THIS_MONTH = "thisMonth";
    public static final String PARAM_LAST_MONTH = "lastMonth";
    public static final String PARAM_THIS_QUARTER = "thisQuarter";
    public static final String PARAM_LAST_QUARTER = "lastQuarter";
    public static final String PARAM_LAST_6MONTHS = "last12Months";
    public static final String PARAM_THIS_YEAR = "thisYear";
    public static final String PARAM_LAST_YEAR = "lastYear";
    public static final String PARAM_NULL_VALUE = "---";
    public static final String PARAM_VISIBLE = "visible";
    public static final String PARAM_DRILLDOWN_DISABLED = "drillDownDisabled";
    public static final String PARAM_SECTION = "section";
    public static final String PARAM_SHOW_REFRESH_BUTTON = "showRefreshButton";
    public static final String PARAM_SHOW_APPLY_BUTTON = "showApplyButton";
    public static final String PARAM_SHOW_CLEAR_BUTTON = "showClearButton";
    public static final String PARAM_SHOW_PROPERTY_NAMES = "showPropertyNames";
    public static final String PARAM_SHOW_SUBMIT_ON_CHANGE = "showSubmitOnChange";
    public static final String PARAM_SHOW_AUTO_REFRESH = "showAutoRefresh";
    public static final String PARAM_SHORT_MODE = "shortMode";
    public static final String PARAM_SHOW_LEGEND= "showLegend";

    @Inject
    private transient Logger log;

    @Inject @Config("/components/bam/dashboard_filter/extended/show.jsp")
    protected String componentIncludeJSPshow;

    @Inject @Config("/components/bam/dashboard_filter/extended/edit.jsp")
    protected String componentIncludeJSPedit;

    @Inject @Config("/components/bam/dashboard_filter/extended/properties.jsp")
    protected String componentIncludeJSPproperties;

    @Inject @Config("/components/bam/dashboard_filter/short/show.jsp")
    protected String componentIncludeJSPshort_show;

    @Inject @Config("/components/bam/dashboard_filter/short/edit.jsp")
    protected String componentIncludeJSPshort_edit;

    @Inject @Config("/components/bam/dashboard_filter/short/properties.jsp")
    protected String componentIncludeJSPshort_properties;

    @Inject
    protected DashboardHandler dashboardHandler;

    @Inject
    protected DashboardFilterRequestProcessor requestProcessor;

    protected String serializedProperties;

    // Component options.
    protected boolean isShowMode;
    protected boolean isEditMode;
    protected boolean isShortMode;
    protected boolean showPropertyNames;
    protected boolean showRefreshButton;
    protected boolean showApplyButton;
    protected boolean showClearButton;
    protected boolean showSubmitOnChange;
    protected boolean showLegend;
    protected boolean showAutoRefresh;

    // Handle component properties.
    protected List<DashboardFilterProperty> properties; // Properties selected in edit mode. Filter can be executed with these properties.
    protected List notAllowedProperties; // Properties that cannot be displayed because there is another property visible with same property identifier.
    protected Set filterPropertyErrors; // Errors when data input to filter cannot be parsed.
    protected boolean panelDuplicated; // Flag which indicate if a panel instance is duplicated in different sections.

    // Auto-Refresh control
    protected int autoRefreshTimeout;
    protected boolean refreshEnabled;

    public DashboardFilterHandler() {
        serializedProperties = null;
        isShowMode = true;
        isEditMode = false;
        isShortMode = true;
        properties = new ArrayList();
        filterPropertyErrors = new HashSet();
        notAllowedProperties = new ArrayList();
        showPropertyNames = true;
        showRefreshButton = true;
        showApplyButton = true;
        showClearButton = true;
        showSubmitOnChange = true;
        showLegend = true;
        showAutoRefresh = false;
        autoRefreshTimeout = 15;
        panelDuplicated = false;
    }

// -------------- START GETTERS AND SETTERS --------------------- //

    public boolean isPanelDuplicated() {
        return panelDuplicated;
    }

    public void setPanelDuplicated(boolean panelDuplicated) {
        this.panelDuplicated = panelDuplicated;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public int getAutoRefreshTimeout() {
        return autoRefreshTimeout;
    }

    public void setAutoRefreshTimeout(int autoRefreshTimeout) {
        this.autoRefreshTimeout = autoRefreshTimeout;
    }

    public boolean isShowAutoRefresh() {
        return showAutoRefresh;
    }

    public void setShowAutoRefresh(boolean showAutoRefresh) {
        this.showAutoRefresh = showAutoRefresh;
    }

    public List getNotAllowedProperties() {
        // Return a copy of the array to avoid concurrent issues.
        return notAllowedProperties;
    }

    public void clearNotAllowedProperties() {
        notAllowedProperties.clear();
    }

    public boolean isShowSubmitOnChange() {
        return showSubmitOnChange;
    }

    public void setShowSubmitOnChange(boolean showSubmitOnChange) {
        this.showSubmitOnChange = showSubmitOnChange;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public DashboardFilterRequestProcessor getRequestProcessor() {
        return requestProcessor;
    }

    public void setRequestProcessor(DashboardFilterRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public boolean isShowApplyButton() {
        return showApplyButton;
    }

    public void setShowApplyButton(boolean showApplyButton) {
        this.showApplyButton = showApplyButton;
    }

    public boolean isShowClearButton() {
        return showClearButton;
    }

    public void setShowClearButton(boolean showClearButton) {
        this.showClearButton = showClearButton;
    }

    public boolean isShowRefreshButton() {
        return showRefreshButton;
    }

    public void setShowRefreshButton(boolean showRefreshButton) {
        this.showRefreshButton = showRefreshButton;
    }

    public boolean isShowPropertyNames() {
        return showPropertyNames;
    }

    public void setShowPropertyNames(boolean showPropertyNames) {
        this.showPropertyNames = showPropertyNames;
    }

    public List<DashboardFilterProperty> getProperties() {
        // Return a copy of the array to avoid concurrent issues.
        return new ArrayList<DashboardFilterProperty>(properties);
    }

    public void setProperties(List<DashboardFilterProperty> l) {
        properties.clear();
        properties.addAll(l);
    }

    public DashboardHandler getDashboardHandler() {
        return dashboardHandler;
    }

    public void setDashboardHandler(DashboardHandler dashboardHandler) {
        this.dashboardHandler = dashboardHandler;
    }

    public boolean isShortMode() {
        return isShortMode;
    }

    public void setShortMode(boolean shortMode) {
        isShortMode = shortMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public boolean isShowMode() {
        return isShowMode;
    }

    public String getJSPForShowMode() {
        if (isShortMode) return componentIncludeJSPshort_show;
        else return componentIncludeJSPshow;
    }

    public String getJSPForEditMode() {
        return componentIncludeJSPedit;
    }

    public String getJSPForProperties() {
        if (!isShortMode) return componentIncludeJSPproperties;
        else return componentIncludeJSPshort_properties;
    }

    public String getJSP() {
        if (isEditMode) return getJSPForEditMode();
        return getJSPForShowMode();
    }

    public String getBeanJSP() {
        return getJSP();
    }

    // -------------- END GETTERS AND SETTERS --------------------- //

    // -------------- START HANDLING METHODS ---------------------- //

    public static DashboardFilterHandler lookup(String code) {
        DashboardFilterHandler handler = null;

        // Get handler for code;
        if (!StringUtils.isBlank(code)) handler = (DashboardFilterHandler) CDIBeanLocator.getBeanByName("DashboardFilterHandler_" + code);
        if (handler == null) handler = CDIBeanLocator.getBeanByType(DashboardFilterHandler.class);
        return handler;
    }

    public Dashboard getDashboard() {
        return dashboardHandler.getCurrentDashboard();
    }

    public DashboardFilter getFilter() {
        return getDashboard().getDashboardFilter();
    }

    public void enableEditMode() {
        isEditMode = true;
        isShowMode = false;
    }

    public void enableShowMode() {
        isShowMode = true;
        isEditMode = false;

        // Enable or disable autorefresh on show mode.
        if (showAutoRefresh) setRefreshEnabled(true);
        else setRefreshEnabled(false);
    }

    public String getSerializedProperties() {
        return serializedProperties;
    }

    public void setSerializedProperties(String serializedProperties) {
        this.serializedProperties = serializedProperties;
    }

    public synchronized DashboardFilterProperty getDashboardFilterPropertyForCurrentFilter(String dataProviderCode, String propertyId) {
        for (DashboardFilterProperty dashboardFilterProperty : properties) {
            if (dataProviderCode.equals(dashboardFilterProperty.getDataProviderCode()) &&
                propertyId.equals(dashboardFilterProperty.getPropertyId())) {
                return dashboardFilterProperty;
            }
        }
        return null;
    }

    public synchronized DashboardFilterProperty getDashboardFilterProperty(String propertyId) {
        for (DashboardFilterProperty dashboardFilterProperty : properties) {
            if (propertyId.equals(dashboardFilterProperty.getPropertyId())) {
                return dashboardFilterProperty;
            }
        }
        return null;
    }

    // Calls dashboard filter to get static  properties but keep properties instance configuration. Is propety is found on this instance properties List thsi instance is returned.
    public DashboardFilterProperty[] getStaticPropertiesForCurrentFilter() {
        DashboardFilterProperty[] staticProperties = getFilter().getStaticProperties();
        if (staticProperties == null) return null;
        DashboardFilterProperty[] results = new DashboardFilterProperty[staticProperties.length];
        for (int i = 0; i < staticProperties.length; i++) {
            DashboardFilterProperty staticProperty = staticProperties[i];
            DashboardFilterProperty property = getDashboardFilterPropertyForCurrentFilter(staticProperty.getDataProviderCode(),staticProperty.getPropertyId());
            if (property != null) results[i]=property;
            else results[i] = staticProperty;
        }
        return results;
    }

    public DashboardFilterProperty[] getAllPropertiesForCurrentFilter() {
        List results = new ArrayList();
        try {
            // Static properties.
            DashboardFilterProperty[] staticProps = getStaticPropertiesForCurrentFilter();
            if (staticProps != null) results.addAll(Arrays.asList(staticProps));

            // Dynamic properties.
            Iterator it = getDashboard().getDataProviders().iterator();
            while (it.hasNext()) {
                DataProvider dataProvider = (DataProvider) it.next();
                DataProperty[] allProperties = dataProvider.getDataSet().getProperties();
                for (int i = 0; i < allProperties.length; i++) {
                    DataProperty property = allProperties[i];
                    DashboardFilterProperty prop = getDashboardFilterPropertyForCurrentFilter(dataProvider.getCode(), property.getPropertyId());
                    if (prop == null) prop = new DashboardFilterProperty(dataProvider.getCode(), property.getPropertyId(), getFilter() ,null, false);
                    results.add(prop);
                }
            }
        } catch (Exception e) {
            log.error("Cannot get data provider results.", e);
        }
        return (DashboardFilterProperty[]) results.toArray(new DashboardFilterProperty[results.size()]);
    }

    public synchronized DashboardFilterProperty[] getBeingFilteredProperties() {
        List results = new ArrayList();
        for (DashboardFilterProperty dashboardFilterProperty : properties) {
            if (dashboardFilterProperty.isBeingFiltered()) {
                results.add(dashboardFilterProperty);
            }
        }

        return (DashboardFilterProperty[]) results.toArray(new DashboardFilterProperty[results.size()]);
    }

    public synchronized List<DashboardFilterProperty> getVisibleProperties() {
        List<DashboardFilterProperty> results = new ArrayList<DashboardFilterProperty>();
        for (DashboardFilterProperty dashboardFilterProperty : properties) {
            if (dashboardFilterProperty.isVisible()) {
                results.add(dashboardFilterProperty);
            }
        }
        return results;
    }

    public boolean hasError(String propertyId) {
        return filterPropertyErrors.contains(propertyId);
    }

    // --------------- END HANDLING METHODS ------------------------ //

    // --------------- START ACTIONS ------------------------------- //
    
    public synchronized void actionStore(CommandRequest request) {
        Map parameters = request.getRequestObject().getParameterMap();

        // Initialize parameters and properties to default.
        showPropertyNames = false;
        showRefreshButton = false;
        showApplyButton = false;
        showClearButton = false;
        showSubmitOnChange = false;
        isShortMode = false;
        showLegend = false;
        showAutoRefresh = false;
        properties.clear();
        notAllowedProperties.clear();

        // Component options.
        if (parameters.containsKey(PARAM_SHOW_REFRESH_BUTTON)) showRefreshButton = true;
        if (parameters.containsKey(PARAM_SHOW_PROPERTY_NAMES)) showPropertyNames = true;
        if (parameters.containsKey(PARAM_SHOW_CLEAR_BUTTON)) showClearButton = true;
        if (parameters.containsKey(PARAM_SHOW_APPLY_BUTTON)) showApplyButton = true;
        if (parameters.containsKey(PARAM_SHOW_SUBMIT_ON_CHANGE)) showSubmitOnChange = true;
        if (parameters.containsKey(PARAM_SHORT_MODE)) isShortMode = true;
        if (parameters.containsKey(PARAM_SHOW_LEGEND)) showLegend = true;
        if (parameters.containsKey(PARAM_SHOW_AUTO_REFRESH)) showAutoRefresh = true;

        // Component properties.
        DashboardFilterProperty[] allProperties = getAllPropertiesForCurrentFilter();
        for (int i = 0; i < allProperties.length; i++) {
            DashboardFilterProperty property = allProperties[i];
            String dataProviderCode = property.getDataProviderCode();
            String propertyId = property.getPropertyId();

            String visibleParamKey = new StringBuffer().append(PARAM_VISIBLE).append("/").append(dataProviderCode).append("/").append(propertyId).toString();
            String drillDownParamKey = new StringBuffer().append(PARAM_SECTION).append("/").append(dataProviderCode).append("/").append(propertyId).toString();
            boolean isVisible = parameters.containsKey(visibleParamKey);
            Long sectionId = null;
            if (parameters.containsKey(drillDownParamKey)) {
                String sectionIdStr = ((String[]) parameters.get(drillDownParamKey))[0];
                if (!PARAM_DRILLDOWN_DISABLED.equals(sectionIdStr)) sectionId = Long.decode(sectionIdStr);
            }

            if (!isVisible && sectionId == null) continue;

            // Property must be added?
            DashboardFilterProperty prop = getDashboardFilterPropertyForCurrentFilter(dataProviderCode, propertyId);
            if (prop == null) prop = new DashboardFilterProperty(dataProviderCode, propertyId, getFilter() ,null, false);

            // Check if another property with same identifier.
            if (getDashboardFilterProperty(propertyId) != null) {
                // Another property with same id is already set to the filter.
                // Filter cannot use two properties with same property id., so show warning.
                notAllowedProperties.add(prop);
                continue;
            }

            // Add property to this component.
            properties.add(prop);

            // Set property parameters
            prop.setBeingFiltered(false);
            prop.setVisible(isVisible);
            prop.setSectionId(sectionId);
        }
    }

    public synchronized CommandResponse actionFilter(CommandRequest request) throws Exception {
        Dashboard currentDashboard = getDashboard();
        DashboardFilter filterRequest = parseFilterRequest(request);
        filterPropertyErrors.clear();

        // On drill-down, refresh the whole screen
        if (currentDashboard.filter(filterRequest)) {
            return new ShowCurrentScreenResponse();
        }
        // Refresh only the filter panel and the affected dashboard panels
        return null;
    }

    public DashboardFilter parseFilterRequest(CommandRequest request) throws Exception {
        DashboardFilter filterRequest = new DashboardFilter();
        for (DashboardFilterProperty dashboardFilterProperty : properties) {

            // Is property already in the dashboard filter?. Then is not possible to filter by this property, it's already added to dashboard filter.
            if (dashboardFilterProperty.isBeingFiltered()) {
                continue;
            }
            if (!dashboardFilterProperty.isPropertyAlive()){
                log.warn("Trying to filter by " + dashboardFilterProperty.getPropertyId() + ". This property is not in any dataset.");
                continue;
            }
            if (!dashboardFilterProperty.isVisible()) {
                continue;
            }

            Object[] result;
            try {
                result = requestProcessor.parseDashboardProperty(request.getRequestObject(), dashboardFilterProperty);
            } catch (Exception e) {
                log.error("Error parsing property " + dashboardFilterProperty.getPropertyId() + ".", e);
                continue;
            }

            if (result.length != 3) {
                log.error("Error parsing property: '" + dashboardFilterProperty.getPropertyId() + "' for dataProvider: '"
                        + dashboardFilterProperty.getDataProviderCode() + "'");
                continue;
            }

            Collection allowedValues = (Collection) result[0];
            Object minValue = result[1];
            Object maxValue = result[2];
            if (allowedValues != null || minValue != null || maxValue != null) {
                filterRequest.addProperty(dashboardFilterProperty.getPropertyId(),
                        minValue, true,
                        maxValue, true,
                        allowedValues, FilterByCriteria.ALLOW_ANY);
            }
        }
        return filterRequest;
    }

    public void actionRefresh(CommandRequest request) throws Exception {
        String timeOutValue = request.getRequestObject().getParameter("refreshTimeOut");
        if (!StringUtils.isBlank(timeOutValue)) {
            try {
                autoRefreshTimeout = Integer.decode(timeOutValue).intValue();
            } catch (NumberFormatException e) {
                log.warn("Cannot parse auto refresh value as a number.");
            }
        }
        getDashboard().refresh();
    }

    public CommandResponse actionClear(CommandRequest request) throws Exception {
        Dashboard dashboard = getDashboard();
        if (dashboard.unfilter()) {
            return new ShowCurrentScreenResponse();
        }
        return null;
    }

    public CommandResponse actionDeleteFilteredProperty(CommandRequest request) throws Exception {
        String propertyToDelete = request.getRequestObject().getParameter("filteredPropertyToDelete");
        if (propertyToDelete == null || propertyToDelete.trim().length() == 0) return null;

        Dashboard dashboard = getDashboard();
        if (dashboard.unfilter(propertyToDelete)) {
            return new ShowCurrentScreenResponse();
        }
        return null;
    }

    public void actionPlay(CommandRequest request) {
        setRefreshEnabled(true);
    }

    public void actionStop(CommandRequest request) {
        setRefreshEnabled(false);
    }

    // --------- END ACTIONS ---------------------------------- //

    // --------- SERIALIZATION / DESERIALIZATION -------------- //

    public synchronized String serializeComponentData() throws Exception {
        // Serialize visible properties and options.
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        int indent = 0;
        printIndent(out, indent);
        out.println("<dashboard_filter>");
        for (DashboardFilterProperty dashboardFilterProperty : properties) {
            printIndent(out, indent+1);
            out.println("<property id=\"" + StringEscapeUtils.escapeXml(dashboardFilterProperty.getPropertyId()) + "\" providerCode =\"" +
            StringEscapeUtils.escapeXml(dashboardFilterProperty.getDataProviderCode())+ "\">");
            printIndent(out, indent+2);
            out.println("<visible>"+dashboardFilterProperty.isVisible()+"</visible>");
            if (dashboardFilterProperty.getSectionId() != null) {
                printIndent(out, indent+2);
                out.println("<section>"+dashboardFilterProperty.getSectionId()+"</section>");
            }
            printIndent(out,indent+1);
            out.println("</property>");
        }

        // Serialize options.
        printIndent(out,indent+1);
        out.println("<options>");
        printIndent(out,indent+2);
        out.println("<shortViewMode>" + isShortMode + "</shortViewMode>");
        printIndent(out,indent+2);
        out.println("<showLegend>"+showLegend+"</showLegend>");
        printIndent(out,indent+2);
        out.println("<showRefreshButton>" + showRefreshButton + "</showRefreshButton>");
        printIndent(out,indent+2);
        out.println("<showApplyhButton>" + showApplyButton + "</showApplyhButton>");
        printIndent(out,indent+2);
        out.println("<showClearButton>" + showClearButton + "</showClearButton>");
        printIndent(out,indent+2);
        out.println("<showPropertyNames>"+showPropertyNames+"</showPropertyNames>");
        printIndent(out,indent+2);
        out.println("<showSubmitOnChange>"+showSubmitOnChange+"</showSubmitOnChange>");
        printIndent(out,indent+1);
        out.println("<showAutoRefresh>"+showAutoRefresh+"</showAutoRefresh>");
        printIndent(out,indent+1);
        out.println("</options>");
        printIndent(out,indent);

        out.println("</dashboard_filter>");
        serializedProperties = sw.toString();
        return sw.toString();
    }

    protected  void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    // Return if is needed to serialize and save properties after this call because properties that does not exist on current filter or data providers must be deleted from persistence.
    // return: must clear serialized trash properties after deserialize process saving this data.
    public synchronized boolean deserializeComponentData(String serializedData) throws Exception {
        // Load options and visible properties
        if (serializedData == null || serializedData.trim().length() == 0) {
            log.info("No data to deserialize.");
            return false;
        }

        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(serializedData)));
        Document doc = parser.getDocument();
        NodeList nodes = doc.getElementsByTagName("dashboard_filter");
        if (nodes.getLength() > 1) {
            log.error("Each dashboard filter component just can parse one <dashboard_filter>");
            return false;
        }
        if (nodes.getLength() == 0) {
            log.info("No data to deserialize.");
            return false;
        }

        boolean needsToSerializeAfter = false;
        serializedProperties = serializedData;
        Node rootNode = nodes.item(0);
        nodes = rootNode.getChildNodes();
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equals("property")) {
                // Parse visible properties.
                String dataProviderCode = node.getAttributes().getNamedItem("providerCode").getNodeValue();
                String propertyId = node.getAttributes().getNamedItem("id").getNodeValue();
                String sectionId = null;
                boolean visible = false;
                NodeList subnodes = node.getChildNodes();
                for (int i = 0; i < subnodes.getLength(); i++) {
                    Node subnode = subnodes.item(i);
                    if (subnode.getNodeName().equals("section")) {
                        sectionId = subnode.getFirstChild().getNodeValue();
                    }
                    if (subnode.getNodeName().equals("visible")) {
                        visible = Boolean.valueOf(subnode.getFirstChild().getNodeValue()).booleanValue();
                    }
                }
                Long lSectionId = sectionId != null ? Long.decode(sectionId) : null;
                DashboardFilterProperty filterProp = new DashboardFilterProperty(dataProviderCode, propertyId, getFilter(), lSectionId, true);
                filterProp.setVisible(visible);
                if (filterProp.isPropertyAlive()) properties.add(filterProp);
                else needsToSerializeAfter = true;
            } else if (node.getNodeName().equals("options")) {
                // Parse component options.
                NodeList options = node.getChildNodes();
                String showRefreshButton = null;
                String showPropertyNames = null;
                String showClearButton = null;
                String showApplyButton = null;
                String showSubmitOnChange = null;
                String showShortViewMode = null;
                String showLegend = null;
                String showAutoRefresh = null;
                for (int i = 0; i < options.getLength(); i++) {
                    Node option = options.item(i);
                    if (option.getNodeName().equals("showRefreshButton")) showRefreshButton = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showPropertyNames")) showPropertyNames = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showClearButton")) showClearButton = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showApplyhButton")) showApplyButton = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showSubmitOnChange")) showSubmitOnChange = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("shortViewMode")) showShortViewMode = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showLegend")) showLegend = option.getFirstChild().getNodeValue();
                    if (option.getNodeName().equals("showAutoRefresh")) showAutoRefresh = option.getFirstChild().getNodeValue();
                }
                this.showPropertyNames = Boolean.valueOf(showPropertyNames).booleanValue();
                this.showRefreshButton = Boolean.valueOf(showRefreshButton).booleanValue();
                this.showApplyButton = Boolean.valueOf(showApplyButton).booleanValue();
                this.showClearButton = Boolean.valueOf(showClearButton).booleanValue();
                this.showSubmitOnChange= Boolean.valueOf(showSubmitOnChange).booleanValue();
                this.isShortMode = Boolean.valueOf(showShortViewMode).booleanValue();
                this.showLegend = Boolean.valueOf(showLegend).booleanValue();
                this.showAutoRefresh = Boolean.valueOf(showAutoRefresh).booleanValue();

                // Enable auto-refresh if necessary on start.
                if (this.showAutoRefresh) setRefreshEnabled(true);
            }
        }
        return needsToSerializeAfter;
    }

    public synchronized void beforeRenderBean() {
        super.beforeRenderBean();

        // Get the filter.
        DashboardFilter filter = getFilter();

        // Check all visible properties exist.
        Iterator props = properties.iterator();
        while (props.hasNext()) {
            DashboardFilterProperty dashboardFilterProperty = (DashboardFilterProperty) props.next();
            if (!dashboardFilterProperty.isPropertyAlive()) props.remove();
        }

        // Check if filtered properties for this filter component are already in dashboard filter.
        DashboardFilterProperty[] beingFilteredProps = getBeingFilteredProperties();
        for (int i = 0; i < beingFilteredProps.length; i++) {
            List dfProperties = Arrays.asList(filter.getPropertyIds());
            DashboardFilterProperty beingFilteredProp = beingFilteredProps[i];
            if (!dfProperties.contains(beingFilteredProp.getPropertyId())) beingFilteredProp.setBeingFiltered(false);
        }

        // Check filtered properties and hide from available filter properties (set property not visible)
        String[] propIds = filter.getPropertyIds();
        for (int i = 0; i < propIds.length; i++) {
            String propId = propIds[i];
            DashboardFilterProperty prop = getDashboardFilterProperty(propId);
            if (prop == null) {
                DashboardFilterProperty parentProperty = filter.getPropertyInParentDashboards(propId);
                if (parentProperty !=null) {
                    prop = new DashboardFilterProperty(parentProperty.getDataProviderCode(), propId, getFilter(), null, true);
                    properties.add(prop);
                }
            } else {
                prop.setBeingFiltered(true);
            }
        }
    }
}
