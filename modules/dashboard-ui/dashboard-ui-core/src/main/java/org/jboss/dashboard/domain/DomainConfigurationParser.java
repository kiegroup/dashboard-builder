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
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.CommandRequest;

public class DomainConfigurationParser {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DomainConfigurationParser.class);

    protected DomainConfiguration config;

    public DomainConfigurationParser(DomainConfiguration config) {
        this.config = config;
    }

    public void parse(CommandRequest request) {
        Locale locale = LocaleManager.currentLocale();
        config.setPropertyName(request.getRequestObject().getParameter("descripDomainDetails"), locale);
        String maxIntervalsStr = request.getRequestObject().getParameter("domainMaxNumberOfIntervals");
        if (maxIntervalsStr != null && maxIntervalsStr.trim().length() > 0) {
            try {
                int maxIntervals = Integer.parseInt(maxIntervalsStr);
                config.setMaxNumberOfIntervals(Integer.toString(maxIntervals));
            } catch (NumberFormatException e) {
                log.warn("Cannot parse max intervals value as a number.");
            }
        }

        // Label domain specifics.
        config.setLabelIntervalsToHide(request.getRequestObject().getParameter("labelIntervalsToHide"), locale);
        
        // Date domain specifics.
        config.setDateTamInterval(request.getRequestObject().getParameter("dateTamInterval"));
        config.setDateMinDate(request.getRequestObject().getParameter("initialDate"));
        config.setDateMaxDate(request.getRequestObject().getParameter("endDate"));

        // Numeric domain specifics.
        config.setNumericTamInterval(request.getRequestObject().getParameter("numericTamInterval"));
        config.setNumericMinValue(request.getRequestObject().getParameter("numericMinValue"));
        config.setNumericMaxValue(request.getRequestObject().getParameter("numericMaxValue"));
    }
}
