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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>


<mvc:formatter name="org.jboss.dashboard.ui.formatters.UnassignedPanelsFormatter">
    <%------------------------------------------------------------------------%>
    <mvc:fragment name="outputStart">
        <div style="margin-bottom:10px;"><b>
            <i18n:message key="ui.notAssignedPanels"/>
        </b></div>
        <table align="center" cellpadding="0" cellspacing="0" class="skn-table_border"><tr><td>
        <table cellpadding="3" cellspacing="1" border="0" align="center">
        <tr class="skn-table_header">
            <td height="18">
                <i18n:message key="ui.id"/>
                :
            </td>
            <td>
                <i18n:message key="ui.title"/>
                :
            </td>
            <td>
                <i18n:message key="ui.type"/>
                :
            </td>
            <td>
                <i18n:message key="ui.region"/>
                :
            </td>
            <td>&nbsp;</td>
        </tr>
    </mvc:fragment>
    <%------------------------------------------------------------------------%>
    <mvc:fragment name="outputPanel">
        <mvc:fragmentValue name="index" id="index">
            <mvc:fragmentValue name="panelId" id="panelId">
        <tr class="<%=((Integer)index).intValue()%2==1?"skn-even_row":"skn-odd_row"%>" valign="top">
                <td ><%=panelId%></td>
                <td style="white-space:nowrap;"><mvc:fragmentValue name="panelTitle"/></td>
                <td style="white-space:nowrap;"><mvc:fragmentValue name="providerType"/></td>
                <td style="white-space:nowrap;">
                    <form method="post" style="margin:0px;" action="<factory:url bean='org.jboss.dashboard.ui.components.PanelsHandler' action='moveToRegion'/>">
                        <input type='hidden' name='panelId' value='<%=panelId%>'>
                        <select class="skn-input" name="region" onChange='form.submit()'>
                            <option value="">--- <i18n:message key="ui.selectRegion"/> ---</option>
                            <mvc:fragmentValue name="regions" id="regions">
                            <mvc:formatter name="org.jboss.dashboard.ui.formatters.ForFormatter">
                                <mvc:formatterParam name="array" value="<%=regions%>"/>
                                <mvc:fragment name="output">
                                    <option value="<mvc:fragmentValue name="element/id"/>">
                                        <mvc:fragmentValue name="element/description"/>
                                    </option>
                                </mvc:fragment>
                            </mvc:formatter>
                            </mvc:fragmentValue>
                        </select>
                    </form>
                </td>
                <td>
                    <i18n:message key="ui.delete" id="deleteMsg"/>
                    <i18n:message key="ui.panel.confirmDelete" id="confirmDeleteMsg"/>

                    <a href="<panel:link panel='<%=panelId.toString()%>' action='_remove'/>" onclick="return confirm('<%=confirmDeleteMsg%>');">
                        <img src="<static:image relativePath="general/16x16/ico-directory-trash.png"/>" border="0" alt="<%=deleteMsg%>" title="<%=deleteMsg%>" />
                    </a>
                </td>
            </tr>
                </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <%------------------------------------------------------------------------%>
    <mvc:fragment name="outputEnd">
        </table>
        </td></tr></table>
    </mvc:fragment>
    <%------------------------------------------------------------------------%>
</mvc:formatter>

