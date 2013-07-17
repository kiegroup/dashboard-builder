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
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class Envelope extends GraphicElement implements Comparable, Cloneable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Envelope.class.getName());

    public static final String DESCRIPTOR_FILENAME = "envelope.properties";
    public static final Properties DEFAULT_PROPERTIES;

    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.setProperty("resource.FULL_PAGE", "full.jsp");
        DEFAULT_PROPERTIES.setProperty("resource.SHARED_PAGE", "shared.jsp");
        DEFAULT_PROPERTIES.setProperty("resource.CSS", "styles.css");
    }

    public Envelope() {
        super();
    }

    public Envelope(String id, File zipFile) throws Exception {
        super(id, zipFile);
    }

    public String getCategoryName() {
        return "envelope";
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
        return UIServices.lookup().getEnvelopesManager().getElementsDir();
    }

    /**
     * Subdirectory inside getMappingDir() where this element stores files. Often composed of workspace name and id.
     *
     * @return
     */
    protected String getBaseDir() {
        String subDir = "workspaceEnvelopes/";
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
        return UIServices.lookup().getEnvelopesManager();
    }

    /**
     * Get an instance to a previewer for this item. This method MUST be implemented in subclasses of
     * GraphicElement, in order to make it available for the admin jsps.
     *
     * @return
     */
    public static GraphicElementPreview getPreviewInstance(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        return new EnvelopePreview(f, workspaceId, sectionId, panelId, id);
    }

    protected String getPrefixForResourcePath(String path) {
        Map fpMap = (Map) resources.get("FULL_PAGE");
        Map spMap = (Map) resources.get("SHARED_PAGE");
        if (fpMap != null && path.equals(fpMap.get(null)))
            return JSPS_PREFIX;
        if (spMap != null && path.equals(spMap.get(null)))
            return JSPS_PREFIX;
        return super.getPrefixForResourcePath(path);
    }

    protected String getSuffixForResourcePath(String path) {
        Map fpMap = (Map) resources.get("FULL_PAGE");
        Map spMap = (Map) resources.get("SHARED_PAGE");
        if (fpMap != null && path.equals(fpMap.get(null)))
            return JSPS_SUFFIX;
        if (spMap != null && path.equals(spMap.get(null)))
            return JSPS_SUFFIX;
        return super.getSuffixForResourcePath(path);
    }

    /**
     * @return a clone for this object.
     */
    public Object elementClone() {
        try {
            Envelope clone = new Envelope();
            clone.setDescription((Properties) description.clone());
            clone.setId(id);
            clone.setWorkspaceId(workspaceId);
            clone.setPanelId(panelId);
            clone.setSectionId(sectionId);
            clone.setZipFile(getZipFile());
            return clone;
        } catch (Exception e) {
            log.error("Error cloning Envelope:", e);
        }
        return null;
    }

}
