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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<factory:property property="treeFormatter" id="formatter">
<mvc:formatter name="<%=formatter%>">
    <mvc:fragment name="outputDirectory">
        <table cellspacing="0" cellpadding="0"><tr>
            <mvc:fragmentValue name="path" id="path">
                <mvc:fragmentValue name="hasChildrenDirectories" id="hasChildrenDirectories">
                    <mvc:fragmentValue name="isOpen" id="isOpen">
                        <mvc:fragmentValue name="isCurrent" id="isCurrent">
                <td style="width:32px; white-space:nowrap;">
                    <% if (Boolean.TRUE.equals(hasChildrenDirectories) ) {
                        if (Boolean.TRUE.equals(isOpen) ) { %>
                    <a href="<factory:url action="close"><factory:param name="path" value="<%=path%>"/></factory:url>"><!--
                 --><img border="0" src="<static:image relativePath="fileSystem/16x16/minus.gif"/>" alt="[-]"><!--
                 --></a><a href="<factory:url action="close"><factory:param name="path" value="<%=path%>"/></factory:url>"><!--
                 --><img border="0" src="<static:image relativePath="fileSystem/16x16/folder_open.gif"/>" alt="open"><!--
                 --></a>
                      <%  } else{ %>
                    <a href="<factory:url action="open"><factory:param name="path" value="<%=path%>"/></factory:url>"><!--
                 --><img border="0" src="<static:image relativePath="fileSystem/16x16/plus.gif"/>" alt="[-]"><!--
                 --></a><a href="<factory:url action="open"><factory:param name="path" value="<%=path%>"/></factory:url>"><!--
                 --><img border="0" src="<static:image relativePath="fileSystem/16x16/folder_closed.gif"/>" alt="closed"><!--
                 --></a>
                      <% }} else {%>
                    <img border="0" src="<static:image relativePath="general/spacer.png"/>" width="16px" height="16" alt=""><!--
                 --><a href="<factory:url action="select"><factory:param name="path" value="<%=path%>"/></factory:url>"><!--
                 --><img border="0" src="<static:image relativePath="fileSystem/16x16/folder_closed.gif"/>" alt="closed"><!--
                 --></a>
                      <%}%>
                </td>
                <td style="white-space:nowrap; margin-left:12px; padding-left:4px;" >
                    <span <%if(Boolean.TRUE.equals(isCurrent)){%>class="skn-background"<%}%>>
                    <a href="<factory:url action="select"><factory:param name="path" value="<%=path%>"/></factory:url>"><mvc:fragmentValue name="dirName"/></a>
                    </span>
                </td>
                        </mvc:fragmentValue>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </tr></table>
    </mvc:fragment>
    <mvc:fragment name="beforeChildren">
        <table cellspacing="0" cellpadding="0"><tr><td style="width:16px"><img border="0" src="<static:image relativePath="general/spacer.png"/>" width="10px" height="16" alt=""></td><td>
    </mvc:fragment>
    <mvc:fragment name="afterChildren">
        </td></tr></table>
    </mvc:fragment>
</mvc:formatter>
</factory:property>
