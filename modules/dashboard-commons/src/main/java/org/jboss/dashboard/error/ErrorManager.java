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
package org.jboss.dashboard.error;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.profiler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.CoreServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Manages the error handling in the platform.
 */
@ApplicationScoped
public class ErrorManager {

    /** Logger */
    private static transient Logger log = LoggerFactory.getLogger(ErrorManager.class.getName());

    /**
     * Get an ExceptionManager instance.
     */
    public static ErrorManager lookup() {
        return CoreServices.lookup().getErrorManager();
    }

    @Inject @Config("true")
    protected boolean logErrorReportEnabled;

    @Inject @Config("true")
    protected boolean logDBInterlockThreadsEnabled;

    public boolean isLogErrorReportEnabled() {
        return logErrorReportEnabled;
    }

    public void setLogErrorReportEnabled(boolean logErrorReportEnabled) {
        this.logErrorReportEnabled = logErrorReportEnabled;
    }

    public boolean isLogDBInterlockThreadsEnabled() {
        return logDBInterlockThreadsEnabled;
    }

    public void setLogDBInterlockThreadsEnabled(boolean logDBInterlockThreadsEnabled) {
        this.logDBInterlockThreadsEnabled = logDBInterlockThreadsEnabled;
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the give message string is displayed
     * in a error report modal window.<br>
     * <br>
     * <p>NOTE: This functionality is not implemented yet.
     * Nevertheless, the future error handling subsystem will notify such error to the error handling component.
     * Actually, as a temporal solution, the error manager is overridden by the bpe module in order to notify to the
     * StepExecutionErrorHandler component, so the message specified is displayed as part of the error in the BPM task list .
     *
     * @param message The custom message to display in the error window.
     * @param cause The cause of the error, also displayed in the error window.
     */
    public void throwTechnicalError(String message, Throwable cause) {
        throw new TechnicalError(message, cause);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as an error in a modal window.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessError(String title, String message) {
        throw new BusinessError(BusinessError.ERROR, title, message);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as a warning in a modal window.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessWarning(String title, String message) {
        throw new BusinessError(BusinessError.WARN, title, message);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as extra information in a modal window.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessInfo(String title, String message) {
        throw new BusinessError(BusinessError.INFO, title, message);
    }

    /**
     * Get the error cause (if any) thrown by the application logic.
     */
    public ApplicationError getApplicationErrorCause(Throwable e) {
        LinkedList<ApplicationError> appErrors = new LinkedList<ApplicationError>();
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ApplicationError) appErrors.add((ApplicationError)cause);
            cause = cause.getCause();
        }
        if (appErrors.isEmpty()) return null;
        return appErrors.getLast();
    }

    /**
     * Get the root exception.
     */
    public Throwable getRootCause(Throwable t) {
        if (t == null) return null;
        Throwable root = t.getCause();
        if (root == null) {
            if (t instanceof ServletException) root = ((ServletException) t).getRootCause();
            if (t instanceof JspException) root = ((JspException) t).getRootCause();
            if (t instanceof InvocationTargetException) root = ((InvocationTargetException) t).getTargetException();
        }
        if (root == null) return t;
        else return getRootCause(root);
    }

    /**
     * Check if the specified exception is a DB interlock error.
     */
    public boolean isDBInterlockException(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) root = root.getCause();

        if (root.getClass().getName().endsWith("SQLServerException")) {
            if (root.getMessage().indexOf("en bloqueo recursos con otro proceso y fue elegida como sujeto del interbloqueo") != -1) {
                return true;
            }
        }
        if (root.getClass().getName().endsWith("PSQLException")) {
            if (root.getMessage().indexOf("ERROR: no se pudo serializar el acceso debido a un update concurrente") != -1) {
                return true;
            }
        }
        if (root.getClass().getName().endsWith("SQLException")) {
            if (root.getMessage().indexOf("ORA-00060: detectado interbloqueo mientras se esperaba un recurso") != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate an error report and log the error if requested.
     */
    public ErrorReport notifyError(Throwable t, boolean doLog) {
        // Only save the very first error notification within the current thread.
        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        if (threadProfile != null && threadProfile.getErrorReport() != null) {
            return threadProfile.getErrorReport();
        }

        // Build the report.
        ErrorReport report = new ErrorReport();
        report.setId(String.valueOf(System.currentTimeMillis()));
        report.setException(t);

        // Generate a profiling trace.
        CodeBlockTrace trace = new ErrorTrace(report).begin();
        try {
            // Store the report into the current thread.
            if (threadProfile != null) {
                threadProfile.setErrorReport(report);
            }

            // Logger the error.
            if (doLog) logError(report);
        } finally {
            trace.end();
        }
        return report;
    }

    /**
     * Log the specified error report.
     */
    public void logError(ErrorReport report) {
        // Log only the non-application errors.
        ApplicationError appError = getApplicationErrorCause(report.getException());
        if (appError == null) {
            // Print the report in the log.
            if (logErrorReportEnabled) {
                log.error("UNEXPECTED ERROR.\n" + report.printContext(0));
            }
            // Catch database interlock exceptions & print a detailed report in order to find out the cause.
            if (logDBInterlockThreadsEnabled && isDBInterlockException(getRootCause(report.getException()))) {
                log.error(Profiler.lookup().printActiveThreadsReport());
            }
        }
    }
}
