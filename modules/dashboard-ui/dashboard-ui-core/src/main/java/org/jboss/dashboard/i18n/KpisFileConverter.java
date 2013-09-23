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
package org.jboss.dashboard.i18n;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Bundle converter for KPIs exported as XML files.
 */
public class KpisFileConverter extends XmlToBundleConverter {

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
        Attribute langAttr = node.getAttribute("language");
        if (langAttr != null && !StringUtils.isBlank(langAttr.getValue())) {
            Locale l = new Locale(langAttr.getValue());
            String value = node.getText();
            getBundle(bundles, l).setProperty(key + "." + node.getName(), value);
        }
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            extractNode(child, key, bundles);
        }
    }

    protected String calculateKey(Element node, String parentKey) {
        String nodeName = node.getName();
        if (nodeName.equals("dataprovider")) {
            String id = node.getAttribute("code").getValue();
            return (parentKey == null ? "" : parentKey + ".") + id;
        }
        if (nodeName.equals("dataproperty")) {
            String id = node.getAttribute("id").getValue();
            return (parentKey == null ? "" : parentKey + ".") + id;
        }
        if (nodeName.equals("kpi")) {
            String id = node.getAttribute("code").getValue();
            return (parentKey == null ? "" : parentKey + ".") + id;
        }
        if (nodeName.equals("domain") || nodeName.equals("range") || nodeName.equals("groupby")) {
            return (parentKey == null ? "" : parentKey + ".") + nodeName;
        }
        if (nodeName.equals("column")) {
            String id = node.getChild("viewindex").getText();
            return (parentKey == null ? "" : parentKey + ".") + "column." + id;
        }
        return parentKey;
    }

    public List<Element> lookupNodes(Element node, List<String> path) throws Exception {
        if (path.isEmpty()) return new ArrayList<Element>();

        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            Attribute lang = child.getAttribute("language");

            if (child.getName().equals("dataprovider") || child.getName().equals("kpi")) {
                String targetCode = path.get(0);
                String code = child.getAttributeValue("code");
                if (targetCode.equals(code)) {
                    path.remove(0);
                    return lookupNodes(child, path);
                }
            } else if (child.getName().equals("dataproperty")) {
                String propId = path.get(0);
                String id = child.getAttributeValue("id");
                if (propId.equals(id)) {
                    path.remove(0);
                    return lookupNodes(child, path);
                }
            } else if (child.getName().equals("domain") ||
                    child.getName().equals("range") ||
                    child.getName().equals("groupby")) {
                String propId = path.get(0);
                if (propId.equals(child.getName())) {
                    path.remove(0);
                    return lookupNodes(child, path);
                }
            } else if (child.getName().equals("column")) {
                String propId = path.get(0);
                if (propId.equals(child.getName())) {
                    String columnIdx = path.get(1);
                    Element viewIndexEl = child.getChild("viewindex");
                    if (columnIdx.equals(viewIndexEl.getTextTrim())) {
                        path.remove(0);
                        path.remove(0);
                        return lookupNodes(child, path);
                    }
                }
            } else if (lang != null) {
                String nodeName = path.get(0);
                if (nodeName.equals(child.getName())) {
                    path.remove(0);
                    return child.getParentElement().getChildren(nodeName);
                }
            } else if (child.getName().equals("displayer") ||
                       child.getName().equals("dataproperties")) {
                return lookupNodes(child, path);
            }
        }
        return new ArrayList<Element>();
    }

    public void injectNode(Element node, Locale locale, String value) throws Exception {
        node.setText(value);
    }
}
