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
<%@ page import="java.util.Map" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderPanelHeaderFormatter">
<mvc:formatterParam name="panel" value="<%=panel%>"/>
<mvc:formatterParam name="administratorMode" value="<%=administratorMode%>"/>
<mvc:fragment name="outputStart">
    <mvc:fragmentValue name="panelId" id="panelId">
        <table cellspacing="0" cellpadding="0" border="0" bordercolor="green"
        width="100%">
        <tr >
        <td width="1" align="left"><resource:image category="skin" panelId="<%=(Long)panelId%>"
                                                   resourceId="HEADER_LEFT"/></td>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="panelTitle (Normal)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;<panel:localize
                                data="<%=(Map)panelTitle%>"/>&nbsp;&nbsp;</td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="panelTitle (Edit Mode)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left"style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;
                            <panel:localize data="<%=(Map)panelTitle%>"/>&nbsp;&nbsp;
                        </td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="panelTitle (Tabbed Edit Mode)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left"style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;
                        <a style='text-decoration:none;' href='<panel:link action="_select" panel="<%=String.valueOf(panelId)%>"/>'>
                            <panel:localize data="<%=(Map)panelTitle%>"/> &nbsp;&nbsp;
                        </a>
            </td>
            </tr>
            </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="panelTitle (Tabbed Normal)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;
                            <a style='text-decoration:none;'
                               href='<panel:link action="_select" panel="<%=String.valueOf(panelId)%>" />'>
                               <panel:localize data="<%=(Map)panelTitle%>"/>
                                &nbsp;&nbsp;
                            </a>
                        </td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="panelTitle (Tabbed Normal Selected)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;
                           <panel:localize data="<%=(Map)panelTitle%>"/>&nbsp;&nbsp;
                        </td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="panelTitle (Tabbed Edit Mode Selected)">
    <mvc:fragmentValue name="panelId" id="panelId">
        <mvc:fragmentValue name="panelTitle" id="panelTitle">
            <td nowrap align="left" style="Background-Image: url('<resource:link category="skin" resourceId="HEADER_BG"/>')">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td><resource:image category="skin" resourceId="BULLET" panelId="<%=(Long)panelId%>"/></td>
                        <td nowrap valign="middle">&nbsp;
                             <panel:localize data="<%=(Map)panelTitle%>"/>&nbsp;&nbsp;
                        </td>
                    </tr>
                </table>
            </td>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="beforePanelButtons">
    <td valign="middle" nowrap align="right" width="100%">&nbsp;</td>
    <td align="right" nowrap valign="middle">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
    <td nowrap valign="bottom">
</mvc:fragment>
<%--RENDERED FOR EVERY BUTTON--%>
<mvc:fragment name="panelButton">
    <mvc:fragmentValue name="buttonMessage" id="buttonMessage">
        <mvc:fragmentValue name="panelId" id="panelId">
            <mvc:fragmentValue name="imageId" id="imageId">
                <mvc:fragmentValue name="imageAlternative" id="imageAlternative">
                    <a title="<i18n:message key="<%=(String)buttonMessage%>">!!!<%=buttonMessage%></i18n:message>"
                       target="_top" href="<mvc:fragmentValue name="link"/>"><resource:image category="skin"
                                                                                             resourceId="<%=(String)imageId%>"
                                                                                             panelId="<%=(Long)panelId%>"
                                                                                             border="0"
                                                                                             align="absmiddle"><%=imageAlternative%>
                    </resource:image></a>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="afterPanelButtons">
    <mvc:fragmentValue name="panelId" id="panelId">
        </td>
        </tr>
        </table>
        </td>
        <td valign=middle width="1" align=right><resource:image category="skin" panelId="<%=(Long)panelId%>"
                                                                resourceId="HEADER_RIGHT"/></td>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputEnd">
    </tr>
    </table>
</mvc:fragment>
</mvc:formatter>