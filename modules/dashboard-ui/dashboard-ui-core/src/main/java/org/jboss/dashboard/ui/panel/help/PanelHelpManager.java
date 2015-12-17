/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.panel.help;

import org.jboss.dashboard.LocaleManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class PanelHelpManager {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelHelpManager.class.getName());

    public static final String PHELP = "phelp";

    public static final String PANEL_ID = "panel-id";

    public static final String ABOUT = "about";

    public static final String USAGE = "usage";

    public static final String EDIT_USAGE = "edit-usage";

    public static final String PANEL_PARAMETER = "panel-parameter";

    public static final String TEXT = "text";

    public static final String LANG = "lang";

    public static final String NAME = "name";

    /**
     * Read a panel help from an input stream
     *
     * @param is
     * @return The panel help object read from the stream.
     */
    public PanelHelp readPanelHelp(InputStream is) throws IOException, SAXException {
        Document doc = getDocument(is);
        is.close();
        return getPanelHelp(doc);
    }

    /**
     * Write the panel help to an outputStream
     *
     * @param os
     */
    public void writePanelHelp(OutputStream os, PanelHelp pHelp) throws IOException {
        log.debug("Writing pHelp to output.");
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + "\n" + "<phelp>");
        writeAbout(writer, pHelp.getAbout());
        writePanelIds(writer, pHelp.getIds());
        writeUsage(writer, pHelp);
        writeEditUsage(writer, pHelp);
        writeParameters(writer, pHelp);
        writer.write("</phelp>");
        writer.close();
        log.debug("Written.");
    }

    protected void writeParameters(Writer writer, PanelHelp help) throws IOException {
        String[] paramNames = help.getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            writer.write("\n<panel-parameter name=\"" + paramName + "\">");
            String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
            for (int j = 0; j < langs.length; j++) {
                String lang = langs[i];
                String usage = help.getParameterUsage(paramName, new Locale(lang));
                if (usage != null) {
                    writeText(writer, usage, lang);
                }
            }
            writer.write("</panel-parameter>");
        }
    }

    protected void writeUsage(Writer writer, PanelHelp help) throws IOException {
        writer.write("\n<usage>");
        String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
        for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            String usage = help.getUsage(new Locale(lang));
            if (usage != null)
                writeText(writer, usage, lang);
        }
        writer.write("\n</usage>");
    }

    protected void writeEditUsage(Writer writer, PanelHelp help) throws IOException {
        writer.write("\n<edit-usage>");
        String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
        for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            String usage = help.getEditModeUsage(new Locale(lang));
            if (usage != null)
                writeText(writer, usage, lang);
        }
        writer.write("\n</edit-usage>");
    }

    protected void writePanelIds(Writer writer, String[] ids) throws IOException {
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            writer.write("\n<panel-id>" + escape(id) + "</panel-id>");
        }
    }

    protected void writeAbout(Writer writer, PanelAbout about) throws IOException {
        writer.write("\n<about>");
        String[] props = about.getProperties();
        for (int i = 0; i < props.length; i++) {
            String prop = props[i];
            String val = about.getProperty(prop);
            prop = escape(prop);
            writer.write("\n<" + prop + ">");
            writer.write(escape(val));
            writer.write("</" + prop + ">");
        }
        writer.write("\n</about>");
    }

    protected void writeText(Writer writer, String text, String lang) throws IOException {
        writer.write("\n<text lang=\"" + lang + "\">");
        writer.write(escape(text));
        writer.write("</text>");
    }

    protected String escape(String s) {
        return StringEscapeUtils.escapeXml(s);
    }

    /**
     * Convert given input stream to a Document.
     *
     * @param is
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    protected Document getDocument(InputStream is) throws SAXException, IOException {
        URL schemaUrl = getClass().getResource("help.xsd");

        if (schemaUrl == null)
            log.error("Could not find org.jboss.dashboard.ui.panel.help.help.xsd]. Used [" + getClass().getClassLoader() + "] class loader in the search.");
        else
            log.debug("URL to org.jboss.dashboard.ui.panel.help.help.xsd is [" + schemaUrl.toString() + "].");
        String schema = schemaUrl.toString();

        // Create a DOMParser
        DOMParser parser = new DOMParser();

        // Set the validation feature
        parser.setFeature("http://xml.org/sax/features/validation", true);

        // Set the schema validation feature
        parser.setFeature("http://apache.org/xml/features/validation/schema", true);

        // Set schema full grammar checking
        parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        // Disable whitespaces
        parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);

        // Set schema location
        parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schema);

        // Set the error handler
        parser.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXParseException {
                throw exception;
            }

            public void fatalError(SAXParseException exception) throws SAXParseException {
                throw exception;
            }

            public void warning(SAXParseException exception) {
            }
        });

        // Parse the XML document
        parser.parse(new InputSource(is));

        return parser.getDocument();
    }

    protected PanelHelp getPanelHelp(Document doc) {
        PanelHelpImpl help = new PanelHelpImpl();

        NodeList nlist = doc.getChildNodes();
        Node rootNode = null;
        for (int i = 0; i < nlist.getLength(); i++) {
            Node item = nlist.item(i);
            if (PHELP.equals(item.getNodeName()))
                rootNode = item;
        }
        if (rootNode != null) {
            NodeList rootChildren = rootNode.getChildNodes();
            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node node = rootChildren.item(i);
                String nodeName = node.getNodeName();
                if (PANEL_ID.equals(nodeName)) {
                    NodeList childNodes = node.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node textNode = childNodes.item(j);
                        if ("#text".equals(textNode.getNodeName())) {
                            help.addId(textNode.getNodeValue().trim());
                            break;
                        }
                    }
                } else if (USAGE.equals(nodeName)) {
                    Map m = getTextsFromNode(node);
                    for (Iterator iterator = m.entrySet().iterator(); iterator.hasNext();) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Locale locale = (Locale) entry.getKey();
                        String value = (String) entry.getValue();
                        help.addUsage(locale, value);
                    }
                } else if (EDIT_USAGE.equals(nodeName)) {
                    Map m = getTextsFromNode(node);
                    for (Iterator iterator = m.entrySet().iterator(); iterator.hasNext();) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Locale locale = (Locale) entry.getKey();
                        String value = (String) entry.getValue();
                        help.addEditUsage(locale, value);
                    }
                } else if (PANEL_PARAMETER.equals(nodeName)) {
                    String paramName = node.getAttributes().getNamedItem(NAME).getNodeValue();
                    Map m = getTextsFromNode(node);
                    for (Iterator iterator = m.entrySet().iterator(); iterator.hasNext();) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Locale locale = (Locale) entry.getKey();
                        String value = (String) entry.getValue();
                        help.addParamDescription(paramName, locale, value);
                    }
                } else if (ABOUT.equals(nodeName)) {
                    help.setAbout(getAboutFromNode(node));
                }
            }
        }
        if (log.isDebugEnabled())
            log.debug("Created help " + help);
        return help;
    }

    protected PanelAbout getAboutFromNode(Node node) {
        PanelAboutImpl about = new PanelAboutImpl();
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);
            String nodeName = child.getNodeName();
            String nodeValue = child.getNodeValue();
            NodeList childChildNodes = child.getChildNodes();
            for (int j = 0; j < childChildNodes.getLength(); j++) {
                Node textNode = childChildNodes.item(j);
                if ("#text".equals(textNode.getNodeName())) {
                    nodeValue = textNode.getNodeValue().trim();
                    break;
                }
            }
            if (nodeName != null && nodeValue != null && !"".equals(nodeName.trim()) && !"".equals(nodeValue.trim()))
                about.addProperty(nodeName, nodeValue);
        }
        return about;
    }

    protected Map getTextsFromNode(Node node) {
        Map m = new HashMap();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (TEXT.equals(child.getNodeName())) {
                String language = child.getAttributes().getNamedItem(LANG).getNodeValue();
                String value = null;
                NodeList childChildNodes = child.getChildNodes();
                for (int j = 0; j < childChildNodes.getLength(); j++) {
                    Node textNode = childChildNodes.item(j);
                    if ("#text".equals(textNode.getNodeName())) {
                        value = textNode.getNodeValue().trim();
                        break;
                    }
                }
                m.put(new Locale(language), value);
            }
        }
        return m;
    }

}
