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
package org.jboss.dashboard.commons.xml;

import org.jboss.dashboard.commons.text.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.util.XMLChar;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class XMLNode implements Serializable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(XMLNode.class.getName());

    private String objectName;

    private Properties attributes;
    private List<XMLNode> children;
    private byte[] content;
    private List warnings;
    private List warningArguments;
    private XMLNode parent;

    public XMLNode(String objectName, XMLNode parent) {
        this.parent = parent;
        this.objectName = objectName;
        attributes = new Properties();
        children = new ArrayList<XMLNode>();
        warnings = new ArrayList();
        warningArguments = new ArrayList();
    }

    public String getObjectName() {
        return objectName;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildren() {
        return children;
    }

    public List getWarnings() {
        return warnings;
    }

    public List getWarningArguments() {
        return warningArguments;
    }

    public XMLNode getParent() {
        return parent;
    }

    public Object addAttribute(String name, String value) {
        if (name != null) {
            if (value == null) {
                return attributes.remove(name);
            }
            return attributes.setProperty(name, value);
        }
        return null;
    }

    public void addChild(XMLNode node) {
        children.add(node);
    }

    public void addWarning(String warning, Object[] arguments) {
        warnings.add(warning);
        warningArguments.add(arguments);
    }

    public void addWarning(String warning, Object arguments) {
        warnings.add(warning);
        warningArguments.add(new Object[]{arguments});
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void writeXML(Writer writer, boolean blanks) throws IOException {
        writer.write(blanks ? "\n" : "");
        writer.write("<");
        writer.write(objectName);
        for (Iterator it = attributes.keySet().iterator(); it.hasNext();) {
            String attributeName = (String) it.next();
            String attributeValue = attributes.getProperty(attributeName, "");
            writer.write(" " + attributeName + "=\"" + escapeXml(attributeValue) + "\"");
        }
        if (children.isEmpty() && content == null) {
            writer.write("/>");
        } else {
            writer.write(">");
            for (XMLNode child : children) {
                child.writeXML(writer, blanks);
            }
            if (content != null)
                writer.write(Base64.encode(content));
            else
                writer.write(blanks ? "\n" : "");
            writer.write("</");
            writer.write(objectName);
            writer.write(">");
        }
        writer.flush();
    }

    public void loadFromXMLNode(Node node) {
        objectName = node.getNodeName();
        NamedNodeMap attributesMap = node.getAttributes();
        if (attributesMap != null)
            for (int i = 0; i < attributesMap.getLength(); i++) {
                Node attribute = attributesMap.item(i);
                addAttribute(attribute.getNodeName(), StringEscapeUtils.unescapeXml(attribute.getNodeValue()));
            }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("#text")) {
                String content = child.getNodeValue();
                if (content != null && content.trim().length() > 0)
                    setContent(Base64.decode(child.getNodeValue().trim()));
            } else {
                XMLNode childNode = new XMLNode("?", this);
                childNode.loadFromXMLNode(child);
                addChild(childNode);
            }
        }
    }

    public static String escapeXml(String s) {
        s = StringEscapeUtils.escapeXml(s);
        StringBuffer dest = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (XMLChar.isValid(c)) {
                dest.append(c);
            }
        }
        return dest.toString();
    }
}
