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
package org.jboss.dashboard.export;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * BAM export manager.
 */
@ApplicationScoped
public class ExportManagerImpl implements ExportManager {

    private transient static Log log = LogFactory.getLog(ExportManagerImpl.class);

    public ExportOptions createExportOptions() {
        return new ExportOptionsImpl();
    }

    public String format(ExportOptions options) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        format(options, out, 0);
        return sw.toString();
    }

    public void format(ExportOptions options, PrintWriter out, int indent) throws Exception {
        // Start
        printIndent(out, indent++);
        out.println("<kpis>");

        if (!options.ignoreDataProviders()) formatDataProviders(options, out, indent);
        if (!options.ignoreKPIs()) formatKPIs(options, out, indent);

        // End
        printIndent(out, --indent);
        out.println("</kpis>");
    }

    public void formatKPIs(ExportOptions options, PrintWriter out, int indent) throws Exception {
        Iterator it = options.getKPIs().iterator();
        while (it.hasNext()) {
            KPI kpi = (KPI) it.next();
            DataProvider provider = kpi.getDataProvider();
            DataDisplayer displayer = kpi.getDataDisplayer();
            DataDisplayerXMLFormat displayerXMLFormat = displayer.getDataDisplayerType().getXmlFormat();

            // Start KPI
            printIndent(out, indent++);
            out.println("<kpi code=\"" + StringEscapeUtils.escapeXml(kpi.getCode()) + "\">");

            // Description
            Map descriptions = kpi.getDescriptionI18nMap();
            for (Object o : descriptions.keySet()) {
                String key = (String) o;
                printIndent(out, indent);
                out.print("<description language");
                out.print("=\"" + StringEscapeUtils.escapeXml(key) + "\">");
                out.print(StringEscapeUtils.escapeXml((String) descriptions.get(key)));
                out.println("</description>");
            }

            // Provider
            String providerCode = provider.getCode();
            if (providerCode != null) {
                printIndent(out, indent);
                out.println("<provider code=\"" + StringEscapeUtils.escapeXml(providerCode) + "\" />");
            }

            // Displayer
            displayerXMLFormat.format(displayer, out, indent);

            // End KPI
            printIndent(out, --indent);
            out.println("</kpi>");
        }
    }

    public void formatDataProviders(ExportOptions options, PrintWriter out, int indent) throws Exception {
        for (DataProvider dataProvider : options.getDataProviders()) {
            DataLoader dataLoader = dataProvider.getDataLoader();
            DataProviderType providerType = dataLoader.getDataProviderType();

            printIndent(out, indent++);
            out.println("<dataprovider code=\"" + StringEscapeUtils.escapeXml(dataProvider.getCode()) + "\" type=\"" + StringEscapeUtils.escapeXml(providerType.getUid()) + "\">");

            Map descriptions = dataProvider.getDescriptionI18nMap();
            Iterator keys = descriptions.keySet().iterator();
            while (keys.hasNext()) {
                Locale key = (Locale) keys.next();
                printIndent(out, indent);
                out.print("<description language");
                out.print("=\"" + StringEscapeUtils.escapeXml(key.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml((String) descriptions.get(key)));
                out.println("</description>");
            }           

            if (!dataProvider.isCanEdit()) {
                printIndent(out, indent);
                out.println("<canEdit>false</canEdit>");
            }

            if (!dataProvider.isCanEditProperties()) {
                printIndent(out, indent);
                out.println("<canEditProperties>false</canEditProperties>");
            }

            if (!dataProvider.isCanDelete()) {
                printIndent(out, indent);
                out.println("<canDelete>false</canDelete>");
            }

            // The loader
            DataLoaderXMLFormat providerXMLFormat = providerType.getXmlFormat();
            providerXMLFormat.format(dataLoader, out, indent);

            // Provider properties
            dataProvider.getDataSet().formatXMLProperties(out, indent);

            // End
            printIndent(out, --indent);
            out.println("</dataprovider>");
        }
    }

    protected  void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }
}