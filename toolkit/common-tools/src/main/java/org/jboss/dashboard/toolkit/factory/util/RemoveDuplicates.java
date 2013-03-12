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
package org.jboss.dashboard.toolkit.factory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class RemoveDuplicates extends TextConverterFunction {
     
    public String convertValue(String value) {
        StringTokenizer strtk = new StringTokenizer(value, ",");
        List l = new ArrayList();
        while (strtk.hasMoreTokens()) {
            String tk = strtk.nextToken();
            if (!l.contains(tk)) {
                l.add(tk);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < l.size(); i++) {
            String s = (String) l.get(i);
            if (i > 0) sb.append(',');
            sb.append(s);
        }
        return sb.toString();
    }
}
