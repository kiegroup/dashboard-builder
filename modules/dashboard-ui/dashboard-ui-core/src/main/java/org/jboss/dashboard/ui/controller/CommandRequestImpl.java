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
package org.jboss.dashboard.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * Implementation of a CommandRequest object.
 */
public class CommandRequestImpl implements CommandRequest {

    /**
     * Current request object
     */
    private HttpServletRequest req = null;

    /**
     * Current response object
     */
    private HttpServletResponse res = null;


    private Map filesByParamName;

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommandRequestImpl.class.getName());

    /**
     * Regular constructor
     */
    public CommandRequestImpl(HttpServletRequest req, HttpServletResponse res) {
        super();
        this.req = req;
        this.res = res;
        buildFiles();
    }

    /**
     * Retrieves files from a multipart request
     */
    private void buildFiles() {
        try {
            /*
             *  Hack for handling multipart requests.
             */
            if (req instanceof RequestMultipartWrapper) {
                RequestMultipartWrapper wrap = (RequestMultipartWrapper) req;
                Enumeration en = wrap.getFileParameterNames();
                if (en != null && en.hasMoreElements()) {
                    filesByParamName = new HashMap();
                    while (en.hasMoreElements()) {
                        String name = (String) en.nextElement();
                        File file = wrap.getUploadedFile(name);
                        if (file != null) {
                            filesByParamName.put(name, file);
                            log.debug("Found file " + file.getName() + " in " + file.getAbsoluteFile());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error building files", e);
        }
    }

    public javax.servlet.http.HttpServletRequest getRequestObject() {
        return req;
    }

    /**
     * @return
     * @deprecated Access request object directly
     */
    public Set getParameterNames() {
        Set s = new HashSet();
        Enumeration en = getRequestObject().getParameterNames();
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            s.add(o);
        }
        return s;
    }

    public javax.servlet.http.HttpServletResponse getResponseObject() {
        return res;
    }

    public Map getFilesByParamName() {
        return filesByParamName == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(filesByParamName);
    }

    public String getParameter(String name) {
        return getRequestObject().getParameter(name);
    }

    public int getUploadedFilesCount() {
        return filesByParamName != null ? filesByParamName.size() : 0;
    }

    public javax.servlet.http.HttpSession getSessionObject() {
        return getRequestObject().getSession();
    }
}
