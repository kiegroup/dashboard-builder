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
package org.jboss.dashboard.dataset;

import java.util.Locale;

import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataPropertyFormatter;

import static org.fest.assertions.api.Assertions.*;

public class Assertions {

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param x The x position of the cell to check (starting at 0).
     * @param y The y position of the cell to check (starting at 0).
     * @param expected The expected value in the given cell.
     */
    public static void assertDataSetValue(DataSet dataSet, int x, int y, String expected) {
        Locale locale = Locale.ENGLISH;
        DataFormatterRegistry dataFormatterRegistry = DataFormatterRegistry.lookup();
        Object value = dataSet.getValueAt(x,y);

        DataProperty prop = dataSet.getPropertyByColumn(y);
        DataPropertyFormatter propFormatter = dataFormatterRegistry.getPropertyFormatter(prop.getPropertyId());
        String displayedValue = propFormatter.formatValue(prop, value, locale);
        if (!displayedValue.equals(expected)) {
            fail("Data set value [" + x + "," + y + "] is different. " +
                    "Column=\"" + prop.getPropertyId() + "\" " +
                    "Actual=\"" + displayedValue + "\" Expected=\"" + expected + "\"");
        }
    }

    /**
     * Check if some data set rows match a given result.
     * @param dataSet The data set to validate.
     * @param expected The expected row values.
     * @param index The starting data set row index where the comparison starts.
     */
    public static void assertDataSetValues(DataSet dataSet, String[][] expected, int index) {
        Locale locale = Locale.ENGLISH;
        DataFormatterRegistry dataFormatterRegistry = DataFormatterRegistry.lookup();

        // Check size
        assertThat(dataSet.getRowCount()).isGreaterThan(index);

        for (int i = index; i < expected.length; i++) {
            String[] row = expected[i];

            // Check row values
            for (int j = 0; j < row.length; j++) {
                Object dataSetValue = dataSet.getValueAt(i, j);
                DataProperty prop = dataSet.getPropertyByColumn(j);

                String expectedValue = row[j];
                if (expectedValue == null) continue;

                // Compare the data set value with the value the user is expecting to see.
                DataPropertyFormatter propFormatter = dataFormatterRegistry.getPropertyFormatter(prop.getPropertyId());
                String displayedValue = propFormatter.formatValue(prop, dataSetValue, locale);
                if (!displayedValue.equals(expectedValue)) {
                    fail("Data set value [" + i + "," + j + "] is different. " +
                            "Column=\"" + prop.getPropertyId() + "\" " +
                            "Actual=\"" + displayedValue + "\" Expected=\"" + expectedValue + "\"");
                }
            }
        }
    }
}
