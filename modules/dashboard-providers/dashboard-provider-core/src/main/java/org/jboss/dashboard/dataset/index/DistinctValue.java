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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.function.ScalarFunctionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistinctValue {

    public ColumnIndex columnIndex = null;
    public Object value = null;
    public List<Integer> rows = new ArrayList<Integer>();
    public Map<Integer, Map<String, Double>> scalars = new HashMap<Integer, Map<String, Double>>();

    public DistinctValue(ColumnIndex columnIndex, Object value) {
        this.columnIndex = columnIndex;
        this.value = value;
    }

    public boolean equals(Object other) {
        if (value == null) return other == null;
        return value == other || value.equals(other);
    }

    public int hashCode() {
        if (value == null) return 0;
        return value.hashCode();
    }

    public Double getScalar(int column, String functionCode) {
        Map<String,Double> columnScalars = scalars.get(column);
        if (columnScalars == null) scalars.put(column, columnScalars = new HashMap<String, Double>());
        Double scalar = columnScalars.get(functionCode);
        if (scalar != null) return scalar;

        scalar = calculateScalar(column, functionCode);
        columnScalars.put(functionCode, scalar);
        return scalar;
    }

    protected Double calculateScalar(int column, String functionCode) {
        DataSet dataSet = columnIndex.getDataSetIndex().dataSet;

        List targetValues = new ArrayList();
        List columnValues = dataSet.getValuesAt(column);
        for (Integer targetRow : rows) {
            targetValues.add(columnValues.get(targetRow));
        }

        ScalarFunctionManager scalarFunctionManager = DataProviderServices.lookup().getScalarFunctionManager();
        ScalarFunction function = scalarFunctionManager.getScalarFunctionByCode(functionCode);

        if (!CollectionUtils.exists(targetValues, NON_NULL_ELEMENTS)) {
            return new Double(0);
        } else {
            double value = function.scalar(targetValues);
            return new Double(value);
        }
    }

    protected static Predicate NON_NULL_ELEMENTS = new Predicate() {
        public boolean evaluate(Object o) {
            return o != null;
        }
    };
}