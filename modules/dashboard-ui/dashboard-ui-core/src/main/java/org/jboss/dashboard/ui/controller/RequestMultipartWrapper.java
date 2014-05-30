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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Request wrapper that handles multipart post and retrieves files and parameters
 */
public class RequestMultipartWrapper extends HttpServletRequestWrapper {
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RequestMultipartWrapper.class.getName());

    /**
     * Map of parameters which are uploaded files *
     */
    protected Map<String, FileItem> requestFiles = new HashMap<String, FileItem>();
    /**
     * Map of regular parameters and their values
     */
    protected Map<String, List<String>> requestParameters = new HashMap<String, List<String>>();
    /**
     * Map of uploaded files
     */
    protected Map<String, File> uploadedFiles = new HashMap<String, File>();

    /**
     * Directory where temporary files are bein stored
     */
    private String privateDir = null;

    /**
     * Encoding used by the framework.
     */
    private String encoding = null;

    /**
     * Constructs a new MultipartRequest to handle the specified request,
     * saving any uploaded files to the given directory, and limiting the
     * upload size to the specified length.
     *
     * @param request       the servlet request.
     * @param saveDirectory the directory in which to save any uploaded files.
     * @param maxPostSize   the maximum size of the POST content.
     * @throws IOException if the uploaded content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public RequestMultipartWrapper(HttpServletRequest request, String saveDirectory, int maxPostSize, String encoding) throws IOException {
        super(request);
        this.privateDir = saveDirectory;
        this.encoding = encoding;

        DiskFileItemFactory factory = new DiskFileItemFactory();

        // factory.setSizeThreshold(yourMaxMemorySize);

        if (privateDir != null) {
            File dir = new File(privateDir);
            if (dir.isDirectory() && dir.canWrite()) {
                factory.setRepository(dir);
            } else {
                log.warn("Directory " + privateDir + " is not valid or permissions to write not granted");
            }
        }

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxPostSize);

        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e) {
            throw new IOException("Error parsing multipart in URI" + request.getRequestURI(), e);
        }

        if (items != null) {
            for (FileItem item : items) {
                if (item.isFormField()) {
                    addFormField(item);
                } else {
                    try {
                        addUploadedFile(item);
                    } catch (Exception e) {
                        throw new IOException("Error parsing multipart item " + item.getName(), e);
                    }
                }
            }
        }

        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            List<String> paramValues = Arrays.asList(request.getParameter(paramName));
            requestParameters.put(paramName, paramValues);
        }
    }

    /**
     * Adds a new regular field to the list of parameters
     *
     * @param item
     */
    protected void addFormField(FileItem item) throws IOException {
        String name = item.getFieldName();
        String itemValue = null;
        if (name != null) {
            List<String> values = requestParameters.get(name);
            if (values == null) {
                values = new ArrayList<String>();
                requestParameters.put(name, values);
            }
            itemValue = item.getString(encoding);
            if (itemValue != null) {
                values.add(itemValue);
            }
        }
    }

    /**
     * Adds a file field to the list of parameters
     *
     * @param item
     * @throws Exception
     */
    protected void addUploadedFile(FileItem item) throws Exception {
        // Process a file upload
        if (!item.isFormField()) {
            String fieldName = item.getFieldName();
            String fileName = item.getName();
            // FIX: Some browsers return the full path and others just the file name
            if (fileName != null && !fileName.trim().equals("")) {
                fileName = new File(item.getName()).getName();
            }
            if (fileName != null && !fileName.trim().equals("")) {
                File uploadedFile = new File(this.privateDir, fileName);
                item.write(uploadedFile);
                uploadedFile.deleteOnExit();
                if (uploadedFile.exists() && uploadedFile.length() > 0 && uploadedFile.canRead()) {
                    requestFiles.put(fieldName, item);
                    uploadedFiles.put(fieldName, uploadedFile);
                }
            }
        }
    }

    /**
     * Returns the parameter names on the MultipartRequest
     *
     * @return An Enumeration containing all the Parameter Names
     */
    public Enumeration<String> getParameterNames() {
        Vector<String> names = new Vector<String>(requestParameters.size() + requestFiles.size());
        names.addAll(requestParameters.keySet());
        names.addAll(requestFiles.keySet());
        return names.elements();
    }

    /**
     * Returns the value for the given parameter name
     *
     * @param name the parameter name
     * @return The value for the given parameter name
     */
    public String getParameter(String name) {
        List<String> values = requestParameters.get(name);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    /**
     * Returns the values for the given parameter name
     *
     * @param name the parameter name
     * @return The values for the given parameter name
     */
    public String[] getParameterValues(String name) {
        List<String> values = requestParameters.get(name);
        return (values == null || values.isEmpty()) ? null : values.toArray(new String[0]);
    }

    /**
     * Returns a Map containing all the parameters on the MultipartRequest
     *
     * @return Map containing all the parameters on the MultipartRequest
     */
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        Enumeration<String> enumParameters = getParameterNames();
        while (enumParameters.hasMoreElements()) {
            String name = enumParameters.nextElement();
            String[] values = getParameterValues(name);
            if (values != null) {
                map.put(name, values);
            }
        }
        return map;
    }

    /**
     * Returns the parameter names on the MultipartRequest that are of type file
     *
     * @return An Enumeration containing all the Parameter Names
     */
    public Enumeration<String> getFileParameterNames() {
        Vector<String> names = new Vector<String>(requestFiles.size());
        names.addAll(requestFiles.keySet());
        return names.elements();
    }

    /**
     * Returns the uploaded file for a given parameter, or null it not found
     *
     * @param parameter
     * @return
     */
    public File getUploadedFile(String parameter) {
        return uploadedFiles.get(parameter);
    }
}
