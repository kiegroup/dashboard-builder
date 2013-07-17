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
<%@ page import="org.jboss.dashboard.ui.components.permissions.PermissionsHandler" %>
<%@ page import="org.jboss.dashboard.security.PermissionDescriptor" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.components.permissions.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<%
	org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PermissionsHandler.class.getName());
	PermissionsHandler permissionsHandler = PermissionsHandler.lookup();

	// Obtain actions for this permission class
	Class permissionClass = permissionsHandler.getPermissionClass();
	List<String> actionList = (List<String>) permissionClass.getField("LIST_OF_ACTIONS").get(permissionClass);

	Method actionNameMethod = null;
	try {
		actionNameMethod = permissionClass.getMethod("getActionName", new Class[]{String.class, String.class, Locale.class});
	} catch (NoSuchMethodException cnfe) {
		log.warn("Permission class doesn't provide method getActionName(String, String, Locale) to get action names.");
	}

	String[] actionDescriptions = new String[(actionList.size())];
	for (int i = 0; i < actionList.size(); i++) {
		String actionName = (String) actionList.get(i);
		String actionDescription = actionName;
		if (actionNameMethod != null) {
			actionDescription = (String) actionNameMethod.invoke(null, new Object[]{permissionClass.getName(), actionName, LocaleManager.currentLocale()});
		}
		actionDescriptions[i] = actionDescription;
	}

	// Otain permissions for specific class
	List<PermissionDescriptor> permissions = permissionsHandler.getPermissions();
	boolean hasPermissions = permissions.size() > 0;
%>

<div style="padding-top:20px; padding-bottom:10px; margin:0px; font-weight:bold;">
	<i18n:message key="permissions.list">!!!Permissions</i18n:message>:
