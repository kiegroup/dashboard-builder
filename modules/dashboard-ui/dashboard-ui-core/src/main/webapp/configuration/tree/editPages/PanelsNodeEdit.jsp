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
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>

<mvc:formatter name = "org.jboss.dashboard.ui.config.components.panels.PanelsPropertiesFormatter" >

    <mvc:fragment name="outputStart">
    <table cellpadding="0" cellspacing="1" border="0" align="left" width="100%">
            <tr>
                <td>
                    <br><table cellpadding="3" cellspacing="1" border="0" align="left" width="470" class="skn-table_border">
                        
    </mvc:fragment>
    <mvc:fragment name="outputHeaderDelete">
        <td class="skn-table_header" width="10px"><i18n:message key="ui.admin.workarea.actions">!!!Actions</i18n:message></td>
    </mvc:fragment>
    <mvc:fragment name="outputHeaders">
        <mvc:fragmentValue name="value">
            <td class="skn-table_header" align="left">
                <i18n:message key="<%=(String)value%>"/>
            </td>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputStartRow">
        <tr>
    </mvc:fragment>

    <mvc:fragment name="empty">
            <td align="left" colspan="100">
                <i18n:message key="ui.panel.noDefined"/>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputDelete">
                <mvc:fragmentValue name="dbid" id="dbid">
            <mvc:fragmentValue name="estilo" id="estilo">
                <mvc:fragmentValue name="sectionId" id="sectionId">
                <td class="<%=estilo%>" align="center">
                    <div align="center"><a title="<i18n:message key="ui.admin.workarea.indexer.contentGroups.delete">!!!Borrar.</i18n:message>" href="<factory:url friendly="false" action="deletePanel" bean="org.jboss.dashboard.ui.config.components.panels.PanelsPropertiesHandler">
                        <factory:param name="dbid" value="<%=dbid%>"/>
                        <factory:param name="sectionId" value="<%=sectionId%>"/>
                    </factory:url> "
                            onclick="return confirm('<i18n:message key="ui.panel.confirmDelete">Sure?</i18n:message>');">
                        <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0" />
                    </a></div>
                </td>
            </mvc:fragmentValue>
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

    <mvc:fragment name="outputSection">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td align="center" class="<%=estilo%>"><div align="left"> <%=value%></div></td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputRegion">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td align="center" class="<%=estilo%>"><div align="left"> <%=value%></div></td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEndRow">
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>

</mvc:formatter>