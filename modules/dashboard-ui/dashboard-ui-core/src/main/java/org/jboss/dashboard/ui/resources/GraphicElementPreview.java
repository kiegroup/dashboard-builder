/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class GraphicElementPreview implements Serializable, ResourceHolder {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphicElementPreview.class.getName());

    public static final int STATUS_OK = 0;
    public static final int STATUS_ZIP_CORRUPT = 1;
    public static final int STATUS_MISSING_DESCRIPTOR = 2;
    public static final int STATUS_DESCRIPTOR_CORRUPT = 3;

    protected byte[] zipData;
    protected String workspaceId;
    protected Long sectionId;
    protected Long panelId;
    protected String id;
    protected int status;
    protected Properties description;
    protected Properties resources;
    protected Map resourcesDeployed;

    protected abstract String getDescriptorFilename();

    protected abstract GraphicElement makeNewElement();

    public GraphicElementPreview(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        this.sectionId = sectionId;
        this.panelId = panelId;
        this.workspaceId = workspaceId;
        this.id = id;
        status = STATUS_OK;
        try {
            zipData = toByteArray(new FileInputStream(f));
            initDataStructures(f);
        } catch (IOException e) {
            log.warn("Error processing zip file. ", e);
            status = STATUS_ZIP_CORRUPT;
        }
    }

    protected void initDataStructures(File f) throws IOException {
        ZipFile zfile = new ZipFile(f);
        ZipEntry descriptorEntry = zfile.getEntry(getDescriptorFilename());
        if (descriptorEntry == null) {
            status = STATUS_MISSING_DESCRIPTOR;
            return;
        }
        Properties prop = new Properties();
        try {
            prop.load(zfile.getInputStream(descriptorEntry));
        } catch (IOException ioe) {
            log.warn("Error processing descriptor file. ", ioe);
            status = STATUS_DESCRIPTOR_CORRUPT;
            return;
        }
        description = new Properties();
        resources = new Properties();
        Enumeration properties = prop.propertyNames();
        while (properties.hasMoreElements()) {
            String propName = (String) properties.nextElement();
            if (propName.startsWith("name.")) {
                String lang = propName.substring(propName.lastIndexOf(".") + 1);
                setDescription(prop.getProperty(propName), lang);
                log.debug("Element preview name (" + lang + "): " + prop.getProperty(propName));
            } else if (propName.startsWith("resource.")) {
                String resourceName = propName.substring("resource.".length());
                String resourcePath = prop.getProperty(propName);
                resources.setProperty(resourceName, resourcePath);
            } else {
                log.warn("Unknown property in element " + propName);
            }
        }
        log.debug("Resources inside zip = " + resources);
        resourcesDeployed = new HashMap();
        for (Enumeration en = resources.propertyNames(); en.hasMoreElements();) {
            String resName = (String) en.nextElement();
            String resPath = resources.getProperty(resName);
            log.debug("Deploying property " + resName + "=" + resPath);
            ZipEntry resourceEntry = zfile.getEntry(resPath);
            if (resourceEntry != null) {
                resourcesDeployed.put(resName, toByteArray(zfile.getInputStream(resourceEntry)));
            }
        }
    }

    protected void setDescription(String value, String lang) {
        description.setProperty(lang, value);
    }


    protected byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream os = new BufferedOutputStream(bos);
        InputStream bis = new BufferedInputStream(is);
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = bis.read(b)) != -1) {
            os.write(b, 0, len);
        }
        os.close();
        bis.close();
        return bos.toByteArray();
    }

    /**
     * Retrieve resource by resource identifier. Preview ignores language settings, as they are intended to show all
     * resources.
     *
     * @param resName Resource whose name is to be retrieved.
     * @return
     */
    public Resource getResource(ResourceName resName, String lang) throws Exception {
        String resourceId = resName.getResourceId();
        byte[] data = (byte[]) resourcesDeployed.get(resourceId);
        String dataName = resources.getProperty(resourceId);
        if (data == null || dataName == null)
            return null;
        /*if(dataName.lastIndexOf('/')!=-1)
            dataName = dataName.substring(dataName.lastIndexOf('/')+1);*/
        return ByteArrayResource.getInstance(resName, data, new File(dataName).getName());
    }

    /**
     * Get the resource names inside this preview.
     *
     * @return
     */
    public Set getResources() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    /**
     * Checks resource deployment status, and deploys if needed
     */
    public void checkDeployment() {
    }

    public int getStatus() {
        return status;
    }

    public int getZipSize() {
        return zipData.length;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public String getId() {
        return id;
    }

    public Map getDescription() {
        return description;
    }

    /**
     * Convert this preview into a skin.
     *
     * @return
     */
    public GraphicElement toElement() {
        try {
            GraphicElement element = makeNewElement();
            element.setZipFile(zipData);
            element.setWorkspaceId(workspaceId);
            element.setSectionId(sectionId);
            element.setPanelId(panelId);
            element.setDescription(description);
            element.setId(id);
            return element;
        } catch (Exception e) {
            log.error("Error:", e);
        }
        return null;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ZIP data: ");
        sb.append(zipData.length);
        sb.append("bytes.\n");
        sb.append("WorkspaceId: ");
        sb.append(workspaceId);
        sb.append("\nStatus: ");
        sb.append(status);
        sb.append("\nDescription: ");
        sb.append(description);
        sb.append("\nResources: ");
        sb.append(resources);
        sb.append("\nResourcesDeployed:");
        sb.append(resourcesDeployed);
        return sb.toString();
    }


}
