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
<%@ page import="java.util.Map" %>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"/>


<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.messages" />
<factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="clearAfterRender" propValue="true" />
<factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>
<mvc:formatter name="org.jboss.dashboard.ui.config.components.section.SectionPropertiesFormatter">
<mvc:fragment name="outputStart">
    <form method="post" name="editSection" edit="editSection" action="<factory:formUrl friendly="false"/>">
        <factory:handler bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" action="save"/>
        <table cellpadding="4" cellspacing="1" border="0" align="left" width="470" class="skn-table_border">
            <tr>
                <td colspan="3" class="skn-table_header">
                    <i18n:message key="ui.sections.currentProperties">!!!Propiedades de la pagina</i18n:message>
                </td>
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputStartTitle">
    <mvc:fragmentValue name="error" id="error">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.title"/>
                </td>
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td>
                <mvc:formatter name="org.jboss.dashboard.ui.formatters.ForFormatter">
                    <mvc:formatterParam name="factoryElement" value="org.jboss.dashboard.LocaleManager"/>
                    <mvc:formatterParam name="property" value="platformAvailableLocales"/>
                    <mvc:fragment name="outputStart">
                                <select class="skn-input" onchange="
                                    var elements = this.form.elements;
                                    var selectedOption = this.options[this.selectedIndex];
                                    for(i =0 ; i<elements.length; i++){
                                        var element = elements[i];
                                        if (element.tagName.toUpperCase() == 'INPUT' && element.type.toUpperCase()=='TEXT'){
                                            if (element.name.substr(0,5)=='name_'){
                                                if(element.id == '<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="title"/><%--<panel:encode name="name"/>--%>'+selectedOption.value ){
                                                    element.style.display= 'block';
                                                }
                                                else{
                                                    element.style.display= 'none';
                                                }
                                            }
                                        }
                                    }">
                    </mvc:fragment>
                    <mvc:fragment name="output">
                        <mvc:fragmentValue name="index" id="index">
                        <mvc:fragmentValue name="element" id="locale">
                                    <option <%= LocaleManager.currentLang().equals(((Locale)locale).getLanguage()) ? "selected" : ""%>
                                            value="<%=locale%>">
                                             <%=StringUtils.capitalize(((Locale)locale).getDisplayName((Locale)locale))%>
                                    </option>
                        </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragment>
                    <mvc:fragment name="outputEnd">
                                </select>
                    </mvc:fragment>
                </mvc:formatter>
                            </td>
                            <td>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputTitle">
    <mvc:fragmentValue name="value" id="value">
    <mvc:fragmentValue name="lang" id="lang">
    <mvc:fragmentValue name="selected" id="selected">
                                <input type="text" style=" <%= (selected!=null && ((Boolean)selected).booleanValue()) ? "" : "display:none" %>" class="skn-input"
                                       id="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="title"/><%=lang%>"
                                       name="<%="name_"+lang%>" value="<%=value%>">
     </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputTitleEnd">
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputUrl">
    <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.url"/>
                </td>
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left">
                    <mvc:fragmentValue name="urlPreffix"/><input size="20" class="skn-input" maxlength="50"
                                                                 name="<factory:bean bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="url"/>"
                                                                 value="<factory:property bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="url"/>">
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputVisible">
    <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.sections.visibleSection"/>
                </td>
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left">
                    <input type="hidden" id="<panel:encode name="visibleHidden"/>"
                           value="<factory:property bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="visible"/>"
                           name="<factory:bean bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="visible"/>">
                    <input type="checkbox"
                        <%=((Boolean)value).booleanValue()?"checked":""%>
                           onclick="document.getElementById('<panel:encode name="visibleHidden"/>').value=this.checked">
                </td>
            </tr>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="skinsPreStart">
    <mvc:fragmentValue name="error" id="error">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.admin.workarea.skins.skin"/>
                </td>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="skinsStart">
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left">
                    <select style="width:275px" class="skn-input" name="<factory:bean
                        bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="skin"/>">
</mvc:fragment>
<mvc:fragment name="outputSkin">
                        <option value="<mvc:fragmentValue name="skinId"/>">
                            <mvc:fragmentValue name="skinDescription"/>
                        </option>
</mvc:fragment>
<mvc:fragment name="outputSelectedSkin">
                        <option selected class="skn-important" value="<mvc:fragmentValue name="skinId"/>">
                            <mvc:fragmentValue name="skinDescription"/>
                        </option>
