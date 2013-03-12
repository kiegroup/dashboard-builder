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
package org.jboss.dashboard.workspace.export.structure;

import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.workspace.export.ExportVisitor;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of a Workspace import result.
 */
public class ImportResult extends ImportExportResult {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ImportResult.class.getName());

    private XMLNode node;
    private String entryName;
    private Map attributes = new HashMap();

    public Map getAttributes() {
        return attributes;
    }

    public ImportResult(String entryName, InputStream is) {
        super();
        this.entryName = entryName;
        try {
            load(is);
        }
        catch (Exception e) {
            setException(e);
        }
    }

    public XMLNode getRootNode() {
        return node;
    }

    protected void load(InputStream is) throws IOException, ParserConfigurationException, SAXException, SAXNotRecognizedException {
        URL schemaUrl = getClass().getResource("workspace.xsd");

        if (schemaUrl == null) log.fatal("Could not find [org.jboss.dashboard.workspace.export.workspace.xsd]. Used [" + getClass().getClassLoader() + "] class loader in the search.");
        else log.debug("URL to org.jboss.dashboard.workspace.export.workspace.xsd is [" + schemaUrl.toString() + "].");
        String schema = schemaUrl.toString();

        //Create a DOMParser
        DOMParser parser = new DOMParser();

        //Set the validation feature
        parser.setFeature("http://xml.org/sax/features/validation", true);

        //Set the schema validation feature
        parser.setFeature("http://apache.org/xml/features/validation/schema", true);

        //Set schema full grammar checking
        parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        //Disable whitespaces
        parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);

        //Set schema location
        parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schema);

        //Set the error handler
        parser.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXParseException {
                throw exception;
            }

            public void fatalError(SAXParseException exception) throws SAXParseException {
                throw exception;
            }

            public void warning(SAXParseException exception) {
                /*getWarnings().add(exception.getMessage());
                getWarningArguments().add(new Object[]{exception});*/
            }
        });

        // Put it in a byte array before because the parser closes the stream.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
        int byteRead;
        while ((byteRead = is.read()) != -1) {
            bos.write(byteRead);
        }

        //Parse the XML document
        parser.parse(new InputSource(new ByteArrayInputStream(bos.toByteArray())));

        Document doc = parser.getDocument();
        init(doc);
    }

    protected void init(Document doc) {
        NodeList nlist = doc.getChildNodes();
        Node rootNode = null;
        for (int i = 0; i < nlist.getLength(); i++) {
            Node item = nlist.item(i);
            if (ExportVisitor.WORKSPACE_EXPORT.equals(item.getNodeName()))
                rootNode = item;
        }
        node = new XMLNode("?", null);
        node.loadFromXMLNode(rootNode);
    }


    public String getEntryName() {
        return entryName;
    }
}
