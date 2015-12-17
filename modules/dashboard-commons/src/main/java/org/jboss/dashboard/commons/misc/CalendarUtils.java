/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.commons.misc;

import org.jboss.dashboard.LocaleManager;
import org.jfree.data.time.Quarter;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class CalendarUtils {

    /**
     * Get calendar instance using platform current locale.
     * @return The calendar instance.
     */
    public static Calendar getInstance() {
        return Calendar.getInstance(LocaleManager.currentLocale());
    }

    public static class CalendarRangeUtils {

        public static Calendar setAtDayStart(Calendar calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            return calendar;
        }

        public static Calendar setAtDayEnd(Calendar calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            return calendar;
        }

        public static Calendar[] getLastHour(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.HOUR, -1);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getLast12Hours(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.HOUR, -12);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getToday(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            setAtDayStart(calendarFrom);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getYesterday(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.DAY_OF_WEEK, -1);
            setAtDayStart(calendarFrom);
            Calendar calendarTo = (Calendar) referenceCalendar.clone();
            calendarTo.add(Calendar.DAY_OF_WEEK, -1);
            setAtDayEnd(calendarTo);
            return new Calendar[] {calendarFrom, calendarTo};
        }

        public static Calendar[] getLast7Days(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.DAY_OF_WEEK, -7);
            setAtDayStart(calendarFrom);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getThisMonth(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.set(Calendar.DAY_OF_MONTH, 1);
            setAtDayStart(calendarFrom);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getLastMonth(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.MONTH, -1);
            calendarFrom.set(Calendar.DAY_OF_MONTH,1);
            setAtDayStart(calendarFrom);
            Calendar calendarTo = (Calendar) referenceCalendar.clone();
            calendarTo.add(Calendar.MONTH, -1);
            calendarTo.set(Calendar.DAY_OF_MONTH, calendarTo.getActualMaximum(Calendar.DAY_OF_MONTH));
            setAtDayEnd(calendarTo);
            return new Calendar[] {calendarFrom, calendarTo};
        }

        public static Calendar[] getThisQuarter(Calendar referenceCalendar) {
            Quarter quarter = new Quarter(referenceCalendar.getTime());
            Calendar startCal = getInstance();
            Calendar endCal = getInstance();
            long start = quarter.getFirstMillisecond(getInstance());
            long end = quarter.getLastMillisecond(getInstance());
            startCal.setTimeInMillis(start);
            endCal.setTimeInMillis(end);
            return new Calendar[] {startCal, endCal};
        }

        public static Calendar[] getLastQuarter(Calendar referenceCalendar) {
            Quarter quarter = new Quarter(referenceCalendar.getTime());
            Calendar startCal = getInstance();
            Calendar endCal = getInstance();
            long start = quarter.previous().getFirstMillisecond(getInstance());
            long end = quarter.previous().getLastMillisecond(getInstance());
            startCal.setTimeInMillis(start);
            endCal.setTimeInMillis(end);
            return new Calendar[] {startCal, endCal};
        }

        public static Calendar[] getLast6Months(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.MONTH, -6);
            setAtDayStart(calendarFrom);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getThisYear(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.set(Calendar.MONTH, calendarFrom.getActualMinimum(Calendar.MONTH));
            calendarFrom.set(Calendar.DAY_OF_MONTH, 1);
            setAtDayStart(calendarFrom);
            return new Calendar[] {calendarFrom, referenceCalendar};
        }

        public static Calendar[] getLastYear(Calendar referenceCalendar) {
            Calendar calendarFrom = (Calendar) referenceCalendar.clone();
            calendarFrom.add(Calendar.YEAR, -1);
            calendarFrom.set(Calendar.MONTH, calendarFrom.getActualMinimum(Calendar.MONTH));
            calendarFrom.set(Calendar.DAY_OF_MONTH, 1);
            setAtDayStart(calendarFrom);
            Calendar calendarTo = (Calendar) referenceCalendar.clone();
            calendarTo.add(Calendar.YEAR, -1);
            calendarTo.set(Calendar.MONTH, calendarTo.getActualMaximum(Calendar.MONTH));
            calendarTo.set(Calendar.DAY_OF_MONTH, calendarTo.getActualMaximum(Calendar.DAY_OF_MONTH));
            setAtDayEnd(calendarTo);
            return new Calendar[] {calendarFrom, calendarTo};
        }
    }

    // Testing.

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance(new Locale("es"));
        System.out.println("\nLast hour: ");
        print(CalendarRangeUtils.getLastHour(c));
        System.out.println("\nLast 12 hours: ");
        print(CalendarRangeUtils.getLast12Hours(c));
        System.out.println("\ntoday: ");
        print(CalendarRangeUtils.getToday(c));
        System.out.println("\nyesterday: ");
        print(CalendarRangeUtils.getYesterday(c));
        System.out.println("\nLast 7days: ");
        print(CalendarRangeUtils.getLast7Days(c));
        System.out.println("\nthis month: ");
        print(CalendarRangeUtils.getThisMonth(c));
        System.out.println("\nlast month: ");
        print(CalendarRangeUtils.getLastMonth(c));
        System.out.println("\nThis quarter: ");
        print(CalendarRangeUtils.getThisQuarter(c));
        System.out.println("\nLast quarter: ");
        print(CalendarRangeUtils.getLastQuarter(c));
        System.out.println("\nLast 6 months: ");
        print(CalendarRangeUtils.getLast6Months(c));
        System.out.println("\nthis year: ");
        print(CalendarRangeUtils.getThisYear(c));
        System.out.println("\nlast year: ");
        print(CalendarRangeUtils.getLastYear(c));
    }

    protected static void print(Calendar[] calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.print(" - FROM: " + sdf.format(calendar[0].getTime()) + " - TO: " + sdf.format(calendar[1].getTime()));
    }
}
