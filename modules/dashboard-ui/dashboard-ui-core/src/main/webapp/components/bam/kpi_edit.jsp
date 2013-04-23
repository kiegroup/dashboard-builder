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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ page import="org.jboss.dashboard.DataDisplayerServices"%>
<%@ page import="org.jboss.dashboard.ui.components.KPIEditor"%>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayer"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.jboss.dashboard.kpi.KPI"%>
<%@ page import="org.jboss.dashboard.provider.DataProvider" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.jboss.dashboard.displayer.DataDisplayerType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.jboss.dashboard.ui.UIBeanLocator" %>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    // Get the selected tab
    KPIEditor kpiEditor = KPIEditor.lookup();
    Locale locale = LocaleManager.currentLocale();
    KPI kpi = kpiEditor.getKpi();
    DataDisplayer displayer = kpi.getDataDisplayer();
    DataProvider provider = kpi.getDataProvider();
    String editorPath = UIBeanLocator.lookup().getEditor(displayer).getName();
%>

<!-- Add the properties to configure the KPIEditor -->
<table align="left" cellpadding="4" cellspacing="0" border="0">
    <tr>
       <td align="left">
         <table align="left"  cellpadding="4" cellspacing="0" border="0">
          <tr>
            <td align="left">
                <i18n:message key='<%= KPIEditor.I18N_PREFFIX + "providerSelected"%>'>!!Proveedor de datos</i18n:message>:
            </td>
            <td align="left">
                <select name="providerSelected" title="<%= provider.getDescription(locale) %>" id="<factory:encode name="providerSelected"/>" class="skn-input"
                        style="width:150px;" onChange="return bam_kpiedit_submitProperties(this);">
                <%
                    Set dataProviders = DataDisplayerServices.lookup().getDataProviderManager().getAllDataProviders();
                    List<DataProvider> dataProviderList = new ArrayList<DataProvider>(dataProviders);
                    DataDisplayerServices.lookup().getDataProviderManager().sortDataProvidersByDescription(dataProviderList, true);

                    for (DataProvider dataProvider : dataProviderList) {
                        String selected = "";
                        String providerDescrip = dataProvider.getDescription(locale);
                        String providerCode = dataProvider.getCode();
                        if (providerCode != null && providerCode.equals(provider.getCode())) selected = "selected";
                %>
                        <option title="<%= providerDescrip %>" value="<%= providerCode %>" <%= selected %>>
                            <%= providerDescrip %>
                        </option>
                <%
                    }
                %>
                </select>
            </td>
          </tr>
           <tr>
             <td align="left">
               <i18n:message key='<%= KPIEditor.I18N_PREFFIX + "kpiName"%>'>!!KPI name</i18n:message>:
             </td>
             <td align="left">
               <input size="22" class="skn-input" name="<%=KPIEditor.PARAM_KPI_DESCRIPTION%>" type="text" value="<%= kpiEditor.getKpi().getDescription(locale) %>"
                      onChange="return bam_kpiedit_submitProperties(this);">
             </td>
             <td align="left">
               <mvc:include page="modules/i18n/locale_selector_combo.jsp" flush="true"/>
             </td>
           </tr>
        <% if (!kpiEditor.isReady()) { %>
             <tr>
                 <td colspan="2">
                     <span class="skn-error"><i18n:message key="kpiEditorComponent.providerIsNotReady">!!!Proveedor mal configurado.</i18n:message></span>
                 </td>
             </tr>
         <% } %>
        </table>
      </td>
    </tr>
    <tr>
      <td align="left">
        <% if (kpiEditor.isReady()) { %>
        <table width="100%" align="left"  cellpadding="4" cellspacing="0" border="0">
        <tr>
          <td valign="top">
            <table>
                <input type="hidden" name="uid" id="<factory:encode name="uid"/>" value=""/>
                <input type="hidden" id="<factory:encode name="changeDisplayer" />" name="changeDisplayer" value="false">
                <%
                    DataDisplayerType[] displayerTypes = DataDisplayerServices.lookup().getDataDisplayerManager().getDataDisplayerTypes();
                    for (int i = 0; i < displayerTypes.length; i++) {
                        boolean selected = displayerTypes[i].getUid().equals(displayer.getDataDisplayerType().getUid());
                %>
                        <tr>
                            <td>
                                <%
                                    if (selected) {
                                %>
                                    <img src="<%=request.getContextPath()%>/<%= displayerTypes[i].getIconPath() %>"
                                         style="opacity: 0.5; -moz-opacity: 0.5; filter: alpha( opacity = 50 );"
                                         title="<%= displayerTypes[i].getDescription(locale) %>">
                                <%
                                    } else {
                                %>

                                <a style="border:0" id="<factory:encode name="changeDisplayer"/>" href="#" onclick="
                                    document.getElementById('<factory:encode name="uid"/>').value='<%= displayerTypes[i].getUid() %>';
                                    document.getElementById('<factory:encode name="changeDisplayer"/>').value='true';
                                    bam_kpiedit_submitProperties(document.getElementById('<factory:encode name="changeDisplayer"/>'));return false;">

                                    <img src="<%=request.getContextPath()%>/<%= displayerTypes[i].getIconPath() %>" border="0" title="<%= displayerTypes[i].getDescription(locale) %>">
                                </a>
                                <script defer="true">
                                    setAjax('<factory:encode name="changeDisplayer"/>');
                                </script>
                                <%
                                    }
                                %>
                            </td>
                        </tr>
                <%
                    }
                %>
            </table>
        </td>
        <td>
            <!-- Include the displayer editor -->
            <factory:useComponent bean="<%= editorPath %>"/>
        </td>
      </tr>
     </table>
      <% } %>
     </td>
   </tr>
</table>
