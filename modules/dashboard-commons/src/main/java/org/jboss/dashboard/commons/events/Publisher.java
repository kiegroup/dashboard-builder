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
package org.jboss.dashboard.commons.events;

import java.util.*;
import java.lang.ref.WeakReference;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;

/**
 * The publisher party int the publish/subscribe GoF design pattern.
 * <p>WARNING: In order to solve the -<i>lapsed listener</i>- problem the reference
 * to the subscriber stored by the Publisher is weak.
 * This it means that if the Subscriber reference is lost then is also automatically removed from the
 * subscribers list. To avoid this issue ensure that your Subscriber instance is referenced in your object model
 * and exists as long as the Publisher is alive.
 */
public class Publisher {

    /**
     * The event representing all events.
     * <p>NOTE: <code>EVENT_ALL=0</code> is a reserved identifier.
     */
    public static final int EVENT_ALL = 0;

    /**
     * The registered subscribers grouped by eventId.
     */
    protected Map subscribers;

    public Publisher() {
        subscribers = Collections.synchronizedMap(new HashMap());
    }

    /**
     * Register a subscriber interested in ALL events.
     */
    public synchronized void subscribe(Subscriber subscriber) {
        if (subscriber == null) return;
        subscribe(subscriber, EVENT_ALL);
    }

    /**
     * Register a subscriber interested in a single event.
     * @param eventId The event interested in.
     * <p>NOTE: <code>EVENT_ALL=0</code> is a reserved identifier.
     */
    public synchronized void subscribe(Subscriber subscriber, int eventId) {
        if (subscriber == null) return;

        // Discard duplicates.
        unsubscribe(subscriber, eventId);

        // Avoid subscribers to be "leaked" by the process manager.
        // Solve the -lapsed listener- problem.
        List eventSubscribers = (List) subscribers.get(new Integer(eventId));
        if (eventSubscribers == null) {
            eventSubscribers = Collections.synchronizedList(new ArrayList());
            subscribers.put(new Integer(eventId), eventSubscribers);
        }
        eventSubscribers.add(new WeakReference(subscriber));
    }

    /**
     * Subscribe a collection subscribers for a concrete event.
     */
    public synchronized void subscribe(Collection c, int eventId) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            this.subscribe((Subscriber) it.next(), eventId);
        }
    }

    /**
     * Removes a registered subscriber.
     */
    public synchronized void unsubscribe(Subscriber subscriber) {
        if (subscriber == null) return;

        Iterator it = subscribers.keySet().iterator();
        while (it.hasNext()) {
            Integer eventId = (Integer) it.next();
            unsubscribe(subscriber, eventId.intValue());
        }
    }

    /**
     * Removes a registered subscriber.
     * @param eventId The event interested in.
     */
    public synchronized void unsubscribe(Subscriber subscriber, int eventId) {
        if (subscriber == null) return;

        List eventSubscribers = (List) subscribers.get(new Integer(eventId));
        if (eventSubscribers == null) return;
        Iterator it = eventSubscribers.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
            Subscriber regSubscr = (Subscriber) wr.get();
            if (regSubscr == null || regSubscr.equals(subscriber)) it.remove();
        }
    }

    /**
     * Retrieve registered subscribers.
     * @return A map of eventId (Integer) / Subscriber (List).
     * If no subscribers are registered for an event then no entry is returned.
     */
    public synchronized Map getSubscribers() {
        Map results = new HashMap();
        Iterator it = subscribers.keySet().iterator();
        while (it.hasNext()) {
            Integer eventId = (Integer) it.next();
            results.put(eventId, getSubscribers(eventId.intValue()));
        }
        return results;
    }

    /**
     * Retrieve registered Subscribers for a specified event.
     */
    public synchronized List getSubscribers(int eventId) {
        List results = Collections.synchronizedList(new ArrayList());
        List eventSubscribers = (List) subscribers.get(new Integer(eventId));
        if (eventSubscribers == null) return results;

        Iterator it = eventSubscribers.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
            Subscriber subscriber = (Subscriber) wr.get();
            if (subscriber == null) it.remove();
            else results.add(subscriber);
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * Interface used to fire events.
     * @param eventInfo The object where event occurs.
     */
    public void notifyEvent(final int eventId, final Object eventInfo) {
        // Calculate the target subscribers.
        // Those subscribed to ALL_EVENT must also be taken.
        Set _subscribersToNotify = new HashSet();
        _subscribersToNotify.addAll(getSubscribers(eventId));
        _subscribersToNotify.addAll(getSubscribers(EVENT_ALL));
        Iterator it = _subscribersToNotify.iterator();
        while (it.hasNext()) {
            try {
                final Subscriber subscriber = (Subscriber) it.next();
                switch (subscriber.getTransactionContext()) {

                    // Notify to subscriber right now within the current transaction and thread.
                    case Subscriber.TXCONTEXT_DEFAULT:
                        subscriber.notifyEvent(eventId, eventInfo);
                        break;

                        // Notify to subscriber just before the current transaction completes succesfully.
                        case Subscriber.TXCONTEXT_BEFORE_COMMIT:
                            new HibernateTxFragment(false, true) {
                            protected void beforeCommit() throws Exception {
                                subscriber.notifyEvent(eventId, eventInfo);
                            }}.execute();
                            break;

                    // Notify to subscriber after the current transaction completes succesfully.
                    case Subscriber.TXCONTEXT_AFTER_COMMIT:
                        new HibernateTxFragment(false, true) {
                        protected void afterCommit() throws Exception {
                            subscriber.notifyEvent(eventId, eventInfo);
                        }}.execute();
                        break;

                    // Notify to subscriber within a new transaction launched in a separated thread.
                    case Subscriber.TXCONTEXT_NEW_THREAD:
                        new HibernateTxFragment(false, true) {
                        protected void afterCommit() throws Exception {
                            new Thread(new Runnable() {
                            public void run() {
                                subscriber.notifyEvent(eventId, eventInfo);
                            }}).start();
                        }}.execute();
                        break;

                    // Not supported notification mode.
                    default:
                        throw new IllegalArgumentException("Subscriber notification mode not supported: " + subscriber.getTransactionContext());
                }
            }
            catch (Throwable e) {
                if (e instanceof RuntimeException) throw (RuntimeException) e;
                else throw new RuntimeException("Exception occurred when firing event: " + eventId, e);
            }
        }
    }
}
