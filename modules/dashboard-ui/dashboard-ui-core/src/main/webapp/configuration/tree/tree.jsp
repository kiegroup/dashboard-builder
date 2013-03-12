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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%
    try {
        String preffix = request.getContextPath();
        while (preffix.endsWith("/")) preffix = preffix.substring(preffix.length() - 1);
%>
<mvc:formatter name="org.jboss.dashboard.ui.config.formatters.TreeFormatter">
    <mvc:fragment name="treeStart">

    </mvc:fragment>
    <mvc:fragment name="mainNode">
        <mvc:fragmentValue name="mainNode" id="mainNode">
            <mvc:fragmentValue name="id_mainNode" id="id_mainNode">
                <mvc:fragmentValue name="level_mainNode" id="level_mainNode">
                    <mvc:fragmentValue name="path_mainNode" id="path_mainNode">
                        <mvc:fragmentValue name="icon_mainNode" id="icon_mainNode">
                            <mvc:fragmentValue name="name_mainNode" id="name_mainNode">
                                <mvc:fragmentValue name="expand_icon" id="expand_icon">
                                    <mvc:fragmentValue name="expand_action" id="expand_action">

                                        <div style="width:100%; height:700px; overflow:auto; overflow-y:auto; overflow-x:auto;">

                                            <table border="0" cellspacing="0" cellpadding="0" align="left" width="10%"  style="margin-top: 15px;">
                                                <% request.setAttribute("node", mainNode); %>
                                                <jsp:include page="node.jsp" flush="true"/>
                                                <% request.removeAttribute("node"); %>
                                            </table>

                                        </div>
                                    </mvc:fragmentValue>
                                </mvc:fragmentValue>
                            </mvc:fragmentValue>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="treeEnd">

    </mvc:fragment>
    <mvc:fragment name="error">
    </mvc:fragment>
</mvc:formatter>
<%
    } catch (Throwable t) {
        t.printStackTrace();
    }
%>
