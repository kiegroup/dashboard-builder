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
<%@ taglib  uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib  uri="factory.tld" prefix="factory" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.config.components.homePages.HomePagesFormatter">
    <mvc:fragment name="outputStart">
        <form action="<factory:formUrl/>" method="POST">
            <factory:handler bean="org.jboss.dashboard.ui.config.components.homePages.HomePagesHandler" action="saveHomePages"/>
            <table width="80%" border="0" cellspacing="1" cellpadding="0" class="skn-table_border">
                <tr class="skn-table_header">
                    <td colspan="2">
                        <i18n:message key="ui.users.defaultSection">!!!Roles en el workspace</i18n:message>
                    </td>
                </tr>
    </mvc:fragment>
    <mvc:fragment name="outputRoleStart">
                <tr class="<mvc:fragmentValue name="className"/>">
                    <td>
                        <mvc:fragmentValue name="roleDescription"/>
                    </td>
    </mvc:fragment>

    <mvc:fragment name="outputSelectStart">
                    <td>
                        <select class="skn-input" name="<mvc:fragmentValue name="inputName"/>">
    </mvc:fragment>
    <mvc:fragment name="outputPageSelectOption">
        <mvc:fragmentValue name="selected" id="selected">
                            <option <%=Boolean.TRUE.equals(selected)?"selected":""%>  value="<mvc:fragmentValue name="sectionId"/>" ><mvc:fragmentValue name="sectionName"/></option>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputSelectEnd">
                        </select>
                    </td>
                </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" value="<i18n:message key="ui.saveChanges">!!! Guardar Cambios</i18n:message> " class="skn-button">
                    </td>
                </tr>
            </table>
        </form>
    </mvc:fragment>
</mvc:formatter>