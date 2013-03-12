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
import org.apache.commons.vfs.FileType;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileNavigationTreeFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FileNavigationTreeFormatter.class.getName());

    private FileNavigationHandler fileNavigationHandler;

    public FileNavigationHandler getFileNavigationHandler() {
        return fileNavigationHandler;
    }

    public void setFileNavigationHandler(FileNavigationHandler fileNavigationHandler) {
        this.fileNavigationHandler = fileNavigationHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            FileObject currentDir = getFileNavigationHandler().getRootDirectory();
            renderDirectory(currentDir);
        } catch (FileSystemException e) {
            log.error("Error: ", e);
        }
    }

    protected void renderDirectory(FileObject currentDir) throws FileSystemException {
        String dirName = currentDir.getName().getBaseName();
        setAttribute("dirName", StringUtils.defaultIfEmpty(dirName,"/"));
        setAttribute("path", currentDir.getName().getPath());
        FileObject[] children = currentDir.getChildren();
        List childDirectories = new ArrayList();
        boolean hasChildDirectories = false;
        if (children != null)
            for (int i = 0; i < children.length; i++) {
                FileObject child = children[i];
                if (child.getType().equals(FileType.FOLDER) && !child.isHidden() && child.isReadable()) {
                    hasChildDirectories = true;
                    childDirectories.add(child);
                }
            }
        setAttribute("hasChildrenDirectories", hasChildDirectories);
        String directoryPath = currentDir.getName().getPath();
        boolean isOpen = getFileNavigationHandler().getOpenPaths().contains(directoryPath);
        setAttribute("isOpen", isOpen);
        setAttribute("isCurrent", getFileNavigationHandler().getCurrentPath().equals(directoryPath));
        renderFragment("outputDirectory");
        if (childDirectories.size() > 0) {
            sortDirectories(childDirectories);
            if (isOpen) {
                renderFragment("beforeChildren");
                for (int i = 0; i < childDirectories.size(); i++) {
                    FileObject child = (FileObject) childDirectories.get(i);
                    renderDirectory(child);
                }
                renderFragment("afterChildren");
            }
        }
    }

    protected void sortDirectories(List childDirectories) {
        final Collator collator = Collator.getInstance(getLocale());
        Collections.sort(childDirectories, new Comparator() {
            public int compare(Object o1, Object o2) {
                return collator.compare(((FileObject) o1).getName().getBaseName(), ((FileObject) o2).getName().getBaseName());
            }
        });
    }
}
