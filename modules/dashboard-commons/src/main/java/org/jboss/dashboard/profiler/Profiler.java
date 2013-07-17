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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.FactoryWork;
import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.error.ErrorReport;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The profiler keeps track of threads execution with the following goals in mind:
 * <ul>
 * <li>Provide real-time information about where threads spend their execution time.
 * <li>Provide an easy interface to quick understand why a given thread is slower than expected.
 * <li>Do not impact the application performance.
 * </ul>
 */
@ApplicationScoped
@Named("profiler")
public class Profiler implements Runnable {

    public static Profiler lookup() {
        return (Profiler) CDIBeanLocator.getBeanByName("profiler");
    }

    public static String printCurrentContext() {
        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        CodeBlockTrace blockInProgress = threadProfile.getCodeBlockInProgress();
        return blockInProgress.printContext(true);
    }

    /** Logger */
    private static transient Logger log = LoggerFactory.getLogger(Profiler.class.getName());

    /**
     * Amount of time in milliseconds the profile will remain idle between two consecutive profiling snapshots.
     */
    @Inject @Config("100")
    protected long idleTimeInMillis;

    /**
     * Switch on the profiler on start.
     */
    @Inject @Config("false")
    protected boolean runOnStart;

    /**
     * If a thread's execution time overtake a specified amount of time then the profiler will automatically stop getting profiling data for such thread,
     * Normally, the data obtained by the profiler until that moment will be enough to determine the thread's performance problem.
     */
    @Inject @Config("300000")
    protected long maxThreadProfilingTimeMillis;

    /**
     * If a thread's stack trace length overtake at any moment a specified limit then the profiler will stop getting profiling data for such thread,
     * Normally, the data obtained by the profiler until that moment will be enough to determine the thread's performance problem.
     * This property is very useful to detect and protect the profiler against threads that perform uncontrolled recursive calls.
     */
    @Inject @Config("500")
    protected int maxThreadStackTraceLength;

    /**
     * The minimum required time length in milliseconds for a code trace to be saved by the profiler.
     */
    @Inject @Config("10")
    protected long minCodeTraceTimeMillis;

    /**
     * Maximum number of completed threads to keep into the registry.
     */
    @Inject @Config("100")
    protected int completedThreadsMaxSize;

    /**
     * Save as completed only the threads which execution exceeds a maximum amount of time.
     */
    @Inject @Config("60000")
    protected long completedThreadsMinTimeMillis;

    /**
     * Flag indicating whether threads that ends with errors should be added to the thread registry.
     */
    @Inject @Config("true")
    protected boolean completedThreadsErrorsEnabled;

    /**
     * The profiler thread.
     */
    protected Thread profilerThread;

    /**
     * The profiler thread status flag.
     */
    protected transient boolean running;

    /**
     * Current active threads
     */
    protected List<ThreadProfile> activeThreads;

    /**
     * Completed threads that exceed the max. time expected.
     */
    protected List<ThreadProfile> completedThreads;

    /**
     * Filter that a completed thread must satisfy in order to be added to the registry.
     */
    protected ThreadProfileFilter completedThreadsFilter;

    /**
     * The thread profile for the current thread.
     */
    protected ThreadLocal currentThreadProfile;

    public Profiler() {
        runOnStart = false;
        running = false;
        idleTimeInMillis = 100;
        maxThreadProfilingTimeMillis = 300000;
        maxThreadStackTraceLength = 500;
        activeThreads = new ArrayList();
        completedThreads = new ArrayList();
        completedThreadsMinTimeMillis = 60000;
        completedThreadsMaxSize = 100;
        completedThreadsErrorsEnabled = true;
        completedThreadsFilter = new ThreadProfileFilter();
        minCodeTraceTimeMillis = 10;
        currentThreadProfile = new ThreadLocal();
    }

    public boolean isRunOnStart() {
        return runOnStart;
    }

    public void setRunOnStart(boolean runOnStart) {
        this.runOnStart = runOnStart;
    }

    public long getIdleTimeInMillis() {
        return idleTimeInMillis;
    }

    public void setIdleTimeInMillis(long idleTimeInMillis) {
        this.idleTimeInMillis = idleTimeInMillis;
    }

    public long getMaxThreadProfilingTimeMillis() {
        return maxThreadProfilingTimeMillis;
    }

