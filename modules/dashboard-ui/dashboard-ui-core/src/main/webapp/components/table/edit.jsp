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
<!-- %@ taglib uri="resources.tld" prefix="resource" % -->
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ page import="org.jboss.dashboard.ui.components.table.TableHandler" %>
<%@ page import="org.jboss.dashboard.displayer.table.Table" %>
<%@ page import="org.jboss.dashboard.displayer.table.TableModel" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.components.table.messages" locale="<%= LocaleManager.currentLocale() %>" />
<%
    TableHandler tableHandler = (TableHandler) request.getAttribute("tableHandler");
    Table table = tableHandler.getTable();
    TableModel model = table.getModel();
%>
<mvc:formatter name="<%= tableHandler.getTableFormatter() %>">
<mvc:formatterParam name="tableHandler" value="<%= tableHandler %>" />

<mvc:fragment name="notable">
    <div class="skn-error">Error: No table specified.</div>
</mvc:fragment>

<mvc:fragment name="tablestart">
    <table align="left" width="100%" cellspacing="2">
    <tr>
</mvc:fragment>

<mvc:fragment name="tablestarthead">
    <td align="left" valign="top">
    <table cellspacing="2" width="250px">
    <tr>
        <td align="left">
            <i18n:message key="table.maxRowsPerPage">!!Max. rows x page</i18n:message>:
        </td>
        <td>
            <input class="skn-input" type="text" name="maxrowspage" size="14" value="<mvc:fragmentValue name="maxrowspage"/>"
                   onchange="return bam_kpiedit_submitProperties(this);">
        </td>
    </tr>
    <tr>
        <td align="left">
            <i18n:message key="table.headerPosition">!!Header pos</i18n:message>:
        </td>
        <td>
            <mvc:fragmentValue name="headerposition" id="header">
                <%  String headerposition = (String) header; %>
                <select name="headerposition" style="width:95px" class="skn-input" onchange="return bam_kpiedit_submitProperties(this);">
                    <option value="hidden" <%= headerposition.equals("hidden") ? "selected" : "" %>><i18n:message key="table.headerHidden">!!!Hidden</i18n:message></option>
                    <option value="top" <%= headerposition.equals("top") ? "selected" : "" %>><i18n:message key="table.headerTop">!!!Top</i18n:message></option>
                    <option value="bottom" <%= headerposition.equals("bottom") ? "selected" : "" %>><i18n:message key="table.headerBottom">!!!Bottom</i18n:message></option>
                </select>
            </mvc:fragmentValue>
        </td>
    </tr>
</mvc:fragment>

<mvc:fragment name="tablestartcontents">
    <%-- Addressed to add extensions to the edit form --%>
</mvc:fragment>

<mvc:fragment name="tablestartcompleted">

    </table>
    </td>
</mvc:fragment>

<mvc:fragment name="tableoutput">
    <td valign="top" >
    <table style="<%=table.getHtmlStyle()%>" class="<%=table.getHtmlClass()%>">
</mvc:fragment>

<mvc:fragment name="tableempty">
    <tr>
        <td align="left" colspan="100"><i18n:message key="table.empty">!!Sin datos</i18n:message></td>
    </tr>
</mvc:fragment>

<mvc:fragment name="headerstart">
    <tr >
</mvc:fragment>

<mvc:fragment name="headerstartWithoutStyle">
    <tr >
</mvc:fragment>

