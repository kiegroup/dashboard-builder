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
package org.jboss.dashboard.ui.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.controller.requestChain.JspUrlProcessor;
import org.jboss.dashboard.ui.controller.requestChain.KPIProcessor;
import org.jboss.dashboard.ui.controller.requestChain.RequestDispatcher;
import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenProcessor;
import org.jboss.dashboard.ui.controller.requestChain.DashboardProcessor;
import org.jboss.dashboard.ui.controller.requestChain.FreeMemoryProcessor;
import org.jboss.dashboard.ui.controller.requestChain.FriendlyUrlProcessor;
import org.jboss.dashboard.ui.controller.requestChain.HttpSSOProcessor;
import org.jboss.dashboard.ui.controller.requestChain.ModalDialogStatusSaver;
import org.jboss.dashboard.ui.controller.requestChain.MultipartProcessor;
import org.jboss.dashboard.ui.controller.requestChain.NavigationCookieProcessor;
import org.jboss.dashboard.ui.controller.requestChain.RequestChainProcessor;
import org.jboss.dashboard.ui.controller.requestChain.ResponseHeadersProcessor;
import org.jboss.dashboard.ui.controller.requestChain.SessionInitializer;

@ApplicationScoped
public class RequestProcessor {

    private static CDIBeanLocator beanLocator = CDIBeanLocator.get();
    
    public static RequestProcessor lookup() {
        return beanLocator.lookupBeanByType(RequestProcessor.class);
    }

    protected List<RequestChainProcessor> processorChain = new ArrayList<RequestChainProcessor>();

    @PostConstruct
    protected void initChain() {
        processorChain.add(beanLocator.lookupBeanByType(HttpSSOProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(ModalDialogStatusSaver.class));
        processorChain.add(beanLocator.lookupBeanByType(ResponseHeadersProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(MultipartProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(CSRFTokenProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(DashboardProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(FreeMemoryProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(SessionInitializer.class));
        processorChain.add(beanLocator.lookupBeanByType(FriendlyUrlProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(JspUrlProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(NavigationCookieProcessor.class));
        processorChain.add(beanLocator.lookupBeanByType(RequestDispatcher.class));
        processorChain.add(beanLocator.lookupBeanByType(KPIProcessor.class));
    }

    public void run() throws Exception {
        for (RequestChainProcessor processor : processorChain) {
            if (processor.processRequest() == false) {
                // Stop in case the processor has explicitly stopped the chain's processing.
                return;
            }
        }
    }
}
