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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ResourceBundle;

public class MessagesComponentFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private MessagesComponentHandler messagesComponentHandler;

    @Inject @Config("5")
    private int maxVisibleErrors = 5;

    @Inject @Config("general/32x32/info.gif")
    private String messagesImg;

    @Inject @Config("general/32x32/warning.gif")
    private String warningsImg;

    @Inject @Config("general/32x32/error.gif")
    private String errorsImg;

    @Inject @Config("")
    private String classForMessages;

    @Inject @Config("skn-error")
    private String classForWarnings;

    @Inject @Config("skn-error")
    private String classForErrors;

    /** The locale manager. */
    protected LocaleManager localeManager;

    public MessagesComponentFormatter() {
        localeManager = LocaleManager.lookup();
    }

    public MessagesComponentHandler getMessagesComponentHandler() {
        return messagesComponentHandler;
    }

    public void setMessagesComponentHandler(MessagesComponentHandler messagesComponentHandler) {
        this.messagesComponentHandler = messagesComponentHandler;
    }

    public int getMaxVisibleErrors() {
        return maxVisibleErrors;
    }

    public void setMaxVisibleErrors(int maxVisibleErrors) {
        this.maxVisibleErrors = maxVisibleErrors;
    }

    public String getClassForMessages() {
        return classForMessages;
    }

    public void setClassForMessages(String classForMessages) {
        this.classForMessages = classForMessages;
    }

    public String getClassForWarnings() {
        return classForWarnings;
    }

    public void setClassForWarnings(String classForWarnings) {
        this.classForWarnings = classForWarnings;
    }

    public String getClassForErrors() {
        return classForErrors;
    }

    public void setClassForErrors(String classForErrors) {
        this.classForErrors = classForErrors;
    }

    public String getMessagesImg() {
        return messagesImg;
    }

    public void setMessagesImg(String messagesImg) {
        this.messagesImg = messagesImg;
    }

    public String getWarningsImg() {
        return warningsImg;
    }

    public void setWarningsImg(String warningsImg) {
        this.warningsImg = warningsImg;
    }

    public String getErrorsImg() {
        return errorsImg;
    }

    public void setErrorsImg(String errorsImg) {
        this.errorsImg = errorsImg;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        if (messagesComponentHandler.getErrorsToDisplay() != null && messagesComponentHandler.getErrorsToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getErrorsToDisplay(), messagesComponentHandler.getErrorsParameters(), errorsImg, classForErrors);
        } else if (messagesComponentHandler.getWarningsToDisplay() != null && messagesComponentHandler.getWarningsToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getWarningsToDisplay(), messagesComponentHandler.getWarningsParameters(), warningsImg, classForWarnings);
        } else if (messagesComponentHandler.getMessagesToDisplay() != null && messagesComponentHandler.getMessagesToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getMessagesToDisplay(), messagesComponentHandler.getMessagesParameters(), messagesImg, classForMessages);
        }
    }

    protected void renderMessages(List messages, List params, String img, String className) {
        while (messages.size() > params.size()) {
            params.add(null);
        }
        long id = System.currentTimeMillis();
        boolean maxRised = false;

        setAttribute("image", img);
        setAttribute("bundle", messagesComponentHandler.getI18nBundle());
        renderFragment("outputStart");

        renderFragment("outputVisibleMessagesStart");

        for (int i = 0; i < messages.size(); i++) {
            if (i == maxVisibleErrors) {
                renderFragment("outputMessagesEnd");
                renderFragment("outputNewLine");
                setAttribute("id", id);
                renderFragment("outputHiddenMessagesStart");
                maxRised = true;
            }
            setAttribute("bundle", messagesComponentHandler.getI18nBundle());
            setAttribute("msg", messages.get(i));
            setAttribute("params", params.get(i));
            setAttribute("className", className);
            renderFragment("outputMessage");
        }
        renderFragment("outputMessagesEnd");
        if (maxRised) {
            renderFragment("outputNewLine");
            setAttribute("id", id);
            renderFragment("outputDisplayLinks");
        }
        renderFragment("outputEnd");
        if (messagesComponentHandler.isClearAfterRender()) messagesComponentHandler.clearAll();
    }

    protected String localizeMessage(String message) {
        try {
            if (messagesComponentHandler.getI18nBundle() != null) {
                ResourceBundle bundle = localeManager.getBundle(messagesComponentHandler.getI18nBundle(), LocaleManager.currentLocale());
                message = bundle.getString(message);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Error trying to get message '" + message + "' from bundle '" + messagesComponentHandler.getI18nBundle());
        }
        return message;
    }
}
