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
import org.jboss.dashboard.workspace.events.ListenerQueueImpl;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jboss.dashboard.workspace.events.EventListener;
import org.jboss.dashboard.workspace.events.ListenerQueue;

/**
 * PanelsManagerImpl
 */
@ApplicationScoped
@Named("panelsManager")
public class PanelsManagerImpl implements PanelsManager {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelsManagerImpl.class.getName());

    /**
     * Handles the management of event listeners
     */
    private transient ListenerQueue<EventListener> listenerQueue = new ListenerQueueImpl<EventListener>();

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
        if (panelId != null) {
            final List<Panel> results = new ArrayList<Panel>();
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
            if (results.size() > 0) return results.get(0);
            else log.debug("Does not exists panel with DB id: " + panelId);
        }
        return null;
    }

    public Panel getPaneltById(final Long panelId) throws Exception {
        final List<Panel> results = new ArrayList<Panel>();
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
        if (results.size() > 0) return results.get(0);
        else log.debug("Does not exists panel with id: " + panelId);
        return null;
    }

    /**
     * Retrieves the panel instances with the given paramId as a panelParameter.
     */
    public Set<PanelInstance> getPanelsByParameter(final String paramId, final String value) throws Exception {
        final Set<PanelInstance> results = new HashSet<PanelInstance>();
        if(value == null || paramId == null) return results;

        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("select p ");
            sql.append("from ").append(PanelInstance.class.getName()).append(" as p join p.panelParams as param ");
            sql.append("where param.idParameter = :paramId ");

            Query query = session.createQuery(sql.toString());
            query.setString("paramId", paramId);

            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();

        Set<PanelInstance> matchPanel = new HashSet<PanelInstance>();
        for(PanelInstance panelInstance : results){
            Set<PanelParameter> panelParams = panelInstance.getPanelParams();
            for(PanelParameter panelParameter : panelParams){
                if (paramId.equals(panelParameter.getIdParameter()) &&
                    value.equals(panelParameter.getValue())){
                    matchPanel.add(panelInstance);
                }
            }
        }
        return matchPanel;
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
    public List<EventListener> getListeners(String eventId) {
        return listenerQueue.getListeners(eventId);
    }

    /**
     * Return listeners that should be notified of given event ID.  @param eventId
     *
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set<EventListener> getUniqueListeners(String eventId) {
        return listenerQueue.getUniqueListeners(eventId);
    }
}
