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
<%@ page import="org.jboss.dashboard.ui.components.DashboardFilterHandler" %>
<%@ page import="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.filter.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    String componentCode = (String) request.getAttribute("componentCode");
    DashboardFilterHandler handler = DashboardFilterHandler.lookup(componentCode);
%>
<mvc:formatter name="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter">
<mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_RENDER_TYPE%>"
                    value="<%=DashboardFilterFormatter.RENDER_TYPE_SHOW%>"/>
<mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_COMPONENT_CODE%>"
                    value='<%=request.getAttribute("componentCode")%>'/>

<mvc:fragment name="outputStart">
    <table border="0" style="margin:0px;" cellspacing="0" width="100%">
    <tr>
    <td>
    <form method="post" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="filterForm"/>"
    onkeyup="/* If Enter pressed submit form*/ if(event.keyCode == 13) {submitAjaxForm(this);}">
    <factory:handler bean="<%=handler.getComponentPath()%>" action="filter"/>
    <table border="0" cellspacing="5" cellpadding="0" width="100%">

</mvc:fragment>

<mvc:fragment name="outputEmpty">
    <tr>
        <td colspan="2">
            <span class="skn-error">
                <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "emptyPanel"%>'>!!!Panel empty</i18n:message>
            </span>
        </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputPanelDuplicated">
    <tr>
        <td colspan="2">
            <div style="width:160px; overflow:hidden;"
                 title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "duplicatedPanel"%>'>!!!Panel duplicado</i18n:message>">
                <span class="skn-error"><i18n:message
                        key='<%=DashboardFilterHandler.I18N_PREFFIX + "duplicatedPanel"%>'>!!!Panel duplicado</i18n:message></span>
            </div>
        </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputStartLegend">
    <tr>
    <td colspan="2">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
        <td style="white-space:nowrap; border-bottom: 1px dotted gray;" colspan="3">
            <div style="width:180px; white-space:nowrap; overflow:hidden;"
                 title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "filteredProperties"%>'>!!! Propiedades filtradas</i18n:message>">
                <b><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "filteredProperties"%>'>!!! Propiedades filtradas</i18n:message></b>
            </div>
        </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputLegendStringProperty">
    <mvc:fragmentValue name="index" id="index">
        <%
            String className, altClass;
            if (((Integer) index).intValue() % 2 == 0) {
                className = "skn-even_row";
                altClass = "skn-even_row_alt";
            } else {
                className = "skn-odd_row";
                altClass = "skn-odd_row_alt";
            }
        %>
        <tr>
            <td style="width:20px; height:20px; padding-top:4px; text-align:center; vertical-align:middle;">
                <a href="#" onclick="
                        document.getElementById('<panel:encode name="filteredPropertyToDelete"/>').value = '<mvc:fragmentValue name="propertyId"/>';
                        submitAjaxForm(document.getElementById('<panel:encode name="deleteFilteredPropertyForm"/>'));
                        return false;">
                    <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0" />
                </a>
            </td>
            <td>
                <div style="width:70px; white-space:nowrap; overflow:hidden;"
                     title="<mvc:fragmentValue name="propertyName"/>">
                    <mvc:fragmentValue name="propertyName"/>
                </div>
            </td>
            <td>
                <div style="width:70px; white-space:nowrap; overflow:hidden;"
                     title="<mvc:fragmentValue name="propertyValue"/>">
                    <mvc:fragmentValue name="propertyValue"/>
                </div>
            </td>
        </tr>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputLegendToFromProperty">
    <mvc:fragmentValue name="propertyMaxValue" id="propertyMaxValue">
        <mvc:fragmentValue name="index" id="index">
            <mvc:fragmentValue name="outputText" id="outputText">
                <%
                    String className, altClass;
                    if (((Integer) index).intValue() % 2 == 0) {
                        className = "skn-even_row";
                        altClass = "skn-even_row_alt";
                    } else {
                        className = "skn-odd_row";
                        altClass = "skn-odd_row_alt";
                    }
                %>
                <tr style="border-bottom: solid 1px #808080;">
                    <td style="width:20px; height:20px; padding-top:4px; text-align:center; vertical-align:middle;">
                        <a title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "clear"%>'>!!! Clear</i18n:message>"
                           href="#" onclick="
                                document.getElementById('<panel:encode name="filteredPropertyToDelete"/>').value = '<mvc:fragmentValue name="propertyId"/>';
                                submitAjaxForm(document.getElementById('<panel:encode name="deleteFilteredPropertyForm"/>'));
                                return false;">
                            <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0" />
                        </a>
                    </td>
                    <td>
                        <div style="width:70px; white-space:nowrap; overflow:hidden;"
                             title="<mvc:fragmentValue name="propertyName"/>">
                            <mvc:fragmentValue name="propertyName"/>
                        </div>
                    </td>
                    <td>
                        <div style="width:70px; white-space:nowrap; overflow:hidden;"
                             title="<mvc:fragmentValue name="outputText"/>">
                            <mvc:fragmentValue name="outputText"/>
                        </div>
                    </td>
                </tr>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputEndLegend">
    </table>
    </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputStartProperties">
    <tr>
    <td colspan="2">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr><td colspan="2">

