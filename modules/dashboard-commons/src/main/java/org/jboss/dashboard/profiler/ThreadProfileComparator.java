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
package org.jboss.dashboard.profiler;

import org.jboss.dashboard.commons.comparator.AbstractComparatorByCriteria;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;

import java.util.Iterator;

/**
 * Class used to compare ThreadProfile block instances using several criterias.
 */
public class ThreadProfileComparator extends AbstractComparatorByCriteria {

    // Sort criteria
    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";
    public static final String ELAPSED_TIME = "elapsedTime";

    public static ThreadProfileComparator comparatorByBeginDate(boolean ascending) {
        ThreadProfileComparator c = new ThreadProfileComparator();
        c.addSortCriteria(BEGIN_DATE, ascending ? 1 : -1);
        return c;
    }

    public static ThreadProfileComparator comparatorByElapsedTime(boolean ascending) {
        ThreadProfileComparator c = new ThreadProfileComparator();
        c.addSortCriteria(ELAPSED_TIME, ascending ? 1 : -1);
        return c;
    }

    public ThreadProfileComparator() {
        super();
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof ThreadProfile == false) return 1;
        if (o2 instanceof ThreadProfile == false) return -1;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        ThreadProfile entry1 = (ThreadProfile) o1;
        ThreadProfile entry2 = (ThreadProfile) o2;

        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps =  (Object[]) it.next();
            String criteriaId = (String) criteriaProps[0];
            int ordering = ((Integer)criteriaProps[1]).intValue();
            if (criteriaId.equals(BEGIN_DATE)) {
                int compById = ComparatorUtils.compare(entry1.getBeginDate(), entry2.getBeginDate(), ordering);
                if (compById != 0) return compById;
            }
            else if (criteriaId.equals(END_DATE)) {
                int compById = ComparatorUtils.compare(entry1.getEndDate(), entry2.getEndDate(), ordering);
                if (compById != 0) return compById;
            }
            else if (criteriaId.equals(ELAPSED_TIME)) {
                int compById = ComparatorUtils.compare(entry1.getElapsedTime(), entry2.getElapsedTime(), ordering);
                if (compById != 0) return compById;
            }
            else {
                int compByProp = ComparatorUtils.compare(entry1.getContextProperty(criteriaId), entry2.getContextProperty(criteriaId), ordering);
                if (compByProp != 0) return compByProp;
            }
        }
        // Comparison gives equality.
        return 0;
    }
}