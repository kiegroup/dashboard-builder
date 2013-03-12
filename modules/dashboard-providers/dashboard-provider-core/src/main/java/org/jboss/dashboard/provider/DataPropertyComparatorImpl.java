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
package org.jboss.dashboard.provider;

import java.util.Iterator;

import org.jboss.dashboard.commons.comparator.AbstractComparatorByCriteria;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;

public class DataPropertyComparatorImpl extends AbstractComparatorByCriteria implements DataPropertyComparator {

    public DataPropertyComparatorImpl() {
        super();
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof DataProperty == false) return 1;
        if (o2 instanceof DataProperty == false) return -1;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        DataProperty entry1 = (DataProperty) o1;
        DataProperty entry2 = (DataProperty) o2;

        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps =  (Object[]) it.next();
            String criteriaId = (String) criteriaProps[0];
            int ordering = ((Integer)criteriaProps[1]).intValue();
            if (criteriaId.equals(CRITERIA_ID)) {
                String o1_Id = entry1.getPropertyId();
                String o2_Id = entry2.getPropertyId();
                int compById = ComparatorUtils.compare(o1_Id, o2_Id, ordering);
                if (compById != 0) return compById;
            }
            else if (criteriaId.equals(CRITERIA_NAME)) {
                String o1_Name = entry1.getName(locale);
                String o2_Name = entry2.getName(locale);
                int compByName = ComparatorUtils.compare(o1_Name, o2_Name, ordering);
                if (compByName != 0) return compByName;
            }
        }
        // Comparison gives equality.
        return 0;
    }    
}
