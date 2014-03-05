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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.workspace.export.Visitable;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.ui.resources.Skin;

import java.util.Map;
import java.util.Set;

/**
 * A workspace.
 */
public interface Workspace extends Visitable {

    /**
     * Give priority in home search to home sections assigned to roles over other sections.
     */
    int SEARCH_MODE_ROLE_HOME_PREFERENT = 0;

    /**
     * Current section has preference over other sections.
     */
    int SEARCH_MODE_CURRENT_SECTION_PREFERENT = 1;


    String getSkinId();

    void setSkinId(String lookId);

    Skin getSkin();

    String getEnvelopeId();

    void setEnvelopeId(String envelope);

    Envelope getEnvelope();

    int getSectionsCount();

    Section getSection(Long id);

    boolean existsSection(Long id);

    Map getTitle();

    Map getName();

    void setName(Map name);

    void setTitle(Map description);

    void setTitle(String title, String lang);

    String getId();

    void setId(String id);

    boolean equals(Object obj);

    int hashCode();

    String toString();

    Set<String> getPanelProvidersAllowed();

    void setPanelProvidersAllowed(Set<String> s);

    void addPanelProviderAllowed(String id);

    void removePanelProviderAllowed(String id);

    boolean isProviderAllowed(String id);

    String getFriendlyUrl();

    void setFriendlyUrl(String s);

    boolean getDefaultWorkspace();

    void setDefaultWorkspace(boolean b);

    int getHomeSearchMode();

    Set<WorkspaceHome> getWorkspaceHomes();
}
