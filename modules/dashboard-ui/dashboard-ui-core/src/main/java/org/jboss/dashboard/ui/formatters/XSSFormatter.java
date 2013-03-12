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

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XSSFormatter extends Formatter {
    public org.jboss.dashboard.ui.components.XSSHandler xssHandler;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        if (xssHandler.getScriptToRun() != null) {
            setAttribute("scr", xssHandler.getScriptToRun());
            renderFragment("output");
            xssHandler.setScriptToRun(null);
        }
    }
}
