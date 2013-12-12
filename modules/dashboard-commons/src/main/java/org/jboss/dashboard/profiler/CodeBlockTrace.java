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
package org.jboss.dashboard.profiler;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.text.DateFormat;
import java.util.*;

/**
 * A code block trace is the way that the platform has to register the execution of a given portion of code (code block).
 * Such registry contains:
 * <ul>
 * <li>A reference to the belonging thread.
 * <li>The begin/end times of the code block.
 * <li>A map of variables representing the execution context.
 * <li>A parent trace.
 * <li>A set of child traces.   
 * </ul>
 */
public abstract class CodeBlockTrace {

    protected static DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public static boolean RUNTIME_CONTRAINTS_ENABLED = true;

    ThreadProfile threadProfile;
    protected CodeBlockTrace parent;
    protected CodeBlockTraces children;
    protected String id;
    protected long beginTimeMillis;
    protected long endTimeMillis;
    protected long childrenElapsedTimeMillis;
    protected Set<CodeBlockType> ancestorTypes;
    protected List<RuntimeConstraint> runtimeConstraints;

    public CodeBlockTrace(String id) {
        this.threadProfile = null;
        this.parent = null;
        this.id = id;
        this.beginTimeMillis = -1;
        this.endTimeMillis = -1;
        this.childrenElapsedTimeMillis = 0;
        this.children = null;
        this.ancestorTypes = null;
    }

    public abstract CodeBlockType getType();
    public abstract String getDescription();
    public abstract Map<String,Object> getContext();

    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (id == null) return false;

            CodeBlockTrace other = (CodeBlockTrace) obj;
            return id.equals(other.id);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public ThreadProfile getThreadProfile() {
        return threadProfile;
    }

    public void setThreadProfile(ThreadProfile threadProfile) {
        this.threadProfile = threadProfile;
    }

    public CodeBlockTrace getParent() {
        return parent;
    }

    public void setParent(CodeBlockTrace parent) {
        this.parent = parent;
    }

    public CodeBlockTraces getChildren() {
        return children;
    }

    public void setChildren(CodeBlockTraces children) {
        this.children = children;
    }

    public Set<CodeBlockType> getAncestorTypes() {
        return ancestorTypes;
    }

    public void setAncestorTypes(Set<CodeBlockType> ancestorTypes) {
        this.ancestorTypes = ancestorTypes;
    }

    public boolean isDescendantOfType(CodeBlockType type) {
        return ancestorTypes !=  null && ancestorTypes.contains(type);
    }

    public void addChild(CodeBlockTrace child) {
        // Avoid memory problems
        if (children != null && children.size() > 10000) return;

        Set<CodeBlockType> childAncestorTypes = new HashSet<CodeBlockType>();
        childAncestorTypes.add(this.getType());
        if (ancestorTypes != null) childAncestorTypes.addAll(ancestorTypes);
        child.setAncestorTypes(childAncestorTypes);

        child.setParent(this);
        if (children == null) children = new CodeBlockTraces();
        children.add(child);
    }

    public void removeChild(CodeBlockTrace child) {
        if (children != null) {
            child.setParent(null);
            children.remove(child);
        }
    }

    public CodeBlockTraces getDescendants() {
        if (children == null) return null;
        CodeBlockTraces result = new CodeBlockTraces();
        for (int i=0; i<children.size(); i++) {
            CodeBlockTrace child = children.get(i);
            CodeBlockTraces descendants = child.getDescendants();
            result.add(child);
            result.addAll(descendants);
        }
        return result;
    }

    public int getLevel() {
        if (parent == null) return 0;
        return parent.getLevel() + 1;
    }
    
