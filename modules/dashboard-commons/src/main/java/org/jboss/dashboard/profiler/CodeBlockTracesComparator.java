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
package org.jboss.dashboard.profiler;

import org.jboss.dashboard.commons.comparator.AbstractComparatorByCriteria;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;

import java.util.Iterator;

/**
 * Class used to compare CodeBlockTrace's using several criterias.
 */
public class CodeBlockTracesComparator extends AbstractComparatorByCriteria {

    // Sort criteria
    public static final String CRITERIA_ELAPSED_TIME = "elapsedTime";
    public static final String CRITERIA_SELF_TIME = "selfTime";

    public CodeBlockTracesComparator() {
        super();
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof CodeBlockTraces == false) return 1;
        if (o2 instanceof CodeBlockTraces == false) return -1;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        CodeBlockTraces entry1 = (CodeBlockTraces) o1;
        CodeBlockTraces entry2 = (CodeBlockTraces) o2;

        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps =  (Object[]) it.next();
            String criteriaId = (String) criteriaProps[0];
            int ordering = ((Integer)criteriaProps[1]).intValue();
            if (criteriaId.equals(CRITERIA_ELAPSED_TIME)) {
                int compById = ComparatorUtils.compare(entry1.getElapsedTimeMillis(), entry2.getElapsedTimeMillis(), ordering);
                if (compById != 0) return compById;
            }
            else if (criteriaId.equals(CRITERIA_SELF_TIME)) {
                int compById = ComparatorUtils.compare(entry1.getSelfTimeMillis(), entry2.getSelfTimeMillis(), ordering);
                if (compById != 0) return compById;
            }
        }
        // Comparison gives equality.
        return 0;
    }
}