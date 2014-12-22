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
package org.jboss.dashboard.workspace.export;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.export.structure.CreateResult;
import org.jboss.dashboard.workspace.export.structure.ExportResult;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *  Workspace import/export manager.
 */
@ApplicationScoped
public class ExportManager {

    private static transient Logger log = LoggerFactory.getLogger(ExportManager.class.getName());
    public static final String WORKSPACE_EXTENSION = "workspace";

    @Inject
    private WorkspaceBuilder workspaceBuilder;

    @Inject
    private ExportVisitor exportVisitor;

    @Inject @Config("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    private String xmlHeader;

    @Inject @Config(WORKSPACE_EXTENSION)
    private String[] allowedEntryExtensions;

    public String getXmlHeader() {
        return xmlHeader;
    }

    public void setXmlHeader(String xmlHeader) {
        this.xmlHeader = xmlHeader;
    }

    public String[] getAllowedEntryExtensions() {
        return allowedEntryExtensions;
    }

    public void setAllowedEntryExtensions(String[] allowedEntryExtensions) {
        this.allowedEntryExtensions = allowedEntryExtensions;
    }

    /**
     * Export workspaces and graphic resources
     */
    public ExportResult export(ExportData data) throws Exception {
        GraphicElement[] resources = data.getResourcesToExport();
        Workspace[] workspaces = data.getWorkspacesToExport();
        try {
            if (resources != null)
                for (int i = 0; i < resources.length; i++) {
                    GraphicElement resource = resources[i];
                    resource.acceptVisit(exportVisitor);
                }
            if (workspaces != null)
                for (int i = 0; i < workspaces.length; i++) {
                    Workspace workspace = workspaces[i];
                    workspace.acceptVisit(exportVisitor);
                }
        }
        catch (Exception e) {
            log.error("Error: ", e);
            return new ExportResult(e, this);
        }
        return new ExportResult(exportVisitor.getRootNode(), this);
    }

    /**
     * Load an export definition from given stream
     *
     * @param is Stream to a zipped file containing an export result
     * @return An array of ImportResult objects, representing the objects inside the export file.
     */
    public ImportResult[] load(InputStream is) {
        ArrayList list = new ArrayList();
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry zEntry = null;
            while ((zEntry = zis.getNextEntry()) != null) {
                boolean entryAllowed = false;
                String entryName = null;
                for (int i = 0; i < allowedEntryExtensions.length && !entryAllowed; i++) {
                    String allowedEntryExtension = allowedEntryExtensions[i].toLowerCase();
                    if (zEntry.getName().toLowerCase().endsWith("." + allowedEntryExtension)) {
                        entryAllowed = true;
                        entryName = zEntry.getName().substring(0, zEntry.getName().length() - 1 - allowedEntryExtension.length());
                    }
                }
                if (entryAllowed) {
                    log.debug("Reading entry " + entryName);
                    list.add(loadXML(entryName, zis));
                }
            }
        } catch (IOException e) {
            log.error("Error:", e);
            return null;
        }
        try {
            is.close();
        } catch (IOException e) {
            log.error("Error:", e);
        }
        return (ImportResult[]) list.toArray(new ImportResult[list.size()]);
    }

    public ImportResult loadXML(String name, InputStream is) {
        return new ImportResult(name, is);
    }

    /**
     * Create the objects represented by given results. Objects are filtered by
     * the indexes parameter.
     * The indexes parameter filters in the following way:<ul>
     * <li> If it is null, consider all the results
     * <li> For every int[] in the list, add the results represented by this int[].<ul>
     * <li>If this int[] is null or empty, ignore
     * <li>If it contains only one int, it is the index inside results that will be created (all its children).
     * <li>If it contains more than one, the first one is the index inside results that will be created,
     * and the following ones are the child indexes.
     * </ul>
     * </ul>
     *
     * @param results import results to create.
     * @param indexes filter for the results array
     * @return an array of create results, representing the operation result.
     */
    public CreateResult[] create(ImportResult[] results, int[][] indexes) {
        return create(results, indexes, false);
    }

    public CreateResult[] create(ImportResult[] results, int[][] indexes, boolean onStartup) {
        List elementsToCreate = new ArrayList();
        List attributesForCreation = new ArrayList();
        if (indexes == null) {
            for (int i = 0; i < results.length; i++) {
                ImportResult result = results[i];
                elementsToCreate.addAll(result.getRootNode().getChildren());
                while (attributesForCreation.size() < elementsToCreate.size())
                    attributesForCreation.add(result.getAttributes());
            }
        } else {
            for (int i = 0; i < indexes.length; i++) {
                int[] index = indexes[i];
                if (index != null)
                    if (index.length == 1) {
                        ImportResult result = results[index[0]];
                        elementsToCreate.addAll(result.getRootNode().getChildren());
                        while (attributesForCreation.size() < elementsToCreate.size())
                            attributesForCreation.add(result.getAttributes());
                    } else if (index.length > 1) {
                        ImportResult result = results[index[0]];
                        for (int j = 1; j < index.length; j++) {
                            int idx = index[j];
                            elementsToCreate.add(result.getRootNode().getChildren().get(idx));
                            while (attributesForCreation.size() < elementsToCreate.size())
                                attributesForCreation.add(result.getAttributes());
                        }
                    }
            }
        }
        List createResults = new ArrayList();
        for (int i = 0; i < elementsToCreate.size(); i++) {
            XMLNode nodeToCreate = (XMLNode) elementsToCreate.get(i);
            Map attributes = (Map) attributesForCreation.get(i);
            if (ExportVisitor.WORKSPACE.equals(nodeToCreate.getObjectName())
                    ||
                    ExportVisitor.RESOURCE.equals(nodeToCreate.getObjectName())
                    ) {
                CreateResult result = workspaceBuilder.create(nodeToCreate, attributes, onStartup);
                createResults.add(result);
            }
        }

        return (CreateResult[]) createResults.toArray(new CreateResult[createResults.size()]);
    }
}
