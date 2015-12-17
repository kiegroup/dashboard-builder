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

/**
 * A snapshot of a thread execution at a given time.
 */
public class StackTrace {

    protected long creationTimeMillis;
    protected StackTraceElement[] stackTrace;
    protected CodeBlockTrace context;

    public StackTrace(StackTraceElement[] stackTrace, CodeBlockTrace context) {
        this.creationTimeMillis = System.currentTimeMillis();
        this.stackTrace = stackTrace;
        this.context = context;
    }

    public boolean equals(StackTrace other) {
        if (other.length() != length()) {
            return false;
        }

        for (int i=length()-1; i>=0; i--) {
            if (!equals(stackTrace[i], other.stackTrace[i])) {
                return false;
            }
        }
        return true;
    }

    public CodeBlockTrace getContext() {
        return context;
    }

    public void setContext(CodeBlockTrace context) {
        this.context = context;
    }

    public long getCreationTimeMillis() {
        return creationTimeMillis;
    }

    public int length() {
        if (stackTrace == null) return 0;
        return stackTrace.length;
    }

    public StackTraceElement get(int index) {
        if (index >= stackTrace.length) return null;
        return stackTrace[reverseIndex(index)];
    }

    public StackTraceElement last() {
        if (stackTrace.length == 0) return null;
        return stackTrace[0];
    }

    public StackTraceElement first() {
        if (stackTrace.length == 0) return null;
        return stackTrace[reverseIndex(0)];
    }

    public StackTraceElement[] from(int begin, int length) {
        StackTraceElement[] result = new StackTraceElement[length];
        System.arraycopy(stackTrace, stackTrace.length-begin-length , result, 0, length);
        return result;
    }

    protected int reverseIndex(int index) {
        return stackTrace.length-1-index;
    }

    /**
     * Intersect two stack traces.
     * <br>
     * <code>A B C D E F G H I J K L M N</code><br>
     * <code>A B C D E F G H 1 2 3 4 5 6 7 8 9 0</code><br>
     * <br>
     * Cut both to the length of the shortest one<br>
     * <code>A B C D E F G H I J K L M N</code><br>
     * <code>A B C D E F G H 1 2 3 4 5 6</code><br>
     * <br>
     * If length = 1 return the element as the intersect.<br>
     * Else check the element in the middle: G<br>
     * <br>
     * If the middle is not equals then return the intersect of the first half.<br>
     * Else return the intersect of the whole by comparing all the elements.<br>
     */
    protected StackTrace intersect(StackTrace other) {
        int length = length() < other.length() ? length() : other.length();
        if (length == 0) return null;

        int commonLength = 0;
        boolean equals = true;
        for (int i=0; i<length && equals; i++) {
            StackTraceElement e1 = get(i);
            StackTraceElement e2 = other.get(i);
            if (equals = equals(e1, e2)) commonLength++;
        }
        // If intersect fails at the very beginning then return null.
        if (commonLength == 0) return null;

        // Return the common stack trace fragment.
        StackTraceElement[] result = new StackTraceElement[commonLength];
        System.arraycopy(stackTrace, stackTrace.length - commonLength, result, 0, commonLength);
        return new StackTrace(result, null);
    }

    protected boolean equals(StackTraceElement e1, StackTraceElement e2) {
        if (e1 == e2) return true;
        if (e1.getLineNumber() != e2.getLineNumber()) return false;
        if (!eq(e1.getMethodName(), e2.getMethodName())) return false;
        if (!eq(e1.getClassName(), e2.getClassName())) return false;
        if (!eq(e1.getFileName(), e2.getFileName())) return false;
        return true;
    }

    protected boolean eq(Object a, Object b) {
        return a==b || (a != null && a.equals(b));
    }

    public String printStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(stackTrace[0]);
        for (int i=1; i < stackTrace.length; i++) {
            StackTraceElement trace = stackTrace[i];
            pw.println("\tat " + trace);
        }
        return sw.toString();
    }

    public static String printStackElement(StackTraceElement e, int maxLength) {
        String className = e.getClassName();
        if (className.length() > maxLength) className = className.substring(className.length() - maxLength);
        return className + "." + e.getMethodName() +
            (e.isNativeMethod() ? "(Native Method)" :
             (e.getFileName() != null && e.getLineNumber() >= 0 ?
              "(" + e.getFileName() + ":" + e.getLineNumber() + ")" :
              (e.getFileName() != null ?  "("+e.getFileName()+")" : "(Unknown Source)")));
    }
}