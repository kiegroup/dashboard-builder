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
import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.factory.ComponentsContextManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.FactoryWork;
import org.jboss.dashboard.commons.text.StringUtil;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.components.ControllerStatus;
import org.jboss.dashboard.ui.components.ErrorReportHandler;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.controller.requestChain.RequestChainProcessor;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.factory.PanelSessionComponentsStorage;
import org.jboss.dashboard.factory.SessionComponentsStorage;
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

    public final static String FACTORY_CONFIG_DIR = "factory";
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
        initAppDirectories();
        initFactory();
        Factory.doWork(new FactoryWork() {
        public void doWork() {
            try {
                Application.lookup().start();
                initSuccess = true;
            } catch (Throwable e) {
                log.error("Error initializing application. Marking it as uninitialized ", e);
                initException = e;
                initSuccess = false;
                initError();
            }
        }});
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
            Application.lookup().shutdown();
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
            baseAppDir = new File(getServletContext().getRealPath(".")).getPath();
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
     * Init the advanced configuration subsystem based in the Factory trees.
     */
    protected void initFactory() {
        if (Application.lookup().getGlobalFactory() == null) {
            String factoryCfgDir = Application.lookup().getBaseCfgDirectory() + "/" + FACTORY_CONFIG_DIR;
            Factory factory = Factory.getFactory(new File(factoryCfgDir));
            if (factory != null) Application.lookup().setGlobalFactory(factory);
        }
        ComponentsContextManager.addComponentStorage(Component.SCOPE_REQUEST, new RequestComponentsStorage());
        ComponentsContextManager.addComponentStorage(Component.SCOPE_PANEL_SESSION, new PanelSessionComponentsStorage());
        ComponentsContextManager.addComponentStorage(Component.SCOPE_SESSION, new SessionComponentsStorage());
    }

    /**
     * Process incoming HTTP requests
     *
     * @param request  Object that encapsulates the request to the servlet.
     * @param response Object that encapsulates the response from the servlet.
     */
    public final void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (initSuccess) {
            Factory.doWork(new FactoryWork() {
                public void doWork() {
                    try {
                        HTTPSettings webSettings = HTTPSettings.lookup();
                        request.setCharacterEncoding(webSettings.getEncoding());
                    } catch (UnsupportedEncodingException e) {
                        log.error("Error: ", e);
                    }

                    // Init the request context.
                    ControllerServletHelper helper = ControllerServletHelper.lookup();
                    CommandRequest cmdRq = helper.initThreadLocal(request, response);
                    ControllerStatus.lookup().setRequest(cmdRq);

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

                        // Clear the request context.
                        helper.clearThreadLocal(request, response);
                    }
                }
            });
        } else {
            log.error("Received request, but application servlet hasn't been properly initialized. Ignoring.");
            response.sendError(500, "Application incorrectly initialized.");
        }
    }

    protected void processTheRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                // Process the request.
                if (log.isDebugEnabled()) log.debug("Processing request\n" + Profiler.printCurrentContext());
                RequestChainProcessor requestProcessor = (RequestChainProcessor) Factory.lookup("org.jboss.dashboard.ui.controller.requestChain.StartingProcessor");
                requestProcessor.doRequestProcessing();

                // Ensure GETs URIs are fully processed.
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    ControllerStatus.lookup().compareConsumedUri();
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
                RequestChainProcessor renderingProcessor = (RequestChainProcessor) Factory.lookup("org.jboss.dashboard.ui.controller.requestChain.StartingRenderer");
                renderingProcessor.doRequestProcessing();
            }}.execute();
        } catch (Throwable e) {
            log.error("Error painting response. User might have seen something ugly in the browser if he is still there.");
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
        ControllerStatus controllerStatus = ControllerStatus.lookup();
        controllerStatus.setResponse(new ShowCurrentScreenResponse());
    }

    /**
     * Called when it's destroyed.
     */
    public void destroy() {
        Application.lookup().shutdown();
        log.debug("Destroying controller servlet");
    }
}
