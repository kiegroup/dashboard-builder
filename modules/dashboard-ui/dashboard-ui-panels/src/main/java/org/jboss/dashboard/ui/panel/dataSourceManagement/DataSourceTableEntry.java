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

import java.util.Iterator;
import java.util.List;

public class DataSourceTableEntry {
    private Long dbid;
    private String datasource;
    private String name;
    private String cols;
    private List columns;
    private String selected;
        
    private DataSourceTableEntry dataSourceTableEntry;

    public DataSourceTableEntry getDataSourceTableEntry() {
        return dataSourceTableEntry;
    }

    public void setDataSourceTableEntry(DataSourceTableEntry dataSourceTableEntry) {
        this.dataSourceTableEntry = dataSourceTableEntry;
    }

    public List getColumns() {
        return columns;
    }

    public void setColumns(List columns) {
        this.columns = columns;
        Iterator it = columns.iterator();
        if(it.hasNext()){
            DataSourceColumnEntry columnEntry = (DataSourceColumnEntry) it.next();
            this.cols=columnEntry.getName();
        }
        while(it.hasNext()){
            DataSourceColumnEntry columnEntry = (DataSourceColumnEntry) it.next();
            this.cols=this.cols+","+columnEntry.getName();
        }
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
