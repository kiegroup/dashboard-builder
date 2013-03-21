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
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Locale"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<jsp:include page="datetimepicker.jsp" flush="true" />

Calendar.prototype.SetYear = function(amount) {
    try{
        var year = parseInt(amount);
        if(!isNaN(year)) Cal.Year=year;
    } catch(e) {}
};

    MonthName = [
            <%
            Locale currentLocale = LocaleManager.currentLocale();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM",currentLocale);
            Calendar calendar = GregorianCalendar.getInstance(currentLocale);
            calendar.set(GregorianCalendar.MONTH,GregorianCalendar.JANUARY);
            for(int i = 0; i< 12; i++){
                String monthName = sdf.format(calendar.getTime());
                calendar.add(GregorianCalendar.MONTH,1);

            %>
            <%=i==0?"":", "%>"<%=monthName%>"
            <%
            }
            %>];

    WeekDayName<%=calendar.getFirstDayOfWeek()%> = [
            <%
            sdf = new SimpleDateFormat("EE",currentLocale);
            calendar.set(GregorianCalendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            for(int i = 0; i< 7; i++){
                String monthName = sdf.format(calendar.getTime());
                calendar.add(GregorianCalendar.DAY_OF_WEEK,1);
            %>
            <%=i==0?"":", "%>"<%=monthName%>"
            <%
            }
            %>];
    MondayFirstDay=<%=calendar.getFirstDayOfWeek() == Calendar.MONDAY%>;

function NewCal(pCtrl, pFormat, pShowTime) {
    NewCssCal(pCtrl, pFormat, "INPUTTEXT", pShowTime, '24', false, null);
}
function NewCal(pCtrl, pFormat, pShowTime, clickEvent) {
    NewCssCal(pCtrl, pFormat, "INPUTTEXT", pShowTime, '24', false, clickEvent);
}
