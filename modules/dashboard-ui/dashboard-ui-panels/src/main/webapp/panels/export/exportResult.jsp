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
                        <i18n:message key="exportResult">!!!Export result</i18n:message>
                </th>
            </tr>
            <tr>
                <td class="skn-background_alt" style="padding-top:20px; padding-bottom:20px;">
                    <mvc:formatter name="org.jboss.dashboard.ui.panel.export.RenderExportResultFormatter">
                        <mvc:fragment name="errors">
                        <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                         <img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>" border="0" />
                                    </td>
                                    <td>
                                        <i18n:message key="errorExporting">!!!Export error</i18n:message>: <mvc:fragmentValue name="errorMessage"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <br/>
                        </mvc:fragment>

                        <mvc:fragment name="warningOutputStart">
                            <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                            <img src="<static:image relativePath="general/16x16/ico-warning.png"/>" border="0" />
                                        </td>
                                        <td>
                                            <i18n:message key="warnings">!!!Warnings</i18n:message>:<br/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2">
                                            <ul>
                                        </mvc:fragment>
                                        <mvc:fragment name="warningOutput">
                                            <mvc:fragmentValue name="warning" id="warning">
                                                <mvc:fragmentValue name="arguments" id="arguments">
                                                    <span style="padding-left:15px; padding-right:5px;"><i18n:message key="<%=warning.toString()%>"><%=warning%></i18n:message></span>
                                                </mvc:fragmentValue>
                                            </mvc:fragmentValue>
                                        </mvc:fragment>
                                        <mvc:fragment name="warningOutputEnd">
                                            </ul>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <br/>
                        </mvc:fragment>

                        <mvc:fragment name="downloadResult">
                            <div class="skn-table_border" style="background-color:#ffffff; padding-top:10px; padding-bottom:10px; margin-left:8px; margin-right:8px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td style="padding-left:10px; padding-right:5px; vertical-align:top;">
                                            <img src="<static:image relativePath="general/16x16/ico-ok.png"/>" border="0" />
                                        </td>
                                        <td>
                                            <i18n:message key="downloadSuccess">!!!Export has completted successfully. Click the link below to download the resulting file.</i18n:message>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2" style="width:100%; text-align:center; padding-top:10px;">
                                            <a href="<mvc:fragmentValue name="url"/>" style="text-decoration:underline;"><i18n:message key="download">!!!Download</i18n:message></a>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </mvc:fragment>
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
