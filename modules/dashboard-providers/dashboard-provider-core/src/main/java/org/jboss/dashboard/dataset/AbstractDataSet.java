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

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;

import java.io.PrintWriter;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base class for the implementation of custom data sets.
 */
public abstract class AbstractDataSet implements DataSet {

    protected DataProvider provider;
    protected DataProperty[] properties;
    protected List[] propertyValues;
    protected List[] propertyValuesFiltered;

    public AbstractDataSet(DataProvider provider) {
        properties = null;
        propertyValues = null;
        propertyValuesFiltered = null;
        this.provider = provider;
    }

    public DataProvider getDataProvider() {
        return provider;
    }

    public void setDataProvider(DataProvider provider) {
        this.provider = provider;
    }

    public void setPropertySize(int propSize) {
        properties = new DataProperty[propSize];
        propertyValues = new List[propSize];
        propertyValuesFiltered = null;
    }

    public void clear() {
        setPropertySize(0);
    }

    public DataProperty[] getProperties() {
        if (properties == null) return new DataProperty[] {};
        return properties;
    }

    public void addProperty(DataProperty dp, int index) {
        properties[index] = dp;
        propertyValues[index] = new ArrayList();
        dp.setDataSet(this);
    }

    public int getPropertyColumn(DataProperty p) {
        if (p == null) return -1;
        for (int column = 0; column < properties.length; column++) {
            DataProperty property = properties[column];
            if (property.equals(p)) return column;
        }
        return -1;
    }

    public DataProperty getPropertyById(String id){
        if (id == null) return null;
        for (int i = 0; properties != null && i < properties.length; i++) {
            DataProperty property = properties[i];
            if (property.getPropertyId().equalsIgnoreCase(id)) return property;
        }
        return null;
    }

    public DataProperty getPropertyByIndex(int column) {
        if (column < 0 || column >= properties.length) {
            throw new ArrayIndexOutOfBoundsException("Column out of bounds: " + column + "(must be between 0 and " + (properties.length-1) + ")");
        }
        return properties[column];
    }

    protected List[] getPropertyValues() {
        if (propertyValuesFiltered != null) return propertyValuesFiltered;
        return propertyValues;
    }

    public List getPropertyValues(DataProperty dp) {
        int index = getPropertyColumn(dp);
        if (index == -1) return new ArrayList();
        return new ArrayList(getPropertyValues()[index]);
    }

    public void addRowValue(int index, Object value) {
        List values = getPropertyValues()[index];
        if (values != null) values.add(value);
    }

    public void addRowValues(Object[] row) {
        if (row.length != properties.length) {
            throw new IllegalArgumentException("The row argument size and the data set row size do not match.");
        }
        for (int i = 0; i < row.length; i++) {
            // TODO: There is a problem if more than one column has the same name. The length of the row and the number of properties doesn't match.
            getPropertyValues()[i].add(row[i]);
        }
    }

    public int getRowCount() {
        if (getPropertyValues() == null || getPropertyValues().length == 0) return 0;
        return getPropertyValues()[0].size();
    }

    public Object getValueAt(int row, int column) {
        if (row >= getRowCount()) return null;
        if (column >= getProperties().length) return null;

        List values = getPropertyValues()[column];
        if (row >= values.size()) return null;
        return values.get(row);
    }

    public List getValuesAt(int column) {
        List[] values = getPropertyValues();
        if (column < 0 || column >= values.length) {
            throw new ArrayIndexOutOfBoundsException("Column out of bounds: " + column + "(must be between 0 and " + (values.length-1) + ")");
        }
        return values[column];
    }

    public Object[] getRowAt(int row) {
        if (row >= getRowCount()) return null;
        Object[] result = new Object[getPropertyValues().length];
        fillArrayWithRow(row, result);
        return result;
    }

    protected void fillArrayWithRow(int row, Object[] array) {
        if (row >= getRowCount()) return;
        List[] matrix = getPropertyValues();
        for (int i = 0; i < array.length; i++) array[i] = matrix[i].get(row);
    }

