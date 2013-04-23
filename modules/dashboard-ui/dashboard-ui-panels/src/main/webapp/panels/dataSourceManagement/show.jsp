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
<%@ page import="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.dataSourceManagement.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%try {%>
<mvc:formatter name="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementFormatter">
    <mvc:fragment name="outputStart">

            <table border="0" style="margin:0px;" cellspacing="0" width="800px">

    </mvc:fragment>
    <mvc:fragment name="outputNewDS">
            <tr><td width="800px"><table align="center" width="800px" cellspacing="0" cellpadding="4" border="0">
                <tr style="display:table-row; width:12px;">
                    <td class="skn-table_border" colspan="2">
                       <div style="vertical-align:middle; text-align:left;">
                           <a id="<panel:encode name="createNewDatasource"/>" href="<factory:url bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="CreateNewDatasource"/>">
                                <img style="border:none; vertical-align:middle;" src="<static:image relativePath="general/16x16/ico-add.png"/>" alt="<i18n:message key="datasource.create.title">!!create</i18n:message>" title="<i18n:message key="datasource.create.title">!!create</i18n:message>">
                                <i18n:message key="datasource.create.title">!!create</i18n:message>
                           </a>
                           <script defer>
                               setAjax('<panel:encode name="createNewDatasource"/>');
                           </script>
                       </div>
                    </td>
                </tr>
            </table></td></tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreatingNewDS">
            <tr><td width="800px"><table align="center" width="800px" cellspacing="1" cellpadding="4" border="0">
                <tr style="display:table-row; width:12px;">
                    <td class="skn-table_border" colspan="2">
                       <div style="vertical-align:middle; text-align:left;">
                                <i18n:message key="datasource.creating.title">!!create</i18n:message>
                       </div>
                    </td>
                </tr>
            </table></td></tr>
    </mvc:fragment>
    <mvc:fragment name="outputDatasourceForm">
        <tr>
                    <td width="800px">
                        <jsp:include page="newDatasourceForm.jsp"/>
                    </td>
                </tr>

    </mvc:fragment>

                    <mvc:fragment name="outputStartTable"><tr><td width="800px"> <table class="skn-table_border" cellspacing="1" cellpadding="4" border="0" align="center" width="800px">
                        <tr class="skn-table_header">
                            <td colspan="2">
                                <i18n:message key="datasource.actions">!!!Acciones</i18n:message>
                            </td>
                            <td>
                                <i18n:message key="datasource.name">
                                    !!!Nombre
                                </i18n:message>
                            </td>
                            <td>
                                <i18n:message key="datasource.type">!!!Tipo</i18n:message>
                            </td>
                            <td>
                                <i18n:message key="datasource.path">!!!Ruta</i18n:message>
                            </td>
                            <td width="1px">
                                <i18n:message
                                        key="datasource.status">!!!Estado
                                </i18n:message>
                            </td>
                        </tr>
                    </mvc:fragment>
                    <mvc:fragment name="outputDataSource">
                        <tr>
                            <td width="1px">
                                &nbsp;
                            </td>
                            <td width="1px">
                                &nbsp;
                            </td>
                            <td><b>
                                <i18n:message key="datasource.hardcoded.name">!!!Local</i18n:message>
                            </b></td>
                            <td><b>
                                <i18n:message
                                        key="datasource.hardcoded.type">!!!Hardcoded
                                </i18n:message>
                            </b></td>
                            <td></td>
                            <td align="center"><img src="<static:image relativePath="general/16x16/ico-menu_permissions_g.png"/>" border="0"
                                                    title="<i18n:message key='datasource.test.ok'>!!!OK</i18n:message>"/>
                            </td>
                        </tr>
                    </mvc:fragment>
                    <mvc:fragment name="output">
                        <mvc:fragmentValue name="dataSName" id="dataSName">
                            <mvc:fragmentValue name="index" id="index">
                                <mvc:fragmentValue name="selected" id="selected"><%
                                    String className, altClass;
                                    if (((Boolean) selected).booleanValue()) {
                                        className = "skn-even_row_alt";
                                        altClass = "skn-even_row_alt";
                                    } else {
                                        if (((Integer) index).intValue() % 2 == 0) {
                                            className = "skn-even_row";
                                            altClass = "skn-even_row_alt";
                                        } else {
                                            className = "skn-odd_row";
                                            altClass = "skn-odd_row_alt";
                                        }
                                    }

                                %>
                                    <tr class="<%=className%>" onmouseover="className='<%=altClass%>'"
                                        onmouseout="className='<%=className%>'">
                                        <td align="center" width="1px"><a
                                                title="<i18n:message key="datasource.edit">!!!Editar</i18n:message>"
                                                href="<factory:url bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="editDataSource"> <factory:param name="<%=DataSourceManagementHandler.PARAM_DS_NAME%>" value="<%=dataSName%>"/></factory:url>"
                                                id="<panel:encode name='<%="editLink"+dataSName%>'/>">
                                            <img src="<static:image relativePath="general/16x16/ico-edit.png"/>" border="0" />
                                        </a>
                                        <script defer>
                                            setAjax('<panel:encode name='<%="editLink"+dataSName%>'/>');
                                        </script>

                                        </td>
                                        <td align="center" width="1px"><a
                                                title="<i18n:message key="datasource.delete">!!!Borrar</i18n:message>"
                                                href="<factory:url bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="deleteDataSource"> <factory:param name="<%=DataSourceManagementHandler.PARAM_DS_NAME%>" value="<%=dataSName%>"/></factory:url>"
                                                id="<panel:encode name='<%="deleteLink"+dataSName%>'/>"
                                                onclick="return confirm('<i18n:message key="datasource.delete.confirm">!!!Sure?</i18n:message>')">
                                            <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0" />
                                        </a>
                                        <script defer>
                                            setAjax('<panel:encode name='<%="deleteLink"+dataSName%>'/>');
                                        </script>
                                        </td>
                                        <td nowrap="nowrap" width="200px">
                                            <div style="width:200px; height:18px; overflow:hidden; vertical-align:middle"
                                                    title="<mvc:fragmentValue name="Name"/>">
                                                <mvc:fragmentValue name="Name"/>
                                            </div>
                                        </td>
                                        <mvc:fragmentValue name="entryType" id="entryType">
                                        <td nowrap="nowrap" width="150px">
                                                <div style="width:150px; height:18px; overflow:hidden; vertical-align:middle"
                                                    title="<i18n:message key='<%="datasource.type."+entryType%>'>!!!Type </i18n:message>">
                                                    <i18n:message key='<%="datasource.type."+entryType%>'>!!!Type</i18n:message>
                                                </div>
                                        </td>
                                        </mvc:fragmentValue>
                                        <td nowrap="nowrap" width="200px" >
                                            <div style="width:200px; height:18px; overflow:hidden; vertical-align:middle"
                                                    title="<mvc:fragmentValue name="entryPath"/>">
                                                <mvc:fragmentValue name="entryPath"/>
                                            </div>
                                        </td>
                                        <td align="center" >
                                            <div style=" height:18px; overflow:hidden; vertical-align:middle">
                                                <mvc:fragmentValue name="statusIcon" id="statusIcon">
                                                <%  String sIcon = (String) statusIcon; if (sIcon  != null && sIcon.equals(DataSourceManagementHandler.RESULT_OK)) { %>
                                                    <img src="<static:image relativePath="general/16x16/ico-menu_permissions_g.png"/>" border="0" title="<i18n:message key='datasource.test.ok'>!!!OK</i18n:message>"/>
                                                <% } else {
                                                    String messageAlt = StringUtils.replace(StringUtils.remove(StringEscapeUtils.escapeHtml(((String) statusIcon)), '\n'), "\"", "\'");
                                                    String messageAlert = StringUtils.replace(messageAlt, "'", "\\'"); %>
                                                <a href="#" onclick="window.alert('<i18n:message key='datasource.test.wrong'>!!!Bad</i18n:message>');return false;">
                                                    <img src="<static:image relativePath="general/16x16/ico-menu_permissions_r.png"/>" border="0" title="<i18n:message key='datasource.test.wrong'>!!!Bad</i18n:message>"/></a>
                                               <% } %>
                                               </mvc:fragmentValue>
                                          </div>
                                        </td>
                                    </tr>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragment>
                    <mvc:fragment name="outputEndTable"></td></tr></table></mvc:fragment>

      <mvc:fragment name="outputEnd">
          </table>
      </mvc:fragment>
</mvc:formatter>
<%
    } catch (Throwable e) {
        e.printStackTrace();
    }
%>
