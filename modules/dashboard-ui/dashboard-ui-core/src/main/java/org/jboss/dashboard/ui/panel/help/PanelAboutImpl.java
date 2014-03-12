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
package org.jboss.dashboard.ui.panel.help;

import java.util.*;

/**
 *
 */
public class PanelAboutImpl implements PanelAbout {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelAboutImpl.class.getName());

    private Properties properties = new Properties();

    /**
     * Get supported properties
     *
     * @return A list of supported property keys
     */
    public String[] getProperties() {
        String[] idsToReturn = properties.keySet().toArray(new String[properties.size()]);
        final List<String> order = new ArrayList<String>();
        order.add(PROP_AUTHOR);
        order.add(PROP_COMPANY);
        order.add(PROP_URL);
        order.add(PROP_LICENSE);
        Arrays.sort(idsToReturn, new Comparator<String>() {
            public int compare(String s1, String s2) {
                int i1 = order.indexOf(s1);
                int i2 = order.indexOf(s2);
                if (i1 != i2)
                    return i1 - i2;
                if (i1 != -1)
                    return -1;
                if (i2 != -1)
                    return 1;
                return s1.compareTo(s2);
            }
        });
        return idsToReturn;
    }

    /**
     * Get property by key
     *
     * @param key
     * @return Value for given key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }


    public void addProperty(String name, String value) {
        properties.setProperty(name, value);
    }
}
