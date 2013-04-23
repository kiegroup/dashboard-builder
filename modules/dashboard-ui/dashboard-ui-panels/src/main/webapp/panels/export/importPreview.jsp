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
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.panel.export.messages"
             locale="<%=SessionManager.getCurrentLocale()%>"/>

<table width="55%" border="0" cellpadding="0" cellspacing="0">
    <tr><td>
        <table width="100%" border="0" align="left" cellpadding="4" cellspacing="1" class="skn-table_border">
        <tr>
            <th height="15" class="skn-table_header skn-table_border" align="center">
                <i18n:message key="selectObjectsToImport">!!!Select the objects you want to import.</i18n:message>
            </th>
        </tr>
        <tr>
            <td class="skn-background_alt" style="padding-top:20px; padding-bottom:20px;">
                <mvc:formatter name="org.jboss.dashboard.ui.panel.export.RenderImportPreviewFormatter">
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="empty">
                        <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                            <table border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                     <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                         <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                                    </td>
                                    <td>
                                        <i18n:message key="nothingToImport">!!!No hay nada que importar</i18n:message>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="fatalError">
                        <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                            <table border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                        <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                                    </td>
                                    <td>
                                        <i18n:message key="corruptFile">!!!Fichero corrupto</i18n:message>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="outputStart">
                        <form id="<panel:encode name="importForm"/>" action="<panel:link action="import"/>">
                        <panel:hidden action="import"/>
                        <table width="100%" border="0" cellpadding="4" cellspacing="1">
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="entryStart">
                        <tr><td align="left">
                        <mvc:fragmentValue name="entryName"/>:
                        <div align="left" style="width:500; height:120; overflow:auto; border:1 solid #808080; margin-top: 5px; padding:5px; background-color:#FFFFFF" class="skn-table_border">
                        <table width="100%" border="0" cellpadding="4" cellspacing="1">
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="errors">
                        <tr>
                            <td style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                            <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                                        </td>
                                        <td>
                                            <i18n:message key="errorLoading">!!!Error loading</i18n:message>
                                            <mvc:fragmentValue name="errorMessage"/>
                                            <mvc:fragmentValue name="exception" id="exception">
                                                <%((Throwable) exception).printStackTrace();%>
                                            </mvc:fragmentValue>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="warningOutputStart">
                        <tr>
                            <td style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td style="padding-left:15px; padding-right:5px; vertical-align:top;">
                                            <img src="<static:image relativePath="general/16x16/ico-warning.png"/>" border="0" />
                                        </td>
                                        <td>
                                            <i18n:message key="warnings">!!!Warnings</i18n:message>:<br>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2">
                                            <ul>
                                        </mvc:fragment>
                                        <mvc:fragment name="warningOutput">
                                            <mvc:fragmentValue name="warning" id="warning">
                                                <mvc:fragmentValue name="arguments" id="arguments">
                                                    <span class="skn-error"><i18n:message key='<%="warning."+String.valueOf(warning)%>'
                                                                  args="<%=(Object[])arguments%>"><%=warning%></i18n:message></span>
                                                </mvc:fragmentValue>
                                            </mvc:fragmentValue>
                                        </mvc:fragment>
                                        <mvc:fragment name="warningOutputEnd">
                                            </ul>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="emptyEntry">
                        <tr><td align="center">
                            <span style="padding-left:15px; padding-right:5px;"><img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" /></span>
                            !!! No hay nada que importar
                        </td></tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="entryElementsOutputStart">
                        <tr><td align="left">
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="workspaceEntryElement">
                        <input type="checkbox" checked value="true" name="<mvc:fragmentValue name="inputName"/>">
                        WORKSPACE: <mvc:fragmentValue name="entryElementName"/><br>
                    </mvc:fragment>
                    <mvc:fragment name="resourceEntryElement">
                        <input type="checkbox" checked value="true" name="<mvc:fragmentValue name="inputName"/>">
                        RESOURCE: <mvc:fragmentValue name="entryElementName"/><br>
                    </mvc:fragment>
                    <mvc:fragment name="entryElement">
                        UNKNOWN: <mvc:fragmentValue name="entryElementName"/><br>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="entryElementsOutputEnd">
                        </td></tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="entryEnd">
                        </table></div></td></tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="importButton">
                        <tr><td>
                        <div align="center" style="padding-top:10px;"><input type="submit" class="skn-button" value="<i18n:message key="import">!!!Import</i18n:message>"></div>
                        </td></tr>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                    <mvc:fragment name="outputEnd">
                        </table>
                        </form>
                        <script type="text/javascript" defer>
                            setAjax('<panel:encode name="importForm"/>');
                        </script>
                    </mvc:fragment>
                    <%------------------------------------------------------------------------------------------------------------------------%>
                </mvc:formatter>
            </td>
        </tr>
        </table>
    </td></tr>
    <tr><td>
        <div style="width:100%; text-align:center;">
            <form action="<panel:link action="GoToStart"/>" id="<panel:encode name="back"/>" method="post" enctype="multipart/form-data">
                <panel:hidden action="GoToStart"/>
                <input style="bottom:100%; vertical-align:bottom; margin-top:10px;" type="submit" class="skn-button" value="<i18n:message key="back">!!!Back</i18n:message>">
            </form>
        </div>
    </td></tr>
</table>
<script type="text/javascript" defer>
    setAjax("<panel:encode name="back"/>");
</script>
