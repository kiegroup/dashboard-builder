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

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The home section of a workspace.
 */
public class WorkspaceHome implements Serializable {

    /** The section id */
    protected Long sectionId;

    /** The role */
    protected String roleId;

    /** The workspace */
    protected Workspace workspace;

    public WorkspaceHome() {
        this.sectionId = null;
        this.roleId = null;
        this.workspace = null;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceHome)) return false;

        WorkspaceHome that = (WorkspaceHome) o;
        String workspaceId = workspace == null ? null : workspace.getId();
        String thatWorkspaceId = that.workspace == null ? null : that.workspace.getId();

        if (sectionId != null ? !sectionId.equals(that.sectionId) : that.sectionId != null) return false;
        if (roleId != null ? !roleId.equals(that.roleId) : that.roleId != null) return false;
        if (workspaceId != null ? !workspaceId.equals(thatWorkspaceId) : thatWorkspaceId != null) return false;
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        String workspaceId = workspace == null ? null : workspace.getId();
        return new ToStringBuilder(this).
                append("workspaceId", workspaceId).
                append("sectionId", sectionId).
                append("roleId", roleId).toString();
    }
}
