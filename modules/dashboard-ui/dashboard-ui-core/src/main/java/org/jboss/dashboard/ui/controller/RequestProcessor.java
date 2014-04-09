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

    public static RequestProcessor lookup() {
        return CDIBeanLocator.getBeanByType(RequestProcessor.class);
    }

    protected List<RequestChainProcessor> processorChain = new ArrayList<RequestChainProcessor>();

    @PostConstruct
    protected void initChain() {
        processorChain.add(CDIBeanLocator.getBeanByType(CSRFTokenProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(HttpSSOProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(ModalDialogStatusSaver.class));
        processorChain.add(CDIBeanLocator.getBeanByType(ResponseHeadersProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(MultipartProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(DashboardProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(FreeMemoryProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(SessionInitializer.class));
        processorChain.add(CDIBeanLocator.getBeanByType(FriendlyUrlProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(JspUrlProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(NavigationCookieProcessor.class));
        processorChain.add(CDIBeanLocator.getBeanByType(RequestDispatcher.class));
        processorChain.add(CDIBeanLocator.getBeanByType(KPIProcessor.class));
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
