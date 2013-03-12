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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<html>
<head>
    <panel:envelopeHead/>
</head>
<body style="background:#E3E3C7; height:100%">

<%
    String galleriesPage = "galleryImage.jsp";
    String modeParam = request.getParameter("mode");
    if( "link".equals(modeParam) || "image".equals(modeParam)){
%>
    <factory:setProperty property="mode" propValue="<%=modeParam%>" bean="org.jboss.dashboard.ui.components.FCKEditorHandler"/>
<%
    }
%>
<factory:property property="mode" id="mode" bean="org.jboss.dashboard.ui.components.FCKEditorHandler">
    <%
        if ("link".equals(mode)) {
            galleriesPage = "galleryLink.jsp";
        }
    %>
</factory:property>

<factory:property property="fullUrl" id="fullUrl" bean="org.jboss.dashboard.ui.components.FCKEditorHandler">
    <%
        if (fullUrl != null && Boolean.valueOf(fullUrl.toString()).booleanValue()) {
            galleriesPage += "?fullUrl=true";
        }
    %>
</factory:property>


<mvc:formatter name="org.jboss.dashboard.ui.formatters.FCKEditorViewServerFormatter">
    <mvc:formatterParam name="page-public" value="publicImages.jsp"/>
    <mvc:formatterParam name="page-private" value="privateImages.jsp"/>
    <mvc:formatterParam name="page-galleries" value="<%=galleriesPage%>"/>
    <%--------------------------------------------------------------------%>
    <mvc:fragment name="outputTabsStart">
       <table cellspacing="0" cellpadding="0"><tr>
    </mvc:fragment>
    <%--------------------------------------------------------------------%>
    <mvc:fragment name="outputTab">
        <mvc:fragmentValue name="current" id="current">
            <td nowrap width="1">
                <table cellspacing="0" cellpadding="0" border="0" width="100%">
                    <tr height="100%">
                        <td width="1" align="left"><resource:image category="skin" resourceId="HEADER_LEFT"/></td>
                        <td nowrap style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                            <mvc:fragmentValue name="tabName" id="tabName">
                            <%if(Boolean.TRUE.equals(current)){%>
                            <span class="skn-important">
                                <i18n:message key='<%="fcktab."+tabName%>'>!!<%=tabName%></i18n:message>
                            </span>
                            <%} else {%>
                            <a href="<factory:url action="changeTab" bean="org.jboss.dashboard.ui.components.FCKEditorHandler"><factory:param name="tab" value="<%=tabName%>"/></factory:url>">
                                <i18n:message key='<%="fcktab."+tabName%>'>!!<%=tabName%></i18n:message>
                            </a>
                            <%}%>
                            </mvc:fragmentValue>
                        </td>
                        <td width="1" align="left"><resource:image category="skin" resourceId="HEADER_RIGHT"/></td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%--------------------------------------------------------------------%>
    <mvc:fragment name="outputTabsEnd">
     </tr></table>
    </mvc:fragment>
    <%--------------------------------------------------------------------%>
    <mvc:fragment name="beforeCurrentPage">
     <div style="overflow:auto;">
    </mvc:fragment>
    <%--------------------------------------------------------------------%>
    <mvc:fragment name="afterCurrentPage">
     </div>
    </mvc:fragment>
    <%--------------------------------------------------------------------%>
</mvc:formatter>
<panel:envelopeFooter/>
</body>
</html>