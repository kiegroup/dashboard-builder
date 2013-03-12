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

public class FileNavigationActionsFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FileNavigationActionsFormatter.class.getName());

    private FileNavigationHandler fileNavigationHandler;

    public FileNavigationHandler getFileNavigationHandler() {
        return fileNavigationHandler;
    }

    public void setFileNavigationHandler(FileNavigationHandler fileNavigationHandler) {
        this.fileNavigationHandler = fileNavigationHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            renderFilterFileList();
            renderCreateFolder();
            renderCreateFile();
            renderDeleteFolder();
            renderDeleteFile();
        } catch (FileSystemException e) {
            throw new FormatterException(e);
        }
    }

    protected void renderFilterFileList() throws FileSystemException {
        if (fileNavigationHandler.getCurrentDir() != null) {
            String currentFilter = getFileNavigationHandler().getCurrentFilter();
            setAttribute("currentFilter", currentFilter);
            renderFragment("filterFile");
        }
    }

    protected void renderDeleteFile() throws FileSystemException {
        if (fileNavigationHandler.isDeleteFileAllowed() || fileNavigationHandler.getUserStatus().isRootUser()) {
            FileObject currentFile = fileNavigationHandler.getCurrentFile();
            if (currentFile != null && currentFile.isWriteable()) {
                FileObject parentDir = currentFile.getParent();
                if (parentDir != null && parentDir.isWriteable()) {
                    //setAttribute("folderName", parentDir.getName().getBaseName());
                    setAttribute("fileName", currentFile.getName().getBaseName());
                    renderFragment("deleteFile");
                }
            }
        }
    }

    protected void renderDeleteFolder() throws FileSystemException {
        if (fileNavigationHandler.isDeleteFolderAllowed() || fileNavigationHandler.getUserStatus().isRootUser()) {
            if (!fileNavigationHandler.getCurrentPath().equals(fileNavigationHandler.getStartingPath())) {
                FileObject currentDir = fileNavigationHandler.getCurrentDir();
                if (currentDir != null && currentDir.isWriteable() && (currentDir.getChildren() == null || currentDir.getChildren().length == 0)) {
                    FileObject parentDir = currentDir.getParent();
                    if (parentDir != null && parentDir.isWriteable()) {
                        setAttribute("folderName", currentDir.getName().getBaseName());
                        renderFragment("deleteFolder");
                    }
                }
            }
        }
    }

    protected void renderCreateFile() throws FileSystemException {
        if (fileNavigationHandler.isUploadFileAllowed() || fileNavigationHandler.getUserStatus().isRootUser()) {
            FileObject currentDir = fileNavigationHandler.getCurrentDir();
            if (currentDir != null && currentDir.isWriteable()) {
                setAttribute("folderName", currentDir.getName().getBaseName());
                renderFragment("uploadFileInput");
            }
        }
    }

    protected void renderCreateFolder() throws FileSystemException {
        if (fileNavigationHandler.isCreateFolderAllowed() || fileNavigationHandler.getUserStatus().isRootUser()) {
            FileObject currentDir = fileNavigationHandler.getCurrentDir();
            if (currentDir != null && currentDir.isWriteable()) {
                setAttribute("folderName", currentDir.getName().getBaseName());
                renderFragment("createFolderInput");
            }
        }
    }
}
