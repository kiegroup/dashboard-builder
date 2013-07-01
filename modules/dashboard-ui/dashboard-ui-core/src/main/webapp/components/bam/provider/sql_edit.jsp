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
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%@ page import="org.jboss.dashboard.ui.components.sql.SQLProviderEditor"%>
<%@ page import="org.jboss.dashboard.database.DataSourceManager"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ListIterator"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.provider.sql.SQLDataLoader" %>
<%@ page import="org.jboss.dashboard.CoreServices" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@taglib uri="factory.tld" prefix="factory"%>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.sql.messages"
        locale="<%=LocaleManager.currentLocale()%>"/>
<panel:defineObjects/>
<%
    // Get the data provider from the data provider viewer and save it in the sql provider editor if it's neccessary
    SQLProviderEditor editor = (SQLProviderEditor) Factory.lookup("org.jboss.dashboard.ui.components.SQLProviderEditor");

    // Get the dataSource and the query
    SQLDataLoader sqlLoader = editor.getSQLDataLoader();
    String currentDataSource = sqlLoader.getDataSource();
    String sqlQuery = sqlLoader.getSQLQuery();
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left" style="padding-left:20px; padding-right:20px;">
            <i18n:message key="editor.sql.datasourceToUse">!!!Datasource a utilizar</i18n:message>: &nbsp;
            <select name="dataSource" title="<%= currentDataSource %>" width="65px" class="skn-input">
            <%
                DataSourceManager dataSourceManager = CoreServices.lookup().getDataSourceManager();
                List dataSourcesList = dataSourceManager.getDataSourceNames();
                ListIterator dataSourcesListIterator = dataSourcesList.listIterator();
                while (dataSourcesListIterator.hasNext()) {
                    String selected = "";
                    String dataSource = (String) dataSourcesListIterator.next();
                    if (dataSource.equals(currentDataSource)) selected = "selected";
            %>
                    <option title="<%= dataSource %>" value="<%= dataSource %>" <%= selected %>><%= dataSource %></option>
            <%
                }
            %>
            </select>
        </td>
    </tr>
    <tr>
        <td align="left"  style="padding-top:8px; padding-bottom:8px;padding-left:20px; padding-right:20px;"><i18n:message key="editor.sql.query"/>: <br/>
        <textarea name="sqlQuery" rows="10" cols="90" style="width:100%;"><%= sqlQuery == null ? "" : sqlQuery %></textarea>

<%
    // Check if the result of the test has been correct or not.
    if (editor.isConfiguredOk()) {
%>
            <br>
            <font color=green>
                <% if (editor.getElapsedTime() > 0) { %>
					<i18n:message key="editor.sql.dataSetOk">!!!Conjunto de datos correcto</i18n:message>
					<br>
                    <i18n:message key="editor.sql.elapsedTime"/>: <%=editor.getElapsedTime()%> ms
                    <br>
                    <i18n:message key="editor.sql.numberOfResults"/>: <%=editor.getNrows()%>
                <% } %>
            </font>
<%
    }
%>
        </td>
   </tr>
   <tr>
        <td align="center">
            <label>
                <input class="skn-button" type="submit" value="<i18n:message key="editor.sql.tryButton"/>"/>
            </label>
        </td>
    </tr>
</table>
