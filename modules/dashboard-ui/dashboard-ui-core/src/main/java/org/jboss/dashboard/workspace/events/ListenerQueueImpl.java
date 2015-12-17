/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 27-may-2004
 * Time: 18:02:57
 * @param <T> Type of EventListener this queue can hold
 */
public class ListenerQueueImpl<T extends EventListener> implements ListenerQueue<T> {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ListenerQueueImpl.class.getName());

    protected Map<String, List<T>> queues = new HashMap<String, List<T>>();
    protected List<T> allEventsQueue = new ArrayList<T>();

    public ListenerQueueImpl() {
    }

    /**
     * Adds a listener for all queues
     *
     * @param listener EventListener to add
     */
    public synchronized void addListener(T listener) {
        allEventsQueue.add(listener);
    }

    /**
     * Adds a listener to the queue for events with given id
     *
     * @param listener EventListener to add
     * @param eventId  Event id the listener is interested in.
     */
    public synchronized void addListener(T listener, String eventId) {
        List<T> list = queues.get(eventId);
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.add(listener);
        queues.put(eventId, list);
    }

    /**
     * Removes Listener from all queues
     *
     * @param listener listener EventListener to remove
     */
    public synchronized void removeListener(T listener) {
        allEventsQueue.remove(listener);
        for (List<T> list : queues.values()) {
            list.remove(listener);
        }
    }

    /**
     * Removes a Listener from given queue
     *
     * @param listener listener EventListener to remove
     * @param eventId  Event id queue to remove listener from.
     */
    public synchronized void removeListener(T listener, String eventId) {
        List<T> list = queues.get(eventId);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Return listeners that should be notified of given event ID. May contain
     * duplicates.
     *
     * @param eventId
     * @return A List of listeners
     */
    public List<T> getListeners(String eventId) {
        List<T> list = new ArrayList<T>(allEventsQueue);
        if (queues.containsKey(eventId)) {
            list.addAll(queues.get(eventId));
        }
        return list;
    }

    /**
     * Return listeners that should be notified of given event ID.  @param eventId
     *
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set<T> getUniqueListeners(String eventId) {
        return new HashSet<T>(getListeners(eventId));
    }
}
