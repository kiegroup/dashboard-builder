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

<div class="skn-table_header" style="text-align:center; width:100%; background-color:#d6d6bc;"><i18n:message key="actions">!!Actions</i18n:message></div>
<ul style="list-style:none;">

<factory:property property="actionsFormatter" id="formatter">
<mvc:formatter name="<%=formatter%>">
    <mvc:fragment name="filterFile">
        <li style="margin-top:10px;">
            <form action="<factory:formUrl/>" method="post" >
                <i18n:message key="fileFilter">!!!Filtrar archivos </i18n:message>:<br />
                <factory:handler action="setFilter"/>
                <input type="text" name="filter" class="skn-input" value="<mvc:fragmentValue name="currentFilter"/>" style="margin-top:5px;">
                <input type="submit" class="skn-button" value="<i18n:message key="filter">!!Filtrar</i18n:message>">
            </form>
        </li>
    </mvc:fragment> 
    <mvc:fragment name="deleteFile">
        <li style="margin-top:10px">
            <a href="<factory:url  action="deleteFile"/>"> <i18n:message key="deleteFile">!!!Borrar fichero</i18n:message> <i><mvc:fragmentValue name="fileName"/></i> </a>
        </li>
    </mvc:fragment>
    <mvc:fragment name="deleteFolder">
        <li style="margin-top:10px">
            <a href="<factory:url  action="deleteFolder"/>"> <i18n:message key="deleteFolder">!!!Borrar carpeta</i18n:message> <i><mvc:fragmentValue name="folderName"/></i> </a>
        </li>
    </mvc:fragment>
    <mvc:fragment name="uploadFileInput">
        <li style="margin-top:10px">
            <form action="<factory:formUrl/>" method="post" enctype="multipart/form-data">
                <i18n:message key="uploadFile">!!!Subir fichero en carpeta </i18n:message> <i><mvc:fragmentValue name="folderName"/></i>:<br />
                <factory:handler action="uploadFile"/>
                <input type="file" name="file" class="skn-input" style="margin-top:5px;">
                <input type="submit" class="skn-button" value="<i18n:message key="upload"/>">
            </form>
        </li>
    </mvc:fragment>
    <mvc:fragment name="createFolderInput">
        <li style="margin-top:10px">
       <form action="<factory:formUrl/>" method="post" >
           <i18n:message key="createFolder">!!!Crear nueva carpeta en </i18n:message> <i><mvc:fragmentValue name="folderName"/></i>:<br />
           <factory:handler action="createFolder"/>
           <input type="text" name="folderName" class="skn-input" style="margin-top:5px;">
           <input type="submit" class="skn-button" value="<i18n:message key="create"/>">
       </form>
       </li>
    </mvc:fragment>
</mvc:formatter>
</factory:property>
</ul>