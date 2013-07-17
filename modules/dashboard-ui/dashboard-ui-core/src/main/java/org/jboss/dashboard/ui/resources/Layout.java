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
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.jboss.dashboard.workspace.LayoutRegion;
import org.jboss.dashboard.workspace.LayoutsManager;
import org.jboss.dashboard.workspace.LayoutRegion;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 */
public class Layout extends GraphicElement implements Comparable, Cloneable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Layout.class.getName());

    public static final String DESCRIPTOR_FILENAME = "layout.properties";
    public static final Properties DEFAULT_PROPERTIES;

    /**
     * List containing all this template's regions.
     */
    private List regions;

    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.setProperty("resource.JSP", "layout.jsp");
        DEFAULT_PROPERTIES.setProperty("resource.XML", "layout.xml");
        DEFAULT_PROPERTIES.setProperty("resource.IMG", "layout.jpg");
    }

    public Layout() {
        super();
    }

    public Layout(String id, File zipFile) throws Exception {
        super(id, zipFile);
    }

    public String getCategoryName() {
        return "layout";
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
        return UIServices.lookup().getLayoutsManager().getElementsDir();
    }

    /**
     * Subdirectory inside getMappingDir() where this element stores files. Often composed of workspace name and id.
     *
     * @return
     */
    protected String getBaseDir() {
        String subDir = "workspaceLayouts/";
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
        return UIServices.lookup().getLayoutsManager();
    }

    /**
     * Get an instance to a previewer for this item. This method MUST be implemented in subclasses of
     * GraphicElement, in order to make it available for the admin jsps.
     *
     * @return
     */
    public static GraphicElementPreview getPreviewInstance(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        return new LayoutPreview(f, workspaceId, sectionId, panelId, id);
    }

    /**
     * Compare layouts.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this Object.
     */
    public int compareTo(Object o) {
        Layout oLayout = (Layout) o;
        LayoutsManager layoutsManager = UIServices.lookup().getLayoutsManager();
        if (layoutsManager.isBaseElement(this) && !layoutsManager.isBaseElement(oLayout))
            return Integer.MIN_VALUE;
        if (!layoutsManager.isBaseElement(this) && layoutsManager.isBaseElement(oLayout))
            return Integer.MAX_VALUE;

        if (this.getWorkspaceId() != null && oLayout.getWorkspaceId() != null) {
            if (this.getWorkspaceId().equals(oLayout.getWorkspaceId())) {
                return this.getId().compareTo(oLayout.getId());
            } else {
                return this.getWorkspaceId().compareTo(oLayout.getWorkspaceId());
            }
        } else if (this.getWorkspaceId() == null && oLayout.getWorkspaceId() != null) {
            return Integer.MIN_VALUE;
        } else if (this.getWorkspaceId() != null && oLayout.getWorkspaceId() == null) {
            return Integer.MAX_VALUE;
        } else { //All workspace Id's are null
            return this.getId().compareTo(oLayout.getId());
        }
    }

    /**
     * @return a clone for this object.
     */
    public Object elementClone() {
        try {
            Layout clone = new Layout();
            clone.setDescription((Properties) description.clone());
            clone.setId(id);
            clone.setWorkspaceId(workspaceId);
            clone.setPanelId(panelId);
            clone.setSectionId(sectionId);
            clone.setZipFile(getZipFile());
            return clone;
        } catch (Exception e) {
            log.error("Error cloning Layout:", e);
        }
        return null;
    }


    protected void deploy() throws Exception {
        super.deploy();
        InputStream in = new BufferedInputStream(new FileInputStream(getTmpZipFile()));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        Map descriptorPathMap = (Map) resources.get("XML");
        if (descriptorPathMap == null)
            throw new IllegalArgumentException("No valid xml descriptor in layout " + getId());
        String descriptorPath = (String) descriptorPathMap.get(null);
        if (descriptorPath == null)
            throw new IllegalArgumentException("No valid xml descriptor in layout " + getId());

        while ((e = zin.getNextEntry()) != null) {
            if (e.getName().equals(descriptorPath)) {
                doc = builder.build(zin);
                break;
            }
        }
        zin.close();
        if (doc == null)
            throw new IllegalArgumentException("No valid xml descriptor in layout " + getId());
        Element root = doc.getRootElement();

        if (root.getName().equalsIgnoreCase("layout")) {
            List xmlRegions = loadRegions(root);
            regions = new ArrayList();
            for (int i = 0; i < xmlRegions.size(); i++)
                addRegion((LayoutRegion) xmlRegions.get(i));
        } else {
            throw new IOException("Invalid layout format in xml descriptor");
        }
    }

    /**
     * Deploy to a given directory. Extract all files in associated zip to given directory plus getBaseDir()
     * Backwards compatibility requires that extracted jsps for old layouts are stored under previous directory,
     * because they may use relative paths.
     *
     * @throws Exception
     */
    protected void deployFiles() throws Exception {
        log.debug("Deploying layout files. Will have to move the jsp if it is old format.");
        super.deployFiles();
        Map jspNameMap = (Map) resources.get("JSP");
        if (jspNameMap == null) {
            log.error("Layout has no JSP!!");
            return;
        }
        String jspName = (String) jspNameMap.get(null);
        if (jspName == null) {
            log.error("Layout has no JSP!!");
            return;
        }
        if (workspaceId == null && jspName.startsWith("../")) {//Means it has old format.
            log.debug("JSP resource is " + jspName + " -> old format.");
            File jspFile = new File(getDeploymentDirName() + "/" + jspName.substring(3));
            File jspDestination = new File(new File(getDeploymentDirName()).getParent() + jspName.substring(jspName.lastIndexOf('/')));
            log.debug("Moving file " + jspFile + " to " + jspDestination + ", for backwards compatibility.");
            if (!jspFile.renameTo(jspDestination))
                log.warn("Cannot move old format layout jsp file from " + jspFile + " to " + jspDestination);
        }
    }

    protected String getPrefixForResourcePath(String path) {
        Map fpMap = (Map) resources.get("JSP");
        if (fpMap != null && path.equals(fpMap.get(null)))
            return JSPS_PREFIX;
        return super.getPrefixForResourcePath(path);
    }

    protected String getSuffixForResourcePath(String path) {
        Map fpMap = (Map) resources.get("JSP");
        if (fpMap != null && path.equals(fpMap.get(null)))
            return JSPS_SUFFIX;
        return super.getSuffixForResourcePath(path);
    }

    public String getJSPName() {
        Map jspMap = (Map) resources.get("JSP");
        String name = (String) jspMap.get(null);
        return name.substring(0, name.length() - 4);
    }

    /**
     *
     */
    private List loadRegions(Element root) {
        List results = new ArrayList();
        List regions = root.getChildren("region");
        for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            String type = element.getChildTextTrim("type");
            String idRegion = element.getAttributeValue("id");

            // Build new region
            LayoutRegion layoutRegion = new LayoutRegion();
            layoutRegion.setId(idRegion);
            if ("column".equalsIgnoreCase(type))
                layoutRegion.setType(LayoutRegion.COLUMN);
            else if ("tabs".equalsIgnoreCase(type))
                layoutRegion.setType(LayoutRegion.TABBED);
            else if ("row".equalsIgnoreCase(type))
                layoutRegion.setType(LayoutRegion.ROW);

            List params = element.getChildren("param");
            for (Iterator iteratorParams = params.iterator(); iteratorParams.hasNext();) {
                Element elementParams = (Element) iteratorParams.next();
                String idParam = elementParams.getAttributeValue("id");
                String value = elementParams.getAttributeValue("value");
                layoutRegion.getRenderAttributes().put(idParam, value);
            }
            results.add(layoutRegion);
        }
        log.debug("Regions loaded from xml: " + results);
        return results;
    }


    /**
     * Adds a region to this template
     */
    protected void addRegion(LayoutRegion region) {
        region.setLayout(this);
        if (regions == null)
            regions = new ArrayList();
        regions.add(region);
    }

    /**
     * Returns a region by identifier
     */
    public LayoutRegion getRegion(String id) {
        if (id == null)
            return null;
        Iterator it = regions.iterator();
        while (it.hasNext()) {
            LayoutRegion r = (LayoutRegion) it.next();
            if (r.getId() != null && r.getId().equals(id))
                return r;
        }
        return null;
    }

    /**
     * Returns all regions
     */
    public LayoutRegion[] getRegions() {
        return (LayoutRegion[]) regions.toArray(new LayoutRegion[regions.size()]);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" Layout with dbid ");
        sb.append(getDbid());
        sb.append(" and regions: ");
        sb.append(regions);
        return sb.toString();
    }
}
