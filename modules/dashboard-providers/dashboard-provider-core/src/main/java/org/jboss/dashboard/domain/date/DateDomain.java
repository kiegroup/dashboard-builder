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

import org.jboss.dashboard.domain.AbstractDomain;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.util.*;

/**
 * This domain is used for properties that contains dates values.
 * The intervals of a date domain are time periods (years, days, quarters, hours, ...).
 * The time-period used by the data displayer it depends on both the configuration of the
 * domain property and the data set values to visualize.
 */
public class DateDomain extends AbstractDomain {

    // i18n
    public static final String I18N_PREFFIX = "dateDomain.";

    // Date range modes.
    public static final int INTERVAL_SECOND   = 0;
    public static final int INTERVAL_MINUTE   = 1;
    public static final int INTERVAL_HOUR     = 2;
    public static final int INTERVAL_DAY      = 3;
    public static final int INTERVAL_WEEK     = 4;
    public static final int INTERVAL_MONTH    = 5;
    public static final int INTERVAL_QUARTER  = 6;
    public static final int INTERVAL_YEAR     = 7;
    public static final int INTERVAL_DECADE   = 8;

    public static final long[] INTERVAL_DURATION_IN_SECONDS = new long[] {
        1, /* INTERVAL_SECOND   = 0; */
        60, /* INTERVAL_MINUTE   = 1; */
        60*60, /* INTERVAL_HOUR     = 2; */
        60*60*24, /* INTERVAL_DAY      = 3; */
        60*60*24*7, /* INTERVAL_WEEK     = 4; */
        60*60*24*31,/* INTERVAL_MONTH    = 5; */
        60*60*24*31*4, /* /* INTERVAL_QUARTER  = 6; */
        60*60*24*31*12, /* INTERVAL_YEAR     = 7; */
        60*60*24*31*120, /* INTERVAL_DECADE   = 8; */};

    protected Date maxDate;
    protected Date minDate;
    protected int tamInterval;
    protected int intervalMode;

    public DateDomain() {
        super();
        maxDate = null;
        minDate = null;
        tamInterval = -1;
        intervalMode = -1;
    }

    public Class<Date> getValuesClass() {
        return java.util.Date.class;
    }

