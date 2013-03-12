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
package org.jboss.dashboard.filesystem;

import org.jboss.dashboard.Application;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.jboss.dashboard.annotation.config.Config;

import javax.inject.Inject;
import java.io.File;

public class BinariesMapping implements FileSystemMapping {

    @Inject @Config("file")
    private String schema;

    @Inject @Config("binaries")
    private String junctionPoint;

    @Inject @Config("../../../../binaries")
    private String junctionUrl;

    @Inject @Config("")
    private String serverUri;

    @Inject @Config("true")
    private boolean isWebappDirectoryRelative;

    public boolean isWebappDirectoryRelative() {
        return isWebappDirectoryRelative;
    }

    public void setWebappDirectoryRelative(boolean webappDirectoryRelative) {
        isWebappDirectoryRelative = webappDirectoryRelative;
    }

    public String getJunctionPoint() {
        return junctionPoint;
    }

    public void setJunctionPoint(String junctionPoint) {
        this.junctionPoint = junctionPoint;
    }

    public String getJunctionUrl() {
        return junctionUrl;
    }

    public void setJunctionUrl(String junctionUrl) {
        this.junctionUrl = junctionUrl;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public void addToVirtualFilesystem(FileSystem fileSystem) throws FileSystemException {
        if (!isWebappDirectoryRelative()) {
            FileObject file = VfsWrapper.getManager().resolveFile(getSchema() + "://" + StringUtils.defaultString(getJunctionUrl()));
            file.createFolder(); //ensure it exists
            fileSystem.addJunction(getJunctionPoint(), file);
        } else {
            File parentFile = new File(Application.lookup().getBaseAppDirectory());
            String junctionUrl = getJunctionUrl();

            while (junctionUrl.startsWith("../")) {
                junctionUrl = junctionUrl.substring(3);
                parentFile = parentFile.getParentFile();
            }

            FileObject file = VfsWrapper.getManager().resolveFile(getSchema() + "://" + parentFile +
                    (StringUtils.isEmpty(junctionUrl) ? "" : ("/" + junctionUrl)));
            file.createFolder(); //ensure it exists
            fileSystem.addJunction(getJunctionPoint(), file);

        }
    }

    /**
     * If this file can be accessed through an URI, return it.
     */
    public String getURI(FileObject file) {
        if (this.getServerUri() != null) {
            String fileName = file.getName().getPath();
            if (fileName.startsWith("/" + getJunctionPoint())) {
                fileName = fileName.substring(getJunctionPoint().length() + 1);
                String uri = getServerUri() + fileName;
                return uri;
            }
        }
        return null;
    }

}
