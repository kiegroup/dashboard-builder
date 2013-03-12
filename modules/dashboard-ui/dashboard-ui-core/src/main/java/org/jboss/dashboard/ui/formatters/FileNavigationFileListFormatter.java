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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class FileNavigationFileListFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FileNavigationFileListFormatter.class.getName());

    private FileNavigationHandler fileNavigationHandler;

    public FileNavigationHandler getFileNavigationHandler() {
        return fileNavigationHandler;
    }

    public void setFileNavigationHandler(FileNavigationHandler fileNavigationHandler) {
        this.fileNavigationHandler = fileNavigationHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        try {
            FileObject currentPath = getFileNavigationHandler().getCurrentDir();
            if (currentPath != null) {
                FileObject[] children = currentPath.getChildren();
                List sortedChildren = new ArrayList();
                for (int i = 0; i < children.length; i++) {
                    FileObject child = children[i];
                    if (child.getType().equals(FileType.FILE) && child.isReadable() && !child.isHidden()) {
                        if (matchesPattern(child, getFileNavigationHandler().getCurrentFilter()))
                            sortedChildren.add(child);
                    }
                }
                sortFiles(sortedChildren);
                renderFiles(sortedChildren);
            }
        } catch (FileSystemException e) {
            log.error("Error: ", e);
        }
    }

    protected boolean matchesPattern(FileObject child, String currentFilter) {
        if (!StringUtils.isBlank(currentFilter)) {
            String search = currentFilter;
            search = StringUtils.replace(search, "?", ".");
            search = StringUtils.replace(search, "*", ".*");
            try {
                return child.getName().getBaseName().matches(search);
            }
            catch (PatternSyntaxException pse) {
                return false;
            }
        }
        return true;
    }

    protected void renderFiles(List sortedChildren) throws FileSystemException {
        if (!sortedChildren.isEmpty()) {
            renderFragment("outputStart");
            for (int i = 0; i < sortedChildren.size(); i++) {
                FileObject fileObject = (FileObject) sortedChildren.get(i);
                FileContent content = fileObject.getContent();
                setAttribute("fileObject", fileObject);
                setAttribute("name", fileObject.getName().getBaseName());
                setAttribute("fileSize", content.getSize());
                String path = fileObject.getName().getPath();
                setAttribute("path", path);
                setAttribute("current", path.equals(getFileNavigationHandler().getCurrentFilePath()));
                String contentType = content.getContentInfo().getContentType();
                String extension = fileObject.getName().getExtension();
                if (contentType == null) {
                    contentType = extension;
                }
                setAttribute("contentType", contentType);
                setAttribute("imageURL", getFileNavigationHandler().getFilesystemManager().getThumbnailPath(extension));
                renderFragment("output");
            }
            renderFragment("outputEnd");
        }

    }

    protected void sortFiles(List files) {
        final Collator collator = Collator.getInstance(getLocale());
        Collections.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                return collator.compare(((FileObject) o1).getName().getBaseName(), ((FileObject) o2).getName().getBaseName());
            }
        });
    }
}
