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
package org.jboss.dashboard.provider.sql;

import org.jboss.dashboard.export.AbstractDataLoaderXMLFormat;
import org.jboss.dashboard.provider.DataLoader;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Iterator;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * This class it offers both SQLDataLoader XML parsing and formatting services.
 * <p>Below is a sample SQL loader formatted as XML:<br><br>
 * <font size="-1">
 * &lt;sqlprovider&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;datasource&gt;local&lt;/datasource&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;query&gt;SELECT * from EXPENSE_REPORTS&lt;/query&gt;<br>
 * &lt;/sqlprovider&gt;<br>
 * </font>
 */
public class SQLDataLoaderXMLFormat extends AbstractDataLoaderXMLFormat {

    public SQLDataLoaderXMLFormat() {
        super();
    }

    public DataLoader parse(NodeList xmlNodes) throws Exception {
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node item = xmlNodes.item(i);
            if (item.getNodeName().equals("sqlprovider")) {
                SQLDataLoader loader = new SQLDataLoader();
                NodeList subNodes = item.getChildNodes();
                for (int j = 0; j < subNodes.getLength(); j++) {
                    item = subNodes.item(j);
                    if (item.getNodeName().equals("datasource") && item.hasChildNodes()) {
                        loader.setDataSource(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()));
                    }
                    if (item.getNodeName().equals("query") && item.hasChildNodes()) {
                        Node typeNode = item.getAttributes().getNamedItem("type");
                        String dataBaseName = SQLDataLoader.PARAM_DEFAULT_QUERY;
                        if (typeNode != null) dataBaseName = typeNode.getNodeValue().toLowerCase();
                        loader.setSQLQuery(StringEscapeUtils.unescapeXml(item.getFirstChild().getNodeValue()), dataBaseName);
                    }                   
                }
                return loader;
            }
        }
        return null;
    }

    public void format(DataLoader loader, PrintWriter out, int indent) throws Exception {
        SQLDataLoader sqlLoader = (SQLDataLoader) loader;
        printIndent(out, indent++);
        out.println("<sqlprovider>");

        printIndent(out, indent);
        out.print("<datasource>");
        out.print(StringEscapeUtils.escapeXml(sqlLoader.getDataSource()));
        out.println("</datasource>");

        Map queryMap = sqlLoader.getQueryMap();
        if (queryMap.size() > 1) {
            Iterator it = queryMap.keySet().iterator();
            while (it.hasNext()) {
                String dataBaseName = (String) it.next();
                printIndent(out, indent);
                out.print("<query type=\""+ dataBaseName.toLowerCase() + "\">");
                out.print(StringEscapeUtils.escapeXml(sqlLoader.getSQLQuery(dataBaseName)));
                out.println("</query>");
            }
        } else if (queryMap.size() == 1) {
            // Serialize query as default.
            String dataBaseName = (String) queryMap.keySet().iterator().next();
            out.print("<query>");
            out.print(StringEscapeUtils.escapeXml(sqlLoader.getSQLQuery(dataBaseName)));
            out.println("</query>");
        }

        printIndent(out, --indent);
        out.println("</sqlprovider>");
    }
}
