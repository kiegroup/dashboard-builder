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
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.domain.DomainConfigurationParser;
import org.jboss.dashboard.domain.RangeConfigurationParser;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.domain.RangeConfiguration;
import org.jboss.dashboard.dataset.DataSet;
import org.apache.commons.lang.StringUtils;
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
                DomainConfiguration domainConfig = new DomainConfiguration();
                DomainConfigurationParser parser = new DomainConfigurationParser(domainConfig);
                parser.parse(request);
                domainConfig.setPropertyId(idDomainDetails);
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
                RangeConfiguration rangeConfig = new RangeConfiguration();
                RangeConfigurationParser parser = new RangeConfigurationParser(rangeConfig);
                parser.parse(request);
                rangeConfig.setPropertyId(idRangeDetails);
                rangeConfig.apply(displayer.getRangeProperty());
                displayer.setRangeScalarFunction(DataDisplayerServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(rangeConfig.getScalarFunctionCode()));
                displayer.setUnitI18nMap(rangeConfig.getUnitI18nMap());
            }
        }

        // Retrieve other configuration parameters and set the new properties to the displayer.
        String chartType = request.getRequestObject().getParameter("chartType");
        if (chartType != null && !"".equals(chartType)) displayer.setType(chartType);

        // Other properties.
        String showTitle = request.getRequestObject().getParameter("showTitle");
        String showLegend = request.getRequestObject().getParameter("showLegend");
        String axisInteger = request.getRequestObject().getParameter("axisInteger");
        String color = request.getRequestObject().getParameter("color");
        String backgroundColor = request.getRequestObject().getParameter("backgroundColor");
        String width = request.getRequestObject().getParameter("width");
        String height = request.getRequestObject().getParameter("height");
        String legendAnchor = request.getRequestObject().getParameter("legendAnchor");
        String graphicAlign = request.getRequestObject().getParameter("graphicAlign");
        String marginLeft = request.getRequestObject().getParameter("marginLeft");
        String marginRight = request.getRequestObject().getParameter("marginRight");
        String marginTop = request.getRequestObject().getParameter("marginTop");
        String marginBottom = request.getRequestObject().getParameter("marginBottom");

        displayer.setShowTitle(showTitle != null);
        displayer.setShowLegend(showLegend != null);
        displayer.setAxisInteger(axisInteger != null);
        if (axisInteger != null) displayer.setAxisInteger(true);
        if (color != null && !"".equals(color)) displayer.setColor(color);
        if (backgroundColor != null && !"".equals(backgroundColor)) displayer.setBackgroundColor(backgroundColor);
        try {
            if (!StringUtils.isBlank(width)) displayer.setWidth(Integer.parseInt(width));
            if (!StringUtils.isBlank(height)) displayer.setHeight(Integer.parseInt(height));
            if (!StringUtils.isBlank(marginLeft)) displayer.setMarginLeft(Integer.parseInt(marginLeft));
            if (!StringUtils.isBlank(marginRight)) displayer.setMarginRight(Integer.parseInt(marginRight));
            if (!StringUtils.isBlank(marginTop)) displayer.setMarginTop(Integer.parseInt(marginTop));
            if (!StringUtils.isBlank(marginBottom)) displayer.setMarginBottom(Integer.parseInt(marginBottom));
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
