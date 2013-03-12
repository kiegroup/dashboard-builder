<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jboss.dashboard.ui.resources.ResourceGallery,
                 java.util.Arrays,
                 java.util.Set" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    ResourceGallery gallery = (ResourceGallery) request.getAttribute("previewElement");
    Set resourcesSet = gallery.getResources();
    String[] resources = (String[]) resourcesSet.toArray(new String[resourcesSet.size()]);
    Arrays.sort(resources);
%>
<p style="width:100%; height:100%; overflow:auto; border:solid; border-width:1px; border-color:#666666;" align="center">
    <%for (int i = 0; i < resources.length; i++) {
        String resource = resources[i];
    %><resource:image category="resourceGallery" workspaceId="<%=gallery.getWorkspaceId()%>"
                      sectionId="<%=gallery.getSectionId()%>" panelId="<%=gallery.getPanelId()%>"
                      title="<%=resource%>" categoryId="<%=gallery.getId()%>" resourceId="<%=resource%>"
                      useDefaults="false"></resource:image>&nbsp;<%
    }
%>
</p>