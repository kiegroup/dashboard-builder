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
package org.jboss.dashboard.commons.events;

import java.util.EventListener;

/**
 * The subscriber party int the publish/subscribe GoF design pattern.
 */
public interface Subscriber extends EventListener {

    /**
     * Event notifications are performed when requested within the publisher calling thread and transaction.
     * Regardless the current transaction is completed or rolled back the notification is sent.
     */
    int TXCONTEXT_DEFAULT = 1;

    /**
     * Events notifications are performed within the publisher calling thread and
     * just before the current transaction is going to be committed. Calls to the persistence layer
     * are executed within the current transaction.
     */
    int TXCONTEXT_BEFORE_COMMIT = 2;

    /**
     * Events notifications are performed within the publisher calling thread and only after
     * the current transaction completes successfully. Calls to the persistence layer
     * are executed in a different transaction.
     */
    int TXCONTEXT_AFTER_COMMIT = 3;

    /**
     * Same conditions than AFTER_COMMIT but in addition a new thread separated from
     * the publisher calling thread is created. Calls to the persistence layer
     * are executed in a different transaction and thread.
     */
    int TXCONTEXT_NEW_THREAD = 4;

    /**
     * Specify the transaction context in which event notifications must be sent to this subscriber.
     * @return See above <code>TXCTX_</code> constants defined.
     */
    int getTransactionContext();

    /**
     * An event is notified by the Publisher to this Subscriber.
     * @param eventId The id. of the event.
     * @param eventInfo The event related information object.
     */
    void notifyEvent(int eventId, Object eventInfo);
}
