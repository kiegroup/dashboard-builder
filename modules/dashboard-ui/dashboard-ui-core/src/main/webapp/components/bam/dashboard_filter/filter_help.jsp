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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.DashboardFilterHandler" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib prefix="i18n" uri="http://jakarta.apache.org/taglibs/i18n-1.0" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.filter.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
	String propId = StringUtils.deleteWhitespace((String) request.getAttribute("propertyId"));
%>
<table border="0" align="left" cellpadding="10" cellspacing="0"  class="skn-table_border" bgcolor="#ffffff"
       style="display:none; position:absolute;"
       id="<panel:encode name='<%="helpForProperty_" + propId%>'/>">
   <tr>
     <td align="left"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "filterHelp"%>' /></td>
   </tr>
</table>
<script defer="defer">
    window.<panel:encode name='<%="helpForProperty_" + propId + "_function"%>'/> = function() {
        var element = document.getElementById('<panel:encode name='<%="helpForProperty_" + propId%>'/>');
        if (element.style.display=='none') element.style.display = "block";
        else element.style.display = "none";
    }
</script>
