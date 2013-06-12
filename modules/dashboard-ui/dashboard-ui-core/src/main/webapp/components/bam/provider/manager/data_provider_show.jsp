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
<%@ page import="org.jboss.dashboard.ui.components.DataProviderHandler" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jboss.dashboard.ui.formatters.DataProviderFormatter">
    <mvc:fragment name="outputStart">
      <table border="0" cellspacing="0" cellpadding="4" width="650px">
    </mvc:fragment>

    <mvc:fragment name="outputEmpty">
        <tr>
            <td width="100%">
                <span class="skn_error">
                    <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "noDataProviders"%>'>!!no hay proveedores de datos</i18n:message>
                </span>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputNewDataProvider">
           <tr><td width="100%"><table align="center" width="100%" cellspacing="0" cellpadding="4" border="0">
                <tr style="display:table-row; width:12px;">
                    <td class="skn-table_border">
                       <a id="<factory:encode name="createNewDataProvider"/>" href="<factory:url bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="StartCreateNewDataProvider"/>">
                            <img style="border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-add.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "createNewDataProvider"%>'>!!create</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "createNewDataProvider"%>'>!!create</i18n:message>">
                            <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "createNewDataProvider"%>'>!!create</i18n:message>
                       </a>&nbsp;
                       <script defer>
                           setAjax('<factory:encode name="createNewDataProvider"/>');
                       </script>
                    </td>
                </tr>
            </table></td></tr>
    </mvc:fragment>
    <mvc:fragment name="outputStartDataProviders">
        <tr><td width="100%">
        <table class="skn-table_border" cellspacing="1" cellpadding="4" border="0" align="center" width="100%">
        <tr class="skn-table_header">
            <td colspan="3">
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "header_actions"%>'>!!! Acciones</i18n:message>
            </td>
            <td>
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "header_dataProviderName"%>'>!!! Nombre</i18n:message>
            </td>
            <td>
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "selectProviderType"%>'>!!! Tipo</i18n:message>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputDataProvider">
        <mvc:fragmentValue name="canDelete" id="canDelete">
        <mvc:fragmentValue name="canEdit" id="canEdit">
        <mvc:fragmentValue name="canEditProperties" id="canEditProperties">
        <mvc:fragmentValue name="code" id="code">
        <mvc:fragmentValue name="index" id="index">
        <mvc:fragmentValue name="numberOfKPIs" id="numberOfKPIs">
        <mvc:fragmentValue name="deleteMessage" id="deleteMessage">
            <%
                int nkpis = numberOfKPIs == null ? 0 : (Integer) numberOfKPIs;
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
                    <td align="center" width="1">
                        <% if (((Boolean)canEdit).booleanValue()) { %>
                        <a title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProvider"%>'>!!!Editar</i18n:message>"
                            href="<factory:url bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="editDataProvider"> <factory:param name="<%=DataProviderHandler.PARAM_PROVIDER_CODE%>" value="<%=code%>"/></factory:url>"
                            id="<factory:encode name='<%="editLink"+code%>'/>">
                                <img style="border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-edit_page01.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProvider"%>'>!!!Editar</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProvider"%>'>!!!Editar</i18n:message>">

                           </a>
                        <% } else { %>
                                <img style="opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 ); border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-edit_page01.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProvider"%>'>!!!Editar</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProvider"%>'>!!!Editar</i18n:message>">
                        <% } %>
                    <script defer>
                        setAjax('<factory:encode name='<%="editLink"+code%>'/>');
                    </script>
                    </td>
                    <td align="center" width="1">
                        <% if (((Boolean)canEditProperties).booleanValue()) { %>
                        <a title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProviderProperties"%>'>!!!Editar propiedades</i18n:message>"
                            href="<factory:url bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="editDataProviderProperties"> <factory:param name="<%=DataProviderHandler.PARAM_PROVIDER_CODE%>" value="<%=code%>"/></factory:url>"
                            id="<factory:encode name='<%="editPropertiesLink"+code%>'/>">
                                <img style="border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-edit_page02.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProviderProperties"%>'>!!!Editar propiedades</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProviderProperties"%>'>!!!Editar propiedades</i18n:message>">
                               
                           </a>
                        <% } else { %>
                                <img style="opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 ); border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-edit_page01.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProviderProperties"%>'>!!!Editar propiedades</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editDataProviderProperties"%>'>!!!Editar propiedades</i18n:message>">
                        <% } %>
                    <script defer>
                        setAjax('<factory:encode name='<%="editPropertiesLink"+code%>'/>');
                    </script>
                    </td>
                    <td align="center" width="1">
                        <% if (((Boolean)canDelete).booleanValue()) { %>
                        <a title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar</i18n:message>"
                           id="<factory:encode name='<%="dropLinks"+code%>'/>"
                            <% if (nkpis > 0) { %>
                               href="#"
                               onclick="alert('<%= deleteMessage %>');return false;"
                            <% } else { %>
                               href="<factory:url bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="deleteDataProvider"> <factory:param name="<%=DataProviderHandler.PARAM_PROVIDER_CODE%>" value="<%=code%>"/></factory:url>"
                               onclick="return confirm('<%= deleteMessage %>');"
                            <% } %> >
                                <img style="border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-trash.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar</i18n:message>">
                           </a>
                        <% } else { %>
                                <img style="opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 ); border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-trash.png"/>" alt="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar</i18n:message>" title="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "deleteDataProvider"%>'>!!!Borrar</i18n:message>">
                        <% } %>
                    <script defer>
                        setAjax('<factory:encode name='<%="dropLink"+code%>'/>');
                    </script>
                    </td>
                    <td align="left">
                         <div style="width:360px; height:15px; text-align:left; overflow:hidden; vertical-align:middle"
                            title="<mvc:fragmentValue name="dataProviderName" />">
                             <mvc:fragmentValue name="dataProviderName"/>
                         </div>
                    </td>
                    <td align="left">
                        <div style="width:140px; height:15px; text-align:left; overflow:hidden; vertical-align:middle"
                                                    title="<mvc:fragmentValue name="dataProviderType" />">
                            <mvc:fragmentValue name="dataProviderType"/>
                         </div>
                    </td>
            </tr>

        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEndDataProviders">
        </table></td></tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
</mvc:formatter>
