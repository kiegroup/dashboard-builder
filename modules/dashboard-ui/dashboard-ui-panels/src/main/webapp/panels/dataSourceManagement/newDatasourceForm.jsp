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
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.dataSourceManagement.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<%try {%>
<mvc:formatter name="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementFormFormatter">

<mvc:fragment name="outputStartEditing">
    <mvc:fragmentValue name="dsName" id="dsName">
            <table align="center" width="800px" cellspacing="1" cellpadding="4" border="0">
                <tr style="display:table-row; width:12px;">
                    <td class="skn-table_border" colspan="2">
                        <div style="vertical-align:middle; text-align:left;"><span style="font-weight:bold;"><i18n:message key="datasource.edit.title">!!EDITAR</i18n:message></span>&nbsp;<%=dsName%></div>
                    </td>
                </tr>
            </table>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputStart">
    <form action="<factory:formUrl/>" name='<panel:encode name="formDS"/>' id="<panel:encode name="formDS"/>" style="margin:0px; padding:0px;">
            <factory:handler bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="CreateDatasource"/>
    <table class="skn-table_border" align="center" width="800px" cellspacing="1" cellpadding="4" border="0">
</mvc:fragment>

<mvc:fragment name="outputRadios">
    <mvc:fragmentValue name="error" id="error">
    <mvc:fragmentValue name="type" id="type">
        <mvc:fragmentValue name="display" id="display">
                <tr>
                     <td class="skn-even_row" width="150px">
                        <span class="<%=((Boolean)error).booleanValue() ?"skn-error":""%>">
                            <i18n:message key="datasource.type">!!!Tipo</i18n:message>
                        </span>
                    </td>
                    <td  nowrap="nowrap" align="left">
                        <input type="radio"
                               onclick="if(this.checked){
                                            document.getElementById('<panel:encode name="nameTR"/>').style.display = '';
                                            document.getElementById('<panel:encode name="jndiTR"/>').style.display = '';
                                            document.getElementById('<panel:encode name="testQTR"/>').style.display = '';
                                            document.getElementById('<panel:encode name="testQuery"/>').value = '';
                                            document.getElementById('<panel:encode name="outputUrlTR"/>').style.display = 'none';
                                            document.getElementById('<panel:encode name="outputDBTypeTR"/>').style.display = 'none';
                                            document.getElementById('<panel:encode name="outputUserNameTR"/>').style.display = 'none';
                                            document.getElementById('<panel:encode name="outputUserPwdTR"/>').style.display = 'none';
                                        }"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="type"/>"
                               value="<%=DataSourceManagementHandler.JNDI_TYPE%>"
                               <%= (type!=null && DataSourceManagementHandler.JNDI_TYPE.equals(type)) ? "checked" : ""%>>
                        <i18n:message key="datasource.data.jndi">!!JNDI</i18n:message>
                        &nbsp;
                        <input type="radio" onclick="if(this.checked){
                                                         document.getElementById('<panel:encode name="nameTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="outputUrlTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="outputDBTypeTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="outputUserNameTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="outputUserPwdTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="testQTR"/>').style.display = '';
                                                         document.getElementById('<panel:encode name="testQuery"/>').value = testQuerys[0];
                                                         document.getElementById('<panel:encode name="jndiTR"/>').style.display = 'none';
                                                     }"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="type"/>"
                               value="<%=DataSourceManagementHandler.CUSTOM_TYPE%>"
                               <%= (type!=null && DataSourceManagementHandler.CUSTOM_TYPE.equals(type)) ? "checked" : ""%>>
                        <i18n:message key="datasource.data.custom">!!A medida</i18n:message>
                    </td>
              </tr>
    </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragmentValue>
</mvc:fragment>


