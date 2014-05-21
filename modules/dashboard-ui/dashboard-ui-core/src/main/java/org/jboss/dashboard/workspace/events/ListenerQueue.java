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

import java.util.List;
import java.util.Set;

/**
 * Date: 27-may-2004
 * Time: 17:53:07
 * @param <T> type of event listener this queue can hold
 */
public interface ListenerQueue<T extends EventListener> {
    /**
     * Adds a listener for all queues
     *
     * @param listener EventListener to add
     */
    public void addListener(T listener);

    /**
     * Adds a listener to the queue for events with given id
     *
     * @param listener EventListener to add
     * @param eventId  Event id the listener is interested in.
     */
    public void addListener(T listener, String eventId);

    /**
     * Removes Listener from all queues
     *
     * @param listener listener EventListener to remove
     */
    public void removeListener(T listener);

    /**
     * Removes a Listener from given queue
     *
     * @param listener listener EventListener to remove
     * @param eventId  Event id queue to remove listener from.
     */
    public void removeListener(T listener, String eventId);

    /**
     * Return listeners that should be notified of given event ID. May contain
     * duplicates.
     *
     * @param eventId
     * @return A List of listeners
     */
    public List<T> getListeners(String eventId);

    /**
     * Return listeners that should be notified of given event ID.
     *
     * @param eventId
     * @return A Set with listeners that should be notified of givent event id.
     */
    public Set<T> getUniqueListeners(String eventId);
}