    public void setMaxThreadProfilingTimeMillis(long maxThreadProfilingTimeMillis) {
        this.maxThreadProfilingTimeMillis = maxThreadProfilingTimeMillis;
    }

    public int getMaxThreadStackTraceLength() {
        return maxThreadStackTraceLength;
    }

    public void setMaxThreadStackTraceLength(int maxThreadStackTraceLength) {
        this.maxThreadStackTraceLength = maxThreadStackTraceLength;
    }

    public int getCompletedThreadsMaxSize() {
        return completedThreadsMaxSize;
    }

    public void setCompletedThreadsMaxSize(int completedThreadsMaxSize) {
        this.completedThreadsMaxSize = completedThreadsMaxSize;
    }

    public boolean isCompletedThreadsErrorsEnabled() {
        return completedThreadsErrorsEnabled;
    }

    public void setCompletedThreadsErrorsEnabled(boolean completedThreadsErrorsEnabled) {
        this.completedThreadsErrorsEnabled = completedThreadsErrorsEnabled;
    }

    public long getMinCodeTraceTimeMillis() {
        return minCodeTraceTimeMillis;
    }

    public void setMinCodeTraceTimeMillis(long minCodeTraceTimeMillis) {
        this.minCodeTraceTimeMillis = minCodeTraceTimeMillis;
    }

    public ThreadProfileFilter getCompletedThreadsFilter() {
        return completedThreadsFilter;
    }

    public void setCompletedThreadsFilter(ThreadProfileFilter completedThreadsFilter) {
        this.completedThreadsFilter = completedThreadsFilter;
    }

    // Lifecycle operations

    @PostConstruct
    public void start() throws Exception {
        if (runOnStart) {
            turnOn();
        }
    }

    @PreDestroy
    public void shutdown() throws Exception {
        turnOff();
    }

    public void turnOn() {
        if (!running) {
            running = true;
            profilerThread = new Thread(this, "Profiler thread");
            profilerThread.setPriority(Thread.MAX_PRIORITY);
            profilerThread.setDaemon(true);
            profilerThread.start();
        }
    }

    public void turnOff() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // Runnable interface

    public void run() {
        while (running) {
            Factory.doWork(new FactoryWork() {
            public void doWork() {
                try {
                    // Save the stack traces for the current active transactions.
                    dumpStackTraces();

                    // Make the profile wait before take a new snapshot.
                    Thread.sleep(idleTimeInMillis);
                } catch (Throwable e) {
                    log.error("Error dumping stack traces.", e);
                }
            }}, false /* Do not profile the profiler itself */);
        }
    }

    protected synchronized void dumpStackTraces() {
        long beginTime = System.currentTimeMillis();
        for (ThreadProfile activeThread : activeThreads) {
            activeThread.dumpStackTrace();
        }
        // Be aware of the time spent to do so.
        long timeSpent = System.currentTimeMillis() - beginTime;
        if (timeSpent > 100) {
            log.warn(activeThreads.size() + " stack traces saved in more than 100ms: " + Chronometer.formatElapsedTime(timeSpent));
        }
    }

    public synchronized void removeAllThreads() {
        completedThreads.clear();
    }

    public synchronized void removeThread(ThreadProfile tp) {
        completedThreads.remove(tp);
    }

    public long getCompletedThreadsMinTimeMillis() {
        return completedThreadsMinTimeMillis;
    }

    public void setCompletedThreadsMinTimeMillis(long completedThreadsMinTimeMillis) {
        this.completedThreadsMinTimeMillis = completedThreadsMinTimeMillis;
    }

    public ThreadProfile createThreadProfile() {
        ThreadProfile threadProfile = new ThreadProfile();
        threadProfile.setMaxThreadDurationInMillis(maxThreadProfilingTimeMillis);
        threadProfile.setMaxStackTraceLength(maxThreadStackTraceLength);
        return threadProfile;
    }

