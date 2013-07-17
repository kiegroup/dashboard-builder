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
package org.jboss.dashboard.workspace.events;

import java.util.*;

/**
 * Date: 27-may-2004
 * Time: 18:02:57
 */
public class ListenerQueueImpl implements ListenerQueue {
    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ListenerQueueImpl.class.getName());

    protected Hashtable queues = new Hashtable();
    protected List allEventsQueue = new ArrayList();

    public ListenerQueueImpl() {
    }

    /**
     * Adds a listener for all queues
     *
     * @param listener EventListener to add
     */
    public synchronized void addListener(EventListener listener) {
        allEventsQueue.add(listener);
    }

    /**
     * Adds a listener to the queue for events with given id
     *
     * @param listener EventListener to add
     * @param eventId  Event id the listener is interested in.
     */
    public synchronized void addListener(EventListener listener, String eventId) {
        List list = (List) queues.get(eventId);
        if (list == null)
            list = new ArrayList();
        list.add(listener);
        queues.put(eventId, list);
    }

    /**
     * Removes Listener from all queues
     *
     * @param listener listener EventListener to remove
     */
    public synchronized void removeListener(EventListener listener) {
        allEventsQueue.remove(listener);
        for (Iterator it = queues.values().iterator(); it.hasNext();) {
            List list = (List) it.next();
            list.remove(listener);
        }
    }

    /**
     * Removes a Listener from given queue
     *
     * @param listener listener EventListener to remove
     * @param eventId  Event id queue to remove listener from.
     */
    public synchronized void removeListener(EventListener listener, String eventId) {
        List list = (List) queues.get(eventId);
        if (list == null)
            return;
        list.remove(listener);
    }

    /**
     * Return listeners that should be notified of given event ID. May contain
     * duplicates.
     *
     * @param eventId
     * @return A List of listeners
     */
    public List getListeners(String eventId) {
        List list = new ArrayList();
        list.addAll(allEventsQueue);
        if (queues.containsKey(eventId))
            list.addAll((List) queues.get(eventId));
        return list;
    }

    /**
     * Return listeners that should be notified of given event ID.  @param eventId
     *
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set getUniqueListeners(String eventId) {
        List list = getListeners(eventId);
        Set set = new HashSet(list.size());
        set.addAll(list);
        return set;
    }
}
