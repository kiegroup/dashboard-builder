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
package org.jboss.dashboard.ui.panel.help;

import java.util.*;

/**
 *
 */
public class PanelHelpImpl implements PanelHelp {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelHelpImpl.class.getName());

    private List ids = new ArrayList();
    private Map usages = new HashMap();
    private Map editUsages = new HashMap();
    private Map paramDescriptions = new HashMap();
    private Map extraParams = new HashMap();
    private PanelAbout about;


    /**
     * Panel provider ids for which the help is valid
     *
     * @return A list of panel provider ids.
     */
    public String[] getIds() {
        return (String[]) ids.toArray(new String[ids.size()]);
    }

    /**
     * General panel usage in given lang.
     *
     * @param lang Desired language for the description.
     * @return General panel usage in given lang.
     */
    public String getUsage(Locale lang) {
        return (String) usages.get(lang.getLanguage());
    }

    /**
     * Edit mode panel usage in given lang.
     *
     * @param lang Desired language for the description.
     * @return General panel edit mode usage in given lang.
     */
    public String getEditModeUsage(Locale lang) {
        return (String) editUsages.get(lang.getLanguage());
    }

    /**
     * Determine the parameter id's that have related description.
     *
     * @return the parameter id's that have related description.
     */
    public String[] getParameterNames() {
        return (String[]) paramDescriptions.keySet().toArray(new String[paramDescriptions.size()]);
    }

    /**
     * Determine the parameter help string for a given parameter
     * in a given lang.
     *
     * @param parameterName Parameter name
     * @param lang          Desired language for the description.
     * @return the parameter help string for a given parameter
     *         in a given lang.
     */
    public String getParameterUsage(String parameterName, Locale lang) {
        Map m = (Map) paramDescriptions.get(parameterName);
        if (m != null) {
            return (String) m.get(lang.getLanguage());
        }
        return null;
    }

    /**
     * Determine the extra message keys
     *
     * @return A list of extra message keys.
     */
    public String[] getMessages() {
        return (String[]) extraParams.keySet().toArray(new String[extraParams.size()]);
    }

    /**
     * Get the message associated with a message key
     *
     * @param messageName message key
     * @param lang        Desired language for the description.
     * @return the message associated with a message key
     */
    public String getMessage(String messageName, Locale lang) {
        Map m = (Map) extraParams.get(messageName);
        if (m != null) {
            return (String) m.get(lang.getLanguage());
        }
        return null;
    }

    /**
     * Get the about information
     *
     * @return the panel's about information
     */
    public PanelAbout getAbout() {
        return about;
    }

    public void setAbout(PanelAbout about) {
        this.about = about;
    }

    public void addId(String id) {
        ids.add(id);
    }

    public void addUsage(Locale lang, String usage) {
        usages.put(lang.getLanguage(), usage);
    }

    public void addEditUsage(Locale lang, String usage) {
        editUsages.put(lang.getLanguage(), usage);
    }

    public void addParamDescription(String parameterName, Locale lang, String description) {
        Map m = (Map) paramDescriptions.get(parameterName);
        if (m == null) paramDescriptions.put(parameterName, m = new HashMap());
        m.put(lang.getLanguage(), description);
    }

    public void addMessage(String messageName, Locale lang, String description) {
        Map m = (Map) extraParams.get(messageName);
        if (m == null) extraParams.put(messageName, m = new HashMap());
        m.put(lang.getLanguage(), description);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Help for panels: ").append(ids).append("\n");
        sb.append("Usages: ").append(usages).append("\n");
        sb.append("EditUsages: ").append(editUsages).append("\n");
        sb.append("ParamDescriptions: ").append(paramDescriptions).append("\n");
        sb.append("ExtraParams: ").append(extraParams).append("\n");
        return sb.toString();
    }
}
