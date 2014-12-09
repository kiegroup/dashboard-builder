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

import org.jboss.dashboard.commons.comparator.RuntimeConstrainedComparator;
import org.jboss.dashboard.commons.misc.ReflectionUtils;
import org.jboss.dashboard.dataset.profiler.DataSetFilterConstraints;
import org.jboss.dashboard.dataset.profiler.DataSetGroupByConstraints;
import org.jboss.dashboard.dataset.profiler.DataSetSortConstraints;
import org.jboss.dashboard.profiler.ProfilerHelper;
import org.jboss.dashboard.profiler.memory.SizeEstimations;
import org.jboss.dashboard.provider.DefaultDataProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.dataset.index.DataSetIndex;
import org.jboss.dashboard.dataset.index.DistinctValue;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.label.LabelInterval;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.function.ScalarFunction;

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

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(AbstractDataSet.class);

    protected DataProvider provider;
    protected DataProperty[] properties;
    protected List[] propertyValues;
    protected DataSetIndex index;

    protected static Predicate NON_NULL_ELEMENTS = new Predicate() {
        public boolean evaluate(Object o) {
            return o != null;
        }
    };

    public AbstractDataSet() {
        this(null);
    }

    public AbstractDataSet(DataProvider provider) {
        this.provider = provider;
        properties = null;
        propertyValues = null;
        index = new DataSetIndex(this);
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
        index.clearAll();
    }

    public void clear() {
        setPropertySize(0);
    }

    public int sizeOf() {
        int nrows = getRowCount();
        if (nrows == 0) return 0;

        SizeEstimations sizeEstimator = SizeEstimations.lookup();
        int ncells = nrows * getProperties().length;
        int result = ncells * 4;
        DataProperty[] props = getProperties();
        for (int i = 0; i < props.length; i++) {
            Object firstRowValue = getValueAt(0, i);
            if (firstRowValue instanceof String) {
                for (int j = 0; j < nrows; j++) {
                    String stringValue = (String) getValueAt(j, i);
                    result += sizeEstimator.sizeOfString(stringValue);
                }
            } else {
                int singleValueSize = sizeEstimator.sizeOf(firstRowValue);
                result += nrows * singleValueSize;
            }
        }
        return result;
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
            if (property == p) return column;
        }
        for (int column = 0; column < properties.length; column++) {
            DataProperty property = properties[column];
            if (property.equals(p)) return column;
        }
        return -1;
    }

    public DataProperty getPropertyById(String id) {
        if (id == null) return null;
        for (int i = 0; properties != null && i < properties.length; i++) {
            DataProperty property = properties[i];
            if (property.getPropertyId().equalsIgnoreCase(id)) return property;
        }
        return null;
    }

    public DataProperty getPropertyByColumn(int column) {
        if (column < 0 || column >= properties.length) {
            throw new ArrayIndexOutOfBoundsException("Column out of bounds: " + column + "(must be between 0 and " + (properties.length-1) + ")");
        }
        return properties[column];
    }

    public List[] getPropertyValues() {
        return propertyValues;
    }

    public DataSetIndex getDataSetIndex() {
        return index;
    }

    public List getPropertyValues(DataProperty dp) {
        int column = getPropertyColumn(dp);
        if (column == -1) return new ArrayList();

        return new ArrayList(getPropertyValues()[column]);
    }

    public void addRowValue(int index, Object value) {
        List values = getPropertyValues()[index];
        if (values != null) values.add(_transformValue(value));
    }

    public void addRowValues(Object[] row) {
        if (row.length != properties.length) {
            throw new IllegalArgumentException("The row argument size and the data set row size do not match.");
        }
        for (int i = 0; i < row.length; i++) {
            // TODO: There is a problem if more than one column has the same name. The length of the row and the number of properties doesn't match.
            getPropertyValues()[i].add(_transformValue(row[i]));
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

    public DataSet filter(DataFilter filter) throws Exception {
        return _filterInMemory(filter);
    }

    protected DataSet _filterInMemory(DataFilter filter) throws Exception {

        // Filter only if required.
        if (getRowCount() == 0 || getProperties().length == 0 || filter == null) return null;
        String[] filterPropertyIds = filter.getPropertyIds();
        if (filterPropertyIds.length == 0) return null;

        // Create a target filter containing only those properties belonging to this dataset.
        FilterByCriteria targetFilter = filter.cloneFilter();
        String[] remainingPropIds = filter.getPropertyIds();
        for (String propId : remainingPropIds) {
            if (getPropertyById(propId) == null) {
                targetFilter.removeProperty(propId);
            }
        }

        // Go ahead only if the target filter contains at least one property.
        if (targetFilter.getPropertyIds().length == 0) {
            return null;
        }

        // Add filter constraints to the current thread.
        ProfilerHelper.addRuntimeConstraint(new DataSetFilterConstraints(this));

        // Create the result data set instance.
        DefaultDataSet _result = new DefaultDataSet(provider);
        _result.setPropertySize(propertyValues.length);
        for (int j=0; j<propertyValues.length; j++) {
            DataProperty dataProp = getPropertyByColumn(j);
            DataProperty _prop = dataProp.cloneProperty();
            _result.addProperty(_prop, j);
        }

        // Get only the subset of rows to be analyzed.
        Set<Integer> targetRows = preProcessFilter(targetFilter);
        if (targetRows.isEmpty() && targetFilter.getPropertyIds().length == 0) {
            // Return an empty data set if there is no more criteria to filter for.
            return _result;
        }

        // Filter the target rows and build the results matrix.
        Iterator<Integer> _rowIt = targetRows.iterator();
        Map _rowMap = new HashMap();
        Object[] _rowArray = new Object[propertyValues.length];
        boolean _continue = true;
        int _index = 0;
        int _row = 0;
        int _nrows = 0;
        while (_continue) {
            // Iterate against the target rows or over the whole data set.
            if (!targetRows.isEmpty()) _row = _rowIt.next();
            else _row = _index++;

            // If all properties has been processed then no additional filter is required.
            if (targetFilter.getPropertyIds().length == 0) {
                fillArrayWithRow(_row, _rowArray);
                _result.addRowValues(_rowArray);
            }
            // Else, check every target row with the target filter.
            else {
                fillMapWithRow(_row, _rowMap);
                if (targetFilter.pass(_rowMap)) {
                    fillArrayWithRow(_row, _rowArray);
                    _result.addRowValues(_rowArray);
                }
            }
            // Check filter constraints (every 1000 rows)
            if (++_nrows > 1000) {
                _nrows = 0;
                ProfilerHelper.checkRuntimeConstraints();
            }
            // Check loop finished.
            if (!targetRows.isEmpty()) _continue = _rowIt.hasNext();
            else _continue = _index < getRowCount();
        }
        return _result;
    }

    /**
     * Method that leverages the data set index information to boost the performance when filtering by label properties.
     * @return A set of rows that matches one or more of the filter criteria.
     * Also noticed that the criteria matched will be removed from the specified filter instance.
     */
    protected Set<Integer> preProcessFilter(FilterByCriteria filter) {
        Set<Integer> targetRows = new HashSet<Integer>();
        String[] remainingPropIds = filter.getPropertyIds();
        for (String propId : remainingPropIds) {
            List allowedValues = filter.getPropertyAllowedValues(propId);
            if (allowedValues != null && allowedValues.size() == 1) {
                for (Object allowedValue : allowedValues) {
                    if (allowedValue instanceof LabelInterval) {
                        LabelInterval labelInterval = (LabelInterval) allowedValue;
                        targetRows.addAll(labelInterval.getHolder().rows);
                        filter.removeProperty(propId);
                    }
                    else if (allowedValue instanceof CompositeInterval) {
                        CompositeInterval compositeInterval = (CompositeInterval) allowedValue;
                        if (compositeInterval.getDomain() instanceof LabelDomain) {
                            LabelDomain labelDomain = (LabelDomain) compositeInterval.getDomain();
                            Set<Integer> compositeRows = labelDomain.getRowNumbers(compositeInterval.getIntervals());
                            targetRows.addAll(compositeRows);
                            filter.removeProperty(propId);
                        }
                    }
                }
            }
        }
        return targetRows;
    }


    public DataSet groupBy(DataProperty groupByProperty, int[] columns, String[] functionCodes) {
        return groupBy(groupByProperty, columns, functionCodes, 0, 0);
    }

    public DataSet groupBy(DataProperty groupByProperty, int[] columns, String[] functionCodes, int sortIndex, int sortOrder) {
        // Group by operations are time constrained.
        ProfilerHelper.addRuntimeConstraint(new DataSetGroupByConstraints(this));

        // For label-type properties use the high-performance groupByLabel method.
        if (groupByProperty.getDomain() instanceof LabelDomain) {
            return groupByLabel(groupByProperty, columns, functionCodes, sortIndex, sortOrder);
        }
        // Get the intervals
        List<Interval> intervals = groupByProperty.getDomain().getIntervals();

        // Create the result data set instance.
        DefaultDataSet _result = new DefaultDataSet(provider);
        _result.setPropertySize(columns.length);

        // Populate the dataset with the calculations.
        int pivotColumn = -1;
        for (int j=0; j<columns.length; j++) {

            // Create a new data property for each target column.
            DataProperty dataProp = getPropertyByColumn(columns[j]);
            DataProperty _prop = dataProp.cloneProperty();
            _result.addProperty(_prop, j);

            if (pivotColumn == -1 && groupByProperty.equals(dataProp)) {
                _prop.setDomain(new LabelDomain());
                pivotColumn = j;

                // The row values for the pivot column are the own interval instances.
                for (Interval interval : intervals) {
                    _result.addRowValue(j, interval);
                }
            } else {
                // The values for other columns is a scalar function applied on the interval's values.
                ScalarFunctionManager scalarFunctionManager = DataProviderServices.lookup().getScalarFunctionManager();
                ScalarFunction function = scalarFunctionManager.getScalarFunctionByCode(functionCodes[j]);
                for (Interval interval : intervals) {
                    Double scalar = calculateScalar(interval, dataProp, function);
                    _result.addRowValue(j, scalar);
                }
                // After calculations, ensure the new property domain is numeric.
                _prop.setDomain(new NumericDomain());
            }
        }

        // Sort the resulting data set according to the sort order specified.
        if (sortOrder != 0) {
            DataSetComparator comp = new DataSetComparator();
            comp.addSortCriteria(Integer.toString(sortIndex), sortOrder);
            sort(comp);
        }

        return _result;
    }

    public DataSet groupByLabel(DataProperty groupByProperty, int[] columns, String[] functionCodes, int sortIndex, int sortOrder) {
        // Create the result data set instance.
        DefaultDataSet _result = new DefaultDataSet(provider);
        _result.setPropertySize(columns.length);
        DataProperty _pivotProp = groupByProperty.cloneProperty();

        // Get the pivot column
        int pivotColumn = -1;
        for (int j=0; j<columns.length; j++) {
            DataProperty dataProp = getPropertyByColumn(columns[j]);
            if (pivotColumn == -1 && groupByProperty.equals(dataProp)) {
                pivotColumn = j;
                break;
            }
        }

        // Get the indexed labels
        int groupByColumn = getPropertyColumn(groupByProperty);
        List<DistinctValue> _distinctValues = index.getDistinctValues(groupByColumn);
        List<DistinctValue> _sortedValues = new ArrayList<DistinctValue>(_distinctValues);
        if (sortOrder != 0) {
            if (sortIndex < 0 || sortIndex == pivotColumn) index.sortByValue(_sortedValues, sortOrder);
            else index.sortByScalar(_sortedValues, functionCodes[sortIndex], columns[sortIndex], sortOrder);
        }

        // Build the label interval set from the sorted list of distinct values.
        LabelDomain _pivotDomain = (LabelDomain) _pivotProp.getDomain();
        List<Interval> intervals = _pivotDomain.getIntervals(_sortedValues);

        // Populate the dataset with the calculations.
        for (int j=0; j<columns.length; j++) {
            DataProperty dataProp = getPropertyByColumn(columns[j]);

            if (j == pivotColumn) {
                _result.addProperty(_pivotProp, j);

                // The row values for the pivot column are the own interval instances.
                for (Interval interval : intervals) {
                    _result.addRowValue(j, interval);
                }
            } else {
                DataProperty _prop = dataProp.cloneProperty();
                _result.addProperty(_prop, j);

                // The values for other columns is a scalar function applied on the interval's values.
                ScalarFunctionManager scalarFunctionManager = DataProviderServices.lookup().getScalarFunctionManager();
                ScalarFunction function = scalarFunctionManager.getScalarFunctionByCode(functionCodes[j]);
                for (Interval interval : intervals) {
                    Double scalar = calculateScalar(interval, dataProp, function);
                    _result.addRowValue(j, scalar);
                }
                // After calculations, ensure the new property domain is numeric.
                _prop.setDomain(new NumericDomain());
            }
        }
        return _result;
    }

    protected Double calculateScalar(Interval interval, DataProperty property, ScalarFunction function) {
        Collection values = interval.getValues(property);
        if (!CollectionUtils.exists(values, NON_NULL_ELEMENTS)) {
            return new Double(0);
        } else {
            double value = function.scalar(values);

            // Check constraints every time an scalar calculation is carried out.
            ProfilerHelper.checkRuntimeConstraints();

            return new Double(value);
        }
    }

    public DataSet sort(Comparator comparator) {
        // Get the list of rows to sort.
        List sortedPropertyValues = new ArrayList();
        for (int row = 0; row < getRowCount(); row++) {
            Object[] rowMap = getRowAt(row);
            sortedPropertyValues.add(rowMap);
        }

        // Sort the rows using a runtime constrained comparator.
        ProfilerHelper.addRuntimeConstraint(new DataSetSortConstraints(this));
        RuntimeConstrainedComparator _comp = new RuntimeConstrainedComparator(comparator, 10000);
        Collections.sort(sortedPropertyValues, _comp);

        // Update the internal data set matrix.
        List[] propertyValues = getPropertyValues();
        for (List propertyValue : propertyValues) {
            propertyValue.clear();
        }
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
        for (DataProperty property : properties) {
            printIndent(out, indent++);
            out.println("<dataproperty id=\"" + StringEscapeUtils.escapeXml(property.getPropertyId()) + "\">");
            printIndent(out, indent);
            Domain domain = property.getDomain();
            String convertedFromNumeric = "";
            if (domain instanceof LabelDomain && ((LabelDomain)domain).isConvertedFromNumeric()) convertedFromNumeric = " convertedFromNumeric=\"true\" ";
            out.println("<domain" + convertedFromNumeric + ">" + StringEscapeUtils.escapeXml(property.getDomain().getClass().getName()) + "</domain>");
            Map<Locale,String> names = property.getNameI18nMap();
            if (names != null) {
                for (Locale locale : names.keySet()) {
                    printIndent(out, indent);
                    out.println("<name language=\"" + locale + "\">" + StringEscapeUtils.escapeXml(names.get(locale)) + "</name>");
                }
            }
            printIndent(out, --indent);
            out.println("</dataproperty>");
        }

        printIndent(out, --indent);
        out.println("</dataproperties>");
    }

    public void parseXMLProperties(NodeList nodes) throws Exception {
        boolean update = getProperties().length > 0;
        List<DataProperty> result = _parseXMLProperties(nodes, update);
        if (!update) {
            setPropertySize(result.size());
            for (int i = 0; i < result.size(); i++) {
                DataProperty p = result.get(i);
                addProperty(p, i);
            }
        }
    }

    public List<DataProperty> _parseXMLProperties(NodeList nodes, boolean update) throws Exception {
        List<DataProperty> result = new ArrayList<DataProperty>();
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equals("dataproperty")) {
                String idDataProperty = StringEscapeUtils.unescapeXml(node.getAttributes().getNamedItem("id").getNodeValue());
                DataProperty property = getPropertyById(idDataProperty);
                if (property == null) {
                    if (update) continue; // Be aware of deleted properties.
                    else property = new DefaultDataProperty(idDataProperty);
                }

                result.add(property);
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
        return result;
    }

    protected  void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    protected static final String ORACLE_TIMESTAMP = "oracle.sql.TIMESTAMP";

    protected Object _transformValue(Object value) {
        if (value == null) return value;
        Class valueClass = value.getClass();
        if (!Comparable.class.isAssignableFrom(valueClass)) {
            if (ORACLE_TIMESTAMP.equals(valueClass.getName())) {
                return ReflectionUtils.invokeMethod(value, "dateValue", null);
            }
        }
        return value;
    }
}
