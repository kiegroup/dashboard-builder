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
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.jboss.dashboard.domain.Domain" %>
<%@ page import="org.jboss.dashboard.domain.AbstractDomain" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.domain.RangeConfiguration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jboss.dashboard.function.ScalarFunction" %>
<%@ page import="org.jboss.dashboard.domain.RangeConfigurationParser" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    RangeConfiguration range2Config = (RangeConfiguration) request.getAttribute("range2Config");
%>
    <tr>
         <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "description"%>'>!!Descripcion</i18n:message>:

        </td>
        <td nowrap="nowrap" align="left">
            <input class="skn-input" name="descripRange2Details" type="text" value="<%= range2Config.getName(locale) %>">
        </td>
    </tr>
    <tr>
         <td height="15" nowrap="nowrap" align="left"  class="skn-even_row">

                <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "scalarFunction"%>'>!!Funcion escalar a aplicar</i18n:message>:

        </td>
        <td nowrap="nowrap" align="left">
            <select name="scalar2FunctionCode" class="skn-input" style="width:120px;">
            <%
                Domain range2Domain = range2Config.getRangeProperty().getDomain();
                for (ScalarFunction scalarFunction : range2Domain.getScalarFunctionsSupported()) {
                    String selected = "";
                    if (scalarFunction.getCode().equals(range2Config.getScalarFunctionCode())) selected = "selected";
            %>
                    <option value="<%= scalarFunction.getCode() %>" <%= selected %>><%= scalarFunction.getName(locale) %></option>
            <%
                }
            %>
            </select>
        </td>
    </tr>
    <tr>
         <td height="15" nowrap="nowrap" align="left" class="skn-even_row">

                <i18n:message key='<%= AbstractDomain.I18N_PREFFIX + "unit"%>'>!!Unidad</i18n:message>:
            
        </td>
        <%
            String unit = range2Config.getUnit(locale);
            if (unit == null || unit.trim().equals("") || unit.indexOf(AbstractChartDisplayer.UNIT_VALUE_TAG) == -1) unit = AbstractChartDisplayer.UNIT_VALUE_TAG;
        %>
        <td nowrap="nowrap" align="left">
            <input class="skn-input" name="unit2" type="text" value="<%= unit %>">
        </td>
    </tr>
    <tr>
        <td align="center" colspan="2" style="padding-bottom:10px; padding-top:10px">
            <input type="hidden" name="<%=AbstractChartDisplayerEditor.RANGE2_SAVE_BUTTON_PRESSED%>" id="<factory:encode name="updateRange2Details"/>" value="false">
            <input class= "skn-button" type="button" value="<i18n:message key="dataProviderComponent.save">!!Guardar</i18n:message>" onclick="document.getElementById('<factory:encode name="updateRange2Details"/>').value='true'; bam_kpiedit_submitProperties(this.form);">
            <input class= "skn-button" type="button" value="<i18n:message key="dataProviderComponent.cancel">!!Cancelar</i18n:message>" onclick="window.<factory:encode name="editRange2"/>(); return false;">
        </td>
    </tr>