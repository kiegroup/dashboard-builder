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
<%@ page import="org.jboss.dashboard.Application" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<html lang="<factory:property bean="org.jboss.dashboard.LocaleManager" property="currentLang"/>">
<head>
    <panel:envelopeHead/>
    <title><panel:propertyRead localize="true" object="workspace" property="title"/> - <panel:propertyRead localize="true" object="section" property="title"/></title>
</head>

<body style="background:white;">

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">

    <tr>
        <td valign="top" align="center"><mvc:pane id='center'/></td>
    </tr>
    <tr>
      <td valign="top" align="center">&nbsp;</td>
    </tr>
    <tr>
      <td valign="top" align="center"><br><%= Application.lookup().getCopyright() %></td>
    </tr>
</table>
<panel:envelopeFooter/>
</body>
</html>