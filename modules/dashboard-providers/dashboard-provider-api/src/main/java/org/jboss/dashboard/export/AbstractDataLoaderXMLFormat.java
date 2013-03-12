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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.jboss.dashboard.provider.DataLoader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Base class for the implementation of custom data loader XML formatters.
 */
public abstract class AbstractDataLoaderXMLFormat implements DataLoaderXMLFormat {

    protected AbstractDataLoaderXMLFormat() {
    }

    public void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    public DataLoader parse(String xml) throws Exception {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        dFactory.setIgnoringComments(true);
        StringReader isr = new StringReader(xml);
        Document doc = dBuilder.parse(new InputSource(isr));
        isr.close();
        return parse(doc.getChildNodes());
    }

    public String format(DataLoader loader) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        format(loader, pw, 0);
        return sw.toString();
    }
}
