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
<%@ page import="org.jboss.dashboard.workspace.Parameters" %>
<%@ page import="org.jboss.dashboard.ui.SessionManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%
    String preffix = request.getContextPath();
    while (preffix.endsWith("/")) preffix = preffix.substring(preffix.length() - 1);
%>
<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=SessionManager.getCurrentLocale()%>"></i18n:bundle>

<link rel="StyleSheet" media="screen" type="text/css"
      href="<mvc:context uri="/templates/css/administration_bar.css"/>">

<jsp:include page="/components/modalDialogComponent/show.jsp"/>
<mvc:formatter name="org.jboss.dashboard.ui.config.formatters.ConfigurationPageFormatter">
<mvc:fragment name="output">
    <table id="administrationMenuTable">
        <tr>
            <td>
                <div align="left" parseWidgets="false"><a href="index.jsp">
                    <img border="0" title="<i18n:message key='ui.envelope.backToWorkspace'/>"
                         src="<static:image relativePath="general/workspace_button.png"/>">
                </a></div>
            </td>
        </tr>
        <tr>
            <td class="shadow"></td>
        </tr>
    </table>
    <br>

    <table align="center" width="95%" style="border-radius: 18px;" class="skn-table_border">
        <tr>
            <td style="padding: 15px;">

                <table width="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF" class="skn-table_border">
                    <tr>
                        <td id="myRow" width="400" height="700px" valign="top"
                            class="skn-background_alt">
                            <div id="SomeDiv1" width="100%" style=" overflow:-moz-scrollbars-horizontal;overflow-x:hidden;border-style:solid;border-right-width:0px;border-bottom-width:0px;border-top-width:1px;border-left-width:1px;border-color:#4f565e;">
                                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td><jsp:include page="tree/tree.jsp" flush="true"/></td>
                                        <td align="right"><div class="corner" id="DragHandleSomeDiv1" >&nbsp;</div></td>
                                    </tr>
                                </table>

                            </div>

                        </td>
                        <td valign="top">
                            <div id="editPagePane" style="display:block; height:700px; border-style:none; border-width:1px 0 0 1px;border-color:#4f565e; text-align: left; vertical-align: top;">

                                <mvc:fragmentValue name="editPage" id="editPage">
                                    <mvc:fragmentValue name="ajaxCompatible" id="ajaxCompatible">
                                        <mvc:fragmentValue name="icon_Node" id="icon_Node">
                                            <mvc:fragmentValue name="iconNodePath" id="iconNodePath">
                                                <mvc:fragmentValue name="name_Node" id="name_Node">

                                                    <table width="100%" align="center" border="0" cellpadding="0"
                                                           cellspacing="0" >
                                                        <tr>
                                                            <td class="skn-background_alt" width="10"
                                                                style="padding-left:5px;">
                                                                <% if (icon_Node == null || "".equals((String) icon_Node)) {%>
                                                                <img src="<static:image relativePath="general/spacer.png"/>" height="18" width="1"/>
                                                                <% } else { %>
                                                                <img src="<static:image relativePath='<%="general/tree/"+icon_Node%>'/>"
                                                                     title="<%= (String) name_Node %>" border="0">
                                                                <% } %>
                                                            </td>
                                                            <td align="left" class="skn-background_alt" height="25px">
                            <span class="skn-title2" style="font-weight:normal;">&nbsp;<mvc:fragmentValue
                                    name="description"/></span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="3" class="skn-table_border" style="border-bottom: none;"></td>
                                                        </tr>
                                                    </table>
                                                    <div style="overflow: auto;height:675px;">
                                                        <table border="0" cellpadding="0" cellspacing="0" width="90%" align="center" style="margin-top:15px;">
                                                            <tr>
                                                                <td>
                                                                    <%if (editPage != null) {%>
                                                                    <jsp:include page="<%=(String)editPage%>"
                                                                                 flush="true"/>
                                                                    <%}%>

                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                </mvc:fragmentValue>
                                            </mvc:fragmentValue>
                                        </mvc:fragmentValue>
                                    </mvc:fragmentValue>
                                </mvc:fragmentValue>

                            </div>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
    </table>


    <script type="text/javascript" language="javascript">


        function createCookie(name, value_x, days) {

            document.cookie = name +"="+value_x;
        }

        function readCookie(name) {
            var nameField = name + "=";
            var c = document.cookie.split(';');

            for(var i=0;i < c.length;i++) {
                var aux2 = c[i];
                while (aux2.charAt(0)==' ') aux2 = aux2.substring(1,aux2.length);
                if (aux2.indexOf(nameField) == 0) {
                    return aux2.substring(nameField.length,aux2.length);
                }
            }
            return null;
        }

        function readPosition(divName) {

            var cookie = readCookie("menu_position_"+divName);
            var valuex;
            if(cookie == null){
                valuex = "400";
            } else {
                valuex = cookie ? cookie : "400";
            }
            return valuex;

        }

        function storePosition(divName,value) {
            createCookie("menu_position_" + divName , value,1);
        }

        function DragCorner(container, handle, minsize) {
            var container = $(container);
            var handle = $(handle);

            /* Add property to container to store position variables */
            container.moveposition = {x:0, y:0};

            function moveListener(event) {
                /* Calculate how far the mouse moved */

                var moved = {
                    x:(event.pointerX() - container.moveposition.x),
                    y:0
                };
                /* Reset container's x/y utility property */
                container.moveposition = {x:event.pointerX(), y:0};
                /* Border adds to dimensions */
                var borderStyle = container.getStyle('border-width');
                var borderSize = borderStyle.split(' ')[0].replace(/[^0-9]/g,'');
                /* Padding adds to dimensions */
                var paddingStyle = container.getStyle('padding');
                var paddingSize = paddingStyle.split(' ')[0].replace(/[^0-9]/g,'');
                /* Add things up that change dimensions */
                var sizeAdjust = (borderSize*2) + (paddingSize*2);
                /* Update container's size */
                var size = container.getDimensions();
                var a = size.width + moved.x-sizeAdjust;
                if(size.width + moved.x-sizeAdjust > minsize){
                    container.setStyle({
                        //height: size.height+moved.y-sizeAdjust+'px',
                        width:size.width+moved.x-sizeAdjust+'px'
                    });
                    storePosition('SomeDiv1',a);
                    document.getElementById("myRow").width=a;
                }
            }

            /* Listen for 'mouse down' on handle to start the move listener */
            handle.observe('mousedown', function(event) {
                /* Set starting x/y */
                container.moveposition = {x:event.pointerX(),y:0};
                /* Start listening for mouse move on body */
                Event.observe(document.body,'mousemove',moveListener);
            });

            /* Listen for 'mouse up' to cancel 'move' listener */
            Event.observe(document.body,'mouseup', function(event) {
                Event.stopObserving(document.body,'mousemove',moveListener);
            });
        }

        var desp= readPosition('SomeDiv1');
        DragCorner('SomeDiv1','DragHandleSomeDiv1',400);
        document.getElementById("myRow").width=desp;


    </script><style type="text/css">
    div.corner {
        background-color: #979797;
        border-left: 1px solid #FFFFFF;
        border-right: 1px solid #000000;
        cursor: w-resize;
        height: 700px;
        position: relative;
        width: 4px;
    }
</style>
</mvc:fragment>
<mvc:fragment name="accessDenied">
    <jsp:include page="../../login.jsp"/>
</mvc:fragment>
</mvc:formatter>


