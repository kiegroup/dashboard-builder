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
<%@ include file="../common/global.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<br><br>
<form name="config" action="<panel:mvclink handler='section' action='create'/>" method="post">
    <table border="0" cellspacing="0" cellpadding="0" class="skn-table_border skn-background_alt" width="70%">
        <tr>
            <td align="center"><br><font class="skn-title3"><i18n:message key="ui.sections.createNew"/></font><br><br></td>
        </tr>
        <tr>
            <td width="100%">
                <jsp:include page="section_properties.jsp" flush="true"/>
            </td>
        </tr>
        <tr>
            <td align="center">
                <br>
                <i18n:message key="ui.saveChanges" id="saveChangesMsg"/>
                <input class="skn-button" type="submit" value="<%=saveChangesMsg%>">
                <br>
                <br>
            </td>
        </tr>
    </table>
</form>
