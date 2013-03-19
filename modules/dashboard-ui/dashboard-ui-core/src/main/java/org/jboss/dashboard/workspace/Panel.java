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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Layout;
import org.jboss.dashboard.ui.resources.Skin;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.security.Policy;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.Permission;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Defines a PanelInstance that is publicated on a Page region.
 */
public class Panel implements Cloneable, Comparable, Visitable {

    /**
     * logger
     */
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Panel.class.getName());

    /*
     * Panel static attributes
     */
    private Long panelId;

    /**
     * Database id: Unique
     */
    private Long dbid;

    /**
     * Panel instance id
     */
    private Long instanceId;

    /**
     * The panel instance
     */
    private transient PanelInstance instance;

    /**
     * Region position this panel belongs to
     */
    private int position = -1;

    /**
     * Section this panel is located in
     */
    private Section section = null;

    private String layoutRegionId = null;

    public Panel() {
    }

    /**
     * Returns a constant value.
     * <p/>
     * <p>VERY IMPORTANT NOTE.
     * Regarding the message from <b>jiesheng zhang</b> posted on Fri, 01 Aug 2003 03:06:26 -0700 at
     * <i>hibernate-devel MAIL ARCHIVE</i>: "In hibernate current implementation, if a object is retrieved
     * from Set and its hashCode is changed, there is no way to remove it from set."
     * <p>You can read the original message at:
     * <code>http://www.mail-archive.com/hibernate-devel@lists.sourceforge.net/msg00008.html.</code>
     */
    public int hashCode() {
        return 0;
    }


    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (dbid == null || obj == null || !(obj instanceof Panel)) return false;

        Panel other = (Panel) obj;
        return dbid.equals(other.dbid);
    }

    public int compareTo(Object obj) {
        // Compare references
        if (this == obj) return 0;
        Panel other = (Panel) obj;

        // Try by position...
        if (position > other.position) return 1;
        if (position < other.position) return -1;

        // Try by db id...
        if (dbid == null) return -1;
        if (other.dbid == null) return 1;
        return dbid.compareTo(other.dbid);
    }

    public boolean isNew() {
        return dbid == null;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
        if (panelId == null)
            panelId = dbid;
    }

    /**
     * @deprecated Use getPanelId instead.
     */
    public String getId() {
        if (panelId == null)
            return "";
        else
            return panelId.toString();
    }

    /**
     * @deprecated Use setPanelId instead.
     */
    public void setId(String id) {
        setPanelId(new Long(id));
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns the regions this panel is assigned to, or null
     */
    public LayoutRegion getRegion() {
        if (layoutRegionId == null) {
            return null;
        } else {
            Layout layout = section.getLayout();
            if (layout != null) {
                return layout.getRegion(layoutRegionId);
            } else {
                return null;
            }
        }
    }

    public void setRegion(LayoutRegion region) {
        this.layoutRegionId = (region != null ? region.getId() : null);
//        instance.fireWorkspaceModified();
    }

    public String getLayoutRegionId() {
        return layoutRegionId;
    }

    public void setLayoutRegionId(String layoutRegionId) {
        this.layoutRegionId = layoutRegionId;
    }

    /**
     * Init panel
     */
    public void init() throws Exception {
        //log.debug("Init. Setting default parameters");
    }

    /**
     * Notify this panel is about to be removed
     */
    public void panelRemoved() {
        //Delete own resources
        GraphicElementManager[] managers = UIServices.lookup().getGraphicElementManagers();
        for (int i = 0; i < managers.length; i++) {
            GraphicElementManager manager = managers[i];
            if (!manager.getElementScopeDescriptor().isAllowedPanel())
                continue; //This manager does not define panel elements.
            GraphicElement[] elements = manager.getElements(getWorkspace().getId(), getSection().getId(), getPanelId());
            for (int j = 0; j < elements.length; j++) {
                GraphicElement element = elements[j];
                manager.delete(element);
            }
        }
    }

    public PanelInstance getInstance() {
        if (instance != null) return instance;
        WorkspaceImpl workspace = getWorkspace();
        if (workspace == null) {
            log.error("Panel dbid=" + getDbid() + " has null workspace.");
        } else {
            instance = workspace.getPanelInstance(instanceId);
            if (instance == null) {
                log.error("Panel dbid=" + getDbid() + " points to unexistant instance id=" + getInstanceId() + " in workspace " + workspace.getId());
            }
            return instance;
        }
        return null;
    }

    public void setInstance(PanelInstance instance) {
        this.instance = instance;
        this.instanceId = (instance != null ? instance.getInstanceId() : null);
    }

    public PanelProvider getProvider() {
        return getInstance().getProvider();
    }

    public void setProvider(PanelProvider provider) {
        getInstance().setProvider(provider);
    }

    public WorkspaceImpl getWorkspace() {
        return getSection().getWorkspace();
    }

    public Serializable getContentData() {
        return getInstance().getContentData();
    }

    public void setContentData(Serializable data) {
        getInstance().setContentData(data);
    }

    public Map getTitle() {
        return getInstance().getTitle();
    }

    public String getTitle(String language) {
        return getInstance().getTitle(language);
    }

    public void setTitle(Map title) {
        getInstance().setTitle(title);
    }

    public void setTitle(String title, String language) {
        getInstance().setTitle(title, language);
    }

    public int getHeight() {
        return getInstance().getHeight();
    }

    public int getCacheTime() {
        return 0;
    }

    public void setHeight(int height) {
        getInstance().setHeight(height);
    }

    public boolean isMaximizable() {
        return false;
    }

    public boolean isMinimizable() {
        return false;
    }

    public boolean isInitiallyMaximized() {
        return false;
    }

    public boolean isPaintTitle() {
        return false;
    }

    public boolean isPaintBorder() {
        return false;
    }

    public String getParameterValue(String id) {
        return getInstance().getParameterValue(id);
    }

    public String getParameterValue(String id, String language) {
        return getInstance().getParameterValue(id, language);
    }

    public void setParameterValue(String id, String value) {
        getInstance().setParameterValue(id, value);
    }

    public void setParameterValue(String id, String value, String language) {
        getInstance().setParameterValue(id, value, language);
    }

    /**
     * Returns all parameters defined for this panel, regardless of their types
     *
     * @return all parameters defined for this panel, regardless of their types
     */
    public PanelProviderParameter[] getAllParameters() {
        return getInstance().getAllParameters();
    }

    /**
     * Returns all system parameters defined for this panel
     *
     * @return all system parameters defined for this panel
     */
    public PanelProviderParameter[] getSystemParameters() {
        return getInstance().getSystemParameters();
    }

    /**
     * Returns all custom parameters defined for this panel
     *
     * @return all custom parameters defined for this panel
     */
    public PanelProviderParameter[] getCustomParameters() {
        return getInstance().getCustomParameters();
    }

    /**
     * Returns all internationalizable parameters defined for this panel
     *
     * @return all internationalizable parameters defined for this panel
     */
    public PanelProviderParameter[] getI18nParameters() {
        return getInstance().getI18nParameters();
    }

    /**
     * Returns if this panel has been successfully configured
     */
    public boolean isWellConfigured() {
        return getInstance().isWellConfigured();
    }

    public boolean supportsEditMode() {
        return getProvider().getDriver().supportsEditMode(this);
    }

    public boolean supportsHelpMode() {
        return getProvider().getDriver().supportsHelpMode(this);
    }

    /**
     * Return the all the properties defined for this kind of panels. It's a shortcut for getProvider().getProperties();
     */
    public Properties getProperties() {
        return getInstance().getProperties();
    }

    /**
     * Return the resource defined for this kind of panels. It's a shortcut for getProvider().getResource();
     */
    public String getResource(String key) {
        return getInstance().getResource(key);
    }

    /**
     * Return the resource defined for this kind of panels. It's a shortcut for getProvider().getResource();
     */
    public String getResource(String key, Locale locale) {
        return getInstance().getResource(key, locale);
    }

    /**
     * Return a clone of this object, with attributes copied, but without relations.
     *
     * @return The Panel created.
     */
    public Object clone() {
        Panel panelCopy = new Panel();
        panelCopy.setPosition(getPosition());
        panelCopy.setPanelId(getPanelId());
        return panelCopy;
    }


    /**
     * @return a String representation for this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Panel: \n");
        sb.append("     InstanceId: " + this.instanceId + "\n");
        sb.append("            New: " + isNew() + "\n");
        sb.append(" layoutRegionId: " + this.layoutRegionId + "\n");
        sb.append("      panelId: " + this.panelId + "\n");
        sb.append("       position: " + this.position + "\n");
        if (this.section != null) sb.append("        section: " + this.section.getId() + "\n");
        sb.append("End Panel. \n");
        return sb.toString();
    }

    /**
     * Return a string describing the panel.
     * Sample: Workspace de procesos > Tareas pendientes > Lista de tareas [id=13456]
     */
    public String getFullDescription() {
        LocaleManager localeManager = LocaleManager.lookup();
        StringBuffer buf = new StringBuffer();
        buf.append(localeManager.localize(getSection().getWorkspace().getTitle())).append(" \u003E ");
        buf.append(localeManager.localize(getSection().getTitle())).append(" \u003E ");
        buf.append(localeManager.localize(getInstance().getTitle())).append(" [id=").append(getPanelId()).append("]");
        return buf.toString();
    }

    public Skin getSkin() {
        return getSection().getSkin();
    }

    /**
     * Called for panels after the page they are in is left.
     */
    public void pageLeft() {
        if (getInstance() == null) {
            log.warn("Ignoring page left for panel with dbid=" + getDbid());
            return;
        }
        if (!getInstance().isSessionAliveAfterPageLeft()) {
            PanelSession pSession = getPanelSession();
            RequestContext reqCtx = RequestContext.getCurrentContext();
            HttpSession session = reqCtx.getRequest().getSessionObject();
            pSession.clear();
            pSession.setWorkMode(PanelSession.SHOW_MODE);
            reqCtx.getRequest().getRequestObject().setAttribute(Parameters.RENDER_PANEL, this);
            getProvider().initSession(pSession, session);
            reqCtx.getRequest().getRequestObject().removeAttribute(Parameters.RENDER_PANEL);
        }
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitPanel(this);

        //Panel permissions
        Policy securityPolicy = SecurityServices.lookup().getSecurityPolicy();
        Map workspacePermissions = securityPolicy.getPermissions(this, WorkspacePermission.class);
        Map panelPermissions = securityPolicy.getPermissions(this, PanelPermission.class);
        Map sectionPermissions = securityPolicy.getPermissions(this, SectionPermission.class);
        Map[] permissions = new Map[]{workspacePermissions, panelPermissions, sectionPermissions};
        for (int i = 0; i < permissions.length; i++) {
            Map permissionMap = permissions[i];
            for (Iterator it = permissionMap.keySet().iterator(); it.hasNext();) {
                Principal principal = (Principal) it.next();
                Permission perm = (Permission) permissionMap.get(principal);
                if (perm instanceof UIPermission) {
                    ((UIPermission) perm).setRelatedPrincipal(principal);
                    ((UIPermission) perm).acceptVisit(visitor);
                }
            }
        }

        return visitor.endVisit();
    }

    public PanelSession getPanelSession() {
        return SessionManager.getPanelSession(this);
    }
}
