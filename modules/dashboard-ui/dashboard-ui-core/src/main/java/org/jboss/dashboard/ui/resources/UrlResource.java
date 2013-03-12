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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.RedirectToURLResponse;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 */
public class UrlResource extends Resource {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(UrlResource.class.getName());

    private ResourceName resName;
    protected String url = null;
    protected String context = null;
    protected String path = null;


    public static UrlResource getInstance(ResourceName resName, String context, String path) {
        if (resName == null || path == null || "".equals(path))
            throw new NullPointerException();
        return new UrlResource(resName, context, path);
    }

    private UrlResource(ResourceName resName, String context, String path) {
        this.resName = resName;
        this.context = (context == null || "".equals(context)) ? "" : (context.startsWith("/") ? context : ("/" + context));
        this.path = path;
        url = this.context + this.path;
        log.debug("Constructed UrlResource with context=" + this.context + ", path=" + this.path + ", url=" + url);
    }

    public CommandResponse getResourceAsResponse() {
        return new RedirectToURLResponse(url);
    }

    public InputStream getResourceAsStream() {
        String filePath = Application.lookup().getBaseAppDirectory() + "/" + path;
        try {
            return new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            log.error("Error:", e);
        }
        return null;
    }

    public String getResourceName() {
        return resName.toString();
    }

    public String getResourceFileName() {
        return path;
    }

    public String getResourceUrl(ServletRequest request, ServletResponse response, boolean portableUrl) {
        if (portableUrl)
            return super.getResourceUrl(request, response, portableUrl);
        return url;//Better than passing through ResourceCommands
    }

    /**
     * Return the uri without context
     *
     * @param request
     * @param response
     * @return
     */
    public String getResourcePage(ServletRequest request, ServletResponse response) {
        return path;
    }

    /**
     * The resource portable name (without workspace info)
     *
     * @return
     */
    public String getPortableResourceName() {
        return resName.getPortableResourceName();
    }
}
