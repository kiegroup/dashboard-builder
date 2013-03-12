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
<%@ page import="org.jboss.dashboard.workspace.Panel,
                 com.polorg.jboss.dashboard.workspace.PanelInstance            org.jboss.dashboard.workspace.Section" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.TreeSet"%>
<%@ page import="org.jboss.dashboard.workspace.copyoptions.SectionCopyOption" %>
<%@ page import="org.jboss.dashboard.workspace.PanelInstance" %>
<%@ include file="../common/global.jsp" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>

<%
    String sectionId = request.getParameter("id");
    Long lSectionId = new Long(sectionId);
    Section section = currentWorkspace.getSection(lSectionId);
%>

<table width="70%" cellpadding="0" cellspacing="0" border="0">
    <tr>
        <td valign=top>
            <jsp:include page="../workspace/menu.jsp" flush="true"/>
        </td>
    </tr>
    <tr>
        <td valign="top" align="center">
            <table border="0" width="100%" height="200" class="skn-table_border skn-background_alt" align="center" valign="middle">
                <tr>
                    <td align="center" valign="middle" >
                        <br><br>
                        <span class="skn-title2"><i18n:message key="ui.sections.duplicate"/> <panel:localize
                                data="<%=section.getTitle()%>"/></span>
                        <br><br>

                        <form name="sectionDuplicate" action="<panel:mvclink handler='section' action='duplicate'/>"
                              method="post">
                            <input type="hidden" name="id" value="<%=sectionId%>">
                            <%
                                TreeSet panelInstances = new TreeSet();
                                Panel[] panels = section.getAllPanels();
                                for (int i = 0; i < panels.length; i++) {
                                    Panel panel = panels[i];
                                    panelInstances.add(panel.getInstanceId());
                                }
                                if (!panelInstances.isEmpty()) {
                            %>

                            <span >
                    <i18n:message key="ui.sections.choosePanelDuplicationMode"/>:
                </span>
                            <br><br>
                            <select class="skn-input" name="<%=SectionCopyOption.COPY_MODE%>" onchange="
                if(value == '<%=SectionCopyOption.COPY_SOME%>'){
                    document.getElementById('panelsToDuplicateDIV').style.display='block';
                }
                else {
                    document.getElementById('panelsToDuplicateDIV').style.display='none';
                };
                return true;
                ">
                                <option value="<%=SectionCopyOption.COPY_NONE%>">
                                    <i18n:message key="ui.sections.doNotDuplicatePanels"/>
                                </option>
                                <option value="<%=SectionCopyOption.COPY_ALL%>">
                                    <i18n:message key="ui.sections.duplicateAllPanels"/>
                                </option>
                                <option value="<%=SectionCopyOption.COPY_SOME%>" selected>
                                    <i18n:message key="ui.sections.choosePanelsToDuplicate"/>
                                </option>
                            </select>
                            <br><br>

                            <div id="panelsToDuplicateDIV">
                                <table cellpadding=4 cellspacing=1 width=500 align="center" valign="middle" >
                                    <tr class="skn-table_header">
                                        <td  width="18">
                                            <i18n:message key="ui.id"/></td>
                                        <td ><i18n:message
                                                key="ui.group"/></td>
                                        <td ><i18n:message
                                                key="ui.type"/></td>
                                        <td ><i18n:message
                                                key="ui.title"/></td>
                                        <td align="center">
                                            <i18n:message key="ui.sections.duplicateInstance"/></td>
                                        <td align="center">
                                            <i18n:message key="ui.sections.noDuplicateInstance"/></td>
                                    </tr>
                                    <%
                                        int counter = 0;
                                        for (Iterator it = panelInstances.iterator(); it.hasNext();counter++) {
                                            String instanceId = it.next().toString();
                                            PanelInstance instance = section.getWorkspace().getPanelInstance(instanceId);
                                    %>

                                    <tr class="<%=counter%2==0?"skn-odd_row":"skn-even_row"%>">
                                        <td><%=instanceId%></td>
                                        <td><%=instance.getResource(instance.getProvider().getGroup(), currentLocale)%></td>
                                        <td><%=instance.getResource(instance.getProvider().getDescription(), currentLocale)%></td>
                                        <td><panel:localize data="<%=instance.getTitle()%>"/></td>
                                        <td align="center">
                                            <input type="radio" name="<%="duplicatePanelInstance_"+instanceId%>"
                                                   value="true">
                                        </td>
                                        <td align="center">
                                            <input type="radio" name="<%="duplicatePanelInstance_"+instanceId%>"
                                                   value="false" checked>
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </table>
                            </div>
                            <%
                                }
                            %>
                            <i18n:message key="ui.sections.duplicate" id="duplicateMsg"/>
                            <br><br><input class="skn-button" type="submit" value="<%=duplicateMsg%>">&nbsp;<br><br>
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
