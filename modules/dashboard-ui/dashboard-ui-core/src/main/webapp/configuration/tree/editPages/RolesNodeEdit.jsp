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
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.messages"
             locale="<%= LocaleManager.currentLocale() %>"/>
<fieldset style="border:solid 1px #8eb6e6;margin:10px;padding:10px">
    <legend class="skn-title3">
        <i18n:message key="ui.roles.rolesManagement"/>
    </legend>
    <br>
    INSERTAR CRUD DE ROLES AQUI
</fieldset>
