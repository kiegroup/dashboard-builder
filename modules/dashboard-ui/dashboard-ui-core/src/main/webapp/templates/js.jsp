<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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
    for (String jsFile : jsIncluder.getJsHeaderFiles()) {
%>
    <script src='<mvc:context uri="<%= jsFile %>" />'></script>
<%
    }
%>
<script  language="Javascript" type="text/javascript">
<%
    for (String jspFile : jsIncluder.getJspHeaderFiles()) {
%>
    <jsp:include page="<%= jspFile %>" flush="true"/>
<%
    }
%>
</script>