    public CodeBlockTraces toPlainList() {
        CodeBlockTraces results = new CodeBlockTraces();
        results.add(this);
        results.addAll(getDescendants());
        return results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getBeginTimeMillis() {
        return beginTimeMillis;
    }

    public void setBeginTimeMillis(long beginTimeMillis) {
        this.beginTimeMillis = beginTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public long getElapsedTimeMillis() {
        if (isRunning()) return System.currentTimeMillis()-beginTimeMillis;
        return endTimeMillis-beginTimeMillis;
    }

    public long getSelfTimeMillis() {
        if (isRunning()) return 0;
        long total = getElapsedTimeMillis();
        long self = total - childrenElapsedTimeMillis;
        return self > 0 ? self : 0;
    }

    public long getChildrenElapsedTimeMillis() {
        return childrenElapsedTimeMillis;
    }

    public boolean isRunning() {
        return endTimeMillis == -1;
    }

    public CodeBlockTrace begin() {
        if (beginTimeMillis == -1) {
            beginTimeMillis = System.currentTimeMillis();
            ThreadProfile threadExec = Profiler.lookup().getCurrentThreadProfile();
            if (threadExec != null) {
                this.threadProfile = threadExec; 
                CodeBlockTrace currentTrace = threadExec.codeBlockInProgress;
                if (currentTrace != null) currentTrace.addChild(this);
                threadExec.codeBlockInProgress = this;
            }
        }
        return this;
    }

    public void end() {
        if (endTimeMillis == -1) {
            endTimeMillis = System.currentTimeMillis();
            if (parent != null) parent.childrenElapsedTimeMillis += getElapsedTimeMillis();
            ThreadProfile threadExec = Profiler.lookup().getCurrentThreadProfile();
            if (threadExec != null) {
                // When a trace ends set as current the last running parent.
                CodeBlockTrace runningParent = parent;
                while (runningParent != null && !runningParent.isRunning()) runningParent = runningParent.parent;
                threadExec.codeBlockInProgress = runningParent;
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getType().getId()).append("=").append(getDescription()).append("\n");
        if (beginTimeMillis > 0) buf.append("Begin date").append("=").append(dateFormat.format(new Date(getBeginTimeMillis()))).append("\n");
        if (endTimeMillis > 0) buf.append("End date").append("=").append(dateFormat.format(new Date(getEndTimeMillis()))).append("\n");
        buf.append(printContext(false, "=", "\n"));
        return buf.toString();
    }

    // Context manipulation

    public String printContext(boolean includeParent) {
        return printContext(includeParent, "=", "\n", 0);
    }

    public String printContext(boolean includeParent, String valueSeparator, String rowSeparator) {
        return printContext(includeParent, valueSeparator, rowSeparator, 0);
    }

    public String printContext(boolean includeParent, String valueSeparator, String rowSeparator, int indent) {
        if (includeParent && parent != null) {
            StringBuffer buf = new StringBuffer();
            buf.append(parent.printContext(true, valueSeparator, rowSeparator, indent));
            buf.append(rowSeparator);
            buf.append(printMap(getContext(), valueSeparator, rowSeparator, indent));
            return buf.toString();
        } else {
            return printMap(getContext(), valueSeparator, rowSeparator, indent);
        }
    }

    public void printIndent(StringBuffer buf, int indent) {
        for (int i=0; i<indent; i++) {
            buf.append("    ");
        }
    }

    public String printMap(Map m, String valueSeparator, String rowSeparator) {
        return printMap(m, valueSeparator, rowSeparator, 0);
    }

    public String printMap(Map m, String valueSeparator, String rowSeparator, int indent) {
        StringBuffer buf = new StringBuffer();
        Iterator mit = m.keySet().iterator();
        while (mit.hasNext()) {
            Object key = mit.next();
            Object value = m.get(key);
            if (buf.length() > 0) buf.append(rowSeparator);
            printIndent(buf, indent);
            buf.append(key != null ? key.toString() : "null");
            buf.append(valueSeparator);
            buf.append(value != null ? value.toString() : "null");
        }
        return buf.toString();
    }

    // Runtime constraints handling

    public void addRuntimeConstraint(RuntimeConstraint runtimeConstraint) {
        if (runtimeConstraints == null) runtimeConstraints = new ArrayList<RuntimeConstraint>();
        runtimeConstraints.add(runtimeConstraint);
    }

    public void checkRuntimeConstraints() throws Exception {
        if (!RUNTIME_CONTRAINTS_ENABLED) return;

        CodeBlockTrace _trace = this;
        while (_trace != null) {
            if (_trace.runtimeConstraints != null) {
                for (RuntimeConstraint runtimeConstraint : _trace.runtimeConstraints) {
                    runtimeConstraint.validate();
                }
            }
            _trace = _trace.parent;
        }
    }
}