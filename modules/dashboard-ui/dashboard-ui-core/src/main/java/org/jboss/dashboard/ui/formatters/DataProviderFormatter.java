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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.provider.*;
import org.jboss.dashboard.ui.controller.requestChain.KPIProcessor;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.jboss.dashboard.commons.comparator.ComparatorByCriteria;
import org.jboss.dashboard.domain.Domain;
import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.ui.components.DataProviderHandler;
import org.jboss.dashboard.ui.components.DataProviderEditor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Workspace;
import org.slf4j.Logger;

public class DataProviderFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    protected DataProviderHandler handler;

    @Inject /** The locale manager. */
    protected LocaleManager localeManager;

    public DataProviderHandler getHandler() {
        return handler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        if (handler.isEdit()) renderEdit(httpServletRequest, httpServletResponse);
        else if (handler.isCreate()) renderCreate(httpServletRequest, httpServletResponse);
        else if (handler.isEditProperties()) renderEditProperties(httpServletRequest, httpServletResponse);
        else if (handler.isRemove()) renderRemove(httpServletRequest, httpServletResponse);
        else renderShow(httpServletRequest, httpServletResponse);
    }

    private void renderEdit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            renderFragment("outputStart");
            setAttribute("providerName", StringEscapeUtils.ESCAPE_HTML4.translate(handler.getProviderName()));
            renderFragment("outputEditTitle");
            if (handler.hasErrors()) {
                setAttribute("message", handler.getProviderMessage());
                renderFragment("outputError");
            }
            renderFragment("outputTableStart");
            renderFragment("outputDataProviderTypes");
            DataProvider dataProvider = handler.getDataProvider();
            if (dataProvider != null) {
                setAttribute("error", Boolean.valueOf(handler.getFieldErrors().size() > 0));
                setAttribute("value", handler.getDescriptions());
                renderFragment("outputProviderName");

                // Provider type selected, show type editor page.
                DataProviderEditor editor = UIBeanLocator.lookup().getEditor(dataProvider.getDataProviderType());
                setAttribute("component", editor);
                renderFragment("outputEditProviderPage");
            } else {
                renderFragment("outputCancelButtonNoTypeSelected");
            }
            setAttribute("showSaveButton", Boolean.TRUE);
            if (dataProvider != null) renderFragment("outputButtons");
            renderFragment("outputTableEnd");
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error rendering.", e);
        }
    }

    private void renderCreate(HttpServletRequest request, HttpServletResponse response) {
        try {
            renderFragment("outputStart");
            renderFragment("outputCreateTitle");
            if (handler.hasErrors()) {
                setAttribute("message", handler.getProviderMessage());
                renderFragment("outputError");
            }
            renderFragment("outputTableStart");
            renderFragment("outputDataProviderTypes");
            DataProviderType type = null;
            if (handler.getCurrentProviderTypeUid() != null) {
                type = DataDisplayerServices.lookup().getDataProviderManager().getProviderTypeByUid(handler.getCurrentProviderTypeUid());
            }
            if (type != null) {
                // Provider type selected, show type editor page.
                DataProviderEditor editor = UIBeanLocator.lookup().getEditor(type);
                DataProvider dataProvider = editor.getDataProvider();
                if (dataProvider == null) {
                    DataProviderManager dataProviderManager = DataProviderServices.lookup().getDataProviderManager();
                    dataProvider = dataProviderManager.createDataProvider();
                    dataProvider.setDataLoader(type.createDataLoader());
                    editor.setDataProvider(dataProvider);
                }
                setAttribute("error", Boolean.valueOf(handler.getFieldErrors().size() > 0));
                setAttribute("value", handler.getDescriptions());
                renderFragment("outputProviderName");
                setAttribute("component", editor);
                renderFragment("outputEditProviderPage");
            } else {
                renderFragment("outputCancelButtonNoTypeSelected");
            }
            if (type != null) renderFragment("outputButtons");
            renderFragment("outputTableEnd");
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error rendering.", e);
        }
    }

    private void renderEditProperties(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            renderFragment("outputStart");
            setAttribute("providerName", StringEscapeUtils.ESCAPE_HTML4.translate(handler.getProviderName()));
            renderFragment("outputTitle");
            renderFragment("outputFormStart");
            renderFragment("outputStartProperties");

            DataProvider dataProvider = handler.getDataProvider();
            DataProperty[] properties = dataProvider.getDataSet().getProperties();
            if (properties == null || properties.length == 0) {
                renderFragment("outputStartRow");
                renderFragment("outputEmpty");
                renderFragment("outputEndRow");
            } else {
                for (int i = 0; i < properties.length; i++) {
                    AbstractDataProperty property = (AbstractDataProperty) properties[i];
                    setAttribute("index", new Integer(i));
                    renderFragment("outputStartRow");
                    setAttribute("propertyId", property.getPropertyId());
                    renderFragment("outputPropertyId");

                    Domain domain = property.getDomain();
                    String domainI18nKey = "";
                    if (domain instanceof NumericDomain) domainI18nKey = "numeric";
                    else if (domain instanceof LabelDomain) domainI18nKey = "label";
                    else if (domain instanceof DateDomain) domainI18nKey = "date";

                    if (domain instanceof NumericDomain ||
                            ((domain instanceof LabelDomain) && ((LabelDomain) domain).isConvertedFromNumeric())) {
                        // Numeric domain can be changed to label domain by user request.
                        // Option type combo.
                        String[] keys = new String[]{NumericDomain.class.getName(), LabelDomain.class.getName()};
                        String[] values = new String[]{"domain.numeric", "domain.label"};
                        setAttribute("propertyId", property.getPropertyId());
                        setAttribute("selected", domainI18nKey);
                        setAttribute("keys", keys);
                        setAttribute("values", values);
                        renderFragment("outputPropertyTypeCombo");
                    } else {
                        // Option type text.
                        setAttribute("propertyType", "domain." + domainI18nKey);
                        renderFragment("outputPropertyTypeText");
                    }


                    setAttribute("propertyId", property.getPropertyId());
                    // To print property names is necessary to call property.getName for each locale and create a i18n Map.
                    // Use of method property.getNameI18nMap() is not correct. This does not apply DataPropertyFormatter pattern,
                    // just returns the Map, although it's empty. Method getName uses DataPropertyFormatter pattern.
                    Map<String,String> names = new HashMap<String,String>();
                    String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
                    for (int j = 0; j < langs.length; j++) {
                        String lang = langs[j];
                        String name = property.getName(new Locale(lang));
                        if (name != null && name.trim().length() > 0)
                            names.put(lang, StringEscapeUtils.ESCAPE_HTML4.translate(name));
                    }
                    setAttribute("value", names);
                    renderFragment("outputPropertyTitle");
                    renderFragment("outputEndRow");
                }
            }
            renderFragment("outputEndProperties");
            renderFragment("outputButtons");
            renderFragment("outputFormEnd");
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error rendering provider properties.", e);
        }

    }

    private void renderShow(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            KPIManager kpiManager = DataDisplayerServices.lookup().getKPIManager();
            Set<KPI> kpis = kpiManager.getAllKPIs();

            DataProviderManager dataProviderManager = DataProviderServices.lookup().getDataProviderManager();
            Set<DataProvider> dataProviders = dataProviderManager.getAllDataProviders();
            Set<DataProvider> orderedDataProviders = new TreeSet<DataProvider>(DATA_PROVIDER_COMPARATOR);
            orderedDataProviders.addAll(dataProviders);

            if (dataProviders != null) {
                renderFragment("outputStart");
                renderFragment("outputNewDataProvider");
                if (dataProviders.isEmpty()) {
                    renderFragment("outputEmpty");
                } else {
                    renderFragment("outputStartDataProviders");
                    int i = 0;
                    for (DataProvider dataProvider : orderedDataProviders) {
                        if (dataProvider == null) continue;

                        String providerType = dataProvider.getDataProviderType().getDescription(getLocale());
                        setAttribute("index", new Integer(i));
                        setAttribute("code", StringEscapeUtils.ESCAPE_HTML4.translate(dataProvider.getCode()));
                        setAttribute("dataProviderName", StringEscapeUtils.ESCAPE_HTML4.translate((String) getLocaleManager().localize(dataProvider.getDescriptionI18nMap())));
                        setAttribute("dataProviderType", StringEscapeUtils.ESCAPE_HTML4.translate(providerType));
                        setAttribute("canEdit", Boolean.valueOf(dataProvider.isCanEdit()));
                        setAttribute("canEditProperties", Boolean.valueOf(dataProvider.isCanEditProperties()));
                        setAttribute("canDelete", Boolean.valueOf(dataProvider.isCanDelete()));
                        renderFragment("outputDataProvider");
                        i++;
                    }
                    renderFragment("outputEndDataProviders");
                }
                renderFragment("outputEnd");
            }
        } catch (Exception e) {
            log.error("Cannot render data providers.", e);
        }
    }

    private void renderRemove(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            KPIManager kpiManager = DataDisplayerServices.lookup().getKPIManager();
            Set<KPI> kpis = kpiManager.getAllKPIs();

            DataProviderManager dataProviderManager = DataProviderServices.lookup().getDataProviderManager();
            DataProvider provider = dataProviderManager.getDataProviderById(handler.getDataProviderId());

            renderFragment("outputStart");

            if (provider != null) {
                setAttribute("description",provider.getDescription(localeManager.getCurrentLocale()) );
                renderFragment("outputTitle");

                // Get the list of KPI that are using this data provider.
                List<KPI> providerKpis = new ArrayList<KPI>();
                int kpiCount = 0;
                for (KPI kpi : kpis) {
                    if (kpi.getDataProvider().getCode().equals(provider.getCode())) {
                        providerKpis.add(kpi);
                        kpiCount++;
                    }
                }
                
                if (!providerKpis.isEmpty()) {
                    renderFragment("outputTableStart");
                    renderFragment("outputKpiHeaders");
                    
                    int i = 0;
                    for (KPI kpi : providerKpis) {
                        String kpiDescription = kpi.getDescription(localeManager.getCurrentLocale());
                        setAttribute("description", kpiDescription);
                        Panel kpiPanel = KPIProcessor.getKPIPanel(kpi);
                        if (kpiPanel != null) {
                            String kpiPanelTitle = kpiPanel.getSection().getTitle().get(localeManager.getCurrentLang());
                            setAttribute("pageTitle", kpiPanelTitle);
                            Workspace kpiWorkspace = kpiPanel.getWorkspace();
                            if (kpiWorkspace != null) {
                                String kpiWorkspaceTitle = kpiWorkspace.getName().get(localeManager.getCurrentLang());
                                setAttribute("workspaceTitle", kpiWorkspaceTitle);
                            }
                        }
                        setAttribute("index", new Integer(i));
                        renderFragment("outputKpi");
                        i++;
                    }

                    renderFragment("outputTableEnd");
                    ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", getLocale());
                    String deleteMessage = i18n.getString(DataProviderHandler.I18N_PREFFIX + "cannotDelete");
                    deleteMessage = MessageFormat.format(deleteMessage, kpiCount);
                    setAttribute("message", deleteMessage);
                    renderFragment("outputKPIMessage");
                } else {
                    renderFragment("outputNO_KPIMessage");
                }
            }

            renderFragment("outputButtons");
            renderFragment("outputEnd");
            
        } catch (Exception e) {
            log.error("Cannot render data providers.", e);
        }
    }

    private final Comparator<DataProvider> DATA_PROVIDER_COMPARATOR = new Comparator<DataProvider>() {
        public int compare(DataProvider d1, DataProvider d2) {
            String s1 = (String) getLocaleManager().localize(d1.getDescriptionI18nMap());
            String s2 = (String) getLocaleManager().localize(d2.getDescriptionI18nMap());
            int result = ComparatorUtils.compare(s1, s2, ComparatorByCriteria.ORDER_ASCENDING);
            if (result == 0) {
                result = ComparatorUtils.compare(d1.getId(), d2.getId(), ComparatorByCriteria.ORDER_ASCENDING);
            }
            return result;
        }
    };
}
