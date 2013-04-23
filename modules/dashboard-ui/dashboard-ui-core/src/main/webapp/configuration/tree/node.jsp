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
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ page import="org.jboss.dashboard.ui.config.TreeNode"%>
<%
    try{
%>
<mvc:formatter name="org.jboss.dashboard.ui.config.formatters.TreeNodeFormatter">
    <mvc:formatterParam name="treenode" value='<%=request.getAttribute("node")%>'/>
    <mvc:fragment name="nodeTab">
    </mvc:fragment>
    <mvc:fragment name="subNode">
         <mvc:fragmentValue name="id_Node" id="id_Node">
         <mvc:fragmentValue name="node" id="node">
         <mvc:fragmentValue name="level_Node" id="level_Node">
         <mvc:fragmentValue name="path_Node" id="path_Node">
         <mvc:fragmentValue name="icon_Node" id="icon_Node">
         <mvc:fragmentValue name="iconNodePath" id="iconNodePath">
         <mvc:fragmentValue name="name_Node" id="name_Node">
         <mvc:fragmentValue name="parent_Node" id="parent_Node">
         <mvc:fragmentValue name="expand_path" id="expandPath">
         <mvc:fragmentValue name="line_path" id="linePath">
         <mvc:fragmentValue name="branchPath" id="branchPath">
         <mvc:fragmentValue name="expand_action" id="expand_action">
         <mvc:fragmentValue name="isEdited" id="isEdited">
         <mvc:fragmentValue name="isEditable" id="isEditable">
         <mvc:fragmentValue name="nodeIndex" id="nodeIndex">
             <tr>
<%
                TreeNode theSubnode = (TreeNode) node;
                String nodeName = (String) name_Node;
                if ( ((Boolean)isEdited).booleanValue() ) nodeName = "<b>" + nodeName + "</b>";
                int level = ((Integer) level_Node).intValue();
                for (int i=1; i<level; i++){
                    TreeNode ancestorInLevel = theSubnode.getAncestorForLevel(i);
                    boolean paintLine = !ancestorInLevel.isLastChild();
                    if (paintLine) {
%>
                        <td align="right" width="1px" height="18px" style="background:url('<static:image relativePath="general/tree/line_expand.gif"/>')"></td>
<%
                    }
                    else{
%>
                        <td width="1px" height="18px"></td>
<%
                    }
                }
%>
                 <td align="right" width="1px" height="18px" style="background:url('<static:image relativePath='<%= "general/tree/"+linePath%>' />')">
                     <%if(!theSubnode.isLeaf()){%>
                         <a href="<factory:url bean="org.jboss.dashboard.ui.config.TreeActionsHandler" action="expandOrCollapse"><factory:param name="path" value="<%=path_Node%>"/></factory:url>">
                             <img src="<static:image relativePath='<%= "general/tree/"+expandPath %>' />"  border="0">
                         </a>
                     <%}else{%>
                         <img src="<static:image relativePath='<%= "general/tree/"+branchPath %>' />"  border="0">
                     <%}%>
                 </td>



<%
                if (icon_Node != null && !icon_Node.equals("")) {
%>
                <td width="1px" height="18px" align="left">
<%
                    if(((Boolean)isEditable).booleanValue()){
%>
                    <a href="<factory:url bean="org.jboss.dashboard.ui.config.TreeActionsHandler" friendly="false" action="navigateTo"><factory:param name="path" value="<%=path_Node%>"/></factory:url>">
                        <img src="<static:image relativePath='<%= "general/tree/"+icon_Node %>' />" title="<%= (String) name_Node %>" border="0">
                    </a>
<%
                    } else {
%>
                    <img src="<static:image relativePath='<%= "general/tree/"+icon_Node %>' />" title="<%= (String) name_Node %>" border="0">
<%
                    }
%>
                </td>
<%
                }
%>

                 <td colspan="<%=100-((Integer)level_Node).intValue()%>" nowrap="nowrap" align="left" height="18px" width="100%">
<%
                if(((Boolean)isEditable).booleanValue()){
%>
                    <a href="<factory:url bean="org.jboss.dashboard.ui.config.TreeActionsHandler" friendly="false" action="navigateTo"><factory:param name="path" value="<%=path_Node%>"/></factory:url>" id="<panel:encode name='<%="edit_node_name_"+(String)path_Node%>'/>" title="<%= (String) name_Node %>">
                        &nbsp;<%= nodeName %>
                    </a>
                    <script defer>
                        setAjax('<panel:encode name='<%="edit_node_name_"+(String)path_Node%>'/>');
                    </script>
<%
                } else {
%>
                     <div class="skn-disabled">&nbsp;<%= nodeName %></div>
<%
                }
%>
                 </td>
             </tr>
            <% request.setAttribute("node", theSubnode); %>
            <jsp:include page="node.jsp" flush="true"/>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
         </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="error">
    </mvc:fragment>
</mvc:formatter>
<%}catch(Throwable t){t.printStackTrace();}%>
