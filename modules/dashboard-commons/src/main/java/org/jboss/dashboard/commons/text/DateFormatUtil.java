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
package org.jboss.dashboard.commons.text;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utilities for formatting and parsing dates.
 */
public class DateFormatUtil {

    private static final SimpleDateFormat shortdf;
    private static final SimpleDateFormat longdf;
    private static final SimpleDateFormat nativedf;
    private static final SimpleDateFormat hourdf;
    private static final SimpleDateFormat utcdf;

    /**
     * Init the formatters.
     */
    static {
        shortdf = new SimpleDateFormat("dd/MM/yyyy");
        longdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        nativedf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        hourdf = new SimpleDateFormat("HH:mm:ss");

        // setup x.208 generalized time formatter
        utcdf = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        // Time in UTC - must set formatter
        TimeZone tz = TimeZone.getTimeZone("UTC");
        utcdf.setTimeZone(tz);
    }

    /**
     * Constructs an empty object.
     * This constructor is private to prevent instantiating this class.
     */
    private DateFormatUtil() {
    }

    /**
     * Tells whether Timestamp is in long format: with hours, minutes and
     * seconds fields.
     *
     * @param date the time value to be tested
     * @return true if this date is in long format; false otherwise
     */
    public static boolean isLongDate(Timestamp date) {
        return date.toString().indexOf("00:00:00") == -1;
    }

    /**
     * Tells whether date/time string is in the format "dd/MM/yyyy".
     *
     * @param text the date/time string to be tested
     * @return true if this string is in "dd/MM/yyyy" format; false otherwise
     */
    public static boolean isValidDate(String text)
            throws ParseException {
        if (text == null || text.trim().equals(""))
            return true;

        text = text.trim();
        Timestamp date = strToTimestamp(text);

        if (date == null)
            return false;
        else {
            if (isLongDate(date))
                return longdf.format(date).equals(text);
            else
                return shortdf.format(date).equals(text);
        }
    }

    /**
     * Formats a Date into a time string. The result time string has the
     * pattern "HH:mm:ss"
     *
     * @param date the time value to be formatted into a time string.
     * @return the formatted time string.
     */
    public static String timeToStr(java.util.Date date) {
        if (date == null)
            date = new java.util.Date();

        return hourdf.format(date);
    }

    /**
     * Formats a Date into a date string. The result time string has the
     * pattern "dd/MM/yyyy"
     *
     * @param date the time value to be formatted into a date string.
     * @return the formatted date string.
     */
    public static String dateToStr(java.util.Date date) {
        if (date == null)
            date = new java.util.Date();

        return shortdf.format(date);
    }

    /**
     * Parse a date/time string. The date/time string must be in "dd/MM/yyyy"
     * format
     *
     * @param text The date/time string to be parsed
     * @return A Timestamp, or null if the input could not be parsed
     * @throws ParseException If the given string cannot be parsed as a date
     */
    public static Timestamp strToTimestamp(String text)
            throws ParseException {
        text = text.trim();

        return new Timestamp(shortdf.parse(text).getTime());
    }

    /**
     * Parse a native date/time string. The date/time string must be in
     * "yyyy/MM/dd" format
     *
     * @param text The date/time string to be parsed
     * @return A Timestamp, or null if the input could not be parsed
     * @throws ParseException If the given string cannot be parsed as a date
     */
    public static Timestamp nativeStrToTimestamp(String text)
            throws ParseException {
        text = text.trim();

        return new Timestamp(nativedf.parse(text).getTime());
    }

    /**
     * Turns a UTCTime in a java.util.Date object into a String object.
     *
     * @param UTCDate Date with a UTCTime date
     * @return Date
     */
    public static String dateToUTCStr(Date UTCDate)
            throws ParseException {
        if (UTCDate == null) {
            return "";
        }
        return utcdf.format(UTCDate);
    }

    /**
     * Turns a UTCTime in string format into a java.util.Date object
     *
     * @param UTCTime String with a UTCTime date
     * @return Date
     */
    public static Date utcStrToDate(String UTCTime)
            throws ParseException {
        if (UTCTime == null) {
            return new Date();
        }
        return utcdf.parse(UTCTime);
    }

    // --*-- String utilities from ExtString.java at util0.4.jar --*--

    /**
     * Contains some Oracle specific fixes to the timestamp.
     * Hopefully I've fixed it so it isn't incompatible with others now!
     * Also turns nulls into "null"s.
     *
     * @return the formatted time string.
     */
    public static String dateToOracleStr(java.sql.Timestamp ts) {
        StringBuffer tsString = new StringBuffer("null");
        if (ts != null) {
            tsString.append("to_date('")
                    .append(ts.toString().substring(0, 19))
                    .append("','YYYY-MM-DD HH24:MI:SS')");
        }

        return tsString.toString();
    }
    // --*-- End String utilities from ExtString.java at util0.4.jar --*--

}

