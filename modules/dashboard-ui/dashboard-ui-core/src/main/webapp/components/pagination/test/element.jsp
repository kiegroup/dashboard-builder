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
<%@ page import="org.jboss.dashboard.ui.components.PaginationComponentFormatter" %>
<%
    Integer index = (Integer) request.getAttribute(PaginationComponentFormatter.PARAM_INDEX);
    String className, altClass;
    if (index!= null && index.intValue() % 2 != 0) {
        className = "skn-even_row";
        altClass = "skn-even_row_alt";
    } else {
        className = "skn-odd_row";
        altClass = "skn-odd_row_alt";
    }
%>
    <tr>
        <td class="<%=className%>"  onmouseover="className='<%=altClass%>'" onmouseout="className='<%=className%>'">
            <%=request.getAttribute(PaginationComponentFormatter.PARAM_ELEMENT)%>
        </td>
    </tr>