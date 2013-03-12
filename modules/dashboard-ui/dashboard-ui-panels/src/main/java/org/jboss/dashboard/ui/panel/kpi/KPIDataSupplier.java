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
package org.jboss.dashboard.ui.panel.kpi;


import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameterDataSupplier;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.LocaleManager;

import java.util.List;
import java.util.ArrayList;

/**
 * KPI selector data supplier.
 */
public class KPIDataSupplier implements ComboListParameterDataSupplier {

    /** Logger */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(KPIDataSupplier.class);

    public KPIDataSupplier() {
    }

    public void init(PanelInstance panelInstance) {
    }

    public List getKeys() {
        List keys = new ArrayList();
        try {
            for (KPI kpi : getKPIList()) {
                keys.add(kpi.getCode());
            }
        } catch (Exception e) {
            log.error("Can not retrieve KPIs.", e);
        }
        return keys;
    }

    public List getValues() {
        List values = new ArrayList();
        try {
            for (KPI kpi : getKPIList()) {
                values.add(kpi.getDescription(LocaleManager.currentLocale()));
            }
        } catch (Exception e) {
            log.error("Can not retrieve KPIs.", e);
        }
        return values;
    }

    public List<KPI> getKPIList() throws Exception {
        List<KPI> kpis = new ArrayList<KPI>(DataDisplayerServices.lookup().getKPIManager().getAllKPIs());
        DataDisplayerServices.lookup().getKPIManager().sortKPIsByDescription(kpis, true);
        return kpis;
    }
}
