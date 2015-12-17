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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;

import java.io.Serializable;

/**
 * Definition for extra parameters for Workspaces.
 */
public class WorkspaceParameter implements Serializable, Visitable {
    /**
     * log
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkspaceParameter.class);

    private String parameterId;
    private String language;
    private String value;
    private WorkspaceImpl workspace;

    public WorkspaceParameter() {
        this(null, null, null, null);
    }

    public WorkspaceParameter(String parameterId, WorkspaceImpl workspace, String language, String value) {
        this.workspace = workspace;
        this.parameterId = parameterId;
        this.language = language;
        this.value = value;
    }

    public WorkspaceImpl getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceImpl workspace) {
        this.workspace = workspace;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceParameter)) return false;

        final WorkspaceParameter workspaceParameter = (WorkspaceParameter) o;

        String workspaceId = workspace == null ? null : workspace.getId();
        String parameterWorkspaceId = workspaceParameter.workspace == null ? null : workspaceParameter.workspace.getId();

        if (language != null ? !language.equals(workspaceParameter.language) : workspaceParameter.language != null) return false;
        if (parameterId != null ? !parameterId.equals(workspaceParameter.parameterId) : workspaceParameter.parameterId != null) return false;
        if (workspaceId != null ? !workspaceId.equals(parameterWorkspaceId) : parameterWorkspaceId != null) return false;

        return true;
    }

    public int hashCode() {
        return 0;
    }

    public Object clone() {
        WorkspaceParameter p = new WorkspaceParameter();
        p.setLanguage(getLanguage());
        p.setParameterId(getParameterId());
        p.setValue(getValue());
        return p;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PanelParameter {");
        sb.append(" id: ");
        sb.append(parameterId);
        sb.append(" val: ");
        sb.append(value);
        sb.append(" lang: ");
        sb.append(language);
        sb.append(" instance: ");
        sb.append(workspace == null ? "null" : (String.valueOf(workspace.getDbid())));
        sb.append(" }");
        return sb.toString();
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitWorkspaceParameter(this);
        return visitor.endVisit();
    }
}
