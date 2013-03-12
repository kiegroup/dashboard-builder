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
package org.jboss.dashboard.security;

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named("permissionManager")
public class PermissionManager {

    public static PermissionManager lookup() {
        return (PermissionManager) CDIBeanLocator.getBeanByName("permissionManager");
    }

    private static transient Log log = LogFactory.getLog(PermissionManager.class.getName());

    public PermissionDescriptor createNewItem() {
        return new PermissionDescriptor();        
    }

    public List<PermissionDescriptor> getAllInstances() throws Exception {
        return find(null);
    }

    /**
     * Find the permission descriptor for given principal and permission
     */
    public PermissionDescriptor find(final Principal prpal, final Permission perm) {
        final List results = new ArrayList(1);
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                StringBuffer buf = new StringBuffer();
                buf.append(" from " + PermissionDescriptor.class.getName() + " as item where item.dbid is not null ");
                if (prpal != null) {
                    buf.append(" and item.principalClass = :principalClass  ");
                    buf.append(" and item.principalName =  :principalName  ");
                }
                buf.append("and item.permissionClass = :permissionClass and item.permissionResource = :permissionResource");
                Query query = session.createQuery(buf.toString());
                if (prpal != null) {
                    query.setString("principalClass", prpal.getClass().getName());
                    query.setString("principalName", prpal.getName());
                }
                query.setString("permissionClass", perm.getClass().getName());
                query.setString("permissionResource", perm.getName());
                query.setCacheable(true);
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                results.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }
        };

        try {
            txFragment.execute();
            if (!results.isEmpty())
                return (PermissionDescriptor) results.get(0);
            else
                return null;
        } catch (Exception e) {
            log.error("Error retrieving PermissionDescriptor", e);
            return null;
        }
    }

    public List<PermissionDescriptor> find(final String resourceName) throws Exception {
        final List results = new ArrayList();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            StringBuffer buf = new StringBuffer();
            buf.append(" from " + PermissionDescriptor.class.getName() + " as item where item.dbid is not null ");
            if (!StringUtils.isBlank(resourceName)) buf.append(" and item.permissionResource = :res1 or  item.permissionResource = :res2");
            Query query = session.createQuery(buf.toString());
            if (!StringUtils.isBlank(resourceName)) {
                query.setString("res1", resourceName);
                query.setString("res2", resourceName + ".*");
            }
            query.setCacheable(true);
            FlushMode oldFlushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.NEVER);
            results.addAll(query.list());
            session.setFlushMode(oldFlushMode);
        }}.execute();
        return results;
    }
}
