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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.profiler.memory.MemoryProfiler;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyzes the memory available on every incoming request and it tries to free memory if required.
 */
public class FreeMemoryProcessor extends RequestChainProcessor {

    private static transient Logger log = LoggerFactory.getLogger(FreeMemoryProcessor.class.getName());

    protected boolean processRequest() throws Exception {
        MemoryProfiler memoryProfiler = MemoryProfiler.lookup();
        if (memoryProfiler.isLowMemory()) {
            log.warn("Memory is running low ...");
            memoryProfiler.freeMemory();
            if (memoryProfiler.isLowMemory()) {
                getControllerStatus().setResponse(new SendErrorResponse(503));
                getControllerStatus().consumeURIPart(getControllerStatus().getURIToBeConsumed());
                log.error("Memory is so low that the request had to be canceled - 503 sent. Consider increasing the JVM memory.");
                return false;
            }
        }
        return true;
    }
}
