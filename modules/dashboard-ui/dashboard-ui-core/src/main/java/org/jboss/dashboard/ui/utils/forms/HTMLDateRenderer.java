/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.ui.utils.forms;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Simple utility class used to render a date combo (day + combo month + year)
 */
public class HTMLDateRenderer {
    private static final String[] months = {
            "Enero",
            "Febrero",
            "Marzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre"
    };

    private static String noNull(Object value) {
        return value == null ? "" : value.toString();
    }

    public static String render(String fieldId, Date value, boolean renderMinutes) {

        Date date = (Date) value;

        StringBuffer buf = new StringBuffer();
        Calendar calendar = new GregorianCalendar();

        if (date != null)
            calendar.setTime(date);

        String dayStr = (date == null) ? "" : String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String monthStr = (date == null) ? "" : String.valueOf((calendar.get(Calendar.MONTH) + 1));
        String yearStr = (date == null) ? "" : String.valueOf(calendar.get(Calendar.YEAR));
        String hourStr = (date == null) ? "" : String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minStr = (date == null) ? "" : String.valueOf(calendar.get(Calendar.MINUTE));

        buf.append("<input type='text' class='FormInput' size='4' name='" + fieldId + "_day' value='" + dayStr + "'>");
        buf.append("&nbsp;de&nbsp;<select name='" + fieldId + "_month' class='formSelect'>");

        buf.append("<option value=''>-- Mes --</option>");

        for (int i = 0; i < months.length; i++) {
            if (monthStr.equals(String.valueOf(i + 1))) {
                buf.append("<option value=" + (i + 1) + " selected>" + months[i] + "</option>");
            } else {
                buf.append("<option value=" + (i + 1) + ">" + months[i] + "</option>");
            }
        }

        buf.append("</select>");
        buf.append("&nbsp;de&nbsp;<input type='text' class='FormInput' size='4' name='" + fieldId + "_year' value='" + yearStr + "'>");
        buf.append("&nbsp;");

        if (renderMinutes) {
            buf.append("a las &nbsp;<input type='text' class='FormInput' size='2' name='" + fieldId + "_hour' value='" + hourStr + "'>");
            buf.append("&nbsp;:&nbsp;<input type='text' class='FormInput' size='2' name='" + fieldId + "_min' value='" + minStr + "'>");
        }

        return buf.toString();
    }
}
