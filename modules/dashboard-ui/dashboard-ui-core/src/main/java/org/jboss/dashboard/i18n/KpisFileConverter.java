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
            processNode(node, parentKey, bundles);
        }
        return bundles;
    }

    protected void processNode(Element node, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        String key = updateKey(node, parentKey);
        Attribute langAttr = node.getAttribute("language");
        if (langAttr != null && !StringUtils.isBlank(langAttr.getValue())) {
            Locale l = new Locale(langAttr.getValue());
            String value = node.getText();
            getBundle(bundles, l).setProperty(key + "." + node.getName(), value);
        }
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            processNode(child, key, bundles);
        }
    }

    protected String updateKey(Element node, String parentKey) {
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

    public void inject(Map<Locale,Properties> bundles) throws Exception {

    }
}
