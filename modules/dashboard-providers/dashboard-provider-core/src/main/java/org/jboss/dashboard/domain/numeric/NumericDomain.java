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
package org.jboss.dashboard.domain.numeric;

import org.jboss.dashboard.domain.AbstractDomain;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.util.*;

/**
 * This domain is used for properties that contains numeric values.
 * The intervals of a numeric domain are 10-multiple numeric ranges (billions, millions, tens, ...)
 * The numeric ranges used by the data displayer it depends on both the configuration of the
 * domain property and the data set values to visualize.  
 */
public class NumericDomain extends AbstractDomain {

    // i18n
    public static final String I18N_PREFFIX = "numericDomain.";

    // Numeric range modes.
    public static final int INTERVAL_MIL_MILLONESIMA    = 0;
    public static final int INTERVAL_MILLONESIMA        = 1;
    public static final int INTERVAL_CIENMILESIMA       = 2;
    public static final int INTERVAL_DIEZMILESIMA       = 3;
    public static final int INTERVAL_MILESIMA           = 4;
    public static final int INTERVAL_CENTESIMA          = 5;
    public static final int INTERVAL_DECIMA             = 6;
    public static final int INTERVAL_UNIDAD             = 7;
    public static final int INTERVAL_DECENA             = 8;
    public static final int INTERVAL_CENTENA            = 9;
    public static final int INTERVAL_UNIDAD_DE_MILLAR   = 10;
    public static final int INTERVAL_DECENA_DE_MILLAR   = 11;
    public static final int INTERVAL_CENTENA_DE_MILLAR  = 12;
    public static final int INTERVAL_MILLON             = 13;
    public static final int INTERVAL_BILLON             = 14;

    public static final long[] INTERVAL_EQUIVALENCE_IN_UNITS = new long[] {
        1/10/10/10/10/10/10/10/10/10, /* INTERVAL_MIL_MILLONESIMA = 0; */
        1/10/10/10/10/10/10,  /* INTERVAL_MILLONESIMA = 1; */
        1/10/10/10/10/10,  /* INTERVAL_CIENMILESIMA = 2; */
        1/10/10/10/10, /* INTERVAL_DIEZMILESIMA = 3; */
        1/10/10/10, /* INTERVAL_MILESIMA = 4; */
        1/10/10, /* INTERVAL_CENTESIMA = 5; */
        1/10, /* INTERVAL_DECIMA = 6; */

        1, /* INTERVAL_UNIDAD = 7; */
        10, /* INTERVAL_DECENA = 8; */
        10*10, /* INTERVAL_CENTENA = 9; */
        10*10*10, /* INTERVAL_UNIDAD_DE_MILLAR = 10; */
        10*10*10*10, /* INTERVAL_DECENA_DE_MILLAR = 11; */
        10*10*10*10*10,/* INTERVAL_CENTENA_DE_MILLAR = 12; */
        10*10*10*10*10*10, /* /* INTERVAL_MILLON = 13; */
        10*10*10*10*10*10*10*10*10, /* INTERVAL_BILLON = 14; */};

    protected Number maxValue;
    protected Number minValue;
    protected int tamInterval;
    protected int intervalMode;

    public NumericDomain() {
        super();
        maxValue = null;
        minValue = null;
        tamInterval = -1;
        intervalMode = -1;
    }

    public Class getValuesClass() {
        return java.lang.Number.class;
    }

