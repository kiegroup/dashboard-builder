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
package org.jboss.dashboard.displayer.chart;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.AbstractDataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.domain.RangeConfiguration;
import org.jboss.dashboard.function.CountFunction;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Base class for the implementation of chart-like data displayers.
 */
public abstract class AbstractChartDisplayer extends AbstractDataDisplayer {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(AbstractChartDisplayer.class);

    /**
     * The default unit value pattern.
     */
    public static final String UNIT_VALUE_TAG = "{value}";

    protected transient DataProperty domainProperty;
    protected transient DataProperty rangeProperty;
    protected transient ScalarFunction rangeScalarFunction;
    protected transient Map<Locale, String> unitI18nMap;
    protected transient DomainConfiguration domainConfig;
    protected transient RangeConfiguration rangeConfig;

    public static final int INTERVALS_SORT_CRITERIA_LABEL = 0;
    public static final int INTERVALS_SORT_CRITERIA_VALUE = 1;
    public static final int INTERVALS_SORT_ORDER_NONE = 0;
    public static final int INTERVALS_SORT_ORDER_ASC = 1;
    public static final int INTERVALS_SORT_ORDER_DESC = -1;

    protected String type;
    protected String color;
    protected String backgroundColor;
    protected int width;
    protected int height;
    protected boolean showLegend;
    protected boolean axisInteger;
    protected String legendAnchor;
    protected boolean showTitle;
    protected String title;
    protected String graphicAlign;
    protected int intervalsSortCriteria;
    protected int intervalsSortOrder;
    protected int marginLeft;
    protected int marginRight;
    protected int marginBottom;
    protected int marginTop;

    /** The flag indicating if the X-aAxis labels should be displayed. */
    protected boolean showLabelsXAxis;

    // Constructor of the class

    public AbstractChartDisplayer() {
        domainProperty = null;
        rangeProperty = null;
        domainConfig = null;
        rangeConfig = null;
        rangeScalarFunction = null;
        unitI18nMap = new HashMap<Locale, String>();
        color = "#FFFFFF";
        backgroundColor = "#FFFFFF";
        width = 600;
        height = 300;
        showLegend = false;
        axisInteger = false;
        legendAnchor = "south";
        showTitle = false;
        title = null;
        graphicAlign = "center";
        intervalsSortCriteria = INTERVALS_SORT_CRITERIA_LABEL;
        intervalsSortOrder = INTERVALS_SORT_ORDER_NONE;
        marginLeft=30;
        marginRight=30;
        marginTop=30;
        marginBottom=30;
    }

    public void setDataProvider(DataProvider dp) throws DataDisplayerInvalidConfiguration {

        // If the provider changes then reset the current configuration.
        if (dataProvider != null && !dataProvider.equals(dp)) {
            setDomainProperty(null);
            setRangeProperty(null);
        }

        // If data provider definition does not match with displayer configuration, do not set the provider
        validate(dp);
        dataProvider = dp;
    }

    /**
     * Get the list of properties valid as domain.
     */
    public DataProperty[] getDomainPropertiesAvailable() {
        List<DataProperty> dpList = new ArrayList<DataProperty>();
        try {
            DataProperty[] props = dataProvider.getDataSet().getProperties();
            dpList.addAll(Arrays.asList(props));
        } catch (Exception e) {
            log.error("Can not retrieve dataset properties.", e);
        }
        // Build the data property array
        return dpList.toArray(new DataProperty[dpList.size()]);
    }

    /**
     * Get the list of properties valid as range.
     */
    public DataProperty[] getRangePropertiesAvailable() {
        List<DataProperty> dpList = new ArrayList<DataProperty>();
        try {
            DataProperty[] props = dataProvider.getDataSet().getProperties();
            dpList.addAll(Arrays.asList(props));
        } catch (Exception e) {
            log.error("Can not retrieve dataset properties.", e);
        }
        // Build the data property array
        return dpList.toArray(new DataProperty[dpList.size()]);
    }

