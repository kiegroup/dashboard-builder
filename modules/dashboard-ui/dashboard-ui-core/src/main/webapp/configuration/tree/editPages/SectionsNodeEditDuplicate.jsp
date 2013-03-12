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
<%@ page import="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler"%>
<%@ page import="org.jboss.dashboard.workspace.copyoptions.SectionCopyOption" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="security_taglib.tld" prefix="security" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.sections.SectionCopyFormatter">
    <mvc:fragment name="outputStart">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <form style="margin:0px;" method="POST" action="<factory:formUrl friendly="false"/>" id="duplicateSection">
                <factory:handler bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" action="duplicateSection"/>
                <table width="100%" border="0" cellpadding="0" cellspacing="1" align="left" class="skn-table_border">
                    <tr class="skn-table_header">
                        <td>
                            <i18n:message key="ui.sections.duplicate">!!Duplicate section</i18n:message> <mvc:fragmentValue name="sectionTitle"/>
                        </td>
                    </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputMode">
                    <tr>
                        <td align="center">
                            <select style="width:250px" class="skn-input" name="<%=SectionCopyOption.COPY_MODE%>" onchange="
                                if(value == '<%=SectionCopyOption.COPY_SOME%>'){
                                    document.getElementById('panelsToDuplicateDIV').style.display='block';
                                }
                                else {
                                    document.getElementById('panelsToDuplicateDIV').style.display='none';
                                };
                                return true;
                                ">
                                <option value="<%=SectionCopyOption.COPY_NONE%>">
                                    <i18n:message key="ui.sections.doNotDuplicatePanels"/>
                                </option>
                                <option value="<%=SectionCopyOption.COPY_ALL%>">
                                    <i18n:message key="ui.sections.duplicateAllPanels"/>
                                </option>
                                <option value="<%=SectionCopyOption.COPY_SOME%>" selected>
                                    <i18n:message key="ui.sections.choosePanelsToDuplicate"/>
                                </option>
                            </select>
                        </td>
                    </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputHeaders">
                    <tr>
                        <td>
                            <table width="100%" align="left" valign="middle" border="0">
                                <tr>
                                    <td>
                                        <div id="panelsToDuplicateDIV" style="width:100%">
                                            <table cellpadding=4 cellspacing=1 width="100%" align="left" valign="middle" border="0">
                                                <tr class="skn-table_header">
                                                    <td width="18"><i18n:message key="ui.id"/></td>
                                                    <td><i18n:message key="ui.group"/></td>
                                                    <td><i18n:message key="ui.type"/></td>
                                                    <td><i18n:message key="ui.title"/></td>
                                                    <td align="center"><i18n:message key="ui.sections.duplicateInstance"/></td>
                                                    <td align="center"><i18n:message key="ui.sections.noDuplicateInstance"/></td>
                                                </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputOpt">
        <mvc:fragmentValue name="instanceId" id="instanceId">
        <mvc:fragmentValue name="instance" id="instance">
        <mvc:fragmentValue name="counter" id="counter">
                                                <tr class="<%=((Integer)counter).intValue()%2==0?"skn-odd_row":"skn-even_row"%>">
                                                    <td><%=instanceId%></td>
                                                    <td><mvc:fragmentValue name="group"/></td>
                                                    <td><mvc:fragmentValue name="description"/></td>
                                                    <td><mvc:fragmentValue name="title"/></td>
                                                    <td align="center">
                                                        <input type="radio" name="<%="duplicatePanelInstance_"+instanceId%>" value="true">
                                                    </td>
                                                    <td align="center">
                                                        <input type="radio" name="<%="duplicatePanelInstance_"+instanceId%>" value="false" checked>
                                                    </td>
                                                </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputHeadersEnd">
                                            </table>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center">
                                        <input type="hidden"
                                               name="<factory:bean bean="org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler" property="action"/>"
                                               id="duplicate_action">
                                        <input class="skn-button" name="save" type="submit" value="<i18n:message key="ui.sections.duplicate"/>"
                                               onclick="document.getElementById('duplicate_action').value='<%=SectionsPropertiesHandler.ACTION_SAVE%>';">
                                        &nbsp;&nbsp;&nbsp;
                                        <input class="skn-button_alt" type="submit" name="cancel"
                                               onclick="document.getElementById('duplicate_action').value='<%=SectionsPropertiesHandler.ACTION_CANCEL%>';"
                                               value="<i18n:message key="ui.admin.workarea.cancel">!!Cancelar</i18n:message>">
                                    </td>
                                </tr>
                            </table>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEmpty">
                    <tr>
                        <td align="center" class="skn-error">
                            <br>
                            <b><i18n:message key="ui.sections.noPanels">!!Pagina sin Panels</i18n:message></b>
                            <br><br>
                        </td>
                    </tr>
    </mvc:fragment>
    <%-------------------------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
                </table>
            </form>
        </td>
    </tr>
</table>
    </mvc:fragment>
</mvc:formatter>