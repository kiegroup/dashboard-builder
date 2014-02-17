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
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.commons.text.StringUtil;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Envelope;
import org.jboss.dashboard.ui.resources.Skin;
import org.jboss.dashboard.security.Policy;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import java.security.Permission;
import java.security.Principal;
import java.util.*;

/**
 * A Workspace definition
 */
public class WorkspaceImpl implements Workspace {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkspaceImpl.class.getName());

    /**
     * Workspace identifier
     */
    private String id = null;

    /**
     * Look-and-feel identifier
     */
    private String skin = null;

    /**
     * Envelope identifier
     */
    private String envelope = null;

    /**
     * Friendly url for this workspace.
     */
    private String friendlyUrl;

    /**
     * Is this workspace the default workspace?
     */
    private boolean defaultWorkspace = false;

    /**
     * The user home search mode.
     * How user home search algorithm must behave for this workspace.
     */
    private int homeSearchMode = SEARCH_MODE_ROLE_HOME_PREFERENT;

    /**
     * List of sections for this workspace
     *
     * @link aggregation
     */
    private Set<Section> sections = new HashSet<Section>();

    /**
     * Panels stored by their ID
     *
     * @link aggregation
     */
    private Set panelInstancesSet = new HashSet();

    /**
     * PanelProvider Id's this workspace is allowed to see.
     *
     * @link aggregation
     */
    private Set panelProvidersAllowed = new HashSet();

    protected Set<WorkspaceHome> workspaceHomes;
    protected Set<WorkspaceParameter> workspaceParams;

    /**
     * Constructor
     */
    public WorkspaceImpl() {
        workspaceParams = new HashSet<WorkspaceParameter>();
        workspaceHomes = new HashSet<WorkspaceHome>();
    }

    public int getHomeSearchMode() {
        return homeSearchMode;
    }

    public void setHomeSearchMode(int homeSearchMode) {
        this.homeSearchMode = homeSearchMode;
    }

    public Set getWorkspaceParams() {
        return workspaceParams;
    }

    public void setWorkspaceParams(Set workspaceParams) {
        this.workspaceParams = workspaceParams;
    }

    public Map getWorkspaceParamValue(String name) {
        Map result = new HashMap();
        Iterator it = workspaceParams.iterator();
        while (it.hasNext()) {
            WorkspaceParameter parameter = (WorkspaceParameter) it.next();
            if (parameter.getParameterId().equals(name))
                result.put(parameter.getLanguage(), parameter.getValue());
        }

        return result;
    }

    public void setWorkspaceParamValue(String name, Map value) {
        Iterator langs = value.keySet().iterator();
        while (langs.hasNext()) {
            String lang = (String) langs.next();
            setWorkspaceParamValue(name, lang, (String) value.get(lang));
        }
    }

    protected void setWorkspaceParamValue(String name, String lang, String value) {
        boolean found = false;
        Iterator it = workspaceParams.iterator();
        while (it.hasNext()) {
            WorkspaceParameter parameter = (WorkspaceParameter) it.next();
            if (parameter.getParameterId().equals(name)) {
                if (parameter.getLanguage().equals(lang)) {
                    parameter.setValue(value);
                    found = true;
                }
            }
        }

        if (!found) {
            workspaceParams.add(new WorkspaceParameter(name, this, lang, value));
        }
    }

    public Set<WorkspaceHome> getWorkspaceHomes() {
        return workspaceHomes;
    }

    public void setWorkspaceHomes(Set<WorkspaceHome> workspaceHomes) {
        this.workspaceHomes = workspaceHomes;
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
        if (id == null || obj == null || !(obj instanceof WorkspaceImpl)) return false;

        WorkspaceImpl other = (WorkspaceImpl) obj;
        return id.equals(other.getId());
    }

    public String getEnvelopeId() {
        return envelope;
    }

    public void setEnvelopeId(String lookId) {
        this.envelope = lookId;
    }

    public Envelope getEnvelope() {
        String envelopeId = envelope;
        envelopeId = (envelopeId == null) ? UIServices.lookup().getEnvelopesManager().getDefaultElement().getId() : envelopeId;
        Envelope envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getElement(envelopeId, getId(), null, null);
        if (envelopeToReturn == null) //Try with a global envelope
            envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getElement(envelopeId, null, null, null);
        if (envelopeToReturn == null) //Try with default envelope
            envelopeToReturn = (Envelope) UIServices.lookup().getEnvelopesManager().getDefaultElement();
        return envelopeToReturn;
    }


    public String getSkinId() {
        return skin;
    }

    public void setSkinId(String lookId) {
        this.skin = lookId;
    }

    public Skin getSkin() {
        String skinId = skin;
        skinId = (skinId == null) ? UIServices.lookup().getSkinsManager().getDefaultElement().getId() : skinId;
        Skin skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getElement(skinId, getId(), null, null);
        if (skinToReturn == null)  //Try with a global skin
            skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getElement(skinId, null, null, null);
        if (skinToReturn == null)  //Return default skin
            skinToReturn = (Skin) UIServices.lookup().getSkinsManager().getDefaultElement();
        return skinToReturn;
    }

    /**
     * @return A Set of strings containing the Providers allowed for this workspace.
     */
    public Set getPanelProvidersAllowed() {
        return panelProvidersAllowed;
    }

    /**
     * @param s Set of panelProviders allowed to set.
     */
    public void setPanelProvidersAllowed(Set s) {
        panelProvidersAllowed = s;
    }

    /**
     * Adds given id to the Set of Providers allowed.
     *
     * @param id The id to add
     */
    public void addPanelProviderAllowed(String id) {
        Set s = new HashSet();
        s.add(id);
        s.addAll(panelProvidersAllowed);
        setPanelProvidersAllowed(s);
    }

    /**
     * Removes given id from the Set of panelInstances allowed.
     *
     * @param id The id to remove.
     */
    public void removePanelProviderAllowed(String id) {
        panelProvidersAllowed.remove(id);
    }

    /**
     * Determines if this panel provider is allowed for current workspace.
     *
     * @param id
     * @return if given provider id is allowed for this workspace
     */
    public boolean isProviderAllowed(String id) {
        return panelProvidersAllowed.contains(id);
    }

    public String getFriendlyUrl() {
        return friendlyUrl;
    }

    public void setFriendlyUrl(String s) {
        friendlyUrl = s;
    }

    public boolean getDefaultWorkspace() {
        return defaultWorkspace;
    }

    public void setDefaultWorkspace(boolean b) {
        defaultWorkspace = b;
    }

    public Set<Section> getSections() {
        return sections;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }

    /**
     * Returns all the sections
     */
    public Section[] getAllSections() {
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.addAll(sections);
        Collections.sort(sectionList);
        return sectionList.toArray(new Section[sectionList.size()]);
    }

    /**
     * Returns all the sections, but unsorted
     */
    public Section[] getAllUnsortedSections() {
        List<Section> sectionList = new ArrayList<Section>();
        sectionList.addAll(sections);
        return sectionList.toArray(new Section[sectionList.size()]);
    }

    /**
     * Returns all the root sections
     */
    public Section[] getAllRootSections() {
        return getAllChildSections(null);
    }

    /**
     * Returns all the children for a given section
     */
    public Section[] getAllChildSections(final Long sectionId) {
        final List<Section> childSections = new ArrayList<Section>();

        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);

                StringBuffer hql = new StringBuffer("from ");
                hql.append(Section.class.getName()).append(" as section where section.workspace=:workspace");
                if (sectionId == null) hql.append(" and section.parentSectionId is null");
                else hql.append(" and section.parentSectionId=:parentSectionId");

                Query query = session.createQuery(hql.toString());
                query.setParameter("workspace", WorkspaceImpl.this);
                if (sectionId != null) query.setLong("parentSectionId", sectionId);
                query.setCacheable(true);
                childSections.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        Collections.sort(childSections);
        return childSections.toArray(new Section[childSections.size()]);
    }

    public int getSectionsCount() {
        try {
            final int[] size = new int[1];
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    Query query = session.createQuery("from " + Section.class.getName() + " as section " +
                            "where section.workspace=:workspace");

                    query.setParameter("workspace", WorkspaceImpl.this);
                    query.setCacheable(true);
                    size[0] = query.list().size();
                    session.setFlushMode(oldFlushMode);
                }}.execute();
            return size[0];
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return -1;
    }

    /**
     * Returns a given section according to its identifier
     */
    public Section getSection(final Long id) {
        if (id == null) return null;
        final List candidates = new ArrayList();
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    Query query = session.createQuery("from " + Section.class.getName() + " as section " +
                            "where section.workspace=:workspace and section.sectionId=:pageid");

                    query.setParameter("workspace", WorkspaceImpl.this);
                    query.setLong("pageid", id.longValue());
                    query.setCacheable(true);
                    candidates.addAll(query.list());
                    session.setFlushMode(oldFlushMode);
                }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        if (candidates.size() == 1) {
            return (Section) candidates.get(0);
        }
        return null;

    }

    /**
     * Returns true if a given section exists
     */
    public boolean existsSection(Long id) {
        return getSection(id) != null;
    }

    /**
     * Removes a section according to its identifier
     */
    public void removeSection(final Section section) {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                // Notify all panels they are being removed before deleting section.
                Panel[] panels = section.getAllPanels();
                for (int i = 0; i < panels.length; i++) {
                    Panel panel = panels[i];
                    panel.getProvider().getDriver().fireBeforePanelRemoved(panel);
                    panel.panelRemoved();
                }

                //Delete own resources
                GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
                for (int i = 0; i < managers.length; i++) {
                    GraphicElementManager manager = managers[i];
                    GraphicElement[] elements = manager.getElements(section.getWorkspace().getId(), section.getId());
                    for (int j = 0; j < elements.length; j++) {
                        GraphicElement element = elements[j];
                        manager.delete(element);
                    }
                }

                // Remove attached section permissions
                Policy policy = SecurityServices.lookup().getSecurityPolicy();
                policy.removePermissions(section);
                policy.save();

                //Reposition other sections and remove this
                List childSections = section.getChildren();
                int childCount = childSections.size();

                List brotherSections;
                Section parentSection = section.getParent();
                int currentPosition = section.getPosition();
                if (parentSection != null) {
                    brotherSections = section.getParent().getChildren();
                } else {
                    brotherSections = Arrays.asList(getAllRootSections());
                }
                for (int i = 0; i < brotherSections.size(); i++) {
                    Section brotherSection = (Section) brotherSections.get(i);
                    if (brotherSection.getPosition() > currentPosition) {
                        brotherSection.setPosition(brotherSection.getPosition() + childCount - 1);
                        session.update(brotherSection);
                    }
                }

                for (int i = 0; i < childSections.size(); i++) {
                    Section childSection = (Section) childSections.get(i);
                    childSection.setParentSectionId(null);
                    childSection.setPosition(childSection.getPosition() + section.getPosition());
                    session.update(childSection);
                }
                // Remove section
                sections.remove(section);
                session.update(WorkspaceImpl.this);
//                session.delete(section);
                sectionsDiagnoseFix();
            }
        };


        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Can't delete section " + section.getId(), e);
        }
    }

    /**
     * Adds a section, either to regular sections or to system sections
     */
    public void addSection(Section section) {
        section.setWorkspace(this);
        if (section.getPosition() < 0) //If section has position set, consider it, else, generate one
            if (section.getParent() != null)
                section.setPosition(getAllChildSections(section.getParent().getId()).length);
            else
                section.setPosition(getAllRootSections().length);
        sections.add(section);
    }

    public Map getName() {
        return getWorkspaceParamValue("name");
    }

    public void setName(Map name) {
        setWorkspaceParamValue("name", name);
    }

    public void setName(String name, String lang) {
        if (lang == null || lang.trim().length() == 0)
            lang = LocaleManager.lookup().getDefaultLang();

        for (Iterator it = workspaceParams.iterator(); it.hasNext();) {
            WorkspaceParameter param = (WorkspaceParameter) it.next();
            if (param.getParameterId().equals("name") && param.getLanguage().equals(lang)) {
                param.setValue(name);
                return;
            }
        }

        workspaceParams.add(new WorkspaceParameter("name", this, lang, name));
    }

    public Map getTitle() {
        return getWorkspaceParamValue("title");
    }

    public void setTitle(Map title) {
        setWorkspaceParamValue("title", title);
    }

    public void setTitle(String title, String lang) {
        if (lang == null || lang.trim().length() == 0)
            lang = LocaleManager.lookup().getDefaultLang();

        for (Iterator it = workspaceParams.iterator(); it.hasNext();) {
            WorkspaceParameter param = (WorkspaceParameter) it.next();
            if (param.getParameterId().equals("title") && param.getLanguage().equals(lang)) {
                param.setValue(title);
                return;
            }
        }

        workspaceParams.add(new WorkspaceParameter("title", this, lang, title));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Workspace dbid is the same as logic id because workspace does not depend
     * of any entity and the workspace logic id is unique.
     */
    public String getDbid() {
        return getId();
    }

    /**
     * Changes the order of a section, putting it before it was
     */
    public void moveUp(Section section) {
        if (section == null)
            return;
        if (section.getWorkspace().equals(this))
            log.warn("Section doesn't belong to this workspace!");
        if (section.getPosition() == 0)
            return;
        Section[] sectionList = getAllRootSections();
        for (int i = 0; i < sectionList.length; i++) {
            if (section.getId().equals(sectionList[i].getId())) {
                sectionList[i - 1].setPosition(sectionList[i - 1].getPosition() + 1);
                sectionList[i].setPosition(sectionList[i].getPosition() - 1);
                try {
                    UIServices.lookup().getSectionsManager().store(sectionList[i - 1]);
                    UIServices.lookup().getSectionsManager().store(sectionList[i]);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Changes the order of a section belonging to this workspace
     */
    public void moveDown(Section section) {
        if (section == null)
            return;

        Section[] sectionList = getAllRootSections();
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

    // Panels instances management methods

    /**
     * Adds a panel to this section
     */
    public void addPanelInstance(PanelInstance instance) {
        if (!panelInstancesSet.contains(instance)) {
            instance.setWorkspace(this);
            panelInstancesSet.add(instance);
        }
    }

    /**
     * Removes a panel from this workspace
     */
    public void removePanelInstance(String panelId) {
        if (panelId != null && panelId.trim().length() > 0) {
            removePanelInstance(new Long(panelId));
        }
    }

    /**
     * Removes a panel from this workspace
     */
    public void removePanelInstance(Long instanceId) {
        if (instanceId != null)
            removePanelInstance(getPanelInstance(instanceId));
    }

    /**
     * Removes a panel from this workspace
     */
    public void removePanelInstance(final PanelInstance instance) {
        if (instance != null) {
            try {

                HibernateTxFragment txFragment = new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {

                        // Remove related permissions
                        Policy policy = SecurityServices.lookup().getSecurityPolicy();
                        policy.removePermissions(instance);
                        policy.save();
                        for (Section section : sections) {
                            for (Panel panel : section.getAllPanels()) {
                                if (instance.getInstanceId().equals(panel.getInstanceId())) {
                                    section.removePanel(panel);

                                    // Remove panels
                                    // Apply this patch in order to ensure all panel are removed before deleting PanelInstance.
                                    // If this line is not added workspace store fails with a "org.hibernate.PropertyValueException:
                                    // not-null property references a null or transient value.
                                    session.update(section);
                                }
                            }
                        }

                        // Notify instance removal before deleting it.
                        instance.instanceRemoved(session);

                        // Remove instance
                        // Apply this patch to force the removal of PanelInstance.
                        // cascade="all-delete-orphan" don't works because an aggregation (Workspace <-> PanelInstance)
                        // and association (Panel -> PanelInstance) over an element are defined.
                        panelInstancesSet.remove(instance);
                        session.delete(instance);
                    }
                };

                txFragment.execute();
            } catch (Exception e) {
                log.error("Can't remove panel instance with id " + instance.getId(), e);
            }
        }
    }

    /**
     * Returns a panel by identifier
     */
    public PanelInstance getPanelInstance(String id) {
        if (id != null && id.trim().length() > 0)
            return getPanelInstance(new Long(id));
        else
            return null;
    }

    public PanelInstance getPanelInstance(final Long id) {
        if (id == null) return null;
        final List candidates = new ArrayList();
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    Query query = session.createQuery("from " + PanelInstance.class.getName() + " as instance " +
                            "where instance.workspace=:workspace and instance.instanceId=:instid");

                    query.setParameter("workspace", WorkspaceImpl.this);
                    query.setLong("instid", id.longValue());
                    query.setCacheable(true);
                    candidates.addAll(query.list());
                    session.setFlushMode(oldFlushMode);
                }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        if (candidates.size() == 1) {
            return (PanelInstance) candidates.get(0);
        }
        return null;
    }

    public boolean existsPanelInstance(String id) {
        return getPanelInstance(id) != null;
    }

    /**
     * Returns all panels
     */
    public PanelInstance[] getPanelInstances() {
        return (PanelInstance[]) panelInstancesSet.toArray(new PanelInstance[panelInstancesSet.size()]);
    }

    /**
     * Return panel instances inside given group.
     *
     * @param groupId Group inside which panels are returned.
     * @return A (possibly empty) array of instances belonging to a group.
     */
    public PanelInstance[] getPanelInstancesInGroup(String groupId) {
        List panels = new ArrayList();
        if (groupId != null)
            for (Iterator it = panelInstancesSet.iterator(); it.hasNext();) {
                PanelInstance instance = (PanelInstance) it.next();
                if (groupId.equals(instance.getProvider().getGroup()))
                    panels.add(instance);
            }
        return (PanelInstance[]) panels.toArray(new PanelInstance[panels.size()]);
    }

    public Set getPanelInstancesSet() {
        return panelInstancesSet;
    }

    public void setPanelInstancesSet(Set instances) {
        panelInstancesSet = instances;
    }

    public static Map getSectionArray(WorkspaceImpl workspace, Section parent) {
        Map result = new HashMap();

        Section[] sections;
        if (parent == null)
            sections = workspace.getAllRootSections();
        else
            sections = workspace.getAllChildSections(parent.getId());

        for (int i = 0; i < sections.length; i++) {
            Section section = sections[i];
            result.put(section, getSectionArray(workspace, section));
        }

        return result;
    }

    public static String getSectionTree(WorkspaceImpl workspace, Section parent, String locale) {
        String result = "";
        Map sections = getSectionArray(workspace, parent);
        if (sections != null && !sections.isEmpty()) {
            List sectionList = new ArrayList(sections.keySet());
            Collections.sort(sectionList);
            int sectionCount = sectionList.size();
            for (int i = 0; i < sectionCount; i++) {
                Section section = (Section) sectionList.get(i);

                Map children = (Map) sections.get(section);
                LocaleManager localeManager = LocaleManager.lookup();

                String sectionTitle = StringUtil.replaceAll(localeManager.localize(section.getTitle()).toString(), "\\", "\\\\");
                sectionTitle = StringUtil.replaceAll(sectionTitle, "'", "\\'");
                result += "['" + sectionTitle + "', '" + section.getId() + "_" + section.getPosition() + "_" + (sectionCount - 1) + "'";

                if (children != null && !children.isEmpty()) {
                    result += ", ";
                    result += getSectionTree(workspace, section, locale);
                }
                result += "],";
            }

        }

        return result;
    }

    public void reorderSections(Section root, int startIndex) throws Exception {
        Section[] sections;
        if (root != null)
            sections = getAllChildSections(root.getId());
        else
            sections = getAllRootSections();

        for (int i = startIndex; i < sections.length; i++) {
            sections[i].setPosition(i);
            UIServices.lookup().getSectionsManager().store(sections[i]);
        }
    }

    /**
     * Create a shallow copy of this Workspace.
     * A new Workspace with same basic attributes is created and returned.
     *
     * @return The Workspace clone.
     */
    protected Object clone() {
        WorkspaceImpl workspace = new WorkspaceImpl();
        log.debug("Setting basic attributes to workspace clone ");
        workspace.setEnvelopeId(this.envelope);
        workspace.setSkinId(this.skin);

        // Parameters
        Set params = new HashSet();
        for (Iterator it = workspaceParams.iterator(); it.hasNext();) {
            WorkspaceParameter param = (WorkspaceParameter) it.next();
            if (param.getValue() != null && param.getValue().trim().length() > 0)
                params.add(new WorkspaceParameter(param.getParameterId(), workspace, param.getLanguage(), param.getValue()));
        }
        workspace.setWorkspaceParams(params);

        for (Iterator it = getPanelProvidersAllowed().iterator(); it.hasNext();)
            workspace.addPanelProviderAllowed((String) it.next());
        return workspace;
    }


    /**
     * @return a String representation for this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Workspace: \n");
        sb.append("              Id: ").append(id).append("\n");
        sb.append("        envelope: ").append(envelope).append("\n");
        sb.append("            skin: ").append(skin).append("\n");
        sb.append("            name: ").append(getName()).append("\n");
        sb.append("           title: ").append(getTitle()).append("\n");
        sb.append("  \nPanel Instances   \n\n");
        for (Iterator it = getPanelInstancesSet().iterator(); it.hasNext();)
            sb.append(it.next()).append("\n");
        sb.append("  \nSections \n\n");
        for (Section section : sections)
            sb.append(section).append("\n");
        sb.append("\nEnd Workspace.\n");

        return sb.toString();
    }

    /**
     * For debug purposes
     *
     * @return A String representation for this object, more useful than the default toString
     */
    public String getStructureRepresentation() {
        StringBuffer sb = new StringBuffer();
        sb.append("Workspace id=").append(getId()).append(" dbid=").append(getDbid()).append("\n");
        PanelInstance[] instances = this.getPanelInstances();
        for (int i = 0; i < instances.length; i++) {
            PanelInstance instance = instances[i];
            sb.append("|__PI(dbid=").append(instance.getDbid()).append(", id=").append(instance.getInstanceId()).append(", provider=").append(instance.getProviderName()).append(")\n");
        }

        Section[] pages = this.getAllSections();
        for (int i = 0; i < pages.length; i++) {
            Section section = pages[i];
            sb.append("|__S (dbid=").append(section.getDbid()).append(", id=").append(section.getId()).append(" title=").append(section.getTitle()).append(" path=").append(section.getPathNumber()).append(" )\n");
            Panel[] panels = section.getAllPanels();
            for (int j = 0; j < panels.length; j++) {
                Panel panel = panels[j];
                sb.append("   |__P (dbid=").append(panel.getDbid()).append(", id=").append(panel.getPanelId()).append(", instanceId=").append(panel.getInstanceId()).append(")\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a given section according to its url
     */
    public Section getSectionByUrl(final String friendlyUrl) {
        if (friendlyUrl == null) return null;
        final List candidates = new ArrayList();
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    Query query = session.createQuery("from " + Section.class.getName() + " as section " +
                            "where section.workspace=:workspace and section.friendlyUrl=:pageid");

                    query.setParameter("workspace", WorkspaceImpl.this);
                    query.setString("pageid", friendlyUrl);
                    query.setCacheable(true);
                    candidates.addAll(query.list());
                    session.setFlushMode(oldFlushMode);
                }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        if (candidates.size() == 1) {
            return (Section) candidates.get(0);
        }
        return null;
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitWorkspace(this);

        //Workspace permissions
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        Map workspacePermissions = policy.getPermissions(this, WorkspacePermission.class);
        Map panelPermissions = policy.getPermissions(this, PanelPermission.class);
        Map sectionPermissions = policy.getPermissions(this, SectionPermission.class);
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

        //Workspace parameters
        for (Iterator it = getWorkspaceParams().iterator(); it.hasNext();) {
            WorkspaceParameter param = (WorkspaceParameter) it.next();
            param.acceptVisit(visitor);
        }

        //Workspace resources
        GraphicElement[] galleries = UIServices.lookup().getResourceGalleryManager().getElements(getId(), null, null);
        GraphicElement[] skins = UIServices.lookup().getSkinsManager().getElements(getId(), null, null);
        GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getElements(getId(), null, null);
        GraphicElement[] layouts = UIServices.lookup().getLayoutsManager().getElements(getId(), null, null);
        GraphicElement[][] elements = {galleries, skins, envelopes, layouts};
        for (int i = 0; i < elements.length; i++) {
            GraphicElement[] elementsArray = elements[i];
            for (int j = 0; j < elementsArray.length; j++) {
                GraphicElement element = elementsArray[j];
                element.acceptVisit(visitor);
            }
        }

        //Add panelInstances
        PanelInstance[] panelInstances = getPanelInstances();
        Arrays.sort(panelInstances, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((PanelInstance) o1).getDbid().compareTo(((PanelInstance) o2).getDbid());
            }
        });
        for (int i = 0; i < panelInstances.length; i++) {
            PanelInstance panelInstance = panelInstances[i];
            panelInstance.acceptVisit(visitor);
        }

        //Add sections
        Section[] sortedSections = getAllSections();
        for (int i = 0; i < sortedSections.length; i++) {
            Section section = sortedSections[i];
            section.acceptVisit(visitor);
        }

        return visitor.endVisit();
    }

    public int sectionsDiagnose() throws Exception {
        return sectionsDiagnose(Arrays.asList(getAllRootSections()), false);
    }

    public void sectionsDiagnoseFix() throws Exception {
        sectionsDiagnose(Arrays.asList(getAllRootSections()), true);
    }

    private int sectionsDiagnose(List sections, boolean fixing) throws Exception {
        int errors = 0;
        for (int i = 0; i < sections.size(); i++) {
            final Section section = (Section) sections.get(i);
            if (section.getPosition() != i) {
                if (fixing) {
                    final int i1 = i;
                    new HibernateTxFragment() {
                        protected void txFragment(Session session) throws Exception {
                            section.setPosition(i1);
                            session.update(section);
                        }
                    }.execute();
                } else {
                    log.error(" Workspace " + getId() + " page " + section.getId() + " is in wrong position. Expecting " + i + ", found " + section.getPosition());
                    errors++;
                }
            }
            errors += sectionsDiagnose(section.getChildren(), fixing);
        }
        return errors;
    }

    public Section getDefaultHomePageForRole(String role) {
        for (WorkspaceHome home : workspaceHomes) {
            if (home.getRoleId().equals(role)) {
                return getSection(home.getSectionId());
            }
        }
        return null;
    }
}
