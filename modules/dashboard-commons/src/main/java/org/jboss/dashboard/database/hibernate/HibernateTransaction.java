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
package org.jboss.dashboard.database.hibernate;

import org.hibernate.*;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.FactoryWork;
import org.jboss.dashboard.error.ErrorManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.dashboard.CoreServices;

import java.util.ArrayList;
import java.util.List;

/**
 * A Hibernate transaction in the platform is always tied to a single thread.
 * Every time a thread is executed an instance of this class is created
 * and bounded to the current thread. The standard way to interact with the
 * underlying transaction is to make use of the HibernateTxFragment idiom.
 *
 * @see org.jboss.dashboard.database.hibernate.HibernateTxFragment
 */
public class HibernateTransaction {

    /** Logger */
    private static transient Log log = LogFactory.getLog(HibernateTransaction.class.getName());

    /** Current active transaction per thread */
    private static transient ThreadLocal<HibernateTransaction> activeTx = new ThreadLocal<HibernateTransaction>();

    /**
     * Get the current tx associated with the calling thread.
     * This method ensures that a tx instance is always available.
     */
    public static HibernateTransaction getCurrentTx() {
        HibernateTransaction tx = activeTx.get();
        if (tx == null) activeTx.set(tx = new HibernateTransaction());
        return tx;
    }

    /**
     * The transaction identifier.
     */
    private String id;

    /**
     * The current tx fragment being processed by the transaction.
     */
    HibernateTxFragment currentFragment;

    /**
     * Children listeners that were opened within this fragment interested in transaction callbacks.
     */
    List<HibernateTxFragment> listeners;

    /**
     * List of "marked new" transactions, that will be executed AFTER this tx is commited or rolled back.
     */
    List<HibernateTxFragment> followers;

    /**
     * Hibernate session associated with the transaction.
     */
    private Session session;

    /**
     * Flag indicating i the transaction has been initiated.
     */
    private boolean active;

    /**
     * Flag indicating if the transaction has been marked as rollback only.
     */
    private boolean rollback;

    /**
     * Status flag indicating if the transaction is has started the completion phase..
     */
    private boolean completing;

    /**
     * The underlying Hibernate transaction
     */
    protected Transaction tx;

