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
package org.jboss.dashboard.i18n;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.message.Message;
import org.jboss.dashboard.commons.message.MessageList;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.displayer.chart.AbstractXAxisDisplayer;
import org.jboss.dashboard.displayer.table.DataSetTable;
import org.jboss.dashboard.displayer.table.TableColumn;
import org.jboss.dashboard.displayer.table.TableDisplayer;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.domain.RangeConfiguration;
import org.jboss.dashboard.export.ImportManager;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.*;

/**
 * Bundle converter for KPIs exported as XML files.
 */
public class KpisFileConverter extends XmlToBundleConverter {

    /** Logger */
    protected Logger log = LoggerFactory.getLogger(KpisFileConverter.class);

    @Inject
    protected ImportManager importManager;

    public Map<Locale,Properties> extract() throws Exception {
        Map<Locale,Properties> bundles = new HashMap<Locale, Properties>();
        if (xmlFile != null && xmlFile.exists()) {
            ImportResults importResults = importManager.parse(new FileInputStream(xmlFile));

            // Check parsing results.
            MessageList messages = importResults.getMessages();
            Locale locale = LocaleManager.currentLocale();
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                Message message = (Message) it.next();
                switch (message.getMessageType()) {
                    case Message.ERROR: throw new Exception(message.getMessage(locale));
                    case Message.WARNING: log.warn(message.getMessage(locale)); break;
                    case Message.INFO: log.info(message.getMessage(locale)); break;
                }
            }

            // Extract i18n literals from data providers
            Set<DataProvider> dataProviders = importResults.getDataProviders();
            for (DataProvider dataProvider : dataProviders) {
                processDataProvider(dataProvider, bundles);
            }

            // Extract i18n literals from KPIs
            Set<KPI> kpis = importResults.getKPIs();
            for (KPI kpi : kpis) {
                processKPI(kpi, bundles);
            }
        }
        return bundles;
    }

    protected void processDataProvider(DataProvider dataProvider, Map<Locale,Properties> bundles) throws Exception {
        Map<Locale,String> descrMap = dataProvider.getDescriptionI18nMap();
        for (Locale l : descrMap.keySet()) {
            String value = descrMap.get(l);
            String key = dataProvider.getCode() + ".description";
            getBundle(bundles, l).setProperty(key, value);

            DataProperty[] dataProps = dataProvider.getDataSet().getProperties();
            for (DataProperty dataProp : dataProps) {
                processDataProperty(dataProp, dataProvider.getCode(), bundles);
            }
        }
    }

    protected void processDataProperty(DataProperty dataProperty, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        Map<Locale,String> nameMap = dataProperty.getNameI18nMap();
        for (Locale l : nameMap.keySet()) {
            String value = nameMap.get(l);
            String key = parentKey + "." + dataProperty.getPropertyId() + ".name";
            getBundle(bundles, l).setProperty(key, value);
        }
    }

    protected void processKPI(KPI kpi, Map<Locale,Properties> bundles) throws Exception {
        Map<String,String> descrMap = kpi.getDescriptionI18nMap();
        for (String lang : descrMap.keySet()) {
            String value = descrMap.get(lang);
            getBundle(bundles, new Locale(lang)).setProperty(kpi.getCode() + ".description", value);

            DataDisplayer dataDisplayer = kpi.getDataDisplayer();
            if (dataDisplayer instanceof TableDisplayer) {
                TableDisplayer tableDisplayer = (TableDisplayer) dataDisplayer;
                processTableDisplayer(tableDisplayer, kpi.getCode(), bundles);
            }
            if (dataDisplayer instanceof AbstractChartDisplayer) {
                AbstractChartDisplayer chartDisplayer = (AbstractChartDisplayer) dataDisplayer;
                processChartDisplayer(chartDisplayer, kpi.getCode(), bundles);
            }
        }
    }

    protected void processChartDisplayer(AbstractChartDisplayer chartDisplayer, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        DomainConfiguration domainConfig = new DomainConfiguration(chartDisplayer.getDomainProperty());
        processDomain(domainConfig, parentKey + ".domain", bundles);

        RangeConfiguration rangeConfig = new RangeConfiguration(chartDisplayer.getRangeProperty(), chartDisplayer.getRangeScalarFunction(), chartDisplayer.getUnitI18nMap());
        processRange(rangeConfig, parentKey + ".range", bundles);
    }

    protected void processDomain(DomainConfiguration domainConfig, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        Map<Locale,String> namesI18nMap = domainConfig.getPropertyNameI18nMap();
        for (Locale l: namesI18nMap.keySet()) {
            String value = namesI18nMap.get(l);
            getBundle(bundles, l).setProperty(parentKey + ".name", value);
        }
        Map<Locale,String> hideI18nMap = domainConfig.getLabelIntervalsToHideI18nMap();
        for (Locale l: hideI18nMap.keySet()) {
            String value = hideI18nMap.get(l);
            getBundle(bundles, l).setProperty(parentKey + ".labelsToHide", value);
        }
    }

    protected void processRange(RangeConfiguration rangeConfig, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        Map<Locale,String> namesI18nMap = rangeConfig.getNameI18nMap();
        for (Locale l: namesI18nMap.keySet()) {
            String value = namesI18nMap.get(l);
            getBundle(bundles, l).setProperty(parentKey + ".name", value);
        }
        Map<Locale,String> unitsI18nMap = rangeConfig.getUnitI18nMap();
        for (Locale l: unitsI18nMap.keySet()) {
            String value = unitsI18nMap.get(l);
            getBundle(bundles, l).setProperty(parentKey + ".unit", value);
        }
    }

    protected void processTableDisplayer(TableDisplayer tableDisplayer, String parentKey, Map<Locale,Properties> bundles) throws Exception {
        DataSetTable table = tableDisplayer.getTable();
        DataProperty groupByProp = table.getGroupByProperty();
        if (groupByProp != null) {
            DomainConfiguration domainConfig = new DomainConfiguration(groupByProp);
            processDomain(domainConfig, parentKey + ".groupBy", bundles);
        }
        for (int columnIndex=0; columnIndex<table.getColumnCount(); columnIndex++) {
            DataProperty columnProperty = table.getOriginalDataProperty(columnIndex);
            if (columnProperty == null) continue;

            TableColumn column = table.getColumn(columnIndex);
            Map<Locale,String> columnName = column.getNameI18nMap();
            for (Locale l : columnName.keySet()) {
                String value = columnName.get(l);
                getBundle(bundles, l).setProperty(parentKey + ".column."+ columnIndex + ".name", value);
            }

            Map<Locale,String> columnHint = column.getHintI18nMap();
            for (Locale l : columnHint.keySet()) {
                String value = columnHint.get(l);
                getBundle(bundles, l).setProperty(parentKey + ".column."+ columnIndex + ".hint", value);
            }
        }
    }

    public void inject(Map<Locale,Properties> bundles) throws Exception {

    }
}
