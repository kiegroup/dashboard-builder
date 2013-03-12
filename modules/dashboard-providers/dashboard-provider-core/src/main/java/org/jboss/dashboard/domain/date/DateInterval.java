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
package org.jboss.dashboard.domain.date;

import org.jboss.dashboard.domain.AbstractInterval;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * A date interval is a period between two given dates.
 */
public class DateInterval extends AbstractInterval {

    protected Date minDate;
    protected Date maxDate;
    protected boolean minDateIncluded;
    protected boolean maxDateIncluded;

    public DateInterval() {
        minDate = null;
        maxDate = null;
        minDateIncluded = true;
        maxDateIncluded = false;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public boolean isMinDateIncluded() {
        return minDateIncluded;
    }

    public void setMinDateIncluded(boolean minDateIncluded) {
        this.minDateIncluded = minDateIncluded;
    }

    public boolean isMaxDateIncluded() {
        return maxDateIncluded;
    }

    public void setMaxDateIncluded(boolean maxDateIncluded) {
        this.maxDateIncluded = maxDateIncluded;
    }

    public String getDescription(Locale l) {

        // Example of date format:
        //      SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        //      System.out.println(format.format(new Date()));  Tue Nov 04 21:53:43 EST 2003
        // TODO: complete
        DateDomain dd = (DateDomain) domain;
        switch (dd.getIntervalMode()) {
            case DateDomain.INTERVAL_DECADE:
                SimpleDateFormat formatYear  = new SimpleDateFormat("yyyy", l);
                return formatYear.format(minDate);
            case DateDomain.INTERVAL_YEAR:
                SimpleDateFormat format  = new SimpleDateFormat("yyyy", l);
                return format.format(minDate);
            case DateDomain.INTERVAL_QUARTER:
                format = new SimpleDateFormat("MMM yyyy", l);
                return format.format(minDate);
            case DateDomain.INTERVAL_MONTH:
                format = new SimpleDateFormat("MMMM yyyy", l);
                return format.format(minDate);
            case DateDomain.INTERVAL_WEEK:
                return DateFormat.getDateInstance(DateFormat.SHORT, l).format(minDate);
            case DateDomain.INTERVAL_DAY:
                format = new SimpleDateFormat("EEE", l);
                return format.format(minDate) + " " + DateFormat.getDateInstance(DateFormat.SHORT, l).format(minDate);
            case DateDomain.INTERVAL_HOUR:
                format = new SimpleDateFormat("HH", l);
                return format.format(minDate) + "h";
            case DateDomain.INTERVAL_MINUTE:
                format = new SimpleDateFormat("mm", l);
                return format.format(minDate);
            case DateDomain.INTERVAL_SECOND:
                format = new SimpleDateFormat("ss", l);
                return format.format(minDate);
            default:
                format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", l);
                return format.format(minDate);
        }
    }

    public boolean contains(Object value) {
        try {
            if (value == null) return false;
            Date d = (Date) value;
            if (minDate != null && minDateIncluded && d.before(minDate)) return false;
            if (minDate != null && !minDateIncluded && !d.after(minDate)) return false;
            if (maxDate != null && maxDateIncluded && d.after(maxDate)) return false;
            if (maxDate != null && !maxDateIncluded && !d.before(maxDate)) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}