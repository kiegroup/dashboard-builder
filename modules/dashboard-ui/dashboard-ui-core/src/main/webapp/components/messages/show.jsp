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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.components.messages" id="defaultBundle" locale="<%=LocaleManager.currentLocale()%>"/>

<factory:property property="messagesComponentFormatter" id="messagesComponentFormatter">
<mvc:formatter name="<%=messagesComponentFormatter%>">
    <mvc:fragment name="outputStart">
        <mvc:fragmentValue name="image" id="image">
        <mvc:fragmentValue name="bundle" id="bundle">
        <div style="background-color: #f5f5dc; margin: 0px; width:100%; padding-left: 5px; padding-top: 5px; padding-bottom: 5px">
<%
    if (!StringUtils.isEmpty((String)bundle)) {
%>
            <i18n:bundle baseName="<%=(String)bundle%>" id="theBundle" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    }
%>
            <table border="0">
                <tr>
                    <td valign="top">
                        <img src="<static:image relativePath="<%= (String)image%>"/>"  border="0"/>
                    </td>
                    <td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputVisibleMessagesStart">
                        <table>
    </mvc:fragment>
    <mvc:fragment name="outputHiddenMessagesStart">
        <mvc:fragmentValue name="id" id="id">
                        <table id="<%="table_" + id%>" style="display:none;">
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputMessage">
        <mvc:fragmentValue name="msg" id="msg">
        <mvc:fragmentValue name="params" id="params">
        <mvc:fragmentValue name="className" id="className">
        <mvc:fragmentValue name="bundle" id="bundle">
                            <tr>
                                <td <%= className!=null ? "class=\""+className+"\"" : ""%> align="left">
<%
    if (!StringUtils.isEmpty((String)bundle)) {
%>
                                    <i18n:message key="<%=(String)msg%>" bundleRef="theBundle" args="<%=(String[])params%>"><%=msg%></i18n:message>
<%
    } else {
%>
                                    <%=msg%>
<%
    }
%>
                                </td>
                            </tr>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputMessagesEnd">
                        </table>
                    </td>
    </mvc:fragment>
    <mvc:fragment name="outputNewLine">
                </tr>
                <tr>
                    <td></td>
                    <td>
    </mvc:fragment>
    <mvc:fragment name="outputDisplayLinks">
        <mvc:fragmentValue name="id" id="id">
        <mvc:fragmentValue name="min" id="min">
        <mvc:fragmentValue name="max" id="max">
                        <div style="width:100%; text-align:center;">
                            <a href="#" id="<%="link_"+id+"_show"%>"
                               onclick="document.getElementById('<%="table_" + id%>').style.display='';
                                        document.getElementById('<%="link_"+id+"_hide"%>').style.display='';
                                        this.style.display='none';
                                        return false;">
                                <i18n:message key="show" bundleRef="defaultBundle">!!! ver mas</i18n:message>
                            </a>
                            <a href="#" style="display:none;" id="<%="link_"+id+"_hide"%>"
                               onclick="document.getElementById('<%="table_" + id%>').style.display='none';
                                        document.getElementById('<%="link_"+id+"_show"%>').style.display='';
                                        this.style.display='none';
                                        return false;">
                                <i18n:message key="hide" bundleRef="defaultBundle">!!! esconder</i18n:message>
                            </a>
                        </div>
                    </td>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
                </tr>
            </table>
        </div>
    </mvc:fragment>
</mvc:formatter>
</factory:property>