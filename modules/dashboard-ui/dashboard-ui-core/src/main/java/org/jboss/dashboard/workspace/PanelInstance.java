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
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.export.WorkspaceVisitor;
import org.jboss.dashboard.workspace.export.Visitable;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.ui.panel.parameters.IntParameter;
import org.jboss.dashboard.ui.utils.javascriptUtils.JavascriptTree;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.*;
import java.util.*;

/**
 * Definition class of a Panel type. It determines what are the properties for each type (provider, parameters...)
 */
public class PanelInstance implements Cloneable, Visitable {

    /**
     * Common parameters
     */
    public static final String PARAMETER_TITLE = "title";
    public static final String PARAMETER_GROUP = "group";
    public static final String PARAMETER_HEIGHT = "height";
    public static final String PARAMETER_HTML_BEFORE = "htmlBeforePanel";
    public static final String PARAMETER_HTML_AFTER = "htmlAfterPanel";
    public static final String PARAMETER_MAXIMIZABLE = "maximized";
    public static final String PARAMETER_MINIMIZABLE = "minimizable";
    public static final String PARAMETER_PAINT_TITLE = "paint_title";
    public static final String PARAMETER_PAINT_BORDER = "paint_border";
    public static final String PARAMETER_SESSION_KEEP_ALIVE = "session_keep_alive";