    public boolean hasDataSetChanged(DataProperty property) {
        try {
            DataSet ds1 = dataProvider.getDataSet();
            DataSet ds2 = property.getDataSet();
            return (ds1 != ds2 || ds1.getRowCount() != ds2.getRowCount());
        } catch (Exception e) {
            log.error("Error getting data set.", e);
        }
        return false;
    }

    /**
     * Check if data provider definition (all properties) match with the serialized in the current displayer.
     * @param provider The data provider.
     * @throws org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration Current displayer configuration is invalid.
     */
    @Override
    public void validate(DataProvider provider) throws DataDisplayerInvalidConfiguration {
        if (provider != null) {
            boolean hasDomainPropChanged = false;
            boolean hasRangePropChanged = false;
            try {
                String domainPropertyId = (domainProperty != null) ? domainProperty.getPropertyId() : (domainConfig != null) ? domainConfig.getPropertyId() : null;
                hasDomainPropChanged = hasProviderPropertiesChanged(domainPropertyId, provider);
                String rangePropertyId = (rangeProperty != null) ? rangeProperty.getPropertyId() : (rangeConfig != null) ? rangeConfig.getPropertyId() : null;
                hasRangePropChanged = hasProviderPropertiesChanged(rangePropertyId, provider);
            } catch (Exception e) {
                throw new DataDisplayerInvalidConfiguration("Error during displayer initialization.", e);
            }
            if (hasDomainPropChanged && domainConfig != null) throw new DataDisplayerInvalidConfiguration("The current chart displayer domain property [" + domainConfig.getPropertyId() + "] is no longer available in data provider with code [" + provider.getCode() + "].");
            if (hasRangePropChanged && rangeConfig != null) throw new DataDisplayerInvalidConfiguration("The current chart displayer range property [" + rangeConfig.getPropertyId() + "] is no longer available in data provider with code [" + provider.getCode() + "].");
        }
    }

    /**
     * Check if a data provider property match with the serialized in the displayer.
     * @param propertyId The data property identifier of this displayer to check against data provider properties.
     * @param dataProvider The current data provider definition.
     * @return If the data displayer property exists in current data provider.
     */
    public boolean hasProviderPropertiesChanged(String propertyId, DataProvider dataProvider) throws Exception{
        if (propertyId == null) return false;
        
        DataSet dataSet = dataProvider.getDataSet();
        DataProperty[] datasetProperties = dataSet.getProperties();
        if (datasetProperties != null && datasetProperties.length > 0) {
            for (DataProperty datasetProperty : datasetProperties) {
                String datasetPropertyId = datasetProperty.getPropertyId();
                if (datasetPropertyId.equals(propertyId)) return false;
            }
        }
        return true;
    }
    
