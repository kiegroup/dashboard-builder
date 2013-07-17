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

import org.hibernate.jdbc.Work;
import org.jboss.dashboard.profiler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.jboss.dashboard.commons.misc.ReflectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>A transaction fragment is a block of code executed inside the scope of a global transaction.
 * <p> Pattern:
 * <pre>
 * // Define the transaction fragment
 * <b>final</b> String <b>finalVar</b> = "";
 * new HibernateTxFragment() {
 * new HibernateTxFragment(false, true) { // Activate callback methods afterXXX/beforeXXX.
 * new HibernateTxFragment(true, false) { // Open a brand new transaction.
 * protected void txFragment(Session session) throws Exception {
 *   // Transactional block
 *   // ...
 *   <b>finalVar</b> = ""; // Forbidden sentence
 * }}.execute();
 * </pre>
 * <p>IMPORTANT NOTE: All variables accessed inside the transaction fragment block
 * must be defined as <i>final</i> because a inner class can't change the reference to
 * a variable defined into its scope.
 * <p> Another issue to be considered is what happens when a fragment needs to return a
 * value from <i>txFragment</i> method. Use an array or collection to store the return value(s):
 * <pre>
 * final Object[] <b>result</b> = new Object[] {null};
 * new HibernateTxFragment() {
 * protected void txFragment(Session session) throws Exception {
 *    // ...
 *    <b>result</b>[0] = ...;
 * }}.execute();
 * return <b>result</b>[0];
 * </pre>
 * <p>The variable result can be an array or a collection or any other structure where we can
 * store the results to be returned by the fragment block.
 */
public abstract class HibernateTxFragment {

    private static transient Logger log = LoggerFactory.getLogger(HibernateTxFragment.class.getName());

    /**
     * Flag to prevent double execution
     */
    private boolean alreadyExecuted;

    /**
     * Flag to control the reuse of the current transaction.
     * If true then a brand new transaction is opened and added to the list of transactions linked with the current thread.
     */
    protected boolean newTransactionRequested;

    /**
     * Flag to control the Hibernate flush.
     * If true a <code>Session.flush()</code> call will be performed just before the finish of the tx fragment.
     */
    protected boolean flushAfterFinish;

    /**
     * If true then this fragment will receive callback invocations both before and after the transaction completion.
     */
    protected boolean callbacksEnabled;

    /**
     * The parent fragment. It only applies for child fragments.
     */
    protected HibernateTxFragment parentFragment;

    /**
     * Create a new fragment linked to existing transaction if any
     */
    public HibernateTxFragment() {
        this(false, false);
    }

    /**
     * Create a new fragment
     *
     * @param newTransactionRequested set to false to make it linked to existing transaction if any, true to be run in a new transaction
     */
    public HibernateTxFragment(boolean newTransactionRequested) {
        this(newTransactionRequested, false);
    }

    /**
     * Create a new fragment
     *
     * @param newTransactionRequested set to false to make it linked to existing transaction if any, true to be run in a new transaction
     * @param callbacksEnabled If true then this fragment will receive callback invocations both before and after the transaction completion.
     */
    public HibernateTxFragment(boolean newTransactionRequested, boolean callbacksEnabled) {
        this(newTransactionRequested, callbacksEnabled, false);
    }

    /**
     * Create a new fragment
     *
     * @param newTransactionRequested set to false to make it linked to existing transaction if any, true to be run in a new transaction
     * @param callbacksEnabled If true then this fragment will receive callback invocations both before and after the transaction completion.
     * @param flushAfterFinish If true a <code>Session.flush()</code> call will be performed just before the finish of the tx fragment.
     */
    public HibernateTxFragment(boolean newTransactionRequested, boolean callbacksEnabled, boolean flushAfterFinish) {
        this.newTransactionRequested = newTransactionRequested;
        this.callbacksEnabled = callbacksEnabled;
        this.flushAfterFinish = flushAfterFinish;
        this.alreadyExecuted = false;
        this.parentFragment = null;
    }

    /**
     * Enable callback method notifications: <code>beforeCommit, afterCommit</code> will be invoked if the transaction is commited
     * and <code>beforeRollback, afterRollback</code> otherwise.
     */
    protected void registerForCallbackNotifications() {
        callbacksEnabled = true;
    }

    /**
     * Mark the transaction as rollback only
     */
    protected void markAsRollbackOnly() {
        HibernateTransaction tx = HibernateTransaction.getCurrentTx();
        tx.setRollback(true);
    }

    /**
     * Mark the transaction as rollback only
     * @param t The rollback cause.
     */
    protected void markAsRollbackOnly(Throwable t) {
        HibernateTransaction tx = HibernateTransaction.getCurrentTx();
        tx.error(t);
    }

    /**
     * Execute this fragment.
     */
    public final void execute() throws Exception {
        if (alreadyExecuted) {
            log.error("Double execution of fragment is not allowed.");
        } else {
            HibernateTransaction tx = HibernateTransaction.getCurrentTx();
            if (!tx.isActive()) executeInitiator(tx);
            else executeChild(tx);
        }
    }

    protected final void executeInitiator(HibernateTransaction tx) throws Exception {
        // Begin the tx
        tx.begin();
        CodeBlockTrace trace = new HibernateTxTrace(tx).begin();
        try {
            // Execute the tx fragment
            alreadyExecuted = true;
            tx.executeFragment(this);
        } finally {
            // Complete the tx
            tx.complete();
            trace.end();
        }
    }

    protected final void executeChild(HibernateTransaction tx) throws Exception {
        if (newTransactionRequested) {
            // Nested tx's are deferred until the end of the current tx.
            tx.followers.add(this);
        } else {
            // If it is just a child fragment then execute it right now.
            alreadyExecuted = true;
            tx.executeFragment(this);
        }
    }

    /**
     * Custom fragment implementation.
     */
    protected void txFragment(Session session) throws Throwable {
    }

    /**
     * Callback method invoked before the transaction is commited.
     */
    protected void beforeCommit() throws Throwable {
    }

    /**
     * Callback method invoked before the transaction is rolled back.
     */
    protected void beforeRollback() throws Throwable {
    }

    /**
     * Callback method invoked after a transaction commit.
     */
    protected void afterRollback() throws Throwable {
    }

    /**
     * Callback method invoked after a transaction rollback.
     */
    protected void afterCommit() throws Throwable {
    }

    /** Transaction trace */
    class HibernateTxTrace extends CodeBlockTrace {

        public static final String CONNECTION_ID = "Tx Connection id";
        public static final String PROCESS_ID = "Tx Process id";
        public static final String TX_ISOLATION = "Tx Isolation";
        public static final String AUTO_COMMIT = "Tx Auto commit";

        protected Map<String,Object> context;

        public HibernateTxTrace(HibernateTransaction tx) throws Exception {
            super(Integer.toString(tx.hashCode()));
            context = buildContext(tx);
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.TRANSACTION;
        }

        public String getDescription() {
            return "Transaction " + id;
        }

        public Map<String,Object> getContext() {
            return context;
        }

        public Map<String,Object> buildContext(HibernateTransaction tx) throws Exception {
            final Map<String,Object> ctx = new LinkedHashMap<String,Object>();
            tx.getSession().doWork(new Work() {
            public void execute(Connection conn) throws SQLException {

                // Generic
                ctx.put("Tx id", id);
                ctx.put(TX_ISOLATION, Integer.toString(conn.getTransactionIsolation()));
                ctx.put(AUTO_COMMIT, conn.getAutoCommit());

                // SQLServer-specific
                Object tdsChannel = ReflectionUtils.getPrivateField(conn, "tdsChannel");
                if (tdsChannel != null) {
                    Object spid = ReflectionUtils.getPrivateField(tdsChannel, "spid");
                    if (spid != null) ctx.put(PROCESS_ID, spid.toString());
                }
                // SQLServer-specific
                Object connId = ReflectionUtils.getPrivateField(conn, "connectionID");
                if (connId == null) connId = ReflectionUtils.getPrivateField(conn, "traceID");
                if (connId != null) {
                    ctx.put(CONNECTION_ID, connId.toString());
                }

                ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
                if (threadProfile != null) threadProfile.addContextProperties(ctx);
            }});
            return ctx;
        }
    }
}
