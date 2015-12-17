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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * A time trace is a way to group a sequence of stack traces in a hierarchical way.
 */
public class TimeTrace extends StackTrace {

    protected TimeTrace parent;
    protected List<StackTrace> stackTraces;
    protected List<TimeTrace> children;
    protected CodeBlockTraces codeBlockTraces;
    protected int level;

    public TimeTrace(StackTraceElement[] stackTrace, List<StackTrace> samples, List<TimeTrace> children, CodeBlockTrace context) {
        super(stackTrace, context);
        this.parent = null;
        this.codeBlockTraces = new CodeBlockTraces();
        this.level = 0;
        setStackTraces(samples);
        setChildren(children);
    }

    public TimeTrace getParent() {
        return parent;
    }

    public void setParent(TimeTrace parent) {
        this.parent = parent;
    }

    public List<TimeTrace> getChildren() {
        return children;
    }

    public void setChildren(List<TimeTrace> children) {
        this.children = children;
        for (TimeTrace child : children) {
            child.setParent(this);
            child.setLevel(level+1);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<StackTrace> getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(List<StackTrace> stackTraces) {
        this.stackTraces = stackTraces;
    }

    public CodeBlockTraces getCodeBlockTraces() {
        return codeBlockTraces;
    }

    public long getBeginTimeMillis() {
        StackTrace first = stackTraces.get(0);
        return first.getCreationTimeMillis();
    }

    public long getEndTimeMillis() {
        StackTrace last = stackTraces.get(stackTraces.size()-1);
        return last.getCreationTimeMillis();
    }

    public long getElapsedTimeMillis() {
        return getEndTimeMillis()-getBeginTimeMillis();
    }

    public long getSampleAverageTimeMillis() {
        return getElapsedTimeMillis() / stackTraces.size();
    }

    public long getSelfTimeMillis() {
        long total = getElapsedTimeMillis();
        for (TimeTrace child : children) {
            total -= child.getElapsedTimeMillis();
        }
        return total;
    }

    /**
     * If this is a child stack trace (has a parent) then only print the stack trace fragment that belongs exclusively to itself.<br>
     * Else print the full stack trace.
     */
    public String printChildStackTrace() {
        if (parent == null) return printStackTrace();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(stackTrace[0]);
        int shortLength = length() - parent.length();
        for (int i=1; i <= shortLength; i++) {
            StackTraceElement trace = stackTrace[i];
            pw.println("\tat " + trace);
        }
        pw.println("\t(... see the parent\u0027s stack trace)");
        return sw.toString();
    }
}