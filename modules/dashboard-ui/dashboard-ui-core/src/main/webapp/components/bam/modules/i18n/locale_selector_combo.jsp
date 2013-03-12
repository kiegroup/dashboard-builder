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
<%--
  This JSP module has been designed to be included in forms where local selection is required.
  When the locale is changed the form is submitted via AJAX.
--%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%@ page import="java.util.*" %>
<%
    LocaleManager lm = LocaleManager.lookup();
    Locale current = lm.getCurrentLocale();
    Locale[] locales = lm.getPlatformAvailableLocales();
%>
<select name="<%= "localeLang" %>" class="skn-input"
         style="width:120px; height:18px; text-align:center; overflow:hidden; vertical-align:middle"
         onchange="return bam_kpiedit_submitProperties(this);">
<%
    for (int i = 0; i < locales.length; i++) {
        Locale locale = locales[i];
%>
    <option value="<%= locale.getLanguage() %>" <%= locale.getLanguage().equals(current.getLanguage()) ? "selected" : "" %>>
        <%= locale.getDisplayName(current) %>
    </option>
<%
   }
%>
</select>
