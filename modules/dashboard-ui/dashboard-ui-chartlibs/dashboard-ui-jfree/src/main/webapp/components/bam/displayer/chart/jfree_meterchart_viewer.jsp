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
<%@ page import="org.jboss.dashboard.factory.Factory"%>
<%@ page import="org.jboss.dashboard.displayer.chart.MeterChartDisplayer" %>
<%@ page import="org.jboss.dashboard.dataset.DataSet" %>
<%@ page import="java.util.List" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.JFreeMeterChartViewer" %>
<%@ page import="org.jboss.dashboard.ui.components.chart.JFreeAbstractChartViewer" %>
<%@taglib uri="/WEB-INF/tlds/cewolf.tld" prefix="cewolf" %>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib prefix="panel" uri="bui_taglib.tld" %>
<panel:defineObjects/>
<%
    // Set the viewer
    JFreeMeterChartViewer viewer = (JFreeMeterChartViewer) Factory.lookup("org.jboss.dashboard.ui.components.MeterChartViewer_jfree");
    MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) viewer.getDataDisplayer();
    DataSet xyDataSet =  meterDisplayer.buildXYDataSet();
    List orderListOfIntervals = xyDataSet.getProperties()[0].getValues();
%>
    <table align="<%= meterDisplayer.getGraphicAlign() %>">
<%
    if (meterDisplayer.getPositionType().equals("horizontal")) {
%>
        <tr>
        <%
            for (int i = 0; i < orderListOfIntervals.size(); i++) {
                viewer.setIntervalToShow(i);
        %>
            <td>
                <a href="<factory:url action="<%=JFreeAbstractChartViewer.PARAM_ACTION%>" bean="<%=viewer.getName()%>">
                    <factory:param name="<%=JFreeAbstractChartViewer.PARAM_NSERIE%>" value="<%=new Integer(i)%>"/>
                </factory:url>" id="<panel:encode name="meter<%=new Integer(i)%>"/>">
                <%
                    request.setAttribute("viewer", viewer);
                    request.setAttribute("chartType", "combined" + meterDisplayer.getType());
                %>                    
                <mvc:include page="jfree_chart.jsp"  flush="true" />
                </a>
              <script defer="true">
                setAjax('<panel:encode name="meter<%=new Integer(i)%>"/>');
              </script>
            </td>
        <%
            }
        %>
        </tr>
<%
    } else {
        for (int i = 0; i < orderListOfIntervals.size(); i++) {
            viewer.setIntervalToShow(i);
%>
        <tr>
            <td>
                <a href="<factory:url action="<%=JFreeAbstractChartViewer.PARAM_ACTION%>" bean="<%=viewer.getName()%>">
                    <factory:param name="<%=JFreeAbstractChartViewer.PARAM_NSERIE%>" value="<%=new Integer(i)%>"/>
                </factory:url>" id="<panel:encode name="meter<%=new Integer(i)%>"/>">
                <%
                    request.setAttribute("viewer", viewer);
                    request.setAttribute("chartType", "combined" + meterDisplayer.getType());
                %>                
                <mvc:include page="jfree_chart.jsp"  flush="true" />
                </a>
              <script defer="true">
                setAjax('<panel:encode name="meter<%=new Integer(i)%>"/>');
              </script>
            </td>
        </tr>
<%
        }
    }
%>
    </table>