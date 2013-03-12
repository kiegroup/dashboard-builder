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
<%@ page import="org.jboss.dashboard.ui.SessionManager,
                 java.util.Arrays,
                 java.util.Set" %>
<%@ page import="org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler"%>
<%@ page import="org.jboss.dashboard.ui.resources.GraphicElementPreview"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%
    GraphicElementPreview preview = (GraphicElementPreview) request.getSession().getAttribute(ResourcesPropertiesHandler.PREVIEW_ATTRIBUTE);
    String graphicElement = (String) request.getAttribute("graphicElement");
    Set resourcesSet = preview.getResources();
    String[] resources = (String[]) resourcesSet.toArray(new String[resourcesSet.size()]);
    Arrays.sort(resources);
%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<tr class="skn-odd_row">
    <td nowrap="nowrap" align="left"><i18n:message key='<%="ui.admin.workarea."+graphicElement+"s.preview"%>'>
        !!!Preview</i18n:message></td>
    <td align="center">

            <%for (int i = 0; i < resources.length; i++) {
                String resource = resources[i];
                if (resource!=null && !"".equals(resource) && "IMG".equals(resource)) {
            %>
        <resource:image category="layoutPreview" title="<%=resource%>" categoryId="<%=preview.getId()%>"
                              resourceId="<%=resource%>"></resource:image>
        <%
                }
            }
        %>

    </td>
</tr>