<mvc:fragment name="outputName">
    <mvc:fragmentValue name="errorName" id="errorName">
                <tr id="<panel:encode name="nameTR"/>" style="display:none;">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorName).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.name">* !!!Name</i18n:message>
                        </span>
                    </td>
                    <td align="left">
                        <input class="skn-input"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="name"/>"
                               value="<mvc:fragmentValue name="Name"/>">
                    </td>
                </tr>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputJNDI">
    <mvc:fragmentValue name="errorJndiPath" id="errorJndiPath">
                <tr id="<panel:encode name="jndiTR"/>" style="display:none">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorJndiPath).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.path">* !!!ruta</i18n:message>
                        </span>
                    </td>
                    <td align="left">
                        <input class="skn-input"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="jndiPath" />"
                               value="<mvc:fragmentValue name="JndiPath"/>"
                               size="52">
                    </td>
                </tr>
    </mvc:fragmentValue>
</mvc:fragment>


<mvc:fragment name="outputLocal">
    <mvc:fragmentValue name="errorUrl" id="errorUrl">
                <tr id="<panel:encode name="outputUrlTR"/>" style="display:none">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorUrl).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.url">* !!!url</i18n:message>
                        </span>
                    </td>
                    <td align="left">
                        <input class="skn-input" size="52"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="url"/>"
                               value="<mvc:fragmentValue name="Url"/>">
                    </td>
                </tr>
    </mvc:fragmentValue>

		<mvc:fragmentValue name="errorDriverClass" id="errorDriverClass">
                <tr id="<panel:encode name="outputDBTypeTR"/>" style="display:none">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorDriverClass).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.database">* !!!database type</i18n:message>
                        </span>
                    </td>
										<td>
											<table>
												<tr>
													<td  align="left">
														<select class="skn-input"
																		name="db" onchange="changeTestQuery(this, '<panel:encode name="testQuery"/>'); changeDriverClass(this, '<panel:encode name="driverClassName"/>');">
														<option value="" <mvc:fragmentValue name="selectedNone"/>></option>
														<option value="com.mysql.jdbc.Driver" <mvc:fragmentValue name="selectedMySQL"/>>
															MySQL</option>
														<option value="org.postgresql.Driver"
															<mvc:fragmentValue name="selectedPostgres"/>>PostgreSQL</option>
														<option value="oracle.jdbc.driver.OracleDriver"
															<mvc:fragmentValue name="selectedOracle"/>>Oracle</option>
														<option value="com.microsoft.sqlserver.jdbc.SQLServerDriver"
															<mvc:fragmentValue name="selectedSQLServer"/>>SQLServer</option>
														<option value="org.h2.Driver"
															<mvc:fragmentValue name="selectedH2"/>>H2</option>
														<option value="org.teiid.jdbc.TeiidDriver"
															<mvc:fragmentValue name="selectedTeiid"/>>Teiid</option>                                                            
														</select>
													</td>&nbsp;&nbsp;
													<td align="left">
														&nbsp;&nbsp;<i18n:message key="datasource.data.database">* !!!database type</i18n:message>
														<input class="skn-input" id="<panel:encode name="driverClassName"/>"
																	 name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="driverClass" />"
																	 value="<mvc:fragmentValue name="DriverClassName"/>">
													</td>
												</tr>
											</table>
										</td>
                </tr>
		</mvc:fragmentValue>

    <mvc:fragmentValue name="errorUserName" id="errorUserName">
                <tr id="<panel:encode name="outputUserNameTR"/>" style="display:none">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorUserName).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.user">!!!User</i18n:message>
                        </span>

                    </td>
                    <td align="left">
                        <input class="skn-input"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="userName"/>"
                               value="<mvc:fragmentValue name="UserName"/>">
                    </td>
                </tr>
    </mvc:fragmentValue>

    <mvc:fragmentValue name="errorPassw" id="errorPassw">
                <tr id="<panel:encode name="outputUserPwdTR"/>" style="display:none">
                    <td width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorPassw).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.passw">*!!! Password</i18n:message>
                        </span>
                    </td>
                    <td align="left">
                        <input type="password"
                               class="skn-input"
                               name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="password"/>"
                               value="<mvc:fragmentValue name="Passw"/>">
                    </td>
                </tr>
    </mvc:fragmentValue>
