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
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>
<%@ taglib prefix="panel" uri="bui_taglib.tld"%>
<%@ taglib prefix="i18n" uri="http://jakarta.apache.org/taglibs/i18n-1.0"%>

<i18n:bundle baseName="org.jboss.dashboard.ui.panel.navigation.treeMenu.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<form action="<panel:link action="saveEdit"/>" method="POST" id="<panel:encode name="editForm"/>">
    <panel:hidden action="saveEdit"/>

    <mvc:formatter name="org.jboss.dashboard.ui.panel.navigation.treeMenu.TreeMenuFormatter">
        <mvc:formatterParam name="allSectionsOpen" value='<%=request.getAttribute("openAllSections")%>'/>
        <mvc:formatterParam name="editMode" value="true"/>
        <mvc:fragment name="pageStart">
            <mvc:fragmentValue name="checked" id="checked">
                <mvc:fragmentValue name="sectionName" id="sectionName">
                    <table border="0" cellspacing="0" cellpadding="0" style="margin-left: 15px;"><tr><td>
                    <input type="checkbox" value="true" <%=Boolean.TRUE.equals(checked)?"checked":""%> name="show_<mvc:fragmentValue name="pageId"/>">
                    <%=sectionName%></td></tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="beforeTabulation">
            <tr><td>
            <table cellspacing="0" cellpadding="0" width="100%" style="margin-left: 15px;">
        </mvc:fragment>

        <mvc:fragment name="childrenStart">
            <mvc:fragmentValue name="sectionName" id="sectionName">
                <mvc:fragmentValue name="checked" id="checked">
                    <tr><td>
                        <input type="checkbox" value="true" <%=Boolean.TRUE.equals(checked)?"checked":""%> name="show_<mvc:fragmentValue name="pageId"/>">
                        <%=sectionName%></td></tr>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragment>
        <mvc:fragment name="afterTabulation">
            </table>
        </mvc:fragment>
        <mvc:fragment name="childrenEnd">
        </mvc:fragment>
        <mvc:fragment name="pageEnd">
            </table>
        </mvc:fragment>
        <mvc:fragment name="outputEnd">
            <hr>
            <mvc:fragmentValue name="checked" id="checked">
                <div style="margin-left: 15px;" >
                    <input type="checkbox" value="true" <%=Boolean.TRUE.equals(checked)?"checked":""%> name="show_*">
                    <a href="#"><i18n:message key="all">!!!Todos</i18n:message></a>
                </div>
            </mvc:fragmentValue>
            <hr>
        </mvc:fragment>
    </mvc:formatter>
    <div  width="100%" align="middle">
        <input type="submit" value="<i18n:message key="save">!!!Guardar</i18n:message>" class="skn-button" >
    </div>
</form>
<script type="text/javascript">
    var theForm = document.getElementById("<panel:encode name="editForm"/>");
    var aes = theForm.getElementsByTagName("a");
    for(var i=0;i<aes.length;i++ ){
        aes[i].onclick = function(){
            var checkboxes = this.parentNode.getElementsByTagName("input");
            checkboxes[0].checked = !checkboxes[0].checked;
            return false;
        }
    }
</script>
