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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%try {%>
<%@ taglib uri="resources.tld" prefix="resource" %><resource:page category="envelope" resourceId="FULL_PAGE" id="pageToInclude"/>
<jsp:include page='<%=pageToInclude.toString()%>' flush="true"/>
<%}
catch (Exception e) {
    e.printStackTrace();
}%>
