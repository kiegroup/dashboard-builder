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
<%@ taglib uri="factory.tld" prefix="factory" %>

<factory:property bean="org.jboss.dashboard.ui.NavigationManager" property="showingConfig" id="showingConfig">
<% if(Boolean.TRUE.equals(showingConfig)){%>
        <meta http-equiv="Page-Enter"
              content="progid:DXImageTransform.Microsoft.Zigzag(duration=0)">
            <meta http-equiv="Page-Exit"
              content="progid:DXImageTransform.Microsoft.Zigzag(duration=0)">
<%}%>
</factory:property>
<link rel="stylesheet" href='<resource:link category="skin" resourceId="CSS"/>' type="text/css">
<link rel="stylesheet" href='<resource:link category="envelope" resourceId="CSS"/>' type="text/css">
<link REL="icon" HREF="<mvc:context uri="/favicon.ico"/>" TYPE="image/x-icon">
<link rel="shortcut icon" href="<mvc:context uri="/favicon.ico"/>" type="image/x-icon">

<style type="text/css">
.dropOnRegion {
background-color:gold;
}
.regionTitle {
background-color:#0099FF;
}

/* Chart related styles */
.skn-chart-table {  padding:10px; margin:5px;border:1px solid #eeeeee; }
.skn-chart-title { text-align:center; font-size: 120%; font-weight: bold;padding-bottom:10px; }
.skn-chart-tooltip { text-align:center; font-size: 100%; font-weight: bold; height:25px}

</style>

