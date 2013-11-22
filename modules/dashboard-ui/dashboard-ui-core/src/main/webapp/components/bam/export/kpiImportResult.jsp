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
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.export.messages"
			 locale="<%=SessionManager.getCurrentLocale()%>"/>

<table style="width: 100%;">
	<tr>
		<td style="overflow:hidden;">
			<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
								 property="clearAfterRender" propValue="false" />
			<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>
		</td>
	</tr>
	<tr>
		<td align="center">
			<form method="post" style="margin:0px;" id="<panel:encode name="back"/>" action="<factory:formUrl friendly="false"/>" >
				<factory:handler action="goBack"/>
				<input style="bottom:100%; vertical-align:bottom; margin-bottom:10px;" type="submit" class="skn-button" value="<i18n:message key="import.kpis.back"> !!!Back</i18n:message>">
			</form>
		</td>
	</tr>
</table>