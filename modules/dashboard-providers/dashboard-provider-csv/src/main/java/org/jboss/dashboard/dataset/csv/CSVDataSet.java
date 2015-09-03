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
import org.jboss.dashboard.dataset.profiler.DataSetLoadConstraints;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.profiler.memory.MemoryProfiler;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.csv.CSVDataLoader;
import org.jboss.dashboard.provider.csv.CSVDataProperty;
import org.apache.commons.lang3.StringUtils;
import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.*;
import java.util.HashMap;
import java.util.Map;

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
    protected transient static Logger log = LoggerFactory.getLogger(CSVDataSet.class);

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
        CSVReadTrace trace = new CSVReadTrace(csvLoader);
        trace.addRuntimeConstraint(new DataSetLoadConstraints(this));
        trace.begin();
        try {
            // Check file existence.
            File f = csvLoader.getCsvProviderFile();
            if (f == null || !f.exists() || !f.canRead()) {
                throw new IOException("Can't load data from file: '" + f + "'");
            }

            // Read the header
            FileReader fileReader = new FileReader(f);
            BufferedReader br = new BufferedReader(fileReader);
            csvReader = new CSVReader(br, csvLoader.getCsvSeparatedBy().charAt(0), csvLoader.getCsvQuoteChar().charAt(0), csvLoader.getCsvEscapeChar().charAt(0));

            String[] header = csvReader.readNext();
            if (header == null) throw new IOException("The CSV file has no header: '" + f + "'");

            String[] firstRow = csvReader.readNext();
            if (firstRow == null || firstRow.length < header.length) firstRow = null;

            // Build the data set properties
            setPropertySize(header.length);
            for (int i = 0; i < header.length; i++) {
                String token = header[i];
                Domain domain = (firstRow != null ? calculateDomain(firstRow[i]) : new LabelDomain());
                CSVDataProperty dp = createCSVProperty();
                dp.setPropertyId(token.toLowerCase());
                dp.setDomain(domain);
                addProperty(dp, i);
            }

            // Load the CSV rows
            if (firstRow != null) {
                Object[] row = processLine(firstRow);
                this.addRowValues(row);
                String[] line = csvReader.readNext();
                while (line != null) {
                    // Read 10,000 lines
                    for (int i = 0; line != null && i < 10000; i++) {
                        row = processLine(line);
                        this.addRowValues(row);
                        line = csvReader.readNext();
                    }
                    // Check load constraints (every 10,000 rows)
                    trace.update(this);
                    trace.checkRuntimeConstraints();
                }
            }
        } finally {
            trace.end();
        }
    }

    protected Object[] processLine(String[] line) throws Exception {
        Object[] row = new Object[line.length];
        for (int j=0; j<line.length; j++) {
            String valueStr = line[j];
            CSVDataProperty prop = (CSVDataProperty) getProperties()[j];
            if (!StringUtils.isBlank(valueStr)){
                row[j] = parseValue(prop, valueStr);
            } else {
                row[j] = null;
            }
        }
        return row;
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

    public class CSVReadTrace extends CodeBlockTrace {

        protected Map<String,Object> context;

        public CSVReadTrace(CSVDataLoader dataLoader) {
            super("csv-read" + dataLoader.getFileURL());
            context = new HashMap<String,Object>();
            context.put("Description", "CSV Read - " + csvLoader.getFileURL());
            context.put("File URL", dataLoader.getFileURL());
            context.put("Quote char", dataLoader.getCsvQuoteChar());
            context.put("Separator", dataLoader.getCsvSeparatedBy());
            context.put("Escape char", dataLoader.getCsvEscapeChar());
            context.put("Date pattern", dataLoader.getCsvDatePattern());
            context.put("Number pattern", dataLoader.getCsvNumberPattern());
            context.put("Number decimal separator", dataLoader.getCsvNumberDecimalSeparator());
            context.put("Number group separator", dataLoader.getCsvNumberGroupSeparator());
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.CSV;
        }

        public String getDescription() {
            return (String) context.get("Description");
        }

        public Map<String,Object> getContext() {
            return context;
        }

        public void update(CSVDataSet dataSet) {
            context.put("Data set #columns", dataSet.getProperties().length);
            context.put("Data set #rows", dataSet.getRowCount());
            context.put("Data set size", MemoryProfiler.formatSize(dataSet.sizeOf()));
        }
    }
}
