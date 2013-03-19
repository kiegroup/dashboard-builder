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
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jboss.dashboard.users.RolesManager" %>
<%@ page import="org.jboss.dashboard.SecurityServices" %>
<%@ page import="org.jboss.dashboard.users.Role" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.components.permissions.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<div style="padding-top:20px; padding-bottom:10px; margin:0px; font-weight:bold;">
    <i18n:message key="permissions.title">!!!Asignacion de permisos</i18n:message>:
</div>
<mvc:formatter name="org.jboss.dashboard.ui.components.permissions.PermissionsAssignerFormatter">
    <mvc:fragment name="tableStart">
        <form style="padding:0px; margin:0px;" action="<factory:formUrl/>" method="POST" id="<panel:encode
            name="userSelectionForm"/>">
        <factory:handler action="addNewPermissions"/>
        <table class="skn-table_border" cellspacing="0" cellpadding="0" border="0" style="margin-bottom:20px;"><tr><td>
			<table cellspacing="0" cellpadding="5">
				<tr>
					<td class="skn-table_header" style="border:1px solid #FFFFFF;">
						<i18n:message key="permissions.role">!!!Quien</i18n:message>
					</td>
					<td colspan="2" class="skn-table_header" style="border:1px solid #FFFFFF; border-left:none;">
						<i18n:message key="permissions.actions">!!!Acciones</i18n:message>
					</td>
				</tr>
				<tr>
    </mvc:fragment>
    <mvc:fragment name="rolesSelection">
					<td>
						<table cellspacing="0" cellpadding="0">
							<tr>
								<td colspan="2" style="height:20px; padding-top:5px; vertical-align:top;">
									<i18n:message key="permissions.select.role">!!!Seleccionar rol</i18n:message>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="height:20px; padding-top:5px; vertical-align:top;">
									<select class="skn-input" name="roleName"/>">
									<%
										RolesManager rolesManager = SecurityServices.lookup().getRolesManager();
										for (Iterator<Role> rIt = rolesManager.getAllRoles().iterator(); rIt.hasNext(); ) {
											Role role = rIt.next();
									%>
									<option value="<%= role.getName() %>"><%= role.getDescription(LocaleManager.currentLocale()) %></option>
									<%
										}
									%>
									</select>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="height:20px; padding-top:5px; vertical-align:top;">
									<input type="checkbox" name="invert" class="skn-input"><i18n:message key="permissions.reverse">!!!Invertir</i18n:message>
								</td>
							</tr>
						</table>
					</td>
					<td>
						<table cellspacing="0" cellpadding="0">
    </mvc:fragment>
    <mvc:fragment name="outputAction">
							<tr>
								<td style=" white-space:nowrap;">
									<mvc:fragmentValue name="actionDescription"/>
								</td>
								<td style="padding-left:10px; padding-top:3px; padding-bottom:3px; vertical-align:top;">
									<select class="skn-input" name="action_<mvc:fragmentValue name="actionName"/>">
										<option value="?"></option>
										<option value="false"><i18n:message key="permissions.no"/></option>
										<option value="true"><i18n:message key="permissions.yes"/></option>
									</select>
								</td>
							</tr>
    </mvc:fragment>
    <mvc:fragment name="tableEnd">
						</table>
					</td>
				</tr>
			</table>
			</td></tr>
			<tr>
				<td style="padding-top:10px; text-align:center;"><input type="submit" value="<i18n:message key="permissions.save">!!!Guardar</i18n:message>" class="skn-button">
				</td>
			</tr>
        </table>
        </form>
        <script defer>
            setAjax("<panel:encode name="userSelectionForm"/>");
        </script>
    </mvc:fragment>
</mvc:formatter>


