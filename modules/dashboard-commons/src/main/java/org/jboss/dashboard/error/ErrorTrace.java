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
package org.jboss.dashboard.error;

import org.jboss.dashboard.profiler.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A profiler trace generated always that an unexpected error occurs within the platform.
 */
public class ErrorTrace extends CodeBlockTrace {

    public static final String ERROR_ID = "Error id";
    public static final String ERROR_DATE = "Error date";
    public static final String ERROR_STACKTRACE = "Error stack trace";

    protected ErrorReport error;
    protected Map<String,Object> context;

    public ErrorTrace(ErrorReport error) {
        super(error.getId());
        this.error = error;
        context = new LinkedHashMap<String,Object>();
        context.put(ERROR_ID, error.getId());
        context.put(ERROR_DATE, error.getDate());
        context.put(ERROR_STACKTRACE, error.printExceptionTrace());

        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        if (threadProfile != null) threadProfile.addContextProperties(context);
    }

    public CodeBlockType getType() {
        return CoreCodeBlockTypes.ERROR;
    }

    public String getDescription() {
        Throwable root = ErrorManager.lookup().getRootCause(error.getException());
        return root.getMessage();
    }

    public Map<String,Object> getContext() {
        return context;
    }
}
