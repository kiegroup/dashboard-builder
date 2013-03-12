package org.jboss.dashboard;

import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.filesystem.FileSystemManager;
import org.jboss.dashboard.database.hibernate.HibernateSessionFactoryProvider;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.log.Log4JManager;
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
    protected Log4JManager log4JManager;

    @Inject
    protected Profiler profiler;

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected ErrorManager errorManager;

    @Inject
    protected FileSystemManager fileSystemManager;

    @Inject
    protected DataSourceManager dataSourceManager;

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

    public Log4JManager getLog4JManager() {
        return log4JManager;
    }

    public void setLog4JManager(Log4JManager log4JManager) {
        this.log4JManager = log4JManager;
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

    public FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    public void setFileSystemManager(FileSystemManager fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
}
