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
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
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
                <i18n:message key="importResult">!!!Import result</i18n:message>
            </th>
        </tr>
        <tr>
        <td class="skn-background_alt" style="padding-top:20px; padding-bottom:20px;">
        <mvc:formatter name="org.jboss.dashboard.ui.panel.export.RenderImportResultFormatter">
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="fatalError">
            <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                            <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                        </td>
                        <td>
                            <i18n:message key="fatalError">!!!Error creando</i18n:message>
                        </td>
                   </tr>
                </table>
            </div>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
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
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="outputStart">
            <table border="0" border="0" cellpadding="6" cellspacing="0" width="100%">
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="createResultStart">
            <tr><td class="skn-background_alt">
            <table border="0" cellpadding="4" cellspacing="0" width="100%">
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="errors">
            <tr><td>
            <i18n:message key="errorLoading">!!!Error loading</i18n:message>
                    <mvc:fragmentValue name="errorMessage"/>
            </td></tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="abortedErrorMessageStart">
            <tr>
                <td class="skn-table_border" style="background-color:#ffffff;">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="padding-left:10px; padding-right:5px; vertical-align:top; width:16px;">
                                <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                            </td>
                            <td>
                                <i18n:message key="importAbortedError">!!!Elemento descartado debido a errores</i18n:message>
                           </td>
                        </tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="abortedSuccessMessageStart">
            <tr>
                <td class="skn-table_border" style="background-color:#ffffff;">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="padding-left:10px; padding-right:5px; vertical-align:top; width:16px;">
                                <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                            </td>
                            <td>
                                <i18n:message key="importAbortedSuccess">!!!Elemento descartado debido a otros errores</i18n:message>
                            </td>
                        </tr>
        </mvc:fragment>
        <mvc:fragment name="abortedEnd">
                    </table>
                </td>
            </tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="warningOutputStart">
            <mvc:fragmentValue name="uid" id="uid">
            <tr>
                <td colspan="2">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="padding-left:50px; padding-right:5px; vertical-align:top; width:16px;">
                                <img src="<static:image relativePath="general/16x16/ico-warning.png"/>" border="0" />
                            </td>
                            <td>
                                <a href="#" onclick="<panel:encode name='<%="modify"+uid%>'/>();return false;" style="margin:0px; padding:0px;"><i18n:message key="warnings">!!!Warnings</i18n:message></a>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <script type="text/javascript" defer>
                                    function <panel:encode name='<%="modify"+uid%>'/>() {
                                        var element = document.getElementById("<panel:encode name='<%="div"+uid%>'/>");
                                        if (element.style.display == 'none')
                                            element.style.display = 'block';
                                        else
                                            element.style.display = 'none'
                                    }
                                </script>
                                <div id="<panel:encode name='<%="div"+uid%>'/>" style="display:none">
                                    <ul>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="warningOutput">
            <mvc:fragmentValue name="warning" id="warning">
            <mvc:fragmentValue name="arguments" id="arguments">
                                        <li>
                                            <i18n:message key='<%="warning."+String.valueOf(warning)%>'
                                                          args="<%=(Object[])arguments%>"><%=warning%></i18n:message>
                                        </li>
            </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="warningOutputEnd">
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="workspaceSuccess">
            <mvc:fragmentValue name="name" id="name">
            <tr>
                <td class="skn-table_border" style="background-color:#ffffff;">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                <img src="<static:image relativePath="general/16x16/ico-ok.png"/>" border="0" />
                            </td>
                            <td>
                                <i18n:message key="workspaceCreateSuccess">!!! Creado con éxito el workspace </i18n:message>
                                &nbsp;<a href="<mvc:fragmentValue name="url"/>"><%=StringEscapeUtils.escapeHtml((String)name)%></a>
                           </td>
                        </tr>
                    </table>
                </td>
            </tr>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="resourceSuccess">
            <tr>
                <td class="skn-table_border" style="background-color:#ffffff;">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                <img src="<static:image relativePath="general/16x16/ico-ok.png"/>" border="0" />
                            </td>
                            <td>
                                <mvc:fragmentValue name="category" id="category">
                                    <i18n:message key="resourceCreateSuccess" args="<%=new Object[]{category}%>">!!! Creado con éxito el <%=category%></i18n:message>
                                </mvc:fragmentValue>
                                <mvc:fragmentValue name="name"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="createResultEnd">
            </table></td></tr>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="outputEnd">
            </table>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
        <mvc:fragment name="aborted"><br>
            <span class="skn-error" style="padding-left:10px;"><i18n:message key="operationAborted">!!!Operation aborted due to previous errors</i18n:message></span>
        </mvc:fragment>
        <%---------------------------------------------------------------------------------------------------------%>
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
