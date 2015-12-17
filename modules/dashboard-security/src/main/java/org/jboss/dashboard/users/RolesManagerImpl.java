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
package org.jboss.dashboard.users;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.config.Config;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Manager class for the platform roles.
 */
@ApplicationScoped
public class RolesManagerImpl implements RolesManager {

    @Inject
    protected Application application;

    @Inject @Config("admin=Administrator,user=User")
    protected String[] rolesConfig;

    protected transient Map<String, Role> roleMap;
    protected transient Role roleAnonymous;

    @PostConstruct
    public void init() {
        roleAnonymous = new RoleImpl("anonymous", "Anonymous");
        roleMap = new HashMap<String, Role>();
        roleMap.put(roleAnonymous.getName(), roleAnonymous);

        File webXml = new File(application.getBaseAppDirectory() + File.separator + "WEB-INF/web.xml");
        if (webXml.exists()) {
            registerRolesFromWebXml(webXml);
        } else {
            registerRolesFromConfig();
        }
    }

    protected void registerRolesFromConfig() {
        for (int i = 0; i < rolesConfig.length; i++) {
            final String[] arr = rolesConfig[i].split("=");
            if (arr.length != 2) throw new IllegalArgumentException("Error: illegal role definition");

            roleMap.put(arr[0], new RoleImpl(arr[0], arr[1]));
        }
    }

    protected void registerRolesFromWebXml(File webXml) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(webXml);
            Element root = doc.getRootElement();

            // Register a role instance for every <security-role> defined in the web.xml descriptor.
            List bundleNodes = root.getChildren("security-role");
            if (bundleNodes.isEmpty()) bundleNodes = root.getChildren("security-role", null);
            for (Iterator iterator = bundleNodes.iterator(); iterator.hasNext();) {
                Element el_role = (Element) iterator.next();
                List ch_role = el_role.getChildren();
                RoleImpl role = new RoleImpl();
                for (int i = 0; i < ch_role.size(); i++) {
                    Element el_child = (Element) ch_role.get(i);
                    if (el_child.getName().equals("role-name")) role.setName(el_child.getValue().trim());
                    if (el_child.getName().equals("description")) role.setDescription(el_child.getValue().trim());
                }
                // Only register the role if a non-empty name has been assigned.
                if (!StringUtils.isBlank(role.getName())) {
                    roleMap.put(role.getName(), role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Role getRoleById(String id) {
        return roleMap.get(id);
    }

    public Set<Role> getAllRoles() {
        Set<Role> _roles = new HashSet<Role>(roleMap.size());
        _roles.addAll(roleMap.values());
        return _roles;
    }
}