    public Map getRowAsMap(int row) {
        if (row >= getRowCount()) return null;
        Map  result = new HashMap();
        fillMapWithRow(row, result);
        return result;
    }

    protected void fillMapWithRow(int row, Map m) {
        if (row >= getRowCount()) return;
        List[] matrix = getPropertyValues();
        for (int i = 0; i < properties.length; i++) {
            m.put(properties[i].getPropertyId(), matrix[i].get(row));
        }
    }

    public void filter(DataFilter filter) throws Exception {
        // Reset the current filter.
        propertyValuesFiltered = null;

        // Filter only if required.
        if (getRowCount() == 0 || getProperties().length == 0) return;
        String[] filterPropertyIds = filter.getPropertyIds();
        if (filterPropertyIds.length == 0) return;

        // Get only those dataset's properties included into the filter criteria.
        Set<String> dataSetPropertyIds = new HashSet<String>();
        for (int i = 0; i < filterPropertyIds.length; i++) {
            if (getPropertyById(filterPropertyIds[i]) != null) {
                dataSetPropertyIds.add(filterPropertyIds[i]);
            }
        }

        // Go ahead only if the filter contains at least one property belonging to this dataset.
        if (dataSetPropertyIds.isEmpty()) {
            return;
        }

        // Create an empty result set.
        List[] results = new List[propertyValues.length];
        for (int column = 0; column < results.length; column++) {
            results[column] = new ArrayList();
        }

        // Filter the rows and build the results matrix.
        Map rowMap = new HashMap();
        Object[] rowArray = new Object[propertyValues.length];
        for (int row = 0; row < getRowCount(); row++) {
            fillMapWithRow(row, rowMap);
            if (filter.pass(rowMap)) {
                fillArrayWithRow(row, rowArray);
                for (int column = 0; column < rowArray.length; column++) {
                    results[column].add(rowArray[column]);
                }
            }
        }

        // Activate the filter once we got the filter results.
        propertyValuesFiltered = results;
    }

    public DataSet groupBy(DataProperty groupByProperty, List<DataProperty> groupByProps, List<String> functionCodes) {
        // Create the dataset instance.
        DefaultDataSet groupByDataSet = new DefaultDataSet(null);
        groupByDataSet.setPropertySize(groupByProps.size());
        DataProperty pivotProp = null;
        for (int i = 0; i < groupByProps.size(); i++) {
            DataProperty grProp = groupByProps.get(i).cloneProperty();
            groupByDataSet.addProperty(grProp, i);
            if (pivotProp == null && grProp.equals(groupByProperty)) pivotProp = grProp;
        }

        // Populate the dataset with the calculations.
        // For each group by interval add a row to the data set.
        Predicate nonNullElements = new Predicate() { public boolean evaluate(Object o) { return o != null; }};
        Interval[] groupByIntervals = groupByProperty.getDomain().getIntervals();
        for (int i=0; i<groupByIntervals.length; i++) {
            Interval groupByInterval = groupByIntervals[i];

            // For each data set property calculate its grouped interval value.
            for (int j=0; j<groupByProps.size(); j++) {

                // The row value for the group by column is the own interval instance.
                DataProperty grProp = groupByDataSet.getProperties()[j];
                String grFunctionCode = functionCodes.get(j);
                if (grProp == pivotProp) {
                    groupByDataSet.addRowValue(j, groupByInterval);
                }
                // The value for the other columns is a scalar function applied over the column values belonging to the interval.
                else {
                    DataProperty dataSetProp = getPropertyById(grProp.getPropertyId());
                    Collection dataSetValues = groupByInterval.getValues(dataSetProp);
                    if (!CollectionUtils.exists(dataSetValues, nonNullElements)) {
                        // If all the interval elements to group are null then set 0.
                        groupByDataSet.addRowValue(j, new Double(0));
                    } else {
                        ScalarFunctionManager scalarFunctionManager = DataProviderServices.lookup().getScalarFunctionManager();
                        ScalarFunction grFunction = scalarFunctionManager.getScalarFunctionByCode(grFunctionCode);
                        double value = grFunction.scalar(dataSetValues);
                        groupByDataSet.addRowValue(j, new Double(value));
                    }
                }
            }
        }
        // After ending the group by calculations and populate the data set, set the domains.
        DataProperty[] groupByProperties = groupByDataSet.getProperties();
        for (int i = 0; i < groupByProperties.length; i++) {
            DataProperty byProperty = groupByProperties[i];
            if (byProperty.equals(groupByProperty)) byProperty.setDomain(new LabelDomain());
            else byProperty.setDomain(new NumericDomain());
        }
        return groupByDataSet;
    }

