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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.dashboard.profiler.memory.MemoryProfiler;
import org.jboss.dashboard.ui.components.ControllerStatus;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyzes the memory available on every incoming request and it tries to free memory if required.
 */
@ApplicationScoped
public class FreeMemoryProcessor implements RequestChainProcessor {

    @Inject
    private transient Logger log;

    public boolean processRequest(CommandRequest request) throws Exception {
        MemoryProfiler memoryProfiler = MemoryProfiler.lookup();
        if (memoryProfiler.isLowMemory()) {
            log.warn("Memory is running low ...");
            memoryProfiler.freeMemory();
            if (memoryProfiler.isLowMemory()) {
                ControllerStatus controllerStatus = ControllerStatus.lookup();
                controllerStatus.setResponse(new SendErrorResponse(503));
                controllerStatus.consumeURIPart(controllerStatus.getURIToBeConsumed());
                log.error("Memory is so low that the request had to be canceled - 503 sent. Consider increasing the JVM memory.");
                return false;
            }
        }
        return true;
    }
}
