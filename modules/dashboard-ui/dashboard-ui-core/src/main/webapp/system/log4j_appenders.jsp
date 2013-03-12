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
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.*" %>
<%!
    static Map<String,AppenderConfiguration> appenderMap = null;
    static {
        if (appenderMap == null) {
            appenderMap = new HashMap<String,AppenderConfiguration>();

            Set<Logger> loggers = new HashSet<Logger>();
            loggers.add(Logger.getRootLogger());
            Enumeration enumLoggers = LogManager.getCurrentLoggers();
            while (enumLoggers.hasMoreElements()) loggers.add((Logger) enumLoggers.nextElement());

            for (Logger logger : loggers) {
                Enumeration enumApps = logger.getAllAppenders();
                while (enumApps.hasMoreElements()) {
                    Appender appender = (Appender) enumApps.nextElement();
                    AppenderConfiguration appConf = appenderMap.get(appender.getName());
                    if (appConf == null) appenderMap.put(appender.getName(), appConf = new AppenderConfiguration(appender));
                    appConf.addLogger(logger);
                }
            }
        }
    }
    static class AppenderConfiguration {
        Appender appender;
        boolean enabled;
        Set<Logger> loggers;

        AppenderConfiguration(Appender appender) {
            this.appender = appender;
            this.enabled = true;
            this.loggers = new HashSet<Logger>();
        }
        public void enable() {
            Logger rootLogger = Logger.getRootLogger();
            enabled = true;
            rootLogger.addAppender(appender);
        }
        public void disable() {
            Logger rootLogger = Logger.getRootLogger();
            enabled = false;
            rootLogger.removeAppender(appender);
        }

        public void addLogger(Logger logger) {
            loggers.add(logger);
        }

        public String printLoggers() {
            StringBuffer buf = new StringBuffer();
            for (Logger logger: loggers) {
                if (buf.length() > 0) buf.append(", ");
                buf.append(logger.getName());
            }
            return buf.toString();
        }
    }
%>
<%
    // Process request
    String action = request.getParameter("action");
    if (action != null) {
        if (action.equals("appenderON")) {
            String appenderName = request.getParameter("appenderName");
            AppenderConfiguration appenderConf = appenderMap.get(appenderName);
            appenderConf.enable();
        }
        if (action.equals("appenderOFF")) {
            String appenderName = request.getParameter("appenderName");
            AppenderConfiguration appenderConf = appenderMap.get(appenderName);
            appenderConf.disable();
        }
        if (action.equals("changePattern")) {
            String appenderName = request.getParameter("appenderName");
            String pattern = request.getParameter("pattern");
            AppenderConfiguration appenderConf = appenderMap.get(appenderName);
            PatternLayout layout = (PatternLayout) appenderConf.appender.getLayout();
            layout.setConversionPattern(pattern);
        }
    }
%>
<ul>
<%
    for (AppenderConfiguration appConf : appenderMap.values()) {
        Appender appender = appConf.appender;
        Layout layout = appender.getLayout();
%>
        <li><b><%= appender.getName() %></b>&nbsp;-&nbsp;<%= appender.getClass().getName() %><br/>
            <table border="0" cellpadding="1" cellspacing="2">
                <tr>
                    <td align="left">Enabled</td>
                    <td align="left">
                        <form action="<%=request.getRequestURI()%>" method="post">
                            <input type="hidden" name="action" value="<%= appConf.enabled ? "appenderOFF" : "appenderON" %>"/>
                            <input type="hidden" name="appenderName" value="<%= appender.getName() %>"/>
                            <input type="checkbox" class="skn-input" <%= appConf.enabled ? "checked" : "" %> onchange="this.form.submit();" />
                        </form>
                    </td>
                </tr>
                <tr>
                    <td align="left">Logger(s)</td>
                    <td align="left">=<%= appConf.printLoggers() %></td>
                </tr>
<%
        if (appender instanceof FileAppender) {
            FileAppender drfAppender = (FileAppender) appender;
%>
                <tr>
                    <td align="left">Log file</td>
                    <td align="left">=<%= drfAppender.getFile() %></td>
                </tr>
<%
        }
        if (layout instanceof PatternLayout) {
            PatternLayout pLayout = (PatternLayout) layout;
%>
                <tr>
                    <td align="left">Log pattern</td>
                    <td align="left">
                        <form action="<%=request.getRequestURI()%>" method="post">
                            <input type="hidden" name="action" value="changePattern"/>
                            <input type="hidden" name="appenderName" value="<%= appender.getName() %>"/>
                            <input type="text" class="skn-input" size="120" name="pattern" value="<%= pLayout.getConversionPattern() %>" onchange="this.form.submit();" />
                        </form>
                     </td>
                </tr>
<%
        }
%>
            </table>
        </li><br/>
<%
    }
%>
</ul>
