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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ page import="org.jboss.dashboard.ui.components.table.TableHandler" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.jboss.dashboard.displayer.table.Table" %>
<%@ page import="org.jboss.dashboard.displayer.table.ExportTool" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    TableHandler tableHandler = (TableHandler) request.getAttribute("tableHandler");
    Table table = tableHandler.getTable();
%>
<mvc:formatter name="<%= tableHandler.getTableFormatter() %>">
    <mvc:formatterParam name="tableHandler" value="<%= tableHandler %>" />

    <mvc:fragment name="tablestart">
        <table>
        <tr>
        <td align="right">
        <table>
            <tr>
                <td>
                    <a href="<factory:url action="exportData" friendly="true"><factory:param name="<%= TableHandler.EXPORT_FORMAT %>" value="<%= ExportTool.FORMAT_EXCEL %>"/></factory:url>">
                        <img title="<i18n:message key="table.exportData.excel">!!!Excel</i18n:message>" src="<static:image relativePath="general/22x22/MSExcel.png"/>" border="0">
                    </a>
                </td>
                <td>
                    <a href="<factory:url action="exportData" friendly="true"><factory:param name="<%= TableHandler.EXPORT_FORMAT %>" value="<%= ExportTool.FORMAT_CSV %>"/></factory:url>">
                        <img hspace="10px" title="<i18n:message key="table.exportData.csv">!!!CSV</i18n:message>" src="<static:image relativePath="general/22x22/CSV.png"/>" border="0">
                    </a>
                </td>
            </tr>
        </table>
        <form action="<factory:formUrl friendly="false"/>" id="<factory:encode name="tableViewForm"/>">
        <factory:handler action="submitViewer"/>
        <table  border="0" style="solid: #000000; padding:0; text-align:center;" class="skn-table_border" cellpadding="2" cellspacing="1">
    </mvc:fragment>

    <mvc:fragment name="headerstart">
        <tr class="skn-table_header">
    </mvc:fragment>
    <mvc:fragment name="headercolumn">
        <mvc:fragmentValue name="columnsortable" id="sorteable">
            <mvc:fragmentValue name="iconId" id="iconId">
                <mvc:fragmentValue name="iconTextId" id="iconTextId">
                    <td nowrap>
                        <table cellpadding="0" cellspacing="0" border="0" align="center" width="100%">
                            <tr >
                                <td align="right">
                                    <% if (((Boolean) sorteable).booleanValue()) { %>
                                    <a href="#" onclick="window.<factory:encode name="orderCellValue"/>('<mvc:fragmentValue name="columnindex"/>'); return false;" style="border:0;"
                                       title="<i18n:message key="<%=(String)iconTextId%>"/>">
                                        <img src="<static:image relativePath='<%="general/16x16/"+iconId%>'/>"  border="0" ></a>
                                    <% } %>
                                </td>
                                <td title="<mvc:fragmentValue name="columnhint"/>" nowrap>
                                    <div  title="<mvc:fragmentValue name="columnhint"/>">
                                        <mvc:fragmentValue name="columnname"/></div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="headerselected">
        <td style="<mvc:fragmentValue name="columnheaderhtmlstyle"/>" title="<mvc:fragmentValue name="columnhint"/>" height="15" nowrap>
            <div  title="<mvc:fragmentValue name="columnhint"/>">
                <mvc:fragmentValue name="columnname"/></div>
        </td>
    </mvc:fragment>
    <mvc:fragment name="headerend">
        </tr>
    </mvc:fragment>

    <mvc:fragment name="tableempty">
        <tr>
            <td align="left" colspan="100"><i18n:message key="table.empty">!!Sin datos</i18n:message></td>
        </tr>
    </mvc:fragment>

    <mvc:fragment name="bodystart">
    </mvc:fragment>

    <mvc:fragment name="rowstart">
        <mvc:fragmentValue name="rowindex" id="rowindex">
            <%
                Integer index = (Integer) rowindex;
                String style = (index.intValue() % 2 == 0) ? table.getRowEvenStyle() : table.getRowOddStyle();
                String classs = (index.intValue() % 2 == 0) ? table.getRowEventClass() : table.getRowOddClass();
                String altClass = table.getRowHoverClass();
                altClass = StringUtils.isBlank(altClass) ? "" : altClass;
                String altStyle = table.getRowHoverStyle();
                altStyle = StringUtils.isBlank(altStyle) ? "" : altStyle;
            %>
            <tr style="<%=style%>" class="<%=classs%>"
            onmouseover="className='<%=altClass%>'; this.style.cssText='<%=altStyle%>'" onmouseout="className='<%=classs%>';this.style.cssText='<%=style%>'"
            >
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="rowcolumn">
        <mvc:fragmentValue name="columnselectable" id="selectable">
            <td style="<mvc:fragmentValue name="columncellhtmlstyle"/>" title="<mvc:fragmentValue name="rowvalue"/>" height="15" nowrap>
                <%
                    if (((Boolean) selectable).booleanValue()) {
                %>
                <a href="#"
                   onclick="window.<factory:encode name="selectCellValue"/>('<mvc:fragmentValue name="rowindex"/>', '<mvc:fragmentValue name="columnindex"/>'); return false;">
                    <div style="<mvc:fragmentValue name="columncellhtmlstyle"/> height:18px; overflow:hidden; vertical-align:middle" title="<mvc:fragmentValue name="rowvalue"/>">
                        <mvc:fragmentValue name="columnhtmlvalue"/></div></a>
                <%
                } else {
                %>
                <div style="<mvc:fragmentValue name="columncellhtmlstyle"/> height:18px; overflow:hidden; vertical-align:middle" title="<mvc:fragmentValue name="rowvalue"/>" align="">
                    <mvc:fragmentValue name="columnhtmlvalue"/></div>
                <%
                    }
                %>
            </td>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="rowend">
        </tr>
    </mvc:fragment>

    <mvc:fragment name="bodyend">
    </mvc:fragment>

    <mvc:fragment name="tableend">
        <input type="hidden" id="<factory:encode name="tableaction" />" name="tableaction" value="firstPage">
        <input type="hidden" name="rowindex" value="">
        <input type="hidden" name="columnindex" value="">
        <tr>
            <td align="center" colspan="100">
                <mvc:include page="cursor.jsp" flush="true" />
            </td>
        </tr>
        </table>
        </form>
        </td>
        </tr>
        </table>
        <script defer="true">
            setAjax('<factory:encode name="tableViewForm"/>');
            function <factory:encode name="getTableForm"/>() {
                return document.getElementById('<factory:encode name="tableViewForm"/>');
            }

            window.<factory:encode name="selectCellValue"/> = function(row, column) {
                form = <factory:encode name="getTableForm"/>();
                form.tableaction.value = 'selectCellValue';
                form.columnindex.value = column;
                form.rowindex.value = row;
                return submitAjaxForm(form);
            }

            window.<factory:encode name="orderCellValue"/> = function(column) {
                form = <factory:encode name="getTableForm"/>();
                form.tableaction.value = 'sortByColumn';
                form.columnindex.value = column;
                return submitAjaxForm(form);
            }
        </script>
    </mvc:fragment>

    <mvc:fragment name="notable">
        Error: No table specified.
    </mvc:fragment>
</mvc:formatter>
