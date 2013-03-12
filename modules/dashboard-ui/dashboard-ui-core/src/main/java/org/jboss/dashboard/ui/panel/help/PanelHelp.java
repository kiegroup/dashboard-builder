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
package org.jboss.dashboard.ui.panel.help;

import java.util.Locale;

public interface PanelHelp {

    /**
     * Panel provider ids for which the help is valid
     *
     * @return A list of panel provider ids.
     */
    String[] getIds();

    /**
     * General panel usage in given lang.
     *
     * @param lang Desired language for the description.
     * @return General panel usage in given lang.
     */
    String getUsage(Locale lang);

    /**
     * Edit mode panel usage in given lang.
     *
     * @param lang Desired language for the description.
     * @return General panel edit mode usage in given lang.
     */
    String getEditModeUsage(Locale lang);

    /**
     * Determine the parameter id's that have related description.
     *
     * @return the parameter id's that have related description.
     */
    String[] getParameterNames();

    /**
     * Determine the parameter help string for a given parameter
     * in a given lang.
     *
     * @param parameterName Parameter name
     * @param lang          Desired language for the description.
     * @return the parameter help string for a given parameter
     *         in a given lang.
     */
    String getParameterUsage(String parameterName, Locale lang);

    /**
     * Determine the extra message keys
     *
     * @return A list of extra message keys.
     */
    String[] getMessages();

    /**
     * Get the message associated with a message key
     *
     * @param messageName message key
     * @param lang        Desired language for the description.
     * @return the message associated with a message key
     */
    String getMessage(String messageName, Locale lang);

    /**
     * Get the about information
     *
     * @return the panel's about information
     */
    PanelAbout getAbout();
}
