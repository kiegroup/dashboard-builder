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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="resources.tld" prefix="resource" %>


<mvc:formatter name="org.jboss.dashboard.ui.formatters.RegularLayoutFormatter">
    <mvc:fragment name="outputStart">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
    </mvc:fragment>
    <mvc:fragment name="administrationBar">
        <tr>
            <td>
                <mvc:include page="header_top.jsp" flush="true"/>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="noPage">
        <tr>
            <td>
                <mvc:include page="no_section_selected.jsp" flush="true"/>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="page">
        <tr>
            <td>
                <link rel="StyleSheet" media="screen" type="text/css" href="<mvc:context uri="/section/css/RenderSection.css"/>">
                <link rel="StyleSheet" media="screen" type="text/css" href="<mvc:context uri="/templates/css/panel_properties.css"/>">
                <resource:page id="template" category="layout" resourceId="JSP"/>
                <mvc:include page="<%=String.valueOf(template)%>" flush="true"/>
            </td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
    </mvc:fragment>
    <mvc:fragment name="unassignedPanels">
        <br>
        <div style=" margin-left:auto;margin-right:auto;width:50%; margin-top:40px; " >

            <mvc:include page="unassigned_panels.jsp" flush="true"/>

        </div>
    </mvc:fragment>
</mvc:formatter>
