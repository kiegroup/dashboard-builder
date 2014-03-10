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
package org.jboss.dashboard.displayer.map;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.displayer.AbstractDataDisplayerXMLFormat;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.chart.ChartDisplayerXMLFormat;
import org.jboss.dashboard.displayer.table.DataSetTable;
import org.jboss.dashboard.displayer.table.TableColumn;
import org.jboss.dashboard.displayer.table.TableDisplayer;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.provider.DataProperty;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class offers both XML parsing and formatting services for Map displayers.
 */
@ApplicationScoped
public class MapDisplayerXMLFormat {

    public MapDisplayerXMLFormat() {
        super();
    }
}