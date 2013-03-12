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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Utilities for formating and parsing decimal numbers.
 */
public class DecimalFormatUtil {

    /**
     * Constructs an empty object.
     * This constructor is private to prevent instantiating this class.
     */
    private DecimalFormatUtil() {
    }

    /**
     * Formats a double to produce a string with a number of fraction
     * digits.
     *
     * @param origin   double to format
     * @param decimals number of fraction digits
     * @return Formatted string.
     */
    public static String doubleToStr(double origin, int decimals) {
        return getFormat(decimals).format(new Double(origin));
    }

    /**
     * Formats a Number to produce a string with a number of fraction
     * digits.
     *
     * @param origin   Number to format
     * @param decimals number of fraction digits
     * @return Formatted string.
     */
    public static String doubleToStr(Number origin, int decimals) {
        return getFormat(decimals).format(origin);
    }

    /**
     * Parses a string to produce a Double with a number of fraction
     * digits.
     *
     * @param text     string to parse
     * @param decimals number of fraction digits
     * @return Double parsed from string. In case of error, returns null.
     */
    public static Double strToDouble(String text, int decimals) {
        try {
            return new Double(getFormat(decimals).parse(text).doubleValue());
        } catch (Exception except) {
            except.printStackTrace();
            return null;
        }
    }

    /**
     * Parses a string to produce a Float with a number of fraction
     * digits.
     *
     * @param text     string to parse
     * @param decimals number of fraction digits
     * @return Float parsed from string. In case of error, returns null.
     */
    public static Float strToFloat(String text, int decimals) {
        try {
            return new Float(getFormat(decimals).parse(text).floatValue());
        } catch (Exception except) {
            except.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the decimal formatter, which has a number of decimals.
     *
     * @param decimals number of decimals
     * @return desired DecimalFormat
     */
    private static DecimalFormat getFormat(int decimals) {
        DecimalFormat df;
        df = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols dsym = df.getDecimalFormatSymbols();
        char decimalSeparator = dsym.getDecimalSeparator();

        StringBuffer pattern = new StringBuffer("0");
        pattern.append(decimalSeparator);
        for (int i = 0; i < decimals; i++)
            pattern.append("0");

        DecimalFormat numberFormat = new DecimalFormat(pattern.toString());
        DecimalFormatSymbols decimalSymb;
        decimalSymb = numberFormat.getDecimalFormatSymbols();

        numberFormat.setGroupingSize(50);
        decimalSymb.setDecimalSeparator(decimalSeparator);
        numberFormat.setDecimalFormatSymbols(decimalSymb);

        return numberFormat;
    }

    /**
     * Tell whether a double string is in valid format, according with
     * a number of fraction digits.
     *
     * @param value    the double string to be tested
     * @param decimals number of decimals
     * @return true if this double string is valid; false otherwise
     */
    public static boolean isValidDouble(String value, int decimals) {
        DecimalFormat df;
        df = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols dsym = df.getDecimalFormatSymbols();
        char decSepar = dsym.getDecimalSeparator();

        if (value == null || value.trim().equals(""))
            return true;

        value = value.trim();

        if (value.indexOf(decSepar) != value.lastIndexOf(decSepar))
            return false;

        if (value.indexOf(decSepar) != -1 &&
                decimals != -1 &&
                (value.length() - value.indexOf(decSepar) - 1) > decimals)
            return false;

        DecimalFormat numberFormat = getFormat(decimals);

        try {
            ParsePosition pos = new ParsePosition(0);
            Number number = numberFormat.parse(value, pos);
            if (number == null || pos.getIndex() != value.length())
                return false;

            return true;
        } catch (Exception except) {
            return false;
        }
    }

    /**
     * Tell whether a float string is in valid format, according with
     * a number of fraction digits.
     *
     * @param value    the float string to be tested
     * @param decimals number of decimals
     * @return true if this float string is valid; false otherwise
     */
    public static boolean isValidFloat(String value, int decimals) {
        return isValidDouble(value, decimals);
    }
}

