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

import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A data set represents a matrix of data with a fixed number of properties and a fixed number of rows.
 */
public interface DataSet {

    /**
     * DataProvider
     */
    DataProvider getDataProvider();
    void setDataProvider(DataProvider dataProvider);

    /**
     * @link aggregationByValue
     * @associates <{DataProperty}>
     * @clientCardinality 0..*
     * @directed
     * @supplierQualifier properties
     */
    DataProperty[] getProperties();

    /**
     * Get all values encountered for the specified column. No matter if duplicates exists.
     */
    List getPropertyValues(DataProperty p);

    /**
     * Get a property by its column index (starting by 0)
     */
    DataProperty getPropertyByColumn(int column);

    /**
     * Get a property by its column index.
     */
    DataProperty getPropertyById(String id);

    /**
     * Get a property column position in the dataset.
     */
    int getPropertyColumn(DataProperty property);

    /**
     * Get the number of rows in the dataset.
     */
    int getRowCount();

    /**
     * Get the value at a given cell.
     * @param row The cell row (the first row is 0).
     * @param column The cell column (the first column is 0).
     */
    Object getValueAt(int row, int column);

    /**
     * Get all the values at a given column.
     * @param column The cell column (the first column is 0).
     */
    List getValuesAt(int column);

    /**
     * Get all the values for a given row.
     * @param row The row position (the first row is 0).
     * @return An array of objects representing the row.
     */
    Object[] getRowAt(int row);
    
    /**
     * Get all the values for a given row.
     * @param row The row position (the first row is 0).
     * @return A map of [property identifier, value (Object)].
     */
    Map getRowAsMap(int row);

    /**
     * Return the estimated memory (in bytes) this data set is consuming.
     * @return The number of bytes
     */
    int sizeOf();

    /**
     * Filter the dataset.
     * @return A filtered data set or null if there is nothing to do with the filter specified,
     * because it's empty or does not contains properties belonging to this data set.
     */
    DataSet filter(DataFilter filter) throws Exception;

    /**
     * Groups this data set.
     *
     * @param groupByProperty The property to group for.
     * @param columns An array of the columns to be included in the resulting data set.
     * @param functionCodes The scalar function codes to apply to each property of the resulting data set.
     * @return A new data set instance containing the group by calculations.
     */
    DataSet groupBy(DataProperty groupByProperty, int[] columns, String[] functionCodes);

    /**
     * Groups this data set.
     *
     * @param groupByProperty The property to group for.
     * @param columns An array of the columns to be included in the resulting data set.
     * @param functionCodes The scalar function codes to apply to each property of the resulting data set.
     * @param sortIndex The resulting data set column to order the result for (starting at 0 and lower than the columns array length).
     * @param sortOrder 1=Ascending, -1=Descending, 0=None
     * @return A new data set instance containing the group by calculations.
     */
    DataSet groupBy(DataProperty groupByProperty, int[] columns, String[] functionCodes, int sortIndex, int sortOrder);

    /**
     * Sorts this data set.
     * NOTE: Implemented for single column sorting.
     * @param comparator Used to compare rows (Object[] instances).
     * @return Sorted data set.
     */
    DataSet sort(Comparator comparator);

    /**
     * Returns an XML string with the definition of the data set properties.
     */
    void formatXMLProperties(PrintWriter out, int indent) throws Exception;

    /**
     * It updates the dataset properties with the definition coming from the specified XML.
     */
    void parseXMLProperties(NodeList nodes) throws Exception;

    /**
     * Get all the properties this data set references to (directly or indirectly). A property is a reference
     * if any potential change on the property values has an impact on the data hold by this data set,
     * This typically occurs on filter/unfilter requests.
     */
    Set<String> getPropertiesReferenced() throws Exception;
}