    /**
     * logger
     */
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelInstance.class.getName());

    /**
     * Database id: Unique
     */
    private Long dbid;

    /**
     * Identifier inside workspace
     */
    private Long instanceId;

    /**
     * Provider implementing panel's functionality
     */
    private PanelProvider provider;

    /**
     * Provider implementing panel's behaviour
     */
    private String providerName = "";

    /**
     * Parameters provided to this panel
     */
    private Set<PanelParameter> panelParams = new HashSet();

    /**
     * Panel content (information stored by the panel driver) - optional
     */
    private Serializable data;

    private WorkspaceImpl workspace;

    /**
     * String with BASE64 serialization of panel data
     */
    private String persistence;

    public PanelInstance() {
        instanceId = null;
        data = null;
        workspace = null;
        provider = null;
    }

    /**
     * Returns a constant value.
     * <p/>
     * VERY IMPORTANT NOTE.
     * Regarding the message from <b>jiesheng zhang</b> posted on Fri, 01 Aug 2003 03:06:26 -0700 at
     * <i>hibernate-devel MAIL ARCHIVE</i>: "In hibernate current implementation, if a object is retrieved
     * from Set and its hashCode is changed, there is no way to remove it from set." Read the original message
     * at <code>http://www.mail-archive.com/hibernate-devel@lists.sourceforge.net/msg00008.html.</code>
     */
    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        try {
            PanelInstance other = (PanelInstance) obj;
            if (dbid == null || other.dbid == null) return false;
            return dbid.equals(other.getDbid());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getId() {
        return "" + getInstanceId();
    }

    public void setId(String id) {
        setInstanceId(new Long(id));
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
        if (instanceId == null)
            instanceId = dbid;
    }

    public PanelProvider getProvider() {
        if (provider == null || !providerName.equals(provider.getId())) {
            PanelsProvidersManager ppm = UIServices.lookup().getPanelsProvidersManager();
            try {
                PanelProvider provider = ppm.getProvider(providerName);
                if (provider == null) {
                    log.error("Can't find provider " + providerName);
                    provider = ppm.getInvalidPanelProvider(providerName);
                }
                setProvider(provider);
            } catch (Exception e) {
                try {
                    setProvider(ppm.getInvalidPanelProvider(providerName));
                } catch (Exception e1) {
                    log.error("Error getting the default provider instance:", e1);
                }
                log.error("Error: ", e);
            }
        }
        return provider;
    }

    public void setProvider(PanelProvider provider) {
        this.provider = provider;
        providerName = provider.getId();
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public WorkspaceImpl getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceImpl workspace) {
        this.workspace = workspace;
    }

    public Set getPanelParams() {
        return panelParams;
    }

    public void setPanelParams(Set panelParams) {
        this.panelParams = panelParams;
    }

    public Map getTitle() {
        LocaleManager localeManager = LocaleManager.lookup();
        final String[] langs = localeManager.getPlatformAvailableLangs();
        Map title = new AbstractMap() {
            public Set entrySet() {
                return new AbstractSet() {
                    public int size() {
                        return langs.length;
                    }

                    public Iterator iterator() {
                        return new Iterator() {
                            int i = 0;

                            public void remove() {
                                throw new UnsupportedOperationException();
                            }

                            public boolean hasNext() {
                                return i < langs.length;
                            }

                            public Object next() {
                                i++;
                                return new Entry() {
                                    int index = i - 1;

                                    public Object getKey() {
                                        return langs[index];
                                    }

                                    public Object getValue() {
                                        return getParameterValue(PARAMETER_TITLE, langs[index]);
                                    }

                                    public Object setValue(Object value) {
                                        throw new UnsupportedOperationException();
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
        return title;
    }

    public String getTitle(String language) {
        LocaleManager localeManager = LocaleManager.lookup();
        return (String) localeManager.localize(getTitle());
    }

    public void setTitle(Map title) {
        for (Iterator it = title.keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String val = (String) title.get(lang);
            setParameterValue(PARAMETER_TITLE, val, lang);
        }
    }

    public void setTitle(String title, String language) {
        setParameterValue(PARAMETER_TITLE, title, language);
    }

    public int getHeight() {
        // The parameter PARAMETER_HEIGHT probably will be deleted but not decided yet.
        // return IntParameter.value(getParameterValue(PARAMETER_HEIGHT), 0);
        return 0;
    }

    public void setHeight(int height) {
        setParameterValue(PARAMETER_HEIGHT, String.valueOf(height));
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

    /**
     * Determine if panel session is to be kept alive after page changed.
     *
     * @return if panel session is to be kept alive after page changed.
     */
    public boolean isSessionAliveAfterPageLeft() {
        // The parameter PARAMETER_SESSION_KEEP_ALIVE probably will be deleted but not decided yet.
        // return BooleanParameter.value(getParameterValue(PARAMETER_SESSION_KEEP_ALIVE), false);
        return true;
    }

    // Parameters set
    //

    public void addPanelParameter(final PanelParameter param) {
        if (this.panelParams == null) {
            this.panelParams = new HashSet();
        }
        final PanelInstance theInstance = this;
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                theInstance.panelParams.add(param);
                param.setPanelInstance(theInstance);
                session.saveOrUpdate(theInstance);
            }
        };
        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    public void removePanelParameter(final String paramId) {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                for (PanelParameter panelParam : panelParams) {
                    if (!panelParam.getIdParameter().equals(paramId)) continue;
                    if (!panelParam.getPanelInstance().equals(PanelInstance.this)) continue;
                    panelParams.remove(panelParam);
                    session.update(PanelInstance.this);
                }
            }}.execute();
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    public PanelParameter getPanelParameter(String id, String lang) {
        Iterator it = panelParams.iterator();
        while (it.hasNext()) {
            PanelParameter param = (PanelParameter) it.next();
            if (param.getIdParameter().equals(id) &&
                    (lang == null || lang.trim().length() == 0 || param.getLanguage().equalsIgnoreCase(lang)))
                return param;
        }
        return null;
    }

    public PanelParameter[] getPanelParameters() {
        return (PanelParameter[]) panelParams.toArray(new PanelParameter[panelParams.size()]);
    }

    public String getParameterValue(String id) {
        return getParameterValue(id, "");
    }

    protected LocaleManager getLocaleManager() {
        return LocaleManager.lookup();
    }

    public String getParameterValue(final String id, String language) {
        if (language == null || language.trim().length() == 0)
            language = getLocaleManager().getDefaultLang();

        /* If it is defined in .panel file, take it from there*/
        if (getProvider().getProperties().containsKey("parameter." + id)) {
            return getProvider().getProperties().getProperty("parameter." + id);
        }

        /* Get the PanelProviderParameter object from driver */
        PanelProviderParameter providerParam = null;
        PanelProviderParameter[] allParams = getProvider().getDriver().getAllParameters();
        for (int i = 0; i < allParams.length; i++) {
            PanelProviderParameter param = allParams[i];
            if (param.getId().equals(id)) {
                providerParam = param;
                break;
            }
        }

        if (providerParam == null) {
            log.error("Cannot read parameter with id=" + id + ". Provider " + getProvider().getId() + " doesn't declare it.");
            return "";
        } else {
            final boolean isI18n = providerParam.isI18n();
            Set panelParams = getPanelParams();
            final List instanceParameters = new ArrayList();
            for (Iterator iterator = panelParams.iterator(); iterator.hasNext();) {
                PanelParameter panelParameter = (PanelParameter) iterator.next();
                if (id.equals(panelParameter.getIdParameter())) {
                    if (!isI18n || language.equals(panelParameter.getLanguage())) {
                        instanceParameters.add(panelParameter);
                    }
                }
            }
            if (instanceParameters.size() == 1) {
                return ((PanelParameter) instanceParameters.get(0)).getValue();
            } else if (instanceParameters.size() > 1) {
                log.error("There are " + instanceParameters.size() + " values for parameter " + id + " in lang " + language);
                return ((PanelParameter) instanceParameters.get(0)).getValue();
            }
            return StringUtils.defaultString(providerParam.getDefaultValue(language));
        }
    }

    public void setParameterValue(PanelParameter param) {
        if (param != null)
            setParameterValue(param.getIdParameter(), param.getValue(), param.getLanguage());
    }

    public void setParameterValue(String id, String value) {
        setParameterValue(id, value, "");
    }

    public void setParameterValue(String id, String value, String language) {
        if (language == null || "".equals(language)) language = " ";
        boolean updated = false;

        /* Get the PanelProviderParameter object from driver */
        PanelProviderParameter providerParam = null;
        PanelProviderParameter[] allParams = getProvider().getDriver().getAllParameters();
        for (int i = 0; i < allParams.length; i++) {
            PanelProviderParameter param = allParams[i];
            if (param.getId().equals(id)) {
                providerParam = param;
                break;
            }
        }
        String defaultValue = null;
        if (providerParam == null) {
            log.error("Setting parameter with id=" + id + " although driver "+ getProviderName() +" doesn't declare it.");
        } else {
            defaultValue = providerParam.getDefaultValue(language);
        }

        for (Iterator iterator = panelParams.iterator(); iterator.hasNext();) {
            final PanelParameter panelParameter = (PanelParameter) iterator.next();
            if (panelParameter.getIdParameter().equals(id)) {
                if (!StringUtils.isBlank(panelParameter.getLanguage())) {
                    if (language.equals(" ")) {
                        log.warn("Setting value to parameter " + id + " currently with language='" + panelParameter.getLanguage() + "', using null or empty language.");
                    } else if (language.equals(panelParameter.getLanguage())) {
                        updated = true;
                        panelParameter.setValue(value);
                    }
                } else {
                    if (!language.equals(" ")) {
                        log.warn("Setting value to parameter " + id + " without language, using language='" + language + "'");
                    } else {
                        if ((defaultValue != null && defaultValue.equals(value)) || (defaultValue == null && value == null)) {
                            try {
                                iterator.remove();
                                new HibernateTxFragment() {
                                    protected void txFragment(Session session) throws Exception {
                                        session.delete(panelParameter);
                                        session.flush();
                                    }
                                }.execute();
                            } catch (Exception e) {
                                log.error("Error: ", e);
                            }
                        } else {
                            updated = true;
                            panelParameter.setValue(value);
                        }

                    }
                    break;
                }
            }
        }
        if (!updated) {
            // Create it unless it matches the default value.
            if (defaultValue != null && defaultValue.equals(value)) return;

            PanelParameter param = new PanelParameter();
            param.setPanelInstance(this);
            param.setIdParameter(id);
            param.setValue(value);
            param.setLanguage(language);
            addPanelParameter(param);
        }
    }

    // Panel provider parameters
    //

    /**
     * Returns all parameters defined for this panel, regardless of their types
     *
     * @return all parameters defined for this panel, regardless of their types
     */
    public PanelProviderParameter[] getAllParameters() {
        return getProvider().getDriver().getAllParameters();
    }

    /**
     * Returns all system parameters defined for this panel
     *
     * @return all system parameters defined for this panel
     */
    public PanelProviderParameter[] getSystemParameters() {
        List sysParams = new ArrayList();
        PanelProviderParameter[] params = getProvider().getDriver().getAllParameters();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (params[i].isSystemParameter()) sysParams.add(params[i]);
            }
        }
        return (PanelProviderParameter[]) sysParams.toArray(new PanelProviderParameter[sysParams.size()]);
    }

    /**
     * Returns all custom parameters defined for this panel
     *
     * @return all custom parameters defined for this panel
     */
    public PanelProviderParameter[] getCustomParameters() {
        List customParams = new ArrayList();
        PanelProviderParameter[] params = getProvider().getDriver().getAllParameters();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (!params[i].isSystemParameter())
                    customParams.add(params[i]);
            }
        }
        return (PanelProviderParameter[]) customParams.toArray(new PanelProviderParameter[customParams.size()]);
    }

    /**
     * Returns all internationalizable parameters defined for this panel
     *
     * @return all internationalizable parameters defined for this panel
     */
    public PanelProviderParameter[] getI18nParameters() {
        List i18nParams = new ArrayList();
        PanelProviderParameter[] params = getProvider().getDriver().getAllParameters();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (params[i].isI18n())
                    i18nParams.add(params[i]);
            }
        }
        return (PanelProviderParameter[]) i18nParams.toArray(new PanelProviderParameter[i18nParams.size()]);
    }

    /**
     * Returns if this panel has been successfully configured
     *
     * @return if this panel has been successfully configured
     */
    public boolean isWellConfigured() {
        PanelProviderParameter[] params = getAllParameters();

        if (params != null && params.length > 0) {
            // Read all parameters
            for (int i = 0; i < params.length; i++) {
                String value = getParameterValue(params[i].getId());
                if (!params[i].isValid(value)) {
                    log.debug("Param " + params[i].getId() + " hasn't valid value (" + value + ")");
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * @return the all the properties defined for this kind of panels. It's a shortcut for getProvider().getProperties();
     */
    public Properties getProperties() {
        return getProvider().getProperties();
    }

    /**
     * @return the resource defined for this kind of panels. It's a shortcut for getProvider().getResource();
     */
    public String getResource(String key) {
        return getProvider().getResource(key);
    }

    /**
     * @return the resource defined for this kind of panels. It's a shortcut for getProvider().getResource();
     */
    public String getResource(String key, Locale locale) {
        return getProvider().getResource(key, locale);
    }

    public PanelInstance getInstance() {
        return this;
    }

    /**
     * Saves all this panel's properties via the workspaces managers
     *
     * @throws Exception in case of error
     */
    public void saveProperties() throws Exception {
        firePanelPropertiesModified();
        UIServices.lookup().getPanelsManager().store(this);
    }

    /**
     * Saves all this panel's properties via the workspaces managers
     *
     * @throws Exception
     */
    public void saveCustomProperties() throws Exception {
        firePanelPropertiesModified();
        firePanelCustomPropertiesModified();
        UIServices.lookup().getPanelsManager().store(this);
    }

    public Serializable getContentData() {
        return data;
    }

    public void setContentData(Serializable data) {
        //this.data = data;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buf);
            out.writeObject(data);
            out.flush();
            out.close();
            setPersistence(Base64.encode(buf.toByteArray()));
        } catch (Exception e) {
            log.error("Cannot persist content data for instance " + dbid, e);
        }
        log.debug("ContentData for instance " + getDbid() + " set to " + data);
        log.debug(" persistence = " + persistence);
    }

    /**
     * Returns a clone of this object to work with.
     *
     * @return a clone of this object to work with.
     */
    public PanelInstance getPartialClonedCopy() {
        try {
            PanelInstance instance = (PanelInstance) super.clone();
            instance.setContentData(null);
            instance.panelParams = new HashSet();
            instance.panelParams.addAll(this.panelParams);
            return instance;
        } catch (CloneNotSupportedException e) {
            log.error("Error cloning", e);
            return null;
        }
    }


    public String getPersistence() {
        return persistence;
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;

        // Update PanelInstance content data
        data = null;
        try {
            if (persistence != null && !"".equals(persistence)) {
                byte[] bytes = Base64.decode(persistence);
                ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(bytes));
                Serializable object = (Serializable) s.readObject();
                data = object;
            }
        } catch (Exception e) {
            log.error("Content data for instance dbid=" + getDbid() + " is invalid.", e);
        }
        log.debug("Persistence for instance dbid=" + getDbid() + " set to " + persistence);
        log.debug(" data = " + data);
    }

    /**
     * Init panel
     */
    public void init() throws Exception {
        log.debug("Init PanelInstance " + instanceId);
        PanelProviderParameter[] params = getProvider().getDriver().getAllParameters();

        for (int i = 0; i < params.length; i++) {
            String id = params[i].getId();
            String value = params[i].getDefaultValue();

            if ((getParameterValue(id) == null || getParameterValue(id).trim().length() == 0) && value != null)
                setParameterValue(params[i].getId(), value);
        }

        log.debug("Init panel instance");
        getProvider().initPanel(this);
    }

    /**
     * Persists this panel's contents, actually, save it all to DB.
     *
     * @throws Exception
     */
    public synchronized void persist() throws Exception {

        final PanelInstance instance = this;
        setContentData(data);//Make changes in 'data' reflect persistence encoded String
        log.debug("Persisting instance " + dbid);
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                session.saveOrUpdate(instance);
            }
        };

        txFragment.execute();
    }

    /**
     * Restores content from secondary storage
     *
     * @deprecated Now panel persistence is stored as a String property of PanelInstance.
     *             This method has a void implementation but is not removed in order to keep interface .
     */
    public void restore() throws Exception {
    }

    protected void firePanelPropertiesModified() {
        getProvider().getDriver().firePanelPropertiesModified(this);
    }

    protected void firePanelCustomPropertiesModified() {
        getProvider().getDriver().firePanelCustomPropertiesModified(this);
    }

    /**
     * Notify this panel is about to be removed
     */
    public void instanceRemoved(Session session) throws Exception {
        getProvider().getDriver().fireBeforePanelInstanceRemove(this);
        JavascriptTree.regenerateTrees(getWorkspace().getId());

        // Avoid foreign key constraints while removing a PanelInstance.
        // All SQL sentences issued by panel drivers will be executed now.
        session.flush();
    }

    /**
     * Clone this object, that is, return a similar object with same basic attributes, but without relations.
     *
     * @return a clone for this panels instance.
     */
    public Object clone() {
        PanelInstance panelInstanceCopy = new PanelInstance();
        panelInstanceCopy.setProvider(getProvider());
        panelInstanceCopy.setId(getId());
        if (persistence != null) panelInstanceCopy.setPersistence(persistence);
        return panelInstanceCopy;
    }


    /**
     * @return a String representation for this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        //sb.append("PI:" + getDbid());
        sb.append("PanelInstance:\n");
        sb.append("          dbId: " + this.getDbid() + "\n");
        sb.append("            Id: " + this.getId() + "\n");
        sb.append("           workspace: " + workspace.getDbid() + "\n");
        sb.append("      provider: " + provider + " ( Driver: " + (provider == null ? "null" : provider.getDriver().toString()) + " )\n");
        sb.append("End PanelInstance.\n");
        return sb.toString();
    }

    /**
     * Determine all panels hanging from this instance.
     *
     * @return A (probably empty) array of panels whose instance is this object.
     */
    public Panel[] getAllPanels() {
        List panels = new ArrayList();
        for (Iterator it = getWorkspace().getSections().iterator(); it.hasNext();) {
            Section section = (Section) it.next();
            for (Iterator idePanels = section.getPanels().iterator(); idePanels.hasNext();) {
                Panel panel = (Panel) idePanels.next();
                if (instanceId.equals(panel.getInstanceId())) {
                    panels.add(panel);
                }
            }
        }
        return (Panel[]) panels.toArray(new Panel[panels.size()]);
    }

    /**
     * Count all panels hanging from this instance.
     *
     * @return The number of panel whose instance is this object.
     */
    public int getAllPanelsCount() {
        int n = 0;
        for (Iterator it = getWorkspace().getSections().iterator(); it.hasNext();) {
            Section section = (Section) it.next();
            for (Iterator idPanels = section.getPanels().iterator(); it.hasNext();) {
                Panel panel = (Panel) idPanels.next();
                if (instanceId.equals(panel.getInstanceId())) {
                    n++;
                }
            }
        }
        return n;
    }

    public Object acceptVisit(WorkspaceVisitor visitor) throws Exception {
        visitor.visitPanelInstance(this);

        // Panel parameters
        PanelParameter[] parameters = getPanelParameters();
        Arrays.sort(parameters, new Comparator() {
            public int compare(Object o1, Object o2) {
                PanelParameter param1 = ((PanelParameter) o1);
                PanelParameter param2 = ((PanelParameter) o2);
                if (!param1.getIdParameter().equals(param2.getIdParameter())) {
                    return param1.getIdParameter().compareTo(param2.getIdParameter());
                } else {
                    return StringUtils.defaultString(param1.getLanguage()).compareTo(StringUtils.defaultString(param2.getLanguage()));
                }
            }
        });
        for (int i = 0; i < parameters.length; i++) {
            PanelParameter parameter = parameters[i];
            parameter.acceptVisit(visitor);
        }

        //Panel Instance resources
        GraphicElement[] skins = UIServices.lookup().getSkinsManager().getElements(getWorkspace().getId(), null, getInstanceId());
        GraphicElement[] envelopes = UIServices.lookup().getEnvelopesManager().getElements(getWorkspace().getId(), null, getInstanceId());
        GraphicElement[] layouts = UIServices.lookup().getLayoutsManager().getElements(getWorkspace().getId(), null, getInstanceId());
        GraphicElement[][] elements = {skins, envelopes, layouts};
        for (int i = 0; i < elements.length; i++) {
            GraphicElement[] elementsArray = elements[i];
            for (int j = 0; j < elementsArray.length; j++) {
                GraphicElement element = elementsArray[j];
                element.acceptVisit(visitor);
            }
        }

        return visitor.endVisit();
    }

}
