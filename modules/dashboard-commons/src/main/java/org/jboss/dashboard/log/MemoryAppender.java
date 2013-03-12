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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.*;
import java.util.LinkedList;

/**
 * Log4J appender that store the incoming events in a shared memory buffer.
 */
public class MemoryAppender extends AppenderSkeleton {

    protected int bufferSize;
    protected transient LinkedList<LoggingEvent> buffer;
    protected transient Log4JManager log4JManager;
    protected transient boolean _onAppend;

    public MemoryAppender(Log4JManager log4JManager) {
        super();
        this.log4JManager = log4JManager;
        super.name = "MEMORY";
        buffer = new LinkedList<LoggingEvent>();
        bufferSize = 10000;
        _onAppend = false;
    }

    public LinkedList<LoggingEvent> getBuffer() {
        return buffer;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean requiresLayout(){
        return true;
    }

    public void activateOptions() {
    }

    public void close() {
    }

    public synchronized void clear() {
        buffer.clear();
    }

    public synchronized void append(LoggingEvent event) {
        // Avoid uncontrolled recursivity caused by logs calls done within the Log4JManager.onAppend() method.
        if (_onAppend) return;

        // Store the event into the memory buffer.
        buffer.addLast(event);
        if (buffer.size() > bufferSize) {
            buffer.removeFirst();
        }
        // Notify the manager.
        try {
            _onAppend = true;
            log4JManager.onAppend(event);
        } finally {
            _onAppend = false;
        }
    }
}