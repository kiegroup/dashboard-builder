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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.Map"%>
<%@ page import="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="security_taglib.tld" prefix="security" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.sections.SectionCreationFormatter">
    <mvc:fragment name="outputStart">

<form style="margin:0px;" method="POST" name="createSection" action="<factory:formUrl friendly="false"/>" id="createSection"
        onsubmit="this.onsubmit=function(){return false;};return true;">
    <factory:handler bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" action="createSection"/>
    <table width="100%" border="0" cellpadding="4" cellspacing="1" align="left" class="skn-table_border">

    </mvc:fragment>

    <mvc:fragment name="outputName">
        <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
        <tr class="skn-table_header">
            <td colspan="2">
                <i18n:message key="ui.sections.createNew">!!Create new section</i18n:message>
            </td>
        </tr>
        <tr>
            <td align="left" width="25%" class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row">
                <i18n:message key="ui.title">!!Title</i18n:message>
            </td>
            <td align="left" width="75%">
                <table>
                    <tr>
                        <td>
                            <select class="skn-input" onchange="
                                var elements = this.form.elements;
                                var selectedOption = this.options[this.selectedIndex];
                                for(i =0 ; i<elements.length; i++){
                                    var element = elements[i];
                                    if (element.tagName.toUpperCase() == 'INPUT' && element.type.toUpperCase()=='TEXT') {
                                        if (element.name.indexOf('name_') == 0) {
                                            if(element.id == 'section_title_' + selectedOption.value) {
                                                element.style.display= 'block';
                                            } else{
                                                element.style.display= 'none';
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
                          <%
                              }
                          %>
                            </select>
                        </td>
                        <td>
                          <%
                              String[] langs = LocaleManager.lookup().getLangs();
                                for (int i = 0; i < langs.length; i++) {
                                  String langId = langs[i];
                          %>
                            <input id="section_title_<%=langId%>" class="skn-input"
                                   type="text" name="<%="name_"+langId%>" style=" <%=LocaleManager.currentLang().equals(langId)?"":"display:none" %>"
                                   value="<%=StringUtils.defaultString(value != null ? (String)((Map)value).get(langId) : "")%>">
                          <%
                              }
                          %>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputParentsStart">
        <tr>
            <td align="left" width="25%" class="skn-even_row">
                <i18n:message key="ui.sections.parentSection">!!Parent</i18n:message>
            </td>
            <td align="left" width="75%">
                <select style="width:250px" class="skn-input"
                        name="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="parent"/>" >
                    <option value="" selected>
                        -- <i18n:message key="ui.noOne"/> --
                    </option>
    </mvc:fragment>
    <mvc:fragment name="outputParent">
        <option value="<mvc:fragmentValue name="parentId"/>" <mvc:fragmentValue name="selected"/>>
                        <mvc:fragmentValue name="parentTitle"/>
                    </option>
    </mvc:fragment>
    <mvc:fragment name="outputParentsEnd">
                </select>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputSkinsStart">
        <tr>
            <td align="left" class="skn-even_row">
                <i18n:message key="ui.workspace.look"/>
            </td>
            <td align="left">
                <select style="width:250px" class="skn-input" name="<factory:bean
                        bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="skin"/>" >
    </mvc:fragment>
    <mvc:fragment name="outputSkin">
        <mvc:fragmentValue name="skinId" id="skinId">
        <mvc:fragmentValue name="skinTitle" id="skinTitle">
        <mvc:fragmentValue name="selected" id="selected">
                    <option value="<%=skinId%>" <%=selected%>><%=skinTitle%></option>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputSkinsEnd">
                </select>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnvelopesStart">
        <tr>
            <td align="left" class="skn-even_row">
                <i18n:message key="ui.workspace.envelope"/>
            </td>
            <td align="left">
                <select style="width:250px" class="skn-input" name="<factory:bean
                    bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="envelope"/>" >
    </mvc:fragment>
    <mvc:fragment name="outputEnvelope">
        <mvc:fragmentValue name="envelopeId" id="envelopeId">
        <mvc:fragmentValue name="envelopeTitle" id="envelopeTitle">
        <mvc:fragmentValue name="selected" id="selected">
                    <option value="<%=envelopeId%>" <%=selected%> ><%=envelopeTitle%></option>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnvelopesEnd">
                </select>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputLayoutsStart">
        <tr>
            <td align="left" class="skn-even_row">
                <i18n:message key="ui.section.layout"/>
            </td>
            <td align="left">
                <select style="width:250px" class="skn-input" name="<factory:bean
                    bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="layout"/>"
                    onchange="document.getElementById('save_action').value='<%=SectionsPropertiesHandler.ACTION_PREVIEW%>';
                              this.form.submit();">
    </mvc:fragment>
    <mvc:fragment name="outputLayout">
        <mvc:fragmentValue name="layoutId" id="layoutId">
        <mvc:fragmentValue name="selected" id="selected">
                    <option value="<%=layoutId%>" <%=selected%> >
                        <mvc:fragmentValue name="layoutDescription"/>
                    </option>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputLayoutsEnd">
                </select>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="layoutPreview">
        <mvc:fragmentValue name="itemId" id="itemId">
        <mvc:fragmentValue name="template" id="template">
        <tr align="center">
            <td></td>
            <td align="center">
                <resource:page id="template" category="layout" categoryId="<%=(String)itemId%>" resourceId="JSP"/>
                <br>
                <table width="207px" cellpadding="0" cellspacing="0" border="0" align="left">
                    <tr>
                        <td width="100%" align="center">
                            <resource:image category="layout" categoryId="<%=(String)itemId%>" resourceId="IMG">
                                <jsp:include page="<%=(String)template%>" flush="true"/>
                            </resource:image>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        <tr>
            <td colspan="2" align="center">
                <input type="hidden" id="save_action"
                       name="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="action"/>">
                <table cellpadding="10" cellspacing="0" border="0" width="100%" align="center">
                    <tr>
                        <td width="50%" align="right">
                            <input name="save" class="skn-button" type="submit" value="<i18n:message key="ui.sections.createNew"/>"
                                   onclick="document.getElementById('save_action').value='<%=SectionsPropertiesHandler.ACTION_SAVE%>'; this.onclick=function(){this.type='button';return false;};return true;">
                        </td>
                        <td width="50%" align="left">
                            <input name="cancel" type="submit" class="skn-button_alt"
                                   value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>"
                                   onclick="document.getElementById('save_action').value='<%=SectionsPropertiesHandler.ACTION_CANCEL%>'">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
    </mvc:fragment>

</mvc:formatter>