    private HibernateTransaction() {
        this.id = Thread.currentThread().getName();
        this.currentFragment = null;
        this.followers = new ArrayList<HibernateTxFragment>();
        this.listeners = new ArrayList<HibernateTxFragment>();
        this.session = null;
        this.tx = null;
        this.active = false;
        this.rollback = false;
        this.completing = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public boolean isActive() {
        return active;
    }

    /** Begin the transaction */
    public void begin() throws Exception {
        try {
            log.debug("Begin transaction. Id=" + getId());
            HibernateSessionFactoryProvider hibernateSessionFactoryProvider = CoreServices.lookup().getHibernateSessionFactoryProvider();
            SessionFactory sessionFactory = hibernateSessionFactoryProvider.getSessionFactory();
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            active = true;
        } catch (HibernateException e) {
            error(e);
            throw e;
        }
    }

    /** Complete the transaction */
    public void complete() {
        // Flush the session before notifying the listeners
        if (!rollback) {
            flush();
        }

        // Invoke listeners
        completing = true;
        notifyListeners(true);

        // Commit or rollback
        if (rollback) rollback();
        else commit();

        // Close the transaction and unbound it from the current thread
        completing = false;
        active = false;
        close();
        tx = null;
        activeTx.set(null);

        // Invoke listeners
        notifyListeners(false);

        // Process queued txs.
        processFollowers();
    }

    /** Flush the transaction */
    protected void flush() {
        try {
            log.debug("Flush transaction. Id=" + getId());
            session.flush();
        } catch (Throwable e) {
            log.debug("Flush error. Id=" + getId());
            error(e);
        }
    }

    /** Flush the transaction */
    protected void close() {
        try {
            log.debug("Close transaction. Id=" + getId());
            if (session.isOpen()) {
                session.close();
            }
        } catch (Throwable e) {
            log.error("Close error. Id=" + getId());
            error(e);
        }
    }

    /** Rollback the transaction */
    protected void rollback() {
        try {
            log.debug("Rollback transaction. Id=" + getId());
            tx.rollback();
        } catch (Throwable e) {
            log.error("Error in rollback. Id=" + getId());
            rollback = false;
            error(e);
        }
    }

    /** Commit the transaction */
    protected void commit() {
        try {
            log.debug("Commit transaction. Id=" + getId());
            tx.commit();
        } catch (Throwable e) {
            log.error("Error in commit. Id=" + getId());
            error(e);
        }
    }

    /** Exception occurred during a transaction fragment */
    public void error(Throwable t) {
        if (!rollback) {
            // Mark the transaction as rollback only.
            rollback = true;

            // Notify the error.
            ErrorManager.lookup().notifyError(t, true);
        }
    }

    protected final void executeFragment(HibernateTxFragment fragment) throws Exception {
        FlushMode flushMode = session.getFlushMode();
        boolean flushChanged = false;
        try {
            // Change the current fragment.
            fragment.parentFragment = currentFragment;
            currentFragment = fragment;

            // Disable flush if the fragment's flush is set.
            HibernateTxFragment flusherFragment = getFlusherFragment();
            if (fragment == flusherFragment) {
                session.setFlushMode(FlushMode.COMMIT);
                flushChanged = true;
            }

            // Execute the fragment.
            fragment.txFragment(session);

            // Flush the fragment if required.
            if (fragment == flusherFragment) {
                log.debug("Flush transaction. Id=" + getId());
                session.flush();
            }
        } catch (Throwable t) {
            // Rollback the tx.
            error(t);

            // Propagate the exception.
            if (t instanceof Exception) throw (Exception) t;
            else throw new Exception(t);
        } finally {
            if (flushChanged) session.setFlushMode(flushMode);
            currentFragment = fragment.parentFragment;
            if (fragment.callbacksEnabled) {
                listeners.add(fragment);

                // When completing the tx notify the fragment right now.
                if (completing) notifyListener(true, fragment);
            }
        }
    }

    /**
     * Get the first fragment in the chain than has the flush flag enabled.
     */
    protected HibernateTxFragment getFlusherFragment() {
        HibernateTxFragment setter = null;
        HibernateTxFragment fragment = currentFragment;
        while (fragment != null) {
            if (fragment.flushAfterFinish) setter = fragment;
            fragment = fragment.parentFragment;
        }
        return setter;
    }

    /**
     * Invoke the callbacks on listeners
     */
    protected void notifyListeners(boolean before) {
        if (!listeners.isEmpty()) log.debug((before ? "Before " : "After ") + (rollback ? "rollback" : "commit"));

        // A copy of the list is needed because a listener could modify the list if it contains a txFragment with callbacks.
        for (HibernateTxFragment listener : new ArrayList<HibernateTxFragment> (listeners)) {
            boolean wasCommit = !rollback;
            notifyListener(before, listener);

            // If the listener execution aborts the tx then the notifyBeforeRollback must be sent to all the listeners.
            if (before && wasCommit && rollback) {
                notifyListeners(true);

                // The current notifyBeforeCommit notifications must be cancelled.
                break;
            }
        }
    }

    /**
     * Invoke the callbacks on a listener
     * @return true if the transaction has been aborted due to a failure in the listener execution.
     */
    protected void notifyListener(boolean before, HibernateTxFragment listener) {
        if (before) {
            if (rollback) notifyBeforeRollback(listener);
            else notifyBeforeCommit(listener);
        } else {
            if (rollback) notifyAfterRollback(listener);
            else notifyAfterCommit(listener);
        }
    }

    /**
     * Call the followers (new transactions opened within this one, with the "new tx" flag set).
     */
    private void processFollowers() {
        for (HibernateTxFragment fragment : followers) {
            try {
                log.debug("Follower");
                fragment.execute();
            } catch (Throwable e) {
                log.error("Follower error. Id=" + getId(), e);
            }
        }
    }

    protected void notifyBeforeCommit(HibernateTxFragment fragment) {
        try {
            fragment.beforeCommit();
        } catch (Throwable t) {
            // If it fails then the commit must be aborted.
            error(t);
        }
    }

    protected void notifyBeforeRollback(HibernateTxFragment fragment) {
        try {
            fragment.beforeRollback();
        } catch (Throwable e) {
            log.error("Error before rollback: ", e);
        }
    }

    protected void notifyAfterCommit(HibernateTxFragment fragment) {
        try {
            fragment.afterCommit();
        } catch (Throwable e) {
            log.error("Error after commit: ", e);
        }
    }

    protected void notifyAfterRollback(HibernateTxFragment fragment) {
        try {
            fragment.afterRollback();
        } catch (Throwable e) {
            log.error("Error after rollback: ", e);
        }
    }

    public static Object runWork(final HibernateWork work) throws Throwable {
        final Throwable[] error = new Throwable[] {null};
        final Object[] result = new Object[] {null};
        Factory.doWork(new FactoryWork() {
        public void doWork() {
            try {
                new HibernateTxFragment() {
                protected void txFragment(Session session) throws Throwable {
                    result[0] = work.doWork(session);
                }}.execute();
            } catch (Throwable e) {
                error[0] = e;
            }
        }});
        if (error[0] != null) throw error[0];
        else return result[0];
    }
}
