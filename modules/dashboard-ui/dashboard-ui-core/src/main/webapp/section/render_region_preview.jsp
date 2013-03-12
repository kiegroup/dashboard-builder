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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>

<%
    String renderer = (String) request.getAttribute("org.jboss.dashboard.ui.taglib.RegionTag.renderer");
%>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>


<mvc:formatter name="<%=renderer%>">
    <mvc:formatterParam name="preview" value="true"/>
    <mvc:formatterParam name="layoutRegion" value='<%=request.getAttribute("layoutRegion")%>'/>
    <mvc:fragment name="regionStart">
        <table width="100%" bgcolor="#ffffff" border="0" cellspacing="0">
        <tr>
        <td>
        <table cellspacing="1" width="100%" cellspacing="0" cellpadding="0"> </mvc:fragment>
    <mvc:fragment name="newLineStart">
        <tr>
    </mvc:fragment>
    <mvc:fragment name="panelOutputStart">
        <td valign="top" align="center" bgcolor="#FFC600" height="<mvc:fragmentValue
            name="panelHeight"/>" width="<mvc:fragmentValue name="panelWidth"/>">
    </mvc:fragment>
    <mvc:fragment name="panelOutput">
        <mvc:fragmentValue name="regionName"/>
    </mvc:fragment>
    <mvc:fragment name="panelOutputEnd">
        </td>
    </mvc:fragment>
    <mvc:fragment name="newLineEnd">
        </tr>
    </mvc:fragment>
    <mvc:fragment name="regionEnd">
        </table>
        </td>
        </tr>
        </table>
    </mvc:fragment>
</mvc:formatter>

