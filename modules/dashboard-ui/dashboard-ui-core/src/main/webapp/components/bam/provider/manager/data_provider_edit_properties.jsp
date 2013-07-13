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
<%@ page import="org.jboss.dashboard.ui.components.DataProviderHandler" %>
<%@ page import="org.jboss.dashboard.ui.formatters.DataProviderFormatter" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.displayer.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>


<mvc:formatter name="org.jboss.dashboard.ui.formatters.DataProviderFormatter">
    <mvc:fragment name="outputStart">
      <table border="0" style="margin:0px;" cellspacing="0" width="650px">
    </mvc:fragment>

    <mvc:fragment name="outputTitle">
        <mvc:fragmentValue name="providerName" id="providerName">
        <tr>
            <td class="skn-title3"><i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "editingProviderProperties"%>' args="<%=new Object[] {providerName}%>"></i18n:message> <br><br></td>
        </tr>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputFormStart">
        <tr>
            <td>
                <form action="<factory:formUrl friendly="false"/>" method="post" id="<factory:encode name="editDataProviderPropertiesForm"/>">
                <factory:handler bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="storeDataProviderProperties"/>
                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
    </mvc:fragment>

    <mvc:fragment name="outputStartProperties">
        <tr><td width="100%">
        <table class="skn-table_border" cellspacing="1" cellpadding="4" border="0" align="center" width="100%">
        <tr class="skn-table_header">
            <td>
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "header_properties"%>'>!!! Acciones</i18n:message>
            </td>
            <td>
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "header_type"%>'>!!! Nombre</i18n:message>
            </td>
            <td colspan="2">
                <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "header_name"%>'>!!! Nombre</i18n:message>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputStartRow">
        <mvc:fragmentValue name="index" id="index">
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
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEmpty">
        <td>
            <i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "noProperties"%>'>!!!No existen propiedades</i18n:message>
        </td>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyId">
            <td width="150px">
                 <div style="width:150px; height:18px; text-align:left; overflow:hidden; vertical-align:middle"
                         title="<mvc:fragmentValue name="propertyId" />">
                     <mvc:fragmentValue name="propertyId"/>
                 </div>
            </td>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyTypeText">
            <mvc:fragmentValue name="propertyType" id="propertyType">
                <td width="100px" >
                    <div style="width:100px; height:18px; text-align:left; overflow:hidden; vertical-align:middle"
                         title="<i18n:message key="<%=(String) propertyType%>"/>">
                         <i18n:message key="<%=(String) propertyType%>"/>
                     </div>

                </td>
            </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputPropertyTypeCombo">
        <mvc:fragmentValue name="propertyId" id="propertyId">
        <mvc:fragmentValue name="selected" id="selected">
        <mvc:fragmentValue name="keys" id="keys">
            <mvc:fragmentValue name="values" id="values">
                <td width="100px">
                    <select class="skn-input" name="<%=DataProviderHandler.PARAM_PROPERTY_TYPE+"_"+propertyId%>">
                        <%
                            String[] strKeys = (String[]) keys;
                            String[] strValues = (String[]) values;
                            for (int x = 0; x < strKeys.length; x++) {
                                String key = strKeys[x];
                                String value = strValues[x];
                                boolean optionSelected = false;
                                if (value.equals("domain."+selected)) optionSelected = true;
                        %>
                        <option value="<%=key%>" <%=optionSelected ? "selected" : ""%>><i18n:message key="<%=(String) value%>"/></option>
                        <%
                            }
                        %>
                    </select>
                </td>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyTitle">
        <mvc:fragmentValue name="value" id="value">
        <mvc:fragmentValue name="propertyId" id="propertyId">
        <td nowrap="nowrap" colspan="2">
          <%
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (int i = 0; i < locales.length; i++) {
              Locale locale = locales[i];
          %>
          <input id="<factory:encode name="listName"/><%= "_" + propertyId + "_" + locale.toString()%>"
                 name='name<%="/"+propertyId + "/" + locale.toString()%>'
                 class="skn-input"
                 style='<%="width:250px;"+ (locale.getLanguage().equals(LocaleManager.currentLang()) ? "display:inline;" : "display:none;")%>'
                 value='<%=StringUtils.defaultString((value == null || "".equals(value)) ? "" : (String)((Map) value).get(locale))%>'>
          <% } %>
                    <select class="skn-input" onchange="
                    var elements = this.form.elements;
                    var selectedOption = this.options[this.selectedIndex];
                    for(i =0 ; i<elements.length; i++){
                        var element = elements[i];
                        if (element.tagName.toUpperCase() == 'INPUT' && element.type.toUpperCase()=='TEXT') {
                            if (element.id.substring(0,element.id.lastIndexOf('_')) == '<factory:encode name="listName"/>' + '<%= "_" + propertyId%>') {
                                if(element.id == '<factory:encode name="listName"/>' + '<%= "_" + propertyId + "_"%>' + selectedOption.value ){
                                    element.style.display= 'inline';
                                } else {
                                    element.style.display= 'none';
                                }
                            }
                        }
                    }">
              <%
                for (int i = 0; i < locales.length; i++) {
                  Locale locale = locales[i];
              %>
                  <option <%= locale.getLanguage().equals(LocaleManager.currentLang()) ? "selected" : ""%> value="<%=locale%>">
                    <%=StringUtils.capitalize(locale.getDisplayName(locale))%>
                  </option>
              <% } %>
              </select>
        </td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEndRow">
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEndProperties">
        </table>
        </td></tr>
    </mvc:fragment>

    <mvc:fragment name="outputButtons">
        <tr>
            <td width="100%" align="center" style="padding-top:10px;">
               <input class="skn-button" type="button" value="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "save"%>'>!!!Guardar</i18n:message>"
                       onclick="submitAjaxForm(this.form);">&nbsp;
               <input class="skn-button_alt" type="button" value="<i18n:message key='<%=DataProviderHandler.I18N_PREFFIX + "cancel"%>'>!!!Cancelar</i18n:message>"
                                onclick="submitAjaxForm(document.getElementById('<factory:encode name="goToShowPageForm"/>'))"/>
               <input id="<factory:encode name="editCreateFormSubmitted"/>" type="hidden" value="false" name="<factory:bean property="saveButtonPressed" bean="org.jboss.dashboard.ui.components.DataProviderHandler"/>">
            </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputFormEnd">
        </table>
        </form>
        <form action="<factory:formUrl friendly="false"/>" method="post" id="<factory:encode name="goToShowPageForm"/>">
            <factory:handler bean="org.jboss.dashboard.ui.components.DataProviderHandler" action="cancel"/>
        </form>
        <script defer>
            setAjax('<factory:encode name="editDataProviderPropertiesForm"/>');
            setAjax('<factory:encode name="goToShowPageForm"/>');
        </script>
         </td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputEnd">
      </table>
    </mvc:fragment>
</mvc:formatter>