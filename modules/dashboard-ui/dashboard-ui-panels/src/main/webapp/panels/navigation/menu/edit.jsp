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
<%@ page import="org.jboss.dashboard.ui.panel.navigation.menu.MenuDriver"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.messages"
             locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.panel.navigation.menu.RenderMenuFormatter">
    <mvc:fragment name="outputSelected">
        <input type="checkbox"
               name="<mvc:fragmentValue name="inputName"/>"
               value="<mvc:fragmentValue name="itemId"/>" checked>
        <mvc:fragmentValue name="text"/><br>
    </mvc:fragment>
    <mvc:fragment name="outputNotSelected">
        <input type="checkbox"
               name="<mvc:fragmentValue name="inputName"/>"
               value="<mvc:fragmentValue name="itemId"/>">
        <mvc:fragmentValue name="text"/><br>
    </mvc:fragment>
    <mvc:fragment name="outputAllItemsCheckbox">
        <input type="checkbox"
               name="<mvc:fragmentValue name="inputName"/>"
               value="<%=MenuDriver.PARAMETER_ALL_ITEMS%>"
               <mvc:fragmentValue name="allItemsSelected"/> >
        <i18n:message key="ui.allM"/><br>
    </mvc:fragment>
    <mvc:fragment name="submitButton">
        <input type="submit" class="skn-button" value="<i18n:message key="ui.saveChanges"/>">
    </mvc:fragment>
</mvc:formatter>