    public boolean isScalarFunctionSupported(ScalarFunction sf) {
        return sf.isTypeSupported(Number.class);
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
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

    public Interval[] getIntervals() {
        // Purge the null values present in data (f.i: null values in a SQL column)
        List domainValues = property.getValues();
        Iterator it = domainValues.iterator();
        while (it.hasNext()) if (it.next() == null) it.remove();

        // Calculate the data low & upper limits.
        Number absoluteMinValue = null;
        Number absoluteMaxValue = null;
        if (!domainValues.isEmpty()) {
            Collections.sort(domainValues);
            absoluteMinValue = (Number) domainValues.get(0);
            absoluteMaxValue = (Number) domainValues.get(domainValues.size() - 1);
        }

        // Ranges can not be created if limits are not defined.
        Number minValueLimit = minValue != null ? minValue : absoluteMinValue;
        Number maxValueLimit = maxValue != null ? maxValue : absoluteMaxValue;
        if (minValueLimit == null || maxValueLimit == null) return new Interval[] {};
        if (minValueLimit.longValue() > maxValueLimit.longValue()) return new Interval[] {};

        // If min/max are equals then create a single interval.
        if (minValueLimit != null && minValueLimit.longValue() == maxValueLimit.longValue()) {
            NumericInterval interval = new NumericInterval();
            interval.setMinValue(minValueLimit);
            interval.setMaxValue(minValueLimit);
            interval.setMinValueIncluded(true);
            interval.setMaxValueIncluded(true);
            interval.setDomain(this);
            return new Interval[] {interval};
        }

        // Create the intervals according to the min/max dates specified.
        if (maxNumberOfIntervals < 0) maxNumberOfIntervals = 10;
        int intervalMode = calculateNumericIntervalMode(maxNumberOfIntervals, minValueLimit, maxValueLimit);

        // Ensure the interval mode obtained is always greater or equals than the preferred interval size set by the user.
        if (tamInterval != -1 && intervalMode < tamInterval) intervalMode = tamInterval;

        // Don't exceed the maximum number of intervals. In case there are more intervals than the maximum number, increase the interval mode.
        List listOfIntervals = getListOfIntervals(intervalMode, minValueLimit, maxValueLimit);

        // If there are values before the minValue, create the initial composite interval.
        List intervals = new ArrayList();
        if (minValue != null && absoluteMinValue.longValue() < minValue.longValue()) {
            // New date interval.
            NumericInterval numericInterval = new NumericInterval();
            numericInterval.setMinValue(absoluteMinValue);
            numericInterval.setMaxValue(minValue);
            numericInterval.setMinValueIncluded(true);
            numericInterval.setMaxValueIncluded(false);

            // New composite interval.
            CompositeInterval compositeMinInterval = new CompositeInterval();
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (int i=0; i<locales.length; i++) {
                Locale l = locales[i];
                compositeMinInterval.setDescription("< " + ((NumericInterval) listOfIntervals.get(0)).getDescription(l), l);
            }
            Set listOfMinIntervals = new HashSet();
            listOfMinIntervals.add(numericInterval);
            compositeMinInterval.setIntervals(listOfMinIntervals);
            compositeMinInterval.setDomain(this);
            intervals.add(compositeMinInterval);
        }
        // Add the list of intervals.
        intervals.addAll(listOfIntervals);

        // If there are values after the maxValue, create the final composite interval.
        if (maxValue != null && absoluteMaxValue.longValue() > maxValue.longValue()) {
            // New date interval.
            NumericInterval numericInterval = new NumericInterval();
            numericInterval.setMinValue(maxValue);
            numericInterval.setMaxValue(absoluteMaxValue);
            numericInterval.setMinValueIncluded(false);
            numericInterval.setMaxValueIncluded(true);

            // New composite interval.
            CompositeInterval compositeMaxInterval = new CompositeInterval();
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (int i=0; i<locales.length; i++) {
                Locale l = locales[i];
                compositeMaxInterval.setDescription("> " + ((NumericInterval) listOfIntervals.get(listOfIntervals.size()-1)).getDescription(l), l);
            }
            Set listOfMaxIntervals = new HashSet();
            listOfMaxIntervals.add(numericInterval);
            compositeMaxInterval.setIntervals(listOfMaxIntervals);
            compositeMaxInterval.setDomain(this);
            intervals.add(compositeMaxInterval);
        }

        Interval[] results = new Interval[intervals.size()];
        for (int i = 0; i < results.length; i++) results[i] = (Interval) intervals.get(i);
        return results;
    }

    public List getListOfIntervals(int intervalMode, Number minValueLimit, Number maxValueLimit) {
        // Calculate the first interval minimum numeric. For instance: If you have a number like 255639 and the interval mode is CENTENAS_DE_MILLAR,
        // this code round the number to 200000. 255639/100000 = 2; 2*100000 = 200000
        long minInterval = minValueLimit.longValue();
        long minIntervalResult = 0;
        switch (intervalMode) {
            case INTERVAL_BILLON:               minIntervalResult = ((minInterval/1000000000)*1000000000); break;
            case INTERVAL_MILLON:               minIntervalResult = ((minInterval/1000000)*1000000); break;
            case INTERVAL_CENTENA_DE_MILLAR:    minIntervalResult = ((minInterval/100000)*100000); break;
            case INTERVAL_DECENA_DE_MILLAR:     minIntervalResult = ((minInterval/10000)*10000); break;
            case INTERVAL_UNIDAD_DE_MILLAR:     minIntervalResult = ((minInterval/1000)*1000); break;
            case INTERVAL_CENTENA:              minIntervalResult = ((minInterval/100)*100); break;
            case INTERVAL_DECENA:               minIntervalResult = ((minInterval/10)*10);  break;

            case INTERVAL_UNIDAD:               minIntervalResult = (minInterval/1); break;

            case INTERVAL_DECIMA:               minIntervalResult = (((minInterval*10)*1000000000)/1000000000); break;
            case INTERVAL_CENTESIMA:            minIntervalResult = (((minInterval*100)*1000000000)/1000000000); break;
            case INTERVAL_MILESIMA:             minIntervalResult = (((minInterval*1000)*1000000000)/1000000000); break;
            case INTERVAL_DIEZMILESIMA:         minIntervalResult = (((minInterval*10000)*1000000000)/1000000000); break;
            case INTERVAL_CIENMILESIMA:         minIntervalResult = (((minInterval*100000)*1000000000)/1000000000); break;
            case INTERVAL_MILLONESIMA:          minIntervalResult = (((minInterval*1000000)*1000000000)/1000000000); break;
            case INTERVAL_MIL_MILLONESIMA:      minIntervalResult = (((minInterval*1000000000)*1000000000)/1000000000); break;
        }

        // Loop until all intervals are created.
        List intervals = new ArrayList();
        long count = minIntervalResult;
        while (count <= maxValueLimit.longValue()) {
            // Create the interval instance.
            NumericInterval interval = new NumericInterval();
            interval.setMinValue(new Long(count));
            interval.setMinValueIncluded(true);
            long min = count;
            long max = 0;
            switch (intervalMode) {
                case INTERVAL_BILLON:               max = (((min/1000000000)+1)*1000000000); break;
                case INTERVAL_MILLON:               max = (((min/1000000)+1)*1000000); break;
                case INTERVAL_CENTENA_DE_MILLAR:    max = (((min/100000)+1)*100000); break;
                case INTERVAL_DECENA_DE_MILLAR:     max = (((min/10000)+1)*10000); break;
                case INTERVAL_UNIDAD_DE_MILLAR:     max = (((min/1000)+1)*1000); break;
                case INTERVAL_CENTENA:              max = (((min/100)+1)*100); break;
                case INTERVAL_DECENA:               max = (((min/10)+1)*10); break;

                case INTERVAL_UNIDAD:               max = (((min/1)+1)*1); break;

                case INTERVAL_DECIMA:               max = ((((min*10)+1)*1000000000)/1000000000); break;
                case INTERVAL_CENTESIMA:            max = ((((min*100)+1)*1000000000)/1000000000); break;
                case INTERVAL_MILESIMA:             max = ((((min*1000)+1)*1000000000)/1000000000); break;
                case INTERVAL_DIEZMILESIMA:         max = ((((min*10000)+1)*1000000000)/1000000000); break;
                case INTERVAL_CIENMILESIMA:         max = ((((min*100000)+1)*1000000000)/1000000000); break;
                case INTERVAL_MILLONESIMA:          max = ((((min*1000000)+1)*1000000000)/1000000000); break;
                case INTERVAL_MIL_MILLONESIMA:      max = ((((min*1000000000)+1)*1000000000)/1000000000); break;
            }
            interval.setMaxValue(new Long(max));
            interval.setMaxValueIncluded(false);
            interval.setDomain(this);
            intervals.add(interval);

            count = max;
        }
        // The last interval must include the maxDateLimit
        if (!intervals.isEmpty()) {
            NumericInterval lastInterval = (NumericInterval) intervals.get(intervals.size() - 1);
            lastInterval.setMaxValueIncluded(true);
        }
        return intervals;
    }

    public int calculateNumericIntervalMode(int maxIntevals, Number minValue, Number maxValue) {
        double differenceValue = maxValue.doubleValue() - minValue.doubleValue();
        for (int i = 0; i < INTERVAL_EQUIVALENCE_IN_UNITS.length; i++) {
            double nintervals = differenceValue / INTERVAL_EQUIVALENCE_IN_UNITS[i];
            if (nintervals < maxIntevals) return i;
        }
        return INTERVAL_BILLON;
    }

    public String toString(Interval[] intervals) {
        StringBuffer buf = new StringBuffer();
        buf.append("Number of ranges=" + intervals.length).append("\r\n");
        for (int i = 0; i < intervals.length; i++) {
            NumericInterval r = (NumericInterval) intervals[i];
            buf.append("Interval ").append(i).append("=").append(r.getMinValue()).append(" TO ").append(r.getMaxValue()).append("\r\n");
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        NumericDomain dd = new NumericDomain();
        dd.setMaxNumberOfIntervals(10);

        System.out.println("Static domain: max/min limits fixed.");
        dd.setMinValue(new Long(5569238));
        System.out.println("5569238/1000000 = " + (dd.getMinValue().longValue()/1000000)*1000000);
        dd.setMaxValue(new Double(12469555));
        System.out.println(dd.toString(dd.getIntervals()));

        System.out.println("Dynamic domain: init based on a set of dates.");
        List numbers = new ArrayList();
        numbers.add(new Double(123456));
        numbers.add(new Double(120333));
        numbers.add(new Double(125896));
        dd.setMinValue(null);
        dd.setMaxValue(null);
        System.out.println(dd.toString(dd.getIntervals()));
    }
}