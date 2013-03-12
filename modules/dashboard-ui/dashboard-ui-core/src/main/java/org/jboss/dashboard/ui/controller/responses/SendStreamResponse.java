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

package org.jboss.dashboard.ui.controller.responses;

import org.jboss.dashboard.ui.HTTPSettings;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * This class allows sending a file as response.
 */
public class SendStreamResponse implements CommandResponse {
    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SendStreamResponse.class.getName());

    protected InputStream is;
    protected String contentDisposition;
    protected int errorCode = 0;
    protected boolean resetHeaders = true;
    protected String contentType = "application/force-download";
    protected int contentLength = -1;

    /**
     * Send a stream as response, resetting response header
     *
     * @param is                 Stream to send
     * @param contentDisposition Should be something like "inline; filename=file.xxx;"
     */
    public SendStreamResponse(InputStream is, String contentDisposition) {
        init(is, contentDisposition);
    }

     /**
     * Send a stream as response, resetting response header
     *
     * @param is                 Stream to send
     * @param contentDisposition Should be something like "inline; filename=file.xxx;"
     * @param contentLength      Lenght of the stream's content
     */
    public SendStreamResponse(InputStream is, String contentDisposition, int contentLength) {
        this.contentLength = contentLength;
        init(is, contentDisposition);
    }

    /**
     * Send a file as response.
     *
     * @param f File to send
     */
    public SendStreamResponse(File f) {
        try {
            init(new FileInputStream(f), "inline; filename=" + URLEncoder.encode(f.getName()) + ";");
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            contentType = fileNameMap.getContentTypeFor(f.getName());
        } catch (FileNotFoundException e) {
            log.error("Error:", e);
            errorCode = HttpServletResponse.SC_NOT_FOUND;
        }
    }

    /**
     * Send a stream as response. Normally, you need to setContentType() before returning this response. The
     * content type can be inferred in any way you want, but it is recommended to use
     * <p/>
     * URLConnection.getFileNameMap().getContentTypeFor( );
     *
     * @param is                 Stream to send
     * @param contentDisposition Should be something like "inline; filename=file.xxx;"
     * @param resetHeaders       Indicates if headers have to be resetted. Default is false, but should be true if the response is JSP or something else that you don't want to be cached.
     */
    public SendStreamResponse(InputStream is, String contentDisposition, boolean resetHeaders) {
        this.resetHeaders = resetHeaders;
        init(is, contentDisposition);
    }

    /**
     * Send a file as response.
     *
     * @param f            File to send
     * @param resetHeaders Indicates if headers have to be resetted. Default is false, but should be true if the response is JSP or something else that you don't want to be cached.
     */
    public SendStreamResponse(File f, boolean resetHeaders) {
        this(f);
        this.resetHeaders = resetHeaders;
    }

    protected void init(InputStream is, String contentDisposition) {
        this.is = new BufferedInputStream(is);
        this.contentDisposition = contentDisposition;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("SendStreamResponse");
        
        HttpServletResponse res = cmdReq.getResponseObject();

        if (errorCode != 0) {
            res.sendError(errorCode);
            return false;
        }

        if (resetHeaders) {
            //Remove everything, including headers. The stream is often a file that may be cached.
            res.reset();
        }

        res.setHeader("Content-Encoding", HTTPSettings.lookup().getEncoding());
        if (contentDisposition != null) {
            res.setHeader("Content-Disposition", contentDisposition);
            log.debug("Content-Disposition = " + contentDisposition);
        }
        if (contentType != null) {
            res.setContentType(contentType);
        }
        res.setHeader("Content-Transfer-Encoding", "binary");

        if (contentLength < 0) contentLength = is.available();
        if (contentLength > 0) {
            res.setContentLength(contentLength);
            log.debug("Content-Length = " + contentLength);
        }

        try {
            OutputStream os = new BufferedOutputStream(cmdReq.getResponseObject().getOutputStream());
            IOUtils.copy(is, os);
            os.close();
            is.close();
        } catch (Exception e) {
            log.warn("Error sending Stream Response: " + e);
            //res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        return true;
    }
}
