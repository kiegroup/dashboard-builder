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
import org.jboss.dashboard.commons.text.Base64;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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
            processNode(node, parentKey, bundles);
        }
        return bundles;
    }

    protected void processNode(Element node, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        String key = updateKey(node, parentKey);
        if (node.getName().equals("rawcontent")) {
            ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(node.getText().trim()));
            ObjectInputStream ois = new ObjectInputStream(is);
            Map<String,String> map = (Map<String,String>) ois.readObject();
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
                processNode(child, key, bundles);
            }
        }
    }

    protected String updateKey(Element node, String parentKey) {
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

    public void inject(Map<Locale,Properties> bundles) throws Exception {

    }
}