    public boolean isScalarFunctionSupported(ScalarFunction sf) {
        return sf.isTypeSupported(String.class);
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public int getTamInterval() {
        return tamInterval;
    }

    public void setTamInterval(int tamInterval) {
        this.tamInterval = tamInterval;
    }

    public int getIntervalMode() {
        return intervalMode;
    }

    public List<Interval> getIntervals() {
        List<Interval> results = new ArrayList<Interval>();

        // Purge the null values that comes from the data (f.i: null values in a SQL column)
        List domainValues = property.getValues();
        Iterator it = domainValues.iterator();
        while (it.hasNext()) if (it.next() == null) it.remove();

        // Calculate the data low & upper limits.
        Date absoluteMinDate = null;
        Date absoluteMaxDate = null;
        if (!domainValues.isEmpty()) {
            //Don't sort the whole collection (n*log(n) complexity) just to get min/max values (doable in linear time)!
            //Collections.sort(domainValues);
            absoluteMinDate = (Date) Collections.min(domainValues);
            absoluteMaxDate = (Date) Collections.max(domainValues);
        }

        // Ranges can not be created if limits are not defined.
        Date minDateLimit = minDate != null ? minDate : absoluteMinDate;
        Date maxDateLimit = maxDate != null ? maxDate : absoluteMaxDate;
        if (minDateLimit == null || maxDateLimit == null) return new ArrayList<Interval>();
        if (minDateLimit.after(maxDateLimit)) return new ArrayList<Interval>();

        // If min/max are equals then create a single interval.
        if (minDateLimit.compareTo(maxDateLimit) == 0) {
            DateInterval interval = new DateInterval();
            interval.setMinDate(minDateLimit);
            interval.setMaxDate(minDateLimit);
            interval.setMinDateIncluded(true);
            interval.setMaxDateIncluded(true);
            interval.setDomain(this);
            results.add(interval);
            return results;
        }

        // Create the intervals according to the min/max dates specified.
        if (maxNumberOfIntervals < 1) maxNumberOfIntervals = 10;
        intervalMode = calculateDateIntervalMode(maxNumberOfIntervals, minDateLimit, maxDateLimit);

        // Ensure the interval mode obtained is always greater or equals than the preferred interval size set by the user.
        if (tamInterval != -1 && intervalMode < tamInterval) intervalMode = tamInterval;

        // Get the intervals.
        List<DateInterval> listOfIntervals = getListOfIntervals(intervalMode, minDateLimit, maxDateLimit);

        // If there are values before the minDate, create the initial composite interval.
        if (minDate != null && absoluteMinDate != null && absoluteMinDate.before(minDate)) {
            // New date interval.
            DateInterval dateInterval = new DateInterval();
            dateInterval.setMinDate(absoluteMinDate);
            dateInterval.setMaxDate(minDate);
            dateInterval.setMinDateIncluded(true);
            dateInterval.setMaxDateIncluded(false);

            // New composite interval.
            CompositeInterval compositeMinInterval = new CompositeInterval();
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (Locale l : locales) {
                compositeMinInterval.setDescription("< " + listOfIntervals.get(0).getDescription(l), l);
            }
            Set<Interval> listOfMinIntervals = new HashSet<Interval>();
            listOfMinIntervals.add(dateInterval);
            compositeMinInterval.setIntervals(listOfMinIntervals);
            compositeMinInterval.setDomain(this);
            results.add(compositeMinInterval);
        }
        // Add the list of intervals.
        results.addAll(listOfIntervals);

        // If there are values after the maxDate, create the final composite interval.
        if (maxDate != null && absoluteMaxDate != null && absoluteMaxDate.after(maxDate)) {
            // New date interval.
            DateInterval dateInterval = new DateInterval();
            dateInterval.setMinDate(maxDate);
            dateInterval.setMaxDate(absoluteMaxDate);
            dateInterval.setMinDateIncluded(false);
            dateInterval.setMaxDateIncluded(true);

            // New composite interval.
            CompositeInterval compositeMaxInterval = new CompositeInterval();
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (int i=0; i<locales.length; i++) {
                Locale l = locales[i];
                compositeMaxInterval.setDescription("> " + listOfIntervals.get(listOfIntervals.size()-1).getDescription(l), l);
            }
            Set<Interval> listOfMaxIntervals = new HashSet<Interval>();
            listOfMaxIntervals.add(dateInterval);
            compositeMaxInterval.setIntervals(listOfMaxIntervals);
            compositeMaxInterval.setDomain(this);
            results.add(compositeMaxInterval);
        }
        return results;
    }

    public List<DateInterval> getListOfIntervals(int intervalMode, Date minDateLimit, Date maxDateLimit) {
        Calendar gc = GregorianCalendar.getInstance(/*LocaleManager.currentLocale()*/);
        gc.setLenient(false);
        gc.setTime(minDateLimit);

        // Calculate the first interval minimum date.
        // For instance: If you have a date like 14:24:13 23-Apr-2008 and the interval mode is MONTH,
        // this code round the date to 00:00:00 1-Apr-2008
        switch (intervalMode) {
            case INTERVAL_DECADE:  // Nothing to adjust.
            case INTERVAL_YEAR:    gc.add(Calendar.MONTH, gc.get(Calendar.MONTH) * -1);
            case INTERVAL_QUARTER: // Nothing to adjust.
            case INTERVAL_MONTH:   gc.add(Calendar.DAY_OF_MONTH, (gc.get(Calendar.DAY_OF_MONTH) - 1) * -1);
            case INTERVAL_WEEK:    // Nothing to adjust.
            case INTERVAL_DAY:     gc.add(Calendar.HOUR, gc.get(Calendar.HOUR_OF_DAY) * -1);
            case INTERVAL_HOUR:    gc.add(Calendar.MINUTE, gc.get(Calendar.MINUTE) * -1);
            case INTERVAL_MINUTE:  gc.add(Calendar.SECOND, gc.get(Calendar.SECOND) * -1);
            case INTERVAL_SECOND:  gc.add(Calendar.MILLISECOND, gc.get(Calendar.MILLISECOND) * -1);
        }

        List<DateInterval> resultIntervals = new ArrayList<DateInterval>();
        while (gc.getTime().compareTo(maxDateLimit) <= 0) {
            Date minDateRange = gc.getTime();
            switch (intervalMode) {
                case INTERVAL_DECADE: gc.add(Calendar.YEAR, 10); break;
                case INTERVAL_YEAR: gc.add(Calendar.YEAR, 1); break;
                case INTERVAL_QUARTER: gc.add(Calendar.MONTH, 3); break;
                case INTERVAL_MONTH: gc.add(Calendar.MONTH, 1); break;
                case INTERVAL_WEEK: gc.add(Calendar.DAY_OF_MONTH, 7); break;
                case INTERVAL_DAY: gc.add(Calendar.DAY_OF_MONTH, 1); break;
                case INTERVAL_HOUR: gc.add(Calendar.HOUR_OF_DAY, 1); break;
                case INTERVAL_MINUTE: gc.add(Calendar.MINUTE, 1);  break;
                case INTERVAL_SECOND: gc.add(Calendar.SECOND, 1); break;
            }
            // Create the interval instance.
            DateInterval interval = new DateInterval();
            interval.setMinDate(minDateRange);
            interval.setMinDateIncluded(true);
            interval.setMaxDate(gc.getTime());
            interval.setMaxDateIncluded(false);
            interval.setDomain(this);
            resultIntervals.add(interval);
        }
        // The last interval must include the maxDateLimit
        if (!resultIntervals.isEmpty()) {
            DateInterval lastInterval = resultIntervals.get(resultIntervals.size() - 1);
            lastInterval.setMaxDateIncluded(true);
        }
        return resultIntervals;
    }

    public int calculateDateIntervalMode(int maxIntevals, Date minDate, Date maxDate) {
        long millis = maxDate.getTime() - minDate.getTime();
        for (int i = 0; i < INTERVAL_DURATION_IN_SECONDS.length; i++) {
            long nintervals = (millis / 1000) / INTERVAL_DURATION_IN_SECONDS[i];
            if (nintervals < maxIntevals) return i;
        }
        return INTERVAL_DECADE;
    }

    public String toString(List<Interval> intervals) {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of ranges=").append(intervals.size()).append("\r\n");
        for (int i = 0; i < intervals.size(); i++) {
            DateInterval r = (DateInterval) intervals.get(i);
            sb.append("Interval ").append(i).append("=").append(r.getMinDate()).append(" TO ").append(r.getMaxDate()).append("\r\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DateDomain dd = new DateDomain();
        dd.setMaxNumberOfIntervals(10);

        System.out.println("Static domain: max/min limits fixed.");
        dd.setMinDate(new Date(System.currentTimeMillis() + INTERVAL_DURATION_IN_SECONDS[INTERVAL_SECOND]*1000));
        dd.setMaxDate(new Date(System.currentTimeMillis() + INTERVAL_DURATION_IN_SECONDS[INTERVAL_MONTH]*1000 * 6));
        System.out.println(dd.toString(dd.getIntervals()));

        System.out.println("Dynamic domain: init based on a set of dates.");
        List<Date> dates = new ArrayList<Date>();
        dates.add(new Date(System.currentTimeMillis() + INTERVAL_DURATION_IN_SECONDS[INTERVAL_SECOND]*1000));
        dates.add(new Date(System.currentTimeMillis() + INTERVAL_DURATION_IN_SECONDS[INTERVAL_DAY]*1000 * 6));
        dates.add(new Date(System.currentTimeMillis() + INTERVAL_DURATION_IN_SECONDS[INTERVAL_YEAR]*1000 * 1));
        dd.setMinDate(null);
        dd.setMaxDate(null);
        System.out.println(dd.toString(dd.getIntervals()));
    }
}

