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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 * This class represents a graphic element, that is a Skin, an Envelope, or a Layout
 */
public abstract class GraphicElement implements Cloneable, Serializable, ResourceHolder, Lifecycle, Visitable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphicElement.class.getName());

    protected Long dbid;
    protected String id;
    protected Properties description = new Properties();
    protected HashMap resources;
    private File tmpZipFile;
    protected String workspaceId;
    protected Long sectionId;
    protected Long panelId;
    private Date lastModified;

    public static final String DESCRIPTOR_FILENAME = "element.properties";
    public static final Properties DEFAULT_PROPERTIES;

    protected static final String JSPS_PREFIX =
            "<%@ include file=\"/common/global.jsp\" %>" +
                    "<%@ taglib uri=\"http://dashboard.jboss.org/taglibs/i18n-1.0\" prefix=\"i18n\" %>" +
                    "<%@ taglib uri=\"resources.tld\" prefix=\"resource\" %>" +
                    "<%@ taglib uri=\"mvc_taglib.tld\" prefix=\"mvc\" %>" +
                    "<%@ taglib uri=\"factory.tld\" prefix=\"factory\" %>" +
                    "<%@ taglib uri=\"bui_taglib.tld\" prefix=\"panel\" %>";
    protected static final String JSPS_SUFFIX = "";

    static {
        DEFAULT_PROPERTIES = new Properties();
    }

    protected LocaleManager getLocaleManager() {
        return LocaleManager.lookup();
    }

    /**
     * Name of the file that contains the descriptor (i.e. skin.properties...)
     *
     * @return the file name for the descriptor inside the zip file
     */
    protected abstract String getDescriptorFileName();

    /**
     * Directory where the elements of this type deployment files should be placed (starting in the webapp root)
     * also serves as starting point for urls.
     *
     * @return the mapping directory for the deployment of this graphic resource.
     */
    protected abstract String getMappingDir();

    /**
     * Subdirectory inside getMappingDir() where this element stores files. Often composed of workspace name and id.
     *
     * @return Subdirectory inside getMappingDir() where this element stores files.
     */
    protected abstract String getBaseDir();

    /**
     * Manager that handles this type of resource.
     *
     * @return the manager to handler this type of resources
     */
    public abstract GraphicElementManager getInstanceManager();

    /**
     * Clone this object.
     *
     * @return a clone for this object
     */
    protected abstract Object elementClone();

    protected File getTmpZipFile() {
        return tmpZipFile;
    }

    /**
     * Empty constructor required by Hibernate
     */
    public GraphicElement() {
    }

    /**
     * Constructs a graphic resource with given id and zip file.
     *
     * @param id      Identifier for the resource.
     * @param zipFile zipped resource contents.
     * @throws Exception
     */
    public GraphicElement(String id, File zipFile) throws Exception {
        this.id = id;
        setZipFile(zipFile);
    }

    /**
     * Database identifier
     *
     * @return Database identifier
     */
    public Long getDbid() {
        return dbid;
    }

    /**
     * Database identifier
     *
     * @param dbid Database identifier
     */
    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    /**
     * Identifier
     *
     * @return Identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Identifier
     *
     * @param id Identifier
     */
    public void setId(String id) {
        this.id = id;
    }


    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Element i18n description
     *
     * @return Element i18n description
     */
    public Map getDescription() {
        return description;
    }

    /**
     * Element i18n description
     *
     * @param description Element i18n description
     */
    protected void setDescription(Properties description) {
        this.description = description;
    }

    /**
     * Element i18n description
     *
     * @param value
     * @param lang
     */
    protected void setDescription(String value, String lang) {
        description.put(lang, value);
    }

    /**
     * Returns a set of resource identifiers stored in this element
     *
     * @return a set of resource identifiers stored in this element
     */
    public Set getResources() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    /**
     * Retrieve a resource by name in a given lang
     *
     * @param resourceName Resource name
     * @param lang         language for the resource
     * @return existing resource, or null if there isn't
     */
    public Resource getResource(ResourceName resourceName, String lang) {
        String resourceId = resourceName.getResourceId();
        if (resourceId == null) { //Retrieve the whole zip file !!!!
            return ByteArrayResource.getInstance(resourceName, getZipFile(), getId() + ".zip");
        } else {
            Map resMap = (Map) resources.get(resourceId);
            if (resMap == null) {
                log.debug("Cannot find resource " + resourceId + " for " + getClass());
                return null;
            }
            String res = (String) resMap.get(lang);
            if (res == null)
                res = (String) resMap.get(null);//Try with no-language resource.
            if (res == null)
                res = (String) resMap.get(getLocaleManager().getDefaultLang());//Try with default lang

            res = getBaseDir() + "/" + res;
            RequestContext reqCtx = RequestContext.lookup();
            if (reqCtx != null) {
                //Resources are best retrieved through URL (the fastest way)
                log.debug("Resource relative name = " + res);
                String categoryMapping = getMappingDir();
                log.debug("Category where the resource belongs to is mapped to uri " + categoryMapping);
                String url = categoryMapping + "/" + res;
                log.debug("Returning UrlResource to " + url);
                return UrlResource.getInstance(resourceName, reqCtx.getRequest().getRequestObject().getContextPath(), url);
            } else {
                //Create a FileResource...
                try {
                    checkDeployment();
                    return FileResource.getInstance(resourceName, new File(Application.lookup().getBaseAppDirectory() + getMappingDir() + "/" + res));
                } catch (Exception e) {
                    log.error("Error:", e);
                    return null;
                }
            }
        }
    }

    /**
     * Given a resource id in this resource container, return a name based on the resource id.
     *
     * @param id Resource id, something like "CLOSE", "ICO_SMALL" ...
     * @return A resource name for this item.
     */
    public ResourceName getResourceName(String id) {
        return ResourceName.getInstance(ResourceName.getName(workspaceId, sectionId, panelId, getCategoryName(), this.id, id));
    }

    /**
     * Given a resource id in this resource container, return a name based on the resource id.
     *
     * @param id Resource id, something like "CLOSE", "ICO_SMALL" ...
     * @return A resource name for this item.
     */
    public ResourceName getRelativeResourceName(String id) {
        return ResourceName.getInstance(ResourceName.getName(null, null, null, getCategoryName(), this.id, id));
    }

    /**
     * Get the category name representing this type of element
     *
     * @return the category name representing this type of element
     */
    public abstract String getCategoryName();

    /**
     * Zipped file content
     *
     * @return Zipped file content
     */
    public byte[] getZipFile() {
        InputStream is = null;
        try {
            if (tmpZipFile != null) {
                is = new BufferedInputStream(new FileInputStream(tmpZipFile));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int bytesIn;
                byte[] readBuffer = new byte[2048];
                while ((bytesIn = is.read(readBuffer)) != -1) {
                    bos.write(readBuffer, 0, bytesIn);
                }
                is.close();
                return bos.toByteArray();
            }
        } catch (FileNotFoundException e) {
            log.error("Error: ", e);
        } catch (IOException e) {
            log.error("Error: ", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    /**
     * Zipped file content
     *
     * @param zipFile Zipped file content
     * @throws Exception
     */
    public void setZipFile(byte[] zipFile) throws Exception {
        //Else don't touch, as it is being set by hibernate...
        if (tmpZipFile != null) tmpZipFile.delete();

        tmpZipFile = File.createTempFile("graphicElement", ".tmp");
        tmpZipFile.deleteOnExit();
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(tmpZipFile));
            ByteArrayInputStream is = new ByteArrayInputStream(zipFile);
            int bytesIn;
            byte[] readBuffer = new byte[2048];
            while ((bytesIn = is.read(readBuffer)) != -1) {
                os.write(readBuffer, 0, bytesIn);
            }
        } finally {
            if (os != null) os.close();
        }
        deploy();
    }

    /**
     * Zipped file
     *
     * @param f Zipped file
     * @throws Exception
     */
    public void setZipFile(File f) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream os = new BufferedOutputStream(bos);
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(f));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } finally {
            os.close();
            if (is != null) is.close();
        }
        setZipFile(bos.toByteArray());
    }

    /**
     * Workspace identifier this resource belongs to
     *
     * @return Workspace identifier this resource belongs to
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * Workspace identifier this resource belongs to
     *
     * @param workspaceId Workspace identifier this resource belongs to
     */
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    /**
     * Section identifier this resource belongs to
     *
     * @return Section identifier this resource belongs to
     */
    public Long getSectionId() {
        return sectionId;
    }

    /**
     * Section identifier this resource belongs to
     *
     * @param sectionId Section identifier this resource belongs to
     */
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    /**
     * Panel identifier this resource belongs to
     *
     * @return Panel identifier this resource belongs to
     */
    public Long getPanelId() {
        return panelId;
    }

    /**
     * Panel identifier this resource belongs to
     *
     * @param panelId Panel identifier this resource belongs to
     */
    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    /**
     * Process the element descriptor inside the zip file, and set properties for given graphic element.
     *
     * @throws java.io.IOException
     */
    protected void deploy() throws Exception {
        Properties prop = new Properties();
        resources = new HashMap();
        InputStream in = new BufferedInputStream(new FileInputStream(tmpZipFile));
        ZipInputStream zin = null;
        try {
            zin = new ZipInputStream(in);
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (getDescriptorFileName().equals(e.getName())) {
                    prop.load(zin);
                }
            }
        } finally {
            if (zin != null) zin.close();
        }
        if (prop.isEmpty()) {
            log.error("No properties inside descriptor " + getDescriptorFileName() + " for item with id " + id);
        }
        Enumeration properties = prop.propertyNames();
        String[] languages = getLocaleManager().getAllLanguages();
        while (properties.hasMoreElements()) {
            String propName = (String) properties.nextElement();
            if (propName.startsWith("name.")) {
                String lang = propName.substring(propName.lastIndexOf(".") + 1);
                setDescription(prop.getProperty(propName), lang);
                log.debug("Look-and-feel name (" + lang + "): " + prop.getProperty(propName));
            } else if (propName.startsWith("resource.")) {
                String resourceName = propName.substring("resource.".length());
                String langId = null;
                for (int i = 0; i < languages.length; i++) {
                    String lngId = languages[i];
                    if (resourceName.startsWith(lngId + ".")) {
                        langId = lngId;
                        resourceName = resourceName.substring(langId.length() + 1);
                        break;
                    }
                }
                String resourcePath = prop.getProperty(propName);
                Map existingResource = (Map) resources.get(resourceName);
                if (existingResource == null)
                    resources.put(resourceName, existingResource = new HashMap());
                existingResource.put(langId, resourcePath);
            } else {
                log.error("Unknown property in descriptor " + propName);
            }
        }
        log.debug("Loaded Graphic element description" + getDescription());
        log.debug("Loaded Graphic element resources: " + resources);
    }


    /**
     * Deploy to a given directory. Extract all files in associated zip to given directory plus getBaseDir()
     *
     * @throws java.io.IOException
     */
    protected void deployFiles() throws Exception {
        //setLastModified(new Date());
        log.info("Deploying files for " + this.getClass().getName() + " " + getId());
        String destinationDir = getDeploymentDirName();
        File destDirFile = new File(destinationDir);
        destDirFile.mkdirs();
        if (!destDirFile.setLastModified(System.currentTimeMillis())) {
            log.error("Failed to set directory last modified, it might not be supported by the filesystem. " +
                    "This will cause a noticeable performance degradation. Consider using a better operating system.");
        }
        if (getLastModified() == null || destDirFile.lastModified() <= getLastModified().getTime()) {
            setLastModified(new Date(destDirFile.lastModified() - 1));
        }
        InputStream in = new BufferedInputStream(new FileInputStream(tmpZipFile));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {
            String destName = destinationDir + "/" + e.getName();
            if (e.isDirectory()) {
                log.debug("Creating dir " + destName);
                new File(destName).mkdirs();
            } else {
                log.debug("Creating file " + destName);
                unzip(zin, destName, e.getName());
            }
        }
        zin.close();
    }

    /**
     * Unzip given stream to destination file.
     *
     * @param zin Stream to unzip.
     * @param s   File name for destination.
     * @throws java.io.IOException in case there are errors in the stream.
     */
    protected void unzip(ZipInputStream zin, String s, String resourcePath) throws IOException {
        File f = new File(s);
        File parent = new File(f.getParent());
        parent.mkdirs();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(s);
            out.write(getPrefixForResourcePath(resourcePath).getBytes());
            byte[] b = new byte[1024];
            int len;
            while ((len = zin.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.write(getSuffixForResourcePath(resourcePath).getBytes());
        } finally {
            if (out != null) out.close();
        }
    }

    /**
     * Determine a prefix String for resource with given path
     *
     * @param resourcePath
     * @return a String to prepend to given resource path, never null
     */
    protected String getPrefixForResourcePath(String resourcePath) {
        return "";
    }

    /**
     * Determine a prefix String for resource with given path
     *
     * @param resourcePath
     * @return a String to append to given resource path, never null
     */
    protected String getSuffixForResourcePath(String resourcePath) {
        return "";
    }

    /**
     * Physical directory name where this resource is to be deployed.
     *
     * @return Physical directory name where this resource is to be deployed.
     */
    protected String getDeploymentDirName() {
        return Application.lookup().getBaseAppDirectory() + getMappingDir() + "/" + getBaseDir();
    }

    /**
     * Determine if this element has been deployed, and if not, deploys to the default deployment directory.
     */
    public void checkDeployment() {
        try {
            File dir = new File(getDeploymentDirName());
            if (!dir.exists()) {
                deployFiles();
            } else {
                long lastModifiedMillis = Long.MAX_VALUE;
                if (getLastModified() != null) {
                    lastModifiedMillis = getLastModified().getTime();
                }
                if (dir.lastModified() <= lastModifiedMillis) {
                    deployFiles();
                }
            }
        } catch (Exception e) {
            log.error("Error deploying element:", e);
        }
    }

    /**
     * Clear the files inside the deployment dir. Will be regenerated from zip in db, if the resource is used.
     */
    public void clearDeploymentFiles() {
        String destinationDir = getDeploymentDirName();
        log.debug("Deleting all files inside " + destinationDir);
        recursiveDelete(new File(destinationDir));
    }

    private void recursiveDelete(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    recursiveDelete(file);
                }
            }
            if (!f.delete())
                f.deleteOnExit();
        } else if (f.isFile()) {
            if (!f.delete())
                f.deleteOnExit();
        }
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;

        GraphicElement oElm = (GraphicElement) o;
        if (getDbid() == null || oElm.getDbid() == null) return false;
        return getDbid().equals(oElm.getDbid());
    }

    /**
     * Compare with another element.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this Object.
     */
    public int compareTo(Object o) {
        GraphicElement element = (GraphicElement) o;

        if (getInstanceManager().isBaseElement(this) && !getInstanceManager().isBaseElement(element))
            return Integer.MIN_VALUE;
        if (!getInstanceManager().isBaseElement(this) && getInstanceManager().isBaseElement(element))
            return Integer.MAX_VALUE;

        if (this.getWorkspaceId() != null && element.getWorkspaceId() != null) {
            if (this.getWorkspaceId().equals(element.getWorkspaceId())) {
                if (this.getSectionId() != null && element.getSectionId() == null) {
                    return Integer.MIN_VALUE;
                } else if (this.getSectionId() == null && element.getSectionId() != null) {
                    return Integer.MAX_VALUE;
                } else if (this.getSectionId() == null && element.getSectionId() == null) {
                    return this.getId().compareTo(element.getId());
                } else if (this.getSectionId().equals(element.getSectionId())) {
                    if (this.getPanelId() != null && element.getPanelId() == null) {
                        return Integer.MIN_VALUE;
                    }
                    if (this.getPanelId() == null && element.getPanelId() != null) {
                        return Integer.MAX_VALUE;
                    }
                    if (this.getPanelId() == null && element.getPanelId() == null) {
                        return this.getId().compareTo(element.getId());
                    } else if (this.getPanelId().equals(element.getPanelId())) {
                        return this.getId().compareTo(element.getId());
                    } else
                        return this.getPanelId().compareTo(element.getPanelId());
                } else
                    return this.getSectionId().compareTo(element.getSectionId());
            } else {
                return this.getWorkspaceId().compareTo(element.getWorkspaceId());
            }
        } else if (this.getWorkspaceId() == null && element.getWorkspaceId() != null) {
            return Integer.MIN_VALUE;
        } else if (this.getWorkspaceId() != null && element.getWorkspaceId() == null) {
            return Integer.MAX_VALUE;
        } else { //All workspace Id's are null
            return this.getId().compareTo(element.getId());
        }
    }

    //Lyfecycle callbacks.
    public boolean onSave(Session s) throws CallbackException {
        return NO_VETO;
    }

    public boolean onUpdate(Session s) throws CallbackException {
        return NO_VETO;
    }

    public boolean onDelete(Session s) throws CallbackException {
        return NO_VETO;
    }

    public void onLoad(Session s, Serializable id) {
    }


    public Object clone() {
        return elementClone();
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitGraphicElement(this);
        return visitor.endVisit();
    }
}
