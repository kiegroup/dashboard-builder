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

public class DataProviderComparatorImpl extends AbstractComparatorByCriteria implements DataProviderComparator {

    public DataProviderComparatorImpl() {
        super();
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof DataProvider == false) return 1;
        if (o2 instanceof DataProvider == false) return -1;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        DataProvider entry1 = (DataProvider) o1;
        DataProvider entry2 = (DataProvider) o2;

        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps =  (Object[]) it.next();
            String criteriaId = (String) criteriaProps[0];
            int ordering = ((Integer)criteriaProps[1]).intValue();
            if (criteriaId.equals(CRITERIA_CODE)) {
                String o1_Code = entry1.getCode();
                String o2_Code = entry2.getCode();
                int compByCode = ComparatorUtils.compare(o1_Code, o2_Code, ordering);
                if (compByCode != 0) return compByCode;
            }
            else if (criteriaId.equals(CRITERIA_DESCRIPTION)) {
                String o1_Desc = entry1.getDescription(locale);
                String o2_Desc = entry2.getDescription(locale);
                int compByDesc = ComparatorUtils.compare(o1_Desc, o2_Desc, ordering);
                if (compByDesc != 0) return compByDesc;
            }
        }
        // Comparison gives equality.
        return 0;
    }    
}
