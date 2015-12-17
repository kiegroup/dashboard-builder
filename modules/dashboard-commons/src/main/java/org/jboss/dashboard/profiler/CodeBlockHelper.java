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

import java.util.Map;

/**
 * Helper class for the easy registration of custom code traces.
 * <p>Usage:
 * <pre>
 * CodeBlockType traceType = CodeBlockHelper.newCodeBlockType("REPORT", "Report generation");
 * CodeBlockTrace trace = CodeBlockHelper.newCodeBlockTrace(traceType, "report1", "Report 1", java.util.Collections.EMPTY_MAP).begin();
 * try {
 *     // Generate the report 1
 *     // ...
 * } finally {
 *     trace.end();
 * }
 * </pre>
 */
public class CodeBlockHelper {

    public static CodeBlockType newCodeBlockType(final String id, final String description) {
        return new CodeBlockType() {
            public String getId() {
                return id;
            }
            public String getDescription() {
                return description;
            }
        };
    }

    public static CodeBlockTrace newCodeBlockTrace(final CodeBlockType type, final String traceId, final String traceDescr, final Map<String, Object> traceContext) {
        return new CodeBlockTrace(traceId) {
            public String getDescription() {
                return traceDescr;
            }
            public CodeBlockType getType() {
                return type;
            }
            public Map<String, Object> getContext() {
                return traceContext;
            }
        };
    }
}