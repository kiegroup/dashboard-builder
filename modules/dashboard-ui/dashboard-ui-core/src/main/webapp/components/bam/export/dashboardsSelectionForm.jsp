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
<%@ page import="org.jboss.dashboard.ui.components.export.ExportHandler" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.components.export.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jboss.dashboard.ui.formatters.ExportFormatter">
<mvc:fragment name="start">
<div style="padding:1px; height:509px; overflow:visible;">
</mvc:fragment>
<mvc:fragment name="workspaceSelector">
    <mvc:fragmentValue name="workspaceId" id="workspaceId">
    <mvc:fragmentValue name="thumbnail" id="thumbnail">
    <mvc:fragmentValue name="expandAction" id="expandAction">
    <mvc:fragmentValue name="expandIcon" id="expandIcon">
        <div class="skn-table_border "id="<panel:encode name="<%=(String)workspaceId%>"/>" style="width:250px; margin-bottom:1px; vertical-align:middle;">
            <table class="skn-table_header" width="100%" cellpadding="0" cellspacing="0" style="margin:0px; padding:4px">
                <tr>
                    <td width="15px" style="vertical-align:middle">
                        <img src="<static:image relativePath="<%= (String)thumbnail %>"/>" >
                    </td>
                    <td nowrap="nowrap" valign="middle" align="left" style="padding-left:5px;">
                       <a href="<factory:url action="selectWorkspace" friendly="true"><factory:param name="<%= ExportHandler.PARAM_WORKSPACE_ID %>" value="<%=(String)workspaceId%>"/></factory:url>"
                          style="font-weight:normal; color:#465F7D;" id="<panel:encode name='<%="link_"+workspaceId%>'/>" title="<i18n:message key="<%=(String)expandAction%>">!!! Expandir</i18n:message>">
                           <div style="margin-top:2px; width:200px; height:15px; cursor:pointer;"><mvc:fragmentValue name="workspaceName"/></div>
                       </a>
                       <script defer="defer">
                            setAjax("<panel:encode name='<%="link_"+workspaceId%>'/>");
                       </script>
                    </td>
                    <td width="15px" style="vertical-align:middle">
                        <img src="<static:image relativePath="<%=(String)expandIcon%>"/>">
                    </td>
                </tr>
            </table>
        </div>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="workspaceStart">
    <mvc:fragmentValue name="workspaceId" id="workspaceId">
    <mvc:fragmentValue name="nsections" id="nsections">
    <% int size = (((Integer)nsections).intValue()*29); size = size>290 ? 290 : size; size = size<100 ? 100 : size; %>
    <div id="<panel:encode name='<%=workspaceId+"_sections"%>'/>" style="height:<%=size+"px"%>; width:100%; overflow:auto;">
        <form action="<factory:formUrl/>" id="<panel:encode name="checkSectionForm"/>" method="post" enctype="multipart/form-data">
        <factory:handler action="checkSection"/>
        <input type="hidden" name="<%= ExportHandler.PARAM_SECTION_ID %>" />
        <table>
            <tr>
                <td align="left" colspan="2" style="padding:5px;">
                    <a style="text-decoration:underline;" href="<factory:url action="selectAllSections" />"
                       id="<panel:encode name="select_sections"/>">
                        <i18n:message key="select.all">!!! Todos</i18n:message></a>
                    &nbsp;-&nbsp;
                    <a style="text-decoration:underline;" href="<factory:url action="unselectAllSections" />"
                       id="<panel:encode name="unselect_sections"/>">
                        <i18n:message key="select.none">!!! Ninguno</i18n:message>
                    </a>
                    <script defer="true">
                        setAjax("<panel:encode name="select_sections"/>");
                        setAjax("<panel:encode name="unselect_sections"/>");
                    </script>
                </td>
            </tr>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="workspaceSection">
    <mvc:fragmentValue name="sectionName" id="sectionName">
    <mvc:fragmentValue name="sectionId" id="sectionId">
    <mvc:fragmentValue name="checked" id="checked">
    <% Boolean isChecked = (Boolean) checked; %>
            <tr>
                <td align="left" width="10px">
                    <input type="checkbox" name="<%= ExportHandler.PARAM_SECTION_ID + sectionId %>"
                           title="<i18n:message key='<%= isChecked.booleanValue() ? "dashboard.unselect" : "dashboard.select" %>'>!!! Seleccionar</i18n:message>"
                           onclick="this.form.<%=ExportHandler.PARAM_SECTION_ID %>.value='<%=sectionId%>'; submitAjaxForm(this.form);"
                           <%= isChecked.booleanValue() ? "checked" : "" %> />
                </td>
                <td align="left" class="<mvc:fragmentValue name="class"/>">
                    <mvc:fragmentValue name="sectionName"/>
                </td>
            </tr>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="workspaceEnd">
        </table>
        </form>
        <script defer="true">
             setAjax("<panel:encode name="checkSectionForm"/>");
        </script>
    </div>
</mvc:fragment>
<mvc:fragment name="end">
</div>
</mvc:fragment>
</mvc:formatter>