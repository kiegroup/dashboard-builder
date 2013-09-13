<%--

    Copyright (C) 2013 JBoss Inc

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
<%@ page import="org.jboss.dashboard.ui.components.export.ExportHandler" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<table width="700px" cellpadding="0" cellspacing="0" border="0" align="left">
    <tr>
        <td valign="top" align="left" class="skn-table_border" style="padding:1px">
			<factory:setProperty bean="org.jboss.dashboard.ui.components.ExportHandler"
								 property="mode" propValue="<%= ExportHandler.MODE_IMPORT %>" />
            <factory:useComponent bean="org.jboss.dashboard.ui.components.ExportHandler" />
        </td>
    </tr>
</table>
