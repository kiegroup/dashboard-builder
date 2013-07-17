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
package org.jboss.dashboard.ui.taglib.formatter;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TemplateFormatter.class.getName());

    private String fragmentStart = "{";
    private String fragmentEnd = "}";

    public String getFragmentStart() {
        return fragmentStart;
    }

    public void setFragmentStart(String fragmentStart) {
        this.fragmentStart = fragmentStart;
    }

    public String getFragmentEnd() {
        return fragmentEnd;
    }

    public void setFragmentEnd(String fragmentEnd) {
        this.fragmentEnd = fragmentEnd;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String template = (String) getParameter("template");
        if (StringUtils.isEmpty(template)) {
            handleEmptyTemplate();
        } else {
            processTemplate(template);
        }
    }

    protected void processTemplate(String template) {
        int firstStartingDelimiter = template.indexOf(fragmentStart);
        int firstEndingDelimiter = template.indexOf(fragmentEnd, firstStartingDelimiter);
        if (firstStartingDelimiter == -1 || firstEndingDelimiter == -1) {
            writeToOut(template);
        } else {
            String textBefore = template.substring(0, firstStartingDelimiter);
            String fragmentName = template.substring(firstStartingDelimiter + 1, firstEndingDelimiter);
            String textAfter = template.substring(firstEndingDelimiter + 1, template.length());
            if (StringUtils.isEmpty(fragmentName)) {
                writeToOut(textBefore);
                writeToOut(fragmentStart);
                writeToOut(fragmentEnd);
            } else {
                writeToOut(textBefore);
                includeFragment(fragmentName);
            }
            processTemplate(textAfter);
        }
    }

    protected void includeFragment(String fragmentName) {
        setAttributesForFragment(fragmentName);
        renderFragment(fragmentName);
    }

    /**
     * Set attributes for rendering of specified fragment
     *
     * @param fragmentName fragment to be rendered
     */
    protected void setAttributesForFragment(String fragmentName) {
    }

    /**
     * Handle the case in which the template is empty
     */
    protected void handleEmptyTemplate() {
    }

}
