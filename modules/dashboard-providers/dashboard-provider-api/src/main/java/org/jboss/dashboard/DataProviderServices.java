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
import org.jboss.dashboard.command.CommandProcessorFactory;
import org.jboss.dashboard.command.TemplateProcessor;
import org.jboss.dashboard.dataset.DataSetManager;
import org.jboss.dashboard.dataset.DataSetSettings;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.profiler.ThreadProfile;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
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
    protected DataSetSettings dataSetSettings;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected TemplateProcessor templateProcessor;

    @Inject
    protected CommandProcessorFactory commandProcessorFactory;

    @Inject
    protected Instance<CommandFactory> commandFactories;

    public DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataSetManager getDataSetManager() {
        ThreadProfile tp = Profiler.lookup().getCurrentThreadProfile();
        if (tp != null && tp.containsCodeBlockType(CoreCodeBlockTypes.CONTROLLER_REQUEST)) {
            return (DataSetManager) CDIBeanLocator.getBeanByName("sessionScopedDataSetManager");
        }
        return (DataSetManager) CDIBeanLocator.getBeanByName("appScopedDataSetManager");
    }

    public ScalarFunctionManager getScalarFunctionManager() {
        return scalarFunctionManager;
    }

    public TemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public Instance<CommandFactory> getCommandFactories() {
        return commandFactories;
    }

    public CommandProcessorFactory getCommandProcessorFactory() {
        return commandProcessorFactory;
    }

    public DataSetSettings getDataSetSettings() {
        return dataSetSettings;
    }
}

