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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.components.FileNavigationHandler;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileNavigationFilePreviewFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FileNavigationFilePreviewFormatter.class.getName());

    private FileNavigationHandler fileNavigationHandler;

    public FileNavigationHandler getFileNavigationHandler() {
        return fileNavigationHandler;
    }

    public void setFileNavigationHandler(FileNavigationHandler fileNavigationHandler) {
        this.fileNavigationHandler = fileNavigationHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            FileObject currentFile = getFileNavigationHandler().getCurrentFile();
            if (currentFile != null) {
                String contentType = currentFile.getContent().getContentInfo().getContentType();
                if (contentType != null && contentType.startsWith("image")) {
                    String imageUrl = getImageUrl(currentFile);
                    setAttribute("imageUrl", imageUrl);
                    renderFragment("outputAsImage");
                } else {
                    String extension = currentFile.getName().getExtension();
                    String imageUrl = getFileNavigationHandler().getFilesystemManager().getLargeIconPath(extension);
                    setAttribute("imageUrl", imageUrl);
                    setAttribute("url", getFileNavigationHandler().getFilesystemManager().getURI(currentFile));
                    setAttribute("fileName", currentFile.getName().getBaseName());
                    renderFragment("outputAsFile");
                }
            }
        } catch (FileSystemException e) {
            log.error("Error: ", e);
        }
    }

    protected String getImageUrl(FileObject currentFile) {
        String uri = getFileNavigationHandler().getFilesystemManager().getURI(currentFile);
        return uri;
    }
}
