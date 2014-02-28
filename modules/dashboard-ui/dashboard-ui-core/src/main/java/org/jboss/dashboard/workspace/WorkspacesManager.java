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

import org.apache.commons.collections.CollectionUtils;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.events.*;
import org.jboss.dashboard.workspace.events.EventListener;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.ui.utils.javascriptUtils.JavascriptTree;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.security.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

/**
 * Manager class for Workspaces, it provide the common operations over workspaces (create, search, edit, delete..).
 */
@ApplicationScoped
public class WorkspacesManager {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(WorkspacesManager.class.getName());

    /**
     * Handles the management of event listeners
     */
    private transient ListenerQueueImpl listenerQueue = new ListenerQueueImpl();

    @Inject
    protected SkinsManager skinsManager;

    @Inject
    protected EnvelopesManager envelopesManager;

    @Inject
    protected LayoutsManager layoutsManager;

    /**
     * Generate a unused workspace identifier
     */
    public synchronized String generateWorkspaceId() throws Exception {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String id = "" + i;
            if (!existsWorkspace(id, true)) {
                return id;
            }
        }
        return null;
    }

    public boolean existsWorkspace(final String id) throws Exception {
        return existsWorkspace(id, false);
    }

    public boolean existsWorkspace(final String id, final boolean lock) throws Exception {
        final boolean[] exists = new boolean[]{false};
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Object workspaceFound = session.get(WorkspaceImpl.class, id, lock ? LockMode.UPGRADE : LockMode.NONE);
                exists[0] = workspaceFound != null;
            }
        };
        txFragment.execute();
        return exists[0];
    }

    public Map generateUniqueWorkspaceName( Workspace workspace ) throws Exception {
        return generateUniqueWorkspaceName( 1,
                workspace,
                null );
    }

    private Map generateUniqueWorkspaceName(int index, Workspace workspace, Map<String, String> newProposed) throws Exception {
        if (workspace == null) return null;
        Map<String, String> toEval = new HashMap<String, String>();
        toEval.putAll( newProposed != null ? newProposed : workspace.getName() );
        if ( existsWorkspaceName(workspace, toEval) ) {
            Map originalNames = workspace.getName();
            for (Map.Entry<String, String> entry : toEval.entrySet()) {
                entry.setValue(originalNames.get(entry.getKey()) + " (" + index + ")");
            }
            return generateUniqueWorkspaceName(++index, workspace, toEval);
        }
        return toEval;
    }

    private boolean existsWorkspaceName( Workspace workspace, Map proposedNames ) {
        boolean found = false;
        Workspace[] workspaces = getWorkspaces();
        int i = 0;
        while (i < workspaces.length && !found) {
            if ( !workspace.getId().equals(workspaces[i].getId()) ) {
                Map<String, String> wnames = workspaces[i].getName();
                found = CollectionUtils.containsAny(wnames.values(), proposedNames.values());
            }
            i++;
        }
        return found;
    }

    /**
     * Adds a workspace to the whole system
     */
    public void addNewWorkspace(WorkspaceImpl newWorkspace) throws Exception {
        store(newWorkspace);
        enablePanelProviders(newWorkspace);
        fireWorkspaceCreated(newWorkspace);
    }

    private void enablePanelProviders(WorkspaceImpl newWorkspace) {
        PanelProvider[] providers = UIServices.lookup().getPanelsProvidersManager().getProviders();

        for (PanelProvider provider : providers) {
            newWorkspace.addPanelProviderAllowed(provider.getId());
        }
    }

    /**
     * Removes a workspace from the system
     */
    public void delete(final WorkspaceImpl workspace) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {

                //Delete own resources
                GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
                for (int i = 0; i < managers.length; i++) {
                    GraphicElementManager manager = managers[i];
                    GraphicElement[] elements = manager.getElements(workspace.getId());
                    for (int j = 0; j < elements.length; j++) {
                        GraphicElement element = elements[j];
                        manager.delete(element);
                    }
                }

                // Remove attached workspace permissions.
                Policy policy = SecurityServices.lookup().getSecurityPolicy();
                policy.removePermissions(workspace);

                // Notify instances removal before deleting workspace.
                Iterator instanceIt = workspace.getPanelInstancesSet().iterator();
                while (instanceIt.hasNext()) {
                    PanelInstance instance = (PanelInstance) instanceIt.next();
                    instance.instanceRemoved(session);
                }

                // Notify panels before deleting workspace.
                Iterator sectionIt = workspace.getSections().iterator();
                while (sectionIt.hasNext()) {
                    Section section = (Section) sectionIt.next();
                    Panel[] panels = section.getAllPanels();
                    for (int i = 0; i < panels.length; i++) {
                        Panel panel = panels[i];
                        panel.getProvider().getDriver().fireBeforePanelRemoved(panel);
                        panel.panelRemoved();
                    }
                }

                policy.save();

                // Delete workspace.
                session.delete(workspace);
                session.flush();

                // Notify workspace removal
                fireWorkspaceRemoved(workspace);
            }
        };

        txFragment.execute();
    }

    public Workspace getWorkspace(final String id) throws Exception {
        final WorkspaceImpl[] workspace = new WorkspaceImpl[]{null};

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                workspace[0] = (WorkspaceImpl) session.get(WorkspaceImpl.class, id);
            }
        };

        txFragment.execute();
        return workspace[0];
    }

    /**
     * Return all workspaces
     */
    public WorkspaceImpl[] getWorkspaces() {
        final List workspaces = new ArrayList();

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                Query q = session.createQuery(" from " + WorkspaceImpl.class.getName());
                q.setCacheable(true);
                workspaces.addAll(q.list());
                session.setFlushMode(oldFlushMode);
            }
        };

        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Error:", e);
        }
        return (WorkspaceImpl[]) workspaces.toArray(new WorkspaceImpl[workspaces.size()]);
    }

    public synchronized void store(final Workspace workspace) throws Exception {
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                boolean isNew = workspace.getId() == null || !getAllWorkspacesIdentifiers().contains(workspace.getId());
                if (isNew) {
                    session.save(workspace);
                } else {
                    synchronized (("workspace_" + workspace.getId()).intern()) {
                        session.update(workspace);
                        fireWorkspaceUpdated(workspace);
                    }
                }
                session.flush();
            }
        };
        txFragment.execute();
    }

    public Set getAllWorkspacesIdentifiers() throws Exception {
        Set s = new TreeSet();
        WorkspaceImpl[] workspaces = getWorkspaces();
        for (int i = 0; i < workspaces.length; i++) {
            WorkspaceImpl workspace = workspaces[i];
            s.add(workspace.getId());
        }
        return s;
    }

    /**
     * @return
     * @throws Exception
     * @deprecated Workspaces manager shouldn't be aware of current user status.
     */
    public synchronized Set getAvailableWorkspacesIds() throws Exception {
        Set workspacesIds = getAllWorkspacesIdentifiers();
        Set userWorkspacesIds = new HashSet();
        log.debug("Getting available workspace ids for current user.");
        UserStatus userStatus = UserStatus.lookup();
        for (Iterator iterator = workspacesIds.iterator(); iterator.hasNext();) {
            String id = (String) iterator.next();
            log.debug("   Checking workspace " + id);
            WorkspaceImpl p = (WorkspaceImpl) getWorkspace(id);
            WorkspacePermission perm = WorkspacePermission.newInstance(p, WorkspacePermission.ACTION_LOGIN);
            if (p != null && userStatus.hasPermission(perm)) {
                userWorkspacesIds.add(id);
            }
        }
        return userWorkspacesIds;
    }

    public Workspace getWorkspaceByUrl(final String url) throws Exception {
        final Workspace[] workspace = new Workspace[1];
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                Query q = session.createQuery(" from " + WorkspaceImpl.class.getName() + " p where p.friendlyUrl = :url");
                q.setString("url", url);
                q.setCacheable(true);
                List l = q.list();
                if (l.size() == 1) {
                    workspace[0] = (Workspace) l.get(0);
                }
                session.setFlushMode(oldFlushMode);
            }
        }.execute();
        return workspace[0];
    }

    public Workspace getDefaultWorkspace() {
        WorkspaceImpl[] workspaces = getWorkspaces();
        for (int i = 0; i < workspaces.length; i++) {
            WorkspaceImpl workspace = workspaces[i];
            if (workspace.getDefaultWorkspace())
                return workspace;
        }
        return null;
    }

    public void fireWorkspaceWizardFinished(Workspace src, Workspace clone) {
        List list = getListeners(EventConstants.WORKSPACE_WIZARD_FINISHED);
        WorkspaceDuplicationEvent event = new WorkspaceDuplicationEvent(EventConstants.WORKSPACE_WIZARD_FINISHED, src, clone);
        log.debug("Firing event " + event);
        for (Iterator it = list.iterator(); it.hasNext();) {
            WorkspaceListener listener = (WorkspaceListener) it.next();
            listener.workspaceWizardFinished(event);
        }
    }

    /**
     * Adds a listener for all queues
     *
     * @param listener EventListener to add
     */
    public void addListener(EventListener listener) {
        listenerQueue.addListener(listener);
    }

    /**
     * Adds a listener to the queue for events with given id
     *
     * @param listener EventListener to add
     * @param eventId  Event id the listener is interested in.
     */
    public void addListener(EventListener listener, String eventId) {
        listenerQueue.addListener(listener, eventId);
    }

    /**
     * Removes Listener from all queues
     *
     * @param listener listener EventListener to remove
     */
    public void removeListener(EventListener listener) {
        listenerQueue.removeListener(listener);
    }

    /**
     * Removes a Listener from given queue
     *
     * @param listener listener EventListener to remove
     * @param eventId  Event id queue to remove listener from.
     */
    public void removeListener(EventListener listener, String eventId) {
        listenerQueue.removeListener(listener, eventId);
    }

    /**
     * Return listeners that should be notified of given event ID. May contain
     * duplicates.
     *
     * @param eventId
     * @return A List of listeners
     */
    public List getListeners(String eventId) {
        return listenerQueue.getListeners(eventId);
    }

    /**
     * Return listeners that should be notified of given event ID.  @param eventId
     *
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set getUniqueListeners(String eventId) {
        return listenerQueue.getUniqueListeners(eventId);
    }

    /**
     * Fires the workspace created event to all listeners
     *
     * @param p The workspace created.
     */
    protected void fireWorkspaceCreated(Workspace p) {
        List list = getListeners(EventConstants.WORKSPACE_CREATED);
        WorkspaceEvent event = new WorkspaceEvent(EventConstants.WORKSPACE_CREATED, p);
        log.debug("Firing event " + event);
        for (Iterator it = list.iterator(); it.hasNext();) {
            WorkspaceListener listener = (WorkspaceListener) it.next();
            listener.workspaceCreated(event);
        }
    }

    /**
     * Fires the workspace removed event to all listeners
     *
     * @param p The workspace removed.
     */
    protected void fireWorkspaceRemoved(Workspace p) {
        JavascriptTree.regenerateTrees(p.getId());
        List list = getListeners(EventConstants.WORKSPACE_REMOVED);
        WorkspaceEvent event = new WorkspaceEvent(EventConstants.WORKSPACE_REMOVED, p);
        log.debug("Firing event " + event);
        for (Iterator it = list.iterator(); it.hasNext();) {
            WorkspaceListener listener = (WorkspaceListener) it.next();
            listener.workspaceRemoved(event);
        }
    }

    /**
     * Fires the workspace updated event to all listeners
     *
     * @param p The workspace updated.
     */
    protected void fireWorkspaceUpdated(Workspace p) {
        JavascriptTree.regenerateTrees(p.getId());
        List list = getListeners(EventConstants.WORKSPACE_UPDATED);
        WorkspaceEvent event = new WorkspaceEvent(EventConstants.WORKSPACE_UPDATED, p);
        log.debug("Firing event " + event);
        for (Iterator it = list.iterator(); it.hasNext();) {
            WorkspaceListener listener = (WorkspaceListener) it.next();
            listener.workspaceUpdated(event);
        }
    }

    /**
     * Inits a workspace.
     */
    private void init(WorkspaceImpl workspace) throws Exception {
        // TODO: move initialization code to Lifecycle onLoad callbacks

        // Init panel instances
        PanelInstance[] instances = workspace.getPanelInstances();
        for (int i = 0; i < instances.length; i++) {
            instances[i].init();
        }

        // Init sections & panels
        Iterator sectionIt = workspace.getSections().iterator();
        while (sectionIt.hasNext()) {
            Section section = (Section) sectionIt.next();
            section.init();
            Iterator panelIt = section.getPanels().iterator();
            while (panelIt.hasNext()) {
                Panel panel = (Panel) panelIt.next();
                panel.init();
            }
        }
    }

    public void deleteUselessPanelsAndInstances(String workspaceId) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace(workspaceId);
        PanelInstance[] instances = workspace.getPanelInstances();
        if (instances != null) {
            for (int i = 0; i < instances.length; i++) {
                PanelInstance instance = instances[i];
                Panel[] panels = instance.getAllPanels();
                boolean anyPanelPlaced = false;
                if (panels != null) {
                    for (int j = 0; j < panels.length && !anyPanelPlaced; j++) {
                        Panel panel = panels[j];
                        if (panel.getRegion() != null) {
                            anyPanelPlaced = true;
                        }
                    }
                }
                if (!anyPanelPlaced)
                    removeInstance(instance);
            }
        }
    }

    public void deleteUselessPanelInstances(String workspaceId) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace(workspaceId);
        PanelInstance[] instances = workspace.getPanelInstances();
        if (instances != null) {
            for (int i = 0; i < instances.length; i++) {
                PanelInstance instance = instances[i];
                Panel[] panels = instance.getAllPanels();
                if (panels == null || panels.length == 0) {
                    removeInstance(instance);
                }
            }
        }
    }

    public void removeInstance(PanelInstance instance) throws Exception {
        String panelId = instance.getId();
        WorkspaceImpl workspace = instance.getWorkspace();

        // Delete own resources
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (int i = 0; i < managers.length; i++) {
            GraphicElementManager manager = managers[i];
            if (!manager.getElementScopeDescriptor().isAllowedInstance()) {
                continue; // This manager does not define panel elements.
            }
            GraphicElement[] elements = manager.getElements(workspace.getId(), null, new Long(panelId));
            for (int j = 0; j < elements.length; j++) {
                GraphicElement element = elements[j];
                manager.delete(element);
            }
        }
        workspace.removePanelInstance(panelId);
        store(workspace);
    }
}
