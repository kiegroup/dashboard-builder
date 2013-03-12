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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.displayer.table.DataSetTable" %>
<%@ page import="org.jboss.dashboard.provider.DataProperty" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.ui.components.table.DataSetTableHandler" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    DataSetTableHandler tableHandler = (DataSetTableHandler) request.getAttribute("tableHandler");
    DataSetTable table = (DataSetTable) tableHandler.getTable();
    Locale locale = LocaleManager.currentLocale();
    DataProperty groupByDataProperty = table.getGroupByProperty();

    // i18n
    ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.table.messages", locale);
    String groupTitle = i18n.getString("tableDisplayer.groupByHint");
    if (groupByDataProperty != null) groupTitle = groupByDataProperty.getName(locale);
%>
<tr>
    <td>
        <i18n:message key="tableDisplayer.groupBy">!!Group by</i18n:message>
    </td>
    <td>
        <%-- Print the group by property selector --%>
        <select name="groupbyproperty" title="<%= groupTitle  %>" class="skn-input" style="width:95px;"
                onchange="window.<factory:encode name="selectGroupByProperty"/>(); return false;">
            <option value="-1"><i18n:message key="tableDisplayer.groupByHint">!!- Seleccione propiedad -</i18n:message></option>
        <%
            List<DataProperty> dataProperties = Arrays.asList(table.getOriginalDataSet().getProperties().clone());
            DataDisplayerServices.lookup().getDataProviderManager().sortDataPropertiesByName(dataProperties, true);

            for (DataProperty dataProperty : dataProperties) {
                String selected = "";
                if (dataProperty.equals(groupByDataProperty)) selected = "selected";
        %>
                    <option title="<%= dataProperty.getName(locale) %>" value="<%= dataProperty.getPropertyId() %>" <%= selected %>><%= dataProperty.getName(locale) %></option>
        <%
            }
        %>
        </select>
        <script defer="true">
            window.<factory:encode name="showGroupByConfig"/> = function() {
                form = <factory:encode name="getTableForm"/>();
                form.tableaction.value = 'showGroupByConfig';
                return bam_kpiedit_submitProperties(form);
            }
            window.<factory:encode name="selectGroupByProperty"/> = function() {
                form = <factory:encode name="getTableForm"/>();
                form.tableaction.value = 'selectGroupByProperty';
                return bam_kpiedit_submitProperties(form);
            }
        </script>
<% if (groupByDataProperty != null) { %>
        <a href="#" onclick="window.<factory:encode name="showGroupByConfig"/>(); return false;">
            <img src="<static:image relativePath="general/16x16/ico-edit.png"/>" border="0"
                 title="<i18n:message key="tableDisplayer.groupBy">!!Group by</i18n:message>"
                 style="vertical-align:top">
        </a>
        <% if (tableHandler.showGroupByConfig()) { %>
        <div align="center" style="width:550px;height:400px;overflow:-moz-scrollbars-horizontal;overflow-x:hidden;overflow-y:auto; position:absolute;vertical-align:middle;z-index:11;">
        <mvc:include page="table_groupby_edit.jsp" flush="true" /></div>
        <% } %>
<% } %>
    </td>
</tr>