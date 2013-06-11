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
package org.jboss.dashboard.dataset.index;

import org.jboss.dashboard.commons.comparator.ComparatorUtils;

import java.util.*;

public class LabelIndex implements ColumnIndex {

    public DataSetIndex dataSetIndex = null;
    public int column = -1;
    public List<DistinctValue> disctinctValues = new ArrayList<DistinctValue>();

    public LabelIndex(DataSetIndex dataSetIndex, int column) {
        this.dataSetIndex = dataSetIndex;
        this.column = column;
    }

    public DataSetIndex getDataSetIndex() {
        return dataSetIndex;
    }

    public synchronized void indexValues(List values) {
        for (int row = 0; row < values.size(); row++) {
            indexValue(values.get(row), row);
        }
    }

    public synchronized void indexValue(Object value, int row) {
        DistinctValue distinctValue = getDistinctValue(value);
        if (distinctValue == null) disctinctValues.add(distinctValue = new DistinctValue(this, value));
        distinctValue.rows.add(row);
    }

    public synchronized List<DistinctValue> getDistinctValues() {
        return disctinctValues;
    }

    public synchronized List getValues() {
        List result = new ArrayList();
        for (int i = 0; i<disctinctValues.size(); i++) {
            result.add(disctinctValues.get(i).value);
        }
        return result;
    }

    public synchronized DistinctValue getDistinctValue(Object value) {
        for (DistinctValue distinctValue : disctinctValues) {
            if (distinctValue.value == value || distinctValue.value.equals(value)) {
                return distinctValue;
            }
        }
        return null;
    }

    public synchronized List getSiblingValues(Collection values, List targetValues) {
        HashSet<Integer> targetRows = new HashSet<Integer>();
        for (DistinctValue distinctValue : disctinctValues) {
            if (values.contains(distinctValue.value)) {
                targetRows.addAll(distinctValue.rows);
            }
        }

        if (targetRows.isEmpty()) return Collections.EMPTY_LIST;
        List result = new ArrayList();
        for (Integer targetRow : targetRows) {
            result.add(targetValues.get(targetRow));
        }
        return result;
    }

    public synchronized int getNumberOfItems(Object value) {
        DistinctValue distinctValue = getDistinctValue(value);
        if (distinctValue == null) return 0;
        return distinctValue.rows.size();
    }
}
