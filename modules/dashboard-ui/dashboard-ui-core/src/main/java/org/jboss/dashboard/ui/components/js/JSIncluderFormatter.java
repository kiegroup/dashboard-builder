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
package org.jboss.dashboard.ui.components.js;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JSIncluderFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(JSIncluderFormatter.class.getName());

    private JSIncluderComponent jsIncluderComponent;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String path = jsIncluderComponent.getJSFileURL();
        if (!StringUtils.isEmpty(path)) {
            setAttribute("script", path);
            renderFragment("outputScript");
        }
        
        String[] jsps = jsIncluderComponent.getJSPFilesPath();
        if (!ArrayUtils.isEmpty(jsps)) {
            renderFragment("startJSP");
            
            for(String jsp : jsps) {
                setAttribute("jsp", jsp);
                renderFragment("JSP");
            }
            
            renderFragment("endJSP");
        }
    }

    public JSIncluderComponent getJsIncluderComponent() {
        return jsIncluderComponent;
    }

    public void setJsIncluderComponent(JSIncluderComponent jsIncluderComponent) {
        this.jsIncluderComponent = jsIncluderComponent;
    }
}
