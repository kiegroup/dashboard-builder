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
package org.jboss.dashboard.dataset.csv;

import org.jboss.dashboard.dataset.AbstractDataSet;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.csv.CSVDataLoader;
import org.jboss.dashboard.provider.csv.CSVDataProperty;
import org.apache.commons.lang.StringUtils;
import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.*;
import java.util.List;


/**
 * A data set implementation that holds as a matrix in-memory all the rows returned by a given SQL query executed
 * against a local or remote database.
 */
public class CSVDataSet extends AbstractDataSet {

    protected CSVReader csvReader;
    protected CSVDataLoader csvLoader;
    protected transient DateFormat _dateFormat;
    protected transient DecimalFormat _numberFormat;

    /** Logger */
    protected transient static Log log = LogFactory.getLog(CSVDataSet.class);

    public CSVDataSet(DataProvider provider, CSVDataLoader loader) {
        super(provider);
        this.csvLoader = loader;
        DecimalFormatSymbols numberSymbols = new DecimalFormatSymbols();
        numberSymbols.setGroupingSeparator(csvLoader.getCsvNumberGroupSeparator());
        numberSymbols.setDecimalSeparator(csvLoader.getCsvNumberDecimalSeparator());
        this._numberFormat = new DecimalFormat("#,##0.00", numberSymbols);
        this._dateFormat = new SimpleDateFormat(csvLoader.getCsvDatePattern());
    }

    public CSVDataProperty createCSVProperty() {
        return new CSVDataProperty();
    }

    public void load() throws Exception {
        try {
            File f = csvLoader.getCsvProviderFile();
            if (f == null || !f.exists() || !f.canRead()) {
                throw new IOException("Can't load data from file : '" + f + "'");
            }

            // Read the header
            FileReader fileReader = new FileReader(f);
            BufferedReader br = new BufferedReader(fileReader);
            csvReader = new CSVReader(br, csvLoader.getCsvSeparatedBy().charAt(0), csvLoader.getCsvQuoteChar().charAt(0), csvLoader.getCsvEscapeChar().charAt(0));
            List<String[]> lines = csvReader.readAll();
            String[] header = lines.get(0);
            String[] firstRow = lines.get(1);

            // Build the CSV data set properties
            setPropertySize(header.length);
            for (int i = 0; i < firstRow.length; i++) {
                String token = header[i];
                String value = firstRow[i];
                Domain domain = calculateDomain(value);
                CSVDataProperty dp = createCSVProperty();
                dp.setPropertyId(token.toLowerCase());
                dp.setDomain(domain);
                addProperty(dp, i);
            }

            // Load the CSV rows
            for (int lc = 1; lc < lines.size(); lc++) {
                String[] line = lines.get(lc);
                Object[] row = new Object[header.length];
                for (int i = 0; i < line.length; i++) {
                    String valueStr = line[i];
                    CSVDataProperty prop = (CSVDataProperty) getProperties()[i];
                    if (!StringUtils.isBlank(valueStr)){
                        row[i] = parseValue(prop, valueStr);
                    } else {
                        row[i] = null;
                    }
                }
                this.addRowValues(row);
            }

        } catch (Exception e) {
            log.error("Error loading CSV data.", e);
            throw e;
        }
    }

    public Domain calculateDomain(String value) {
        try {
            _dateFormat.parse(value);
            return new DateDomain();
        } catch (Exception e) {
            try {
                _numberFormat.parse(value);
                return new NumericDomain();
            } catch (Exception ee) {
                return new LabelDomain();
            }
        }
    }

    public Object parseValue(CSVDataProperty prop, String value) throws Exception {
        Domain domain = prop.getDomain();
        try {
            if (domain instanceof DateDomain) {
                return _dateFormat.parse(value);
            } else if (domain instanceof NumericDomain) {
                return new Double(_numberFormat.parse(value).doubleValue());
            } else return value;
        } catch (ParseException e) {
            String msg = "Error parsing value: " + value + ", " + e.getMessage() + ". Check column\u0027s data type consistency!";
            log.error(msg);
            throw new Exception(msg);
        }
    }
}
