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

import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayerManager;
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProviderImpl;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.provider.*;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.message.Message;
import org.jboss.dashboard.commons.message.MessageList;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@ApplicationScoped
public class ImportManagerImpl implements ImportManager {

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    public ImportOptions createImportOptions() {
        return new ImportOptionsImpl();
    }

    public ImportResults createImportResults() {
        return new ImportResultsImpl();
    }

    /**
     * Save the elements (KPI, DataProvider) contained in the import results instance.
     * The ImportResults message list gives us feedback about any problem found while saving.
     * @throws Exception If the specified import results contains ERROR messages.
     */
    public void save(ImportResults importResults) throws Exception {
        saveOrUpdate(importResults, false);
    }

    /**
     * Updates the elements (KPI, DataProvider) contained in the import results instance.
     * The ImportResults message list gives us feedback about any problem found while saving.
     * @throws Exception If the specified import results contains ERROR messages.
     */
    public void update(ImportResults importResults) throws Exception {
        saveOrUpdate(importResults, true);
    }

    /**
     * Saves or update from persistence the elements (KPI, DataProvider) contained in the import results instance.
     */
    protected void saveOrUpdate(ImportResults importResults, boolean update) throws Exception {

        // Get errors.
        Locale locale = LocaleManager.currentLocale();
        MessageList messages = importResults.getMessages();
        if (messages.containsMessagesOfType(Message.ERROR)) {
            Iterator errors = messages.getMessagesOfType(Message.ERROR).iterator();
            while (errors.hasNext()) {
                Message bProcessDescriptorMessage = (Message) errors.next();
                throw new RuntimeException(bProcessDescriptorMessage.getMessage(locale));
            }
        }

        // Save the imported data providers.
        DataProviderManager provMgr = DataDisplayerServices.lookup().getDataProviderManager();
        for (DataProvider newProvider : importResults.getDataProviders()) {
            DataProvider oldProvider = provMgr.getDataProviderByCode(newProvider.getCode());
            if (oldProvider != null) {
                // If the provider is already present in the database then simply replace the references to the old provider.
                importResults.replaceDataProvider(newProvider, oldProvider);
                if (update) {
                    oldProvider.setDataLoader(newProvider.getDataLoader());
                    oldProvider.save();
                    importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.PROVIDER_UPDATED, new Object[] {oldProvider}));
                }
                else {
                    importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.PROVIDER_ALREADY_EXISTS, new Object[] {oldProvider}));
                }
            } else {
                newProvider.save();
                importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.PROVIDER_CREATED, new Object[] {newProvider}));
            }
        }

        // Save the imported KPIs.
        KPIManager kpiMgr = DataDisplayerServices.lookup().getKPIManager();
        for (KPI newKPI : importResults.getKPIs()) {
            KPI oldKPI = kpiMgr.getKPIByCode(newKPI.getCode());
            if (oldKPI != null) {
                // If the KPI is already present in the database then simply replace the references to the old KPI.
                importResults.replaceKPI(newKPI, oldKPI);
                if (update) {
                    oldKPI.setDescriptionI18nMap(newKPI.getDescriptionI18nMap());
                    oldKPI.setDataProvider(newKPI.getDataProvider());
                    oldKPI.setDataDisplayer(newKPI.getDataDisplayer());
                    oldKPI.save();
                    importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.KPI_UPDATED, new Object[] {oldKPI}));
                }
                else {
                    importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.KPI_ALREADY_EXISTS, new Object[] {oldKPI}));
                }
            } else {
                newKPI.save();
                importResults.getMessages().add(new ImportExportMessage(ImportExportMessage.KPI_CREATED, new Object[] {newKPI}));
            }
        }
    }

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML fragment.
     */
    public ImportResults parse(String xml) throws Exception {
        return parse(xml, createImportOptions());
    }

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML stream.
     */
    public ImportResults parse(InputStream xml) throws Exception {
        return parse(xml, createImportOptions());
    }

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML fragment.
     */
    public ImportResults parse(String xml, ImportOptions options) throws Exception {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        dFactory.setIgnoringComments(true);
        StringReader isr = new StringReader(xml);
        Document doc = dBuilder.parse(new InputSource(isr));
        isr.close();
        return parse(doc.getChildNodes(), options);
    }

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML stream.
     */
    public ImportResults parse(InputStream xml, ImportOptions options) throws Exception {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
        dFactory.setIgnoringComments(true);
        Document doc = dBuilder.parse(xml);
        return parse(doc.getChildNodes(), options);
    }

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML nodes.
     */
    public ImportResults parse(NodeList xmlNodes, ImportOptions options) throws Exception {
        ImportResults results = createImportResults();
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node root = xmlNodes.item(0);
            if (!options.ignoreDataProviders()) parseProviders(root.getChildNodes(), results);
            if (!options.ignoreKPIs()) parseKPIs(root.getChildNodes(), results);
        }
        return results;
    }

    protected void parseKPIs(NodeList xmlNodes, ImportResults results) throws Exception {
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node item = xmlNodes.item(i);
            if (item.getNodeName().equals("kpi")) {
                try {
                    KPI kpi = DataDisplayerServices.lookup().getKPIManager().createKPI();
                    Node codeNode = item.getAttributes().getNamedItem("code");
                    if (codeNode != null) kpi.setCode(StringEscapeUtils.unescapeXml(codeNode.getNodeValue()));

                    NodeList subNodes = item.getChildNodes();
                    for (int j = 0; j < subNodes.getLength(); j++) {
                        item = subNodes.item(j);

                        // Description
                        if (item.getNodeName().equals("description") && item.hasChildNodes()) {
                            String description = item.getFirstChild().getNodeValue();
                            Locale locale = LocaleManager.currentLocale();
                            Node languageNode = item.getAttributes().getNamedItem("language");
                            if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                            kpi.setDescription(StringEscapeUtils.unescapeXml(description), locale);
                        }

                        // Provider
                        if (item.getNodeName().equals("provider") && item.hasAttributes()) {
                            String providerCode = item.getAttributes().getNamedItem("code").getNodeValue();
                            DataProvider provider = results.getDataProviderByCode(providerCode);
                            if (provider == null) provider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(providerCode);
                            if (provider == null) {
                                results.getMessages().add(new ImportExportMessage(ImportExportMessage.PROVIDER_CODE_NOT_FOUND, new Object[] {providerCode}));
                                throw new RuntimeException("Continue with the next KPI...");
                            }
                            kpi.setDataProvider(provider);
                        }

                        // Displayer
                        if (item.getNodeName().equals("displayer") && item.hasAttributes() && item.hasChildNodes()) {
                            String typeUid = item.getAttributes().getNamedItem("type").getNodeValue();
                            DataDisplayerType type = dataDisplayerManager.getDisplayerTypeByUid(typeUid);
                            if (type == null) {
                                results.getMessages().add(new ImportExportMessage(ImportExportMessage.DISPLAYER_TYPE_NOT_FOUND, new Object[] {typeUid}));
                                throw new RuntimeException("Continue with the next KPI...");
                            }
                            DataDisplayerRenderer renderer = null;
                            Node rendererNode = item.getAttributes().getNamedItem("renderer");
                            if (rendererNode != null) {
                                String rendUid = rendererNode.getNodeValue();
                                renderer = dataDisplayerManager.getDisplayerRendererByUid(rendUid);
                                if (renderer == null) {
                                    results.getMessages().add(new ImportExportMessage(ImportExportMessage.DISPLAYER_RENDERER_NOT_FOUND, new Object[] {rendUid}));
                                    throw new RuntimeException("Continue with the next KPI...");
                                }
                            }
                            DataDisplayer displayer = type.getXmlFormat().parse(subNodes, results);
                            if (results.getMessages().hasErrors()) {
                                throw new Exception(results.getMessages().get(0).toString());
                            }

                            displayer.setDataDisplayerType(type);
                            displayer.setDataDisplayerRenderer(renderer);
                            kpi.setDataDisplayer(displayer);
                        }
                    }
                    results.addKPI(kpi);
                } catch (Exception e) {
                    // Continue with the next KPI...
                }
            }
        }
    }

    public void parseProviders(NodeList xmlNodes, ImportResults results) throws Exception {
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node item = xmlNodes.item(i);
            if (item.getNodeName().equals("dataprovider")) {

                // Get the provider type module by class.
                String uid = item.getAttributes().getNamedItem("type").getNodeValue();
                DataProviderType type = DataDisplayerServices.lookup().getDataProviderManager().getProviderTypeByUid(uid);
                if (type == null) {
                    results.getMessages().add(new ImportExportMessage(ImportExportMessage.PROVIDER_TYPE_NOT_FOUND, new Object[] {uid}));
                    continue;
                }

                // Parse the provider instance custom part.
                NodeList subNodes = item.getChildNodes();
                DataLoader loader = type.getXmlFormat().parse(subNodes);
                if (results.getMessages().hasErrors()) {
                    throw new Exception(results.getMessages().get(0).toString());
                }
                loader.setDataProviderType(type);

                // Get the provider instance common properties.
                DataProviderImpl provider = new DataProviderImpl();
                provider.setDataLoader(loader);
                Node codeNode = item.getAttributes().getNamedItem("code");
                if (codeNode != null) provider.setCode(StringEscapeUtils.unescapeXml(codeNode.getNodeValue()));
                for (int j = 0; j < subNodes.getLength(); j++) {
                    item = subNodes.item(j);

                    if (item.getNodeName().equals("canEdit") && item.hasChildNodes()) {
                        String canEditStr = item.getFirstChild().getNodeValue();
                        if ("false".equalsIgnoreCase(canEditStr)) provider.canEdit = false;
                    }

                    if (item.getNodeName().equals("canEditProperties") && item.hasChildNodes()) {
                        String canEditPropertiesStr = item.getFirstChild().getNodeValue();
                        if ("false".equalsIgnoreCase(canEditPropertiesStr)) provider.canEditProperties = false;
                    }

                    if (item.getNodeName().equals("canDelete") && item.hasChildNodes()) {
                        String canDelete = item.getFirstChild().getNodeValue();
                        if ("false".equalsIgnoreCase(canDelete)) provider.canDelete = false;
                    }

                    if (item.getNodeName().equals("description") && item.hasChildNodes()) {
                        String description = item.getFirstChild().getNodeValue();
                        Locale locale = LocaleManager.currentLocale();
                        Node languageNode = item.getAttributes().getNamedItem("language");
                        if (languageNode != null) locale = new Locale(languageNode.getNodeValue());
                        provider.setDescription(StringEscapeUtils.unescapeXml(description), locale);
                    }

                    if (item.getNodeName().equals("dataproperties") && item.hasChildNodes()) {
                        provider.getDataSet().parseXMLProperties(item.getChildNodes());
                    }
                }

                // Register the imported provider.
                results.addDataProvider(provider);
            }
        }
    }
}