</mvc:fragment>
<mvc:fragment name="skinsEnd">
                    </select>
                </td>
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="envelopesPreStart">
    <mvc:fragmentValue name="error" id="error">
            <tr>
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.admin.workarea.envelopes.envelope"/>
                </td>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="envelopesStart">
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left">
                    <select style="width:275px" class="skn-input" name="<factory:bean
                        bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="envelope"/>">
</mvc:fragment>
<mvc:fragment name="outputEnvelope">
                        <option value="<mvc:fragmentValue name="envelopeId"/>">
                            <mvc:fragmentValue name="envelopeDescription"/>
                        </option>
</mvc:fragment>
<mvc:fragment name="outputSelectedEnvelope">
                        <option selected class="skn-important" value="<mvc:fragmentValue name="envelopeId"/>">
                            <mvc:fragmentValue name="envelopeDescription"/>
                        </option>
</mvc:fragment>
<mvc:fragment name="envelopesEnd">
                    </select>
                </td>
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="layoutsStart">
    <mvc:fragmentValue name="error" id="error">
            <tr>
                <td valign="top" class="<%=((Boolean)error).booleanValue()?"skn-error":""%> skn-even_row" width="25%" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.section.layout"/>
                </td>
                <td colspan="2" width="75%" height="18" nowrap="NOWRAP" align="left" valign="top">
                    <select style="width:275px" class="skn-input" name="<factory:bean
                        bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="layout"/>" onchange="this.form.submit();">
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputLayout">
                        <option value="<mvc:fragmentValue name="layoutId"/>">
                            <mvc:fragmentValue name="layoutDescription" id="layoutDescription">
                                <panel:localize data="<%=(Map)layoutDescription%>"/>
                            </mvc:fragmentValue>
                        </option>
</mvc:fragment>
<mvc:fragment name="outputSelectedLayout">
                        <option selected class="skn-important" value="<mvc:fragmentValue name="layoutId"/>">
                            <mvc:fragmentValue name="layoutDescription" id="layoutDescription">
                                <panel:localize data="<%=(Map)layoutDescription%>"/>
                            </mvc:fragmentValue>
                        </option>
</mvc:fragment>
<mvc:fragment name="layoutEnd">
                    </select>
                </td>
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="cellSpacingStart">
            <tr>
                <td width="25%" height="20" nowrap="NOWRAP" align="left" class="skn-even_row">
                    <i18n:message key="ui.sections.spacingInPixels"/>
                </td>
</mvc:fragment>
<mvc:fragment name="outputRegionsCellSpacing">
    <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%>" width="30%" height="20" nowrap="NOWRAP" align="left">
                    <i18n:message key="ui.sections.regionSpacing"/>&nbsp;&nbsp;
                    <input class="skn-input" maxlength="4" size="4"
                           name="<factory:bean bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="regionsCellSpacing"/>"
                           value="<factory:property bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="regionsCellSpacing"/>">
                </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputPanelsCellSpacing">
    <mvc:fragmentValue name="error" id="error">
        <mvc:fragmentValue name="value" id="value">
                <td class="<%=((Boolean)error).booleanValue()?"skn-error":""%>" height="20" nowrap="NOWRAP"
                    align="left">
                    <i18n:message key="ui.sections.panelSpacing"/>&nbsp;&nbsp;
                    <input class="skn-input" maxlength="4" size="4"
                           name="<factory:bean bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="panelsCellSpacing"/>"
                           value="<factory:property bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="panelsCellSpacing"/>">
                </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="cellSpacingEnd">
            </tr>
</mvc:fragment>
<%------------------------------------------------------------------------------------%>
<mvc:fragment name="layoutPreview">
    <mvc:fragmentValue name="itemId" id="itemId">
        <mvc:fragmentValue name="template" id="template">
            <tr align="center">
                <td></td>
                <td align="center" colspan="2">
                    <resource:page id="template" category="layout" categoryId="<%=(String)itemId%>" resourceId="JSP"/>
                    <br><table width="207px" cellpadding="0" cellspacing="0" border="0" align="left">
                        <tr>
                            <td></td>
                            <td width="100%" align="center" colspan="2">
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

<%------------------------------------------------------------------------------------%>
<mvc:fragment name="outputEnd">
            <tr>
                <td colspan="3" align="center">
                    <br>
                    <input type="hidden" name="<factory:bean bean="org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler" property="saveButtonPressed"/>" id="<panel:encode name="saveButton"/>">
                    <input class="skn-button" type="submit" value='<i18n:message key="ui.saveChanges"/>' onclick="document.getElementById('<panel:encode name="saveButton"/>').value='true'">
                </td>
            </tr>
        </table>
    </form>
</mvc:fragment>
</mvc:formatter>