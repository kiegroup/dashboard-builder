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
package org.jboss.dashboard.function;

import org.jboss.dashboard.profiler.CodeBlockTrace;

import java.util.Collection;
import java.util.Iterator;

import org.jboss.dashboard.annotation.Install;

/**
 * It calculates the min. number of a set of numbers.
 */
@Install
public class MinFunction extends AbstractFunction {

    /**
     * The code of the function.
     */
    public static final String CODE = "min";

    public MinFunction() {
        super();
    }

    public String getCode() {
        return CODE;
    }

    public boolean isTypeSupported(Class type) {
        return Number.class.isAssignableFrom(type);
    }

    public double scalar(Collection values) {
        double ret;
        CodeBlockTrace trace = new ScalarFunctionTrace(CODE, values).begin();
        try {
            if (values == null || values.isEmpty()) return 0;

            // Get the max. value from the collection.
            Number min = null;
            Iterator it = values.iterator();
            while (it.hasNext()) {
                Number n = (Number) it.next();
                if (n == null) continue;
                if (min == null || n.doubleValue() < min.doubleValue()) min = n;
            }
            // Adjust to the specified precision.
            if (min == null) return 0;
            ret = round(min.doubleValue(), precission);
        } finally {
            trace.end();
        }
        return ret;
    }
}