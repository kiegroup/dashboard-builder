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

import org.jboss.dashboard.displayer.chart.AbstractXAxisDisplayer;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.ui.controller.CommandResponse;

public abstract class AbstractXAxisDisplayerEditor extends AbstractChartDisplayerEditor {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(AbstractXAxisDisplayerEditor.class);

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        AbstractXAxisDisplayer xAxisDisplayer = (AbstractXAxisDisplayer) getDataDisplayer();
        if (!xAxisDisplayer.getDataProvider().isReady()) return null;

        super.actionSubmit(request);

        String labelAngleXAxis = request.getRequestObject().getParameter("labelAngleXAxis");
        String showLinesAreas  = request.getRequestObject().getParameter("showLinesAreas");
        try {
            if (!StringUtils.isBlank(labelAngleXAxis)) xAxisDisplayer.setLabelAngleXAxis(Integer.parseInt(labelAngleXAxis));
            if (!StringUtils.isBlank(showLinesAreas)) { xAxisDisplayer.setShowLinesArea(true); } else { xAxisDisplayer.setShowLinesArea(false); }
        } catch (NumberFormatException e) {
            log.warn("Cannot parse labelAngleXAxis value as number: " + labelAngleXAxis);
        }
        return null;
    }
}
