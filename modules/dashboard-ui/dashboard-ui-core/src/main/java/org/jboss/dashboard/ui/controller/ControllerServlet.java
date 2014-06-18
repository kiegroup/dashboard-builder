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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.commons.text.StringUtil;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.ResponseProcessor;
import org.jboss.dashboard.ui.components.ErrorReportHandler;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.error.ErrorReport;
import org.jboss.dashboard.profiler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Application end point for HTTP requests. It provides the following features:<ul>
 * <li> Perform application initialization and ending.
 * <li> Analyze the requests received and dispatch to the proper execution method.</ul>
 */
public class ControllerServlet extends HttpServlet {

    private static transient Logger log = LoggerFactory.getLogger(ControllerServlet.class.getName());

    public final static String INIT_PARAM_CFG_DIR = "cfg.dir";
    public final static String INIT_PARAM_APP_DIR = "app.dir";

    private static boolean initSuccess = true;
    private static Throwable initException;

    /**
     * Initializes the servlet.
     *
     * @throws javax.servlet.ServletException Description of the Exception
     */
    public void init() throws ServletException {
        try {
            initAppDirectories();
            Application.lookup().start();
            initSuccess = true;
        } catch (Throwable e) {
            log.error("Error initializing application. Marking it as uninitialized ", e);
            initException = e;
            initSuccess = false;
            initError();
        }
    }

    protected void initError() {
        // Write some data to file, allowing external checking of what went wrong.
        File outputFile = new File(Application.lookup().getBaseAppDirectory() + "/ControllerError.txt");
        FileWriter writer = null;
        try {
            StringWriter sw = new StringWriter();
            initException.printStackTrace(new PrintWriter(sw));
            writer = new FileWriter(outputFile);
            writer.write(initException.getMessage() + "\n" + sw.toString());
            outputFile.deleteOnExit();
            sw.close();
        } catch (IOException e1) {
            log.error("Error writing to log file: ", e1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                    log.error("Error closing log file: ", e2);
                }
            }
        }
    }

    protected void initAppDirectories() {
        String baseAppDir = getInitParameter(INIT_PARAM_APP_DIR);
        if (baseAppDir == null) {
            baseAppDir = new File(getServletContext().getRealPath("/")).getPath();
            baseAppDir = StringUtil.replaceAll(baseAppDir, "\\", "/");
            log.info("Application Directory: " + baseAppDir);
        }
        String baseConfigDir = getInitParameter(INIT_PARAM_CFG_DIR);
        if (baseConfigDir == null) {
            baseConfigDir = baseAppDir + "/WEB-INF/etc";
            log.info("Application Config Directory: " + baseConfigDir);
        }
        Application.lookup().setBaseAppDirectory(baseAppDir);
        Application.lookup().setBaseCfgDirectory(baseConfigDir);
        Application.lookup().setLibDirectory(baseAppDir + "/WEB-INF/lib");
    }

    /**
     * Process incoming HTTP requests
     *
     * @param request  Object that encapsulates the request to the servlet.
     * @param response Object that encapsulates the response from the servlet.
     */
    public final void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (initSuccess) {
            try {
                HTTPSettings webSettings = HTTPSettings.lookup();
                request.setCharacterEncoding(webSettings.getEncoding());
            } catch (UnsupportedEncodingException e) {
                log.error("Error: ", e);
            }

            // Init the request.
            beforeRequestBegins(request, response);

            // Begin the profiling trace.
            CodeBlockTrace trace = new RequestTrace().begin(request);
            try {
                // Process the request (control layer)
                processTheRequest(request, response);

                // Render the view (presentation layer)
                processTheView(request, response);
            } finally {
                // End the profiling trace.
                trace.end();

                // Complete the request.
                afterRequestEnds(request, response);
            }
        } else {
            log.error("Received request, but application servlet hasn't been properly initialized. Ignoring.");
            response.sendError(500, "Application incorrectly initialized.");
        }
    }

    public void beforeRequestBegins(HttpServletRequest request, HttpServletResponse response) {
        // Start the profiling of the request.
        Profiler.lookup().beginThreadProfile();

        // Initialize the request.
        RequestContext.init(request, response);
    }

    public void afterRequestEnds(HttpServletRequest request, HttpServletResponse response) {
        // Destroy the current request context.
        RequestContext.destroy();

        // Finish the profiling of the request.
        Profiler.lookup().finishThreadProfile();
    }

    protected void processTheRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                // Process the request.
                if (log.isDebugEnabled()) log.debug("Processing request\n" + ProfilerHelper.printCurrentContext());
                RequestProcessor.lookup().run();

                // Ensure GETs URIs are fully processed.
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    RequestContext.lookup().compareConsumedUri();
                }
            }}.execute();
        } catch (Throwable e) {
            // Display the error.
            displayTheError(e);
        }
    }

    protected void processTheView(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                if (log.isDebugEnabled()) log.debug("Rendering response. Id=" + Thread.currentThread().getName());
                ResponseProcessor.lookup().run();
            }}.execute();
        } catch (Throwable e) {
            log.error("Error painting response.");
        }
    }

    protected void displayTheError(Throwable t) {
        // Get the error has been generated during the thread's execution.
        ErrorReport report = ErrorReport.getCurrentThreadError();
        if (report == null) report = ErrorManager.lookup().notifyError(t, true);

        // Initialize the error handler bean.
        ErrorReportHandler errorHandler = ErrorReportHandler.lookup();
        errorHandler.setWidth(1000);
        errorHandler.setHeight(400);
        errorHandler.setErrorReport(report);
        errorHandler.setCloseListener(new Runnable() {
            public void run() {
                ModalDialogComponent.lookup().hide();
            }
        });

        // Display the error in a modal dialog window.
        ModalDialogComponent modalDialog = ModalDialogComponent.lookup();
        modalDialog.setTitle(report.printErrorTitle());
        modalDialog.setCurrentComponent(errorHandler);
        modalDialog.setCloseListener(new Runnable() {
            public void run() {
                ErrorReportHandler errorHandler = ErrorReportHandler.lookup();
                errorHandler.setErrorReport(null);
            }
        });
        modalDialog.show();

        // Force the current screen to be refreshed so the error report will be displayed.
        RequestContext.lookup().setResponse(new ShowCurrentScreenResponse());
    }
}
