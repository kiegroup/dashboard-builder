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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.panel.advancedHTML.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<script type="text/javascript" language="javascript">
    function openOrClose(itemId) {
        var divElement = document.getElementById(itemId);
        if (divElement.style.display == 'none') {
            divElement.style.display = 'block';
        }
        else {
            divElement.style.display = 'none';
        }
        return false;
    }

    function selectUrl(url) {
        window.top.opener.SetUrl(url);
        window.top.opener.document.getElementById("cmbLinkProtocol").value = '';
        window.close();
        return false;
    }
</script>

<table border="0" width="100%" cellspacing="0" cellpadding="5" style="border-top:solid 1px #c2c28f; border-left:solid 1px #c2c28f; border-bottom:solid 1px #aaaa7d; border-right:solid 1px #aaaa7d;">
    <tr>
        <td>
            <mvc:formatter name="org.jboss.dashboard.ui.formatters.ImageSelectionFormatter">
    <mvc:formatterParam name="numCols" value="4"/>
    <mvc:fragment name="outputStart">
        <i18n:message key="linkToFile">!!!To file</i18n:message>:
        <div style="width:99%; height:200px; overflow:auto; background: #F1F1E3; padding:5px;">
        <table border=0>
    </mvc:fragment>
    <mvc:fragment name="galleryOutputStart">
        <tr>
            <td style="FONT-SIZE: 10pt; font-weight: bold; COLOR: #000000; font-family : Verdana, Arial, Helvetica, sans-serif;">
                <a href="#" onclick="return openOrClose('gallery<mvc:fragmentValue name="galleryIndex"/>')">
                    <mvc:fragmentValue name="galleryName"/></a>
            </td>
        </tr>
        <tr>
        <td><div id="gallery<mvc:fragmentValue name="galleryIndex"/>" style="display:none;"><table>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceRowStart">
        <tr>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceOutput">
        <td><a href="#" STYLE="font-size:8px"
               onclick=" return selectUrl( '<mvc:fragmentValue name="resourceUrl"/>' ); "><mvc:fragmentValue
                name="resourceId"/></a></td>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceRowEnd">
        </tr>
    </mvc:fragment>
    <mvc:fragment name="galleryOutputEnd">
        </table></div></td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
        </div>
    </mvc:fragment>
</mvc:formatter>
        </td>
        </tr>
    <tr>
        <td>
            <mvc:formatter name="org.jboss.dashboard.ui.formatters.PageSelectionFormatter">
        <mvc:formatterParam name="numCols" value="1"/>
        <mvc:fragment name="outputStart">
            <i18n:message key="linkToPage">!!!To page</i18n:message>: <a href="#"
                                                                         onclick="return false;"
                                                                         onmouseover="
                                x= event.x?event.x:event.clientX;
                                y= event.y?event.y:event.clientY;
                                document.getElementById('hlpDiv').style.left = x +20;
                                document.getElementById('hlpDiv').style.top  = y -20;
                                document.getElementById('hlpDiv').style.visibility='visible';"
                                                                         onmouseout="document.getElementById('hlpDiv').style.visibility='hidden';"
                >(!)</a>
            <div style="position:relative">
            <div id="hlpDiv"
                 style="background: #EEEEEE; border:solid; padding:5px; position: absolute; top: 0; left: 0; visibility:hidden; z-index: 5000; width:300;">
                <i18n:message key="securityWarning">!!!Warning </i18n:message>
            </div>
            </div>
           <div style="width:99%; height:200px; overflow:auto; background: #F1F1E3; padding:5px;">
            <table border=0 cellspacing=0 cellpadding=0>
        </mvc:fragment>
        <mvc:fragment name="workspaceOutput">
            <tr>
                <td style="  font-weight: bold; COLOR: #000000; font-family : Verdana, Arial, Helvetica, sans-serif;">
                    <a href="#" onclick="return selectUrl('<mvc:fragmentValue name="url"/>')">Workspace <mvc:fragmentValue
                            name="workspaceName"/></a>
                </td>
            </tr>
            <tr>
            <td style="padding-left:8px">
                <mvc:fragmentValue name="workspaceId" id="workspaceId">
                <mvc:formatter name="org.jboss.dashboard.ui.formatters.RenderIndentedSectionsFormatter">
                    <mvc:formatterParam name="preffix" value="&nbsp;&nbsp;"/>
                    <mvc:formatterParam name="workspaceId" value="<%=workspaceId%>"/>
                    <mvc:formatterParam name="permanentLink" value="true"/>
                    <mvc:fragment name="empty">
                        <span class="skn-title3 skn-error">
                            <i18n:message key="ui.sections.noSections"/>
                        </span>
                    </mvc:fragment>
                    <mvc:fragment name="output">
                        <a href="#" onclick="return selectUrl( '<mvc:fragmentValue name="url"/>' );"><mvc:fragmentValue name="title"/></a><br>
                    </mvc:fragment>
                    <mvc:fragment name="outputSelected">
                        <a href="#" onclick="return selectUrl( '<mvc:fragmentValue name="url"/>' );"><mvc:fragmentValue name="title"/></a>
                    </mvc:fragment>
                </mvc:formatter>
                </mvc:fragmentValue>
            </td>
            </tr>
        </mvc:fragment>
        <mvc:fragment name="outputEnd">
            </table>
            </div>
        </mvc:fragment>
    </mvc:formatter>
        </td>
    </tr>
</table>
