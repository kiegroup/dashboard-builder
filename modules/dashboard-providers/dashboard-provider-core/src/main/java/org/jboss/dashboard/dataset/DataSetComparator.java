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

import org.jboss.dashboard.commons.comparator.AbstractComparatorByCriteria;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that can be used to compare two dataset rows by a given column name.
 */
public class DataSetComparator extends AbstractComparatorByCriteria {

    private static transient Log log = LogFactory.getLog(DataSetComparator.class.getName());

    public int compare(Object o1, Object o2) {
        try {
            // Check criteria.
            String[] criteriaIds = getCriteriaIds();
            if (criteriaIds.length == 0) return 0;
            if (criteriaIds.length > 1) log.warn("Sorting by multiple criterias is not supported. Sorting by first criteria only.");
            String columnIndex = criteriaIds[0];

            // Objects must be not null arrays.
            if (o1 == null && o2 != null) return -1;
            else if (o1 != null && o2 == null) return 1;
            else if (o1 == null && o2 == null) return 0;

            // Compare the two rows.
            int column = Integer.parseInt(columnIndex);
            Object[] row1 = (Object[]) o1;
            Object[] row2 = (Object[]) o2;
            return ComparatorUtils.compare(row1[column], row2[column], getSortCriteriaOrdering(columnIndex));
        } catch (ClassCastException e) {
            log.warn("Cannot compare the given objects for the data set.");
            return 0;
        }
    }
}
