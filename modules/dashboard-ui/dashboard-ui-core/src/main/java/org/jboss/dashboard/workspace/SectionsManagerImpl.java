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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.events.ListenerQueueImpl;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class SectionsManagerImpl implements SectionsManager {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SectionsManagerImpl.class.getName());

    /**
     * Handles the management of event listeners
     */
    private transient ListenerQueueImpl listenerQueue = new ListenerQueueImpl();

    public Section getSectionByDbId(final Long dbid) throws Exception {
        if (dbid == null) return null;

        final List<Section> results = new ArrayList<Section>();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("select item ");
            sql.append("from ").append(Section.class.getName()).append(" as item ");
            sql.append("where item.dbid = :dbid");

            Query query = session.createQuery(sql.toString());
            query.setLong("dbid", dbid.longValue());
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) return results.get(0);
        else log.debug("Does not exists a section with dbid: " + dbid);
        return null;
    }
    
    /**
     * Removes a workspace section from the system
     */
    public void delete(final Section section) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {

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

                session.delete(section);
            }
        };

        txFragment.execute();
    }

    /**
     * Stores workspace section state in database
     */
    public synchronized void store(final Section section) throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            synchronized (("section_" + section.getDbid()).intern()) {
                session.saveOrUpdate(section);
                session.flush();
            }
        }}.execute();
    }

    /**
     * Adds a listener for all queues
     *
     * @param listener EventListener to add
     */
    public void addListener(org.jboss.dashboard.workspace.events.EventListener listener) {
        listenerQueue.addListener(listener);
    }

    /**
     * Adds a listener to the queue for events with given id
     *
     * @param listener EventListener to add
     * @param eventId  Event id the listener is interested in.
     */
    public void addListener(org.jboss.dashboard.workspace.events.EventListener listener, String eventId) {
        listenerQueue.addListener(listener, eventId);
    }

    /**
     * Removes Listener from all queues
     *
     * @param listener listener EventListener to remove
     */
    public void removeListener(org.jboss.dashboard.workspace.events.EventListener listener) {
        listenerQueue.removeListener(listener);
    }

    /**
     * Removes a Listener from given queue
     *
     * @param listener listener EventListener to remove
     * @param eventId  Event id queue to remove listener from.
     */
    public void removeListener(org.jboss.dashboard.workspace.events.EventListener listener, String eventId) {
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
     * Return listeners that should be notified of given event ID.
     *
     * @param eventId
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set getUniqueListeners(String eventId) {
        return listenerQueue.getUniqueListeners(eventId);
    }
}
