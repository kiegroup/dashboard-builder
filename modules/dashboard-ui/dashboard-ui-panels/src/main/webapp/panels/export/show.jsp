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
<%@ page import="org.jboss.dashboard.ui.panel.export.ExportDriver" %>
<%@ page import="java.io.PrintWriter" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.panel.export.messages"
             locale="<%=SessionManager.getCurrentLocale()%>"/> <panel:defineObjects/> <%
    try {
        String showExportParam = (String) request.getAttribute(ExportDriver.PARAM_SHOW_EXPORT);
        String showImportParam = (String) request.getAttribute(ExportDriver.PARAM_SHOW_IMPORT);
        boolean showExport = showExportParam == null || new Boolean(showExportParam).booleanValue();
        boolean showImport = showImportParam == null || new Boolean(showImportParam).booleanValue();
%>
<table width="700px" border="0" align="left" cellpadding="0" cellspacing="0" style="text-align:center; background-color:#ffffff;">
    <tr height="100%"><%if (showExport) {%>
        <td width="100%" class="skn-table_border" style="border-bottom:none;"><div class="skn-table_border" style="margin:1px;"><div class="skn-table_header" style="padding-top:5px;"><i18n:message key="exporting">!!!Export</i18n:message></div></div></td><%}%>
        <td>&nbsp;</td><%if (showImport) {%>
        <td width="100%" class="skn-table_border" style="border-bottom:none;"><div class="skn-table_border" style="margin:1px;"><div class="skn-table_header" style="padding-top:5px;"><i18n:message key="importing">!!!Import</i18n:message></div></div></td><%}%>
    </tr>
    <tr><%if (showExport) {%>
        <td align="center" valign="top" class="skn-table_border skn-background_alt" style="border-top:none;"><div style="margin:1px; padding:8px;"><br/><jsp:include page="export.jsp" flush="true"/></div></td><%}%>
        <td>&nbsp;</td><%if (showImport) {%>
        <td align="center" valign="top" class="skn-table_border skn-background_alt" style="border-top:none;"><div style="margin:1px; padding:8px;"><br/><jsp:include page="import.jsp" flush="true"/></div></td><%}%>
    </tr>
</table>
<%}
catch (Exception e) {
    e.printStackTrace(new PrintWriter(out));
}
%>