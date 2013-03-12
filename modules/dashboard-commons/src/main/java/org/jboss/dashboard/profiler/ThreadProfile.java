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

import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.error.ErrorReport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * A thread profile holds detailed information about the execution of a given thread. It holds informations
 * like:
 * <ul>
 * <li>The code trace tree with all the code block traces generated within the thread execution.
 * <li>The sequence of stack traces generated within the thread execution (only when the sampling/instrumentation mode is enabled).
 * <li>The collection of logs events generated within the thread execution
 * </ul>
 * All the information above it makes very easy to diagnose performance problems. 
 */
public class ThreadProfile {

    /** Logger */
    private static transient Log log = LogFactory.getLog(ThreadProfile.class.getName());

    /** The identifier */
    protected String id;

    /** The begin date */
    protected Date beginDate;

    /** The end date */
    protected Date endDate;

    /** The thread */
    protected Thread thread;

    /** The root trace. It stores all the executions traces generated within this thread. */
    protected CodeBlockTrace rootCodeBlock;

    /** The current block in execution */
    protected CodeBlockTrace codeBlockInProgress;

    /** Stack trace samples captured by the profiler. */
    protected List<StackTrace> stackTraces;

    /** Maximum thread length allowed. */
    protected long maxThreadDurationInMillis;

    /** Maximum stack trace length allowed. */
    protected int maxStackTraceLength;

    /** Flag indicating that the thread elapsed time has overtaken a specified threshold (See the <code>maxThreadDurationInMillis</code> property). */
    protected boolean elapsedTimeExceeded;

    /** Flag indicating that a too large stack trace (defined by the <code>maxStackTraceLength</code> property) has been produced by this thread. */
    protected boolean stackTraceTooLarge;

    /** Number of large stack traces captured fot this thread */
    protected int largeStackTracesCount;

    /** Context properties collected during profiling. */
    protected Map<String,Object> contextProperties;

    /** All context property names that any thread profile can hold. */
    protected static List<String> contextPropertyNames = new ArrayList<String>();

    /** The thread's logs */
    protected List<LogEvent> logEvents;

    /** The maximum log events to store. */
    protected int maxLogEvents;

    /** The log overflow flag. Enabled when the log size exceeds the max. events allowed. */
    protected transient boolean logOverflow;

    /** An error occurred during the thread execution (if any). */
    protected ErrorReport errorReport;

    /** Force this thread to be kept by the profiler within the list of target threads. */
    protected boolean targetThread;

    // Core property names
    public static final String THREAD_ID = "Thread id";
    public static final String THREAD_BEGIN_DATE = "Thread begin date";
    public static final String THREAD_GROUP = "Thread group";
    public static final String USER_LOGIN = "User login";
    public static final String USER_NAME = "User name";

    // Final states
    public static final String STATE_ERROR = "ERROR";
    public static final String STATE_COMPLETED = "COMPLETED";

    public ThreadProfile() {
        this.stackTraces = new ArrayList<StackTrace>();
        this.maxThreadDurationInMillis = 120000;
        this.maxStackTraceLength = 500;
        this.contextProperties = Collections.synchronizedMap(new LinkedHashMap<String,Object>());
        this.logEvents = null;
        this.maxLogEvents = 10000;
        this.logOverflow = false;
        this.errorReport = null;
        this.targetThread = false;
        clearStackTraces();
    }

    // Accessors

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getElapsedTime() {
        long endTime = (endDate != null ? endDate.getTime(): System.currentTimeMillis());
        return endTime - beginDate.getTime();
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isRunning() {
        return thread != null;
    }

    public long getMaxThreadDurationInMillis() {
        return maxThreadDurationInMillis;
    }

    public void setMaxThreadDurationInMillis(long maxThreadDurationInMillis) {
        this.maxThreadDurationInMillis = maxThreadDurationInMillis;
    }

    public int getMaxStackTraceLength() {
        return maxStackTraceLength;
    }

    public void setMaxStackTraceLength(int maxStackTraceLength) {
        this.maxStackTraceLength = maxStackTraceLength;
    }

    public boolean isElapsedTimeExceeded() {
        return elapsedTimeExceeded;
    }

    public boolean isStackTraceTooLarge() {
        return stackTraceTooLarge;
    }

    public CodeBlockTrace getCodeBlockInProgress() {
        return codeBlockInProgress;
    }

    public CodeBlockTrace getRootCodeBlock() {
        return rootCodeBlock;
    }

    public int getMaxLogEvents() {
        return maxLogEvents;
    }

    public void setMaxLogEvents(int maxLogEvents) {
        this.maxLogEvents = maxLogEvents;
    }

    public ErrorReport getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
        if (errorReport != null) errorReport.setCodeBlock(codeBlockInProgress);
    }

