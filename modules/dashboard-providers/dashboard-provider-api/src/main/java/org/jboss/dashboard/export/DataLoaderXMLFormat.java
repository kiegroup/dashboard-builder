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
package org.jboss.dashboard.export;

import org.jboss.dashboard.provider.DataLoader;
import org.w3c.dom.NodeList;
import java.io.PrintWriter;

/**
 * Interface for the marshalling/unmarshalling in XML format of a data loader instance.
 */
public interface DataLoaderXMLFormat {

    DataLoader parse(String xml) throws Exception;
    DataLoader parse(NodeList xmlNodes) throws Exception;
    String format(DataLoader loader) throws Exception;
    void format(DataLoader loader, PrintWriter out, int indent) throws Exception;
}
