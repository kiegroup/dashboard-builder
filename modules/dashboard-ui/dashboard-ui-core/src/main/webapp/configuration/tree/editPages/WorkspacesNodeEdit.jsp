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
<%@ page import="org.jboss.dashboard.workspace.Parameters"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.messages" />
<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="clearAfterRender" propValue="false" />
<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>

<mvc:formatter name = "org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesFormatter" >
    <mvc:fragment name="outputStart">
        <form method="POST" action="<factory:formUrl friendly="false"/>">
            <factory:handler bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler" action="createWorkspace"/>
        <table cellpadding="4" cellspacing="1" border="0" width="100%" ><tr><td>
        <table cellpadding="4" cellspacing="1" border="0" width="470" align="left" class="skn-table_border">
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
    <mvc:fragment name="outputDelete">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td class="<%=estilo%>" align="center">
                    <div align="center"><a title="<i18n:message key="ui.admin.workarea.indexer.contentGroups.delete">!!!Borrar.</i18n:message>" href="<factory:url friendly="false" action="deleteWorkspace" bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler">
                        <factory:param name="workspaceId" value="<%=value%>"/>
                    </factory:url> "
                            onclick="return confirm('<i18n:message key="ui.workspace.confirmDelete">Sure?</i18n:message>');">
                        <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0" />
                    </a></div>
                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputCantDelete">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td class="<%=estilo%>" width="10px" align="center">

                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputName">
        <td align="center" class="<mvc:fragmentValue name="estilo"/>">
            <div align="left">
                <mvc:fragmentValue name="workspaceId" id="workspaceId">
                <a href="<factory:url bean="org.jboss.dashboard.ui.config.TreeShortcutHandler" friendly="false" action="workspaceProperties">
                            <factory:param name="<%=Parameters.DISPATCH_IDWORKSPACE%>" value="<%=workspaceId%>"/>
                         </factory:url>">
                    <mvc:fragmentValue name="value"/>
                </a>
                </mvc:fragmentValue>
            </div>
        </td>
    </mvc:fragment>
    <mvc:fragment name="outputNameDisabled">
        <td align="center" class="<mvc:fragmentValue name="estilo"/>">
            <div align="left">
                <mvc:fragmentValue name="workspaceId" id="workspaceId">
                <span class="skn-disabled">
                    <mvc:fragmentValue name="value"/>
                </span>
                </mvc:fragmentValue>
            </div>
        </td>
    </mvc:fragment>
    <mvc:fragment name="outputTitle">
        <mvc:fragmentValue name="value" id="value">
            <mvc:fragmentValue name="estilo" id="estilo">
                <td align="center" class="<%=estilo%>"><div align="left"> <%=value%></div></td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEndRow">
        </tr>
    </mvc:fragment>
    <mvc:fragment name="endTable">
        </table></td></tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceStart">
            <tr><td>
        <table border="0" cellpadding="4" cellspacing="1" align="left" width="470" class="skn-table_border">
                <tr class="skn-table_header">
                    <td colspan="2">
                        <i18n:message key="ui.workspace.createNewWorkspace">!!Create new workspace</i18n:message>
                    </td>
                </tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceName">
        <mvc:fragmentValue name="error" id="error">
                <tr>
                    <td align="left" width="25%" nowrap="nowrap" class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row">
                        <i18n:message key="ui.workspace.name">!!Name</i18n:message>
                    </td>
                    <td align="left">
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputI18nStart">
        <mvc:fragmentValue name="name" id="name">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <td>
                                    <select class="skn-input" onchange="
                                        var elements = this.form.elements;
                                        var selectedOption = this.options[this.selectedIndex];
                                        for(i =0 ; i<elements.length; i++){
                                            var element = elements[i];
                                            if (element.tagName.toUpperCase() == 'INPUT' && element.type.toUpperCase()=='TEXT') {
                                                if (element.name.indexOf('<%=name%>_') == 0) {
                                                    if(element.name == '<%=name%>_' + selectedOption.value ){
                                                        element.style.display = 'block';
                                                    } else{
                                                        element.style.display = 'none';
                                                    }
                                                }
                                            }
                                        }">
                                      <%
                                          Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
                                          for (int i = 0; i < locales.length; i++) {
                                            Locale locale = locales[i];
                                      %>
                                        <option <%= LocaleManager.currentLang().equals(locale.getLanguage()) ? "selected" : ""%> value="<%=locale%>">
                                          <%= StringUtils.capitalize(locale.getDisplayName(locale)) %>
                                        </option>
                                      <% } %>
                                        </select>
                                    </td>
                                    <td>
                                    <td>&nbsp;
                                    </td>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputInput">
        <mvc:fragmentValue name="name" id="name">
        <mvc:fragmentValue name="langId" id="langId">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="maxlength" id="maxlength">
                                        <input type="text" class="skn-input" name="<%=name + "_" + langId%>"
                                               maxlength="<%=maxlength%>"
                                               id="<%=name + "_" + langId%>"
                                               style="width:245px; <%=LocaleManager.currentLang().equals((String)langId) ? "" : "display:none" %>"
                                               value="<%=value%>">
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputI18nEnd">
                                </td>
                            </tr>
                        </table>
    </mvc:fragment>
    <mvc:fragment name="outputEndLine">
                    </td>
                </tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceTitle">
        <mvc:fragmentValue name="error" id="error">
                <tr>
                    <td align="left" class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row">
                        <i18n:message key="ui.workspace.title">!!Title</i18n:message>
                    </td>
                    <td align="left">
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputCreateWorkspaceSkinsStart">
                <tr>
                    <td align="left" class="skn-even_row">
                        <i18n:message key="ui.workspace.look"/>
                    </td>
                    <td align="left">
                        <select style="width:250px" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler" property="skinId"/>" >
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceSkins">
        <mvc:fragmentValue name="skinId" id="skinId">
            <mvc:fragmentValue name="skinTitle" id="skinTitle">
                <mvc:fragmentValue name="selected" id="selected">
                            <option <%=selected%> value="<%=skinId%>" > <%=skinTitle%> </option>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceSkinsEnd">
        </select>
        </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceEmvelopesStart">
                <tr>
                    <td align="left" class="skn-even_row">
                        <i18n:message key="ui.workspace.envelope"/>
                    </td>
                    <td align="left">
                        <select style="width:250px" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler" property="envelopeId"/>" >

    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceEmvelopes">
        <mvc:fragmentValue name="envelopeId" id="envelopeId">
            <mvc:fragmentValue name="envelopeTitle" id="envelopeTitle">
                <mvc:fragmentValue name="selected" id="selected">
                            <option <%=selected%> value="<%=envelopeId%>" > <%=envelopeTitle%></option>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceEmvelopesEnd">
                        </select>
                    </td>
                </tr>
    </mvc:fragment>
    <mvc:fragment name="outputCreateWorkspaceEnd">
                     <tr>
                        <td colspan="2" align="center">
                            <input class="skn-button" type="submit" value="<i18n:message key="ui.workspace.createNewWorkspace"/>">
<%--
                                                &nbsp;&nbsp;
                            <input name="Submit23" type="reset" class="skn-button_alt" value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
--%>


            </td>
            </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
            </table>
        </td></tr>
        </table>
        </form>
    </mvc:fragment>
</mvc:formatter>


    <%  boolean showSectionsDiagnose =  false;
        if(showSectionsDiagnose){%>
    <div>
        <a
            href="<factory:url bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler" action="diagnoseWorkspaces"/>"
            >Diagnose workspaces</a>
        <br><br>
        <a
            href="<factory:url bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacesPropertiesHandler" action="diagnoseWorkspacesAndFix"/>"
            >Fix workspaces</a>
    </div>
    <%}%>