    public String getState() {
        if (isRunning()) return thread.getState().toString();
        if (errorReport != null) return STATE_ERROR;
        return STATE_COMPLETED;
    }
    // Context properties

    public void addContextProperty(String propName, Object propValue) {
        // Populate on demand the list of distinct property names.
        if (propName == null) return;
        if (!contextPropertyNames.contains(propName)) {
            contextPropertyNames.add(propName);
        }
        // Register the property value in the thread's context.
        contextProperties.put(propName, propValue);
    }

    public void addContextProperties(Map<String,Object> props) {
        for (String propName : props.keySet()) {
            if (propName == null) continue;
            addContextProperty(propName, props.get(propName));
        }
    }

    public Object getContextProperty(String propName) {
        return contextProperties.get(propName);
    }

    public Set<String> getContextPropertyNames() {
        return contextProperties.keySet();
    }

    public static List<String> getAllContextPropertyNames() {
        return Collections.unmodifiableList(contextPropertyNames);
    }

    public boolean isTargetThread() {
        return targetThread;
    }

    public void setTargetThread(boolean targetThread) {
        this.targetThread = targetThread;
    }

    // Lifecycle

    public void begin() {
        id = Thread.currentThread().getName();
        thread = Thread.currentThread();
        beginDate = new Date();
        endDate = null;
        codeBlockInProgress = null;
        rootCodeBlock = new RootTrace(thread, beginDate);
        rootCodeBlock.begin();
    }

    public void end() {
        endDate = new Date();
        rootCodeBlock.end();
        thread = null;
    }

    /** The very first trace added to the thread's execution just after its beginning. */
    static class RootTrace extends CodeBlockTrace {

        protected String group;
        protected String state;
        protected Date beginDate;
        protected Map<String,Object> context;

        public RootTrace(Thread thread, Date beginDate) {
            super(thread.getName());
            this.beginDate = beginDate;
            this.state = thread.getState().toString();
            this.group = thread.getThreadGroup().getName();
            this.context = buildContext();
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.THREAD;
        }

        public String getDescription() {
            return id;
        }

        public Map<String,Object> getContext() {
            return context;
        }

        protected Map<String,Object> buildContext() {
            Map<String,Object> ctx = new LinkedHashMap<String,Object>();
            ctx.put(THREAD_ID, id);
            ctx.put(THREAD_BEGIN_DATE, beginDate);
            ctx.put(THREAD_GROUP, group);

            ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
            if (threadProfile != null) threadProfile.addContextProperties(ctx);
            return ctx;
        }
    }