<mvc:fragment name="headercolumn">
    <mvc:fragmentValue name="columnsortable" id="sorteable">
        <mvc:fragmentValue name="iconId" id="iconId">
            <mvc:fragmentValue name="iconTextId" id="iconTextId">
                <mvc:fragmentValue name="columnname" id="columnname">
                    <mvc:fragmentValue name="columnindex" id="columnindex">
                        <td  nowrap>
                            <table cellpadding="0" cellspacing="0" border="0" align="center" width="100%">
                                <tr class="skn-table_header">
                                    <% if (((Integer) columnindex).intValue() > 0 && tableHandler.isStructuralChangesAllowed()) { %>
                                    <td width="10px">
                                        <a href="#" id="<factory:encode name='<%="table_moveLeft_" + columnindex%>'/>"
                                           onclick="window.<factory:encode name="moveColumn"/>('<%= columnindex %>', '-1'); return false;">
                                            <img src="<static:image relativePath="general/10x10/left.gif"/>" title="<i18n:message key="table.moveColumn">!!!Mover columna</i18n:message>" style="border: 0px;">
                                        </a>
                                    </td>
                                    <% } %>
                                    <td align="right">
                                        <% if (((Boolean) sorteable).booleanValue()) { %>
                                        <a href="#"
                                           onclick="window.<factory:encode name="orderCellValue"/>('<mvc:fragmentValue name="columnindex"/>'); return false;"
                                           style="border:0;"
                                           title="<i18n:message key="<%=(String)iconTextId%>"/>">
                                            <img src="<static:image relativePath='<%="general/16x16/"+iconId%>'/>"  border="0" >
                                        </a>
                                        <% } %>
                                    </td>
                                    <td title="<mvc:fragmentValue name="columnhint"/>">
                                        <div  title="<mvc:fragmentValue name="columnhint"/>">
                                            <a href="#" onclick="window.<factory:encode name="selectColumn"/>('<mvc:fragmentValue name="columnindex"/>'); return false;">
                                                <%=columnname%></a>
                                        </div>
                                    </td>
                                    <% if (tableHandler.isStructuralChangesAllowed()) { %>
                                    <td width="10px">
                                        <a href="#" id="<factory:encode name='<%="table_createColumn_" + columnindex%>'/>"
                                           onclick="window.<factory:encode name="newColumn"/>('<%=columnindex%>', '1'); return false;">
                                            <img src="<static:image relativePath="general/10x10/add.gif"/>" title="<i18n:message key="table.createColumn">!!!Crear columna</i18n:message>" style="border: 0px;">
                                        </a>
                                    </td>
                                    <td width="10px">
                                        <a href="#" id="<factory:encode name='<%="table_dropColumn_" + columnindex%>'/>"
                                           onclick="window.<factory:encode name="removeColumn"/>('<%=columnindex%>'); return false;">
                                            <img src="<static:image relativePath="general/10x10/delete.gif"/>" title="<i18n:message key="table.removeColumn">!!!Eliminar esta columna</i18n:message>" style="border: 0px;">
                                        </a>
                                    </td>
                                    <% } %>
                                    <% if (((Integer) columnindex).intValue() < table.getColumnCount()-1 && tableHandler.isStructuralChangesAllowed()) { %>
                                    <td width="10px">
                                        <a href="#" id="<factory:encode name='<%="table_moveRight_" + columnindex%>'/>"
                                           onclick="window.<factory:encode name="moveColumn"/>('<%=columnindex%>', '1'); return false;">
                                            <img src="<static:image relativePath="general/10x10/right.gif"/>" title="<i18n:message key="table.moveColumn">!!!Mover columna</i18n:message>" style="border: 0px;">
                                        </a>
                                    </td>
                                    <% } %>
                                </tr>
                            </table>
                        </td>
                    </mvc:fragmentValue>
                </mvc:fragmentValue>
            </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="headerselected">
    <td bgcolor="#FFAACC" style="<mvc:fragmentValue name="columnheaderhtmlstyle"/>" title="<mvc:fragmentValue name="columnhint"/>">
        <table border="0" cellpadding="0" cellspacing="0" width="100%" ><tr class="skn-table_header" ><td>
            <div title="<mvc:fragmentValue name="columnhint"/>">
                <a href="#" onclick="window.<factory:encode name="deselectColumn"/>(); return false;"><mvc:fragmentValue name="columnname"/></a></div></td></tr></table>

        <div align="center" style="width:400px;height:600px;overflow:-moz-scrollbars-horizontal;overflow-x:hidden;overflow-y:auto; display:block;position:absolute;vertical-align:middle;z-index:11;margin:auto;top:200px;left:250px;">
            <table width="100%" align="left" border="0" bgcolor="#FFFFFF" cellpadding="0" cellspacing="0" class="skn-table_border">
                <tr>
                    <td>
                        <table width="100%" align="left" border="0">
                            <tr class="skn-table_header">
                                <mvc:fragmentValue name="columnname" id="columnname">
                                    <td colspan="2" align="center">
                                        <i18n:message key="table.editProperty" args="<%=new Object[] {columnname}%>">!!! Editar propiedad <%=columnname%></i18n:message>
                                    </td>
                                </mvc:fragmentValue>
                            </tr>
                            <% if (tableHandler.isStructuralChangesAllowed()) { %>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnDataProperty">!!!Propiedad datos</i18n:message>
                                </td>
                                <td align="left">
                                    <mvc:fragmentValue name="columnmodel" id="columnmodel">
                                        <select class="skn-input" name="columnmodel" style="width:240px">
                                            <%
                                                for (int i=0; i < model.getColumnCount(); i++) {
                                                    String columnName = model.getColumnName(i);
                                                    String colmnValue = model.getColumnId(i);
                                            %>
                                            <option value="<%= colmnValue %>" <%= colmnValue.equals(columnmodel) ? "selected" : "" %>><%= columnName %></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </mvc:fragmentValue>
                                </td>
                            </tr>
                            <% } %>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnName">!!!Nombre</i18n:message>
                                </td>
                                <td align="left">
                                    <input class="skn-input" type="text" name="columnname" value="<mvc:fragmentValue name="columnname"/>" size="32">
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnHint">!!!Titulo</i18n:message>
                                </td>
                                <td align="left">
                                    <input class="skn-input" type="text" name="columnhint" value="<mvc:fragmentValue name="columnhint"/>" size="32">
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnSelectable">!!!Es seleccionable</i18n:message>
                                </td>
                                <td align="left">
                                    <mvc:fragmentValue name="columnselectable" id="columnselectable">
                                        <%  Boolean selectable = (Boolean) columnselectable; %>
                                        <input class="skn-input" type="checkbox" name="columnselectable" value="true" <%=  selectable.booleanValue() ? "checked" : "" %>>
                                    </mvc:fragmentValue>
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnSortable">!!!Es ordenable</i18n:message>
                                </td>
                                <td align="left">
                                    <mvc:fragmentValue name="columnsortable" id="columnsortable">
                                        <%  Boolean sortable = (Boolean) columnsortable; %>
                                        <input type="checkbox" name="columnsortable" value="true" <%= sortable.booleanValue() ? "checked" : "" %>>
                                    </mvc:fragmentValue>
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnHeaderHTML">!!!Header HTML</i18n:message>
                                </td>
                                <td align="left">
                                    <input class="skn-input" type="text" name="columnheaderhtmlstyle" value="<mvc:fragmentValue name="columnheaderstyleedit"/>" size="32">
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnCellsHTML">!!!Cells HTML</i18n:message>
                                </td>
                                <td align="left">
                                    <input class="skn-input" type="text" name="columncellhtmlstyle" value="<mvc:fragmentValue name="columncellstyleedit"/>" size="32">
                                </td>
                            </tr>
                            <tr>
                                <td height="15" nowrap="nowrap" align="left" class="skn-even_row">
                                    <i18n:message key="table.columnHTMLValue">!!!HTML Value</i18n:message>
                                </td>
                                <td align="left">
                                    <input class="skn-input" type="text" name="htmlvalue" value="<mvc:fragmentValue name="columnhtmlvalueedit"/>" size="32">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
                                    <input type="button" class="skn-button" value="<i18n:message key="table.columnSave">!!!Guardar</i18n:message>" onclick="window.<factory:encode name="saveColumn"/>('<mvc:fragmentValue name="columnindex"/>'); return false;">
                                    <input type="button" class="skn-button" value="<i18n:message key="table.columnCancel">!!!Cancelar</i18n:message>" onclick="window.<factory:encode name="deselectColumn"/>(); return false;">
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </div>
    </td>
</mvc:fragment>
<mvc:fragment name="headerend">
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
        onmouseover="className='<%=altClass%>'; this.style.cssText='<%=altStyle%>'"
        onmouseout="className='<%=classs%>';this.style.cssText='<%=style%>'">
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="rowcolumn">
    <mvc:fragmentValue name="columnselectable" id="selectable">
        <td style="<mvc:fragmentValue name="columncellhtmlstyle"/>" title="<mvc:fragmentValue name="rowvalue"/>" height="15" nowrap>
            <%
                if (((Boolean) selectable).booleanValue()) {
            %>
            <a href="#" onclick="window.<factory:encode name="selectCellValue"/>('<mvc:fragmentValue name="rowindex"/>', '<mvc:fragmentValue name="columnindex"/>'); return false;">
                <div style="<mvc:fragmentValue name="columncellhtmlstyle"/> height:18px; overflow:hidden; vertical-align:middle" title="<mvc:fragmentValue name="rowvalue"/>">
                    <mvc:fragmentValue name="columnhtmlvalue"/></div></a>
            <%
            } else {
            %>
            <div style="<mvc:fragmentValue name="columncellhtmlstyle"/> height:18px; overflow:hidden; vertical-align:middle" title="<mvc:fragmentValue name="rowvalue"/>">
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
    </table>
    <input type="hidden" id="<factory:encode name="tableaction" />" name="tableaction" value="saveTable">
    <input type="hidden" name="rowindex" value="">
    <input type="hidden" name="columnindex" value="">
    <input type="hidden" name="position" value="">
    <script defer="true">
        function <factory:encode name="getTableForm"/>() {
            return document.getElementById('<factory:encode name="tableaction" />').form;
        }

        window.<factory:encode name="selectColumn"/> = function(index) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'selectColumn';
            form.columnindex.value = index;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="deselectColumn"/> = function() {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'deselectColumn';
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="newColumn"/> = function(index, position) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'newColumn';
            form.columnindex.value = index;
            form.position.value = position;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="moveColumn"/> = function(index, position) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'moveColumn';
            form.columnindex.value = index;
            form.position.value = position;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="removeColumn"/> = function(index) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'removeColumn';
            form.columnindex.value = index;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="saveColumn"/> = function(index) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'saveColumn';
            form.columnindex.value = index;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="selectCellValue"/> = function(row, column) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'selectCellValue';
            form.columnindex.value = column;
            form.rowindex.value = row;
            return bam_kpiedit_submitProperties(form);
        }

        window.<factory:encode name="orderCellValue"/> = function(column) {
            form = <factory:encode name="getTableForm"/>();
            form.tableaction.value = 'sortByColumn';
            form.columnindex.value = column;
            return bam_kpiedit_submitProperties(form);
        }
    </script>
    </td>
    </tr>
    <tr>
        <td></td>
        <td  align="center">
            <mvc:include page="cursor.jsp" flush="true" />
        </td>
    </tr>
    </table>
</mvc:fragment>
<mvc:fragment name="outputtableend">
</mvc:fragment>
</mvc:formatter>
