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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<table width="100%">
    <tr class="skn-table_header" style="background-color:#d6d6bc;">
        <td><i18n:message key="name">!!!Nombre</i18n:message></td>
        <td><i18n:message key="size">!!!Tamaño</i18n:message></td>
        <td><i18n:message key="mimeType">!!!Tipo</i18n:message></td>
    </tr>
<factory:property property="fileListFormatter" id="formatter">
<mvc:formatter name="<%=formatter%>">
    <mvc:fragment name="output">
        <mvc:fragmentValue name="path" id="path">
        <mvc:fragmentValue name="current" id="current">
        <tr <%if(Boolean.TRUE.equals(current)){%> class="skn-background" <%}%>>
            <td style="padding-left:5px;  ">
                <img src="<mvc:fragmentValue name="imageURL"/>" style="vertical-align: middle;" alt="thumbnail">
                <a style="vertical-align:middle"
                        href="<factory:url action="selectFile"><factory:param name="path" value="<%=path%>"/></factory:url>">
                <mvc:fragmentValue name="name"/>
                </a>
            </td>
            <td style="padding-right:5px; text-align:right"><mvc:fragmentValue name="fileSize"/></td>
            <td style="padding-left:5px;"><mvc:fragmentValue name="contentType"/></td>
        </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
</mvc:formatter>
</table>
</factory:property>
