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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a graphic resource.
 */
public abstract class Resource {

    /**
     * CommandResponse that will send the resource to the browser
     *
     * @return a CommandResponse that will send the resource to the browser
     */
    public abstract CommandResponse getResourceAsResponse();

    /**
     * An InputStream to the resource bytes.
     *
     * @return An InputStream to the resource bytes.
     */
    public abstract InputStream getResourceAsStream();

    /**
     * The resource full name
     *
     * @return the resource name
     */
    public abstract String getResourceName();

    /**
     * The resource file name when deployed
     *
     * @return The resource file name when deployed
     */
    public abstract String getResourceFileName();

    /**
     * The resource portable name (without workspace info)
     *
     * @return The resource portable name (without workspace info)
     */
    public abstract String getPortableResourceName();

    /**
     * Get an url to the resource
     *
     * @param request
     * @param response
     * @param portableUrl Indicate if url shoulf be portable (slower) or not (faster for URL resources).
     * @return an url to the resource
     */
    public String getResourceUrl(ServletRequest request, ServletResponse response, boolean portableUrl) {
        URLMarkupGenerator markupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        Map params = new HashMap();
        params.put("resName", portableUrl ? getPortableResourceName() : getResourceName());
        return markupGenerator.getPermanentLink("org.jboss.dashboard.ui.components.ResourcesHandler", "retrieve", params);
    }

}
