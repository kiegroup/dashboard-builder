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
package org.jboss.dashboard.ui.controller.responses;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

/**
 * Null response generator. It does nothing. Not very interesting, but sometimes useful.
 */
public class DoNothingResponse implements CommandResponse {

    public DoNothingResponse() {
        super();
    }

    public boolean execute(CommandRequest cmdReq) throws java.lang.Exception {
        return true;
    }

    public String toString() {
        return "DoNothingResponse (response should have been already committed)";
    }
}