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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.workspace.ResourceGalleryManager;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor;
import org.jboss.dashboard.ui.resources.ResourceGallery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.Properties;

@ApplicationScoped
public class ResourceGalleryManagerImpl extends GraphicElementManagerImpl implements ResourceGalleryManager {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceGalleryManagerImpl.class.getName());

    @Inject @Config("WEB-INF/etc/resourceGallery")
    private String galleriesDir;

    public void start() throws Exception {
        super.classToHandle = ResourceGallery.class;
        super.baseDir = galleriesDir;
        super.start();
    }

    /**
     * Determine the scope for the element handled by this manager.
     */
    public GraphicElementScopeDescriptor getElementScopeDescriptor() {
        return GraphicElementScopeDescriptor.SECTION_SCOPED;
    }

    public GraphicElement getDefaultElement() {
        GraphicElement element = super.getDefaultElement();
        if (element == null) {
            try {
                element = createDefaultGallery();
            } catch (Exception e) {
                log.error("Error: ", e);
            }
        }
        return element;
    }

    protected GraphicElement createDefaultGallery() throws Exception {
        ResourceGallery gallery = new ResourceGallery();
        gallery.setId("default");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        ZipEntry entry= new ZipEntry(ResourceGallery.DESCRIPTOR_FILENAME);
        zos.putNextEntry(entry);
        Properties prop = new Properties();
        //TODO: Make it external
        prop.setProperty("name.es","Im\u00e1genes subidas");
        prop.setProperty("name.en","Uploaded images");
        prop.setProperty("name.ca","Imatges pujades");
        prop.store(zos,"");
        zos.closeEntry();
        zos.close();
        gallery.setZipFile(bos.toByteArray());
        this.createOrUpdate(gallery);
        return gallery;
    }

}
