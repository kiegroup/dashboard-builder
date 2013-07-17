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

import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileResource extends Resource {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileResource.class.getName());
    protected File file = null;
    protected ResourceName resName = null;

    public static FileResource getInstance(ResourceName resName, File f) throws IOException {
        if (resName == null || f == null) throw new NullPointerException();
        if (!f.exists()) throw new FileNotFoundException(f.getCanonicalPath());
        return new FileResource(resName, f);
    }

    private FileResource(ResourceName resName, File f) {
        this.resName = resName;
        file = f;
    }

    public CommandResponse getResourceAsResponse() {
        if (file == null || !file.exists()) {
            log.debug("Resource does not exist: " + file);
            return new SendErrorResponse(HttpServletResponse.SC_NOT_FOUND);
        }
        try {
            return new SendStreamResponse(file);
        } catch (Exception e) {
            log.error("Failed to return resource named " + resName, e);
            return new SendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public InputStream getResourceAsStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Error:", e);
        }
        return null;
    }

    public String getResourceName() {
        return resName.toString();
    }

    public String getResourceFileName() {
        return file.getName();
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
