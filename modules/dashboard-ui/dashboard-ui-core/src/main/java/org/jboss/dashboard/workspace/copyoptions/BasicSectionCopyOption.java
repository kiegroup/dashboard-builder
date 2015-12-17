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
package org.jboss.dashboard.workspace.copyoptions;

import java.util.Hashtable;

/**
 * This class specifies options to copy a Section. It stores which panelInstances are to
 * be copied and which ones are not.
 */
public class BasicSectionCopyOption extends CopyOption implements SectionCopyOption {

    protected boolean defaultDuplicationPolicy;
    protected Hashtable panelInstancesToDuplicate = new Hashtable();

    /**
     * Creates a new BasicSectionCopyOption with given default duplication policy.
     *
     * @param duplicateByDefault Indicates if default behavior is to duplicate panelInstances or not.
     */
    public BasicSectionCopyOption(boolean duplicateByDefault) {
        defaultDuplicationPolicy = duplicateByDefault;
    }

    /**
     * Determines if given panelInstance has to be duplicated or not.
     *
     * @param panelInstanceId
     * @return
     */
    public boolean isDuplicatePanelInstance(String panelInstanceId) {
        Boolean b = (Boolean) panelInstancesToDuplicate.get(panelInstanceId);
        if (b == null)
            return defaultDuplicationPolicy;
        return b.booleanValue();
    }

    /**
     * Adds a panelInstanceId to duplicate.
     *
     * @param panelInstance PanelInstance Id that is specified to be copied or not.
     * @param duplicate       Indicates if this PanelInstance Id must be registered as to be duplicated or not.
     */
    public void addPanelInstanceToDuplicate(String panelInstance, boolean duplicate) {
        panelInstancesToDuplicate.put(panelInstance, duplicate);
    }
}
