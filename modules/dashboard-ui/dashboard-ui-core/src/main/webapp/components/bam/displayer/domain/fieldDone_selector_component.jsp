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
<%@ page import="org.jboss.dashboard.ui.components.chart.AbstractChartDisplayerEditor" %>
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer" %>
<%@ page import="org.jboss.dashboard.provider.DataProperty" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.jboss.dashboard.domain.DomainConfiguration" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.jboss.dashboard.DataDisplayerServices" %>
<%@ page import="java.util.List" %>
<%@taglib uri="factory.tld" prefix="factory"%>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    Locale locale = LocaleManager.currentLocale();
    AbstractChartDisplayerEditor editor = (AbstractChartDisplayerEditor) request.getAttribute("editor");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) editor.getDataDisplayer();
    DataProperty doneProperty = displayer.getDoneProperty();
%>
    <td align="left">
        <select name="idDoneDetails" title="<%= doneProperty.getName(locale) %>" id="<factory:encode name="idDoneDetails"/>" class="skn-input"
                style="width:95px; height:18px; text-align:left; overflow:hidden; vertical-align:middle"
                onChange="return bam_kpiedit_submitProperties(this);"
                >
        <%
            List<DataProperty> doneProperties = Arrays.asList(displayer.getDomainPropertiesAvailable().clone());
            DataDisplayerServices.lookup().getDataProviderManager().sortDataPropertiesByName(doneProperties, true);

            for (DataProperty dataProperty : doneProperties) {
                String selected = "";
                if (dataProperty.getPropertyId().equals(doneProperty.getPropertyId())) selected = "selected";
        %>
                <option title="<%= dataProperty.getName(locale) %>" value="<%= dataProperty.getPropertyId() %>" <%= selected %>>
                    <%= dataProperty.getName(locale) %>
                </option>
        <%
            }
        %>
        </select>
    </td>
    <td align="left">
        
    </td>    