    public synchronized ThreadProfile beginThreadProfile() {
        ThreadProfile existingT = getCurrentThreadProfile();
        if (existingT != null) {
            StringBuffer buf = new StringBuffer();
            buf.append("CURRENT THREAD EXECUTION IN PROGRESS:\n").append(existingT.printContext());
            buf.append("CREATOR CALL: ").append(existingT.printStackTrace(existingT.getThread(), 30));
            buf.append("NEW THREAD REQUESTER: ").append(existingT.printStackTrace(Thread.currentThread(), 30));
            log.warn("\n! TRYING TO BEGIN A NEW THREAD OVER A CURRENT THREAD IN PROGRESS !\n" + buf.toString());
        }
        // Create and activate a brand new thread execution.
        ThreadProfile newT = createThreadProfile();
        activeThreads.add(newT);
        currentThreadProfile.set(newT);
        newT.begin();
        log.debug("Thread execution begin. Id=" + newT.getId() + " (active=" + activeThreads.size() + ")");
        return newT;
    }

    public synchronized void finishThreadProfile(ThreadProfile t) {
        // Deactivate the thread
        t.end();
        currentThreadProfile.set(null);
        activeThreads.remove(t);
        log.debug("Thread end. Id=" + t.getId()
                + ", Duration=" + Chronometer.formatElapsedTime(t.getElapsedTime())
                + ", Active=" + activeThreads.size());

        // Register those completed threads that satisfy the inclusion criteria.
        boolean slow = (t.getElapsedTime() > completedThreadsMinTimeMillis);
        boolean failed = (completedThreadsErrorsEnabled && ThreadProfile.STATE_ERROR.equals(t.getState()));
        boolean filterOn = completedThreadsFilter.getPropertyIds().length > 0;
        boolean filtered =  filterOn && completedThreadsFilter.pass(t);
        boolean target = (!filterOn || filtered) && (slow || failed);
        
        if (target || t.isTargetThread()) {
            completedThreads.add(t);
            if (completedThreads.size() > completedThreadsMaxSize) {
                completedThreads.remove(0);
            }
        }
    }

    public ThreadProfile getCurrentThreadProfile() {
        return (ThreadProfile) currentThreadProfile.get();
    }

    public List<ThreadProfile> getActiveThreads() {
        return new ArrayList(activeThreads);
    }

    public List<ThreadProfile> getCompletedThreads() {
        return new ArrayList(completedThreads);
    }

    public List<ThreadProfile> getAllThreads() {
        List<ThreadProfile> result = new ArrayList(activeThreads);
        result.addAll(completedThreads);
        return result;
    }

    public List<ThreadProfile> getFilteredThreads() {
        List<ThreadProfile> result = new ArrayList<ThreadProfile>();
        for (ThreadProfile thread : activeThreads) {
            if (completedThreadsFilter.pass(thread)) result.add(thread);
        }
        for (ThreadProfile thread : completedThreads) {
            if (completedThreadsFilter.pass(thread)) result.add(thread);
        }
        return result;
    }

    public ThreadProfile getThreadProfile(int hash) {
        for (ThreadProfile tp : getActiveThreads()) {
            if (tp.hashCode() == hash) return tp;
        }
        for (ThreadProfile tp : getCompletedThreads()) {
            if (tp.hashCode() == hash) return tp;
        }
        return null;
    }

    public String printThreadsSummaryReport() {
        List<ThreadProfile> activeTxs = getActiveThreads();
        List<ThreadProfile> completedTxs = getCompletedThreads();
        Collections.sort(activeTxs, ThreadProfileComparator.comparatorByBeginDate(false));
        Collections.sort(completedTxs, ThreadProfileComparator.comparatorByBeginDate(false));
        StringBuffer buf = new StringBuffer();
        if (!activeTxs.isEmpty()) buf.append(activeTxs.size()-1).append(" threads running.\n");
        if (!completedTxs.isEmpty()) buf.append(completedTxs.size()).append(" threads completed in more than " + Chronometer.formatElapsedTime(completedThreadsMinTimeMillis) + " each.\n");
        buf.append("\n");
        for (ThreadProfile t : activeTxs) {
            if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;
            buf.append("RUNNING   ").append(t.getId()).append(", Elapsed time=");
            buf.append(Chronometer.formatElapsedTime(t.getElapsedTime()));
            buf.append(", Begin=").append(t.getBeginDate()).append("\n");
        }
        for (ThreadProfile t : completedTxs) {
            buf.append("COMPLETED ").append(t.getId()).append(", Elapsed time=");
            buf.append(Chronometer.formatElapsedTime(t.getElapsedTime()));
            buf.append(", Begin=").append(t.getBeginDate()).append(", End=").append(t.getEndDate()).append("\n");
        }
        return buf.toString();
    }

