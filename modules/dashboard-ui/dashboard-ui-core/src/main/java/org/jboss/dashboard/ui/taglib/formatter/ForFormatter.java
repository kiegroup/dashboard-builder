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
package org.jboss.dashboard.ui.taglib.formatter;

import org.jboss.dashboard.factory.Factory;
import org.apache.commons.jxpath.JXPathContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.*;

/**
 * This class extends Formatter to provide support for iteration through a list.
 * <p/>
 * It expects the following input parameters:
 * <ul>
 * <li> array. List of objects to render. Optional, but if it is empty or null, nothing is rendered.
 * <li> nullValue. Value to display for the null items in the list. Optional, if not set, the null item is rendered.
 * <li> factoryElement. If array is missing or null, and a Factory element is passed, use it as array.
 * <li> property. If array is missing or null, and factoryElement is specified, use this property as array to iterate.
 * <li> sortProperties. If you want array properties to be sorted, use a string like "+property -property +property"
 * </ul>
 * <p/>
 * It serves the following output fragments, with given output parameters:
 * <ul>
 * <li> outputStart. At the beginning of the iteration, if the list is not empty
 * <li> output. For every item in the list. It receives the following attributes:
 * <ul>
 * <li> index. 0-based position of item in the list.
 * <li> count. 1-based position of item in the list.
 * <li> element. Element being displayed, or the nullValue parameter when it is null.
 * </ul>
 * <li> outputEnd. At the end of the iteration, if the list is not empty.
 * <li> empty.If the list is empty.
 * </ul>
 */
public class ForFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ForFormatter.class.getName());

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        log.debug("Servicing ForFormatter.");
        Object array = getParameter("array");
        if (array == null) {
            Object componentName = getParameter("factoryElement");
            Object propertyName = getParameter("property");
            if (componentName != null) {
                Object component = Factory.lookup((String) componentName);
                array = component;
                if (propertyName != null) {
                    JXPathContext ctx = JXPathContext.newContext(component);
                    try {
                        array = ctx.getValue((String) propertyName);
                    } catch (Exception e) {
                        log.debug("Error:", e);
                    }
                }
            }
        }
        String sortProperties = (String) getParameter("sortProperties");

        Iterator iterator = null;
        if (array == null) {
            renderFragment("empty");
            return;
        }

        if (array instanceof Collection) {
            iterator = ((Collection) array).iterator();
        } else if (array.getClass().isArray()) {
            final Object theArray = array;
            iterator = new Iterator() {
                int index = 0;

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return Array.getLength(theArray) > index;
                }

                public Object next() {
                    return Array.get(theArray, index++);
                }
            };
        } else if (array instanceof Iterator) {
            iterator = (Iterator) array;
        } else if (array instanceof Enumeration) {
            List l = new ArrayList();
            while (((Enumeration) array).hasMoreElements()) {
                l.add(((Enumeration) array).nextElement());
            }
            iterator = l.iterator();
        }

        if (sortProperties != null) {
            iterator = getSortedIterator(iterator, sortProperties);
        }

        if (iterator != null && iterator.hasNext()) {
            renderFragment("outputStart");
            int i = 0;
            while (iterator.hasNext()) {
                Object o = iterator.next();
                setAttribute("index", new Integer(i));
                setAttribute("count", new Integer(++i));
                if (o != null)
                    setAttribute("element", o);
                else
                    setAttribute("element", getParameter("nullValue"));
                renderFragment("output");
            }
            renderFragment("outputEnd");
        } else {
            renderFragment("empty");
        }
    }

    protected Iterator getSortedIterator(Iterator iterator, String sortProperties) {
        List l = new ArrayList();
        while (iterator.hasNext()) {
            l.add(iterator.next());
        }
        Collections.sort(l, new ForComparator(sortProperties));
        return l.iterator();
    }

    public void shutdown() {
        log.debug("Shutting down ForFormatter.");
    }

    public void init() {
        log.debug("Starting up ForFormatter.");
    }
}
