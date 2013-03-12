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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.ui.utils.forms.RenderUtils"%>
<%@ page import="org.jboss.dashboard.workspace.PanelProviderParameter"%>
<%@ page import="org.jboss.dashboard.ui.utils.forms.FormStatus"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>


<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.messages" />
<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>

<factory:property property="showPanelConfigComponentFormatter" id="showPanelConfigComponentFormatter">

<mvc:formatter name="<%=showPanelConfigComponentFormatter%>">

    <mvc:fragment name="outputStart">
<form method="POST" action="<factory:formUrl friendly="false"/>" id="<factory:encode name="panelProperties"/>">
    <factory:handler action="saveProperties"/>

    <table  cellspacing="1" cellpadding="4" class="skn-table_border" align="left" width="100%">
    </mvc:fragment>
    <mvc:fragment name="outputParam">
        <mvc:fragmentValue name="param" id="param">
            <mvc:fragmentValue name="html" id="html">
                <mvc:fragmentValue name="formStatus" id="formStatus">
        <tr>
            <td align="left" class="skn-even_row" width="200px" valign="top">
                <%=RenderUtils.field(request, ((PanelProviderParameter)param).getId(), ((PanelProviderParameter)param).getDescription(LocaleManager.currentLocale()), (FormStatus)formStatus, ((PanelProviderParameter)param).isRequired())%>
            </td>
            <td align="left" >
                <%=html%>
            </td>
        </tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        <tr>
            <td colspan="2" align="center">
                <br>
                <input class="skn-button" type="submit" value='<i18n:message key="ui.saveChanges"/>'>
            </td>
        </tr>
    </table>
</form>
    </mvc:fragment>
</mvc:formatter>

</factory:property>