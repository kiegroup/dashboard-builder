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
import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.workspace.export.ExportManager;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Bundle converter for workspaces exported as XML files.
 */
public class WorkspaceFileConverter extends XmlToBundleConverter {

    /** Logger */
    protected Logger log = LoggerFactory.getLogger(KpisFileConverter.class);

    @Inject
    protected ExportManager exportManager;

    public Map<Locale,Properties> extract() throws Exception {
        Map<Locale,Properties> bundles = new HashMap<Locale, Properties>();
        if (xmlFile != null && xmlFile.exists()) {
            ImportResult result = exportManager.loadXML(xmlFile.getName(), new FileInputStream(xmlFile));

            if (result.getException() != null) throw result.getException();
            if (result.getWarnings() != null && result.getWarnings().size() > 0) {
                for (int j = 0; j < result.getWarnings().size(); j++) {
                    log.warn("Problems importing entry " + result.getEntryName() + ": " + result.getWarnings().get(j));
                }
            }

            String parentKey = null;
            XMLNode node = result.getRootNode();
            processNode(node, parentKey, bundles);
        }
        return bundles;
    }

    protected void processNode(XMLNode node, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        String key = updateKey(node, parentKey);
        if (node.getObjectName().equals("rawcontent")) {
            ByteArrayInputStream is = new ByteArrayInputStream(node.getContent());
            ObjectInputStream ois = new ObjectInputStream(is);
            Map<String,String> map = (Map<String,String>) ois.readObject();
            for (String lang : map.keySet()) {
                String value = map.get(lang);
                getBundle(bundles, new Locale(lang)).setProperty(key, value);
            }
        }
        else {
            Properties nodeProps = node.getAttributes();
            String language = nodeProps.getProperty("lang");
            if (language == null) language = nodeProps.getProperty("language");
            if (!StringUtils.isBlank(language)) {
                // i18n node found
                String value = nodeProps.getProperty("value");
                if (value == null) value = new String(node.getContent());
                getBundle(bundles, new Locale(language)).setProperty(key, value);
            }
            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                XMLNode child = (XMLNode) it.next();
                processNode(child, key, bundles);
            }
        }
    }

    protected String updateKey(XMLNode node, String parentKey) {
        Properties nodeProps = node.getAttributes();
        String nodeName = node.getObjectName();
        if (nodeName.equals("workspace")) {
            String id = nodeProps.getProperty("id");
            return (parentKey == null ? "" : parentKey + ".") + "workspace." + id;
        }
        if (nodeName.equals("panelInstance")) {
            String id = nodeProps.getProperty("id");
            return (parentKey == null ? "" : parentKey + ".") + "panelInstance." + id;
        }
        if (nodeName.equals("section")) {
            String id = nodeProps.getProperty("id");
            return (parentKey == null ? "" : parentKey + ".") + "section." + id;
        }
        if (nodeName.equals("param")) {
            String id = nodeProps.getProperty("name");
            return (parentKey == null ? "" : parentKey + ".") + "param." + id;
        }
        if (nodeName.equals("rawcontent")) {
            return (parentKey == null ? "" : parentKey + ".") + "rawcontent";
        }
        return parentKey;
    }

    public void inject(Map<Locale,Properties> bundles) throws Exception {

    }
}
