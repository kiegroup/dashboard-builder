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

import org.jboss.dashboard.commons.misc.CalendarUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class DashboardFilterRequestProcessor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardFilterRequestProcessor.class.getName());

    /**
     * Parse property filter parameters from HttpRequest object.
     * @param dashboardFilterProperty property to parse
     * @return Returns allowedValues, minValue and maxValue. In this order in the Object[]. If all elements are null object will be not parsed.
     * @throws Exception parseException. Property will be not parsed or filtered.
     */
    public Object[] parseDashboardProperty(HttpServletRequest request, DashboardFilterProperty dashboardFilterProperty) throws Exception {
        Object[] result = new Object[3];
        result[0] = null; result[1] = null; result[2] = null;

        if (dashboardFilterProperty.isLabelProperty()) {
            result[0] = parseValueForLabelProperty(request,dashboardFilterProperty);
        }

        if (dashboardFilterProperty.isDateProperty()) {
            Object[] minMaxValue = parseValueForDateProperty(request,dashboardFilterProperty);
            if (minMaxValue != null) {
                result[1] = minMaxValue[0];
                result[2] = minMaxValue[1];
            }
        }

        if (dashboardFilterProperty.isNumericProperty()) {
            Object[] minMaxValue = parseMinMaxValueForProperty(request,dashboardFilterProperty);
            if (minMaxValue != null) {
                result[1] = minMaxValue[0];
                result[2] = minMaxValue[1];
            }
        }

        return result;
    }

    public Object parseValueForLabelProperty(HttpServletRequest request, DashboardFilterProperty dashboardFilterProperty) throws Exception {
        if (!dashboardFilterProperty.isLabelProperty()) throw new UnsupportedOperationException();

        Map parameters = request.getParameterMap();
        String key = DashboardFilterHandler.PARAM_VALUE + "_" + dashboardFilterProperty.getPropertyId();
        if (parameters.containsKey(key)) {
             String value = ((String[])parameters.get(key))[0];
             Collection allowedValues = new ArrayList();
             if (DashboardFilterHandler.PARAM_NULL_VALUE.equals(value)) return null;

            // Free text input.
            if (DashboardFilterHandler.PARAM_CUSTOM_VALUE.equals(value)) {
                value = ((String[])parameters.get(DashboardFilterHandler.PARAM_CUSTOM_VALUE+"_"+dashboardFilterProperty.getPropertyId()))[0];
                if (value == null || value.trim().length() == 0) return null;
                allowedValues.add(value);
            }
            // Combo selection.
            else {
                List propsValues = dashboardFilterProperty.getPropertyDistinctValues();
                if (propsValues != null && !propsValues.isEmpty()) {
                    Object propSelectedvalue = propsValues.get(Integer.decode(value).intValue() - 2);
                    allowedValues.add(propSelectedvalue);
                }
            }
            return allowedValues;
        }
        return null;
    }

    public Object[] parseValueForDateProperty(HttpServletRequest request, DashboardFilterProperty dashboardFilterProperty) throws Exception {
        if (!dashboardFilterProperty.isDateProperty()) throw new UnsupportedOperationException();
        Map paramters = request.getParameterMap();
        String key = DashboardFilterHandler.PARAM_VALUE + "_" + dashboardFilterProperty.getPropertyId();
        if (paramters.containsKey(key)) {
            String value = ((String[]) paramters.get(key))[0];
            if (DashboardFilterHandler.PARAM_NULL_VALUE.equals(value)) return null;
            else if (DashboardFilterHandler.PARAM_CUSTOM_VALUE.equals(value)) {
                return parseMinMaxValueForProperty(request,dashboardFilterProperty);
            } else {
                Date[] result = getCustomDate(value);
                Object minValue = result[0];
                Object maxValue = result[1];
                return new Object[] {minValue,maxValue};
            }
        }
        return null;
    }

    public Object[] parseMinMaxValueForProperty(HttpServletRequest request, DashboardFilterProperty dashboardFilterProperty) throws Exception {
        Map paramters = request.getParameterMap();
        String minKey = DashboardFilterHandler.PARAM_VALUE_MIN + "_" + dashboardFilterProperty.getPropertyId();
        String maxKey = DashboardFilterHandler.PARAM_VALUE_MAX + "_" + dashboardFilterProperty.getPropertyId();
        String minKeyValue = ((String[]) paramters.get(minKey))[0];
        String maxKeyValue = ((String[]) paramters.get(maxKey))[0];
        try {
            Object minValue=null; Object maxValue=null;
            if (minKeyValue != null && minKeyValue.trim().length() > 0) minValue = dashboardFilterProperty.parsePropertyValue(minKeyValue);
            if (maxKeyValue != null && maxKeyValue.trim().length() > 0) maxValue = dashboardFilterProperty.parsePropertyValue(maxKeyValue);
            if (minValue == null && maxValue == null) return null;
            return new Object[] {minValue,maxValue};
        } catch (Exception e) {
             throw e;
        }
    }

    protected Date[] getCustomDate(String request_date_parameter) {
        Calendar calendar = CalendarUtils.getInstance();
        Calendar[] result = null;

        if (DashboardFilterHandler.PARAM_LAST_HOUR.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLastHour(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_12HOURS.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLast12Hours(calendar);
        }
        else if (DashboardFilterHandler.PARAM_TODAY.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getToday(calendar);

        } else if (DashboardFilterHandler.PARAM_YESTERDAY.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getYesterday(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_7DAYS.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLast7Days(calendar);
        }
        else if (DashboardFilterHandler.PARAM_THIS_MONTH.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getThisMonth(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_MONTH.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLastMonth(calendar);
        }
        else if (DashboardFilterHandler.PARAM_THIS_QUARTER.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getThisQuarter(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_QUARTER.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLastQuarter(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_6MONTHS.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLast6Months(calendar);
        }
        else if (DashboardFilterHandler.PARAM_THIS_YEAR.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getThisYear(calendar);
        }
        else if (DashboardFilterHandler.PARAM_LAST_YEAR.equals(request_date_parameter)) {
            result = CalendarUtils.CalendarRangeUtils.getLastYear(calendar);
        }

        if (result == null) return null;
        return new Date[] {result[0].getTime(), result[1].getTime()};
    }

}
