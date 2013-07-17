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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.LocaleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MessagesComponentHandler extends PanelComponent {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessagesComponentHandler.class.getName());

    protected int width;
    protected int height;

    private String componentIncludeJSP;
    private String i18nBundle;
    private boolean clearAfterRender = true;

    private List messagesToDisplay = new ArrayList();
    private List warningsToDisplay = new ArrayList();
    private List errorsToDisplay = new ArrayList();

    private List messagesParameters = new ArrayList();
    private List warningsParameters = new ArrayList();
    private List errorsParameters = new ArrayList();

    private String messagesComponentFormatter;

    public MessagesComponentHandler() {
        width = 600;
        height = 200;
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

    public List getMessagesToDisplay() {
        return messagesToDisplay;
    }

    public void setMessagesToDisplay(List messagesToDisplay) {
        this.messagesToDisplay = messagesToDisplay;
    }

    public List getWarningsToDisplay() {
        return warningsToDisplay;
    }

    public void setWarningsToDisplay(List warningsToDisplay) {
        this.warningsToDisplay = warningsToDisplay;
    }

    public List getErrorsToDisplay() {
        return errorsToDisplay;
    }

    public void setErrorsToDisplay(List errorsToDisplay) {
        this.errorsToDisplay = errorsToDisplay;
    }

    public List getMessagesParameters() {
        return messagesParameters;
    }

    public void setMessagesParameters(List messagesParameters) {
        this.messagesParameters = messagesParameters;
    }

    public List getWarningsParameters() {
        return warningsParameters;
    }

    public void setWarningsParameters(List warningsParameters) {
        this.warningsParameters = warningsParameters;
    }

    public List getErrorsParameters() {
        return errorsParameters;
    }

    public void setErrorsParameters(List errorsParameters) {
        this.errorsParameters = errorsParameters;
    }

    public String getComponentIncludeJSP() {
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

    public String getMessagesComponentFormatter() {
        return messagesComponentFormatter;
    }

    public void setMessagesComponentFormatter(String messagesComponentFormatter) {
        this.messagesComponentFormatter = messagesComponentFormatter;
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
        ResourceBundle rb = ResourceBundle.getBundle(bundle, LocaleManager.currentLocale());
        return rb.getString(key);
    }
}
