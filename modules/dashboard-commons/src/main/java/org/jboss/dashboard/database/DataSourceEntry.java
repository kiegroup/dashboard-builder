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
package org.jboss.dashboard.database;

import org.jboss.dashboard.commons.security.password.PasswordObfuscator;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.Session;
import java.sql.Connection;

/**
 * A database connection definition either in plain JDBC or JNDI.
 */
public abstract class DataSourceEntry implements Persistent {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DataSourceEntry.class.getName());

    private Long dbid;
    private String name;
    private String jndiPath;
    private String url;
    private String driverClass;
    private String obfuscatedUserName;
    private String obfuscatedPassword;
    private String testQuery;

    protected DataSourceEntry() {
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getJndiPath() {
        return jndiPath;
    }

    public void setJndiPath(String jndiPath) {
        this.jndiPath = jndiPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return PasswordObfuscator.lookup().deobfuscate(obfuscatedPassword);
    }

    public void setPassword(String password) {
        this.obfuscatedPassword = PasswordObfuscator.lookup().obfuscate(password);
    }

    protected String getObfuscatedPassword() {
        return obfuscatedPassword;
    }

    protected void setObfuscatedPassword(String obfuscatedPassword) {
        this.obfuscatedPassword = obfuscatedPassword;
    }

    public String getTestQuery() {
        return testQuery;
    }

    public void setTestQuery(String testQuery) {
        this.testQuery = testQuery;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return PasswordObfuscator.lookup().deobfuscate(obfuscatedUserName);
    }

    public void setUserName(String userName) {
        this.obfuscatedUserName = PasswordObfuscator.lookup().obfuscate(userName);
    }

    protected String getObfuscatedUserName() {
        return obfuscatedUserName;
    }

    protected void setObfuscatedUserName(String obfuscatedUserName) {
        this.obfuscatedUserName = obfuscatedUserName;
    }

    public abstract Connection getConnection() throws Exception;

    // Persistent interface.

    public boolean isPersistent() {
        return dbid != null;
    }

    public void save() throws Exception {
        if (isPersistent()) update();
        else saveOrUpdate();
    }

    public void update() throws Exception {
        if (!isPersistent()) save();
        else saveOrUpdate();
    }

    public void delete() throws Exception {
        persist(2);
    }

    protected boolean saveOrUpdate() throws Exception {
        boolean isTransient = !isPersistent();
        if (isTransient) persist(0);
        else persist(1);
        return isTransient;
    }

    protected void persist(final int op) throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            Object obj = DataSourceEntry.this;
            switch(op) {
                case 0:
                    session.save(obj);
                    break;
                case 1:
                    session.update(obj);
                    break;
                case 2:
                    session.delete(obj);
                    break;
            }
            session.flush();
        }}.execute();
    }
}