    public String printActiveThreadsReport() {
        List txs = getActiveThreads();
        Collections.sort(txs, ThreadProfileComparator.comparatorByBeginDate(false));
        StringBuffer buf = new StringBuffer();
        buf.append("\n\n------------------ ACTIVE THREADS=").append(txs.size()-1).append(" -----------------------------\n");
        Iterator it = txs.iterator();
        while (it.hasNext()) {
            ThreadProfile t = (ThreadProfile) it.next();
            if (t.getThread() != null && t.getThread() == Thread.currentThread()) continue;

            buf.append("\n").append(t.printContext()).append("\n");
            buf.append("--------------------------------------------");

        }
        return buf.toString();
    }

    public String printCompletedThreadsReport(long ignoreTracesMillis, boolean showContext) {
        List txs = getCompletedThreads();
        Collections.sort(txs, ThreadProfileComparator.comparatorByBeginDate(false));
        StringBuffer buf = new StringBuffer();
        buf.append("\n\n------------------ COMPLETED THREADS=").append(txs.size()).append(" -----------------------------\n");
        if (completedThreadsMinTimeMillis > 0) buf.append("Maximum run time expected=" + Chronometer.formatElapsedTime(completedThreadsMinTimeMillis) + ".\n");
        if (ignoreTracesMillis > 0) buf.append("Ignoring traces lower than " + Chronometer.formatElapsedTime(ignoreTracesMillis) + ".\n");
        Iterator it = txs.iterator();
        while (it.hasNext()) {
            ThreadProfile t = (ThreadProfile) it.next();
            CodeBlockTraces rootCodeTraces = new CodeBlockTraces();
            rootCodeTraces.add(t.getRootCodeBlock());
            String threadTree = rootCodeTraces.printTree(ignoreTracesMillis, showContext, "\n", 0);
            if (!StringUtils.isEmpty(threadTree)) {
                buf.append("\n").append(threadTree);
                buf.append("--------------------------------------------");
            }
        }
        return buf.toString();
    }

    protected String getSubjectPrefix() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return "[" + localhost.getHostName() + "] ";
        } catch (UnknownHostException e) {
            return "";
        }

    }
    protected StringBuffer appendErrorReport(StringBuffer buffer, ErrorReport error) {
        CodeBlockTrace codeBlock = error.getCodeBlock();
        buffer.append("<h4>ERROR REPORT</h4>");
        buffer.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"2\">");
        buffer.append("<tr><td valign=\"top\" align=\"left\">");
        buffer.append(codeBlock.printContext(true, "</td><td valign=\"top\" align=\"left\">= ", "</td></tr><tr><td align=\"left\">", 0));
        buffer.append("</td></tr>");
        buffer.append("</table>");
        return buffer;
    }

    protected StringBuffer appendThreadContext(StringBuffer buffer, ThreadProfile tp) {
        buffer.append("<h4>THREAD&#39;S DETAILS</h4>");
        buffer.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"2\">");
        for (String propName : tp.getContextPropertyNames()) {
            if (tp.getContextProperty(propName) == null) continue;
            String propValue = tp.getContextProperty(propName).toString();
            buffer.append("<tr><td valign=\"top\" align=\"left\">").append(propName).append("</td>");
            buffer.append("<td valign=\"top\" align=\"left\">= ").append(propValue).append("</td></tr>");
        }
        buffer.append("</table>");
        return buffer;
    }

    protected StringBuffer appendServerSettings(StringBuffer buffer) {
        buffer.append("<h4>SERVER SETTINGS</h4>");
        buffer.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"2\">");
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            buffer.append("<tr><td align=\"left\">Host name</td>");
            buffer.append("<td align=\"left\">= ").append(localhost.getHostName()).append("</td></tr>");
            buffer.append("<tr><td align=\"left\">Host IP </td>");
            buffer.append("<td align=\"left\">= ").append(localhost.getHostAddress()).append("</td></tr>");
        } catch (UnknownHostException e) {
            buffer.append("<tr><td align=\"left\">Host name</td>");
            buffer.append("<td align=\"left\">= Unknown</td></tr>");
        }
        buffer.append("</table>");
        return buffer;
    }

    protected StringBuffer appendCopyright(StringBuffer buffer) {
        buffer.append(Application.lookup().getCopyright()).append("<br/>");
        return buffer;
    }
}
