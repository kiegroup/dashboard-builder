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
package org.jboss.dashboard.domain;

import java.util.Locale;

import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.CommandRequest;

public class RangeConfigurationParser {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RangeConfigurationParser.class);

    protected RangeConfiguration config;

    public RangeConfigurationParser(RangeConfiguration config) {
        this.config = config;
    }

    public void parse(CommandRequest request) {
        Locale locale = LocaleManager.currentLocale();
        config.setName(request.getRequestObject().getParameter("descripRangeDetails"), locale);
        config.setScalarFunctionCode(request.getRequestObject().getParameter("scalarFunctionCode"));
        String unit = request.getRequestObject().getParameter("unit");
        if (unit != null && unit.indexOf(AbstractChartDisplayer.UNIT_VALUE_TAG) != -1) config.setUnit(unit, locale);
    }
}
