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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import java.util.ArrayList;
import java.util.List;

public class LoggerSet {

    public static Level[] LEVELS_ALL = new Level[] {Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE};
    public static Level[] LEVELS_NODEBUG = new Level[] {Level.FATAL, Level.ERROR, Level.WARN, Level.INFO};

    public static final LoggerSet PROTOTYPE = new LoggerSet("", null, "com.package1\ncom.package2\n...", true);
    public static final LoggerSet ROOT = new LoggerSet("ROOT", org.apache.log4j.Logger.getRootLogger(), false, LEVELS_NODEBUG);

    protected Long id;
    protected String name;
    protected Level level;
    protected boolean editable;
    protected List<org.apache.log4j.Logger> loggers;
    protected Level[] allowedLevels;

    public LoggerSet(String name, Level level, boolean editable) {
        this(name, level, null, editable, LEVELS_ALL);
    }

    public LoggerSet(String name, org.apache.log4j.Logger logger, boolean editable) {
        this(name, logger, editable, LEVELS_ALL);
    }

    public LoggerSet(String name, Level level, String loggers, boolean editable) {
        this(name, level, loggers, editable, LEVELS_ALL);
    }

    LoggerSet(String name, org.apache.log4j.Logger logger, boolean editable, Level[] allowedLevels) {
        this(name, logger.getLevel(), null, editable, allowedLevels);
        loggers.add(logger);
    }

    LoggerSet(String name, Level level, String loggers, boolean editable, Level[] allowedLevels) {
        this.id = (long) (System.currentTimeMillis() * Math.random());
        this.name = name;
        this.level = level;
        this.editable = editable;
        this.loggers = new ArrayList<org.apache.log4j.Logger>();
        this.allowedLevels = allowedLevels;

        parseLoggers(loggers);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        for (org.apache.log4j.Logger logger : loggers) {
            logger.setLevel(level);
        }
    }

    public Level[] getAllowedLevels() {
        return allowedLevels;
    }

    public void setAllowedLevels(Level[] allowedLevels) {
        this.allowedLevels = allowedLevels;
    }

    public void parseLoggers(String loggersStr) {
        loggers.clear();
        if (!StringUtils.isBlank(loggersStr)) {
            String[] loggerNames = StringUtils.split(loggersStr, "\n");
            for (String loggerName : loggerNames) {
                loggerName = loggerName.trim();
                if (!StringUtils.isBlank(loggerName)) {
                    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(loggerName);
                    logger.setLevel(level);
                    loggers.add(logger);
                }
            }
        }
    }
    public String printLoggers(String separator) {
        StringBuffer buf = new StringBuffer();
        for (org.apache.log4j.Logger logger: loggers) {
            if (buf.length() > 0) buf.append(separator);
            buf.append(logger.getName());
        }
        return buf.toString();
    }
}
