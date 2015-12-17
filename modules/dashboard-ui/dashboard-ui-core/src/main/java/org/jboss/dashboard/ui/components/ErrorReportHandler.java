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
package org.jboss.dashboard.ui.components;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.error.ErrorReport;

@SessionScoped
public class ErrorReportHandler extends PanelComponent {

    public static ErrorReportHandler lookup() {
        return CDIBeanLocator.getBeanByType(ErrorReportHandler.class);
    }

    protected ErrorReport errorReport = null;

    protected Runnable closeListener = null;

    @Inject @Config("/components/errorReport/show.jsp")
    protected String beanJSP;

    @Inject @Config("1000")
    protected int width;

    @Inject @Config("400")
    protected int height;

    @Inject @Config("true")
    protected boolean closeEnabled;

    public boolean isCloseEnabled() {
        return closeEnabled;
    }

    public void setCloseEnabled(boolean closeEnabled) {
        this.closeEnabled = closeEnabled;
    }

    public Runnable getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(Runnable closeListener) {
        this.closeListener = closeListener;
    }

    public String getBeanJSP() {
        return beanJSP;
    }

    public ErrorReport getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void actionContinue(CommandRequest request) throws Exception {
        errorReport = null;
        if (closeListener != null) {
            closeListener.run();
        }
    }
}
