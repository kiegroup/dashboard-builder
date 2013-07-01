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
<%@ page import="org.jboss.dashboard.factory.Factory" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.components.csv.CSVProviderEditor" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.jboss.dashboard.provider.csv.CSVDataLoader" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@taglib uri="factory.tld" prefix="factory" %>
<%@taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.csv.messages"
			 locale="<%=LocaleManager.currentLocale()%>"/>
<panel:defineObjects/>
<%
	CSVProviderEditor editor = (CSVProviderEditor) Factory.lookup("org.jboss.dashboard.ui.components.CSVProviderEditor");
	CSVDataLoader csvLoader = editor.getCSVDataLoader();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv.csvFileURL">!!!url</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvFileURL">!!!url</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvUrlFile"
				   value="<%=(csvLoader.getFileURL()!= null? csvLoader.getFileURL():"")%>">
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv.csvSeparatedBy">!!!Data separator</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvSeparatedBy">!!!Data separator</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvSeparatedBy"
				   value="<%= (csvLoader.getCsvSeparatedBy()!=null? StringEscapeUtils.escapeHtml(csvLoader.getCsvSeparatedBy()):"")%>">
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv._csvQuoteChar">!!!Quote character</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvQuoteChar">!!!Quote character</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvQuoteChar"
				   value="<%=(csvLoader.getCsvQuoteChar()!=null ? StringEscapeUtils.escapeHtml(csvLoader.getCsvQuoteChar()):"")%>">
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv._csvEscapeChar">!!!Escape character</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvEscapeChar">!!!Escape character</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvEscapeChar"
				   value="<%=(csvLoader.getCsvEscapeChar()!=null ? StringEscapeUtils.escapeHtml(csvLoader.getCsvEscapeChar()):"")%>">
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv.csvDatePattern">!!!Date format</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvDatePattern">!!!Date format</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvDatePattern"
				   value="<%=(csvLoader.getCsvDatePattern()!=null ? csvLoader.getCsvDatePattern():"")%>">
		</td>
	</tr>
	<tr>
		<td align="left" style="padding-left:20px; padding-right:20px;">
			<i18n:message key="editor.csv.csvNumberPattern">!!!Number format</i18n:message>: &nbsp;
		</td>
		<td>
			<input size="22" class="skn-input"
				   title="<i18n:message key="editor.csv.csvNumberPattern">!!!Formato numeros</i18n:message>"
				   style="width:20em; margin-bottom:1.4em;" type="text"
				   name="csvNumberPattern"
				   value="<%=(csvLoader.getCsvNumberPattern()!= null? csvLoader.getCsvNumberPattern():"")%>">
		</td>
	</tr>


	<tr>
		<td align="left" style="padding-top:8px; padding-bottom:8px;padding-left:20px; padding-right:20px;" colspan="2">

			<%
				// Check if the result of the test has been correct or not.
				if (editor.isConfiguredOk()) {
			%>
			<br>
			<font color=green>
			<%
					if (editor.getElapsedTime() > 0) {
			%>
				<i18n:message key="editor.csv.dataSetOk">!!! Data loaded correctly </i18n:message>
				<br>
				<i18n:message key="editor.csv.elapsedTime"/>: <%=editor.getElapsedTime()%> ms
				<br>
				<i18n:message key="editor.csv.numberOfResults"/>: <%=editor.getNrows()%>
			</font>
			<%
					}
				}
			%>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
			<label>
				<input class="skn-button" type="submit" value="<i18n:message key="editor.csv.tryButton"/>"/>
			</label>
		</td>
	</tr>
</table>
