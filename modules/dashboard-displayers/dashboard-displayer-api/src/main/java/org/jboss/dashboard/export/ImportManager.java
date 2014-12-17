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

import org.w3c.dom.NodeList;

/**
 * Dashboard import manager.
 */
public interface ImportManager {

    ImportOptions createImportOptions();
    ImportResults createImportResults();

    /**
     * Save the elements (KPI, DataProvider) contained in the import results instance.
     * The ImportResults message list gives us feedback about any problem found while saving.
     * @throws Exception If the specified import results contains ERROR messages.
     */
    void save(ImportResults importResults) throws Exception;

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML fragment.
     */
    ImportResults parse(String xml) throws Exception;

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML stream.
     */
    ImportResults parse(InputStream xml) throws Exception;

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML fragment.
     */
    ImportResults parse(String xml, ImportOptions options) throws Exception;

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML stream.
     */
    ImportResults parse(InputStream xml, ImportOptions options) throws Exception;

    /**
     * Creates elements (KPI, DataProvider ,..) parsing the specified XML nodes.
     */
    ImportResults parse(NodeList xmlNodes, ImportOptions options) throws Exception;
}
