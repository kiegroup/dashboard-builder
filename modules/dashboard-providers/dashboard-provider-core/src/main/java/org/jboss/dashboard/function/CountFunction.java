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

import java.util.*;

import org.jboss.dashboard.annotation.Install;

/**
 * It calculates the number of distinct occurrences inside a given collection.
 */
@Install
public class CountFunction extends AbstractFunction {

    /**
     * The code of the function.
     */
    public static final String CODE = "count";

    protected boolean discardDuplicates;
    protected String code;

    public CountFunction() {
        super();
        discardDuplicates = false;
        code = CODE;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDiscardDuplicates() {
        return discardDuplicates;
    }

    public void setDiscardDuplicates(boolean discardDuplicates) {
        this.discardDuplicates = discardDuplicates;
    }

    public boolean isTypeSupported(Class type) {
        return true;
    }

    public double scalar(Collection values) {
        double ret;
        CodeBlockTrace trace = new ScalarFunctionTrace(CODE, values).begin();
        try {
            if (values == null || values.isEmpty()) return 0;
            if (!discardDuplicates) return values.size();

            // Return the number of distinct items in the collection.
            Set distincts = new HashSet();
            Iterator it = values.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (distincts.contains(o)) continue;
                distincts.add(o);
            }
            ret = distincts.size();
        } finally {
            trace.end();
        }
        return ret;
    }
}