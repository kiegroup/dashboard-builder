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
<%@ page import="org.jboss.dashboard.ui.components.DashboardFilterHandler" %>
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ page import="org.jboss.dashboard.ui.NavigationManager" %>
<%@ page import="org.jboss.dashboard.factory.Factory" %>
<%@ page import="org.jboss.dashboard.workspace.Section" %>
<%@ page import="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter" %>
<%@ page import="org.jboss.dashboard.workspace.Section" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.filter.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    NavigationManager navigationManager = NavigationManager.lookup();
    Section[] sections = navigationManager.getCurrentWorkspace().getAllSections();
    LocaleManager localeManager = LocaleManager.lookup();
    String componentCode = (String) request.getAttribute("componentCode");
    DashboardFilterHandler handler = DashboardFilterHandler.lookup(componentCode);
%>
<mvc:formatter name="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter">
    <mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_RENDER_TYPE%>" value="<%=DashboardFilterFormatter.RENDER_TYPE_SHOW%>"/>
    <mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_COMPONENT_CODE%>" value='<%=request.getAttribute("componentCode")%>'/>

    <mvc:fragment name="outputStart">
      <table border="0" style="margin:0px;" cellspacing="0" width="100%">
    </mvc:fragment>

    <mvc:fragment name="outputTableStart">
        <tr>
            <td width="100%">
                <form method="post" action="<factory:formUrl friendly="false"/>" id="<panel:encode name="storePropertiesOptions"/>">
                <factory:handler bean="<%=handler.getComponentPath()%>" action="store"/>
                <table cellspacing="0" cellpadding="8" border="0" width="100%">

    </mvc:fragment>

    <mvc:fragment name="outputHeader">
        <tr><td>
            <table class="skn-table_border" cellspacing="1" cellpadding="4" border="0" align="left">
                <tr class="skn-table_header">
                    <td><div style="width:20px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "visible"%>'/>"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "visible"%>'/></div></td>
                    <td><div style="width:25px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "dataProvider"%>'/>"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "dataProvider"%>'/></div></td>
                    <td><div style="width:25px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "property"%>'/>"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "property"%>'/></div></td>
                    <td><div style="width:75px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "drillDown"%>'/>"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "drillDown"%>'/></div></td>
                </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEmpty">
        <tr>
            <td colspan="4" width="100%">
                <span class="skn_error">
                    <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "noProperties"%>'>!!! No hay propiedades seleccionadas</i18n:message>
                </span>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputTableElement">
        <mvc:fragmentValue name="index" id="index">
        <mvc:fragmentValue name="dataProviderCode" id="dataProviderCode">
            <mvc:fragmentValue name="propertyId" id="propertyId">
                <mvc:fragmentValue name="visibleChecked" id="visibleChecked">
                    <mvc:fragmentValue name="drillDownChecked" id="drillDownChecked">
                        <mvc:fragmentValue name="sectionId" id="sectionId">
                            <mvc:fragmentValue name="currentSectionTitle" id="currentSectionTitle">
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
                                <td style="width:20px; text-align:center;">
                                    <input type="checkbox" class="skn-input"
                                        <%=visibleChecked != null && ((Boolean)visibleChecked).booleanValue() ? "checked" : ""%>
                                            name="<%=DashboardFilterHandler.PARAM_VISIBLE + "/" + dataProviderCode + "/" + propertyId%>"
                                            value="<%=DashboardFilterHandler.PARAM_VISIBLE + "/" + dataProviderCode + "/" + propertyId%>"
                                            onclick="submitAjaxForm(this.form);">
                                </td>
                                <td>
                                    <div style="width:25px; height:18px; text-align:left; overflow:hidden; vertical-align:middle; white-space:nowrap;"
                                        title="<mvc:fragmentValue name="dataProviderName"/>">
                                            <mvc:fragmentValue name="dataProviderName"/>
                                    </div>
                                </td>
                                <td>
                                    <div style="width:25px; height:18px; text-align:left; overflow:hidden; vertical-align:middle; white-space:nowrap;"
                                        title="<mvc:fragmentValue name="propertyName"/>">
                                            <mvc:fragmentValue name="propertyName"/>
                                    </div>
                                </td>
                                <td>
                                    <select style="width:74px" class="skn-input"
                                            name="<%=DashboardFilterHandler.PARAM_SECTION +"/"+dataProviderCode+"/"+propertyId%>"
                                            title="<%= currentSectionTitle %>"
                                            id="<%=DashboardFilterHandler.PARAM_SECTION +"/"+dataProviderCode+"/"+propertyId%>"
                                            onchange="submitAjaxForm(this.form);">
                                        <option title="-- <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "select"%>'/> --" value="<%=DashboardFilterHandler.PARAM_DRILLDOWN_DISABLED%>"
                                                <%=drillDownChecked != null && ((Boolean)drillDownChecked).booleanValue() ? "" : "selected"%> >
                                            -- <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "select"%>'/> --
                                        </option>
                                    <%
                                        for (int i = 0; i < sections.length; i++) {
                                            Section section = sections[i];

                                    %>
                                        <option title="<%=localeManager.localize(section.getTitle())%>" value="<%=section.getId()%>" <%=section.getId().equals(sectionId) ? "selected" : ""%>><%=localeManager.localize(section.getTitle())%></option>
                                    <%
                                        }
                                    %>
                                    </select>
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

    <mvc:fragment name="outputOptions">
        <mvc:fragmentValue name="refreshChecked" id="refreshChecked">
            <mvc:fragmentValue name="applyChecked" id="applyChecked">
            <mvc:fragmentValue name="clearChecked" id="clearChecked">
            <mvc:fragmentValue name="pNamesChecked" id="pNamesChecked">
                <mvc:fragmentValue name="shortModeChecked" id="shortModeChecked">
                <mvc:fragmentValue name="submitOnChangeChecked" id="submitOnChangeChecked">
                     <mvc:fragmentValue name="showLegendChecked" id="showLegendChecked">
                         <mvc:fragmentValue name="showAutoRefresh" id="showAutoRefresh">
                <tr>
                    <td>
                        <fieldset>
                        <legend style="font-weight:bold;"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "options"%>'>!!! Opciones</i18n:message></legend>
                        <table cellspacing="0" cellpadding="0" border="0" align="left" width="100%">
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHORT_MODE%>"
                                        <%=shortModeChecked != null && ((Boolean)shortModeChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                 <td><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showShortMode"%>'>!!! Mostrar vista reducida</i18n:message></td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_LEGEND%>"
                                        <%=showLegendChecked != null && ((Boolean)showLegendChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                 <td><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showLegend"%>'>!!! Mostrar leyenda</i18n:message></td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_REFRESH_BUTTON%>"
                                        <%=refreshChecked != null && ((Boolean)refreshChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                 <td>
                                     <div style="width:140px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showRefreshButton"%>'>!!! Mostrar boton refrescar</i18n:message>">
                                        <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showRefreshButton"%>'>!!! Mostrar boton refrescar</i18n:message>
                                     </div>
                                 </td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_APPLY_BUTTON%>"
                                        <%=applyChecked!= null && ((Boolean)applyChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                <td>
                                    <div style="width:140px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showApplyButton"%>'>!!! Mostrar boton aplicar</i18n:message>">
                                        <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showApplyButton"%>'>!!! Mostrar boton aplicar</i18n:message>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_CLEAR_BUTTON%>"
                                        <%=clearChecked!= null && ((Boolean)clearChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                <td>
                                    <div style="width:140px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showClearButton"%>'>!!! Mostrar boton borrar</i18n:message>">
                                        <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showClearButton"%>'>!!! Mostrar boton borrar</i18n:message>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_PROPERTY_NAMES%>"
                                        <%=pNamesChecked != null && ((Boolean)pNamesChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                <td>
                                    <div style="width:140px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showPropertyNames"%>'>!!! Mostrar nombres de las propiedades</i18n:message>">
                                        <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showPropertyNames"%>'>!!! Mostrar nombres de las propiedades</i18n:message>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_SUBMIT_ON_CHANGE%>"
                                        <%=submitOnChangeChecked != null && ((Boolean)submitOnChangeChecked).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                <td>
                                    <div style="width:140px; overflow:hidden; white-space:nowrap;" title="<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showSubmitOnChange"%>'>!!! Auto submit</i18n:message>">
                                        <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showSubmitOnChange"%>'>!!! Auto submit</i18n:message>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="width:30px;"><input type="checkbox" name="<%=DashboardFilterHandler.PARAM_SHOW_AUTO_REFRESH%>"
                                        <%=showAutoRefresh != null && ((Boolean)showAutoRefresh).booleanValue() ? "checked" : ""%>
                                        onclick="submitAjaxForm(this.form);"></td>
                                <td><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "showAutoRefresh"%>'>!!! Auto refresh</i18n:message></td>
                            </tr>
                        </table>
                         </fieldset>
                    </td>
                </tr>
             </mvc:fragmentValue>
             </mvc:fragmentValue>
             </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
                </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputNotAllowedPropertiesStart">
        <tr>
            <td colspan="4">
                <div class="skn-important" style="width:160px; height:70px; text-align:left; overflow:hidden; vertical-align:middle;"
                     onmouseover="document.getElementById('<panel:encode name="notAllowedPropertiesDiv"/>').style.display='block';"
                     onmouseout="document.getElementById('<panel:encode name="notAllowedPropertiesDiv"/>').style.display='none';">
                    <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "notAllowdProperties"%>'>!!! Propiedades no permitidas</i18n:message>
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="4" width="">
                <table class="skn-table_border"cellspacing="1" cellpadding="0" border="0" width="160px">
                <tr class="skn-table_header">
                        <td width="80px"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "dataProvider"%>'/></td>
                        <td width="80px"><i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "property"%>'/></td>
                    </tr>
    </mvc:fragment>

    <mvc:fragment name="outputNotAllowedProperty">
        <tr>
            <td width="80px" nowrap="nowrap" align="center">
                <div style="width:80px; height:18px; text-align:left; overflow:hidden; vertical-align:middle;"
                     title="<mvc:fragmentValue name="dataProviderName"/>">
                    <mvc:fragmentValue name="dataProviderName"/>
                </div>
            </td>
            <td width="80px" nowrap="nowrap" align="center">
                <div style="width:80px; height:18px; text-align:left; overflow:hidden; vertical-align:middle"
                     title="<mvc:fragmentValue name="propertyName"/>">
                    <mvc:fragmentValue name="propertyName"/>
                </div>
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputNotAllowedPropertiesEnd">
        </table>
        </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputTableEnd">
        </table></td></tr>
        </table>
         </form>
        <script defer>
            setAjax('<panel:encode name="storePropertiesOptions"/>');
        </script>
        </td>
       </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
</mvc:formatter>