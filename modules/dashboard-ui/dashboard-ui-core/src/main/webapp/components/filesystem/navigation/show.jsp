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

<table border="0" width="100%" cellspacing="0" cellpadding="0" style="border-top:solid 1px #c2c28f; border-left:solid 1px #c2c28f; border-bottom:solid 1px #aaaa7d; border-right:solid 1px #aaaa7d;">
    <tr>
        <td rowspan="2" valign="top" width="30%" style="border-right:solid 1px #c2c28f; background-color:#F1F1E3;">
            <div style="width:100%; height:480px; overflow:auto; overflow-x:scroll">
            <jsp:include page="tree.jsp"/>
            </div>
        </td>
        <td valign="top" colspan="2">
            <div style="width:100%; height:270px; overflow:auto; border-bottom:1px solid #c2c28f; background-color:#F1F1E3;">
            <jsp:include page="files.jsp"/>
            </div>
        </td>
    </tr>
    <tr>
        <td width="200" style="border-right:1px solid #c2c28f; vertical-align:top; background-color:#FFFFFF;">
            <table border="0" cellpadding="0" cellspacing="0" width="100%" height="210">
                <tr><td style="text-align:center; vertical-align:top;">
                    <jsp:include page="preview.jsp"/>
                </td></tr>
           </table>
        </td>
        <td style="padding:2px; vertical-align:top; background-color:#F1F1E3;">
            <jsp:include page="actions.jsp"/>
        </td>
    </tr>
</table>

<factory:property property="currentFile" id="currentFile">
    <%if( currentFile != null ){%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr><td align="center">
        <form action="<factory:formUrl/>" style="margin-top:10px;" method="POST">
            <factory:handler action="chooseFile"/>
            <input type="submit" class="skn-button" value="<i18n:message key="ok">!!OK</i18n:message>">
        </form>
    </td></tr>
</table>
    <%}%>
</factory:property>

<factory:property property="selectedFile" id="selectedFile">
    <%if( selectedFile != null ){%>
    <script type="text/javascript" language="Javascript">
        window.top.opener.SetUrl( '<%=selectedFile%>' );
        window.top.opener.ResetSizes(); window.close();
    </script>
    <%}%>
</factory:property>

