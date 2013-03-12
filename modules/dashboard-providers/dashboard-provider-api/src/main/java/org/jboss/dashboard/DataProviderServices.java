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
package org.jboss.dashboard;

import org.jboss.dashboard.command.CommandFactory;
import org.jboss.dashboard.command.CommandProcessor;
import org.jboss.dashboard.command.TemplateProcessor;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named("dataProviderServices")
public class DataProviderServices {

    public static DataProviderServices lookup() {
        return (DataProviderServices) CDIBeanLocator.getBeanByName("dataProviderServices");
    }

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected TemplateProcessor templateProcessor;

    @Inject
    protected Instance<CommandFactory> commandFactories;

    public DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public void setDataProviderManager(DataProviderManager dataProviderManager) {
        this.dataProviderManager = dataProviderManager;
    }

    public ScalarFunctionManager getScalarFunctionManager() {
        return scalarFunctionManager;
    }

    public void setScalarFunctionManager(ScalarFunctionManager scalarFunctionManager) {
        this.scalarFunctionManager = scalarFunctionManager;
    }

    public TemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public Instance<CommandFactory> getCommandFactories() {
        return commandFactories;
    }
}

