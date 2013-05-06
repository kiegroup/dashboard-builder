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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.domain.date.DateDomain" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="org.jboss.dashboard.domain.AbstractDomain" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    DomainConfiguration domainConfig =  (DomainConfiguration) request.getAttribute("domainConfig");
%>
    <tr>
        <td width="160px" height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= DateDomain.I18N_PREFFIX + "dateTamInterval"%>'>!!Tamano del intervalo</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <select name="dateTamInterval" class="skn-input" style="width:120px;">
            <%
                String tamPredefinido = "";
                if (domainConfig.getDateTamInterval().equals("-1") ) tamPredefinido = "selected";
            %>
                 <option value="-1" <%= tamPredefinido %>>
                    <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "tamPredefined"%>'>!!!Tamaño predefinido</i18n:message>
                </option>
            <%
                // i18n
                ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
                String[] dateIntervals = new String[] {
                        i18n.getString(DateDomain.I18N_PREFFIX + "seconds"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "minutes"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "hours"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "days"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "weeks"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "months"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "quarters"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "years"),
                        i18n.getString(DateDomain.I18N_PREFFIX + "decades")};
                for (int i = 0; i < dateIntervals.length; i++) {
                    String selected = "";
                    if (i == Integer.parseInt(domainConfig.getDateTamInterval())) selected = "selected";
            %>
                    <option value="<%= i %>" <%= selected %>>
                        <%= dateIntervals[i] %>
                    </option>
            <%
                }
            %>
            </select>
        </td>
    </tr>
    <tr>
        <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= DateDomain.I18N_PREFFIX + "initialDate"%>'>!!Fecha de inicio</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <% String minDate = domainConfig.getDateMinDate(); %>
            <input class="skn-input" name="initialDate" id="<factory:encode name="initialDate"/>" type="text" value="<%= minDate == null || minDate.trim().equals("") ? "" : minDate %>">&nbsp;
            <a href="#" onclick="NewCal('<factory:encode name="initialDate"/>','ddmmyyyy',true,24); return false;">
                <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0"></a>&nbsp;
            <a href="#">
                <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0"
                     onclick="document.getElementById('<factory:encode name="initialDate"/>').value=''; return false;"></a>
        </td>
    </tr>
    <tr>
        <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= DateDomain.I18N_PREFFIX + "endDate"%>'>!!Fecha de fin</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <% String maxDate = domainConfig.getDateMaxDate(); %>
            <input class="skn-input" name="endDate" id="<factory:encode name="endDate"/>" type="text" value="<%= maxDate == null || maxDate.trim().equals("") ? "" : maxDate %>">&nbsp;
            <a href="#" onclick="NewCal('<factory:encode name="endDate"/>','ddmmyyyy',true,24); return false;">
                <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0"></a>&nbsp;
            <a href="#">
                <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0"
                     onclick="document.getElementById('<factory:encode name="endDate"/>').value=''; return false;"></a>
        </td>
    </tr>