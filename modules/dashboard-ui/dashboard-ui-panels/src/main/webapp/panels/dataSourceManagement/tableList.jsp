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
<%@ page import="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib prefix="factory" uri="factory.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.dataSourceManagement.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<mvc:formatter name="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementTableListFormatter">

<mvc:fragment name="outputTitleForm">
    <div style="position:absolute; top:100px; left:300px; z-index:4000; padding:0px; height:100%; width:400px;" id="userSelectionPopup" class="popup">
    <form action="<factory:formUrl/>" method="POST" id="<panel:encode name="dsTableListForm" />" style="background-color:#ffffff;" name="<panel:encode name="dsTableListForm" />">
        <factory:handler action="saveChanges" bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler"/>
            <table align="center" width="100%" cellspacing="2" cellpadding="0" border="0" style="border:1px solid #888888; border-bottom:2px solid #888888; border-right:2px solid #888888;">
<mvc:fragmentValue name="dsName" id="dsName">
                <tr style="display:table-row;">
                    <td colspan="2" align="center">
                        <table class="skn-table_border" cellpadding="4" cellspacing="0" border="0" style="width:100%; font-weight:bold;">
                            <tr>
                                <td style="width:16px; white-space:nowrap; padding-right:0px; margin-right:0px;">
                                    <a style="cursor:pointer;" onclick="
                                        setSelectedTables();
                                        //document.getElementById('<panel:encode name="dsTableListForm" />').submit();
                                        submitAjaxForm(document.getElementById('<panel:encode name="dsTableListForm" />'));
                                        return false;">
                                        <img style="border:none;" src="<static:image relativePath="general/16x16/save.gif"/>" alt="<i18n:message key="datasource.saveChanges">!!!GUARDAR</i18n:message>" title="<i18n:message key="datasource.saveChanges">!!!GUARDAR</i18n:message>">
                                    </a>
                                </td>
                                <td style="width:16px; white-space:nowrap; padding-left:5px; margin-left:0px;">
                                    <a id="<panel:encode name="dsTableListcancel"/>" href="<factory:url bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="cancelEdit"/>">
                                        <img style="border:none;" src="<static:image relativePath="general/16x16/cancel.gif"/>" alt="<i18n:message key="datasource.cancel">!!!CANCELAR</i18n:message>" title="<i18n:message key="datasource.cancel">!!!CANCELAR</i18n:message>">
                                    </a>
                                </td>
                                <td style="font-weight:bold; text-align:left; padding-left:10px; white-space:nowrap;">
                                    <i18n:message key="datasource.introspectar.title">!!introspect</i18n:message>&nbsp;<%=dsName%>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
</mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="outputTitleTableTD">
                <tr>
                    <td style="width:100%; margin:0px; padding:0px;" colspan="2">
                        <div style="width:100%; height:445px; overflow:auto; overflow-y:scroll; overflow-x:hidden; margin:0px; padding:0px;">
                        <table width="100%" cellpadding="4" cellspacing="0" border="0">
</mvc:fragment>

<mvc:fragment name="outputRow">
	<mvc:fragmentValue name="tableIndex" id="tableIndex">
	<mvc:fragmentValue name="tableName" id="tableName">
	<mvc:fragmentValue name="trClass" id="trClass">
	<mvc:fragmentValue name="currentTrClass" id="currentTrClass">
	<mvc:fragmentValue name="checked" id="checked">
		<tr id="row_<%=tableIndex%>" class="<%=currentTrClass%>">
			<td style="text-align:center; width:40px;"><input type="checkbox" name="<%=tableName%>" id="checkTable_<%=tableIndex%>" onclick="selectRow(this,'<%=trClass%>');" <%=checked%>></td>
            <td style="text-align:left; padding-left:10px;"><%=tableName%></td>
		</tr>
	</mvc:fragmentValue>
	</mvc:fragmentValue>
	</mvc:fragmentValue>
	</mvc:fragmentValue>
	</mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputBeginButtonsTR">
    <input type="hidden" name="selectedTables"/>
 </mvc:fragment>
<mvc:fragment name="outputSaveButton">
</mvc:fragment>
<mvc:fragment name="outputCancelButton">
</mvc:fragment>
<mvc:fragment name="outputEndButtonsTR">
</mvc:fragment>

<mvc:fragment name="outputEndRow">
            </table>
        </div>
    </td>
  </tr>
</mvc:fragment>

<mvc:fragment name="outputDSError">
	<mvc:fragmentValue name="errorDescription" id="errorDescription">
		<tr><td class="skn-error" colspan="2" style="text-align:left; padding-left:10px;"><i18n:message key="datasource.introspectar.error">!!introspectError</i18n:message><br><%=errorDescription%></td></tr>
	</mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputEnd">
</table></form>
</div>
<script defer="true">
    setAjax('<panel:encode name="dsTableListForm" />');
    setAjax('<panel:encode name="dsTableListcancel"/>');
    function setSelectedTables(){

        var txtSelected = document.getElementById('<panel:encode name="dsTableListForm" />').selectedTables;
        var chkSelected;
        var str="";
        <mvc:fragmentValue name="numberOfTables" id="numberOfTables">
            for(i=0;i<=(<%=numberOfTables%>-1);i++){
                chkSelected = document.getElementById("checkTable_"+i);
                if(chkSelected.checked)
                    str = str + chkSelected.name + ",";
            }
        </mvc:fragmentValue>
        if(str != "")
            str = str.substr(0,str.length -1);
        txtSelected.value = str;
    }

    function selectRow(oCheck,rowClass) {
        rowId=oCheck.id.split("_")[1];
        if(oCheck.checked)
            document.getElementById("row_"+rowId).className="skn-row_on";
        else{
            document.getElementById("row_"+rowId).className=rowClass;
        }
    }
</script>
</mvc:fragment>
</mvc:formatter>
