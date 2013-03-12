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

import java.sql.Types;

public class DataSourceColumnEntry {
    private Long dbid;
    private String datasource;
    private String tableName;
    private String name;
    private String primaryKey;
    private String identity;
    private int sqltype;

    private DataSourceColumnEntry dataSourceColumnEntry;

    public DataSourceColumnEntry getDataSourceColumnEntry() {
        return dataSourceColumnEntry;
    }

    public void setDataSourceManager(DataSourceColumnEntry dataSourceColumnEntry) {
        this.dataSourceColumnEntry = dataSourceColumnEntry;
    }
    
    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public int getSqltype(){
        return this.sqltype;
    }

    public void setSqltype(int sqltype){
        this.sqltype = sqltype;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryKey(){
        return primaryKey;
    }
    public void setPrimaryKey(String pk){
        this.primaryKey = pk;
    }
    public String getIdentity(){
        return identity;
    }
    public void setIdentity(String ident){
        this.identity = ident;
    }

    public String getJavaType()
    {
        switch (getSqltype()) {
        case Types.ARRAY:
            return "Array";
        case Types.BIGINT:
            return "Long";
        case Types.BIT:
            return "Boolean";
        case Types.BLOB:
            return "Blob";
        case Types.BOOLEAN:
            return "Boolean";
        case Types.CHAR:
            return "String";
        case Types.CLOB:
            return "Clob";
        case Types.DATALINK:
            return "URL";
        case Types.DATE:
            return "Date";
        case Types.DECIMAL:
            return "BigDecimal";
        case Types.DOUBLE:
            return "Double";
        case Types.FLOAT:
            return "Float";
        case Types.INTEGER:
            return "Int";
        case Types.JAVA_OBJECT:
            return "Object";
        case Types.LONGVARBINARY:
            return "Bytes";
        case Types.LONGVARCHAR:
            return "AsciiStream";
        case Types.NULL:
            return "Null";
        case Types.NUMERIC:
            return "BigDecimal";
        case Types.OTHER:
            return "Object";
        case Types.REF:
            return "Object";
        case Types.SMALLINT:
            return "Short";
        case Types.STRUCT:
            return "Object";
        case Types.TIME:
            return "Time";
        case Types.TIMESTAMP:
            return "Timestamp";
        case Types.TINYINT:
            return "Byte";
        case Types.VARBINARY:
            return "Bytes";
        case Types.VARCHAR:
            return "String";
//        case Types.REAL:
//        case Types.DISTINCT
//        case Types.BINARY:

        }
        return null;
    }

}