</mvc:fragment>

<mvc:fragment name="outputEndProperties">
    </td></tr>
    </table>
    </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputStartBottom">
    <tr>

</mvc:fragment>

<mvc:fragment name="outputAutoRefresh">
    <td style="height:20px">
        <mvc:include page="/components/bam/dashboard_filter/filter_autorefresh.jsp"/>
    </td>
</mvc:fragment>

<mvc:fragment name="outputAutoRefreshInTable">
    <table width="170px" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td style="height:20px" width="100%">
                <mvc:include page="/components/bam/dashboard_filter/filter_autorefresh.jsp"/>
            </td>
        </tr>
        <form method="post" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="refreshForm"/>">
            <factory:handler bean="<%=handler.getComponentPath()%>" action="refresh"/>
            <input type="hidden" id="<panel:encode name="refreshTimeOut"/>" name="refreshTimeOut" value="">
        </form>
    </table>
</mvc:fragment>

<mvc:fragment name="outputStartButtons">
    <td style="height:20px" colspan="<mvc:fragmentValue name="colspan"/>">
    <table border="0" cellpadding="0" cellspacing="0" style="width:100%; text-align:right;"><tr><td nowrap>
</mvc:fragment>

<mvc:fragment name="outputApplyButton">

    <a href="#" onclick="submitAjaxForm(document.getElementById('<panel:encode name="filterForm"/>')); return false;">
        <img src="<static:image relativePath="general/16x16/ico-filter.png"/>" border="0"
             title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "apply"%>'>!!! Aplicar</i18n:message>">
    </a>

</mvc:fragment>

<mvc:fragment name="outputRefreshButton">

    <a href="#" onclick="submitAjaxForm(document.getElementById('<panel:encode name="refreshForm"/>')); return false;">
        <img src="<static:image relativePath="general/16x16/ico-refresh.png"/>"  border="0"
             title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "refresh"%>'>!!! Refresh</i18n:message>">
    </a>

</mvc:fragment>

<mvc:fragment name="outputClearButton">

    <a href="#" onclick="submitAjaxForm(document.getElementById('<panel:encode name="clearForm"/>')); return false;">
      <img src="<static:image relativePath="general/16x16/ico-trash.png"/>" border="0"
           title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "clear"%>'>!!! Clear</i18n:message>"/>
    </a>

</mvc:fragment>


<mvc:fragment name="outputEndButtons">
    </td>
</mvc:fragment>

<mvc:fragment name="outputEndBottom">
    </tr>
    </table>
    </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="outputEnd">
    </table>
    </form>

    <form method="post" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="refreshForm"/>">
        <factory:handler bean="<%=handler.getComponentPath()%>" action="refresh"/>
        <input type="hidden" id="<panel:encode name="refreshTimeOut"/>" name="refreshTimeOut" value="">
    </form>
    <form method="post" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="clearForm"/>">
        <factory:handler bean="<%=handler.getComponentPath()%>" action="clear"/>
    </form>
    <form method="post" action="<factory:formUrl friendly="false"/>"
          id="<panel:encode name="deleteFilteredPropertyForm"/>">
        <factory:handler bean="<%=handler.getComponentPath()%>" action="deleteFilteredProperty"/>
        <input type="hidden" id="<panel:encode name="filteredPropertyToDelete"/>" name="filteredPropertyToDelete"
               value="">
    </form>
    </td>
    </tr>
    </table>
    <script defer="true">
        setAjax('<panel:encode name="refreshForm"/>');
        setAjax('<panel:encode name="filterForm"/>');
        setAjax('<panel:encode name="clearForm"/>');
        setAjax('<panel:encode name="deleteFilteredPropertyForm"/>');
    </script>
</mvc:fragment>

</mvc:formatter>