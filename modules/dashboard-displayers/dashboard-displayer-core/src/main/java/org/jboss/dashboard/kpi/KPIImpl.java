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
package org.jboss.dashboard.kpi;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProviderImpl;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.hibernate.Session;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KPIImpl implements KPI {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KPIImpl.class);

    protected Long id;
    protected String code;
    protected Map<String, String> descriptions;
    protected DataProviderImpl dataProvider;
    protected String dataDisplayerUid;
    protected String dataDisplayerXML;

    protected transient DataDisplayer dataDisplayer;

    public KPIImpl() {
        id = null;
        code = null;
        descriptions = new HashMap<String, String>();
        dataProvider = null;
        dataDisplayer = null;
        dataDisplayerUid = null;
        dataDisplayerXML = null;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (id == null) return false;

            KPIImpl other = (KPIImpl) obj;
            return id.equals(other.getId());
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).toString();
    }

    public boolean isPersistent() {
        return id != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        if (id != null && (code == null || code.trim().equals(""))) code = "kpi_" + id + System.currentTimeMillis();
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription(Locale l) {
        String result = descriptions.get(l.toString());
        if (result == null) result = (String) LocaleManager.lookup().localize(descriptions);
        return result;
    }

    public void setDescription(String descr, Locale l) {
        if (l == null) l = LocaleManager.currentLocale();
        if (descr == null) descriptions.remove(l.toString());
        else {
            descriptions.put(l.toString(), descr);

            DataDisplayer displayer = getDataDisplayer();
            if (displayer instanceof AbstractChartDisplayer) {
                AbstractChartDisplayer chartDisplayer = (AbstractChartDisplayer) displayer;
                chartDisplayer.setTitle(descr);
            }
        }
    }

    public void setDescriptionI18nMap(Map<String, String> descriptions) {
        this.descriptions.clear();
        this.descriptions.putAll(descriptions);
    }

    public Map<String,String> getDescriptionI18nMap() {
        Map<String,String> results = new HashMap<String,String>();
        for (String locale : descriptions.keySet()) {
            results.put(locale, descriptions.get(locale));
        }
        return results;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = (DataProviderImpl) dataProvider;
    }

    public DataDisplayer getDataDisplayer() {
        try {
            if (dataDisplayer == null) deserializeDataDisplayer();
        } catch (DataDisplayerInvalidConfiguration dataDisplayerInvalidConfiguration) {
            // Data displayer is deserialized but does not match with current data provider properties.
            // Returns the non valid deserialized displayer in order to fix it by the UI.
        }
        return dataDisplayer;
    }

    public void setDataDisplayer(DataDisplayer dataDisplayer) throws DataDisplayerInvalidConfiguration {
        this.dataDisplayer = dataDisplayer;
        if (dataDisplayer != null) this.dataDisplayer.setDataProvider(dataProvider);
        serializeDataDisplayer();
    }

    public void merge(KPI source) {
        KPIImpl other = (KPIImpl) source;
        this.code = other.code;
        this.setDescriptionI18nMap(other.getDescriptionI18nMap());
        this.setDataProvider(other.getDataProvider());
        this.dataDisplayer = other.dataDisplayer;
        this.serializeDataDisplayer();
    }

    public void save() throws Exception {
        if (!getDataProvider().isReady()) {
            log.warn("Cannot save KPI because its data provider is not well configured.");
            return;
        }
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            boolean isTransient = !isPersistent();
            serializeDataDisplayer();
            if (isTransient) persist(0);
            else persist(1);
        }}.execute();
    }

    public void update() throws Exception {
        this.save();
    }

    public void delete() throws Exception {
        if (isPersistent()) {
            persist(2);
        }
    }

    protected void persist(final int op) throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            KPIManager kpiManager = DataDisplayerServices.lookup().getKPIManager();
            switch(op) {
                case 0:
                    kpiManager.notifyKPIListener(KPIManager.EVENT_KPI_CREATED, KPIImpl.this);
                    session.save(KPIImpl.this);
                    kpiManager.notifyKPIListener(KPIManager.EVENT_KPI_SAVED, KPIImpl.this);
                    break;
                case 1:
                    session.update(KPIImpl.this);
                    kpiManager.notifyKPIListener(KPIManager.EVENT_KPI_SAVED, KPIImpl.this);
                    break;
                case 2:
                    kpiManager.notifyKPIListener(KPIManager.EVENT_KPI_DELETED, KPIImpl.this);
                    session.delete(KPIImpl.this);
                    break;
            }
            session.flush();
        }}.execute();
    }

    // Persistence internals

    protected void serializeDataDisplayer() {
        try {
            dataDisplayerUid = null;
            dataDisplayerXML = null;
            if (dataDisplayer == null) return;

            DataDisplayerType type = dataDisplayer.getDataDisplayerType();
            dataDisplayerUid = type.getUid();
            dataDisplayerXML = type.getXmlFormat().format(dataDisplayer);
        } catch (Exception e) {
            log.error("Error serializing data displayer for KPI with id: " + id, e);
        }
    }

    protected void deserializeDataDisplayer() throws DataDisplayerInvalidConfiguration{
        try {
            if (dataDisplayerUid == null) return;
            DataDisplayerType type = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerTypeByUid(dataDisplayerUid);
            if (dataDisplayerXML != null) {
                ImportResults importResults = DataDisplayerServices.lookup().getImportManager().createImportResults();
                dataDisplayer = type.getXmlFormat().parse(dataDisplayerXML, importResults);
                if (importResults.getMessages().hasErrors()) {
                    throw new RuntimeException(importResults.getMessages().get(0).toString());
                }
                Locale locale = LocaleManager.currentLocale();
                dataDisplayer.setDataDisplayerType(type);
                if (dataDisplayer instanceof AbstractChartDisplayer) {
                    AbstractChartDisplayer displayer = (AbstractChartDisplayer) dataDisplayer;
                    displayer.setTitle(getDescription(locale));
                }
                dataDisplayer.setDataProvider(getDataProvider());
            }
        } catch (DataDisplayerInvalidConfiguration e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deserializing data displayer for KPI with id: " + id, e);
        }
    }

    // For Hibernate
    protected String getDataDisplayerUid() {
        return dataDisplayerUid;
    }

    // For Hibernate
    protected void setDataDisplayerUid(String dataDisplayerUid) {
        this.dataDisplayerUid = dataDisplayerUid;
    }

    // For Hibernate
    protected String getDataDisplayerXML() {
        return dataDisplayerXML;
    }

    // For Hibernate
    protected void setDataDisplayerXML(String dataDisplayerXML) {
        this.dataDisplayerXML = dataDisplayerXML;
    }

    // For Hibernate
    protected Map<String, String> getDescriptions() {
        return descriptions;
    }

    // For Hibernate
    protected void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }
}
