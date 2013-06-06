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
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jboss.dashboard.function.ScalarFunction" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    DataSetTableHandler tableHandler = (DataSetTableHandler) request.getAttribute("tableHandler");
    DataSetTable table = (DataSetTable) tableHandler.getTable();
    Locale locale = LocaleManager.currentLocale();
    DataProperty groupByDataProperty = table.getGroupByProperty();
    int[] nonGroupByColumns = table.getNonGroupByColumnIndexes();
    if (groupByDataProperty != null) {
%>
<table width="100%" align="left" bgcolor="#FFFFFF" border="0" cellspacing="2" class="skn-table_border">
    <tr class="skn-table_header">
        <td width="100%" colspan="2" align="center">
            <i18n:message key="tableDisplayer.editGroupBy" args="<%=new Object[] {groupByDataProperty.getPropertyId()}%>">!!!Agrupando por <%=groupByDataProperty.getPropertyId()%></i18n:message>
        </td>
    </tr>
    <%-- Print the selected group by property domain configuration --%>
    <%
        if (table.getGroupByProperty() != null) {
            request.setAttribute("domainConfig", new DomainConfiguration(table.getGroupByProperty()));
            request.setAttribute("hideButtons", "true");
            request.setAttribute("hideDescription", "true");
    %>
    <mvc:include page="../domain/domain_details.jsp" flush="true" />
    <%
            request.removeAttribute("domainConfig");
            request.removeAttribute("hideButtons");
            request.removeAttribute("hideDescription");
        }
        // Edit the group by functions if required.
        if (nonGroupByColumns.length > 0) {    
    %>
    <tr>
        <td width="160px" height="15" nowrap="nowrap" align="left" class="skn-even_row">
           <i18n:message key="tableDisplayer.scalarFunction">!!Funcion escalar</i18n:message>
       </td>
        <td align="left">
            <select name="groupbyfunctionindex" class="skn-input" style="width:100px;" onchange="return bam_kpiedit_submitProperties(this);">
            <%
                for (int i=0; i<nonGroupByColumns.length; i++) {
                    int nonGroupByColumnIndex = nonGroupByColumns[i];
                    String selected = "";
                    if (nonGroupByColumnIndex == tableHandler.getGroupBySelectedColumnIndex()) selected = "selected";
            %>
            <option value="<%= nonGroupByColumnIndex %>" <%= selected %>><%= table.getColumnName(nonGroupByColumnIndex) %></option>
            <%
                }
            %>
            </select>
            <select name="groupbyfunctioncode" class="skn-input" style="width:100px;" onchange="return bam_kpiedit_submitProperties(this);">
            <%
                int groupBySelectedColumnIndex = tableHandler.getGroupBySelectedColumnIndex();
                DataProperty groupBySelectedProperty = table.getOriginalDataProperty(groupBySelectedColumnIndex);
                for (ScalarFunction scalarFunction : groupBySelectedProperty.getDomain().getScalarFunctionsSupported()) {
                    String selected = "";
                    if (scalarFunction.getCode().equals(table.getGroupByFunctionCode(groupBySelectedColumnIndex))) selected = "selected";
            %>
                    <option value="<%= scalarFunction.getCode() %>" <%= selected %>><%= scalarFunction.getName(locale) %></option>
            <%
                }
            %>
            </select>
            <script defer="true">
                window.<factory:encode name="selectGroupByPropertyFunction"/> = function() {
                    form = <factory:encode name="getTableForm"/>();
                    form.tableaction.value = 'selectGroupByPropertyFunction';
                    return bam_kpiedit_submitProperties(form);
                }
            </script>
        </td>
    </tr>
    <% } %>
    <tr>
        <td width="160px" height="15" nowrap="nowrap" align="left" class="skn-even_row">
           <i18n:message key="tableDisplayer.showGroupByTotals">!!Mostrar totales</i18n:message>
       </td>
        <td align="left">
            <input type="checkbox" name="groupbyshowtotals" value="true" <%= table.showGroupByTotals() ? "checked" : "" %> onchange="return bam_kpiedit_submitProperties(this);">
        </td>
    </tr>
    <tr>
        <td width="160px" height="15" nowrap="nowrap" align="left" class="skn-even_row">
           <i18n:message key="tableDisplayer.groupByTotalsHtmlStyle">!!Total HTML</i18n:message>
       </td>
        <td align="left">
            <textarea rows="4" cols="18" name="groupbytotalshtmlstyle" class="skn-input"><%= table.getGroupByTotalsHtmlStyle() %></textarea>
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <input type="hidden" name="closegroupbyconfig" value="false">
            <input type="button" class="skn-button" value="<i18n:message key="tableDisplayer.save">!!!Guardar</i18n:message>" onclick="this.form.closegroupbyconfig.value='true'; submitAjaxForm(this.form); return false;">
            <input type="button" class="skn-button" value="<i18n:message key="tableDisplayer.cancel">!!!Cancelar</i18n:message>" onclick="window.<factory:encode name="showGroupByConfig"/>(); return false;">
        </td>
    </tr>
</table>
<% } %>