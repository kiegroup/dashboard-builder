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

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;

@ApplicationScoped
public class RequestRenderer extends AbstractChainProcessor {

    @Inject
    private NavigationManager navigationManager;

    public boolean processRequest() throws Exception {
        navigationManager.freezeNavigationStatus();
        CommandResponse cmdResponse = getResponse();
        CommandRequest cmdRequest = getRequest();
        return cmdResponse.execute(cmdRequest);
    }
}
