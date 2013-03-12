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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name = "org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesFormatter" >
    <mvc:fragment name="outputStart">

        <table cellpadding="3" cellspacing="1" border="0"  class="skn-table_border" width="470px">
    </mvc:fragment>

    <mvc:fragment name="outputHeaderDelete">
        <td class="skn-table_header"><i18n:message key="ui.admin.workarea.actions">!!!Actions</i18n:message></td>
    </mvc:fragment>

    <mvc:fragment name="outputHeaders">
        <mvc:fragmentValue name="value">
            <td class="skn-table_header" width="75%" nowrap="nowrap">
                <i18n:message key="<%=(String)value%>"/>
            </td>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputStartRow">
        <tr>
    </mvc:fragment>

    <mvc:fragment name="outputEndRow">
        </tr>
    </mvc:fragment>

    <mvc:fragment name="empty">
        <td align="left" colspan="100">
            <i18n:message key="ui.admin.configuration.panelInstances.noDefined"/>
        </td>
    </mvc:fragment>

    <mvc:fragment name="outputDelete">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td class="<%=estilo%>">
                    <div align="center">
                        <a title="<i18n:message key="ui.admin.workarea.indexer.contentGroups.delete">!!!Borrar.</i18n:message>"
                           href="<factory:url friendly="false" action="deletePanel" bean="org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesHandler"><factory:param name="panelId" value="<%=value%>"/></factory:url>"
                           onclick="return confirm('<i18n:message key="ui.panel.confirmDelete">Sure?</i18n:message>');">
                            <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0"/>
                        </a></div>
                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputTitle">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td align="center" class="<%=estilo%>"><div align="left"> <%=value%></div></td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputPanelsNumber">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td align="center" class="<%=estilo%>"><div align="left"> <%=value%></div></td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>


    <mvc:fragment name="outputEnd">
        </table>
        <table cellpadding="3" cellspacing="1" border="0"  width="470px">
            <tr><td>
                <div style="text-align:right; padding-top:10px;">
                    <a href="<factory:url bean="org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesHandler" action="deleteUselessPanelInstances"/>">
                        <i18n:message key="ui.admin.deleteUnusedInstances">
                            !!!Delete empty instances
                        </i18n:message>
                    </a>
                </div>
            </td></tr>
            <tr><td>

                <div style="text-align:right; padding-bottom:10px;">
                    <a href="<factory:url bean="org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancesPropertiesHandler" action="deleteUselessPanelsAndInstances"/>">
                        <i18n:message key="ui.admin.deleteUnusedPanelsAndInstances">
                            !!!Delete panels and empty instances
                        </i18n:message>
                    </a>
                </div>
            </td></tr>

        </table>
    </mvc:fragment>

</mvc:formatter>
