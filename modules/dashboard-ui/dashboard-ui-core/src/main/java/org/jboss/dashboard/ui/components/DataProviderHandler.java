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
package org.jboss.dashboard.ui.components;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.provider.*;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Data provider manager component.
 */
@PanelScoped
public class DataProviderHandler extends UIBeanHandler {

    public static final String I18N_PREFFIX = "dataProviderComponent.";
    public static final String PARAM_PROVIDER_CODE = "dataProviderCode";
    public static final String PARAM_PROVIDER_NAME = "dataProviderName";
    public static final String PARAM_PROPERTY_ID = "propertyId";
    public static final String PARAM_PROPERTY_TYPE = "propertyType";
    public static final String PARAM_PROPERTY_TITLE = "propertyTitle";

    @Inject
    private transient Logger log;

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected LocaleManager localeManager;

    @Inject @Config("/components/bam/provider/manager/data_provider_show.jsp")
    protected String componentIncludeJSP;

    @Inject @Config("/components/bam/provider/manager/data_provider_show.jsp")
    protected String componentIncludeJSPshow;

    @Inject @Config("/components/bam/provider/manager/data_provider_edit_create.jsp")
    protected String componentIncludeJSPeditCreate;

    @Inject @Config("/components/bam/provider/manager/data_provider_edit_properties.jsp")
    protected String componentIncludeJSPeditProperties;

    // Component parameters for factory mapping to html objects.
    protected String currentProviderTypeUid;
    protected String currentProviderTypeChanged;
    protected String providerName;
    protected String saveButtonPressed;
    protected String testConfigButtonPressed;

    // Session flags. Is all flags are false, mode enabled is show mode.
    protected boolean isEdit;
    protected boolean isEditProperties;
    protected boolean isCreate;

    protected Long dataProviderId;
    protected Map descriptions;
    protected String providerMessage;
    protected boolean hasErrors = false;
    private ResourceBundle messages;

    protected transient DataProvider newDataProvider;

    public DataProvider getDataProvider() throws Exception {
        if (dataProviderId == null) return newDataProvider;
        return DataDisplayerServices.lookup().getDataProviderManager().getDataProviderById(dataProviderId);
    }

