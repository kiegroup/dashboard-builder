<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
<%@ page import="org.jboss.dashboard.ui.components.DataProviderHandler" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>
<i18n:bundle id="bundleUI" baseName="org.jboss.dashboard.ui.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.DataProviderFormatter">
    <mvc:fragment name="outputStart">
        <form style="margin:0px;" action="<factory:formUrl friendly="false"/>" method="post" id="<factory:encode name="deleteDataProviderForm"/>">
        <factory:handler bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="deleteDataProvider"/>
        <table border="0" style="margin:0px;" cellspacing="0" width="650px">
    </mvc:fragment>

    <mvc:fragment name="outputTitle">
        <mvc:fragmentValue name="description" id="description">
            <tr>
                <td nowrap="nowrap">
                    <div style="vertical-align:middle; text-align:left;" class="skn-title3">
                        <i18n:message bundleRef="bundle" key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar proveedor de datos</i18n:message>&nbsp;<%=description%>
                    </div>
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputTableStart">
            <tr>
            <td>
            <table class="skn-table_border" cellspacing="1" cellpadding="4" border="0" align="center" width="100%">
    </mvc:fragment>


    <mvc:fragment name="outputKpiHeaders">
        <tr class="skn-table_header">
            <td><i18n:message bundleRef="bundle" key='element.kpi'>!!!KPI</i18n:message> &nbsp; <i18n:message bundleRef="bundleUI" key='ui.workspace.description'>!!!Descripcion</i18n:message></td>
            <td><i18n:message bundleRef="bundleUI" key='ui.sections.section'>!!!Pagina</i18n:message></td>
            <td><i18n:message bundleRef="bundleUI" key='ui.workspace'>!!!Espacio de trabajo</i18n:message></td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputKpi">
        <mvc:fragmentValue name="description" id="description">
            <mvc:fragmentValue name="pageTitle" id="pageTitle">
                <mvc:fragmentValue name="workspaceTitle" id="workspaceTitle">
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
                        <tr class="<%=className%>" onmouseover="className='<%=altClass%>'" onmouseout="className='<%=className%>'">
                            <td><%=description%></td>
                            <td><%=pageTitle%></td>
                            <td><%=workspaceTitle%></td>
                        </tr>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputTableEnd">
        </table>
        </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputKPIMessage">
        <mvc:fragmentValue name="message" id="message">
            <tr style="height: 10px;">
                <td>
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <%=message%>
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputNO_KPIMessage">
        <tr style="height: 10px;">
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                <i18n:message bundleRef="bundle"  key='<%=DataProviderHandler.I18N_PREFFIX + "confirmDelete"%>'>!!!Seguro que desea eliminar el proveedor de datos</i18n:message>
            </td>
        </tr>
    </mvc:fragment>
    
    <mvc:fragment name="outputButtons">
        <tr>
            <td width="100%" align="center" style="padding-top: 10px;">
                <input class="skn-button" type="button" value="<i18n:message bundleRef="bundleUI" key='ui.admin.workarea.envelopes.delete'>!!!Borrar</i18n:message>"
                       onclick="submitAjaxForm(this.form);">&nbsp;
                <input class="skn-button_alt" type="button" value="<i18n:message bundleRef="bundle"  key='<%=DataProviderHandler.I18N_PREFFIX + "cancel"%>'>!!!Cancelar</i18n:message>"
                       onclick="submitAjaxForm(document.getElementById('<factory:encode name="goToShowPageForm"/>'))"/>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEnd">
        </table>
        </form>
        <script defer>
            setAjax('<factory:encode name="deleteDataProviderForm"/>');
        </script>
        <form action="<factory:formUrl friendly="false"/>" method="post" id="<factory:encode name="goToShowPageForm"/>">
            <factory:handler bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="cancel"/>
        </form>
        <script defer>
            setAjax('<factory:encode name="goToShowPageForm"/>');
        </script>
    </mvc:fragment>


</mvc:formatter>