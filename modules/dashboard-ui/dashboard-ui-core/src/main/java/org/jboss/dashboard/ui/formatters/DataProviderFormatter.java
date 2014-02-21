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

import org.apache.commons.lang.StringEscapeUtils;
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
        else renderShow(httpServletRequest, httpServletResponse);
    }

    private void renderEdit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            renderFragment("outputStart");
            setAttribute("providerName", StringEscapeUtils.escapeHtml(handler.getDataProvider().getDescription(getLocale())));
            renderFragment("outputEditTitle");
            if (handler.hasErrors()) {
                setAttribute("message", handler.getProviderMessage());
                renderFragment("outputError");
            }
            renderFragment("outputTableStart");
            renderFragment("outputDataProviderTypes");
            DataProvider dataProvider = handler.getDataProvider();
            if (dataProvider != null) {
                // Provider type selected, show type editor page.
                DataProviderEditor editor = UIBeanLocator.lookup().getEditor(dataProvider.getDataProviderType());
                editor.setDataProvider(dataProvider);
                setAttribute("error", Boolean.valueOf(handler.getFieldErrors().size() > 0));

                setAttribute("value", handler.getDescriptions());
                renderFragment("outputProviderName");
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
            setAttribute("providerName", StringEscapeUtils.escapeHtml(handler.getDataProvider().getDescription(getLocale())));
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
                            ((domain instanceof LabelDomain) && (((LabelDomain) domain)).isConvertedFromNumeric())) {
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
                    Map names = new HashMap();
                    Locale[] locales = getLocaleManager().getPlatformAvailableLocales();
                    for (int j = 0; j < locales.length; j++) {
                        Locale locale = locales[j];
                        String name = property.getName(locale);
                        if (name != null && name.trim().length() > 0)
                            names.put(locale, StringEscapeUtils.escapeHtml(name));
                    }
                    setAttribute("value", names);
                    renderFragment("outputPropertyTitle");
                    renderFragment("outputEndRow");
                }
            }
            ;
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
            Set kpis = kpiManager.getAllKPIs();

            DataProviderManager dataProviderManager = DataProviderServices.lookup().getDataProviderManager();
            Set dataProviders = dataProviderManager.getAllDataProviders();
            Set orderedDataProviders = new TreeSet(new DataProviderComparator());
            orderedDataProviders.addAll(dataProviders);

            if (dataProviders != null) {
                renderFragment("outputStart");
                renderFragment("outputNewDataProvider");
                if (dataProviders.size() == 0) {
                    renderFragment("outputEmpty");
                } else {
                    renderFragment("outputStartDataProviders");
                    int i = 0;
                    Iterator it = orderedDataProviders.iterator();
                    while (it.hasNext()) {
                        DataProvider dataProvider = (DataProvider) it.next();
                        if (dataProvider == null) continue;

                        int numberOfKPIs = 0;
                        setAttribute("usedByOtherKpis", Boolean.FALSE);
                        Iterator it1 = kpis.iterator();
                        while (it1.hasNext()) {
                            KPI kpi = (KPI) it1.next();
                            if (kpi.getDataProvider().equals(dataProvider)) numberOfKPIs++;
                        }

                        String providerType = dataProvider.getDataProviderType().getDescription(getLocale());
                        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", getLocale());
                        String deleteMessage = i18n.getString(DataProviderHandler.I18N_PREFFIX + "confirmDelete");
                        if (numberOfKPIs > 0) {
                            deleteMessage = i18n.getString(DataProviderHandler.I18N_PREFFIX + "cannotDelete");
                            deleteMessage = MessageFormat.format(deleteMessage, numberOfKPIs);
                        }

                        setAttribute("index", new Integer(i));
                        setAttribute("code", StringEscapeUtils.escapeHtml(dataProvider.getCode()));
                        setAttribute("dataProviderName", StringEscapeUtils.escapeHtml(dataProvider.getDescription(getLocale())));
                        setAttribute("dataProviderType", StringEscapeUtils.escapeHtml(providerType));
                        setAttribute("canEdit", Boolean.valueOf(dataProvider.isCanEdit()));
                        setAttribute("canEditProperties", Boolean.valueOf(dataProvider.isCanEditProperties()));
                        setAttribute("canDelete", Boolean.valueOf(dataProvider.isCanDelete()));
                        setAttribute("numberOfKPIs", numberOfKPIs);
                        setAttribute("deleteMessage", deleteMessage);
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

    class DataProviderComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            DataProvider d1 = (DataProvider) o1;
            DataProvider d2 = (DataProvider) o2;
            int result = ComparatorUtils.compare(d1.getDescription(getLocale()), d2.getDescription(getLocale()), ComparatorByCriteria.ORDER_ASCENDING);
            if (result == 0) {
                result = ComparatorUtils.compare(d1.getId(), d2.getId(), ComparatorByCriteria.ORDER_ASCENDING);
            }
            return result;
        }
    }
}
