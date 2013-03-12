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
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.panel.export.messages"
             locale="<%=SessionManager.getCurrentLocale()%>"/>
<div style="height:auto;">

<div style="text-align:left;">
    <i18n:message key="fileToImport">!!!Please select the file to import</i18n:message>:
</div>
<form method="POST" enctype="multipart/form-data" style="margin:0px" id="<panel:encode name="importForm"/>"
      action="<panel:link action="startImport"/>" onsubmit="return this.importFile.value"><panel:hidden
      action="startImport"/>
    <div align="left" class="skn-table_border" style="width:300px; height:100px; overflow:auto; padding:5px; background-color:#FFFFFF;">
        <br><input style="width:99%; text-align:center;" type="file" name="importFile" class="skn-input">
    </div>
    <p><br>&nbsp;</p>
    <input style="bottom:100%; vertical-align:bottom; margin-top:10px;" type="submit" class="skn-button" value="<i18n:message key="import"> !!!Import</i18n:message>">
</form>

</div>
