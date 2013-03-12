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

import java.text.NumberFormat;
import java.util.Locale;

import org.jboss.dashboard.displayer.chart.MeterChartDisplayer;
import org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeterChartEditor extends AbstractChartDisplayerEditor {

    /** Logger */
    private transient static Log log = LogFactory.getLog(MeterChartEditor.class);

    // i18n
    public static final String I18N_METER = "meterChartDisplayer.";
    public static final String METER_SAVE_BUTTON_PRESSED = "updateMeterDetails";

    public void actionSubmit(CommandRequest request) throws Exception {
        MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) getDataDisplayer();
        if (!meterDisplayer.getDataProvider().isReady()) return;

        super.actionSubmit(request);
        try {
            Locale locale = LocaleManager.currentLocale();
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

            // Parse the position.
            String positionType = request.getRequestObject().getParameter("positionType");
            if (positionType != null && !"".equals(positionType)) meterDisplayer.setPositionType(positionType);

            // Meter
            if (meterDisplayer.getType().equals("meter")) {
                String minValueParam = request.getRequestObject().getParameter("minValue");
                String maxValueParam = request.getRequestObject().getParameter("maxValue");
                String maxMeterTicksParam = request.getRequestObject().getParameter("maxMeterTicks");
                String warningThresholdParam = request.getRequestObject().getParameter("meterWarningThreshold");
                String criticalThresholdParam = request.getRequestObject().getParameter("meterCriticalThreshold");
                if (minValueParam == null || "".equals(minValueParam.trim())) return;
                if (maxValueParam == null || "".equals(maxValueParam.trim())) return;
                if (warningThresholdParam == null || "".equals(warningThresholdParam.trim())) return;
                if (criticalThresholdParam == null || "".equals(criticalThresholdParam.trim())) return;
                if (maxMeterTicksParam == null || "".equals(maxMeterTicksParam.trim())) return;

                double minValue = numberFormat.parse(minValueParam).doubleValue();
                double maxValue = numberFormat.parse(maxValueParam).doubleValue();
                double warningThreshold = numberFormat.parse(warningThresholdParam).doubleValue();
                double criticalThreshold = numberFormat.parse(criticalThresholdParam).doubleValue();
                int maxMeterTicks = numberFormat.parse(maxMeterTicksParam).intValue();
                if (minValue > maxValue) return;
                if (warningThreshold < minValue || warningThreshold > maxValue) return;
                if (criticalThreshold < minValue || criticalThreshold > maxValue) return;
                if (warningThreshold > criticalThreshold) return;
                if (maxMeterTicks < 0) return;

                meterDisplayer.setMaxMeterTicks(maxMeterTicks);
                meterDisplayer.setMinValue(minValue);
                meterDisplayer.setWarningThreshold(warningThreshold);
                meterDisplayer.setCriticalThreshold(criticalThreshold);
                meterDisplayer.setMaxValue(maxValue);

                /* Intervals descriptions. Hide them until the global legend will be available.
                    String descripNormalInterval = request.getRequestObject().getParameter("descripNormalInterval");
                    String descripWarningInterval = request.getRequestObject().getParameter("descripWarningInterval");
                    String descripCriticalInterval = request.getRequestObject().getParameter("descripCriticalInterval");
                    if (descripCriticalInterval != null && !"".equals(descripCriticalInterval.trim())) meterDisplayer.setDescripCriticalInterval(descripCriticalInterval, locale);
                    if (descripWarningInterval != null && !"".equals(descripWarningInterval.trim())) meterDisplayer.setDescripWarningInterval(descripWarningInterval, locale);
                    if (descripNormalInterval != null && !"".equals(descripNormalInterval.trim())) meterDisplayer.setDescripNormalInterval(descripNormalInterval, locale);
                */
            }
            // Thermometer
            else if (meterDisplayer.getType().equals("thermometer")) {
                String thermoLowerBoundParam = request.getRequestObject().getParameter("thermoLowerBound");
                String thermoUpperBoundParam = request.getRequestObject().getParameter("thermoUpperBound");
                String thermoWarningThresholdParam = request.getRequestObject().getParameter("thermoWarningThreshold");
                String thermoCriticalThresholdParam = request.getRequestObject().getParameter("thermoCriticalThreshold");
                if (thermoLowerBoundParam == null || "".equals(thermoLowerBoundParam.trim())) return;
                if (thermoUpperBoundParam == null || "".equals(thermoUpperBoundParam.trim())) return;
                if (thermoWarningThresholdParam == null || "".equals(thermoWarningThresholdParam.trim())) return;
                if (thermoCriticalThresholdParam == null || "".equals(thermoCriticalThresholdParam.trim())) return;

                double thermoLowerBound = numberFormat.parse(thermoLowerBoundParam).doubleValue();
                double thermoUpperBound = numberFormat.parse(thermoUpperBoundParam).doubleValue();
                double thermoWarningThreshold = numberFormat.parse(thermoWarningThresholdParam).doubleValue();
                double thermoCriticalThreshold = numberFormat.parse(thermoCriticalThresholdParam).doubleValue();
                if (thermoLowerBound > thermoUpperBound) return;
                if (thermoWarningThreshold < thermoLowerBound || thermoWarningThreshold > thermoUpperBound) return;
                if (thermoCriticalThreshold < thermoLowerBound || thermoCriticalThreshold > thermoUpperBound) return;
                if (thermoWarningThreshold > thermoCriticalThreshold) return;

                meterDisplayer.setThermoLowerBound(thermoLowerBound);
                meterDisplayer.setWarningThermoThreshold(thermoWarningThreshold);
                meterDisplayer.setCriticalThermoThreshold(thermoCriticalThreshold);
                meterDisplayer.setThermoUpperBound(thermoUpperBound);

                /* Original units of the thermometer disabled.
                    String thermoUnits = request.getRequestObject().getParameter("thermoUnits");
                    if (thermoUnits != null && !"".equals(thermoUnits)) meterDisplayer.setThermoUnits(Integer.parseInt(thermoUnits));
                 */
            }
            // Dial
            else if (meterDisplayer.getType().equals("dial")) {
                String pointerTypeParam = request.getRequestObject().getParameter("pointerType");
                String dialLowerBoundParam = request.getRequestObject().getParameter("dialLowerBound");
                String dialUpperBoundParam = request.getRequestObject().getParameter("dialUpperBound");
                String maxTicksParam = request.getRequestObject().getParameter("maxTicks");
                String minorTickCountParam = request.getRequestObject().getParameter("minorTickCount");
                if (pointerTypeParam == null || "".equals(pointerTypeParam.trim())) return;
                if (dialLowerBoundParam == null || "".equals(dialLowerBoundParam.trim())) return;
                if (dialUpperBoundParam == null || "".equals(dialUpperBoundParam.trim())) return;
                if (maxTicksParam == null || "".equals(maxTicksParam.trim())) return;
                if (minorTickCountParam == null || "".equals(minorTickCountParam.trim())) return;

                double dialLowerBound = numberFormat.parse(dialLowerBoundParam).doubleValue();
                double dialUpperBound = numberFormat.parse(dialUpperBoundParam).doubleValue();
                int maxTicks = numberFormat.parse(maxTicksParam).intValue();
                int minorTickCount = numberFormat.parse(minorTickCountParam).intValue();
                if (dialLowerBound > dialUpperBound) return;
                if (maxTicks < 0) return;
                if (minorTickCount > 10) return;

                meterDisplayer.setDialLowerBound(dialLowerBound);
                meterDisplayer.setDialUpperBound(dialUpperBound);
                meterDisplayer.setMaxTicks(numberFormat.parse(maxTicksParam).intValue());
                meterDisplayer.setMinorTickCount(minorTickCount);
            }
        } catch (Exception e) {
            log.warn("Cannot parse number meter specific properties.");
        }
    }
}