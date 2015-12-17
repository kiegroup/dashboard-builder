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

import org.jboss.dashboard.commons.misc.Chronometer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A collection of code block traces.
 * @see org.jboss.dashboard.profiler.CodeBlockTrace
 */
public class CodeBlockTraces {

    protected LinkedList<CodeBlockTrace> codeBlockList;
    protected CodeBlockTrace max;
    protected CodeBlockTrace min;
    protected long selfTimeMillis;
    protected long elapsedTimeMillis;

    public CodeBlockTraces() {
        codeBlockList = new LinkedList<CodeBlockTrace>();
        selfTimeMillis = 0;
        elapsedTimeMillis = 0;
    }

    public int size() {
        return codeBlockList.size();
    }

    public void addAll(CodeBlockTraces s) {
        if (s == null) return;
        for (CodeBlockTrace trace : s.codeBlockList) {
            add(trace);
        }
    }

    public void add(CodeBlockTrace trace) {
        codeBlockList.add(trace);
        long self = trace.getSelfTimeMillis();
        long elapsed = trace.getElapsedTimeMillis();
        this.selfTimeMillis += self;
        this.elapsedTimeMillis += elapsed;
        max = null;
        min = null;
    }

    public void remove(CodeBlockTrace trace) {
        codeBlockList.remove(trace);
        long self = trace.getSelfTimeMillis();
        long elapsed = trace.getElapsedTimeMillis();
        this.selfTimeMillis -= self;
        this.elapsedTimeMillis -= elapsed;
        max = null;
        min = null;
    }

    public long getSelfTimeMillis() {
        return selfTimeMillis;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
    }

    public long getAverageTimeMillis() {
        if (size() > 0) return selfTimeMillis / size();
        return 0;
    }

    public CodeBlockTrace get(int index) {
        return codeBlockList.get(index);
    }

    public CodeBlockTrace first() {
        if (codeBlockList.isEmpty()) return null;
        return codeBlockList.getFirst();
    }

    public CodeBlockTrace last() {
        if (codeBlockList.isEmpty()) return null;
        return codeBlockList.getLast();
    }

    public CodeBlockTrace max() {
        if (max == null) {
            for (CodeBlockTrace trace : codeBlockList) {
                if (max == null || trace.getSelfTimeMillis() > max.getSelfTimeMillis()) {
                    max = trace;
                }
            }
        }
        return max;
    }

    public CodeBlockTrace min() {
        if (min == null) {
            for (CodeBlockTrace trace : codeBlockList) {
                if (min == null || trace.getSelfTimeMillis() < min.getSelfTimeMillis()) {
                    min = trace;
                }
            }
        }
        return min;
    }

    public long getElapsedTimeMillis(CodeBlockType type, boolean excludeDescendants) {
        long total = 0;
        for (CodeBlockTrace trace : codeBlockList) {
            if (type != null) {
                if (!trace.getType().equals(type)) continue;
                if (excludeDescendants && trace.isDescendantOfType(type)) continue;
            }
            total += trace.getElapsedTimeMillis();
        }
        return total;
    }

    public Map<CodeBlockType, CodeBlockTraces> groupByType() {
        Map<CodeBlockType, CodeBlockTraces> results = new LinkedHashMap<CodeBlockType, CodeBlockTraces>();
        for (CodeBlockTrace trace : codeBlockList) {
            CodeBlockType type = trace.getType();
            CodeBlockTraces traces = results.get(type);
            if (traces == null) results.put(type, traces = new CodeBlockTraces());
            traces.add(trace);
        }
        return results;
    }

    public Map<String, CodeBlockTraces> groupById() {
        Map<String, CodeBlockTraces> results = new HashMap<String, CodeBlockTraces>();
        for (CodeBlockTrace trace : codeBlockList) {
            String id = trace.getId();
            CodeBlockTraces traces = results.get(id);
            if (traces == null) results.put(id, traces = new CodeBlockTraces());
            traces.add(trace);
        }
        return results;
    }

    public String printTree(long ignoreTracesMillis, boolean showContext, String rowSeparator, int indent) {
        if (size() == 0) return "";
        StringBuffer buf = new StringBuffer();

        String start = "* ";
        String sep = " - ";
        for (int i=0; i<size(); i++) {
            CodeBlockTrace trace = get(i);
            long elapsedTime = trace.getElapsedTimeMillis();
            if (ignoreTracesMillis > 0 && elapsedTime < ignoreTracesMillis) continue;

            String traceTime = Chronometer.formatElapsedTime(elapsedTime);
            String traceDescr = trace.getDescription();
            if (traceDescr.length() > 60) traceDescr = "..." + traceDescr.substring(traceDescr.length()-60);

            trace.printIndent(buf, indent);
            buf.append(start);
            if (trace.isRunning()) buf.append("RUNNING ");
            buf.append(traceTime).append(sep).append(trace.getType().getId()).append(sep).append(traceDescr).append(rowSeparator);

            if (showContext) {
                String traceContext = trace.printContext(false, "=", rowSeparator, indent+1);
                buf.append(traceContext).append(rowSeparator);
            }

            CodeBlockTraces children = trace.getChildren();
            if (children != null) {
                String childrenTree = children.printTree(ignoreTracesMillis, showContext, rowSeparator, indent+1);
                if (childrenTree != null) buf.append(childrenTree).append(rowSeparator);
            }
        }
        return buf.toString();
    }
}