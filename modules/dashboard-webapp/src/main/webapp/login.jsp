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
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.controller.requestChain.SessionInitializer" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>JBoss Dashboard Builder</title>

    <style type="text/css">
        * {
            font-family: Helvetica, Arial, sans-serif;
        }

        body {
            margin: 0;
            pading: 0;
            color: #fff;
            background: repeat #55504C;
            font-size: 14px;
            text-shadow: #050505 0 -1px 0;
            font-weight: bold;
        }

        li {
            list-style: none;
        }

        #dummy {
            position: absolute;
            top: 0;
            left: 0;
            border-bottom: solid 3px #777973;
            height: 250px;
            width: 100%;
            background: #FFFFFF;
            z-index: 1;
        }

        #dummy2 {
            position: absolute;
            top: 0;
            left: 0;
            border-bottom: solid 2px #545551;
            height: 252px;
            width: 100%;
            background: transparent;
            z-index: 2;
        }

        #login-wrapper {
            margin: 0 0 0 -160px;
            width: 320px;
	        text-align: left;
            z-index: 99;
            position: absolute;
            top: 0;
            left: 50%;
        }

        #login-top {
            height: 120px;
            width: 201px;
            padding-top: 20px;
            text-align: center;
        }

        #login-content {
            margin-top: 120px;
        }

        label {
            width: 70px;
            float: left;
            padding: 8px;
            line-height: 14px;
            margin-top: -4px;
        }

        input.text-input {
            width: 200px;
            float: right;
            -moz-border-radius: 4px;
            -webkit-border-radius: 4px;
            border-radius: 4px;
            background: #fff;
            border: solid 1px transparent;
            color: #555;
            padding: 8px;
            font-size: 13px;
        }

        input.button {
            float: right;
            padding: 6px 10px;
            color: #fff;
            font-size: 14px;
            background: #E22434; /* Non CSS3 browsers. */
            background: linear-gradient(top, #E05A6A 0%,#E22434 100%); /* W3C */
            background: -webkit-gradient(linear, left top, left bottom, from(#E05A6A), to(#E22434)); /* Chrome,Safari4+ */
            background: -webkit-linear-gradient(top, #E05A6A 0%,#E22434 100%); /* Chrome10+,Safari5.1+ */
            background: -moz-linear-gradient(top,  #E05A6A,  #E22434); /* FF */
            background: -o-linear-gradient(top, #E05A6A 0%,#E22434 100%); /* Opera11.10+ */
            filter: progid:DXImageTransform.Microsoft.Gradient(endColorstr='#E22434', startColorstr='#E05A6A', gradientType='0'); /* IE6-9 */
            background: -ms-linear-gradient(top, #E05A6A 0%,#E22434 100%); /* IE10+ */
            text-shadow: #050505 0 -1px 0;
             background-color: #E22434;
            -moz-border-radius: 4px;
            -webkit-border-radius: 4px;
            border-radius: 4px;
            border: solid 1px transparent;
            font-weight: bold;
            cursor: pointer;
            letter-spacing: 1px;
        }

        input.button:hover {
            background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#a4d04a), to(#a4d04a), color-stop(80%, #76b226));
    		text-shadow: #050505 0 -1px 2px;
	    	background-color: #E22434;
            color: #fff;
        }

        div.error {
            padding: 8px;
            background: rgba(52, 4, 0, 0.4);
            -moz-border-radius: 8px;
            -webkit-border-radius: 8px;
            border-radius: 8px;
            border: solid 1px transparent;
            margin: 6px 0;
        }
    </style>
</head>

<body id="login">

<div id="login-wrapper" class="png_bg">
    <div id="login-top">
        <img src="<%=request.getContextPath()%>/images/jb_logo.png" alt="JBoss Logo" title="Powered By JBoss"/>
    </div>

    <div id="login-content">
        <%
            LocaleManager localeManager = LocaleManager.lookup();
            Locale currentLocale =  localeManager.getCurrentLocale();
            SessionInitializer.PreferredLocale preferredLocale =  SessionInitializer.getPreferredLocale(request);
            if (preferredLocale != null) currentLocale = preferredLocale.asLocale();
            ResourceBundle i18nBundle = LocaleManager.lookup().getBundle("org.jboss.dashboard.login.messages", currentLocale);
            String messageKey = request.getParameter("message");
            if (messageKey == null) messageKey = "login.hint";
        %>
        <h3><%= i18nBundle.getString(messageKey) %></h3>
        <form action="j_security_check" method="POST">
            <p>
                <label><%= i18nBundle.getString("login.username") %></label>
                <input value="" name="j_username" class="text-input" type="text" autofocus/>
            </p>
            <br style="clear: both;"/>

            <p>
                <label><%= i18nBundle.getString("login.password") %></label>
                <input name="j_password" class="text-input" type="password"/>
            </p>
            <br style="clear: both;"/>

            <p>
                <input class="button" type="submit" value="Sign In"/>
            </p>

        </form>
    </div>
</div>
<div id="dummy"></div>
<div id="dummy2"></div>
</body>
</html>
