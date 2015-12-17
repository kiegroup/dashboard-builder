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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Named;

@PanelScoped
@Named("messages_handler")
public class MessagesComponentHandler extends PanelComponent {

    public static MessagesComponentHandler lookup() {
        return CDIBeanLocator.getBeanByType(MessagesComponentHandler.class);
    }

    @Inject
    private transient Logger log;

    @Inject @Config("/components/messages/show.jsp")
    private String componentIncludeJSP;

    protected int width;
    protected int height;
    private String i18nBundle;
    private boolean clearAfterRender = true;

    private List<String> messagesToDisplay = new ArrayList<String>();
    private List<String> warningsToDisplay = new ArrayList<String>();
    private List<String> errorsToDisplay = new ArrayList<String>();

    private List<String[]> messagesParameters = new ArrayList<String[]>();
    private List<String[]> warningsParameters = new ArrayList<String[]>();
    private List<String[]> errorsParameters = new ArrayList<String[]>();

    /** The locale manager. */
    protected LocaleManager localeManager;

    public MessagesComponentHandler() {
        width = 600;
        height = 200;
        localeManager = LocaleManager.lookup();
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

    public List<String> getMessagesToDisplay() {
        return messagesToDisplay;
    }

    public void setMessagesToDisplay(List<String> messagesToDisplay) {
        this.messagesToDisplay = messagesToDisplay;
    }

    public List<String> getWarningsToDisplay() {
        return warningsToDisplay;
    }

    public void setWarningsToDisplay(List<String> warningsToDisplay) {
        this.warningsToDisplay = warningsToDisplay;
    }

    public List<String> getErrorsToDisplay() {
        return errorsToDisplay;
    }

    public void setErrorsToDisplay(List<String> errorsToDisplay) {
        this.errorsToDisplay = errorsToDisplay;
    }

    public List<String[]> getMessagesParameters() {
        return messagesParameters;
    }

    public void setMessagesParameters(List<String[]> messagesParameters) {
        this.messagesParameters = messagesParameters;
    }

    public List<String[]> getWarningsParameters() {
        return warningsParameters;
    }

    public void setWarningsParameters(List<String[]> warningsParameters) {
        this.warningsParameters = warningsParameters;
    }

    public List<String[]> getErrorsParameters() {
        return errorsParameters;
    }

    public void setErrorsParameters(List<String[]> errorsParameters) {
        this.errorsParameters = errorsParameters;
    }

    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getI18nBundle() {
        return i18nBundle;
    }

    public void setI18nBundle(String i18nBundle) {
        this.i18nBundle = i18nBundle;
    }

    public boolean isClearAfterRender() {
        return clearAfterRender;
    }

    public void setClearAfterRender(boolean clearAfterRender) {
        this.clearAfterRender = clearAfterRender;
    }

    public void clearAll() {
        messagesToDisplay.clear();
        warningsToDisplay.clear();
        errorsToDisplay.clear();
        messagesParameters.clear();
        warningsToDisplay.clear();
        errorsToDisplay.clear();
    }

    public void addMessage(String message) {
        addMessage(message, null);
    }

    public void addWarning(String message) {
        addWarning(message, null);
    }

    public void addError(String message) {
        addError(message, null);
    }

    public void addMessage(String message, String[] params) {
        if (isValidMessage(message)) {
            messagesToDisplay.add(message);
            messagesParameters.add(params);
        }
    }

    public void addWarning(String message, String[] params) {
        if (isValidMessage(message)) {
            warningsToDisplay.add(message);
            warningsParameters.add(params);
        }
    }

    public void addError(String message, String[] params) {
        if (isValidMessage(message)) {
            errorsToDisplay.add(message);
            errorsParameters.add(params);
        }
    }

    protected boolean isValidMessage(String message) {
        return message != null && !"".equals(message);
    }

    public void addMessageFromBundle(String bundle, String key) {
        addMessage(getStringFromBundle(bundle, key));
    }

    public void addWarningFromBundle(String bundle, String key) {
        addWarning(getStringFromBundle(bundle, key));
    }

    public void addErrorFromBundle(String bundle, String key) {
        addError(getStringFromBundle(bundle, key));
    }

    protected String getStringFromBundle(String bundle, String key) {
        ResourceBundle rb = localeManager.getBundle(bundle, LocaleManager.currentLocale());
        return rb.getString(key);
    }
}