</mvc:fragment>
<mvc:fragment name="ouputTestQ">
    <mvc:fragmentValue name="errorTestQ" id="errorTestQ">
                <tr id="<panel:encode name="testQTR"/>" style="display:none">
                    <td valign="top" width="150px" align="right" class="skn-even_row">
                        <span class="<%=((Boolean)errorTestQ).booleanValue()?"skn-error":""%>">
                            <i18n:message key="datasource.data.testQ">*!!! testQuery</i18n:message>
                        </span>
                    </td>
                    <td align="left">
                        <textarea class="skn-input" cols="52" rows="8"
                                  id="<panel:encode name="testQuery"/>"
                                  name="<factory:bean bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" property="testQuery"/>"
                                ><mvc:fragmentValue name="TestQ"/></textarea>
                    </td>
                </tr>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputResult">
    <mvc:fragmentValue name="testResult" id="testResult">
                <tr>
                    <td colspan="2" style="margin:10px; text-align:left; width:100%">
                        <div class="skn-background_alt" style="width:100%;">
<%
    String result = (String)testResult;
    if(DataSourceManagementHandler.RESULT_OK.equals(result)){
%>
                        <table border="0" cellspacing="3" cellpadding="4" style="width:100%; text-align:left;">
                            <tr align="left">
                                <td style="vertical-align:top; width:20px;">
                                    <img src="<static:image relativePath="general/16x16/ico-menu_permissions_g.png"/>" border="0" />
                                </td>
                                <td>
                                    <i18n:message key='datasource.test.ok'>!!!OK</i18n:message>
                                </td>
                            </tr>
                        </table>
<%
    } else if(!"".equals(result)) {
%>
                        <table border="0" cellspacing="3" cellpadding="4" style="width:100%; text-align:left;">
                            <tr align="left">
                                <td style="vertical-align:top; width:20px;">
                                    <img src="<static:image relativePath="general/16x16/ico-menu_permissions_r.png"/>" border="0" />
                                </td>
                                <td>
                                    <span class="skn-error">
                                        <i18n:message key='datasource.test.wrong'>!!!Bad</i18n:message><br><%=testResult%>
                                    </span>
                                </td>
                            </tr>
                        </table>
<%
    }
%>                  </div>
                    </td>
                </tr>
    </mvc:fragmentValue>
</mvc:fragment>

<mvc:fragment name="outputRowButtonsBegin">
	<tr>
		<td align="center" colspan="2">

</mvc:fragment>

	<mvc:fragment name="outputSubmitButton">
	                        <input class="skn-button" type="button"
	                               value='<i18n:message key="datasource.saveChanges"/>'
	                               onclick="submitAjaxForm(document.getElementById('<panel:encode name="formDS"/>'))">
	</mvc:fragment>

	<mvc:fragment name="outputClear">
	                        <input class="skn-button" type="reset" value='<i18n:message key="datasource.clearfields"/>'>
	</mvc:fragment>
	<mvc:fragment name="outputCancel">
	                        <input class="skn-button_alt" type="button" id='<panel:encode name="DSCancelButton"/>' value='<i18n:message key="datasource.cancel"/>'
	                               onclick="submitAjaxForm(document.getElementById('<panel:encode name="formCancelEdit"/>'))" >

	</mvc:fragment>

    <mvc:fragment name="outputTryButton">
        <input type="button" value='<i18n:message key="datasource.testQButton"/>' class="skn-button" id='<panel:encode name="DSTestButton"/>'
                onclick="
                document.getElementById('<panel:encode name="isCheckingDS"/>').value='true';
                submitAjaxForm(document.getElementById('<panel:encode name="formDS"/>'))">

        <input type="hidden" name="checkingDS" id="<panel:encode name="isCheckingDS"/>" value="false">
    </mvc:fragment>

    <%--<mvc:fragment name="outputIntrospectButton">--%>
				<%--<input class="skn-button" type="button"--%>
							 <%--value='<i18n:message key="datasource.introspectar"/>'--%>
							 <%--id="<panel:encode name="DSIntrospectButton"/>"--%>
							 <%--onclick="submitAjaxForm(document.getElementById('<panel:encode name="formInstrospect"/>'))">--%>
				<%--<script language="javascript">--%>
					<%--function hideElements(ids){--%>
						<%--var arrIds = ids.split(",")--%>
						<%--for(var i=0; i<arrIds.length; i++){--%>
							<%--if(document.getElementById(arrIds[i]))--%>
								<%--document.getElementById(arrIds[i]).style.display='none';--%>
						<%--}--%>
					<%--}--%>
				<%--</script>--%>
		<%--</mvc:fragment>--%>

