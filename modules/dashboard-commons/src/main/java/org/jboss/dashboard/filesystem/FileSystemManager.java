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

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.jboss.dashboard.annotation.config.Config;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
public class FileSystemManager {

    @Inject
    private Instance<FileSystemMapping> mappings;

    @Inject @Config("default.gif")
    private String defaultFileImage;

    @Inject @Config("fileSystem/fileTypes/16x16")
    private String thumbnailFolder;

    @Inject @Config("fileSystem/fileTypes/32x32")
    private String largeIconFolder;

    @Inject @Config("avi=avi.gif," +
            "mpg=avi.gif," +
            "mpeg=avi.gif," +
            "divx=avi.gif," +
            "mp4=avi.gif," +
            "wmv=avi.gif," +
            "asf=avi.gif," +
            "cer=cer.gif," +
            "pfx=cer.gif," +
            "doc=doc.gif," +
            "exe=exe.gif," +
            "com=exe.gif," +
            "gif=gif.gif," +
            "htm=html.gif," +
            "html=html.gif," +
            "jpg=img.gif," +
            "lib=lib.gif," +
            "so=lib.gif," +
            "dll=lib.gif," +
            "o=lib.gif," +
            "mp3=mp3.gif," +
            "wma=mp3.gif," +
            "wav=mp3.gif," +
            "ogg=mp3.gif," +
            "pdf=pdf.gif," +
            "pps=pps.gif," +
            "ppt=ppt.gif," +
            "properties=properties.gif," +
            "swf=swf.gif," +
            "flash=swf.gif," +
            "tiff=tiff.gif," +
            "txt=txt.gif," +
            "xls=xls.gif," +
            "xml=xml.gif," +
            "zip=zip.gif," +
            "rar=zip.gif," +
            "arj=zip.gif," +
            "ace=zip.gif," +
            "tgz=zip.gif," +
            "bz2=zip.gif," +
            "gz=zip.gif," +
            "png=png.gif")
    private Properties fileNamesImagesMappings;

    private transient FileSystem fileSystem;

    @PostConstruct
    public void start() throws Exception {
        fileSystem = createInitialVirtualFilesystem();

        for (FileSystemMapping mapping : mappings) {
            mapping.addToVirtualFilesystem(fileSystem);
        }
    }

    public String getThumbnailFolder() {
        return thumbnailFolder;
    }

    public void setThumbnailFolder(String thumbnailFolder) {
        this.thumbnailFolder = thumbnailFolder;
    }

    public String getLargeIconFolder() {
        return largeIconFolder;
    }

    public void setLargeIconFolder(String largeIconFolder) {
        this.largeIconFolder = largeIconFolder;
    }

    public String getDefaultFileImage() {
        return defaultFileImage;
    }

    public void setDefaultFileImage(String defaultFileImage) {
        this.defaultFileImage = defaultFileImage;
    }

    public Properties getFileNamesImagesMappings() {
        return fileNamesImagesMappings;
    }

    public void setFileNamesImagesMappings(Properties fileNamesImagesMappings) {
        this.fileNamesImagesMappings = fileNamesImagesMappings;
    }

    protected FileSystem createInitialVirtualFilesystem() throws FileSystemException {
        return VfsWrapper.getManager().createVirtualFileSystem((String) null).getFileSystem();
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * If given FileObject can be accessed through an URI, return it.
     *
     * @param file File to get uri from.
     * @return a URI to given file.
     */
    public String getURI(FileObject file) {
        for (FileSystemMapping mapping : mappings) {
            String uri = mapping.getURI(file);
            if (uri != null) return uri;
        }
        return null;
    }

    public String getFileNamePicture(String fileExtension) {
        return getFileNamesImagesMappings().getProperty(fileExtension.toLowerCase(), getDefaultFileImage());
    }

    public String getThumbnailPath(String fileExtension) {
        return getThumbnailFolder() + "/" + getFileNamePicture(fileExtension);
    }

    public String getLargeIconPath(String fileExtension) {
        return getLargeIconFolder() + "/" + getFileNamePicture(fileExtension);
    }
}


