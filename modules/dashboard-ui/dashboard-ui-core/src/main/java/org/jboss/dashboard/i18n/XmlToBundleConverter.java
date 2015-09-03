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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.*;
import java.util.*;

/**
 * Base class that defines that provides some conversion services between classic resource bundles and dashbuilder's
 * i18n resources such as: KPIs or Workspace export files.
 */
public abstract class XmlToBundleConverter {

    public File xmlFile = null;
    public File bundleDir = null;
    public String bundleName = "messages";

    public abstract Map<Locale,Properties> extract() throws Exception;
    public abstract void injectNode(Element node, Locale locale, String value) throws Exception;
    public abstract List<Element> lookupNodes(Element node, List<String> path) throws Exception;

    public Properties getBundle(Map<Locale,Properties> bundles, Locale l) {
        Properties bundle = bundles.get(l);
        if (bundle == null) bundles.put(l, bundle = new Properties());
        return bundle;
    }

    public Map<Locale,Properties> read() throws Exception {
        Map<Locale,Properties> result = new HashMap<Locale, Properties>();
        File[] bundles = new DirectoriesScanner("properties").findFiles(bundleDir);
        for (File f : bundles) {
            Locale locale = extractLocale(StringUtils.remove(f.getName(), ".properties"));
            if (locale != null) {
                Properties bundle = new Properties();
                bundle.load(new FileReader(f));
                result.put(locale, bundle);
            }
        }
        return result;
    }

    protected Locale extractLocale(String bundleName) {
        if (StringUtils.isBlank(bundleName)) return null;
        String temp = bundleName;
        int last = temp.lastIndexOf("_");
        if (last == -1) return null;
        String token2 = temp.substring(last+1);

        temp = temp.substring(0, last);
        last = temp.lastIndexOf("_");
        if (last == -1) return new Locale(token2);
        String token1 = temp.substring(last+1);
        return new Locale(token1, token2);
    }

    public void write(Map<Locale,Properties> bundles) throws Exception {
        if (bundleDir == null || !bundleDir.isDirectory()) {
            throw new IllegalArgumentException("It's not a directory: " + bundleDir);
        }
        for (Locale locale : bundles.keySet()) {
            Properties bundle = bundles.get(locale);
            File outputFile = new File(bundleDir, bundleName + "_" + locale.toString() + ".properties");
            bundle.store(new FileWriter(outputFile), null);
        }
    }

    public Element lookupNode(Document doc, Locale locale, String bundleKey) throws Exception {
        List<String> keyList = new ArrayList<String>(Arrays.asList(StringUtils.split(bundleKey, ".")));
        List<Element> nodes = lookupNodes(doc.getRootElement(), keyList);

        if (nodes.isEmpty()) throw new RuntimeException("Node not found: " + bundleKey);
        Element node = nodes.get(0);
        if (nodes.size() == 1 && getLanguageAttr(node) == null) return node;

        // Get the node for the specified locale.
        node = getElementForLocale(nodes, locale);
        if (node != null) return node;

        // Node not found for the target language. Create a brand new one.
        Element sibling = nodes.get(0);
        Element parent = sibling.getParentElement();
        node = (Element) sibling.clone();
        Attribute targetAttrLang = getLanguageAttr(node);
        targetAttrLang.setValue(locale.getLanguage());
        int indexOfElement = parent.indexOf(sibling);
        parent.addContent(indexOfElement+1, node);
        return node;
    }

    public void inject(Map<Locale,Properties> bundles) throws Exception {
        if (xmlFile != null && xmlFile.exists()) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(xmlFile);

            // Inject the i18n properties.
            for (Locale locale : bundles.keySet()) {
                Properties bundle = bundles.get(locale);
                for (String key : bundle.stringPropertyNames()) {
                    Element node = lookupNode(doc, locale, key);
                    if (node != null) {
                        String value = bundle.getProperty(key);
                        injectNode(node, locale, value);
                    }
                }
            }

            // Serialize the updated XML doc to file.
            Format format = Format.getPrettyFormat();
            format.setIndent("  ");
            format.setEncoding("UTF-8");
            FileOutputStream fos = new FileOutputStream(xmlFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

            // Escape all the strings to an ASCII neutral encoding.
            XMLOutputter outp = new XMLOutputter(format) {
                public String escapeAttributeEntities(String str) {
                    return StringEscapeUtils.escapeXml(str);
                }
                public String escapeElementEntities(String str) {
                    return StringEscapeUtils.escapeXml(str);
                }
            };
            outp.output(doc, osw);
            fos.close();
        }
    }

    protected Element getElementForLocale(List<Element> elements, Locale l) {
        for (Element element : elements) {
            Attribute attrLang = getLanguageAttr(element);
            if (attrLang == null) continue;

            String lang = attrLang.getValue();
            if (lang.equals(l.getLanguage())) return element;
        }
        return null;
    }

    protected Attribute getLanguageAttr(Element element) {
        Attribute langAttr = element.getAttribute("lang");
        if (langAttr == null) langAttr = element.getAttribute("language");
        return langAttr;
    }
}
