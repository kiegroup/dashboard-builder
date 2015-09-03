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
package org.jboss.dashboard.provider.csv;

import org.jboss.dashboard.export.AbstractDataLoaderXMLFormat;
import org.jboss.dashboard.provider.DataLoader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

/**
 * CSVDataLoader XML parsing and formatting services.
 */
public class CSVDataLoaderXMLFormat extends AbstractDataLoaderXMLFormat {

    public CSVDataLoaderXMLFormat() {
        super();
    }

    public DataLoader parse(NodeList xmlNodes) throws Exception {
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node item = xmlNodes.item(i);
            if (item.getNodeName().equals("csvprovider")) {
                CSVDataLoader loader = new CSVDataLoader();
                NodeList subNodes = item.getChildNodes();
                for (int j = 0; j < subNodes.getLength(); j++) {
                    item = subNodes.item(j);

                    if (item.getNodeName().equals("csvSeparatedBy") && item.hasChildNodes()) {
                        loader.setCsvSeparatedBy(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("csvQuoteChar") && item.hasChildNodes()) {
                        loader.setCsvQuoteChar(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("csvEscapeChar") && item.hasChildNodes()) {
                        loader.setCsvEscapeChar(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("csvDatePattern") && item.hasChildNodes()) {
                        loader.setCsvDatePattern(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("csvNumberPattern") && item.hasChildNodes()) {
                        loader.setCsvNumberPattern(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("fileURL") && item.hasChildNodes()) {
                        loader.setFileURL(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                }
                return loader;
            }
        }
        return null;
    }

    public void format(DataLoader loader, PrintWriter out, int indent) throws Exception {
        CSVDataLoader csvLoader = (CSVDataLoader) loader;
        printIndent(out, indent++);
        out.println("<csvprovider>");

        printIndent(out, indent);
        out.print("<csvSeparatedBy>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getCsvSeparatedBy()));
        out.println("</csvSeparatedBy>");

        printIndent(out, indent);
        out.print("<csvQuoteChar>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getCsvQuoteChar()));
        out.println("</csvQuoteChar>");

        printIndent(out, indent);
        out.print("<csvEscapeChar>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getCsvEscapeChar()));
        out.println("</csvEscapeChar>");

        printIndent(out, indent);
        out.print("<csvDatePattern>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getCsvDatePattern()));
        out.println("</csvDatePattern>");

        printIndent(out, indent);
        out.print("<csvNumberPattern>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getCsvNumberPattern()));
        out.println("</csvNumberPattern>");

        printIndent(out, indent);
        out.print("<fileURL>");
        out.print(StringEscapeUtils.escapeXml(csvLoader.getFileURL()));
        out.println("</fileURL>");

        printIndent(out, --indent);
        out.println("</csvprovider>");
    }
}
