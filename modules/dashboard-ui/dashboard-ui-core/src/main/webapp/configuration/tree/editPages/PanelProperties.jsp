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
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%try {%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>
<mvc:formatter name="<%=formatterName%>">
    <mvc:fragment name="outputStart">
        <form name="config" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="saveConfig"/>" method="POST">
        <factory:handler
                bean="<%=beanName%>"
                action="save"/>
        <table border="0" cellspacing="1" cellpadding="4" class="skn-table_border" align="left" width="100%">
    </mvc:fragment>
    <mvc:fragment name="output">
        <tr class="skn-table_header">
            <td colspan="2">
                <mvc:fragmentValue name="title"/>
            </td>
        </tr>
        <jsp:include page="/common/panelInstanceProperties/panel_instance_properties.jsp" flush="true"/>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        <tr><td colspan="2" align="center">
            <br>
<%--
            <table cellpadding="0" cellspacing="0" border="0" width="100%" align="center"><tr><td align="center">
--%>
                <input class="skn-button" type="submit" value='<i18n:message key="ui.saveChanges"/>'>
<%--
            </td><td align="center">
                <input name="Submit23" type="reset" class="skn-button_alt" value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
            </td></tr></table>
--%>
        </td></tr>
        </table>
        </form>
        <script>
            setAjax("<panel:encode name="saveConfig"/>");
        </script>
    </mvc:fragment>
</mvc:formatter>

<%} catch (Throwable t) {
    t.printStackTrace();
}%>