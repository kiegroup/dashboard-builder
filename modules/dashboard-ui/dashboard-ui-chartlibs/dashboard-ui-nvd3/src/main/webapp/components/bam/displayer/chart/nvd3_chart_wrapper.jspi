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
<!-- Chart wrapper begin -->
<table class="skn-chart-table" width="100%" >
    <tbody>
    <tr>
        <td height="<%= displayer.getHeight() %>" align="<%=displayer.getGraphicAlign()%>" style="">
<% if( displayer.isShowTitle() && displayer.getTitle() != null) { %>
            <div id="title<%=chartId%>" class="skn-chart-title" style="width:<%= displayer.getWidth() %>px"><%=displayer.getTitle()%></div>
<% } %>
            <div id="tooltip<%=chartId%>" class="skn-chart-tooltip" style="width:<%= displayer.getWidth() %>px"></div>
            <div class="skn-chart-wrapper" style="width:<%= displayer.getWidth() %>px;height:<%= displayer.getHeight() %>px" id="<%= chartId %>">
                <svg></svg>
            </div>
        </td>
    </tr>
    </tbody>
</table>
<!-- Chart wrapper end -->