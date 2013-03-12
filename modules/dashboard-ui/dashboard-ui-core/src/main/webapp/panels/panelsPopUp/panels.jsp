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
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>

<link rel="StyleSheet" media="screen" type="text/css" href="<mvc:context uri="/panels/panelsPopUp/css/panels_popup.css"/>">

<table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td width="50%" valign="top" align="left">
            <div style="height:305px;overflow:auto;">
                <%@ include file="panelsGroups.jsp" %>
            </div>
        </td>
        <td width="50%" valign="top" align="left" style="padding-left: 10px;">
            <div style="height:305px; overflow:hidden;">
                <%@ include file="panelsInstances.jsp" %>
            </div>
        </td>
    </tr>
</table>