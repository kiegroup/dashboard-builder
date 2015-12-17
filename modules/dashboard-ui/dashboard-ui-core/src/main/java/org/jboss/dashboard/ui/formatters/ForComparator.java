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
package org.jboss.dashboard.ui.formatters;

import org.apache.commons.jxpath.JXPathContext;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class ForComparator implements Comparator {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ForComparator.class.getName());
    private List sortTokens = new ArrayList();

    public ForComparator(String sortProperties) {
        StringTokenizer stk = new StringTokenizer(sortProperties);
        while (stk.hasMoreTokens()) {
            sortTokens.add(stk.nextToken().trim());
        }
    }

    public int compare(Object o1, Object o2) {
        for (int i = 0; i < sortTokens.size(); i++) {
            String property = (String) sortTokens.get(i);
            boolean descending = property.startsWith("-");
            if (property.startsWith("-") || property.startsWith("+")) {
                property = property.substring(1);
            }
            Object prop1 = getObjectProperty(o1, property);
            Object prop2 = getObjectProperty(o2, property);
            if (prop1 != null && prop2 != null) {
                if (prop1 instanceof Comparable && prop2 instanceof Comparable) {
                    int compareResult = ((Comparable) prop1).compareTo(prop2);
                    if (descending) compareResult *= -1;
                    if (compareResult != 0)
                        return compareResult;
                } else {
                    log.warn("Ignoring sort property " + property + " as it is not comparable.");
                }
            } else {
                log.warn("Ignoring sort property " + property + " as it is null for an item.");
            }
        }
        return 0;
    }

    private Object getObjectProperty(Object object, String property) {
        JXPathContext ctx = JXPathContext.newContext(object);
        try {
            return ctx.getValue(property);
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Invalid property '" + property + "' ", e);
        }
        return null;
    }
}
