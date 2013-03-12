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
<%@ page import="org.jboss.dashboard.displayer.chart.AbstractChartDisplayer"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.jboss.dashboard.provider.DataProperty"%>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.JFreeAbstractChartViewer" %>
<%@taglib uri="/WEB-INF/tlds/cewolf.tld" prefix="cewolf" %>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%
    JFreeAbstractChartViewer viewer = (JFreeAbstractChartViewer) request.getAttribute("viewer");
    AbstractChartDisplayer displayer = (AbstractChartDisplayer) viewer.getDataDisplayer();
    Locale locale = LocaleManager.currentLocale();

    DataProperty domainProperty = displayer.getDomainProperty();
    DataProperty rangeProperty = displayer.getRangeProperty();

    pageContext.setAttribute("datasetProducer", viewer);

    // Meter post processor.
    if (displayer instanceof MeterChartDisplayer) {
        pageContext.setAttribute("meterPostProcessor", viewer);
    }

    // Every chart must have a different identifier so as to do not merge tooltips.
    // Chart identifier is composed by producerId and this suffix.
    String suffix = Long.toString(System.currentTimeMillis());
%>
<cewolf:chart
        id="<%= viewer.getProducerId() + suffix %>"
        type="<%= displayer.getType() %>"
        xaxislabel="<%= domainProperty.getName(locale) %>"
        yaxislabel="<%= rangeProperty.getName(locale) %>"
        axisinteger="<%= displayer.isAxisInteger() %>"
        title="<%= displayer.isShowTitle() ? displayer.getTitle() : "" %>"
        showlegend="<%=(displayer instanceof MeterChartDisplayer) ? false : displayer.isShowLegend() %>"
        legendanchor="<%= displayer.getLegendAnchor() %>">
<%-- background ="<%= displayer.getBackgroundColor() %>"--%>
    <cewolf:data>
        <cewolf:producer id="datasetProducer"/>
    </cewolf:data>
    <%--cewolf:colorpaint color="<%= displayer.getColor() %>"/--%>
    <% if (displayer instanceof MeterChartDisplayer) { %>
    <cewolf:chartpostprocessor id="meterPostProcessor"/>
    <% pageContext.removeAttribute("meterPostProcessor"); } %>
</cewolf:chart>

<table align="<%= displayer.getGraphicAlign() %>">
    <tr>
        <td>
            <cewolf:img chartid="<%= viewer.getProducerId() + suffix %>" renderer="cewolf" border="0"
                        width="<%= displayer.getWidth() %>"
                        height="<%= displayer.getHeight() %>"
                        align="<%= displayer.getGraphicAlign() %>">
                <%
                    // Tooltip and link generators.
                    pageContext.setAttribute("datasetProducerToolTips", viewer.getToolTipGenerator());
                    pageContext.setAttribute("datasetLinkGenerator", viewer.getLinkGenerator());
                %>
                <cewolf:map tooltipgeneratorid="datasetProducerToolTips" linkgeneratorid="datasetLinkGenerator"/>
                <%
                    pageContext.removeAttribute("datasetProducerToolTips");
                    pageContext.removeAttribute("datasetLinkGenerator");
                %>
            </cewolf:img>
        </td>
    </tr>
</table>
<%
    pageContext.removeAttribute("datasetProducer");
%>
