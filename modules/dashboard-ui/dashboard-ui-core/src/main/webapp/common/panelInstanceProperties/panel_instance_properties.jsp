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
<%@ page import="org.jboss.dashboard.ui.utils.forms.RenderUtils"%>
<%@ page import="org.jboss.dashboard.ui.config.components.panelInstance.PanelInstancePropertiesFormatter"%>
<%@ page import="java.util.List"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.jboss.dashboard.workspace.*" %>
<%@ page import="org.jboss.dashboard.ui.utils.forms.FormStatus" %>
<%@ include file="../global.jsp" %>
<%
    PanelProviderParameter[]params = (PanelProviderParameter[])request.getAttribute(PanelInstancePropertiesFormatter.PANEL_INSTANCE_PROPERTIES);
    PanelInstance instance = (PanelInstance)request.getAttribute(PanelInstancePropertiesFormatter.PANEL_INSTANCE);
    FormStatus formStatus = (FormStatus)request.getAttribute(PanelInstancePropertiesFormatter.FORM_STATUS);
%>
  

<tr>
        <td align="left"  class="skn-even_row">
            <%=instance.getResource("panel.id.label", currentLocale)%>:
        </td>
        <td align="left" >
            <b><%=instance.getInstanceId() %></b>
        </td>
    </tr>
    <tr >
        <td align="left" class="skn-even_row">
            <%=instance.getResource("panel.type.label", currentLocale)%>:
        </td>
        <td align="left">
            <b><%=instance.getResource(instance.getProvider().getDescription(), currentLocale) %></b>
        </td>
    </tr>
<%
    if (params!= null) {
        if (formStatus.getValueAsBoolean("multilanguage",false)) {
%>
    <tr >
        <td align="left" class="skn-even_row">
            <i18n:message key="ui.language"/>
        </td>
        <td align="left">
            <select name="editing" class="skn-input" onchange="document.config.submit();">
<%
            Locale[] locales = localeManager.getPlatformAvailableLocales();
            for (int i = 0; i < locales.length; i++) {
                Locale locale = locales[i];
%>
                <option value="<%=locale%>" <%=formStatus.getValue("lang").toString().equalsIgnoreCase(locale.toString()) ? "selected" : ""%>>
                    <%=StringUtils.capitalize(locale.getDisplayName(locale))%>
                </option>
<%
            }
%>
            </select>
        </td>
    </tr>
<%      }
        for (int i=0; i<params.length; i++) {
%>
    <tr>
        <td align="left" class="skn-even_row" width="200px" valign="top">
            <%=RenderUtils.field(request, params[i].getId(), params[i].getDescription(currentLocale), formStatus)%>
        </td>
        <td align="left" >
<%
        String lang = currentLocale.getLanguage();
        if (formStatus.getValueAsBoolean("multilanguage",false)) {
            lang = formStatus.getValue("lang").toString();
        }
%>
            <%=params[i].renderHTML(request, instance, params[i], instance.getParameterValue(params[i].getId(), lang))%>
        </td>
    </tr>
<%
        }
        formStatus.clearWrongFields();
    }
%>