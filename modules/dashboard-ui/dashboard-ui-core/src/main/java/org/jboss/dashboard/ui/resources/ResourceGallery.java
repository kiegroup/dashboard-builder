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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.jboss.dashboard.workspace.GraphicElementManager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class ResourceGallery extends GraphicElement implements Comparable, Cloneable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceGallery.class.getName());

    public static final String DESCRIPTOR_FILENAME = "gallery.properties";
    public static final Properties DEFAULT_PROPERTIES = new Properties();

    public ResourceGallery() {
        super();
    }

    public ResourceGallery(String id, File zipFile) throws Exception {
        super(id, zipFile);
    }

    public String getCategoryName() {
        return "resourceGallery";
    }

    /**
     * Name of the file that contains the descriptor (i.e. skin.properties...)
     *
     * @return
     */
    protected String getDescriptorFileName() {
        return DESCRIPTOR_FILENAME;
    }

    /**
     * Directory where the elements of this type deployment files should be placed (starting in /www/htdocs/
     * also serves as starting point for urls.
     *
     * @return
     */
    protected String getMappingDir() {
        return UIServices.lookup().getResourceGalleryManager().getElementsDir();
    }

    /**
     * Subdirectory inside getMappingDir() where this element stores files. Often composed of workspace name and id.
     *
     * @return
     */
    protected String getBaseDir() {
        String subDir = "workspaceGalleries/";
        StringBuffer sb = new StringBuffer();
        if (workspaceId != null) {
            sb.append(subDir);
            sb.append(workspaceId);
            sb.append("/");
            if (sectionId != null) {
                sb.append(sectionId);
                sb.append("/");
                if (panelId != null) {
                    sb.append(panelId);
                    sb.append("/");
                }
            }
        }
        sb.append(id);
        return sb.toString();
    }

    /**
     * Manager that handles this type of resource.
     *
     * @return
     */
    public GraphicElementManager getInstanceManager() {
        return getManager();
    }

    /**
     * The manager that handles this type of resource. This method MUST be implemented in subclasses of
     * GraphicElement, in order to make it available for the admin jsps.
     *
     * @return
     */
    public static GraphicElementManager getManager() {
        return UIServices.lookup().getResourceGalleryManager();
    }

    /**
     * Get an instance to a previewer for this item. This method MUST be implemented in subclasses of
     * GraphicElement, in order to make it available for the admin jsps.
     *
     * @return
     */
    public static GraphicElementPreview getPreviewInstance(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        return new ResourceGalleryPreview(f, workspaceId, sectionId, panelId, id);
    }

    /**
     * @return a clone for this object.
     */
    public Object elementClone() {
        try {
            ResourceGallery clone = new ResourceGallery();
            clone.setDescription((Properties) description.clone());
            clone.setId(id);
            clone.setWorkspaceId(workspaceId);
            clone.setZipFile(getZipFile());
            clone.setPanelId(panelId);
            clone.setSectionId(sectionId);
            return clone;
        } catch (Exception e) {
            log.error("Error cloning ResourceGallery:", e);
        }
        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" ResourceGallery with dbid ");
        sb.append(getDbid());
        sb.append(" and resources: ");
        sb.append(resources);
        return sb.toString();
    }

    public void setZipFile(byte[] zfile) throws Exception {
        boolean performReindexing = !"general".equals(getId()) && !"icons".equals(getId()) && this.getTmpZipFile() != null;
        if (!performReindexing) {
            super.setZipFile(zfile);
        } else {
            Map resourcesBackup = new HashMap();
            for (Iterator it = resources.keySet().iterator(); it.hasNext();) {
                String resourceName = (String) it.next();
                HashMap resourceValue = (HashMap) resources.get(resourceName);
                resourcesBackup.put(resourceName, resourceValue.clone());
            }
            super.setZipFile(zfile);            
        }
    }


    public boolean onDelete(Session s) throws CallbackException {
         return super.onDelete(s);
    }

    public boolean onSave(Session s) throws CallbackException {

        return super.onSave(s);
    }

}

