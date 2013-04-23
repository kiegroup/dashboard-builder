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
<%@ page import="org.jboss.dashboard.ui.components.DashboardFilterHandler" %>
<%@ page import="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.filter.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.DashboardFilterFormatter">
    <mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_RENDER_TYPE%>" value="<%=DashboardFilterFormatter.RENDER_TYPE_PROPERTIES%>"/>
    <mvc:formatterParam name="<%=DashboardFilterFormatter.PARAM_COMPONENT_CODE%>" value='<%=request.getAttribute("componentCode")%>'/>

    <mvc:fragment name="outputStart">
        <table cellpadding="0" cellspacing="10" border="0">
    </mvc:fragment>

    <mvc:fragment name="outputEmpty">

    </mvc:fragment>

    <mvc:fragment name="outputStartRow">
        <tr>
    </mvc:fragment>

    <mvc:fragment name="outputEndRow">
        </tr>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyName">
        <td>
            <mvc:fragmentValue name="propertyName"/>:
        </td>
    </mvc:fragment>

    <mvc:fragment name="outputNewColumn">
        <td></td>
    </mvc:fragment>

    <mvc:fragment name="outputErrorPropertyName">
        <td>
            <span class="skn-error"><mvc:fragmentValue name="propertyName"/></span>
        </td>
    </mvc:fragment>

    <mvc:fragment name="outputPropertyTypeLabel">
        <mvc:fragmentValue name="dataProviderCode" id="dataProviderCode">
        <mvc:fragmentValue name="propertyId" id="propertyId">
        <mvc:fragmentValue name="selected" id="selected">
        <mvc:fragmentValue name="keys" id="keys">
            <mvc:fragmentValue name="values" id="values">
                <mvc:fragmentValue name="submitOnChange" id="submitOnChange">
                <td style="white-space:nowrap;">
                    <select class="skn-input" style="width:153px;" name="<%=DashboardFilterHandler.PARAM_VALUE +"_"+ propertyId%>"
                            onchange="
                                    var element = document.getElementById('<panel:encode name='<%="CustomStringFor/"+dataProviderCode+"/"+propertyId%>'/>');
                                    if (this.options[this.selectedIndex].value == '<%=DashboardFilterHandler.PARAM_CUSTOM_VALUE%>') element.style.display = 'inline';
                                    else if (this.options[this.selectedIndex].value == '<%=DashboardFilterHandler.PARAM_NULL_VALUE%>') element.style.display = 'none';
                                    else {
                                        element.style.display = 'none';
                                        <% if (submitOnChange != null && ((Boolean)submitOnChange).booleanValue()) {%>
                                            submitAjaxForm(this.form);
                                        <% } %>
                                    }">
                        }
                        <%
                            String[] strKeys = (String[]) keys;
                            String[] strValues = (String[]) values;
                            for (int x = 0; x < strKeys.length; x++) {
                                String key = strKeys[x];
                                if (key == null) continue;
                                String value = strValues[x];
                                boolean optionSelected = false;
                                if (key.equals(selected)) optionSelected = true;
                        %>
                        <option title="<%=value%>" value="<%=key%>" <%=optionSelected ? "selected" : ""%>> <%=value%> </option>
                        <%
                            }
                        %>
                    </select>

                    <div id="<panel:encode name='<%="CustomStringFor/"+dataProviderCode+"/"+propertyId%>'/>" style="display:none">
                        <input class="skn-input" style="width:150px;" type="text" name = "<%=DashboardFilterHandler.PARAM_CUSTOM_VALUE+"_"+ propertyId%>">
                        &nbsp;
                        <a style="height:20px; vertical-align:bottom;" href="#" onclick="window.<panel:encode name='<%="helpForProperty_" + StringUtils.deleteWhitespace((String)propertyId) + "_function"%>'/>(); return false;">
                             <img src="<static:image relativePath="general/16x16/ico-info.png"/>"  border="0" >
                        </a>
                       <% request.setAttribute("propertyId",propertyId); %>
                       <mvc:include page="../filter_help.jsp" />
                    </div>
                </td>
            </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>


    <mvc:fragment name="outputPropertyTypeDate">
        <mvc:fragmentValue name="propertyId" id="propertyId">
            <mvc:fragmentValue name="dataProviderCode" id="dataProviderCode">
                <mvc:fragmentValue name="minValue" id="minValue">
                    <mvc:fragmentValue name="maxValue" id="maxValue">
                        <mvc:fragmentValue name="submitOnChange" id="submitOnChange">
                        <td>
                            <select class="skn-input" style="width:153px;" name="<%=DashboardFilterHandler.PARAM_VALUE +"_"+ propertyId%>"
                                    onchange="
                                    var element = document.getElementById('<panel:encode name='<%="CustomDateFor/"+dataProviderCode+"/"+propertyId%>'/>');
                                    if (this.options[this.selectedIndex].value == '<%=DashboardFilterHandler.PARAM_CUSTOM_VALUE%>') element.style.display = 'inline';
                                    else if (this.options[this.selectedIndex].value == '<%=DashboardFilterHandler.PARAM_NULL_VALUE%>') element.style.display = 'none';
                                    else {
                                        element.style.display = 'none';
                                        <% if (submitOnChange != null && ((Boolean)submitOnChange).booleanValue()) {%>
                                            submitAjaxForm(this.form);
                                        <% } %>
                                    }">
                                }
                                <option title="- <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "select"%>'/> <mvc:fragmentValue name="propertyName"/> -"
                                        value="<%=DashboardFilterHandler.PARAM_NULL_VALUE%>"
                                        <%=minValue == null && maxValue == null ? "selected" : ""%>> - <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "select"%>'/> <mvc:fragmentValue name="propertyName"/> - </option>
                                <option value="<%=DashboardFilterHandler.PARAM_CUSTOM_VALUE%>"
                                        <%=minValue != null || maxValue != null ? "selected": ""%>> - <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "custom"%>'/> - </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_HOUR%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "lastHour"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_12HOURS%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "last12Hours"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_TODAY%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "today"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_YESTERDAY%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "yesterday"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_7DAYS%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "last7Days"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_MONTH%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "lastMonth"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_THIS_MONTH%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "thisMonth"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_THIS_QUARTER%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "thisQuarter"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_QUARTER%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "lastQuarter"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_6MONTHS%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "last6Months"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_THIS_YEAR%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "thisYear"%>'/> </option>
                                <option value="<%=DashboardFilterHandler.PARAM_LAST_YEAR%>" > <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "lastYear"%>'/> </option>
                            </select>
                        &nbsp;
                            <div id="<panel:encode name='<%="CustomDateFor/"+dataProviderCode+"/"+propertyId%>'/>" style="display:none; white-space:nowrap;">
                                <input class="skn-input" style="width:150px;" id="<panel:encode name='<%="dateInputMin_" + dataProviderCode + "/" + propertyId%>'/>"
                                    name="<%=DashboardFilterHandler.PARAM_VALUE_MIN + "_" + propertyId%>"
                                    value="<%=minValue == null ? "" : minValue%>">
                                 <a style="height:20px; vertical-align:bottom;" href="#" onclick="NewCal('<panel:encode name='<%="dateInputMin_" + dataProviderCode + "/" + propertyId%>'/>','ddmmyyyy',true); return false; ">
                                     <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0">
                                 </a>
                                 &nbsp;<i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "to"%>'/> &nbsp;&nbsp;
                                <input class="skn-input" style="width:150px;" id="<panel:encode name='<%="dateInputMax_" + dataProviderCode + "/" + propertyId%>'/>"
                                        name="<%=DashboardFilterHandler.PARAM_VALUE_MAX + "_" + propertyId%>"
                                        value="<%=maxValue == null ? "" : maxValue%>">
                                 <a style="height:20px; vertical-align:bottom;" href="#" onclick="NewCal('<panel:encode name='<%="dateInputMax_" + dataProviderCode + "/" + propertyId%>'/>','ddmmyyyy',true); return false; ">
                                     <img src="<static:image relativePath="general/16x16/ico-calendar.png"/>" border="0">
                                 </a>
                            </div>
                        </td>
            </mvc:fragmentValue>
            </mvc:fragmentValue>
            </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>


    <mvc:fragment name="outputPropertyTypeNumeric">
        <mvc:fragmentValue name="propertyId" id="propertyId">
            <mvc:fragmentValue name="dataProviderCode" id="dataProviderCode">
                <mvc:fragmentValue name="minValue" id="minValue">
                    <mvc:fragmentValue name="maxValue" id="maxValue">
                        <td>
                            <input class="skn-input" style="width:150px;" type="text" id="<panel:encode name='<%="numericInputMin_" + dataProviderCode + "/" + propertyId%>'/>"
                                   name="<%=DashboardFilterHandler.PARAM_VALUE_MIN + "_" +propertyId%>"
                                    value="<%=minValue == null ? "" : minValue%>">
                            &nbsp;&nbsp;&nbsp;&nbsp; <i18n:message key='<%=DashboardFilterHandler.I18N_PREFFIX + "to"%>'/> &nbsp;&nbsp;&nbsp;
                            <input class="skn-input" style="width:150px;" type="text" id="<panel:encode name='<%="numericInputMax_" + dataProviderCode + "/" + propertyId%>'/>"
                                   name="<%=DashboardFilterHandler.PARAM_VALUE_MAX + "_" + propertyId%>"
                                    value="<%=maxValue == null ? "" : maxValue%>">
                        </td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>

    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>

</mvc:formatter>