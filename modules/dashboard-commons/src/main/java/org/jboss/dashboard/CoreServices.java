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
package org.jboss.dashboard;

import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.database.hibernate.HibernateSessionFactoryProvider;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.export.DataSourceImportManager;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.scheduler.Scheduler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named("coreServices")
public class CoreServices {

    public static CoreServices lookup() {
        return (CoreServices) CDIBeanLocator.getBeanByName("coreServices");
    }

    @Inject
    protected HibernateInitializer hibernateInitializer;

    @Inject
    protected HibernateSessionFactoryProvider hibernateSessionFactoryProvider;

    @Inject
    protected Profiler profiler;

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected ErrorManager errorManager;

    @Inject
    protected DataSourceManager dataSourceManager;

    @Inject
    protected DataSourceImportManager dataSourceImportManager;

    public HibernateSessionFactoryProvider getHibernateSessionFactoryProvider() {
        return hibernateSessionFactoryProvider;
    }

    public void setHibernateSessionFactoryProvider(HibernateSessionFactoryProvider hibernateSessionFactoryProvider) {
        this.hibernateSessionFactoryProvider = hibernateSessionFactoryProvider;
    }

    public HibernateInitializer getHibernateInitializer() {
        return hibernateInitializer;
    }

    public void setHibernateInitializer(HibernateInitializer hibernateInitializer) {
        this.hibernateInitializer = hibernateInitializer;
    }

    public Profiler getProfiler() {
        return profiler;
    }

    public void setProfiler(Profiler profiler) {
        this.profiler = profiler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public DataSourceImportManager getDataSourceImportManager() {
        return dataSourceImportManager;
    }

    public void setDataSourceImportManager(DataSourceImportManager dataSourceImportManager) {
        this.dataSourceImportManager = dataSourceImportManager;
    }
}
