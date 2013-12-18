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
package org.jboss.dashboard.dataset;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DefaultDataProperty;

public class RawDataSet implements Serializable {

    protected String numberPattern;
    protected String datePattern;
    protected String[][] data;
    protected Class[] types;
    protected String[] columnIds;
    protected transient DateFormat _dateFormat;
    protected transient DecimalFormat _numberFormat;

    public static final List SUPPORTED_TYPES = Arrays.asList(String.class, Double.class, Integer.class, Date.class);

    public RawDataSet(String[] columnIds, Class[] types, String numberPattern, String datePattern, String[][] data) {
        this.columnIds = columnIds;
        this.types = types;
        this.data = data;
        this.numberPattern = numberPattern;
        this.datePattern = datePattern;
        DecimalFormatSymbols numberSymbols = new DecimalFormatSymbols();
        numberSymbols.setGroupingSeparator(getNumberGroupSeparator());
        numberSymbols.setDecimalSeparator(getNumberDecimalSeparator());

        this._numberFormat = new DecimalFormat(numberPattern, numberSymbols);
        this._dateFormat = new SimpleDateFormat(datePattern);

        for (Class type : types) {
            if (!SUPPORTED_TYPES.contains(type)) {
                throw new IllegalArgumentException("Type not supported: " + type);
            }
        }
    }

    public char getNumberGroupSeparator() {
        if (numberPattern.length() < 2) return ',';
        else return numberPattern.charAt(1);
    }

    public char getNumberDecimalSeparator() {
        if (numberPattern.length() < 6) return '.';
        else return numberPattern.charAt(5);
    }

    public String getRawValueAt(int x, int y) {
        if (x >= data.length) throw new IndexOutOfBoundsException("Max row index allowed: " + (data.length-1));
        String[] row = data[x];
        if (y >= row.length) throw new IndexOutOfBoundsException("Max column index allowed: " + (row.length-1));
        return row[y];
    }


    public Object parseValue(String rawValue, Class type) throws ParseException {
        if (Date.class.isAssignableFrom(type)) {
            return _dateFormat.parse(rawValue);
        }
        if (Number.class.isAssignableFrom(type)) {
            return _numberFormat.parse(rawValue).doubleValue();
        }
        return rawValue;
    }

    public DataSet toDataSet() throws ParseException {
        DefaultDataSet dataSet = new DefaultDataSet();
        dataSet.setPropertySize(columnIds.length);
        for (int i = 0; i < columnIds.length; i++) {
            DataProperty prop = createProperty(i);
            dataSet.addProperty(prop, i);
        }

        for (int i = 0; i < data.length; i++) {
            String[] rawRow = data[i];
            Object[] row = new Object[rawRow.length];
            for (int j = 0; j < rawRow.length; j++) {
                String rawValue = rawRow[j];
                row[j] = parseValue(rawValue, types[j]);
            }
            dataSet.addRowValues(row);
        }
        return dataSet;
    }

    public DataProperty createProperty(int index) {
        DataProperty prop = new DefaultDataProperty();
        prop.setPropertyId(columnIds[index]);
        prop.setDomain(createDomain(types[index]));
        prop.setName(columnIds[index], Locale.ENGLISH);
        return prop;
    }

    public Domain createDomain(Class type) {
        if (Date.class.isAssignableFrom(type)) return new DateDomain();
        if (Number.class.isAssignableFrom(type)) return new NumericDomain();
        return new LabelDomain();
    }
}
