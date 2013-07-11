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
package org.jboss.dashboard.ui.components.js;

import org.jboss.dashboard.annotation.config.Config;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JSIncluder {

    @Inject @Config(//"/components/bam/displayer/chart/gauge/raphael.2.1.0.min.js," +
                    //"/components/bam/displayer/chart/gauge/justgage.1.0.1.min.js," +
                    "/components/bam/displayer/chart/nvd3/lib/d3.v2.min.js," +
                    "/components/bam/displayer/chart/nvd3/nv.d3.min.js," +
                    "/components/bam/displayer/chart/nvd3/src/tooltip.js," +
                    "/components/bam/displayer/chart/nvd3/src/utils.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/axis.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/discreteBar.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/discreteBarChart.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/legend.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/scatter.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/line.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/lineChart.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/pie.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/pieChart.js," +
                    "/js/lib/scriptaculous-js-1.9.0/prototype.js," +
                    "/js/lib/scriptaculous-js-1.9.0/scriptaculous.js," +
                    "/js/lib/scriptaculous-js-1.9.0/effects.js," +
                    "/js/lib/scriptaculous-js-1.9.0/dragdrop.js," +
                    "/common/rs/popup.js," +
                    "/ckeditor/ckeditor.js")
    private String[] jsFilesToIncludeInHeader;

    @Inject @Config("/templates/navigatorDetection.jsp," +
                    "/common/rs/ajax.jsp," +
                    "/components/colorpicker/js/colorPicker.jsp," +
                    "/components/datepicker/js/datePicker.jsp")
    private String[] jspFilesToIncludeInHeader;

    @Inject @Config("")
    private String[] jspFilesToIncludeInBottom;

    @Inject @Config("")
    private String[] jsFilesToIncludeInBottom;

    public String[] getJsFilesToIncludeInHeader() {
        return jsFilesToIncludeInHeader;
    }

    public String[] getJsFilesToIncludeInBottom() {
        return jsFilesToIncludeInBottom;
    }

    public String[] getJspFilesToIncludeInHeader() {
        return jspFilesToIncludeInHeader;
    }

    public String[] getJspFilesToIncludeInBottom() {
        return jspFilesToIncludeInBottom;
    }
}

