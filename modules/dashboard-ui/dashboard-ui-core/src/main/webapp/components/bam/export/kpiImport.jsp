<%--

    Copyright (C) 2013 JBoss Inc

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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.export.messages"
			 locale="<%=SessionManager.getCurrentLocale()%>"/>
<table width="700px" border="0" cellpadding="0" cellspacing="0" style="text-align:center; background-color:#ffffff;">
	<tr height="100%">
		<td width="100%"><div class="skn-table_border"><div class="skn-table_header" style="padding-top:5px;"><i18n:message key="import.kpis.importing">!!!Import</i18n:message></div></div></td>
	</tr>
	<tr>
		<td width="100%" align="center" class="skn-background_alt">
			<div style="height:auto; margin:1px; padding:8px;">

				<div style="text-align:center;">
					<i18n:message key="import.kpis.fileToImport">!!!Please select the file to import</i18n:message>:
				</div>
				<form method="POST" enctype="multipart/form-data" style="margin:0px" id="<panel:encode name="importForm"/>"
					  action="<factory:formUrl/>" onsubmit="return this.importFile.value">
					<factory:handler action="importKPIs"/>
					<div align="left" class="skn-table_border" style="width:300px; height:100px; overflow:auto; padding:5px; background-color:#FFFFFF;">
						<br><input style="width:99%; text-align:center;" type="file" name="importFile" class="skn-input">
					</div>
					<p><br>&nbsp;</p>
					<input style="bottom:100%; vertical-align:bottom; margin-top:10px;" type="submit" class="skn-button" value="<i18n:message key="import.kpis.import"> !!!Import</i18n:message>">
				</form>

			</div>
		</td>
	</tr>
</table>