    /**
     * Get the property selected as the domain.
     */
    public DataProperty getDomainProperty() {
        try {
            // Get the domain property. Be aware of both property removal and data set refresh.
            DataSet dataSet = dataProvider.getDataSet();
            if (domainProperty == null || hasDataSetChanged(domainProperty)) {

                // If a domain is currently configured the try to get the property form that.
                if (domainConfig != null) domainProperty = dataSet.getPropertyById(domainConfig.getPropertyId());

                // If the property has been removed for any reason then reset the domain.
                if (domainProperty == null && domainConfig != null) domainConfig = null;
                if (domainProperty == null) domainProperty = getDomainPropertiesAvailable()[0];

                // Create a copy of the domain property to avoid changes to the original data set.
                domainProperty = domainProperty.cloneProperty();

                // If a domain config exists then apply it to the domain.
                if (domainConfig != null) domainConfig.apply(domainProperty);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return domainProperty;
    }

    public void setDomainProperty(DataProperty property) {
        domainProperty = property;
        if (domainProperty == null) domainConfig = null;
    }

    /**
     * Get the property selected as the range.
     */
    public DataProperty getRangeProperty() {
        try {
            // Get the range property. Be aware of both property removal and data set refresh.
            DataSet dataSet = dataProvider.getDataSet();
            if (rangeProperty == null || hasDataSetChanged(rangeProperty)) {

                // If a range is currently configured then try to get the property from that.
                if (rangeConfig != null) rangeProperty = dataSet.getPropertyById(rangeConfig.getPropertyId());

                // If the property has been removed for any reason then reset the range.
                if (rangeProperty == null && rangeConfig != null) rangeConfig = null;
                if (rangeProperty == null) rangeProperty = getRangePropertiesAvailable()[0];

                // Create a copy of the property to avoid changes to the original data set.
                rangeProperty = rangeProperty.cloneProperty();

                // If a range config exists then apply it to the range.
                if (rangeConfig != null) {
                    rangeConfig.apply(rangeProperty);
                    rangeScalarFunction = DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(rangeConfig.getScalarFunctionCode());
                    unitI18nMap = new HashMap<Locale, String>(rangeConfig.getUnitI18nMap());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rangeProperty;
    }

    public void setRangeProperty(DataProperty property) {
        rangeProperty = property;
        rangeScalarFunction = new CountFunction();
        if (rangeProperty == null) rangeConfig = null;
    }

    public ScalarFunction getRangeScalarFunction() {
        if (rangeScalarFunction != null) return rangeScalarFunction;
        return rangeScalarFunction = new CountFunction();
    }

    public void setRangeScalarFunction(ScalarFunction rangeScalarFunction) {
        this.rangeScalarFunction = rangeScalarFunction;
    }

    public Map<Locale, String> getUnitI18nMap() {
        return unitI18nMap;
    }

    public void setUnitI18nMap(Map<Locale, String> unitI18nMap) {
        this.unitI18nMap.clear();
        this.unitI18nMap.putAll(unitI18nMap);
    }

    public String getUnit(Locale l) {
        Object result = LocaleManager.lookup().localize(unitI18nMap);
        if (result == null) result = UNIT_VALUE_TAG;
        return (String) result;
    }

    public void setUnit(String unit, Locale l) {
        unitI18nMap.put(l, unit);
    }

    public String getType() {
        List<String> types = getDataDisplayerRenderer().getAvailableChartTypes(this);
        if (StringUtils.isBlank(type) || !types.contains(type)) {
            type = getDataDisplayerRenderer().getDefaultChartType(this);
        }
        return type;
    }

    public void setType(String type) {
        List<String> types = getDataDisplayerRenderer().getAvailableChartTypes(this);
        if (types.contains(type)) {
            this.type = type;
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public boolean isAxisInteger() {
        return axisInteger;
    }

    public void setAxisInteger(boolean axisInteger) {
        this.axisInteger = axisInteger;
    }

    public String getLegendAnchor() {
        return legendAnchor;
    }

    public void setLegendAnchor(String legendAnchor) {
        this.legendAnchor = legendAnchor;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGraphicAlign() {
        return graphicAlign;
    }

    public void setGraphicAlign(String graphicAlign) {
        this.graphicAlign = graphicAlign;
    }

    public int getIntervalsSortCriteria() {
        return intervalsSortCriteria;
    }

    public void setIntervalsSortCriteria(int intervalsSortCriteria) {
        this.intervalsSortCriteria = intervalsSortCriteria;
    }

    public int getIntervalsSortOrder() {
        return intervalsSortOrder;
    }

    public void setIntervalsSortOrder(int intervalsSortOrder) {
        this.intervalsSortOrder = intervalsSortOrder;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public boolean isShowLabelsXAxis() {
        return showLabelsXAxis;
    }

    public void setShowLabelsXAxis(boolean showLabelsXAxis) {
        this.showLabelsXAxis = showLabelsXAxis;
    }

    public DataSet buildXYDataSet() {
        DataProperty domainProperty = getDomainProperty();
        DataProperty rangeProperty = getRangeProperty();
        ScalarFunction scalarFunction = getRangeScalarFunction();
        DataSet sourceDataSet = domainProperty.getDataSet();
        CodeBlockTrace trace = new BuildXYDataSetTrace(domainProperty, rangeProperty, scalarFunction).begin();
        try {
            if (domainProperty == null || domainProperty.getDomain() == null) return null;
            if (rangeProperty == null || scalarFunction == null) return null;

            // Group the original data set by the domain property.
            int pivot = sourceDataSet.getPropertyColumn(domainProperty);
            int range = sourceDataSet.getPropertyColumn(rangeProperty);
            int[] columns = new int[] {pivot, range};
            String[] functionCodes = new String[] {CountFunction.CODE, scalarFunction.getCode()};
            return sourceDataSet.groupBy(domainProperty, columns, functionCodes, intervalsSortCriteria, intervalsSortOrder);
        } finally {
            trace.end();
        }
    }

    public void copyFrom(DataDisplayer sourceDisplayer) throws DataDisplayerInvalidConfiguration {
        try {
            super.copyFrom(sourceDisplayer);

            AbstractChartDisplayer source = (AbstractChartDisplayer) sourceDisplayer;
            setBackgroundColor(source.getBackgroundColor());
            setColor(source.getColor());
            setDomainConfiguration(source.domainConfig);
            setDomainProperty(source.getDomainProperty());
            setGraphicAlign(source.getGraphicAlign());
            setHeight(source.getHeight());
            setLegendAnchor(source.getLegendAnchor());
            setRangeConfiguration(source.rangeConfig);
            setRangeProperty(source.getRangeProperty());
            setRangeScalarFunction(source.getRangeScalarFunction());
            setMarginBottom(source.getMarginBottom());
            setMarginTop(source.getMarginTop());
            setMarginLeft(source.getMarginLeft());
            setMarginRight(source.getMarginRight());
            setTitle(source.getTitle());
            setWidth(source.getWidth());
            setAxisInteger(source.isAxisInteger());
            setShowLegend(source.isShowLegend());
            setShowTitle(source.isShowTitle());
            setIntervalsSortCriteria(source.getIntervalsSortCriteria());
            setIntervalsSortOrder(source.getIntervalsSortOrder());
            setShowLabelsXAxis(source.isShowLabelsXAxis());
        } catch (ClassCastException e) {
            // Ignore wrong types
        }
    }

    public void setDomainConfiguration(DomainConfiguration config) {
        domainConfig = config;
    }

    public void setRangeConfiguration(RangeConfiguration config) {
        rangeConfig = config;
    }

    public DomainConfiguration getDomainConfiguration() {
        return domainConfig;
    }

    public RangeConfiguration getRangeConfiguration() {
        return rangeConfig;
    }

    class BuildXYDataSetTrace extends CodeBlockTrace {

        protected String displayerTitle;
        protected String providerCode;
        protected String scalarFunctionCode;
        protected String domainPropId;
        protected String rangePropId;

        public BuildXYDataSetTrace(DataProperty domainProperty, DataProperty rangeProperty, ScalarFunction scalarFunction) {
            super(null);
            displayerTitle = getTitle();
            DataProvider dataProvider = domainProperty.getDataSet().getDataProvider();
            providerCode = dataProvider.getCode();
            scalarFunctionCode = scalarFunction.getCode();
            domainPropId = domainProperty.getPropertyId();
            rangePropId = rangeProperty.getPropertyId();
            setId(providerCode + "-" + scalarFunctionCode + "-" + rangePropId + "-" + domainPropId);
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.DATASET_BUILD;
        }

        public String getDescription() {
            return domainPropId + " / " + scalarFunctionCode + "(" + rangePropId + ")";
        }

        public Map<String, Object> getContext() {
            Map<String, Object> ctx = new HashMap<String, Object>();
            ctx.put("Chart title", displayerTitle);
            ctx.put("Provider code", providerCode);
            ctx.put("Domain property", domainPropId);
            ctx.put("Range property", rangePropId);
            ctx.put("Scalar function", scalarFunctionCode);
            return ctx;
        }
    }
}
