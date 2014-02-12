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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.ui.resources.Layout;
import org.jboss.dashboard.ui.resources.Skin;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.security.Policy;
import org.hibernate.Session;

import javax.servlet.http.HttpSession;
import java.security.Permission;
import java.security.Principal;
import java.util.*;

/**
 * Section belonging to a workspace
 */
public class Section implements Comparable, Visitable {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Section.class.getName());

    /**
     * Section identifier inside workspace
     */
    private Long id = null;

    /**
     * Database id: Unique
     */
    private Long dbid = null;

    /**
     * Title
     */
    private Map title = null;

    /**
     * Layout describing this section's layout
     *
     * @link dependency
     */
    private String layoutId = null;


    /**
     * Id of this section's skin
     *
     * @link dependency
     */
    private String skinId = null;

    /**
     * Id of this section's envelope
     *
     * @link dependency
     */
    private String envelopeId = null;

    /**
     * Workspace this section belongs to
     */
    private WorkspaceImpl workspace = null;

    /**
     * Panels stored by their ID
     */
    private Set<Panel> panels = new HashSet();

    /**
     * Position of the section inside workspace
     */
    private int position = -1;

    /**
     * Values used to render section
     */
    private Boolean visible = Boolean.TRUE;

    /**
     * Cell spacing between regions of the section
     */
    private Integer regionsCellSpacing = new Integer(2);

    /**
     * Cell spacing between panels inside a region
     */
    private Integer panelsCellSpacing = new Integer(2);

    /**
     * Parent section
     */
    private Long parentId;

    /**
     * URL that gives direct access to the section if a friendly format
     */
    protected String friendlyUrl;

    /**
     * Default constructor
     */
    public Section() {
    }

    /**
     * Returns a constant value.
     * <p/>
     * <p>VERY IMPORTANT NOTE.
     * Regarding the message from <b>jiesheng zhang</b> posted on Fri, 01 Aug 2003 03:06:26 -0700 at
     * <i>hibernate-devel MAIL ARCHIVE</i>: "In hibernate current implementation, if a object is retrieved
     * from Set and its hashCode is changed, there is no way to remove it from set."
     * <p>You can read the original message at:
     * <code>http://www.mail-archive.com/hibernate-devel@lists.sourceforge.net/msg00008.html.</code>
     */
    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (dbid == null || obj == null || !(obj instanceof Section)) return false;

        Section other = (Section) obj;
        return dbid.equals(other.getDbid());
    }

    public List getPathNumber() {
        ArrayList l = new ArrayList();
        Section parent = getParent();
        if (parent != null) {
            l.addAll(getParent().getPathNumber());
        }
        l.add(new Integer(position));
        return l;
    }

    public int getDepthLevel() {
        // Section path number cache, to avoid lots of getParent() in the same request...
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx == null || ctx.getRequest() == null) {
            return getPathNumber().size() - 1;
        }
        Map sectionsCache = (Map) ctx.getRequest().getRequestObject().getAttribute("sectionsPathNumberCache");
        if (sectionsCache == null)
            ctx.getRequest().getRequestObject().setAttribute("sectionsPathNumberCache", sectionsCache = new HashMap());

        List myPathNumber = (List) sectionsCache.get(this.getDbid());
        if (myPathNumber == null) {
            myPathNumber = getPathNumber();
            sectionsCache.put(this.getDbid(), myPathNumber);
        }
        return myPathNumber.size() - 1;
    }


    protected static int comparePathNumbers(List l1, List l2) {
        for (int i = 0; i < l1.size(); i++) {
            Integer position1 = (Integer) l1.get(i);
            if (l2.size() > i) {
                Integer position2 = (Integer) l2.get(i);
                int difference = position1.intValue() - position2.intValue();
                if (difference != 0)
                    return difference;
            } else {
                return 1;
            }
        }
        if (l2.size() > l1.size()) {
            return -1;
        }
        return 0;
    }

    private void clearSectionsCache() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx != null && ctx.getRequest() != null) {
            ctx.getRequest().getRequestObject().removeAttribute("sectionsPathNumberCache");
        }
    }

    public int compareTo(Object obj) {
        if (this == obj) return 0;
        Section section = (Section) obj;
        // Section path number cache, to avoid lots of getParent() in the same request...
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx == null || ctx.getRequest() == null) {
            return comparePathNumbers(getPathNumber(), section.getPathNumber());
        }
        Map sectionsCache = (Map) ctx.getRequest().getRequestObject().getAttribute("sectionsPathNumberCache");
        if (sectionsCache == null)
            ctx.getRequest().getRequestObject().setAttribute("sectionsPathNumberCache", sectionsCache = new HashMap());

        List myPathNumber = (List) sectionsCache.get(this.getDbid());
        if (myPathNumber == null) {
            myPathNumber = getPathNumber();
            sectionsCache.put(this.getDbid(), myPathNumber);
        }
        List otherPathNumber = (List) sectionsCache.get(section.getDbid());
        if (otherPathNumber == null) {
            otherPathNumber = section.getPathNumber();
            sectionsCache.put(section.getDbid(), otherPathNumber);
        }
        return comparePathNumbers(myPathNumber, otherPathNumber);
    }

    public boolean isNew() {
        return dbid == null;
    }

    /**
     * Initializes this section
     */
    public void init() {
    }

    public Long getParentSectionId() {
        return parentId;
    }

    public void setParentSectionId(Long id) {
        parentId = id;
    }

    /**
     * Returns the layout this section has, or null if not found
     */
    public Layout getLayout() {
        String lId = layoutId;
        lId = (lId == null) ? UIServices.lookup().getLayoutsManager().getDefaultElement().getId() : lId;
        Layout layoutToReturn = null;
        if (getWorkspace() != null)
            layoutToReturn = (Layout) UIServices.lookup().getLayoutsManager().getElement(lId, getWorkspace().getId(), getId(), null);
        if (getWorkspace() != null && layoutToReturn == null) {  //Try with a workspace layout
            layoutToReturn = (Layout) UIServices.lookup().getLayoutsManager().getElement(lId, getWorkspace().getId(), null, null);
        }
        if (layoutToReturn == null) {  //Try with a global layout
            layoutToReturn = (Layout) UIServices.lookup().getLayoutsManager().getElement(lId, null, null, null);
        }
        if (layoutToReturn == null) {  //Use default layout
            layoutToReturn = (Layout) UIServices.lookup().getLayoutsManager().getDefaultElement();
        }
        return layoutToReturn;
    }

    /**
     * Changes the template assigned to this section.
     */
    public void setLayout(Layout layout) {
        if (layout != null && !layout.getId().equals(this.layoutId)) {
            log.debug("Changing layout");

            // Unassign all panels
            Panel[] panels = getAllPanels();

            // Sort them, so they will keep same position inside new region.
            Arrays.sort(panels);
            for (int i = 0; i < panels.length; i++) {
                Panel panel = panels[i];
                String regionId = panel.getLayoutRegionId();
                // Remove panel from region
                if (regionId != null) {
                    SectionRegion sr = getSectionRegion(regionId);
                    sr.removePanel(panel);
                }

                // Add panel to equivalent region (if exists)
                LayoutRegion equivalentRegion = layout.getRegion(regionId);
                if (equivalentRegion != null) assignPanel(panel, equivalentRegion);
            }
            this.layoutId = layout.getId();
        }
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public Map getTitle() {
        return title;
    }

    public void setTitle(Map title) {
        this.title = title;
    }

    public void setTitle(String title, String lang) {
        if (lang == null || lang.trim().length() == 0)
            lang = LocaleManager.lookup().getDefaultLang();
        if (this.title == null)
            this.title = new HashMap();

        this.title.put(lang, title);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSectionId() {
        return id;
    }

    public void setSectionId(Long id) {
        this.id = id;
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
        if (id == null)
            id = dbid;
    }

    public WorkspaceImpl getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceImpl workspace) {
        this.workspace = workspace;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        if (visible == null) {
            this.visible = Boolean.FALSE;
        } else {
            this.visible = visible;
        }
    }

    public Integer getRegionsCellSpacing() {
        return regionsCellSpacing;
    }

    public void setRegionsCellSpacing(Integer regionsCellSpacing) {
        this.regionsCellSpacing = regionsCellSpacing;
    }

    public Integer getPanelsCellSpacing() {
        return panelsCellSpacing;
    }

    public void setPanelsCellSpacing(Integer panelsCellSpacing) {
        this.panelsCellSpacing = panelsCellSpacing;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        clearSectionsCache();
    }

    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    public void setFriendlyUrl(String friendlyUrl) {
        this.friendlyUrl = friendlyUrl;
    }

    public Section getParent() {
        if (parentId != null && getWorkspace() != null)
            return getWorkspace().getSection(parentId);
        return null;
    }

    public void setParent(Section parent) {
        if (parent != null) {
            parentId = parent.getId();
        } else
            parentId = null;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public List getChildren() {
        List children = new ArrayList();
        Section[] allSections = getWorkspace().getAllUnsortedSections();
        for (int i = 0; i < allSections.length; i++) {
            Section section = allSections[i];
            if (this.getId().equals(section.getParentSectionId())) {
                children.add(section);
            }
        }
        Collections.sort(children);
        return children;
    }

    public Section getRoot() {
        Section root = getParent();
        if (root != null)
            return root.getRoot();
        else
            return this;
    }

    public List getHierarchy() {
        List hierarchy = new ArrayList();
        Section parent = getParent();
        while (parent != null) {
            hierarchy.add(parent);
            parent = parent.getParent();
        }

        List result = new ArrayList();
        for (int i = hierarchy.size() - 1; i >= 0; i--)
            result.add(hierarchy.get(i));
        return result;
    }

    public boolean isAncestor(Section section) {
        if (id == null || section == null || section.getParent() == null) return false;
        if (id.equals(section.getParent().getId())) return true;
        return isAncestor(section.getParent());
    }

    // Panels management methods

    /**
     * Adds a panel to a given region
     */
    public void assignPanel(Panel panel, LayoutRegion region) {
        if (region != null) assignPanel(panel, region.getId());
    }

    public void assignPanel(Panel panel, String layoutRegionId) {
        if (panel == null || panel.getInstance() == null) return;

        // Remove from its current region (if needed)
        if (panel.getRegion() != null) {
            SectionRegion sr = getSectionRegion(panel.getRegion());
            sr.removePanel(panel);
        }

        // Register panel
        panel.setSection(this);
        panels.add(panel);

        // Add it to region
        if (layoutRegionId != null) {
            SectionRegion sr = getSectionRegion(layoutRegionId);
            sr.addPanel(panel);
        }
    }

    /**
     * Removes a panel from this section from database
     */
    public void removePanel(final Panel panel) {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                if (panel != null) {
                    // Notify porlet removal before deleting it.
                    panel.getProvider().getDriver().fireBeforePanelRemoved(panel);
                    panel.panelRemoved();

                    // Remove panel from region. Necessary to calculate panels positions
                    String regionId = panel.getLayoutRegionId();
                    if (regionId != null) {
                        SectionRegion sr = getSectionRegion(regionId);
                        sr.removePanel(panel);
                    }

                    // Remove panel from section
                    panels.remove(panel);
                }
            }
        };

        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Panel " + panel.getPanelId() + " can't be removed.", e);
        }
    }

    /**
     * Removes a panel from this section from memory
     *
     * @deprecated Use removePanel instead.
     */
    public void deletePanel(Panel panel) {
        removePanel(panel);
    }

    /**
     * Returns a panel by identifier
     */
    public Panel getPanel(String id) {
        if (id == null) return null;
        Iterator it = panels.iterator();
        while (it.hasNext()) {
            Panel panel = (Panel) it.next();
            if (panel.getPanelId().toString().equals(id)) {
                return panel;
            }
        }
        return null;
    }

    public boolean existsPanel(String id) {
        return getPanel(id) != null;
    }

    public Set<Panel> getPanels() {
        Iterator<Panel> it = panels.iterator();
        while (it.hasNext()) {
            Panel panel = it.next();
            if (panel == null || panel.getInstance() == null) {
                // Discard rubbish
                it.remove();
            }
        }
        return panels;
    }

    public void setPanels(Set<Panel> panels) {
        this.panels = panels;
    }

    /**
     * Returns all panels
     */
    public Panel[] getAllPanels() {
        Set<Panel> set = getPanels();
        return set.toArray(new Panel[set.size()]);
    }

    /**
     * Return all panels remaining unassigned for this section
     */
    public Panel[] getUnassignedPanels() {
        List list = new ArrayList();
        Panel[] panels = getAllPanels();
        for (int i = 0; i < panels.length; i++) {
            if (panels[i].getRegion() == null) list.add(panels[i]);
        }
        return (Panel[]) list.toArray(new Panel[list.size()]);
    }

    /**
     * Returns the region that is currently maximized for a session, or null if none
     */
    public Panel getMaximizedPanel(HttpSession session) {
        Panel[] panels = getAllPanels();

        for (int i = 0; i < panels.length; i++) {
            if (panels[i].getRegion() != null) {
                PanelSession status = SessionManager.getPanelSession(panels[i]);
                LayoutRegionStatus regionStatus = SessionManager.getRegionStatus(panels[i].getSection(), panels[i].getRegion());
                LayoutRegion region = panels[i].getRegion();

                if (status != null && status.isMaximized()
                        && (region.isColumnRegion() || regionStatus.isSelected(panels[i]))) {
                    return panels[i];
                }
            }
        }

        return null;
    }

    /**
     * Restores all this sections panels to ites regular size
     */
    public void restorePanelSize(HttpSession session) {
        Panel[] panels = getAllPanels();

        for (int i = 0; i < panels.length; i++) {
            if (panels[i].getRegion() != null) {
                PanelSession status = SessionManager.getPanelSession(panels[i]);
                status.setStatus(PanelSession.STATUS_REGULAR_SIZE);
            }
        }
    }

    /**
     * Returns all panels stylesheets that must be generated in order to render this section
     *
     * @return
     * @deprecated Provider will no longer have an StyleSheet.
     */
    public String[] getPanelsStyleSheets() {
        Set styles = new HashSet();
        Panel[] panels = getAllPanels();
        for (int i = 0; i < panels.length; i++) {
            Panel panel = panels[i];
            if (panel.getInstance().getProvider().getStyleSheet() != null) {
                styles.add(panel.getInstance().getProvider().getStyleSheet());
            }
        }

        return (String[]) styles.toArray(new String[styles.size()]);
    }

    /**
     * Changes the order of a section, putting it before it was
     */
    public void moveUp(final Section section) {
        if (section == null || section.getPosition() == 0) return;

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Section[] sectionList = workspace.getAllChildSections(id);
                for (int i = 0; i < sectionList.length; i++) {
                    if (section.getId().equals(sectionList[i].getId())) {
                        sectionList[i - 1].setPosition(sectionList[i - 1].getPosition() + 1);
                        sectionList[i].setPosition(sectionList[i].getPosition() - 1);
                        UIServices.lookup().getSectionsManager().store(sectionList[i - 1]);
                        UIServices.lookup().getSectionsManager().store(sectionList[i]);
                    }
                }
            }
        };

        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * Changes the order of a section belonging to this workspace
     */
    public void moveDown(Section section) {
        if (section == null) return;
        Section[] sectionList = workspace.getAllChildSections(id);
        for (int i = 0; i < sectionList.length - 1; i++) {
            if (section.getId().equals(sectionList[i].getId())) {
                sectionList[i + 1].setPosition(sectionList[i + 1].getPosition() - 1);
                sectionList[i].setPosition(sectionList[i].getPosition() + 1);
                try {
                    UIServices.lookup().getSectionsManager().store(sectionList[i + 1]);
                    UIServices.lookup().getSectionsManager().store(sectionList[i]);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Returns a global key to identify this panel
     */
    public String getKey() {
        return getWorkspace().getId() + "_" + getId();
    }

    /**
     * Clone this object, that is, create a new Section with same parameters, and
     * same panels in the same workspace.
     *
     * @return a clone for this section, or null if it was not possible to replicate it.
     */
    public Object clone() {
//        Section sectionCopy = new Section(true);
        Section sectionCopy = new Section();
        sectionCopy.setLayoutId(getLayoutId());
        sectionCopy.setSkinId(getSkinId());
        sectionCopy.setEnvelopeId(getEnvelopeId());
        for (Iterator it = getTitle().keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String value = (String) getTitle().get(lang);
            sectionCopy.setTitle(value, lang);
        }
        sectionCopy.setVisible(isVisible());
        sectionCopy.setRegionsCellSpacing(getRegionsCellSpacing());
        sectionCopy.setPanelsCellSpacing(getPanelsCellSpacing());
        sectionCopy.setPosition(getPosition());
        sectionCopy.setId(getId());
        sectionCopy.setParentSectionId(getParentSectionId());
        return sectionCopy;
    }


    /**
     * @return a String representation for this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Section: \n");
        sb.append("              Id: ").append(id).append("\n");
//        sb.append("             new: " + isNew + "\n");

        sb.append("        layoutId: ").append(layoutId).append("\n");
        sb.append("           title: ").append(title).append("\n");
        sb.append("\nEnd Section.\n");

        return sb.toString();
    }

    // Section region panel management methods

    private SectionRegion getSectionRegion(LayoutRegion region) {
        return getSectionRegion(region.getId());
    }

    public SectionRegion getSectionRegion(String regionId) {
        if (regionId == null) return null;
        LayoutRegion layoutRegion = getLayout().getRegion(regionId);
        ArrayList panelsInRegion = new ArrayList();
        for (Iterator iterator = panels.iterator(); iterator.hasNext();) {
            Panel panel = (Panel) iterator.next();
            if (regionId.equals(panel.getLayoutRegionId())) {
                panelsInRegion.add(panel);
            }
        }
        Collections.sort(panelsInRegion);
        return new SectionRegion(this, layoutRegion, panelsInRegion);
    }

    /**
     * Removes a panel from its region, but keeping it as unassigned
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public void removePanelFromRegion(Panel panel) {
        if (panel != null) {
            String regionId = panel.getLayoutRegionId();
            if (regionId != null) {
                SectionRegion sr = getSectionRegion(regionId);
                sr.removePanel(panel);
            }
        }
    }

    /**
     * Returns all panels for a given region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public Panel[] getPanels(LayoutRegion region) {
        SectionRegion sr = getSectionRegion(region);
        return sr.getPanels();
    }

    /**
     * Returns the number of panels in a given region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public int getPanelsCount(LayoutRegion region) {
        SectionRegion sr = getSectionRegion(region);
        return sr.getPanelsCount();
    }

    /**
     * Moves the panel backwards in the panels list for its region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public void moveBackInRegion(Panel panel) {
        String regionId = panel.getLayoutRegionId();
        if (regionId != null) {
            SectionRegion sr = getSectionRegion(regionId);
            sr.moveBackInRegion(panel);
        }
    }

    /**
     * Moves the panel forward in the panels list for its region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public void moveForwardInRegion(Panel panel) {
        String regionId = panel.getLayoutRegionId();
        if (regionId != null) {
            SectionRegion sr = getSectionRegion(regionId);
            sr.moveForwardInRegion(panel);
        }
    }

    /**
     * Returns true is it's the first panel in a region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public boolean isFirstPanelInRegion(Panel panel, LayoutRegion region) {
        String regionId = panel.getLayoutRegionId();
        if (regionId != null) {
            SectionRegion sr = getSectionRegion(regionId);
            return sr.isFirstPanelInRegion(panel);
        }
        return false;
    }

    /**
     * Returns true is it's the first panel in a region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public boolean isLastPanelInRegion(Panel panel, LayoutRegion region) {
        String regionId = panel.getLayoutRegionId();
        if (regionId != null) {
            SectionRegion sr = getSectionRegion(regionId);
            return sr.isLastPanelInRegion(panel);
        }
        return false;
    }

    /**
     * Returns true is it's the only panel in a region
     *
     * @deprecated Use getSectionRegion to obtain SectionRegion instance and then use the methods
     *             defined into that class.
     */
    public boolean isOnlyPanelInRegion(Panel panel, LayoutRegion region) {
        String regionId = panel.getLayoutRegionId();
        if (regionId != null) {
            SectionRegion sr = getSectionRegion(regionId);
            return sr.isOnlyPanelInRegion(panel);
        }
        return false;
    }

    public String getSkinId() {
        return skinId;
    }

    public void setSkinId(String skinId) {
        this.skinId = skinId;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public Skin getSkin() {
        if (getSkinId() != null) {
            Skin skinToReturn = null;
            if (getWorkspace() != null)
                skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getElement(getSkinId(), getWorkspace().getId(), getId(), null);
            if (getWorkspace() != null && skinToReturn == null) {  //Try with a workspace
                skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getElement(getSkinId(), getWorkspace().getId(), null, null);
            }
            if (skinToReturn == null) {  //Try with a global
                skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getElement(getSkinId(), null, null, null);
            }
            if (skinToReturn != null)
                return skinToReturn;
        }
        return getWorkspace().getSkin();
    }

    public Envelope getEnvelope() {
        if (getEnvelopeId() != null) {
            Envelope envelopeToReturn = null;
            if (getWorkspace() != null)
                envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getElement(getEnvelopeId(), getWorkspace().getId(), getId(), null);
            if (getWorkspace() != null && envelopeToReturn == null) {  //Try with a workspace
                envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getElement(getEnvelopeId(), getWorkspace().getId(), null, null);
            }
            if (envelopeToReturn == null) {  //Try with a global
                envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getElement(getEnvelopeId(), null, null, null);
            }
            if (envelopeToReturn != null)
                return envelopeToReturn;
        }
        return getWorkspace().getEnvelope();
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitSection(this);

        //Visit permissions
        Policy securityPolicy = SecurityServices.lookup().getSecurityPolicy();
        Map workspacePermissions = securityPolicy.getPermissions(this, WorkspacePermission.class);
        Map panelPermissions = securityPolicy.getPermissions(this, PanelPermission.class);
        Map sectionPermissions = securityPolicy.getPermissions(this, SectionPermission.class);
        Map[] permissions = new Map[]{workspacePermissions, panelPermissions, sectionPermissions};
        for (int i = 0; i < permissions.length; i++) {
            Map permissionMap = permissions[i];
            for (Iterator it = permissionMap.keySet().iterator(); it.hasNext();) {
                Principal principal = (Principal) it.next();
                Permission perm = (Permission) permissionMap.get(principal);
                if (perm instanceof UIPermission) {
                    ((UIPermission) perm).setRelatedPrincipal(principal);
                    ((UIPermission) perm).acceptVisit(visitor);
                }
            }
        }

        //Visit section resources
        GraphicElement[] galleries = UIServices.lookup().getResourceGalleryManager().getElements(getWorkspace().getId(), getId());
        GraphicElement[] skins = UIServices.lookup().getSkinsManager().getElements(getWorkspace().getId(), getId());
        GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getElements(getWorkspace().getId(), getId());
        GraphicElement[] layouts = UIServices.lookup().getLayoutsManager().getElements(getWorkspace().getId(), getId());
        GraphicElement[][] elements = {galleries, skins, envelopes, layouts};
        for (int i = 0; i < elements.length; i++) {
            GraphicElement[] elementsArray = elements[i];
            for (int j = 0; j < elementsArray.length; j++) {
                GraphicElement element = elementsArray[j];
                element.acceptVisit(visitor);
            }
        }

        // Visit panels

        Panel[] sortedPanels = (Panel[]) getPanels().toArray(new Panel[getPanels().size()]);
        Arrays.sort(sortedPanels, new Comparator() {
            public int compare(Object o1, Object o2) {
                Panel p1 = (Panel) o1;
                Panel p2 = (Panel) o2;
                String region1 = p1.getRegion() != null ? p1.getRegion().getId() : "";
                String region2 = p2.getRegion() != null ? p2.getRegion().getId() : "";
                int pos1 = p1.getPosition();
                int pos2 = p2.getPosition();
                if (region1.equals(region2)) {
                    return pos1 - pos2;
                } else {
                    return region1.compareTo(region2);
                }
            }
        });
        for (int i = 0; i < sortedPanels.length; i++) {
            Panel panel = sortedPanels[i];
            panel.acceptVisit(visitor);
        }
        return visitor.endVisit();
    }
}