<mvc:fragment name="outputRowButtonsEnd">
		</td>
	</tr>

</mvc:fragment>

<mvc:fragment name="outputEnd">

            </table>

        </form>
        <script defer>
            setAjax('<panel:encode name="formDS"/>');
        </script>
        <form action="<factory:formUrl/>" name='<panel:encode name="formCancelEdit"/>' id="<panel:encode name="formCancelEdit"/>" style="margin:0px; padding:0px;">
            <factory:handler bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="cancelEdit"/>
        </form>
        <script defer>
            setAjax('<panel:encode name="formCancelEdit"/>');
        </script>
        <form action="<factory:formUrl/>" name='<panel:encode name="formInstrospect"/>' id="<panel:encode name="formInstrospect"/>" style="margin:0px; padding:0px;">
            <factory:handler bean="org.jboss.dashboard.ui.panel.dataSourceManagement.DataSourceManagementHandler" action="introspect"/>
        </form>
        
        <div id="<panel:encode name="formDSIntrospect"/>">
            <jsp:include page="tableList.jsp" flush="true" />
        </div>


    <mvc:fragmentValue name="typeSelect" id="typeSelect">
        <script type="text/javascript" defer>

    window.f = function() {
<%
    String type = (String)typeSelect;
    if (type != null || !"".equals(type)) {
        if(type.equals(DataSourceManagementHandler.JNDI_TYPE)) {
%>
            document.getElementById('<panel:encode name="nameTR"/>').style.display = '';
            document.getElementById('<panel:encode name="jndiTR"/>').style.display = '';
            document.getElementById('<panel:encode name="testQTR"/>').style.display = '';
            document.getElementById('<panel:encode name="outputUrlTR"/>').style.display = 'none';
            document.getElementById('<panel:encode name="outputDBTypeTR"/>').style.display = 'none';
            document.getElementById('<panel:encode name="outputUserNameTR"/>').style.display = 'none';
            document.getElementById('<panel:encode name="outputUserPwdTR"/>').style.display = 'none';
<%
        } else if(type.equals(DataSourceManagementHandler.CUSTOM_TYPE)) {
%>
            document.getElementById('<panel:encode name="nameTR"/>').style.display = '';
            document.getElementById('<panel:encode name="outputUrlTR"/>').style.display = '';
            document.getElementById('<panel:encode name="outputDBTypeTR"/>').style.display = '';
            document.getElementById('<panel:encode name="outputUserNameTR"/>').style.display = '';
            document.getElementById('<panel:encode name="outputUserPwdTR"/>').style.display = '';
            document.getElementById('<panel:encode name="jndiTR"/>').style.display = 'none';
            document.getElementById('<panel:encode name="testQTR"/>').style.display = '';

<%
        }
    }
%>
    }
    setTimeout("window.f()",100);
        </script>
    </mvc:fragmentValue>
</mvc:fragment>
</mvc:formatter>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
