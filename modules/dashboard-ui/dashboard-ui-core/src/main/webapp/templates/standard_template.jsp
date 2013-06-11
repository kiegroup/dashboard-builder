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
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<mvc:formatter name="org.jboss.dashboard.ui.formatters.NavigationFormatter">
    <mvc:fragment name="config">
        <resource:page category="envelope" resourceId="FULL_PAGE" id="pageToInclude"/>
        <mvc:include page='<%=String.valueOf(pageToInclude)%>' flush="true"/>
    </mvc:fragment>
    <mvc:fragment name="workspacePage">
        <resource:page category="envelope" resourceId="SHARED_PAGE" id="pageToInclude"/>
        <mvc:include page='<%=String.valueOf(pageToInclude)%>' flush="true"/>
    </mvc:fragment>
    <mvc:fragment name="loginPage">
		<mvc:include page='/login.jsp' flush="true"/>
    </mvc:fragment>
</mvc:formatter>

