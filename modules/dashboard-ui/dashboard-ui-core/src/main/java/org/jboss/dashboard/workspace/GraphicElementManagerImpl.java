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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import javax.inject.Inject;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class GraphicElementManagerImpl implements GraphicElementManager, Startable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphicElementManagerImpl.class.getName());

    protected Class classToHandle;
    protected String classToHandleName;
    protected String descriptorFileName;
    protected Properties defaultElementProperties;

    protected String baseDir;
    protected String elementsDir;
    protected List elements;
    protected List baseElements;

    @Inject
    private ResourceMappings mappings;

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public void start() throws Exception {
        log.info("Starting " + getClass().getName());
        if (GraphicElement.class.isAssignableFrom(classToHandle)) {

            classToHandleName = classToHandle.getName();
            if (classToHandleName.indexOf('.') != -1) {
                classToHandleName = classToHandleName.substring(classToHandleName.lastIndexOf('.') + 1);
            }

            Field f = classToHandle.getDeclaredField("DESCRIPTOR_FILENAME");
            descriptorFileName = (String) f.get(null);
            f = classToHandle.getDeclaredField("DEFAULT_PROPERTIES");
            defaultElementProperties = (Properties) f.get(null);
        } else {
            throw new IllegalArgumentException(classToHandle + " is not subclass of " + GraphicElement.class);
        }

        elementsDir = mappings.getProperty(classToHandleName.toLowerCase());
        if (elementsDir == null) {
            throw new Exception("Property " + classToHandleName.toLowerCase() + " not found. Cannot load " + classToHandleName.toLowerCase() + "s.");
        }

        loadDbElements();
        deployBaseElements();
        if (elements != null) {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
                    GraphicElement graphicElement = (GraphicElement) iterator.next();
                    graphicElement.checkDeployment();
                    session.update(graphicElement);
                }
            }}.execute();
        }
    }

    /**
     * Take all elements in DB, and load them
     */
    protected void loadDbElements() throws Exception {
        elements = new Vector();

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                List dbItems = session.createQuery("from " + classToHandle.getName() + " as element").list();
                for (int i = 0; i < dbItems.size(); i++) {
                    GraphicElement element = (GraphicElement) dbItems.get(i);
                    //element.deploy();
                    log.debug("Loaded db item " + element.getId());
                    elements.add(element);
                }
                session.setFlushMode(oldFlushMode);
            }
        };

        txFragment.execute();
        Collections.sort(elements);
    }

    /**
     * Reads zipped elements in a directory and deploys them all.
     */
    protected void deployBaseElements() throws Exception {
        log.debug("Deploying elements in directory " + Application.lookup().getBaseAppDirectory() + File.separator + baseDir + ". Expecting type = " + classToHandleName);
        File dir = new File(Application.lookup().getBaseAppDirectory() + File.separator + baseDir);
        baseElements = new ArrayList();
        if (dir.exists()) {
            File[] elementZips = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".zip"));
                }
            });
            for (int i = 0; elementZips != null && i < elementZips.length; i++) {
                final File elementZip = elementZips[i];
                final String elementName = elementZip.getName().substring(0, elementZip.getName().length() - 4);
                baseElements.add(elementName);
                HibernateTxFragment fragment = new HibernateTxFragment(true) {
                    public void txFragment(Session session) throws Exception {
                        deployZippedElement(elementZip, elementName);//Global for all workspaces.
                    }
                };
                fragment.execute();
            }
        }
    }

    /**
     * Deploys an element (zip file). Deployment consists in creating or updating it in database
     */
    protected void deployZippedElement(File zipFile, String elementId) throws Exception {
        log.debug("Deploying " + classToHandleName + " " + zipFile);
        final GraphicElement existingElement = getElement(elementId, null, null, null);
        log.debug("Existing element with id " + elementId + " = " + existingElement);
        if (existingElement != null) {
            log.debug("Updating file " + zipFile + ". (Already deployed in db)");
            existingElement.setZipFile(zipFile);
            existingElement.setLastModified(new Date());
        } else {
            log.info("Deploying to database " + classToHandleName + " " + zipFile);
            Constructor c = classToHandle.getConstructor(new Class[]{String.class, File.class});
            final GraphicElement element = (GraphicElement) c.newInstance(new Object[]{elementId, zipFile});

            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    element.setLastModified(new Date());
                    session.save(element);
                }
            };

            txFragment.execute();
            elements.add(element);
            Collections.sort(elements);
        }
    }

    /**
     * Returns all elements installed in the system
     */
    public GraphicElement[] getElements() {
        //Collections.sort(elements);
        GraphicElement[] elementsArray = (GraphicElement[]) elements.toArray(new GraphicElement[elements.size()]);
        //Arrays.sort(elementsArray);
        return elementsArray;
    }

    /**
     * Return all elements belonging to given workspace
     *
     * @param workspaceId Set to null to indicate elements that apply to all workspaces.
     */
    public GraphicElement[] getElements(String workspaceId) {
        List l = new ArrayList();
        GraphicElement[] sortedElements = getElements();
        for (int i = 0; i < sortedElements.length; i++) {
            GraphicElement element = (GraphicElement) sortedElements[i];
            if (hasSameValue(element.getWorkspaceId(), workspaceId))
                l.add(element);
        }
        log.debug("Elements with workspace=" + workspaceId + ": " + l.size());
        return (GraphicElement[]) l.toArray(new GraphicElement[l.size()]);
    }

    /**
     * Return all elements belonging to given workspace and section
     *
     * @param workspaceId  Set to null to indicate elements that apply to all workspaces.
     * @param sectionId Set to null to indicate elements that apply to all sections.
     */
    public GraphicElement[] getElements(String workspaceId, Long sectionId) {
        List l = new ArrayList();
        GraphicElement[] sortedElements = getElements(workspaceId);
        for (int i = 0; i < sortedElements.length; i++) {
            GraphicElement element = (GraphicElement) sortedElements[i];
            if (hasSameValue(element.getSectionId(), sectionId))
                l.add(element);
        }
        log.debug("Elements with workspace=" + workspaceId + ", section=" + sectionId + ": " + l.size());
        return (GraphicElement[]) l.toArray(new GraphicElement[l.size()]);
    }

    /**
     * Return all elements belonging to given workspace and section
     *
     * @param workspaceId  Set to null to indicate elements that apply to all workspaces.
     * @param sectionId Set to null to indicate elements that apply to all sections.
     * @param panelId Set to null to indicate elements that apply to all panel instances.
     */
    public GraphicElement[] getElements(String workspaceId, Long sectionId, Long panelId) {
        List l = new ArrayList();
        GraphicElement[] sortedElements = getElements(workspaceId, sectionId);
        for (int i = 0; i < sortedElements.length; i++) {
            GraphicElement element = (GraphicElement) sortedElements[i];
            if (hasSameValue(element.getPanelId(), panelId))
                l.add(element);
        }
        log.debug("Elements with workspace=" + workspaceId + ", section=" + sectionId + ", panel=" + panelId + ": " + l.size());
        return (GraphicElement[]) l.toArray(new GraphicElement[l.size()]);
    }

    /**
     * Get the elements visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     * Assume current workspace, section and panel.
     */
    public GraphicElement[] getAvailableElements() {
        Workspace workspace = NavigationManager.lookup().getCurrentWorkspace();
        Section section = NavigationManager.lookup().getCurrentSection();
        RequestContext reqCtx = RequestContext.getCurrentContext();
        Long idPanel = null;
        Panel panel = (Panel) reqCtx.getRequest().getRequestObject().getAttribute(Parameters.RENDER_PANEL);
        if (panel != null && section != null) {
            idPanel = panel.getPanelId();
            if (getElementScopeDescriptor().isAllowedInstance()) {
                idPanel = panel.getInstanceId();
            }
        }
        return getAvailableElements(workspace == null ? null : workspace.getId(), section == null ? null : section.getId(), panel == null ? null : idPanel);
    }

    /**
     * Get the elements visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     */
    public GraphicElement[] getAvailableElements(String workspaceId, Long sectionId, Long panelId) {
        log.debug("Getting available elements in context (" + workspaceId + ", " + sectionId + ", " + panelId + ")");
        List elementsToReturn = new ArrayList();
        Set elementIds = new HashSet();
        GraphicElement[] panelElements = getElements(workspaceId, sectionId, panelId);
        for (int i = 0; i < panelElements.length; i++) {
            GraphicElement element = panelElements[i];
            elementsToReturn.add(element);
            elementIds.add(element.getId());
        }
        GraphicElement[] sectionElements = getElements(workspaceId, sectionId, null);
        for (int i = 0; i < sectionElements.length; i++) {
            GraphicElement element = sectionElements[i];
            if (elementIds.contains(element.getId()))
                continue;
            elementsToReturn.add(element);
            elementIds.add(element.getId());
        }
        GraphicElement[] instanceElements = getElements(workspaceId, null, panelId);
        for (int i = 0; i < instanceElements.length; i++) {
            GraphicElement element = instanceElements[i];
            if (elementIds.contains(element.getId()))
                continue;
            elementsToReturn.add(element);
            elementIds.add(element.getId());
        }
        GraphicElement[] workspaceElements = getElements(workspaceId, null, null);
        for (int i = 0; i < workspaceElements.length; i++) {
            GraphicElement element = workspaceElements[i];
            if (elementIds.contains(element.getId()))
                continue;
            elementsToReturn.add(element);
            elementIds.add(element.getId());
        }
        GraphicElement[] globalElements = getElements(null);
        for (int i = 0; i < globalElements.length; i++) {
            GraphicElement element = globalElements[i];
            if (elementIds.contains(element.getId()))
                continue;
            elementsToReturn.add(element);
            elementIds.add(element.getId());
        }

        GraphicElement[] elementsArray = (GraphicElement[]) elementsToReturn.toArray(new GraphicElement[]{});
        Arrays.sort(elementsArray);
        if (log.isDebugEnabled()) {
            ArrayList l = new ArrayList();
            for (int i = 0; i < elementsArray.length; i++) {
                GraphicElement element = elementsArray[i];
                l.add(element.getDbid());
            }
            log.debug("Got available elements in context (" + workspaceId + ", " + sectionId + ", " + panelId + "): " + l);
        }
        return elementsArray;
    }

    /**
     * Get the elements manageable for a given context. These are the base elements plus the workspace, section and panel
     * elements. Assume current workspace, section and panel.
     */
    public GraphicElement[] getManageableElements() {
        Workspace workspace = NavigationManager.lookup().getCurrentWorkspace();
        Section section = NavigationManager.lookup().getCurrentSection();
        Object panelObject = SessionManager.getCurrentPanel();//TODO : Current panel won't be set!
        Panel panel = null;
        if (panelObject != null && panelObject instanceof Panel)
            panel = (Panel) panelObject;
        return getManageableElements(workspace == null ? null : workspace.getId(), section == null ? null : section.getId(), panel == null ? null : panel.getPanelId());
    }

    /**
     * Get the elements visible for a given context.
     * These are the base elements plus the workspace, section and panel elements.
     */
    public GraphicElement[] getManageableElements(String workspaceId, Long sectionId, Long panelId) {
        List elementsToReturn = new ArrayList();

        if (workspaceId == null && sectionId == null & panelId == null) {
            elementsToReturn.addAll(Arrays.asList(getElements()));//All elements in system
        } else if (workspaceId != null && sectionId == null & panelId == null) {
            GraphicElement[] workspaceGlobalElements = getElements(workspaceId, null, null);
            Set usedIds = new HashSet();
            //Workspace global elements
            for (int i = 0; i < workspaceGlobalElements.length; i++) {
                GraphicElement element = workspaceGlobalElements[i];
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            //Global elements with different id
            GraphicElement[] globalElements = getElements(null);
            for (int i = 0; i < globalElements.length; i++) {
                GraphicElement element = globalElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                elementsToReturn.add(element);
            }
            //Workspace non-global elements
            GraphicElement[] workspaceElements = getElements(workspaceId);
            for (int i = 0; i < workspaceElements.length; i++) {
                GraphicElement element = workspaceElements[i];
                if (element.getSectionId() == null && element.getPanelId() == null)
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
        } else if (workspaceId != null && sectionId != null & panelId == null) {
            //All elements defined for this section, plus workspace base elements, plus base elements with different id.
            Set usedIds = new HashSet();
            GraphicElement[] sectionElements = getElements(workspaceId, sectionId);
            for (int i = 0; i < sectionElements.length; i++) {
                GraphicElement element = sectionElements[i];
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] workspaceElements = getElements(workspaceId, null, null);
            for (int i = 0; i < workspaceElements.length; i++) {
                GraphicElement element = workspaceElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] globalElements = getElements(null);
            for (int i = 0; i < globalElements.length; i++) {
                GraphicElement element = globalElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
        } else if (workspaceId != null && sectionId != null & panelId != null) {
            //All elements defined for this panel, plus workspace base elements, plus base elements with different id.
            Set usedIds = new HashSet();

            GraphicElement[] panelElements = getElements(workspaceId, sectionId, panelId);
            for (int i = 0; i < panelElements.length; i++) {
                GraphicElement element = panelElements[i];
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] sectionElements = getElements(workspaceId, sectionId, null);
            for (int i = 0; i < sectionElements.length; i++) {
                GraphicElement element = sectionElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] instanceElements = getElements(workspaceId, null, panelId);
            for (int i = 0; i < instanceElements.length; i++) {
                GraphicElement element = instanceElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] workspaceElements = getElements(workspaceId, null, null);
            for (int i = 0; i < workspaceElements.length; i++) {
                GraphicElement element = workspaceElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
            GraphicElement[] globalElements = getElements(null);
            for (int i = 0; i < globalElements.length; i++) {
                GraphicElement element = globalElements[i];
                if (usedIds.contains(element.getId()))
                    continue;
                usedIds.add(element.getId());
                elementsToReturn.add(element);
            }
        }

        GraphicElement[] elementsArray = (GraphicElement[]) elementsToReturn.toArray(new GraphicElement[]{});
        Arrays.sort(elementsArray);
        return elementsArray;
    }

    /**
     * Get an element visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     * Assume current workspace, section and panel.
     */
    public GraphicElement getAvailableElement(String id) {
        GraphicElement[] elements = getAvailableElements();
        for (int i = 0; i < elements.length; i++) {
            GraphicElement element = elements[i];
            if (element.getId().equals(id))
                return element;
        }
        return null;
    }

    /**
     * Get an element by dbid.
     */
    public GraphicElement getElementByDbid(String dbid) {
        for (int i = 0; i < elements.size(); i++) {
            GraphicElement element = (GraphicElement) elements.get(i);
            if (element.getDbid().toString().equals(dbid))
                return element;
        }
        return null;
    }

    /**
     * Returns an element by its id and context.
     */
    public GraphicElement getElement(String id, String workspaceId, Long sectionId, Long panelId) {
        GraphicElement[] contextElements = getElements(workspaceId, sectionId, panelId);
        for (int i = 0; i < contextElements.length; i++) {
            GraphicElement element = contextElements[i];
            if (element.getId().equals(id))
                return element;
        }
        return null;
    }

    /**
     * Returns the default element, that is, the one whose id is 'default' and workspace is null.
     */
    public GraphicElement getDefaultElement() {
        return getElement(getDefaultResourceId(), null, null, null);
    }

    protected String getDefaultResourceId() {
        return "Default_" + classToHandleName;
    }

    /**
     * Determines if given element is a base element. These elements are special,
     * as they hold a main copy on HD, and are copied to DB on startup. That's why they can't be edited
     * in admin pages, because the HD copy is intended not to be modified in any way.
     */
    public boolean isBaseElement(GraphicElement element) {
        if (!classToHandle.isAssignableFrom(element.getClass()))
            throw new IllegalArgumentException("Class not compatible with " + classToHandle + ": " + element.getClass());
        return element.getWorkspaceId() == null && element.getSectionId() == null && element.getPanelId() == null && baseElements != null && baseElements.contains(element.getId());
    }

    /**
     * @return the base directory for elements, once deployed, starting in /www/htdocs/.
     */
    public String getElementsDir() {
        return elementsDir;
    }

    /**
     * Create or update existing element
     */
    public void createOrUpdate(GraphicElement element) throws Exception {
        if (!classToHandle.isAssignableFrom(element.getClass()))
            throw new IllegalArgumentException("Class not compatible with " + classToHandle + ": " + element.getClass());

        final GraphicElement elementToUse;
        GraphicElement existingElement = getElement(element.getId(), element.getWorkspaceId(), element.getSectionId(), element.getPanelId());
        if (existingElement != null) {
            elementToUse = existingElement;
            if (isBaseElement(existingElement)) {
                log.error("Cannot replace data for a base element.");
                return;
            }
            elementToUse.setZipFile(element.getZipFile());
        } else {
            elementToUse = element;
            elements.add(element);
        }

        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            elementToUse.setLastModified(new Date());
            session.saveOrUpdate(elementToUse);
        }}.execute();
        Collections.sort(elements);
    }

    /**
     * Delete existing element
     */
    public void delete(final GraphicElement element) {
        if (!classToHandle.isAssignableFrom(element.getClass()))
            throw new IllegalArgumentException("Class not compatible with " + classToHandle + ": " + element.getClass());
        log.debug("Deleting element with dbid = " + element.getDbid());
        elements.remove(element);
        Collections.sort(elements);
        element.clearDeploymentFiles();

        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.delete(element);
            }}.execute();
        } catch (Exception e) {
            log.error("Error deleting element.", e);
        }
    }

    private boolean hasSameValue(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        else if (o1 == null)
            return false;
        else if (o2 == null)
            return false;
        else
            return o1.equals(o2);
    }

    public synchronized void addFileToResource(final String name, final File file, final GraphicElement element, final String descriptorFileName) throws Exception {
        byte[] zipFile = element.getZipFile();
        byte[] buffer = new byte[65536];

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile));
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (entryName.equals(descriptorFileName)) {
                Properties props = new Properties();
                props.load(zis);
                props.setProperty("resource." + name, name);
                zos.putNextEntry(new ZipEntry(entryName));
                props.store(zos, "");
                zos.closeEntry();
            } else if (entryName.equals(name)) {
                throw new Exception("Duplicated entry name: " + name);
            } else {
                zos.putNextEntry(new ZipEntry(entryName));
                int len = 0;
                while ((len = zis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        }

        InputStream in = new BufferedInputStream(new FileInputStream(file));
        zos.putNextEntry(new ZipEntry(name));
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            zos.write(buffer, 0, len);
        }
        zos.closeEntry();
        zos.close();
        element.setZipFile(bos.toByteArray());
        createOrUpdate(element);
    }
}
