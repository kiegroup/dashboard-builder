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
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ page import="org.jboss.dashboard.ui.components.table.TableHandler" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.displayer.table.Table" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    TableHandler tableHandler = (TableHandler) request.getAttribute("tableHandler");
    Table table = tableHandler.getTable();
    if (table.getNumberOfPages() > 1) {
%>
    <a href="#" onclick="window.<factory:encode name="firstPage"/>(); return false;"><img src="<static:image relativePath="general/12x12/ico-page_first.png"/>" title="<i18n:message key="table.firstPage">!!Primera p&aacute;gina</i18n:message>" width="11" height="10" border="0" align="absmiddle"></a>&nbsp;
    <a href="#" onclick="window.<factory:encode name="previousPage"/>(); return false;"><img src="<static:image relativePath="general/12x12/ico-page_previous.png"/>" title="<i18n:message key="table.previousPage">!!P&aacute;gina anterior</i18n:message>" width="10" height="10" border="0" align="absmiddle"></a>&nbsp;
    <i18n:message key="table.currentPage" args="<%= new Object[] {new Integer(table.getCurrentPage()), new Integer(table.getNumberOfPages())} %>">!!P&aacute;gina&nbsp;{0} de {1}</i18n:message>&nbsp;
    <a href="#" onclick="window.<factory:encode name="nextPage"/>(); return false;"><img src="<static:image relativePath="general/12x12/ico-page_following.png"/>" title="<i18n:message key="table.nextPage">!!P&aacute;gina siguiente</i18n:message>" width="10" height="10" border="0" align="absmiddle"></a>&nbsp;
    <a href="#" onclick="window.<factory:encode name="lastPage"/>(); return false;"><img src="<static:image relativePath="general/12x12/ico-page_last.png"/>" title="<i18n:message key="table.lastPage">!!&Uacute;ltima p&aacute;gina</i18n:message>" width="11" height="10" border="0" align="absmiddle"></a>&nbsp;&nbsp;
    <i18n:message key="table.gotoPage">!!Ir a la p&aacute;gina</i18n:message>&nbsp;<input name="pagenumber" type="text" class="skn-input" size="2" maxlength="3">&nbsp;<input class="skn-button" type="button" value="<i18n:message key="table.goButton">!!Ok</i18n:message>" onclick="window.<factory:encode name="gotoPage"/>(); return false;">
    <script defer="true">
        window.<factory:encode name="nextPage"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'nextPage';
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="previousPage"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'previousPage';
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="firstPage"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'firstPage';
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="lastPage"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'lastPage';
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="gotoPage"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'gotoPage';
            return bam_kpiedit_submitProperties(form);
        }
    </script>
<% } %>
