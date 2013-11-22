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
package org.jboss.dashboard.ui.panel.dataSourceManagement;

import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.LocaleManager;

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.database.DataSourceEntry;
import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.database.JDBCDataSourceEntry;
import org.jboss.dashboard.database.JNDIDataSourceEntry;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class DataSourceManagementHandler extends HandlerFactoryElement {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataSourceManagementHandler.class.getName());

    public static final String PARAM_DS_NAME = "_dsname";
    public static final String RESULT_OK = "_result_OK";
    public static final String JNDI_TYPE = "jndi";
    public static final String CUSTOM_TYPE = "custom";
    private static final int   TABLE_COLUMN = 1;
    private static final int   TABLE_SCHEMA = 2;
    private static final int   TABLE_NAME = 3;
    private static final int   TABLE_TYPE = 4;

    public static final String PARAM_PASSW_CHANGED = "passChanged";
    public static final String PARAM_DEFAULT_PASS_VALUE = "!///--/!";
    private static final int   COLUMN_TABLE_NAME = 3;
    private static final int   COLUMN_NAME = 4;
    private static final int   COLUMN_DATA_TYPE = 5;

    private boolean isCreating = false;
    private boolean EDIT_MODE = false;
    private String DS_EDIT;

    private String TEST_RESULT;
    private boolean TEST_MODE;

    private boolean INTROSPECT_MODE = false;
    private String INTROSPECT_RESULT;

    private String name;
    private String jndiPath;
    private String url;
    private String driverClass;
    private String userName;
    private String password;
    private String testQuery;
    private String type;
    private String selectedTables;

    /** The locale manager. */
    protected LocaleManager localeManager;

    public DataSourceManagementHandler() {
        localeManager = LocaleManager.lookup();
    }

    public boolean isCreating() {
        return isCreating;
    }

    public void setCreating(boolean creating) {
        isCreating = creating;
    }

    public DataSourceManager getDataSourceManager() {
        return CoreServices.lookup().getDataSourceManager();
    }

    public boolean getTEST_MODE() {
        return TEST_MODE;
    }

    public void setTEST_MODE(boolean TEST_MODE) {
        this.TEST_MODE = TEST_MODE;
    }

    public String getTEST_RESULT() {
        return TEST_RESULT;
    }

    public void setTEST_RESULT(String TEST_RESULT) {
        this.TEST_RESULT = TEST_RESULT;
    }

    public String getDS_EDIT() {
        return DS_EDIT;
    }

    public void setDS_EDIT(String DS_EDIT) {
        this.DS_EDIT = DS_EDIT;
    }

    public boolean isEDIT_MODE() {
        return EDIT_MODE;
    }

    public void setEDIT_MODE(boolean EDIT_MODE) {
        this.EDIT_MODE = EDIT_MODE;
    }
    public boolean getEDIT_MODE(){
        return this.EDIT_MODE;
    }

    public void setINTROSPECT_RESULT(String result){
        this.INTROSPECT_RESULT = result;
    }

    public String getINTROSPECT_RESULT(){
        return this.INTROSPECT_RESULT;
    }

    public void setINTROSPECT_MODE(boolean mode){
        this.INTROSPECT_MODE = mode;
    }

    public boolean getINTROSPECT_MODE(){
        return this.INTROSPECT_MODE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJndiPath() {
        return jndiPath;
    }

    public void setJndiPath(String jndiPath) {
        this.jndiPath = jndiPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSelectedTables(){
        return this.selectedTables;
    }

    public void setSelectedTables(String selectedTables){
        this.selectedTables = selectedTables;
    }

    public String getTestQuery() {
        return testQuery;
    }

    public void setTestQuery(String testQuery) {
        this.testQuery = testQuery;
    }

    /**
     * Get all tables of the datasource selected.
     */
    public List getIntrospectedTables( String datasource) throws Exception {
        List result = new ArrayList();
        Connection connection = getConnection();
        ResultSet tables = null;
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            String catalog = connection.getCatalog();
            String[] types = { "TABLE" };
            tables = metadata.getTables(catalog, null, "%", types);
            while (tables.next()) {
                String schema = tables.getString(TABLE_SCHEMA);
                String tableName = tables.getString(TABLE_NAME);

                DataSourceTableEntry dataSourceTableEntry = new DataSourceTableEntry();
                dataSourceTableEntry.setDatasource(getName());
                dataSourceTableEntry.setName(tableName);
                result.add( dataSourceTableEntry );
            }
        }
        finally {
            try {
                if (tables != null) tables.close();
                if (connection != null) connection.close();
            }
            catch (SQLException ignore) {}
        }
        return result;
    }

    private List getTableColumns(String tableName) throws Exception {
        List result = new ArrayList();
        Connection connection = getConnection();
        ResultSet columns = null;
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            columns = metadata.getColumns(null, "%", tableName , "%");

            while (columns.next()) {
                DataSourceColumnEntry columnEntry = new DataSourceColumnEntry();
                columnEntry.setDatasource( getName() );
                columnEntry.setTableName(tableName);
                columnEntry.setSqltype( columns.getShort(COLUMN_DATA_TYPE) );
                columnEntry.setName( columns.getString(COLUMN_NAME) );
                columnEntry.setIdentity("false");
                columnEntry.setPrimaryKey("false");
                result.add(columnEntry);
            }
            return result;
        } finally {
            try {
                if (columns != null) columns.close();
                if (connection != null) connection.close();
            }
            catch (SQLException ignore) {}
        }
    }

    public Connection getConnection() throws Exception {
        if (getType().equals(JNDI_TYPE)) {
            JNDIDataSourceEntry jndi = new JNDIDataSourceEntry();
            jndi.setJndiPath(getJndiPath());
            return jndi.getConnection();
        }
        if (getType().equals(CUSTOM_TYPE)) {
            JDBCDataSourceEntry custom = new JDBCDataSourceEntry();
            custom.setUrl(getUrl());
            custom.setDriverClass(getDriverClass());
            custom.setUserName(getUserName());
            custom.setPassword(getPassword());
            return custom.getConnection();
        }
        return null;
    }

    public void actionCreateNewDatasource(CommandRequest request) throws Exception {
        clearParametersHandler();
        setCreating(true);
    }

    public void actionCreateDatasource(CommandRequest request) throws Exception {
        String checkingDS = request.getRequestObject().getParameter("checkingDS");
        String passwordChanged = request.getRequestObject().getParameter(PARAM_PASSW_CHANGED);
        if (checkingDS != null && "true".equals(checkingDS)) {
            actionCheckDataSource(request);
            return;
        }
        validate(getType());
        if (!getFieldErrors().isEmpty()) {
            return;
        }
        DataSourceEntry dSource = getDataSourceManager().getDataSourceEntry(getName());
        if (dSource != null && !isEDIT_MODE()) {
            addFieldError(new FactoryURL(getComponentName(), "name"), null, getName());
            return;
        }

        // Edit the DataSource
        if (isEDIT_MODE()) {
            dSource = getDataSourceManager().getDataSourceEntry(getDS_EDIT());
            if (dSource != null) {
                if (dSource instanceof JNDIDataSourceEntry && getType().equals(CUSTOM_TYPE)) {
                    setJndiStatusError();
                } else if (dSource instanceof JDBCDataSourceEntry && getType().equals(JNDI_TYPE)) {
                    setJdbcStatusError();
                } else if (dSource instanceof JNDIDataSourceEntry && getType().equals(JNDI_TYPE)) {
                    JNDIDataSourceEntry jndiDS = (JNDIDataSourceEntry) dSource;
                    fillValues(jndiDS);
                    jndiDS.save();
                } else if (dSource instanceof JDBCDataSourceEntry && getType().equals(CUSTOM_TYPE)) {
                    JDBCDataSourceEntry polyDS = (JDBCDataSourceEntry) dSource;
                    fillValues(polyDS);
                    if(polyDS.getPassword()==null ||
                            (passwordChanged!=null && "true".equals(passwordChanged))||
                            !PARAM_DEFAULT_PASS_VALUE.equals(getPassword())){
                        polyDS.setPassword(getPassword());
                    }
                    polyDS.save();
                }
            }
        }
        // Create a DataSource
        else {
            if (getType().equals(JNDI_TYPE)) {
                JNDIDataSourceEntry jndiDS = new JNDIDataSourceEntry();
                fillValues(jndiDS);
                jndiDS.save();

            } else if (getType().equals(CUSTOM_TYPE)) {
                JDBCDataSourceEntry polyDS = new JDBCDataSourceEntry();
                fillValues(polyDS);
                polyDS.setPassword(getPassword());
                polyDS.save();
            }
        }
        setEDIT_MODE(false);
        setCreating(false);
    }


    private void validate(String type) {
        clearFieldErrors();
        if (getName() == null || "".equals(getName())) {
            addFieldError(new FactoryURL(getComponentName(), "name"), null, getName());
        }

        if (type == null || "".equals(type)) {
            addFieldError(new FactoryURL(getComponentName(), "jdbc"), null, getType());
            addFieldError(new FactoryURL(getComponentName(), "jndi"), null, getType());
        } else {
            if (type.equals(CUSTOM_TYPE)) {

                if (getDriverClass() == null || "".equals(getDriverClass()))
                    addFieldError(new FactoryURL(getComponentName(), "driverClass"), null, getUrl());

                if (getUrl() == null || "".equals(getUrl()))
                    addFieldError(new FactoryURL(getComponentName(), "url"), null, getUrl());

                if (getUserName() == null || "".equals(getUserName()))
                    addFieldError(new FactoryURL(getComponentName(), "userName"), null, getUserName());

                // if (getPassword() == null || "".equals(getPassword()))
                //    addFieldError(new FactoryURL(getComponentName(), "password"), null, getPassword());

            } else if (type.equals(JNDI_TYPE)) {

                if (getJndiPath() == null || "".equals(getJndiPath()))
                    addFieldError(new FactoryURL(getComponentName(), "jndiPath"), null, getJndiPath());

            }
        }
        if (getTestQuery() == null || "".equals(getTestQuery()))
            addFieldError(new FactoryURL(getComponentName(), "testQuery"), null, getTestQuery());

    }

    private void setJndiStatusError() {
        addFieldError(new FactoryURL(getComponentName(), "jndi"), null, getType());
        setType(JNDI_TYPE);
     }

    private void setJdbcStatusError() {
        addFieldError(new FactoryURL(getComponentName(), "jdbc"), null, getType());
        setType(CUSTOM_TYPE);
    }

    private void fillValues(JNDIDataSourceEntry jndiDS) {
        jndiDS.setName(getName());
        jndiDS.setJndiPath(getJndiPath());
        jndiDS.setTestQuery(getTestQuery());
    }

    private void fillValues(JDBCDataSourceEntry polyDS) {
        polyDS.setName(getName());
        polyDS.setDriverClass(getDriverClass());
        polyDS.setUrl(getUrl());
        polyDS.setUserName(getUserName());
        polyDS.setTestQuery(getTestQuery());
    }

    private void clearParametersHandler() {
        setName("");
        setJndiPath("");
        setUrl("");
        setDriverClass("");
        setUserName("");
        setPassword("");
        setTestQuery("");
        setType(null);

        setDS_EDIT("");
        setEDIT_MODE(false);
        setCreating(false);
        clearTestParameters();
        clearIntrospectParameters();
    }

    private void clearTestParameters(){
        setTEST_RESULT("");
        setTEST_MODE(false);
    }

    private void clearIntrospectParameters(){
        setINTROSPECT_RESULT("");
        setINTROSPECT_MODE(false);
    }

    public void actionDeleteDataSource(CommandRequest request) throws Exception {
        final String dsName = request.getRequestObject().getParameter(PARAM_DS_NAME);
        if (dsName != null && !"".equals(dsName)) {
            final DataSourceEntry dSource = getDataSourceManager().getDataSourceEntry(dsName);
            if (dSource != null) {
                new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    // Delete aggregated table columns
                    String deleteHql = "delete from " + DataSourceColumnEntry.class.getName() + " where datasource = :datasource";
                    Query deleteQuery = session.createQuery(deleteHql);
                    deleteQuery.setString("datasource", dsName);
                    deleteQuery.executeUpdate();

                    // Delete aggregated tables
                    deleteHql = "delete from "+ DataSourceTableEntry.class.getName() + " where datasource = :datasource";
                    deleteQuery = session.createQuery(deleteHql);
                    deleteQuery.setString("datasource", dsName);
                    deleteQuery.executeUpdate();

                    // Delete the data source.
                    dSource.delete();
                }}.execute();
            }
        }
        setCreating(false);
        setEDIT_MODE(false);
    }

    public void actionEditDataSource(CommandRequest request) throws Exception {
        validate(getType());
        clearParametersHandler();
        String dsName = request.getRequestObject().getParameter(PARAM_DS_NAME);
        if (dsName != null && !"".equals(dsName)) {
            DataSourceEntry dSource = getDataSourceManager().getDataSourceEntry(dsName);
            if (dSource != null) {
                if (dSource instanceof JNDIDataSourceEntry) {
                    setJndiPath(dSource.getJndiPath());
                    setType(JNDI_TYPE);
                } else if (dSource instanceof JDBCDataSourceEntry) {
                    setUrl(dSource.getUrl());
                    setDriverClass(dSource.getDriverClass());
                    setUserName(dSource.getUserName());
                    setPassword(PARAM_DEFAULT_PASS_VALUE);
                    setType(CUSTOM_TYPE);
                }

                setName(dSource.getName());
                setTestQuery(dSource.getTestQuery());
                setEDIT_MODE(true);
                setDS_EDIT(dSource.getName());
                clearFieldErrors();
            }
        }
    }

    public void actionIntrospect(CommandRequest request) throws Exception {
        clearTestParameters();
        clearIntrospectParameters();
        actionCheckDataSource(request);
        setINTROSPECT_RESULT(getTEST_RESULT());
        setINTROSPECT_MODE(getTEST_MODE());
        clearTestParameters();
    }

    public void actionSaveChanges(CommandRequest request) throws Exception {
        //save datasourceEntry changes
        setSelectedTables(request.getRequestObject().getParameter("selectedTables"));
        actionCreateDatasource(request);
        //complete selected tables columns, delete previous introspect and save new introspect
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                String[] arrTableName=getSelectedTables().split(",");
                String deleteHql = "delete from "+DataSourceTableEntry.class.getName() + " where datasource = '"+getName()+"'";
                Query deleteQuery = session.createQuery(deleteHql);
                deleteQuery.executeUpdate();
                for(int i=0;i<arrTableName.length;i++){
                    DataSourceTableEntry tableEntry = new DataSourceTableEntry();
                    tableEntry.setDatasource(getName());
                    tableEntry.setName(arrTableName[i]);
                    tableEntry.setSelected("true");
                    session.saveOrUpdate(tableEntry);
                }

                deleteHql = "delete from " + DataSourceColumnEntry.class.getName() + " where datasource = '"+getName()+"'";
                deleteQuery = session.createQuery(deleteHql);
                deleteQuery.executeUpdate();
                for(int i=0;i<arrTableName.length;i++){
                    List tableColumns = getTableColumns(arrTableName[i]);
                    Iterator it = tableColumns.iterator();
                    while(it.hasNext()){
                        DataSourceColumnEntry columnEntry = (DataSourceColumnEntry) it.next();
                        session.saveOrUpdate(columnEntry);
                    }
                }
            }
        };
        txFragment.execute();
        clearParametersHandler();
        setCreating(false);
    }

    public void actionCancelEdit(CommandRequest request) throws Exception {
        clearFieldErrors();
        if (!getINTROSPECT_MODE()) clearParametersHandler();
        else clearIntrospectParameters();
    }

    public void actionCheckDataSource(CommandRequest request) {
        validate(getType());
        if (getFieldErrors().isEmpty()) {
            Connection conn = null;
            try {
                if (CUSTOM_TYPE.equals(getType()) && !getDataSourceManager().checkDriverClassAvailable(getDriverClass())) {
                    ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.panel.dataSourceManagement.messages", LocaleManager.currentLocale());
                    setTEST_RESULT(i18n.getString("datasource.driver.na"));
                } else {
                    String passwordChanged = request.getRequestObject().getParameter(PARAM_PASSW_CHANGED);
                    if(!(passwordChanged!=null && "true".equals(passwordChanged))){
                        try {
                            DataSourceEntry dSource = getDataSourceManager().getDataSourceEntry(getName());
                            setPassword(dSource.getPassword());
                        } catch (Exception e){

                        }
                    }
                    conn = getConnection();
                    if (conn != null) {
                        PreparedStatement s = conn.prepareStatement(getTestQuery());
                        s.executeQuery();
                        s.close();
                        setTEST_RESULT(RESULT_OK);
                    }
                    if(!(passwordChanged!=null && "true".equals(passwordChanged))){
                        setPassword(PARAM_DEFAULT_PASS_VALUE);
                    }
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) log.debug(e.getMessage());
                ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.panel.dataSourceManagement.messages", LocaleManager.currentLocale());
                setTEST_RESULT(i18n.getString("datasource.connection.failed"));
            }
            finally {
                setTEST_MODE(true);
                try {
                    if (conn != null) conn.close();
                }
                catch (SQLException ignore) {}
            }
        } else {
            setTEST_RESULT("");
            setTEST_MODE(false);
        }
    }
}
