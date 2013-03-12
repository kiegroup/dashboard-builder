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

import java.util.Map;
import java.util.Set;

/**
 * This interface acts encapsulates access to the request, response and session objects,
 * to allow the implementators to override this objects' behaviour.
 */
public interface CommandRequest{

    /**
     * @deprecated Access request object directly
     */
    String getParameter(String name);

    /**
     * Returns the number of files being uploaded in the request
     */
    int getUploadedFilesCount();

    /**
     * Returns the original request object
     */
    javax.servlet.http.HttpServletRequest getRequestObject();

    Set getParameterNames();

    /**
     * Returns the original session object
     */
    javax.servlet.http.HttpSession getSessionObject();


    /**
     * Returns the original response object
     */
    javax.servlet.http.HttpServletResponse getResponseObject();

    /**
     * Get a map of files, where keys are parameter names used.
     *
     * @return files parameter map.
     */
    public Map getFilesByParamName();

}