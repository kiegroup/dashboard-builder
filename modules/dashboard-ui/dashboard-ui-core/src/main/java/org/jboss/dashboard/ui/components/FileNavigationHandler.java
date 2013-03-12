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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.filesystem.FileSystemManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowScreenResponse;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.io.IOUtils;
import org.jboss.dashboard.CoreServices;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class FileNavigationHandler extends UIComponentHandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FileNavigationHandler.class.getName());

    private String componentIncludeJSP = "/components/filesystem/navigation/show.jsp";
    private String baseDirPath = "/";
    private String startingPath = "/";
    private String currentPath = "/";
    private Set openPaths = new TreeSet();
    private String currentFilePath;
    private String selectedFile;

    private String actionsFormatter;
    private String fileListFormatter;
    private String filePreviewFormatter;
    private String treeFormatter;

    private boolean createFolderAllowed = true;
    private boolean uploadFileAllowed = true;
    private boolean deleteFolderAllowed = true;
    private boolean deleteFileAllowed = true;
    private String currentFilter;

    public void start() throws Exception {
        super.start();
        calculateStartingPath();
        openPaths.add(getStartingPath());
        currentPath = getStartingPath();
    }

    public String getStartingPath() {
        return startingPath;
    }

    public String getSelectedFile() {
        String file = selectedFile;
        selectedFile = null;
        return file;
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getActionsFormatter() {
        return actionsFormatter;
    }

    public void setActionsFormatter(String actionsFormatter) {
        this.actionsFormatter = actionsFormatter;
    }

    public String getFileListFormatter() {
        return fileListFormatter;
    }

    public void setFileListFormatter(String fileListFormatter) {
        this.fileListFormatter = fileListFormatter;
    }

    public String getFilePreviewFormatter() {
        return filePreviewFormatter;
    }

    public void setFilePreviewFormatter(String filePreviewFormatter) {
        this.filePreviewFormatter = filePreviewFormatter;
    }

    public String getTreeFormatter() {
        return treeFormatter;
    }

    public void setTreeFormatter(String treeFormatter) {
        this.treeFormatter = treeFormatter;
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public void calculateStartingPath() {
        startingPath = getBaseDirPath();
        String login = "";
        if (UserStatus.lookup().isAnonymous()) login = UserStatus.lookup().getUserLogin();
        startingPath = StringUtils.replace(startingPath, "{login}", login);

        if (!StringUtils.isEmpty(startingPath)) {
            try {
                FileObject fileObject = getFilesystemManager().getFileSystem().resolveFile(startingPath);
                if (!fileObject.exists()) {
                    fileObject.createFolder();
                }
            } catch (FileSystemException e) {
                log.error("Error: ", e);
            }
        }
    }

    public String getBaseDirPath() {
        return baseDirPath;
    }

    public void setBaseDirPath(String baseDirPath) {
        this.baseDirPath = baseDirPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public FileSystemManager getFilesystemManager() {
        return CoreServices.lookup().getFileSystemManager();
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public boolean isCreateFolderAllowed() {
        return createFolderAllowed;
    }

    public void setCreateFolderAllowed(boolean createFolderAllowed) {
        this.createFolderAllowed = createFolderAllowed;
    }

    public boolean isUploadFileAllowed() {
        return uploadFileAllowed;
    }

    public void setUploadFileAllowed(boolean uploadFileAllowed) {
        this.uploadFileAllowed = uploadFileAllowed;
    }

    public boolean isDeleteFolderAllowed() {
        return deleteFolderAllowed;
    }

    public void setDeleteFolderAllowed(boolean deleteFolderAllowed) {
        this.deleteFolderAllowed = deleteFolderAllowed;
    }

    public boolean isDeleteFileAllowed() {
        return deleteFileAllowed;
    }

    public void setDeleteFileAllowed(boolean deleteFileAllowed) {
        this.deleteFileAllowed = deleteFileAllowed;
    }

    public FileObject getRootDirectory() throws FileSystemException {
        return getFilesystemManager().getFileSystem().resolveFile(getStartingPath());
    }

    public Set getOpenPaths() {
        return openPaths;
    }

    public void setOpenPaths(Set openPaths) {
        this.openPaths = openPaths;
    }

    public FileObject getCurrentDir() throws FileSystemException {
        if (!StringUtils.isEmpty(currentPath)) {
            return getFilesystemManager().getFileSystem().resolveFile(currentPath);
        }
        return null;
    }

    public FileObject getCurrentFile() throws FileSystemException {
        if (!StringUtils.isEmpty(currentFilePath)) {
            return getFilesystemManager().getFileSystem().resolveFile(currentFilePath);
        }
        return null;
    }

    //Actions

    /**
     * Open given directory
     */
    public CommandResponse actionOpen(CommandRequest request) {
        openPaths.add(request.getRequestObject().getParameter("path"));
        return getCommonResponse(request);
    }

    /**
     * Close given directory
     */
    public CommandResponse actionClose(CommandRequest request) {
        //ensure it is open, and select it, which causes an openorclose
        openPaths.add(request.getRequestObject().getParameter("path"));
        return actionSelect(request);
    }

    /**
     * Open or close given directory
     */
    public CommandResponse actionOpenOrClose(CommandRequest request) {
        String path = request.getRequestObject().getParameter("path");
        if (openPaths.contains(path))
            openPaths.remove(path);
        else
            openPaths.add(path);
        return getCommonResponse(request);
    }

    /**
     * Select given directory, and open or close it
     */
    public CommandResponse actionSelect(CommandRequest request) {
        currentPath = request.getRequestObject().getParameter("path");
        if (currentPath != null && currentFilePath != null) {
            if (!currentFilePath.startsWith(currentPath))
                currentFilePath = null;
            else {
                String fileName = currentFilePath.substring(currentPath.length());
                if (fileName.indexOf("/") != -1) {
                    currentFilePath = null; //It is not directly in selected folder
                }
            }
        }
        return actionOpenOrClose(request);
    }

    /**
     * Select current file
     */
    public CommandResponse actionSelectFile(CommandRequest request) {
        currentFilePath = request.getRequestObject().getParameter("path");
        return getCommonResponse(request);
    }

    public CommandResponse actionChooseFile(CommandRequest request) throws FileSystemException {
        FileObject file = getCurrentFile();
        if (file != null)
            selectedFile = getFilesystemManager().getURI(file);
        return getCommonResponse(request);
    }

    public CommandResponse actionCreateFolder(CommandRequest request) throws FileSystemException {
        if (createFolderAllowed || getUserStatus().isRootUser()) {
            FileObject currentDir = getCurrentDir();
            if (currentDir != null && currentDir.isWriteable()) {
                String folderName = request.getRequestObject().getParameter("folderName");
                FileObject fileObject = getCurrentDir().resolveFile(folderName);
                if (!fileObject.exists()) {
                    fileObject.createFolder();
                    openPaths.add(currentPath);
                    currentPath = fileObject.getName().getPath();
                    selectedFile = null;
                    return getCommonResponse(request);
                } else {
                    //TODO: Deal with errors
                }
            }
        }
        //TODO: Deal with permission error
        return getCommonResponse(request);
    }

    public CommandResponse actionUploadFile(CommandRequest request) throws IOException {
        if (uploadFileAllowed || getUserStatus().isRootUser()) {
            FileObject currentDir = getCurrentDir();
            if (currentDir != null && currentDir.isWriteable()) {
                File file = (File) request.getFilesByParamName().get("file");
                if (file != null) {
                    String fileName = file.getName();
                    FileObject fileObject = currentDir.resolveFile(fileName);
                    if (!fileObject.exists()) {
                        fileObject.createFile();
                        OutputStream os = fileObject.getContent().getOutputStream(true);
                        InputStream is = new BufferedInputStream(new FileInputStream(file));
                        IOUtils.copy(is, os);
                        is.close();
                        os.close();
                        currentFilePath = fileObject.getName().getPath();
                        return getCommonResponse(request);
                    } else {
                        //TODO: Deal with errors
                    }
                }
            }
        }
        //TODO: Deal with permission error
        return getCommonResponse(request);
    }

    public CommandResponse actionDeleteFile(CommandRequest request) throws FileSystemException {
        if (isDeleteFileAllowed() || getUserStatus().isRootUser()) {
            FileObject currentFile = getCurrentFile();
            if (currentFile != null && currentFile.isWriteable()) {
                FileObject parentDir = currentFile.getParent();
                if (parentDir != null && parentDir.isWriteable()) {
                    if (currentFile.delete()) {
                        currentFilePath = null;
                    }
                }
            }
        }
        return getCommonResponse(request);
    }

    public CommandResponse actionDeleteFolder(CommandRequest request) throws FileSystemException {
        if (isDeleteFolderAllowed() || getUserStatus().isRootUser()) {
            if (!getCurrentPath().equals(getBaseDirPath())) {
                FileObject currentDir = getCurrentDir();
                if (currentDir != null && currentDir.isWriteable() && (currentDir.getChildren() == null || currentDir.getChildren().length == 0)) {
                    FileObject parentDir = currentDir.getParent();
                    if (parentDir != null && parentDir.isWriteable()) {
                        if (currentDir.delete()) {
                            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                        }
                    }
                }
            }
        }
        return getCommonResponse(request);
    }

    public CommandResponse getCommonResponse(CommandRequest request) {
        final Object currentPanelIfAny = request.getRequestObject().getAttribute(Parameters.RENDER_PANEL);
        return new ShowScreenResponse("/fckeditor/custom/FCKSelectImage.jsp") {
            public boolean execute(CommandRequest cmdReq) throws Exception {
                cmdReq.getRequestObject().setAttribute(Parameters.RENDER_PANEL, currentPanelIfAny);
                boolean b = super.execute(cmdReq);
                cmdReq.getRequestObject().removeAttribute(Parameters.RENDER_PANEL);
                return b;
            }
        };

    }

    public CommandResponse actionSetFilter(CommandRequest request) {
        setCurrentFilter(request.getRequestObject().getParameter("filter"));
        return getCommonResponse(request);
    }

    public void clear() {
        calculateStartingPath();
        currentPath = getStartingPath();
        openPaths.clear();
        openPaths.add(getStartingPath());
        currentFilePath = null;
        selectedFile = null;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String currentFilter) {
        this.currentFilter = currentFilter;
    }
}
