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

import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

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
     * Get a property by its id
     */
    DataProperty getPropertyByIndex(int column);

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
     * Filter the dataset.
     */
    void filter(DataFilter filter) throws Exception;

    /**
     * Groups this data set.
     * @param property The property to group for.
     * @param groupByProps The list of DataProperty instances to be include in the resulting data set.
     * @param functionCodes The scalar function codes to apply to each property of the resulting data set.
     * @return A new data set instance containing the group by calculations.
     */
    DataSet groupBy(DataProperty property, List<DataProperty> groupByProps, List<String> functionCodes);

    /**
     * Sorts this data set.
     * NOTE: Implemented for single column sorting.
     * @param comparator Comparator used to compare elements.
     * @return Sorted data set.
     */
    DataSet sort(ComparatorByCriteria comparator);

    /**
     * Returns an XML string with the definition of the data set properties.
     */
    void formatXMLProperties(PrintWriter out, int indent) throws Exception;

    /**
     * It updates the dataset properties with the definition coming from the specified XML.
     */
    void parseXMLProperties(NodeList nodes) throws Exception;
}
