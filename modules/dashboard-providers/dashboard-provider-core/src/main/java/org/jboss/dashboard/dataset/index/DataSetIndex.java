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
package org.jboss.dashboard.dataset.index;

import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.provider.DataProperty;

import java.util.*;

/**
 * Class that provides some high-performance operations for DataSet access.
 */
public class DataSetIndex {

    protected DataSet dataSet;
    protected ColumnIndex[] columnIndexes;

    public DataSetIndex(DataSet dataSet) {
        this.dataSet = dataSet;
        clearAll();
    }

    // Index public services

    /**
     * Remove all the internal references to indexed values.
     */
    public synchronized void clearAll() {
        DataProperty[] props = dataSet.getProperties();
        columnIndexes = new ColumnIndex[props.length];
    }

    /**
     * Get the indexed distinct values for a given data set column.
     */
    public synchronized List<DistinctValue> getDistinctValues(int column) {
        ColumnIndex columnIndex = getColumnIndex(column);
        if (columnIndex == null) return null;
        return columnIndexes[column].getDistinctValues();
    }

    /**
     * Sort a list of DistinctValue's by the value itself.
     *
     * @param values The DistinctValue's to order.
     * @param order 1=Ascending, -1=Descending, 0=None
     */
    public void sortByValue(List<DistinctValue> values, final int order) {
        Collections.sort(values, new Comparator<DistinctValue>() {
            public int compare(DistinctValue o1, DistinctValue o2) {
                Object v1 = o1.value;
                Object v2 = o2.value;
                return ComparatorUtils.compare(v1, v2, order);
            }
        });
    }

    /**
     * Sort a list of DistinctValue's by the scalar function value calculated on a target sort column.
     *
     * @param values The DistinctValue's to order.
     * @param functionCode The scalar function code to calculate on the given sortColumn for each DistinctValue instance.
     * @param column The data set column to order for (starting at 0)
     * @param order 1=Ascending, -1=Descending, 0=None
     */
    public void sortByScalar(List<DistinctValue> values, final String functionCode, final int column, final int order) {
        Collections.sort(values, new Comparator<DistinctValue>() {
            public int compare(DistinctValue o1, DistinctValue o2) {
                Double d1 = o1.getScalar(column, functionCode);
                Double d2 = o2.getScalar(column, functionCode);
                return ComparatorUtils.compare(d1, d2, order);
            }
        });
    }

    // Index internal services

    protected void indexColumn(int column) {
        DataProperty prop = dataSet.getPropertyByColumn(column);

        // Only label properties are supported for the time being.
        if (prop.getDomain() instanceof LabelDomain) {
            List values = dataSet.getValuesAt(column);

            columnIndexes[column] = new LabelIndex(this, column);
            columnIndexes[column].indexValues(values);
        }
    }

    protected ColumnIndex getColumnIndex(int column) {
        if (columnIndexes[column] == null) indexColumn(column);
        return columnIndexes[column];
    }
}
