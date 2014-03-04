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
package org.jboss.dashboard.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DataProviderManagerImpl implements DataProviderManager {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(DataProviderManagerImpl.class);

    protected DataProviderType[] dataProviderTypeArray;
    
    @Inject @Install
    protected Instance<DataProviderType> dataProviderTypes;

    @PostConstruct
    protected void init() {
        List<DataProviderType> dpTypeList = new ArrayList<DataProviderType>();
        for (DataProviderType type : dataProviderTypes) dpTypeList.add(type);
        dataProviderTypeArray = dpTypeList.toArray(new DataProviderType[dpTypeList.size()]);
    }

    public DataProviderType[] getDataProviderTypes() {
        return dataProviderTypeArray;
    }

    public DataProviderType getProviderTypeByUid(String uid) {
        if (StringUtils.isBlank(uid)) return null;
        for (DataProviderType type : dataProviderTypeArray) {
            if (type.getUid().equals(uid)) return type;
        }
        return null;
    }

    public DataProvider createDataProvider() {
        DataProviderImpl p = new DataProviderImpl();
        p.setDataLoader(dataProviderTypeArray[0].createDataLoader());
        return p;
    }

    public Set<DataProvider> getAllDataProviders() throws Exception {
        final Set<DataProvider> results = new HashSet<DataProvider>();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);
            Query query = session.createQuery("from " + DataProviderImpl.class.getName() + " order by id");
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        return results;
    }

    public DataProvider getDataProviderById(final Long id) throws Exception {
        final List<DataProvider> results = new ArrayList<DataProvider>();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("from ").append(DataProviderImpl.class.getName()).append(" as instance ");
            sql.append("where instance.id = :id");

            Query query = session.createQuery(sql.toString());
            if (id != null) query.setLong("id", id.longValue());
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) return (DataProviderImpl) results.get(0);
        else log.debug("Data provider with id =" + id + " does not exist.");
        return null;
    }

    public DataProvider getDataProviderByCode(final String code) throws Exception {
        final List<DataProvider> results = new ArrayList<DataProvider>();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("from ").append(DataProviderImpl.class.getName()).append(" as instance ");
            sql.append("where instance.code = :code");

            Query query = session.createQuery(sql.toString());
            if (code != null) query.setString("code", code);
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) return (DataProviderImpl) results.get(0);
        else log.debug("Data provider with code=" + code + " does not exist.");
        return null;
    }

    public void removeDataProvider(DataProvider dataProvider) throws Exception {
        dataProvider.delete();
    }

    public void sortDataProvidersByDescription(List<DataProvider> list, boolean ascending) {
        DataProviderComparator comp = new DataProviderComparatorImpl();
        comp.addSortCriteria(DataProviderComparator.CRITERIA_DESCRIPTION, ascending ? DataProviderComparator.ORDER_ASCENDING : DataProviderComparator.ORDER_DESCENDING);
        Collections.sort(list, comp);
    }

    public void sortDataPropertiesByName(List<DataProperty> list, boolean ascending) {
        DataPropertyComparator comp = new DataPropertyComparatorImpl();
        comp.addSortCriteria(DataPropertyComparator.CRITERIA_NAME, ascending ? DataPropertyComparator.ORDER_ASCENDING : DataPropertyComparator.ORDER_DESCENDING);
        Collections.sort(list, comp);
    }
}
