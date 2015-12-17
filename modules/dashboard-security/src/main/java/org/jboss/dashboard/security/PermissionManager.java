/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static transient Logger log = LoggerFactory.getLogger(PermissionManager.class.getName());

    public PermissionDescriptor createNewItem() {
        return new PermissionDescriptor();        
    }

    public List<PermissionDescriptor> getAllInstances() throws Exception {
        return find( (String) null);
    }

    /**
     * Find the permission descriptor for given principal and permission
     */
    public PermissionDescriptor find(final Principal prpal, final Permission perm) {
        final List<PermissionDescriptor> results = new ArrayList<PermissionDescriptor>(1);
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
                return results.get(0);
            else
                return null;
        } catch (Exception e) {
            log.error("Error retrieving PermissionDescriptor", e);
            return null;
        }
    }

    /**
     * Recover the Permissions for the given permission resource name
     */
    public List<PermissionDescriptor> find(final String resourceName) throws Exception {
        final List<PermissionDescriptor> results = new ArrayList<PermissionDescriptor>();
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

    /**
     * Recover Permissions for the given permission class and resource name
     */
    public List<PermissionDescriptor> find(final String permissionClass, final String permissionResource) {
        return find(permissionClass, permissionResource, Boolean.TRUE);
    }

    /**
     * Recover Permissions for the given permission class and resource name, including or excluding the ones marked as readonly
     */
    public List<PermissionDescriptor> find(final String permissionClass, final String permissionResource, final Boolean includeReadOnly) {
        final List<PermissionDescriptor> results = new ArrayList<PermissionDescriptor>(10);
        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                StringBuffer buf = new StringBuffer(" from " + PermissionDescriptor.class.getName() + " as item where item.dbid is not null ");
                buf.append("and item.permissionClass = :permissionClass and item.permissionResource = :permissionResource");
                if (!includeReadOnly) buf.append(" and item.readonly = :readonly");
                Query query = session.createQuery(buf.toString());
                query.setString("permissionClass", permissionClass);
                query.setString("permissionResource", permissionResource);
                if (!includeReadOnly) query.setBoolean("readonly", includeReadOnly);
                query.setCacheable(true);
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                results.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }
        };
        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Error retrieving PermissionDescriptors for permission class "  + permissionClass + " and resource " + permissionResource, e);
        }
        return results;
    }

    /**
     * Recover a Permission by its Id
     */
    public PermissionDescriptor findPermissionDescriptorById(final Long idPermission) {
        final List<PermissionDescriptor> result = new ArrayList<PermissionDescriptor>(1);
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    String sql = " from " + PermissionDescriptor.class.getName() + " as item where item.dbid = :dbid";
                    Query query = session.createQuery(sql);
                    query.setLong("dbid", idPermission);
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    result.add( (PermissionDescriptor) query.uniqueResult() );
                    session.setFlushMode(oldFlushMode);
                }
            }.execute();
        } catch (Exception e) {
            log.error("PermissionDescriptor with id " + idPermission + " not found!", e);
        }
        return result.get(0);
    }

    /**
     * Recover the Permissions by the Ids indicated in the List parameter
     */
    public List<PermissionDescriptor> find(final List<Long> permissionIds) {
        final List<PermissionDescriptor> results = new ArrayList<PermissionDescriptor>(10);
        if (permissionIds != null) {
            final StringBuilder idString = new StringBuilder(" from " + PermissionDescriptor.class.getName() + " as item where item.dbid in (");
            for (int i = 0; i < permissionIds.size(); i++) {
                idString.append(permissionIds.get(i));
                if (i != permissionIds.size()-1) idString.append(",");
            }
            idString.append(")");
            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    Query query = session.createQuery(idString.toString());
                    FlushMode oldFlushMode = session.getFlushMode();
                    session.setFlushMode(FlushMode.NEVER);
                    results.addAll(query.list());
                    session.setFlushMode(oldFlushMode);
                }
            };
            try {
                txFragment.execute();
            } catch (Exception e) {
                log.error("Error deleting PermissionDescriptors with dbids in ("  + idString + ")", e);
            }
        }
        return results;
    }
}
