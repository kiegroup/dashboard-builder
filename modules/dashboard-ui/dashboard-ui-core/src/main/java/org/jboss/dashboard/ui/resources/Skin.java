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

import java.io.File;
import java.util.Properties;

/**
 *
 */
public class Skin extends GraphicElement implements Comparable, Cloneable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Skin.class.getName());

    public static final String DESCRIPTOR_FILENAME = "skin.properties";
    public static final Properties DEFAULT_PROPERTIES;

    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.setProperty("resource.BULLET", "images/spacer.png");
        DEFAULT_PROPERTIES.setProperty("resource.CLOSE", "images/close.gif");
        DEFAULT_PROPERTIES.setProperty("resource.DOWN", "images/down.gif");
        DEFAULT_PROPERTIES.setProperty("resource.EDIT_MODE", "images/edit_mode.gif");
        DEFAULT_PROPERTIES.setProperty("resource.HEADER_BG", "images/header_bg.gif");
        DEFAULT_PROPERTIES.setProperty("resource.HEADER_LEFT", "images/header_left.gif");
        DEFAULT_PROPERTIES.setProperty("resource.HEADER_RIGHT", "images/header_right.gif");
        DEFAULT_PROPERTIES.setProperty("resource.LEFT", "images/left.gif");
        DEFAULT_PROPERTIES.setProperty("resource.MAXIMIZE", "images/maximice.gif");
        DEFAULT_PROPERTIES.setProperty("resource.MINIMIZE", "images/minimice.gif");
        DEFAULT_PROPERTIES.setProperty("resource.PROPERTIES", "images/properties.gif");
        DEFAULT_PROPERTIES.setProperty("resource.REFRESH", "images/refresh.gif");
        DEFAULT_PROPERTIES.setProperty("resource.RESTORE", "images/restore.gif");
        DEFAULT_PROPERTIES.setProperty("resource.RIGHT", "images/right.gif");
        DEFAULT_PROPERTIES.setProperty("resource.SHOW", "images/show.gif");
        DEFAULT_PROPERTIES.setProperty("resource.UP", "images/up.gif");
        DEFAULT_PROPERTIES.setProperty("resource.RESOURCES_MODE", "images/resources_mode.gif");
        DEFAULT_PROPERTIES.setProperty("resource.CSS", "styles/default.css");
    }

    public Skin() {
        super();
    }

    public Skin(String id, File zipFile) throws Exception {
        super(id, zipFile);
    }

    public String getCategoryName() {
        return "skin";
    }

    /**
     * Return the base directory for this skin, after the base skins directory. Examples:
     * <li>default
     * <li>workspaceSkins/workspace0/windows_xp
     * <li>windows_xp
     *
     * @return the base directory for this skin, after the base skins directory
     */
    public String getBaseDir() {
        String subDir = "workspaceSkins/";
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
        return UIServices.lookup().getSkinsManager();
    }

    /**
     * Get an instance to a previewer for this item. This method MUST be implemented in subclasses of
     * GraphicElement, in order to make it available for the admin jsps.
     *
     * @return
     */
    public static GraphicElementPreview getPreviewInstance(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        return new SkinPreview(f, workspaceId, sectionId, panelId, id);
    }

    /**
     * Name of the file that contains the descriptor (i.e. skin.properties...)
     *
     * @return
     */
    protected String getDescriptorFileName() {
        return DESCRIPTOR_FILENAME;
    }

    public String getMappingDir() {
        return UIServices.lookup().getSkinsManager().getElementsDir();
    }

    /**
     * @return a clone for this object.
     */
    public Object elementClone() {
        try {
            Skin clone = new Skin();
            clone.setDescription((Properties) description.clone());
            clone.setId(id);
            clone.setWorkspaceId(workspaceId);
            clone.setZipFile(getZipFile());
            clone.setPanelId(panelId);
            clone.setSectionId(sectionId);
            return clone;
        } catch (Exception e) {
            log.error("Error cloning skin:", e);
        }
        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" Skin with dbid ");
        sb.append(getDbid());
        sb.append(" and resources: ");
        sb.append(resources);
        return sb.toString();
    }
}
