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
package org.jboss.dashboard.i18n;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.commons.text.Base64;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Bundle converter for workspaces exported as XML files.
 */
public class WorkspaceFileConverter extends XmlToBundleConverter {

    /** Logger */
    protected Logger log = LoggerFactory.getLogger(KpisFileConverter.class);

    public Map<Locale,Properties> extract() throws Exception {
        Map<Locale,Properties> bundles = new HashMap<Locale, Properties>();
        if (xmlFile != null && xmlFile.exists()) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(xmlFile);

            String parentKey = null;
            Element node = doc.getRootElement();
            extractNode(node, parentKey, bundles);
        }
        return bundles;
    }

    protected void extractNode(Element node, String parentKey, Map<Locale, Properties> bundles) throws Exception {
        String key = calculateKey(node, parentKey);
        if (node.getName().equals("rawcontent")) {
            Map<String,String> map = parseRawContent(node);
            for (String lang : map.keySet()) {
                String value = map.get(lang);
                getBundle(bundles, new Locale(lang)).setProperty(key, value);
            }
        }
        else {
            Attribute langAttr = node.getAttribute("lang");
            if (langAttr != null && !StringUtils.isBlank(langAttr.getValue())) {
                Locale l = new Locale(langAttr.getValue());
                if (node.getName().equals("param")) {
                    String value = node.getAttributeValue("value");
                    getBundle(bundles, l).setProperty(key, value);
                }
            }
            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                Element child = (Element) it.next();
                extractNode(child, key, bundles);
            }
        }
    }

    protected String calculateKey(Element node, String parentKey) {
        String nodeName = node.getName();
        if (nodeName.equals("workspace")) {
            String id = node.getAttributeValue("id");
            return (parentKey == null ? "" : parentKey + ".") + "workspace." + id;
        }
        if (nodeName.equals("panelInstance")) {
            String id = node.getAttributeValue("id");
            return (parentKey == null ? "" : parentKey + ".") + "panelInstance." + id;
        }
        if (nodeName.equals("section")) {
            String id = node.getAttributeValue("id");
            return (parentKey == null ? "" : parentKey + ".") + "section." + id;
        }
        if (nodeName.equals("param")) {
            String id = node.getAttributeValue("name");
            return (parentKey == null ? "" : parentKey + ".") + "param." + id;
        }
        if (nodeName.equals("rawcontent")) {
            return (parentKey == null ? "" : parentKey + ".") + nodeName;
        }
        return parentKey;
    }

    public List<Element> lookupNodes(Element node, List<String> path) throws Exception {
        if (path.isEmpty()) return new ArrayList<Element>();

        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();

            if (child.getName().equals("workspaceExport")) {
                return lookupNodes(child, path);
            }
            else if (child.getName().equals("workspace") ||
                    child.getName().equals("panelInstance") ||
                    child.getName().equals("section")) {

                String nodeName = path.get(0);
                if (nodeName.equals(child.getName())) {
                    String targetId = path.get(1);
                    String id = child.getAttributeValue("id");
                    if (targetId.equals(id)) {
                        path.remove(0);
                        path.remove(0);
                        return lookupNodes(child, path);
                    }
                }
            }
            else if (child.getName().equals("param")) {
                String nodeName = path.get(0);
                if (nodeName.equals(child.getName())) {
                    String param = path.get(1);
                    String targetParam = child.getAttributeValue("name");
                    if (param.equals(targetParam)) {
                        path.remove(0);
                        path.remove(0);
                        List<Element> results = new ArrayList<Element>();
                        Iterator it2 = node.getChildren(nodeName).iterator();
                        while (it2.hasNext()) {
                            Element child2 = (Element) it2.next();
                            String id = child2.getAttributeValue("name");
                            if (id.equals(targetParam)) results.add(child);
                        }
                        return results;
                    }
                }
            }
            else if (child.getName().equals("rawcontent")) {
                String nodeName = path.get(0);
                if (nodeName.equals(child.getName())) {
                    path.remove(0);
                    List<Element> results = new ArrayList<Element>();
                    results.add(child);
                    return results;
                }
            }
        }
        return new ArrayList<Element>();
    }

    public void injectNode(Element node, Locale locale, String value) throws Exception {
        if (node.getName().equals("rawcontent")) {
            Map<String,String> map = parseRawContent(node);
            map.put(locale.getLanguage(), value);
            node.setText(formatRawContent(map));
        }
        else if (node.getName().equals("param")) {
            Attribute targetAttrValue = node.getAttribute("value");
            targetAttrValue.setValue(value);
        }
    }

    protected Map<String,String> parseRawContent(Element element) throws Exception {
        if (!element.getName().equals("rawcontent")) throw new RuntimeException("XML node is not <rawcontent> " + element);

        ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(element.getTextTrim()));
        ObjectInputStream ois = new ObjectInputStream(is);
        return (Map<String,String>) ois.readObject();
    }

    protected String formatRawContent(Map<String,String> map) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(map);
        byte[] content = bos.toByteArray();
        return Base64.encode(content);
    }
}
