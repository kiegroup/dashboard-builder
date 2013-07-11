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
<%@ page import="org.jboss.dashboard.ui.UIServices" %>
<%@ page import="org.jboss.dashboard.ui.components.js.JSIncluder" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%
    JSIncluder jsIncluder = UIServices.lookup().getJsIncluder();
    String[] jsFiles = jsIncluder.getJsFilesToIncludeInHeader();
    for (int i = 0; i < jsFiles.length; i++) {
        String jsFile = jsFiles[i];
%>
    <script src='<mvc:context uri="<%= jsFile %>" />'></script>
<%
    }
%>
<script  language="Javascript" type="text/javascript">
<%
    String[] jspFiles = jsIncluder.getJspFilesToIncludeInHeader();
    for (int i = 0; i < jspFiles.length; i++) {
        String jspFile = jspFiles[i];
%>
    <jsp:include page="<%= jspFile %>" flush="true"/>
<%
    }
%>
</script>