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
package org.jboss.dashboard.database.hibernate;

import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;

import java.util.HashMap;
import java.util.Map;

public class SQLStatementTrace extends CodeBlockTrace {

    protected Map<String,Object> context;

    public SQLStatementTrace(String sql) {
        super(stripAfterWhere(sql));
        context = new HashMap<String,Object>();
        context.put("SQL", sql);
    }

    public CodeBlockType getType() {
        return CoreCodeBlockTypes.SQL;
    }

    public String getDescription() {
        return (String) context.get("SQL");
    }

    public Map<String,Object> getContext() {
        return context;
    }

    /**
     * To group sensibly and to avoid recording sensitive data, Don't record the where clause
     * (only used for dynamic SQL since parameters aren't included in prepared statements)
     * @return subset of passed SQL up to the where clause.
     */
    public static String stripAfterWhere(String sql) {
        for (int i=0; i<sql.length()-4; i++) {
            if (sql.charAt(i)=='w' || sql.charAt(i)=='W') {
                if (sql.substring(i+1, i+5).equalsIgnoreCase("here")) {
                    return sql.substring(0, i);
                }
            }
        }
        return sql;
    }
}