<%@ page import="org.jboss.dashboard.ui.components.KPIViewer"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.kpi.KPI" %>
<%@ page import="org.jboss.dashboard.workspace.Parameters" %>
<%@ page import="org.jboss.dashboard.workspace.Panel" %>
<%@ taglib uri="http://dashboard.jboss.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib prefix="mvc" uri="mvc_taglib.tld" %>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>

<html lang="<factory:property bean="org.jboss.dashboard.LocaleManager" property="currentLang"/>">
<head>
    <panel:envelopeHead/>
    <title>kpi </title>
</head>
<body>
<i18n:bundle baseName="org.jboss.dashboard.displayer.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<%
    KPIViewer kpiViewer = KPIViewer.lookup();
    Panel panel = (Panel) request.getAttribute(Parameters.RENDER_PANEL);
    if( kpiViewer != null && panel != null && kpiViewer.isReady()) {
       String viewerPath = kpiViewer.getName();
%>
<%@ include file="/section/render_panel_content.jsp" %>
<% } %>
<panel:envelopeFooter/>
</body>
</html>