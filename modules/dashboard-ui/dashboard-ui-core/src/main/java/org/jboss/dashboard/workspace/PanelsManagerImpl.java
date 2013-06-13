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
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelsManager;
import org.jboss.dashboard.workspace.events.ListenerQueueImpl;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * PanelsManagerImpl
 */
@ApplicationScoped
@Named("panelsManager")
public class PanelsManagerImpl implements PanelsManager {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PanelsManagerImpl.class.getName());

    /**
     * Handles the management of event listeners
     */
    private transient ListenerQueueImpl listenerQueue = new ListenerQueueImpl();

    /**
     * Removes a panel instance
     */
    public void delete(final PanelInstance instance) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.delete(instance);
            }
        };

        txFragment.execute();
    }


    public void delete(final Panel panel) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.delete(panel);
            }
        };

        txFragment.execute();
    }


    /**
     * Persist panel status to database
     */
    public synchronized void store(final Panel panel) throws Exception {

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.saveOrUpdate(panel);
                session.flush();
            }
        };

        synchronized (panel) {
            txFragment.execute();
        }
    }

    /**
     * Persist panel status to database
     */
    public synchronized void store(final PanelInstance instance) throws Exception {
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.saveOrUpdate(instance);
            }
        };
        synchronized (instance) {
            txFragment.execute();
        }
    }

    public Panel getPaneltByDbId(final Long panelId) throws Exception {
        final List results = new ArrayList();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("select p ");
            sql.append("from ").append(Panel.class.getName()).append(" as p ");
            sql.append("where p.dbid = :dbid");

            Query query = session.createQuery(sql.toString());
            query.setLong("dbid", panelId);
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) return (Panel)results.get(0);
        else log.debug("Does not exists panel with DB id: " + panelId);
        return null;
    }

    public Panel getPaneltById(final Long panelId) throws Exception {
        final List results = new ArrayList();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("select p ");
            sql.append("from ").append(Panel.class.getName()).append(" as p ");
            sql.append("where p.panelId = :panelId");

            Query query = session.createQuery(sql.toString());
            query.setLong("panelId", panelId);
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) return (Panel)results.get(0);
        else log.debug("Does not exists panel with id: " + panelId);
        return null;
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
     * Return listeners that should be notified of given event ID.  @param eventId
     *
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set getUniqueListeners(String eventId) {
        return listenerQueue.getUniqueListeners(eventId);
    }
}