    public Long getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(Long dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public String getSaveButtonPressed() {
        return saveButtonPressed;
    }

    public void setSaveButtonPressed(String saveButtonPressed) {
        this.saveButtonPressed = saveButtonPressed;
    }

    public String getTestConfigButtonPressed() {
        return testConfigButtonPressed;
    }

    public void setTestConfigButtonPressed( String testConfigButtonPressed ) {
        this.testConfigButtonPressed = testConfigButtonPressed;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEditProperties() {
        return isEditProperties;
    }

    public void setEditProperties(boolean editProperties) {
        isEditProperties = editProperties;
    }

    public String getCurrentProviderTypeChanged() {
        return currentProviderTypeChanged;
    }

    public void setCurrentProviderTypeChanged(String currentProviderTypeSelectedChanged) {
        this.currentProviderTypeChanged = currentProviderTypeSelectedChanged;
    }

    public Map getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map descriptions) {
        this.descriptions = descriptions;
    }

    public String getProviderMessage() {
        return providerMessage;
    }

    public void setProviderMessage(String providerMessage) {
        this.providerMessage = providerMessage;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public static DataProviderHandler lookup() {
        return CDIBeanLocator.getBeanByType(DataProviderHandler.class);
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getCurrentProviderTypeUid() {
        return currentProviderTypeUid;
    }

    public void setCurrentProviderTypeUid(String currentProviderTypeUid) {
        this.currentProviderTypeUid = currentProviderTypeUid;
    }

    public String getBeanJSP() {
        return this.componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getComponentIncludeJSPshow() {
        return componentIncludeJSPshow;
    }

    public void setComponentIncludeJSPshow(String componentIncludeJSPshow) {
        this.componentIncludeJSPshow = componentIncludeJSPshow;
    }

    public String getComponentIncludeJSPeditCreate() {
        return componentIncludeJSPeditCreate;
    }

    public void setComponentIncludeJSPeditCreate(String componentIncludeJSPeditCreate) {
        this.componentIncludeJSPeditCreate = componentIncludeJSPeditCreate;
    }

    public String getComponentIncludeJSPeditProperties() {
        return componentIncludeJSPeditProperties;
    }

    public void setComponentIncludeJSPeditProperties(String componentIncludeJSPeditProperties) {
        this.componentIncludeJSPeditProperties = componentIncludeJSPeditProperties;
    }

    // --------------------------------
    // Action methods.
    // --------------------------------

    public void actionEditDataProvider(CommandRequest request) {
        String dataProviderCode = getDataProviderCodeFromRequest(request);
        try {
            if (dataProviderCode != null) {
                DataProvider dataProvider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(dataProviderCode);
                DataProviderType providerType = dataProvider.getDataProviderType();

                dataProviderId = dataProvider.getId();
                currentProviderTypeUid = providerType.getUid();
                descriptions = dataProvider.getDescriptionI18nMap();
                providerName = (String) LocaleManager.lookup().localize(descriptions);
                setEdit(true);

                // Go the the edit screen.
                DataProviderEditor editor = UIBeanLocator.lookup().getEditor(providerType);
                editor.setDataProvider(dataProvider);
                setComponentIncludeJSP(componentIncludeJSPeditCreate);
            }
        } catch (Exception e) {
            log.error("Cannot get data provider with codde " + dataProviderCode, e);
        }
    }

    public void actionDeleteDataProvider(CommandRequest request) {
        String dataProviderCode = getDataProviderCodeFromRequest(request);
        if (dataProviderCode != null) {
            try {
                DataProvider dataProvider = dataProviderManager.getDataProviderByCode(dataProviderCode);
                dataProviderManager.removeDataProvider(dataProvider);
            } catch (Exception e) {
                log.error("Cannot delete data provider with id " + dataProviderCode, e);
            }
        }
    }

    public void actionEditDataProviderProperties(CommandRequest request) {
        String dataProviderCode = getDataProviderCodeFromRequest(request);
        try {
            if (dataProviderCode != null) {
                DataProvider dataProvider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(dataProviderCode);
                dataProviderId = dataProvider.getId();
                setEditProperties(true);

                setComponentIncludeJSP(componentIncludeJSPeditProperties);
            }
        } catch (Exception e) {
            log.error("Cannot get data provider with code " + dataProviderCode, e);
        }
    }

    public void actionStoreDataProviderProperties(CommandRequest request) {
        try {
            DataProvider dataProvider = getDataProvider();

            // Find all parameters matching name starts with "name_".
            Map parameters = request.getRequestObject().getParameterMap();
            Iterator it = parameters.keySet().iterator();
            while (it.hasNext()) {
                String param_name = (String) it.next();
                String param_value = request.getRequestObject().getParameter(param_name);
                if (param_name.startsWith("name/")) {
                    String propId = param_name.substring(param_name.indexOf("/") + 1, param_name.lastIndexOf("/"));
                    String lang = param_name.substring(param_name.lastIndexOf("/") + 1, param_name.length());
                    AbstractDataProperty adp = (AbstractDataProperty) dataProvider.getDataSet().getPropertyById(propId);
                    if (!"".equals(param_value)) adp.getNameI18nMap().put(new Locale(lang), param_value);
                    else adp.getNameI18nMap().remove(new Locale(lang));
                }
                if (param_name.startsWith(PARAM_PROPERTY_TYPE)) {
                    String pId = param_name.substring(param_name.indexOf("_") + 1, param_name.length());
                    AbstractDataProperty adp = (AbstractDataProperty) dataProvider.getDataSet().getPropertyById(pId);
                    Domain domnain = (Domain) Class.forName(param_value).newInstance();
                    if (domnain instanceof LabelDomain) ((LabelDomain) domnain).setConvertedFromNumeric(true);
                    adp.setDomain(domnain);
                }
            }
            dataProvider.save();
            clearAttributes();
            setComponentIncludeJSP(componentIncludeJSPshow);
        } catch (Exception e) {
            log.error("Cannot save provider properties with id " + dataProviderId, e);
        }
    }

    public void actionStartCreateNewDataProvider(CommandRequest request) {
        setDataProviderId(null);
        setCreate(true);
        resetProviders();
        setComponentIncludeJSP(componentIncludeJSPeditCreate);
    }

    public void resetProviders() {
        DataProviderType[] types = dataProviderManager.getDataProviderTypes();
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                UIBeanLocator.lookup().getEditor(types[i]).clear();
            }
        }
    }

    public void createDataProvider(CommandRequest request) {
        try {
            // Create the data provider instance.
            DataProviderManager providerManager = DataDisplayerServices.lookup().getDataProviderManager();
            DataProviderType type = providerManager.getProviderTypeByUid(currentProviderTypeUid);

            newDataProvider = providerManager.createDataProvider();
            newDataProvider.setDataLoader(type.createDataLoader());

            DataProviderEditor editor = UIBeanLocator.lookup().getEditor(type);
            editor.setDataProvider(newDataProvider);
            try {
                editor.actionSubmit(request);
            } catch (Exception e) {
                setProviderMessage(e.getMessage());
                setHasErrors(true);
                return;
            }
            if (isTestConfigButtonPressed()) return;

            checkConfig();

            // Save if requested and all is ok.
            if (!hasErrors() && editor.isConfiguredOk() && isSaveButtonPressed()) {
                setProviderDescriptions(newDataProvider);
                newDataProvider.save();

                // Edit data provider properties
                dataProviderId = newDataProvider.getId();
                setCreate(false);
                setEditProperties(true);
                setComponentIncludeJSP(componentIncludeJSPeditProperties);
            }
        } catch (Exception e) {
            log.error("Cannot create data provider.", e);
        }
    }

    public void updateDataProvider(CommandRequest request) {
        try {
            // Catch changes on the provider configuration.
            DataProviderEditor editor = getDataProviderEditor();
            try {
                editor.actionSubmit(request);
            } catch (Exception e) {
                setProviderMessage(e.getMessage());
                setHasErrors(true);
                return;
            }
            if (isTestConfigButtonPressed()) {
                return;
            }

            // Merge property configurations.
            DataProvider dataProvider = getDataProvider();
            dataProvider.setDataLoader(editor.getDataProvider().getDataLoader());

            // Save if requested and all is ok.
            checkConfig();
            if (!hasErrors() && editor.isConfiguredOk() && isSaveButtonPressed()) {
                setProviderDescriptions(dataProvider);
                dataProvider.save();
                clearAttributes();
                setComponentIncludeJSP(componentIncludeJSPshow);
            }
        } catch (Exception e) {
            log.error("Cannot edit data provider.", e);
        }
    }

    protected void setProviderDescriptions(DataProvider dataProvider) {
        Iterator it = descriptions.keySet().iterator();
        while (it.hasNext()) {
            Locale l = (Locale) it.next();
            if (l == null) continue;
            dataProvider.setDescription((String) descriptions.get(l), l);
        }
    }

    public void actionEditCreateNewDataProvider(CommandRequest request) {
        setProviderMessage(null);
        setHasErrors(false);
        if (providerTypeChanged(request)) return;

        // Find all parameters whose name starts with "dataProviderName".
        Map parameters = request.getRequestObject().getParameterMap();
        Iterator it = parameters.keySet().iterator();
        while (it.hasNext()) {
            String param_name = (String) it.next();
            String param_value = request.getRequestObject().getParameter(param_name);
            if (param_name.startsWith(PARAM_PROVIDER_NAME + "/")) {
                String lang = param_name.substring(param_name.lastIndexOf("/") + 1, param_name.length());
                if (!"".equals(param_value)) descriptions.put(new Locale(lang), param_value);
                else descriptions.remove(new Locale(lang));
            }
        }

        if (isEdit) updateDataProvider(request);
        else createDataProvider(request);
    }

    public void actionCancel(CommandRequest request) {
        clearAttributes();
        setComponentIncludeJSP(componentIncludeJSPshow);
    }

    // --------------------------------
    // Common used methods by provider.
    // --------------------------------
    
    public DataProviderEditor getDataProviderEditor() {
        try {
            if (currentProviderTypeUid == null) return null;
            DataProvider dataProvider = getDataProvider();
            if (dataProvider == null) return null;

            return UIBeanLocator.lookup().getEditor(dataProvider.getDataProviderType());
        } catch (Exception e) {
            log.error("Cannot get data provider editor.", e);
        }
        return null;
    }

    protected String getDataProviderCodeFromRequest(CommandRequest request) {
        String dataProviderCode = request.getRequestObject().getParameter(PARAM_PROVIDER_CODE);
        if (dataProviderCode == null || dataProviderCode.trim().length() == 0) log.error("Cannot edit data provider with id null or void.");
        else return dataProviderCode;
        return null;
    }

    protected boolean providerTypeChanged(CommandRequest request) {
        return currentProviderTypeChanged != null && "true".equals(currentProviderTypeChanged);
    }

    protected boolean isSaveButtonPressed() {
        return saveButtonPressed != null && "true".equals(saveButtonPressed);
    }

    protected boolean isTestConfigButtonPressed() {
        return testConfigButtonPressed != null && "true".equals(testConfigButtonPressed);
    }

    // --------------------------------
    // Handling attributes methods.
    // --------------------------------

    @PostConstruct
    public void clearAttributes() {
        DataProviderEditor editor = getDataProviderEditor();
        if (editor != null) editor.clear();
        setCurrentProviderTypeUid(null);
        setCurrentProviderTypeChanged(null);
        setProviderName(null);
        setDescriptions(new HashMap());
        setEdit(false);
        setCreate(false);
        setEditProperties(false);
        setProviderMessage(null);
        setHasErrors(false);
        clearFieldErrors();
    }

    protected void checkConfig() {
        // Name must exist al least for the current locale.
        String name = (String) localeManager.localize(descriptions);
        if (name == null || "".equals(name)) {
            setProviderMessage( MessageFormat.format(getMessage("dataProviderComponent.nameInvalid"),
                    localeManager.getLangDisplayName(localeManager.getCurrentLang())) );
            setHasErrors(true);
            return;
        }

        // Check for existing names
        StringBuilder messagePart = new StringBuilder("");
        for (Iterator descIt = descriptions.keySet().iterator(); descIt.hasNext(); ) {
            Locale descLoc = (Locale) descIt.next();
            String descName = (String) descriptions.get(descLoc);
            DataProvider currentProvider = null;
            try {
                currentProvider = getDataProvider();
            } catch (Exception e) {
                log.error("Error: ", e);
            }
            if ( nameExists(currentProvider, descLoc , descName) ) {
                if (messagePart.length() > 0) messagePart.append(", ");
                messagePart.append( descLoc.getDisplayName(LocaleManager.currentLocale()) ).append(":").append(descName);
            }
        }
        if (messagePart.length() > 0) {
            setProviderMessage( MessageFormat.format(getMessage("dataProviderComponent.nameExists"), messagePart.toString()) );
            setHasErrors(true);
        }
    }

    protected String getMessage(String key) {
        if (key == null || "".equals(key)) return null;
        Locale currentLocale = LocaleManager.currentLocale();
        if (messages == null || !messages.getLocale().equals(currentLocale)) messages = localeManager.getBundle("org.jboss.dashboard.displayer.messages", currentLocale);
        String message = messages.getString(key);
        return (message == null || "".equals(message)) ? null : message;
    }

    private boolean nameExists(DataProvider currentProvider, Locale locale, String name) {
        try {
            for (DataProvider provider : dataProviderManager.getAllDataProviders()) {
                if (currentProvider != null && currentProvider.getId() != null && currentProvider.equals(provider)) continue;
                String localizedName = provider.getDescription(locale);
                if (!StringUtils.isEmpty(localizedName) && localizedName.equalsIgnoreCase(name)) return true;
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }
}
