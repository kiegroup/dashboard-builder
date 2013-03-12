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

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.UIComponentHandlerFactoryElement;

public class JSIncluderComponent extends UIComponentHandlerFactoryElement {

    private String position;
    private String componentIncludeJSP = "/components/js/component.jsp";
    private String componentFormatter;

    public String getJSFileURL() {
        return getJsIncluder().getJSFileURL(position);
    }
    
    public String[] getJSPFilesPath() {
        return getJsIncluder().getJSPFilesPath(position);
    }

    @Override
    public void beforeRenderComponent() {
        getJsIncluder().checkAndDeploy(position);
    }

    public JSIncluder getJsIncluder() {
        return UIServices.lookup().getJsIncluder();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getComponentFormatter() {
        return componentFormatter;
    }

    public void setComponentFormatter(String componentFormatter) {
        this.componentFormatter = componentFormatter;
    }
}
