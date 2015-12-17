<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
<%@ page import="org.jboss.dashboard.workspace.Panel"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

  <mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderPanelContentFormatter">
    <mvc:fragment name="minimized">
        <table cellspacing="0" cellpadding="0" width="100%" height="100%" border="0">
            <tr>
                <td width="100%" valign="top"></td>
            </tr>
        </table>
    </mvc:fragment>
    <mvc:fragment name="outputStart">
        <table class="<mvc:fragmentValue name="tableClass"/>" cellspacing="0" cellpadding="0" width="100%">
        <tr>
        <td width="100%"  height="100%" valign="top" align="left" id="<mvc:fragmentValue name="panelUID"/>">
    </mvc:fragment>
    <mvc:fragment name="outputNotWellConfigured">
        <center><br>
            <font class="skn-error"><i18n:message key="ui.panel.notWellConfigured"/></font>
            <br></center><br>
    </mvc:fragment>

    <mvc:fragment name="output">
    <mvc:fragmentValue name="jsp" id="jsp">
    <mvc:fragmentValue name="panel" id="panel">
        <% try { %>
            <mvc:include page="/common/panels/commonRefreshFormPanels.jsp"/>
            <mvc:include page="/common/panels/beforePanel.jsp"/>
            <mvc:include page="<%= (String)jsp %>" flush="true" />
        <% } finally { %>
            <mvc:include page="/common/panels/afterPanel.jsp"/>
        <% ((Panel)panel).getProvider().getDriver().fireAfterRenderPanel((Panel)panel, request, response); } %>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </td>
        </tr>
        </table>
    </mvc:fragment>
</mvc:formatter>