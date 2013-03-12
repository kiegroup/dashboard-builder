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
<%@ page import="org.jboss.dashboard.ui.SessionManager"%>
<%@ page import="java.util.Map"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.factory.FactoryComponentFormatter">
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <br>
        <table cellpadding="4" cellspacing="1" border="0" width="100%" class="skn-table_border">
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="basicPropertiesStart">
        <tr class="skn-table_header">
            <td colspan=4><i18n:message key="ui.admin.workarea.configuration.basicProperties">!!!Basic properties</i18n:message></td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputBasicProperty">
        <tr>
            <td nowrap="NOWRAP" class="skn-even_row">
                <b><mvc:fragmentValue name="propertyName"/></b>
            </td>
            <td colspan=3>
                <mvc:fragmentValue name="propertyValue"/>
            </td>
        </tr>

    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="statusInvalid">
        <tr>
            <td colspan=4 class="skn-error">
                <i18n:message key="ui.admin.workarea.configuration.invalid">!!!Invalid component</i18n:message>
            </td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputDisabled">
        <tr>
            <td colspan=4 class="skn-error">
                <i18n:message key="ui.admin.workarea.configuration.disabled">!!!Disabled component</i18n:message>
            </td>
        </tr>


    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="propertiesStart">
        <tr class="skn-table_header">
            <td colspan="4"><i18n:message key="ui.admin.workarea.configuration.properties">
                !!!Properties</i18n:message></td>
        </tr>
        <tr class="skn-table_header" align="left">
            <td align="left"><i18n:message key="ui.admin.workarea.configuration.type">!!!Type</i18n:message></td>
            <td align="left"><i18n:message key="ui.admin.workarea.configuration.name">!!!Name</i18n:message></td>
            <td align="left"><i18n:message key="ui.admin.workarea.configuration.liveValue">!!!Live value</i18n:message></td>
            <td align="left"><i18n:message key="ui.admin.workarea.configuration.configuredValue">!!!Configured value</i18n:message></td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputProperty">
        <tr class="<mvc:fragmentValue name="estilo"/>">
            <td nowrap="nowrap" width="1px">
                <div style="overflow:hidden;width:100px;height:15px" title="<mvc:fragmentValue name="fullPropertyType"/>"><mvc:fragmentValue name="propertyType"/></div>
            </td>
            <td nowrap="nowrap" width="300px">
                <b><mvc:fragmentValue name="propertyName"/></b>
            </td>
            <td nowrap="nowrap">
                <div style="overflow:hidden;width:150px;height:15px" title="<mvc:fragmentValue name="propertyValue"/>" >
                    <mvc:fragmentValue name="propertyValue"/>
                </div>
            </td>
            <td nowrap="nowrap">
                <div style="overflow:hidden;width:150px;height:15px" title="<mvc:fragmentValue name="configuredValue"/>" >
                    <mvc:fragmentValue name="configuredValue"/>
                </div>
            </td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="filesStart">
        <tr class="skn-table_header">
            <td colspan=4><i18n:message key="ui.admin.workarea.configuration.files">!!!Files used</i18n:message></td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="file">
        <tr>
            <td colspan="4">
                <mvc:fragmentValue name="fileName"/>
            </td>
        </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </table>
        <br><br>
    </mvc:fragment>
</mvc:formatter>
