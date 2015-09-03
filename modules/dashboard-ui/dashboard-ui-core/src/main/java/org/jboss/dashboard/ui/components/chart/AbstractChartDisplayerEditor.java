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
package org.jboss.dashboard.ui.components.chart;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.domain.DomainConfigurationParser;
import org.jboss.dashboard.domain.RangeConfigurationParser;
import org.jboss.dashboard.ui.components.DataDisplayerEditor;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.domain.RangeConfiguration;
import org.jboss.dashboard.dataset.DataSet;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.ui.controller.CommandResponse;

/**
 * Base class for Chart displayer editors.
 */
public abstract class AbstractChartDisplayerEditor extends DataDisplayerEditor {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractChartDisplayerEditor.class);

    public static final String I18N_PREFFIX = "abstractChartDisplayer.";
    public static final String DOMAIN_SAVE_BUTTON_PRESSED = "updateDomainDetails";
    public static final String RANGE_SAVE_BUTTON_PRESSED = "updateRangeDetails";
    public static final String RANGE2_SAVE_BUTTON_PRESSED = "updateRange2Details";

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        AbstractChartDisplayer displayer = (AbstractChartDisplayer) getDataDisplayer();
        if (!displayer.getDataProvider().isReady()) return null;
        DataSet ds = displayer.getDataProvider().getDataSet();

        // Renderer
        String rendererUid = request.getRequestObject().getParameter("rendererUid");
        DataDisplayerRenderer renderer = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerRendererByUid(rendererUid);
        if (renderer == null) return null;
        displayer.setDataDisplayerRenderer(renderer);

        // New domain and range properties.
        String idDomainDetails = request.getRequestObject().getParameter("idDomainDetails");
        if (idDomainDetails != null) {

            // If the domain property has been changed, load it.
            DataProperty domainProperty = displayer.getDomainProperty();
            if (!idDomainDetails.equals(domainProperty.getPropertyId())) displayer.setDomainProperty(ds.getPropertyById(idDomainDetails));

            // If domain save button has been pressed, update its configuration parameters
            // TODO: Also save if the enter key has been pressed.
            String domainSaveButtonPressed = request.getRequestObject().getParameter(DOMAIN_SAVE_BUTTON_PRESSED);
            boolean updateDomainDetails =  (domainSaveButtonPressed != null) && Boolean.valueOf(domainSaveButtonPressed).booleanValue();
            if (updateDomainDetails) {
                DomainConfiguration domainConfig = displayer.getDomainConfiguration();
                DomainConfigurationParser parser = new DomainConfigurationParser(domainConfig);
                parser.parse(request);
                domainConfig.apply(displayer.getDomainProperty());
            }
        }

        String idRangeDetails = request.getRequestObject().getParameter("idRangeDetails");
        if (idRangeDetails != null) {

            // If the range property has been changed, load it.
            DataProperty rangeProperty = displayer.getRangeProperty();
            if (!idRangeDetails.equals(rangeProperty.getPropertyId())) displayer.setRangeProperty(ds.getPropertyById(idRangeDetails));

            // If range save button has been pressed, update its configuration parameters.
            String rangeSaveButtonPressed = request.getRequestObject().getParameter(RANGE_SAVE_BUTTON_PRESSED);
            boolean updateRangeDetails =  (rangeSaveButtonPressed != null) && Boolean.valueOf(rangeSaveButtonPressed).booleanValue();
            // TODO: Also save if the enter key has been pressed.
            if (updateRangeDetails) {
                RangeConfiguration rangeConfig = displayer.getRangeConfiguration();
                RangeConfigurationParser parser = new RangeConfigurationParser(rangeConfig);
                parser.parse(request);
                rangeConfig.apply(displayer.getRangeProperty());
                displayer.setRangeScalarFunction(DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(rangeConfig.getScalarFunctionCode()));
                displayer.setUnitI18nMap(rangeConfig.getUnitI18nMap());
            }
        }
		
		String idRange2Details = request.getRequestObject().getParameter("idRange2Details");
        if (idRange2Details != null) {

            // If the range2 property has been changed, load it.
            DataProperty range2Property = displayer.getRange2Property();
            if (!idRange2Details.equals(range2Property.getPropertyId())) displayer.setRange2Property(ds.getPropertyById(idRange2Details));

            // If range2 save button has been pressed, update its configuration parameters.
            String range2SaveButtonPressed = request.getRequestObject().getParameter(RANGE2_SAVE_BUTTON_PRESSED);
            boolean updateRange2Details =  (range2SaveButtonPressed != null) && Boolean.valueOf(range2SaveButtonPressed).booleanValue();
            // TODO: Also save if the enter key has been pressed.
            if (updateRange2Details) {
                RangeConfiguration range2Config = displayer.getRange2Configuration();
                RangeConfigurationParser parser = new RangeConfigurationParser(range2Config);
                parser.parse2(request);
                range2Config.apply(displayer.getRange2Property());
                displayer.setRange2ScalarFunction(DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(range2Config.getScalarFunctionCode()));
                displayer.setUnitI18nMap(range2Config.getUnitI18nMap());
            }
        }
		
		//StartDate
		String idStartDateDetails = request.getRequestObject().getParameter("idStartDateDetails");
        if (idStartDateDetails != null) {            
            DataProperty startDateProperty = displayer.getStartDateProperty();
            if (!idStartDateDetails.equals(startDateProperty.getPropertyId())) displayer.setStartDateProperty(ds.getPropertyById(idStartDateDetails));
        }
		
		//EndDate
		String idEndDateDetails = request.getRequestObject().getParameter("idEndDateDetails");
        if (idEndDateDetails != null) {            
            DataProperty endDateProperty = displayer.getEndDateProperty();
            if (!idEndDateDetails.equals(endDateProperty.getPropertyId())) displayer.setEndDateProperty(ds.getPropertyById(idEndDateDetails));
        }
		
		//Size
		String idSizeDetails = request.getRequestObject().getParameter("idSizeDetails");
        if (idSizeDetails != null) {            
            DataProperty sizeProperty = displayer.getSizeProperty();
            if (!idSizeDetails.equals(sizeProperty.getPropertyId())) displayer.setSizeProperty(ds.getPropertyById(idSizeDetails));
        }
		
		//Done
		String idDoneDetails = request.getRequestObject().getParameter("idDoneDetails");
        if (idDoneDetails != null) {            
            DataProperty doneProperty = displayer.getDoneProperty();
            if (!idDoneDetails.equals(doneProperty.getPropertyId())) displayer.setDoneProperty(ds.getPropertyById(idDoneDetails));
        }
		

        // Retrieve other configuration parameters and set the new properties to the displayer.
        String chartType = request.getRequestObject().getParameter("chartType");
        if (chartType != null && !"".equals(chartType)) displayer.setType(chartType);

        // Other properties.
        String showTitle = request.getRequestObject().getParameter("showTitle");
        String showLegend = request.getRequestObject().getParameter("showLegend");
        String disableDrillDown = request.getRequestObject().getParameter("disableDrillDown");
        String useProgressColumns = request.getRequestObject().getParameter("useProgressColumns");
        String axisInteger = request.getRequestObject().getParameter("axisInteger");
        String color = request.getRequestObject().getParameter("color");
        String color2 = request.getRequestObject().getParameter("color2");
        String backgroundColor = request.getRequestObject().getParameter("backgroundColor");
        String width = request.getRequestObject().getParameter("width");
        String height = request.getRequestObject().getParameter("height");
        String legendAnchor = request.getRequestObject().getParameter("legendAnchor");
        String graphicAlign = request.getRequestObject().getParameter("graphicAlign");
        String marginLeft = request.getRequestObject().getParameter("marginLeft");
        String marginRight = request.getRequestObject().getParameter("marginRight");
        String marginTop = request.getRequestObject().getParameter("marginTop");
        String marginBottom = request.getRequestObject().getParameter("marginBottom");
        String labelThreshold = request.getRequestObject().getParameter("labelThreshold");

        displayer.setShowTitle(showTitle != null);
        displayer.setShowLegend(showLegend != null);
        displayer.setDisableDrillDown(disableDrillDown != null);
        displayer.setUseProgressColumns(useProgressColumns != null);
        displayer.setAxisInteger(axisInteger != null);
        if (axisInteger != null) displayer.setAxisInteger(true);
        if (color != null && !"".equals(color)) displayer.setColor(color);
        if (color2 != null && !"".equals(color2)) displayer.setColor2(color2);
        if (backgroundColor != null && !"".equals(backgroundColor)) displayer.setBackgroundColor(backgroundColor);
        try {
            if (!StringUtils.isBlank(width)) displayer.setWidth(Integer.parseInt(width));
            if (!StringUtils.isBlank(height)) displayer.setHeight(Integer.parseInt(height));
            if (!StringUtils.isBlank(marginLeft)) displayer.setMarginLeft(Integer.parseInt(marginLeft));
            if (!StringUtils.isBlank(marginRight)) displayer.setMarginRight(Integer.parseInt(marginRight));
            if (!StringUtils.isBlank(marginTop)) displayer.setMarginTop(Integer.parseInt(marginTop));
            if (!StringUtils.isBlank(marginBottom)) displayer.setMarginBottom(Integer.parseInt(marginBottom));
            if (!StringUtils.isBlank(labelThreshold)) displayer.setLabelThreshold(Integer.parseInt(labelThreshold));
        } catch (NumberFormatException e) {
            log.warn("Cannot parse value width or height value as number.");
        }
        if (legendAnchor != null && !"".equals(legendAnchor)) {
            if (legendAnchor.equals("-1")) displayer.setShowLegend(false);
            else {
                displayer.setShowLegend(true);
                displayer.setLegendAnchor(legendAnchor);
            }
        }
        if (graphicAlign != null && !"".equals(graphicAlign)) displayer.setGraphicAlign(graphicAlign);
        
        // Sort settings
        int sortCrit = Integer.parseInt(request.getRequestObject().getParameter("intervalsSortCriteria"));
        int sortOrder = Integer.parseInt(request.getRequestObject().getParameter("intervalsSortOrder"));
        displayer.setIntervalsSortCriteria(sortCrit);
        displayer.setIntervalsSortOrder(sortOrder);

        // X AXIS labels
        String showLabelsXAxis = request.getRequestObject().getParameter("showLabelsXAxis");
        displayer.setShowLabelsXAxis(!StringUtils.isBlank(showLabelsXAxis));
        return null;
    }

    public CommandResponse actionCancel(CommandRequest request) throws Exception {
        return null;
    }
}
