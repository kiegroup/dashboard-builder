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
<%@ page import="java.util.Map"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.messages" />
<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="clearAfterRender" propValue="false" />
<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesFormatter">
    <mvc:fragment name="outputStart">
        <form action="<factory:formUrl friendly="false"/>" method="POST">
        <factory:handler bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" action="save"/>
        <table cellpadding="4" cellspacing="1" border="0" align="left" class="skn-table_border">
        <tr class="skn-table_header"><td colspan="2"><i18n:message key="ui.workspace.workspaceProperties">!!Propiedades del Workspace</i18n:message></td></tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>

    <mvc:fragment name="outputName">
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
                                    <td>&nbsp;</td>
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
    <mvc:fragment name="outputTitle">
        <mvc:fragmentValue name="error" id="error">
                <tr>
                    <td align="left" class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row">
                        <i18n:message key="ui.workspace.title">!!Title</i18n:message>
                    </td>
                    <td align="left">
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="skinsStart">
            <tr>
            <mvc:fragmentValue name="error" id="error">
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.workspace.look"/>
                </td>
            </mvc:fragmentValue>
                <td width="75%" height="18" nowrap="NOWRAP" align="left">
                    <select style="width:250px" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="skin"/>">
    </mvc:fragment>
    <mvc:fragment name="outputSkin">
        <option value="<mvc:fragmentValue name="skinId"/>">
            <mvc:fragmentValue name="skinDescription" id="skinDescription">
                <panel:localize data="<%=(Map)skinDescription%>"/>
            </mvc:fragmentValue>
        </option>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedSkin">
        <option selected class="skn-important" value="<mvc:fragmentValue name="skinId"/>">
            <mvc:fragmentValue name="skinDescription" id="skinDescription">
                <panel:localize data="<%=(Map)skinDescription%>"/>
            </mvc:fragmentValue>
         </option>
    </mvc:fragment>
    <mvc:fragment name="skinsEnd">
                    </select>
                </td>
            </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="envelopesStart">
            <tr>
            <mvc:fragmentValue name="error" id="error">
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.workspace.envelope"/>
                </td>
            </mvc:fragmentValue>
                <td width="75%" height="18" nowrap="NOWRAP" align="left">
                    <select style="width:250px" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="envelope"/>">
    </mvc:fragment>
    <mvc:fragment name="outputEnvelope">
        <option value="<mvc:fragmentValue name="envelopeId"/>">
            <mvc:fragmentValue name="envelopeDescription" id="envelopeDescription">
                <panel:localize data="<%=(Map)envelopeDescription%>"/>
            </mvc:fragmentValue>
        </option>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedEnvelope">
        <option selected class="skn-important" value="<mvc:fragmentValue name="envelopeId"/>">
            <mvc:fragmentValue name="envelopeDescription" id="envelopeDescription">
                <panel:localize data="<%=(Map)envelopeDescription%>"/>
            </mvc:fragmentValue>
        </option>
    </mvc:fragment>
    <mvc:fragment name="envelopesEnd">
                    </select>
                </td>
            </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputUrl">
        <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.workspace.workspaceURL"/>
                </td>
                <td width="75%" height="18" nowrap="NOWRAP" align="left">
                    <mvc:fragmentValue name="urlPreffix"/><input size="20" class="skn-input" maxlength="50"
                           name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="url"/>"
                           value="<factory:property bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="url"/>">
                </td>
            </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="homeSearchModeStart">
            <tr>
            <mvc:fragmentValue name="error" id="error">
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.workspace.homeSearchMode"/>
                </td>
            </mvc:fragmentValue>
                <td width="75%" height="18" nowrap="NOWRAP" align="left">
                    <select style="width:250px" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="homeSearchMode"/>">
    </mvc:fragment>
    <mvc:fragment name="outputMode">
                      <option value="<mvc:fragmentValue name="modeId"/>">
                        <mvc:fragmentValue name="modeDescription" id="modeDescription">
                          <i18n:message key="<%=(String)modeDescription%>"><%=(String)modeDescription%></i18n:message>
                        </mvc:fragmentValue>
                      </option>
    </mvc:fragment>
    <mvc:fragment name="outputSelectedMode">
                      <option selected class="skn-important" value="<mvc:fragmentValue name="modeId"/>">
                        <mvc:fragmentValue name="modeDescription" id="modeDescription">
                          <i18n:message key="<%=(String)modeDescription%>"><%=(String)modeDescription%></i18n:message>
                        </mvc:fragmentValue>
                      </option>
    </mvc:fragment>
    <mvc:fragment name="homeSearchModeEnd">
                    </select>
                </td>
            </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputDefaultWorkspace">
        <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.workspace.defaultWorkspace"/>
                </td>
                <td width="75%" height="18" nowrap="NOWRAP" align="left">
                    <input type="hidden" id="<panel:encode name="defaultWorkspaceHidden"/>" value="<factory:property bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="defaultWorkspace"/>"
                           name="<factory:bean bean="org.jboss.dashboard.ui.config.components.workspace.WorkspacePropertiesHandler" property="defaultWorkspace"/>">
                    <input type="checkbox"
                           <%=((Boolean)value).booleanValue()?"checked":""%>
                            onclick="document.getElementById('<panel:encode name="defaultWorkspaceHidden"/>').value=this.checked">
                </td>
            </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        <tr>
            <td colspan="2" align="center">
                <br>
<%--
                <table cellpadding="0" cellspacing="0" border="0" width="80%" align="center"><tr><td align="center">
--%>
                <input class="skn-button" type="submit" value='<i18n:message key="ui.saveChanges"/>'>
<%--
                </td>
                <td align="center">
                <input name="Submit23" type="reset" class="skn-button_alt" value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
                </td>
                </tr></table>
--%>
            </td>
        </tr>
        </table>
        </form>
    </mvc:fragment>
</mvc:formatter>
