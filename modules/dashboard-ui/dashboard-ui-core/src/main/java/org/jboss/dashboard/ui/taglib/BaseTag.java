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
package org.jboss.dashboard.ui.taglib;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.error.ErrorReport;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.ui.components.ErrorReportHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTag extends BodyTagSupport {

    /** Logger */
    private static transient Logger log = LoggerFactory.getLogger(BaseTag.class.getName());

    /** The JSP to render if an error occurs. */
    protected String errorPage = "/error.jsp";

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public void jspInclude(String page) {
        CodeBlockTrace trace = new JSPIncludeTrace(page).begin();
        try {
            pageContext.include(page);
        } catch (Throwable t) {
            handleError(t);
        } finally {
            trace.end();
        }
    }

    protected void handleError(Throwable t) {
        ErrorReport errorReport = ErrorManager.lookup().notifyError(t, true);
        CodeBlockTrace trace = new JSPIncludeTrace(errorPage).begin();
        try {
            // Display the error page.
            ErrorReportHandler errorHandler = (ErrorReportHandler) Factory.lookup("org.jboss.dashboard.error.JSPIncludeErrorHandler");
            errorHandler.setErrorReport(errorReport);
            errorHandler.setCloseEnabled(false);
            pageContext.getRequest().setAttribute("errorHandlerName", "org.jboss.dashboard.error.JSPIncludeErrorHandler");
            pageContext.include(errorPage);
        } catch (Throwable t1) {
            log.error("JSP error processing failed.", t1);
            try {
                // If the error JSP rendering fails then print a simple error message.
                String errorStr = errorReport.printErrorMessage() + "\n\n" + errorReport.printContext(0);
                pageContext.getOut().println("<span class=\"skn-error\"><pre>" + errorStr + "</pre></span>");
            } catch (Throwable t2) {
                log.error("Cannot print a JSP error message.", t2);
            }
        } finally {
            trace.end();
        }
    }

    /**
     * The JSPInclude tag profiling trace.
     */
    public static class JSPIncludeTrace extends CodeBlockTrace {

        protected String jsp;

        public JSPIncludeTrace(String jsp) {
            super(jsp);
            this.jsp = jsp;
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.JSP_PAGE;
        }

        public String getDescription() {
            return jsp;
        }

        public Map<String,Object> getContext() {
            Map<String,Object> m =  new HashMap<String, Object>();
            m.put("JSP", jsp);
            return m;
        }
    }
}
