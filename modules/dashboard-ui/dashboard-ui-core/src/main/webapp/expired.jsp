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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%= LocaleManager.lookup().getDefaultLocale()%>"/>
<html>
<head>
    <title>Desconectado</title>
    <meta name="expiredSessionTokenForAjaxRecognizing" content="true"/>
    <script type="text/javascript" defer>
        if (window.top.opener) {
            window.top.opener.top.location.href = window.location.href;
            window.close();
        }
    </script>
</head>

<body>
<script type="text/javascript" defer>
    if (window.top.opener) {
        window.top.opener.top.location.href = window.location.href;
        window.close();
    }
    if (window.top.location.href.indexOf("expired.jsp") == -1) {
        window.top.location.href = "<%=request.getContextPath()%>/expired.jsp";
    }
</script><div id="container" style="margin-top:60px;">
    <div id="content" align="center">
        <fieldset style="width:560px;height:180px;margin:15px;">
                <div style="width:475px;margin-left:0px;margin-right:0px;margin-top:25px;margin-bottom:25px;text-align:left;line-height:20px;font:normal 12px verdana;color:#FF0000;line-height:145%;padding:10px;">
                    <i18n:message key="ui.expired">La sesi&oacute;n ha expirado</i18n:message>
                    <p align="center"><a href="index.jsp"><b><i18n:message key="ui.error.continue">Continuar</i18n:message></b></a></p><br><br>
                </div>
        </fieldset>
    </div>
</div>
</body>
</html>
