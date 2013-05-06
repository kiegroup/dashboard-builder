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
<%@ page import="org.jboss.dashboard.domain.numeric.NumericDomain" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="org.jboss.dashboard.domain.AbstractDomain" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    DomainConfiguration domainConfig =  (DomainConfiguration) request.getAttribute("domainConfig");
%>
    <tr>
        <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= NumericDomain.I18N_PREFFIX + "numericTamInterval"%>'>!!Tamano del intervalo</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <select name="numericTamInterval" class="skn-input" style="width:120px;">
            <%
                String tamPredefinido = "";
                if (domainConfig.getNumericTamInterval().equals("-1") ) tamPredefinido = "selected";
            %>
                 <option value="-1" <%= tamPredefinido %>>
                    <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "tamPredefined"%>'>!!!Tamaño predefinido</i18n:message>
                </option>
            <%
                //TODO: Get the description of the intervals from the formatter
                String[] numericIntervals = new String[] {"1/1.000.000.000","1/1.000.000","1/100.000","1/10.000","1/1.000","1/100","1/10",
                        "1","10","100","1.000","10.000","100.000","1.000.000","1.000.000.000"};
                for (int i = 0; i < numericIntervals.length; i++) {
                    String selected = "";
                    if (i == Integer.parseInt(domainConfig.getNumericTamInterval())) selected = "selected";
            %>
                    <option value="<%= i %>" <%= selected %>>
                        <%= numericIntervals[i] %>
                    </option>
            <%
                }
            %>
            </select>
        </td>
    </tr>
    <tr>
        <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= NumericDomain.I18N_PREFFIX + "numericMinValue"%>'>!!Valor minimo</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <% String minValue = domainConfig.getNumericMinValue(); %>
            <input class="skn-input" name="numericMinValue" type="text" size="10" value="<%= minValue == null || minValue.trim().equals("") ? "" : minValue %>">
        </td>
    </tr>
    <tr>
        <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                <i18n:message key='<%= NumericDomain.I18N_PREFFIX + "numericMaxValue"%>'>!!Valor maximo</i18n:message>:
        </td>
        <td nowrap="nowrap" align="left">
            <% String maxValue = domainConfig.getNumericMaxValue(); %>
            <input class="skn-input" name="numericMaxValue" type="text" size="10" value="<%= maxValue == null || maxValue.trim().equals("") ? "" : maxValue %>">
        </td>
    </tr>