    public DataSet sort(ComparatorByCriteria comparator) {
        // Get the list of rows to sort.
        List sortedPropertyValues = new ArrayList();
        for (int row = 0; row < getRowCount(); row++) {
            Object[] rowMap = getRowAt(row);
            sortedPropertyValues.add(rowMap);
        }

        // Sort the rows.
        Collections.sort(sortedPropertyValues, comparator);

        // Update the internal data set matrix.
        List[] propertyValues = getPropertyValues();
        for (int i = 0; i < propertyValues.length; i++) propertyValues[i].clear();
        Iterator it = sortedPropertyValues.iterator();
        while (it.hasNext()) {
            Object[] valuesAtRow = (Object[]) it.next();
            addRowValues(valuesAtRow);
        }
        return this;
    }

    public void formatXMLProperties(PrintWriter out, int indent) throws Exception {
        printIndent(out, indent++);
        out.println("<dataproperties>");

        DataProperty[] properties = getProperties();
        for (int i = 0; i < properties.length; i++) {
            DataProperty property = properties[i];
            printIndent(out, indent++);
            out.println("<dataproperty id=\"" + StringEscapeUtils.escapeXml(property.getPropertyId()) + "\">");
            printIndent(out, indent);
            Domain domain = property.getDomain();
            String convertedFromNumeric = "";
            if (domain instanceof LabelDomain && ((LabelDomain)domain).isConvertedFromNumeric()) convertedFromNumeric = " convertedFromNumeric=\"true\" ";
            out.println("<domain" + convertedFromNumeric + ">" + StringEscapeUtils.escapeXml(property.getDomain().getClass().getName()) + "</domain>");
            Map names = property.getNameI18nMap();
            if (names != null) {
                Iterator it = names.keySet().iterator();
                while (it.hasNext()) {
                    Locale locale = (Locale) it.next();
                    printIndent(out, indent);
                    out.println("<name language=\"" + locale + "\">" + StringEscapeUtils.escapeXml((String) names.get(locale)) + "</name>");
                }
            }
            printIndent(out, --indent);
            out.println("</dataproperty>");
        }

        printIndent(out, --indent);
        out.println("</dataproperties>");
    }

    public void parseXMLProperties(NodeList nodes) throws Exception {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equals("dataproperty")) {
                String idDataProperty = StringEscapeUtils.unescapeXml(node.getAttributes().getNamedItem("id").getNodeValue());
                DataProperty property = getPropertyById(idDataProperty);
                if (property == null) continue; // Be aware of deleted properties.

                NodeList dataProperties = node.getChildNodes();
                for (int y = 0; y < dataProperties.getLength(); y++) {
                    Node dataProperty = dataProperties.item(y);
                    if (dataProperty.getNodeName().equals("domain")) {
                        Domain domain = (Domain) Class.forName(StringEscapeUtils.unescapeXml(dataProperty.getFirstChild().getNodeValue())).newInstance();
                        if (dataProperty.getAttributes().getNamedItem("convertedFromNumeric") != null) ((LabelDomain) domain).setConvertedFromNumeric(true);
                        property.setDomain(domain);
                    }
                    if (dataProperty.getNodeName().equals("name")) {
                        String lang = dataProperty.getAttributes().getNamedItem("language").getNodeValue();
                        String desc = StringEscapeUtils.unescapeXml(dataProperty.getFirstChild().getNodeValue());
                        property.setName(desc, new Locale(lang));
                    }
                }
            }
        }
    }

    protected  void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }
}
