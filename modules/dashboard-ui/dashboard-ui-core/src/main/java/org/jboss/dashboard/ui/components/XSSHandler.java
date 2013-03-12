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

import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.controller.CommandRequest;


public class XSSHandler extends UIComponentHandlerFactoryElement {

    private String componentIncludeJSP = "/components/xss/show.jsp";
    private String scriptToRun = null;

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getScriptToRun() {
        return scriptToRun;
    }

    public void setScriptToRun(String scriptToRun) {
        this.scriptToRun = scriptToRun;
    }

    public void actionRunScript(CommandRequest req) {
        String script = req.getRequestObject().getParameter("scr");
        if (script != null) {
            scriptToRun = new String(Base64.decode(script));
        }
    }
}