</div>
<table cellspacing="0" cellpadding="0" border="0" style="margin-bottom:20px;">
	<%--Global table-actions header--%>
	<tr>
		<td colspan="2">
			<table class="skn-table_border" cellspacing="0" cellpadding="0" border="0" width="100%">
				<tr>
					<td>
						<table>
							<tr>
								<td style="padding-top:1px; vertical-align:top;">
									<%
										if (permissionsHandler.getSelectedPermissionsAmount() > 0) {
									%>
									<a href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_UNSELECT_ALL_OBJECTS%>" friendly="true"/>">
										<img style="border:0px" src="<static:image relativePath="general/16x16/ico-check_off.png"/>" title="<i18n:message key="permissions.deselect.all">!!! Unselect all</i18n:message>" style="border:none;">
									<% } else { %>
									<a href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_SELECT_ALL_OBJECTS%>" friendly="true"/>">
										<img style="border:0px" src="<static:image relativePath="general/16x16/ico-check_on.png"/>" title="<i18n:message key="permissions.select.all">!!! Select all</i18n:message>" style="border:none;">
									<% }%>
									</a>
								</td>
								<td>
									<a href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_DELETE_SELECTED_OBJECTS%>" friendly="true"/>"
									   onclick="
										   <%
											   	if (permissionsHandler.getSelectedPermissionsAmount() == 0) {
										   %>
												alert('<i18n:message key="permissions.select.none">!!! No items selected</i18n:message>');return false;
										   <%
												} else {
										   %>
										   			if (!confirm('<i18n:message key="permissions.delete.selected.confirm">!!! Are you sure?</i18n:message>')) return false;
										   <%
												}
   %>                               ">
										<img style="border:0px" src="<static:image relativePath="general/16x16/ico-select-trash.png"/>" title="<i18n:message key="permissions.delete.selected">!!! Delete Selected?</i18n:message>" style="border:none;">
									</a>
								</td>
								<td>
									<a onclick="if (!confirm('<i18n:message key="permissions.delete.all.confirm">!!! Delete all?</i18n:message>')) return false;"
									   href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_DELETE_ALL_OBJECTS%>" friendly="true"/>">
										<img style="border:0px" src="<static:image relativePath="general/16x16/ico-all-trash.png"/>" title="<i18n:message key="permissions.delete.all.confirm">!!! Delete all?</i18n:message>" style="border:none;">
									</a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table class="skn-table_border" cellspacing="0" cellpadding="5" border="0" width="100%">
				<%--Permission table header--%>
				<tr>
					<td colspan="2" class="skn-table_header" style="border:1px solid #FFFFFF;">
						<i18n:message key="permissions.actions">!!! Actions</i18n:message>
					</td>
					<td class="skn-table_header" style="border:1px solid #FFFFFF;">
						<i18n:message key="permissions.role">!!! Quien</i18n:message>
					</td>
					<%
						for (int i = 0; i < actionDescriptions.length; i++) {
					%>
					<td class="skn-table_header" style="border:1px solid #FFFFFF; border-left:none;">
						<%= actionDescriptions[i] %>
					</td>
					<%
						}
					%>
				</tr>
				<%
					if (hasPermissions) {
						for (int intPerm = 0; intPerm < permissions.size(); intPerm++) {
							String className, altClass;
							if (intPerm % 2 == 0) {
								className = "skn-odd_row";
								altClass = "skn-odd_row_alt";
							} else {
								className = "skn-even_row";
								altClass = "skn-even_row_alt";
							}

							PermissionDescriptor pd = permissions.get(intPerm);
							Long pdId = pd.getDbid();
				%>
				<tr class="<%=className%>" onmouseover="className='<%=altClass%>'" onmouseout="className='<%=className%>'">
					<%--Individual permission actions (select, delete)--%>
					<td align="center" width="1px" valign="top">
						<% if (!pd.isReadonly()) { %>
						<a href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_SELECT_OBJECT%>" friendly="true"><factory:param name="<%=PermissionsHandler.PARAM_OBJECT_ID%>" value="<%=pdId%>"/></factory:url>">
							<% if (permissionsHandler.isPermissionSelected(pdId)) { %>
							<img title="<i18n:message key="permissions.deselect">!!! Deselect</i18n:message>" src="<static:image relativePath="general/16x16/ico-check_on.png"/>" style="border:0px">
							<% } else { %>
							<img title="<i18n:message key="permissions.select">!!! Select</i18n:message>" src="<static:image relativePath="general/16x16/ico-check_off.png"/>" style="border:0px">
							<% } %>
						</a>
						<% } else { %>
						&nbsp;
						<% } %>
					</td>
					<td align="center" width="1px" valign="top">
						<% if (!pd.isReadonly()) { %>
						<a onclick="if (!confirm('<i18n:message key="permissions.delete.confirm">Sure?</i18n:message>')) return false;"
							href="<factory:url action="<%=PermissionsHandler.PARAM_ACTION_DELETE_OBJECT%>" friendly="true" >
									<factory:param name="<%=PermissionsHandler.PARAM_OBJECT_ID%>" value="<%=pdId%>"/>
								  </factory:url>">
							<img title="<i18n:message key="permissions.delete">!!! Deselect</i18n:message>" src="<static:image relativePath="general/16x16/ico-trash.png"/>" style="border:0px">
						</a>
						<% } else { %>
						&nbsp;
						<% } %>
					</td>
					<%--Permission details--%>
					<td><%= pd.getPrincipal() %>
					</td>
					<%
						String[] pdActions = pd.getPermissionActions().split(",");

						for (Iterator<String> acIt = actionList.iterator(); acIt.hasNext(); ) {
							boolean denied = false;
							boolean granted = false;
							String actionName = acIt.next();
							for (int intPdAction = 0; intPdAction < pdActions.length; intPdAction++)  {
								String pdAction = pdActions[intPdAction];
								if (pdAction.startsWith("!")) {
									if (pdAction.substring(1).equals(actionName)) {
										denied = true;
									}
								} else {
									if (pdAction.equals(actionName)) {
										granted = true;
									}
								}
							}
					%>
					<td>
						<%
							if (denied) {
						%>
						<div style="width:100%; height:100%; text-align:center;vertical-align:middle">
							<img src="<static:image relativePath="general/16x16/ico-no-ok.png"/>"/>
						</div>
						<%
							} else if (granted) {
						%>
						<div style="width:100%; height:100%; text-align:center;vertical-align:middle">
							<img src="<static:image relativePath="general/16x16/ico-ok.png"/>"/>
						</div>
						<%
							}
						%>
					</td>
					<%
						}
					%>
				</tr>
				<%
					}
				} else {
				%>
				<tr>
					<td>
						<i18n:message key="permissions.none">!!!No permissions</i18n:message>
					</td>
				</tr>
				<%
					}
				%>
			</table>
		</td>
	</tr>
</table>

