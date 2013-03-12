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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.components.messages" locale="<%=LocaleManager.currentLocale()%>"/>

<div style="background-color:#F1F1E3; padding:2px; vertical-align:top;"><div style="text-align:center; width:100%; background-color:#d6d6bc;" class="skn-table_header"><i18n:message key="preview">!!Preview</i18n:message></div></div>
<div style="width:200px; height:185px; overflow:auto;">
<factory:property property="filePreviewFormatter" id="formatter">
<mvc:formatter name="<%=formatter%>">
    <mvc:fragment name="outputAsImage">
        <a href="<mvc:fragmentValue name="imageUrl"/>" target="_blank">
            <img style="padding-top:30px;" border="0" src="<mvc:fragmentValue name="imageUrl"/>" alt="preview">
        </a>
    </mvc:fragment>
    <mvc:fragment name="outputAsFile">
        <a href="<mvc:fragmentValue name="url"/>" target="_blank">
        <img style="padding-top:30px;" border="0" src="<mvc:fragmentValue name="imageUrl"/>" alt="preview">
            <br>
        <mvc:fragmentValue name="fileName"/>
        </a>
    </mvc:fragment>
</mvc:formatter>
</factory:property>
</div>

