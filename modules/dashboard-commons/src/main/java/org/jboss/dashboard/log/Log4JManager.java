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
package org.jboss.dashboard.log;

import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.factory.ComponentsContextManager;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.profiler.ThreadProfile;
import org.apache.log4j.*;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class Log4JManager implements Startable {

    protected List<LoggerSet> loggerSets;

    @Inject @Config("MEMORY")
    protected String memoryAppenderName;

    @Inject @Config("[%t %d{MM/dd/yyyy HH:mm:ss}] %-5p %C{6} (%F:%L) - %m [%X{ADDS}]%n")
    protected String memoryAppenderPattern;

    @Inject @Config("10000")
    protected int memoryAppenderBufferSize;

    @Inject @Config("500")
    protected long highThroughput;

    @Inject @Config("false")
    protected boolean highTroughputAllowed;

    @Inject @Config("5000")
    protected long highTroughputMaxTime;

    protected MemoryAppender memoryAppender;
    protected transient long _numberOfEvents;
    protected transient long _startTime;
    protected transient long _highThroughputCount;
    protected transient long _throughput;

    public Log4JManager() {
        _numberOfEvents = 0;
        _startTime = System.currentTimeMillis();
        _highThroughputCount = 0;
    }

    public String getMemoryAppenderName() {
        return memoryAppenderName;
    }

    public void setMemoryAppenderName(String memoryAppenderName) {
        this.memoryAppenderName = memoryAppenderName;
    }

    public String getMemoryAppenderPattern() {
        return memoryAppenderPattern;
    }

    public void setMemoryAppenderPattern(String memoryAppenderPattern) {
        this.memoryAppenderPattern = memoryAppenderPattern;
    }

    public int getMemoryAppenderBufferSize() {
        return memoryAppenderBufferSize;
    }

    public void setMemoryAppenderBufferSize(int memoryAppenderBufferSize) {
        this.memoryAppenderBufferSize = memoryAppenderBufferSize;
    }

    public MemoryAppender getMemoryAppender() {
        return memoryAppender;
    }

    public List<LoggerSet> getLoggerSets() {
        return Collections.unmodifiableList(loggerSets);
    }

    public void addLoggerSet(LoggerSet loggerSet) {
        loggerSets.add(loggerSet);
    }

    public void removeLoggerSet(LoggerSet loggerSet) {
        if (loggerSet.isEditable()) {
            loggerSets.remove(loggerSet);
            loggerSet.setLevel(null);
        }
    }

    public LoggerSet getLoggerSetById(Long id) {
        for (LoggerSet loggerSet : loggerSets) {
            if (loggerSet.id.equals(id)) return loggerSet;
        }
        return null;
    }

    public void setLoggerSetsLevel(org.apache.log4j.Level level) {
        for (LoggerSet loggerSet : loggerSets) {
            loggerSet.setLevel(level);
        }
    }

    public long getHighThroughput() {
        return highThroughput;
    }

    public void setHighThroughput(long highThroughput) {
        this.highThroughput = highThroughput;
    }

    public boolean isHighTroughputAllowed() {
        return highTroughputAllowed;
    }

    public void setHighTroughputAllowed(boolean highTroughputAllowed) {
        this.highTroughputAllowed = highTroughputAllowed;
    }

    public long getHighTroughputMaxTime() {
        return highTroughputMaxTime;
    }

    public void setHighTroughputMaxTime(long highTroughputMaxTime) {
        this.highTroughputMaxTime = highTroughputMaxTime;
    }

    public long getThroughput() {
        return _throughput;
    }

    protected void calculateThroughput() {
        long now = System.currentTimeMillis();
        if ((now - _startTime) < 1000) {
            // Count the events generated within a second.
            _numberOfEvents++;
        } else {
            // Check the throughput every second.
            _throughput = _numberOfEvents;
            if (_throughput > highThroughput) _highThroughputCount++;
            else _highThroughputCount = 0;
            _startTime = now;
            _numberOfEvents = 0;
        }
    }

    // Called by MemoryAppender

    void onAppend(LoggingEvent event) {
        // Attach the event to the current thread.
        if (ComponentsContextManager.isContextStarted()) {
            ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
            if (threadProfile != null) threadProfile.addLog4JEvent(event);
        }
        // Calculate the current throughput.
        calculateThroughput();

        // Avoid high throughput.
        if (!highTroughputAllowed && _highThroughputCount > 0 &&  _highThroughputCount > (highTroughputMaxTime / 1000)) {
            setLoggerSetsLevel(org.apache.log4j.Level.FATAL);
            String warnMessage = "\nWARNING: Log4J High throughput detected (more than " + getHighThroughput() + " events per second). All log levels set to FATAL.\n";
            LoggingEvent warnEvent = new LoggingEvent(Category.class.getName(), Logger.getRootLogger(), org.apache.log4j.Level.WARN, warnMessage, null);
            memoryAppender.buffer.addLast(warnEvent);
            System.out.println(warnMessage);
            _highThroughputCount = 0;
        }
    }

    public Priority getPriority() {
        return Priority.URGENT;
    }

    public void start() throws Exception {
        createMemoryAppender();
        initCoreLoggerSets();
        initLoggerSets();
    }

    protected void createMemoryAppender(){
        memoryAppender = new MemoryAppender(this);
        memoryAppender.setName(memoryAppenderName);
        memoryAppender.setLayout(new PatternLayout(memoryAppenderPattern));
        memoryAppender.setBufferSize(memoryAppenderBufferSize);

        // Attach the memory appender to the configured loggers.
        Logger.getRootLogger().addAppender(memoryAppender);
        Enumeration enumLoggers = LogManager.getCurrentLoggers();
        while (enumLoggers.hasMoreElements()) {
            Logger logger = (Logger) enumLoggers.nextElement();
            if (logger.getLevel() != null) logger.addAppender(memoryAppender);
        }
    }

    protected void initCoreLoggerSets() {
        loggerSets = new ArrayList<LoggerSet>();
        loggerSets.add(LoggerSet.ROOT);
        Enumeration enumLoggers = LogManager.getCurrentLoggers();
        while (enumLoggers.hasMoreElements()) {
            Logger logger = (Logger) enumLoggers.nextElement();
            if (logger.getLevel() != null) loggerSets.add(new LoggerSet(logger.getName(), logger, false));
        }
    }

    protected void initLoggerSets() {
        loggerSets.add(new LoggerSet("Transactions", org.apache.log4j.Level.FATAL, "org.jboss.dashboard.database.hibernate.HibernateTransaction", false));
        loggerSets.add(new LoggerSet("SQL sentences", org.apache.log4j.Level.FATAL, "org.jboss.dashboard.database.NonPooledDataSource", false));
        loggerSets.add(new LoggerSet("Hibernate SQL", org.apache.log4j.Level.FATAL, "org.hibernate.engine.jdbc.internal\norg.hibernate.SQL\norg.hibernate.pretty", false));
        loggerSets.add(new LoggerSet("HTTP requests", org.apache.log4j.Level.FATAL, "org.jboss.dashboard.ui.controller.ControllerServlet\norg.jboss.dashboard.ui.controller.responses", false));
    }
}
