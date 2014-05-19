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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * A PanelInstance parameter
 */
public class PanelParameter implements Serializable, Visitable {

    protected Long dbid;
    protected String idParameter;
    protected String language;
    protected String value;
    protected PanelInstance panelInstance;

    public PanelParameter() {
    }

    public String toString() {
        return new ToStringBuilder(this).append("dbid", getDbid()).toString();
    }

    public boolean equals(Object o) {
        try {
            if (o == null) return false;
            if (this == o) return true;
            if (dbid == null) return false;

            PanelParameter that = (PanelParameter) o;
            return dbid.equals(that.getDbid());
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return dbid == null ? 0 : dbid.hashCode();
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public String getIdParameter() {
        return idParameter;
    }

    public void setIdParameter(String idParameter) {
        this.idParameter = idParameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public PanelInstance getPanelInstance() {
        return panelInstance;
    }

    public void setPanelInstance(PanelInstance panelInstance) {
        this.panelInstance = panelInstance;
    }

    public Object clone() {
        PanelParameter p = new PanelParameter();
        p.setPanelInstance(getPanelInstance());
        p.setIdParameter(getIdParameter());
        p.setLanguage(getLanguage());
        p.setValue(getValue());
        return p;
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitPanelParameter(this);
        return visitor.endVisit();
    }
}