    public static String printStackTrace(Thread t, int lines) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("'" + t.getName() + "' '" + t.getState().toString() + "' ");
        StackTraceElement[] trace = t.getStackTrace();
        for (int i=0; i<trace.length && i<lines; i++) {
            pw.println("\tat " + trace[i]);
        }
        return sw.toString();
    }

    // Context handling

    public String printContext() {
        StringBuffer buf = new StringBuffer();
        if (codeBlockInProgress != null) buf.append(codeBlockInProgress.printContext(true));
        buf.append("\nElapsed time=").append(Chronometer.formatElapsedTime(getElapsedTime()));
        if (isRunning()) buf.append("\nCaller thread: ").append(printStackTrace(getThread(), 9999));
        return buf.toString();
    }

    // Profiling stuff

    public synchronized int getNumberOfSamples() {
        return stackTraces.size();
    }

    public synchronized long getSampleAverageTimeMillis() {
        if (stackTraces.size() == 0) return 0;
        return getProfileTimeMillis() / stackTraces.size();
    }

    public synchronized void clearStackTraces() {
        stackTraces.clear();
        this.elapsedTimeExceeded = false;
        this.stackTraceTooLarge = false;
        this.largeStackTracesCount = 0;
    }

    public synchronized void dumpStackTrace() {
        if (elapsedTimeExceeded || stackTraceTooLarge) {
            return;
        }

        StackTrace st = new StackTrace(thread.getStackTrace(), codeBlockInProgress);
        stackTraces.add(st);

        // Do not accept more stack traces if the transaction is already too long.
        if (getProfileTimeMillis() > maxThreadDurationInMillis) {
            elapsedTimeExceeded = true;
        }
        // If the last five added stack traces are too large then stop recollecting traces.
        if (st.length() > maxStackTraceLength) {
            largeStackTracesCount++;
            stackTraceTooLarge = largeStackTracesCount > 5;
        } else {
            largeStackTracesCount = 0;
        }
    }

    public synchronized long getProfileTimeMillis() {
        if (stackTraces.isEmpty() || stackTraces.size()==1) return 0;
        StackTrace first = stackTraces.get(0);
        StackTrace last = stackTraces.get(stackTraces.size()-1);
        return last.getCreationTimeMillis()-first.getCreationTimeMillis();
    }

    /**
     * Calculates the list of time traces.
     * Each time trace represents a method call and contains the estimated time spent by the thread in the execution of such method.
     */
    public synchronized List<TimeTrace> calculateTimeTraces() {
        // Consider all samples and start at level 0.
        List<TimeTrace> traces = calculateTimeTraces(0, stackTraces.size(), 0);
        if (traces == null) return null;

        // Get a plain list of time traces ordered by its begin time.
        List<TimeTrace> timeTraces = new ArrayList<TimeTrace>();
        toPlainList(traces, timeTraces);

        // Get a plain list of code traces ordered by its begin time.
        CodeBlockTraces codeTraces = rootCodeBlock.toPlainList();

        // Populate the time traces with code block traces.
        for (int codeIndex=0; codeIndex<codeTraces.size(); codeIndex++) {
            CodeBlockTrace codeTrace = codeTraces.get(codeIndex);
            boolean added = false;
            for (int traceIndex=timeTraces.size()-1; traceIndex>=0 && !added; traceIndex--) {
                TimeTrace timeTrace = timeTraces.get(traceIndex);
                if (codeTrace.getBeginTimeMillis() >= timeTrace.getBeginTimeMillis() && codeTrace.getEndTimeMillis() <= timeTrace.getEndTimeMillis()) {
                    timeTrace.getCodeBlockTraces().add(codeTrace);
                    added = true;
                }
            }
            if (!added && log.isDebugEnabled()) {
                log.debug("Time trace not found for code block.\n" + codeTrace.toString());
            }
        }
        // Return the time traces each one containing its code block traces.
        return traces;
    }

    /**
     * Search for two consecutive stack traces in the specified sample interval & stack level.
     */
    protected List<TimeTrace> calculateTimeTraces(int sampleStart, int sampleEnd, int stackLevel) {
        if (stackTraces == null || stackTraces.size() < 2) return null;

        // Look for samples
        List<TimeTrace> results = new ArrayList<TimeTrace>();
        int newTraceStart = -1;
        for (int i=sampleStart+1; i<sampleEnd; i++) {
            StackTrace previous = stackTraces.get(i-1);
            StackTrace current = stackTraces.get(i);
            StackTraceElement prevEl = previous.get(stackLevel);
            StackTraceElement currEl = current.get(stackLevel);

            // Mark the beginning of a new stack trace.
            if (prevEl != null && prevEl.equals(currEl)) {
                if (newTraceStart == -1) {
                    newTraceStart = i-1;
                }
            }
            // New trace found.
            else if (newTraceStart != -1) {
                TimeTrace newStackTrace = createTimeTrace(newTraceStart, i, stackLevel);
                results.add(newStackTrace);
                newTraceStart = -1;
            }
        }
        // New trace found.
        if (newTraceStart != -1) {
            TimeTrace newStackTrace = createTimeTrace(newTraceStart, sampleEnd, stackLevel);
            results.add(newStackTrace);
        }
        return results;
    }

    /**
     * Create a time trace with samples belonging to the specified interval.
     */
    protected TimeTrace createTimeTrace(int traceStart, int traceEnd, int stackLevel) {
        // Determine the stack length.
        int stackLength = stackLevel+1;
        boolean stop = false;
        while (!stop) {
            StackTrace first = stackTraces.get(traceStart);
            StackTraceElement firstEl = first.get(stackLength);
            for (int i=traceStart+1; i < traceEnd && !stop; i++) {
                StackTrace current = stackTraces.get(i);
                StackTraceElement currEl = current.get(stackLength);
                if (firstEl == null || !firstEl.equals(currEl)) {
                    stop = true;
                }
            }
            if (!stop) {
                stackLength++;
            }
        }
        // Create the stack trace.
        StackTraceElement[] trace = stackTraces.get(traceStart).from(0, stackLength);
        List<StackTrace> samples = new ArrayList(stackTraces.subList(traceStart, traceEnd));
        List<TimeTrace> children = calculateTimeTraces(traceStart, traceEnd, stackLength);
        return new TimeTrace(trace, samples, children, null);
    }

    protected void toPlainList(List<TimeTrace> traces, List<TimeTrace> results) {
        if (traces.isEmpty()) return;

        results.addAll(traces);
        for (TimeTrace trace : traces) {
            toPlainList(trace.getChildren(), results);
        }
    }

    // Log4J support

    public void addLog4JEvent(LoggingEvent event) {
        addLogEvent(new Log4JEvent(codeBlockInProgress, event));
    }

    public void addCodeBlockBeginEvent(CodeBlockTrace trace) {
        addLogEvent(new CodeBlockBeginEvent(trace));
    }

    public void addCodeBlockEndEvent(CodeBlockTrace trace) {
        addLogEvent(new CodeBlockEndEvent(trace));
    }

    public void addLogEvent(LogEvent event) {
        if (logEvents == null) logEvents = new ArrayList<LogEvent>();
        logEvents.add(event);

        // Avoid memory problems.
        if (logOverflow) logEvents.remove(1);
        else if (logOverflow = logEvents.size() > maxLogEvents) logEvents.add(0, createLogOverflowMessage());

    }

    protected MessageEvent createLogOverflowMessage() {
        return new MessageEvent(codeBlockInProgress, "\n! AVOID MEMORY OVERFLOW. ONLY THE LOW " + maxLogEvents + " EVENTS RECORDED !\n");
    }

    public List<LogEvent> getLogEvents(boolean includeCodeBlockEvents, boolean removeCodeBlocksWithoutLogs) {
        if (logEvents == null) return Collections.EMPTY_LIST;

        List<LogEvent> result = new ArrayList<LogEvent>(logEvents);
        if (!includeCodeBlockEvents) {
            // Return only Log4J events.
            Iterator<LogEvent> it = result.iterator();
            while (it.hasNext()) {
                LogEvent event = it.next();
                if (!(event instanceof Log4JEvent)) it.remove();
            }
        } else if (removeCodeBlocksWithoutLogs) {
            // Return all events but those code block begin/end events containing no logs.
            int index = 0;
            while (index < result.size()) {
                // Look for two consecutive trace begin/end events (no log events between).
                if (!(result.get(index++) instanceof CodeBlockBeginEvent)) continue;
                if (index >= result.size()) continue;
                if (!(result.get(index) instanceof CodeBlockEndEvent)) continue;

                // Remove such begin/end events.
                result.remove(index--);
                result.remove(index--);
                if (index < 0) index = 0;
            }
        } else {
            // Return all the events.
            return Collections.unmodifiableList(logEvents);
        }
        return result;
    }

    /**
     * Base class for log events.
     */
    public static abstract class LogEvent {

        CodeBlockTrace codeBlockTrace;
        long timestamp;

        protected LogEvent(CodeBlockTrace trace, long timestamp) {
            this.codeBlockTrace = trace;
            this.timestamp = timestamp;
        }

        public CodeBlockTrace getCodeBlockTrace() {
            return codeBlockTrace;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public abstract String format(Map<String,Object> props);
    }

    /**
     * The CodeBlockTrace begin event.
     */
    public static class CodeBlockBeginEvent extends LogEvent {

        public CodeBlockBeginEvent(CodeBlockTrace trace) {
            super(trace, trace.getBeginTimeMillis());
        }

        public String format(Map<String, Object> props) {
            String traceTime = Chronometer.formatElapsedTime(codeBlockTrace.getElapsedTimeMillis());
            String traceStr = codeBlockTrace.getType().getId() + " - " + codeBlockTrace.getDescription() + " - " + traceTime + "\n";
            return "BEGIN " + traceStr;
        }
    }

    /**
     * The CodeBlockTrace end event.
     */
    public static class CodeBlockEndEvent extends LogEvent {

        public CodeBlockEndEvent(CodeBlockTrace trace) {
            super(trace, trace.getEndTimeMillis());
        }

        public String format(Map<String, Object> props) {
            String traceTime = Chronometer.formatElapsedTime(codeBlockTrace.getElapsedTimeMillis());
            String traceStr = codeBlockTrace.getType().getId() + " - " + codeBlockTrace.getDescription() + " - " + traceTime + "\n";
            return "END " + traceStr;
        }
    }

    /**
     * Class that holds events generated by the Log4j.
     */
    public static class Log4JEvent extends LogEvent {

        public static final String LAYOUT = "layout";

        LoggingEvent log4jEvent;

        private Log4JEvent(CodeBlockTrace codeBlockTrace, LoggingEvent log4jEvent) {
            super(codeBlockTrace, log4jEvent.getTimeStamp());
            this.log4jEvent = log4jEvent;
        }

        public String format(Map<String, Object> props) {
            Layout layout = (Layout) props.get(LAYOUT);
            return layout.format(log4jEvent);
        }
    }

    /**
     * A simple event holding a message.
     */
    public static class MessageEvent extends LogEvent {

        protected String message;

        private MessageEvent(CodeBlockTrace codeBlockTrace, String message) {
            super(codeBlockTrace, System.currentTimeMillis());
            this.message = message;
        }

        public String format(Map<String, Object> props) {
            return message;
        }
    }
}