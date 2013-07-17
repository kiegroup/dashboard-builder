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
package org.jboss.dashboard.ui.utils.javascriptUtils;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.commons.text.StringUtil;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.taglib.LocalizeTag;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.dashboard.workspace.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class JavascriptTree implements Comparable {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavascriptTree.class.getName());

    protected String key = null;
    protected String val = null;
    protected Integer valInt = null;
    protected SortedSet children = null;
    protected boolean forceFirst = false;
    protected String configType = null;

    public static final String STANDARD_CONFIG = "old_config";
    public static final String TREE_CONFIG = "tree_config";

    public static String HTMLfilter(String str) {
        return StringEscapeUtils.escapeHtml(str);
    }

    public JavascriptTree(String key, String val) {
        this.key = key == null ? "" : key;
        this.val = val == null ? "" : val;
    }

    public JavascriptTree(String key, int val) {
        this.key = key == null ? "" : key;
        this.valInt = new Integer(val);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val == null ? valInt.toString() : val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Set getChildren() {
        return children == null ? Collections.EMPTY_SET : Collections.unmodifiableSortedSet(children);
    }

    public void addChildren(JavascriptTree child) {
        if (child != null) {
            if (children == null)
                children = new TreeSet();
            children.add(child);
        }
    }

    public boolean isForceFirst() {
        return forceFirst;
    }

    public void setForceFirst(boolean forceFirst) {
        this.forceFirst = forceFirst;
    }


    public int compareTo(Object o) {
        JavascriptTree jsTree = (JavascriptTree) o;
        if (isForceFirst() && !jsTree.isForceFirst())
            return -1;
        if (!isForceFirst() && jsTree.isForceFirst())
            return 1;
        if (isForceFirst() && jsTree.isForceFirst())
            log.warn("Two elements forced to be first. Something may be wrong.");
        if ((getChildren().size() > 0) != (jsTree.getChildren().size() > 0)) {
            return getChildren().size() > 0 ? -1 : 1;
        }
        int keyCompare = key.compareTo(jsTree.getKey());
        if (keyCompare != 0)
            return keyCompare;
        return getVal().compareTo(jsTree.getVal());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("['");
        sb.append(StringEscapeUtils.escapeJavaScript(key));
        sb.append("',");
        if (val != null) {
            sb.append("'");
            sb.append(StringEscapeUtils.escapeJavaScript(val));
            sb.append("'");
        } else {
            sb.append(valInt);
        }
        if (children != null) {
            for (Iterator it = children.iterator(); it.hasNext();) {
                sb.append(",");
                sb.append(it.next());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String replaceAll(String value, Properties properties) {
        for (Enumeration en = properties.keys(); en.hasMoreElements();) {
            String key = (String) en.nextElement();
            String val = properties.getProperty(key);
            value = StringUtil.replaceAll(value, key, val);
        }
        return value;
    }

    /**
     * @param s
     * @return
     * @deprecated Use StringEscapeUtils.escapeHtml(s)
     */
    public static String replaceTildes(String s) {
        return StringEscapeUtils.escapeHtml(s);
    }

    /**
     * Deletes tree cache for given workspace or all, if workspace is null.
     *
     * @param workspaceId Workspace whose cache is to be deleted, or null to delete them all.
     */
    public static synchronized void regenerateTrees(String workspaceId) {
        if (workspaceId == null) {
            treeOfInstancesForDragAndDrop.clear();
            treeOfExistingInstancesWithoutAutoSelect.clear();
            treeOfInstancesForNewInstance.clear();
        } else {
            treeOfInstancesForDragAndDrop.remove(workspaceId);
            treeOfExistingInstancesWithoutAutoSelect.remove(workspaceId);
            treeOfInstancesForNewInstance.remove(workspaceId);
        }
    }

    private static HashMap treeOfInstancesForDragAndDrop = new HashMap();
    private static HashMap treeOfExistingInstancesWithoutAutoSelect = new HashMap();
    private static HashMap treeOfInstancesForNewInstance = new HashMap();

    /**
     * This method generates the javascript tree for the insert existing panel menu, the first one that appears when
     * you click the (+) Add panel button in a region.
     *
     * @param request
     * @param response
     * @return The text for the javascript tree.
     */
    public static synchronized String getTreeOfExistingInstancesWithAutoSelect(HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        log.debug("Starting computation of Javascript tree for insert panel. ");

        // Refactoring. The components system manages the actions by the tree.
        // with this condition we mantain the compatibility with the old configuration system.
        WorkspaceImpl workspace = null;
        Section section = null;
        if (request.getAttribute("workspace") == null)
            workspace = NavigationManager.lookup().getCurrentWorkspace();
        else
            workspace = (WorkspaceImpl) request.getAttribute("workspace");

        if (request.getAttribute("section") == null)
            section = NavigationManager.lookup().getCurrentSection();
        else
            section = (Section) request.getAttribute("section");

        Locale currentLocale = SessionManager.getCurrentLocale();
        PanelInstance[] instances = workspace.getPanelInstances();
        if (instances == null || instances.length == 0)
            return null;


        PanelsProvidersManager providersMgr = UIServices.lookup().getPanelsProvidersManager();
        Map groups = new HashMap();
        for (int i = 0; i < instances.length; i++) {
            PanelInstance instance = instances[i];

            // Provider group
            String groupName = providersMgr.getGroupDisplayName(instance.getProvider().getGroup(), currentLocale);
            Map group = (Map) groups.get(groupName);
            if (group == null) {
                group = new HashMap();
                groups.put(groupName, group);
            }

            // Provider name
            PanelProvider panelProvider = instance.getProvider();
            String providerName = panelProvider.getResource(panelProvider.getDescription(), currentLocale);
            Map provider = (Map) group.get(providerName);
            if (provider == null) {
                provider = new HashMap();
                group.put(providerName, provider);
            }

            String instanceGroupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, currentLocale.getLanguage());
            instanceGroupName = instanceGroupName == null ? "" : instanceGroupName.trim();
            Map instanceGroup = (Map) provider.get(instanceGroupName);
            if (instanceGroup == null) {
                instanceGroup = new HashMap();
                provider.put(instanceGroupName, instanceGroup);
            }

            instanceGroup.put(instance.getInstanceId(), instance);
        }
        final String language = currentLocale.getLanguage();

        JavascriptTree tree = new JavascriptTree("Panels", 0);
        URLMarkupGenerator umkg = UIServices.lookup().getUrlMarkupGenerator();
        for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext();) {
            String providerGroupName = (String) itGroups.next();
            Map providerGroup = (Map) groups.get(providerGroupName);
            JavascriptTree providerGroupTree = new JavascriptTree(providerGroupName, 0);
            for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext();) {
                String providerId = (String) itProviders.next();
                JavascriptTree providerTree = new JavascriptTree(providerId, 0);
                Map instanceGroups = (Map) providerGroup.get(providerId);
                for (Iterator itInstanceGroups = instanceGroups.keySet().iterator(); itInstanceGroups.hasNext();) {
                    String instanceGroupName = (String) itInstanceGroups.next();
                    if ("".equals(instanceGroupName)) {
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            // Refactoring. The components system manages the actions by a configuration tree.
                            // with this condition we mantain the compatibility with the old configuration system.
                            String linkStr = "";

                            if ((request.getAttribute("configType") == null) || (request.getAttribute("configType").equals(STANDARD_CONFIG))){
                                Map params = new HashMap();
                                params.put("id=" , instance.getInstanceId());
                                linkStr = umkg.getMarkup("PanelsHandler", "createPanel", params);
                            }
                            else if (request.getAttribute("configType").equals(TREE_CONFIG))
                                linkStr = "javascript: submitForm(" + instanceId.toString() + ");";

                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, linkStr);
                            providerTree.addChildren(instanceTree);
                        }
                    } else {
                        JavascriptTree instanceGroupTree = new JavascriptTree(instanceGroupName, 0);
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            // Refactoring. The components system manages the actions by the tree.
                            // with this condition we mantain the compatibility with the old configuration system.
                            String linkStr = "";

                            if(true)throw new UnsupportedOperationException();

                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, linkStr);
                            instanceGroupTree.addChildren(instanceTree);
                        }
                        providerTree.addChildren(instanceGroupTree);
                    }
                }
                providerGroupTree.addChildren(providerTree);
            }
            tree.addChildren(providerGroupTree);
        }
        log.debug("Finished computation of Javascript tree for insert panel in " + (System.currentTimeMillis() - startTime) + " ms.");
        return tree.toString();
    }

    /**
     * This method generates the javascript tree for the workspace panels tab in administration
     *
     * @return The text for the javascript tree.
     */
    public static synchronized String getTreeOfExistingInstancesWithoutAutoSelect() {
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        Locale currentLocale = LocaleManager.lookup().currentLocale();

        String cache = null;
        Map cacheMap = (Map) treeOfExistingInstancesWithoutAutoSelect.get(workspace.getId());
        if (cacheMap != null && (cache = (String) cacheMap.get(currentLocale)) != null)
            return cache;

        long startTime = System.currentTimeMillis();
        log.debug("Starting computation of Javascript tree for workspaces admin. ");
        PanelInstance[] instances = workspace.getPanelInstances();
        if (instances == null || instances.length == 0)
            return null;

        PanelsProvidersManager providersMgr = UIServices.lookup().getPanelsProvidersManager();
        Map groups = new HashMap();
        for (int i = 0; i < instances.length; i++) {
            PanelInstance instance = instances[i];

            // Provider group
            String groupName = providersMgr.getGroupDisplayName(instance.getProvider().getGroup(), currentLocale);
            Map group = (Map) groups.get(groupName);
            if (group == null) {
                group = new HashMap();
                groups.put(groupName, group);
            }

            // Provider name
            PanelProvider panelProvider = instance.getProvider();
            String providerName = panelProvider.getResource(panelProvider.getDescription(), currentLocale);
            Map provider = (Map) group.get(providerName);
            if (provider == null) {
                provider = new HashMap();
                group.put(providerName, provider);
            }

            String instanceGroupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, currentLocale.getLanguage());
            instanceGroupName = instanceGroupName == null ? "" : instanceGroupName.trim();
            Map instanceGroup = (Map) provider.get(instanceGroupName);
            if (instanceGroup == null) {
                instanceGroup = new HashMap();
                provider.put(instanceGroupName, instanceGroup);
            }

            instanceGroup.put(instance.getInstanceId(), instance);
        }
        final String language = currentLocale.getLanguage();

        JavascriptTree tree = new JavascriptTree("Panels", 0);
        for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext();) {
            String providerGroupName = (String) itGroups.next();
            Map providerGroup = (Map) groups.get(providerGroupName);
            JavascriptTree providerGroupTree = new JavascriptTree(providerGroupName, 0);
            for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext();) {
                String providerId = (String) itProviders.next();
                JavascriptTree providerTree = new JavascriptTree(providerId, 0);
                Map instanceGroups = (Map) providerGroup.get(providerId);
                for (Iterator itInstanceGroups = instanceGroups.keySet().iterator(); itInstanceGroups.hasNext();) {
                    String instanceGroupName = (String) itInstanceGroups.next();
                    if ("".equals(instanceGroupName)) {
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, instance.getInstanceId().toString());
                            providerTree.addChildren(instanceTree);
                        }
                    } else {
                        JavascriptTree instanceGroupTree = new JavascriptTree(instanceGroupName, 0);
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, instance.getInstanceId().toString());
                            instanceGroupTree.addChildren(instanceTree);
                        }
                        providerTree.addChildren(instanceGroupTree);
                    }
                }
                providerGroupTree.addChildren(providerTree);
            }
            tree.addChildren(providerGroupTree);
        }
        cache = tree.toString();
        Map langMap = (Map) treeOfExistingInstancesWithoutAutoSelect.get(workspace.getId());
        if (langMap == null) {
            langMap = new HashMap();
            treeOfExistingInstancesWithoutAutoSelect.put(workspace.getId(), langMap);
        }
        langMap.put(currentLocale, cache);
        log.debug("Finished computation of Javascript tree for workspaces admin in " + (System.currentTimeMillis() - startTime) + " ms.");
        return cache;
    }

    /**
     * This method generates the javascript tree for the insert existing panel or new panel menu, that appears inside
     * a yellow window, and allows drag-and-drop.
     *
     * @return The text for the javascript tree.
     */
    public static synchronized String getTreeOfInstancesForDragAndDrop() {

        Locale currentLocale = LocaleManager.currentLocale();
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        String cache = null;
        Map cacheMap = (Map) treeOfInstancesForDragAndDrop.get(workspace.getId());
        if (cacheMap != null && (cache = (String) cacheMap.get(currentLocale)) != null)
            return cache;
        long startTime = System.currentTimeMillis();
        log.debug("Starting computation of Javascript tree for yellow window. ");
        PanelsProvidersManager providersMgr = UIServices.lookup().getPanelsProvidersManager();
        String[] groupList = providersMgr.enumerateProvidersGroups(workspace);
        PanelInstance[] instances = workspace.getPanelInstances();
        final String language = currentLocale.getLanguage();
        if (groupList == null || groupList.length == 0)
            return null;
        Map groups = new HashMap();
        String newPanelMessage = "!!!Nuevo Panel";
        ResourceBundle bundle = ResourceBundle.getBundle("org.jboss.dashboard.ui.messages", currentLocale);
        try {
            newPanelMessage = bundle.getString("ui.panel.new");
        } catch (MissingResourceException mre) {
        }
        for (int i = 0; i < groupList.length; i++) {
            String groupId = groupList[i];
            String groupName = providersMgr.getGroupDisplayName(groupId, currentLocale);
            Map group = (HashMap) groups.get(groupName);
            if (group == null) {
                group = new HashMap();
                groups.put(groupName, group);
            }
            PanelProvider[] providers = providersMgr.getProvidersInGroup(groupId, workspace);
            for (int j = 0; j < providers.length; j++) {
                Map provider = (HashMap) group.get(providers[j]);
                if (provider == null) {
                    provider = new HashMap();
                    group.put(providers[j], provider);
                }
                for (int k = 0; k < instances.length; k++) {
                    PanelInstance instance = instances[k];
                    if (instance.getProviderName().equals(providers[j].getId())) {
                        String instanceGroupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, language);
                        instanceGroupName = instanceGroupName == null ? "" : instanceGroupName.trim();
                        Map instanceGroup = (Map) provider.get(instanceGroupName);
                        if (instanceGroup == null) {
                            instanceGroup = new HashMap();
                            provider.put(instanceGroupName, instanceGroup);
                        }
                        instanceGroup.put(instance.getInstanceId(), instance);
                    }
                }
            }
        }

        JavascriptTree tree = new JavascriptTree("Panels", 0);
        for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext();) {
            String providerGroupName = (String) itGroups.next();
            Map providerGroup = (Map) groups.get(providerGroupName);
            JavascriptTree providerGroupTree = new JavascriptTree(providerGroupName, 0);
            for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext();) {
                PanelProvider provider = (PanelProvider) itProviders.next();
                String providerName = provider.getResource(provider.getDescription(), currentLocale);

                JavascriptTree providerTree = new JavascriptTree(providerName, 0);
                JavascriptTree newItemTree = new JavascriptTree(newPanelMessage, provider.getId());
                newItemTree.setForceFirst(true);
                providerTree.addChildren(newItemTree);
                Map instanceGroups = (Map) providerGroup.get(provider);
                for (Iterator itInstanceGroups = instanceGroups.keySet().iterator(); itInstanceGroups.hasNext();) {
                    String instanceGroupName = (String) itInstanceGroups.next();
                    if ("".equals(instanceGroupName)) {
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, instanceId.intValue());
                            providerTree.addChildren(instanceTree);
                        }
                    } else {
                        JavascriptTree instanceGroupTree = new JavascriptTree(instanceGroupName, 0);
                        Map instancesInGroup = (Map) instanceGroups.get(instanceGroupName);
                        for (Iterator itInstance = instancesInGroup.keySet().iterator(); itInstance.hasNext();) {
                            Long instanceId = (Long) itInstance.next();
                            PanelInstance instance = (PanelInstance) instancesInGroup.get(instanceId);
                            String title = LocalizeTag.getLocalizedValue(instance.getTitle(), language, true);
                            JavascriptTree instanceTree = new JavascriptTree(title, instanceId.intValue());
                            instanceGroupTree.addChildren(instanceTree);
                        }
                        providerTree.addChildren(instanceGroupTree);
                    }
                }
                providerGroupTree.addChildren(providerTree);
            }
            tree.addChildren(providerGroupTree);
        }

        cache = tree.toString();

        Map langMap = (Map) treeOfInstancesForDragAndDrop.get(workspace.getId());
        if (langMap == null) {
            langMap = new HashMap();
            treeOfInstancesForDragAndDrop.put(workspace.getId(), langMap);
        }
        langMap.put(currentLocale, cache);
        log.debug("Finished computation of Javascript tree for yellow window in " + (System.currentTimeMillis() - startTime) + " ms.");
        return cache;
    }

    /**
     * This method generates the javascript tree for the new panel menu, that appears inside the workspace admin tabs.
     *
     * @return The text for the javascript tree.
     */
    public static synchronized String getTreeOfInstancesForNewInstance() {

        Locale currentLocale = LocaleManager.currentLocale();
        WorkspaceImpl workspace = NavigationManager.lookup().getCurrentWorkspace();
        String cache = null;
        Map cacheMap = (Map) treeOfInstancesForNewInstance.get(workspace.getId());
        if (cacheMap != null && (cache = (String) cacheMap.get(currentLocale)) != null)
            return cache;

        long startTime = System.currentTimeMillis();
        log.debug("Starting computation of Javascript tree for new panel instance. ");

        PanelsProvidersManager providersMgr = UIServices.lookup().getPanelsProvidersManager();
        String[] groupList = providersMgr.enumerateProvidersGroups(workspace);
        if (groupList == null || groupList.length == 0)
            return null;
        Map groups = new HashMap();
        for (int i = 0; i < groupList.length; i++) {
            String groupId = groupList[i];
            String groupName = providersMgr.getGroupDisplayName(groupId, currentLocale);
            Map group = (HashMap) groups.get(groupName);
            if (group == null) {
                group = new HashMap();
                groups.put(groupName, group);
            }
            PanelProvider[] providers = providersMgr.getProvidersInGroup(groupId, workspace);
            for (int j = 0; j < providers.length; j++) {
                Map provider = (HashMap) group.get(providers[j]);
                if (provider == null) {
                    provider = new HashMap();
                    group.put(providers[j], provider);
                }
            }
        }

        JavascriptTree tree = new JavascriptTree("Panels", 0);
        for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext();) {
            String providerGroupName = (String) itGroups.next();
            Map providerGroup = (Map) groups.get(providerGroupName);
            JavascriptTree providerGroupTree = new JavascriptTree(providerGroupName, 0);
            for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext();) {
                PanelProvider provider = (PanelProvider) itProviders.next();
                String providerName = provider.getResource(provider.getDescription(), currentLocale);
                JavascriptTree providerTree = new JavascriptTree(providerName, provider.getId());
                providerGroupTree.addChildren(providerTree);
            }
            tree.addChildren(providerGroupTree);
        }

        cache = tree.toString();

        Map langMap = (Map) treeOfInstancesForNewInstance.get(workspace.getId());
        if (langMap == null) {
            langMap = new HashMap();
            treeOfInstancesForNewInstance.put(workspace.getId(), langMap);
        }
        langMap.put(currentLocale, cache);

        log.debug("Finished computation of Javascript tree for new panel instance in " + (System.currentTimeMillis() - startTime) + " ms.");
        return cache;
    }

    /**
     * This method generates the javascript tree for the new panel menu, that appears inside the workspace admin tabs.
     *
     * @return The text for the javascript tree.
     */
    public static synchronized String getTreeOfInstancesForNewInstance(Workspace selectWorkspace) {

        Locale currentLocale = SessionManager.getCurrentLocale();
        WorkspaceImpl workspace = (WorkspaceImpl) selectWorkspace;
        String cache = null;
        Map cacheMap = (Map) treeOfInstancesForNewInstance.get(workspace.getId());
        if (cacheMap != null && (cache = (String) cacheMap.get(currentLocale)) != null)
            return cache;

        long startTime = System.currentTimeMillis();
        log.debug("Starting computation of Javascript tree for new panel instance. ");

        PanelsProvidersManager providersMgr = UIServices.lookup().getPanelsProvidersManager();
        String[] groupList = providersMgr.enumerateProvidersGroups(workspace);
        if (groupList == null || groupList.length == 0)
            return null;
        Map groups = new HashMap();
        for (int i = 0; i < groupList.length; i++) {
            String groupId = groupList[i];
            String groupName = providersMgr.getGroupDisplayName(groupId, currentLocale);
            Map group = (HashMap) groups.get(groupName);
            if (group == null) {
                group = new HashMap();
                groups.put(groupName, group);
            }
            PanelProvider[] providers = providersMgr.getProvidersInGroup(groupId, workspace);
            for (int j = 0; j < providers.length; j++) {
                Map provider = (HashMap) group.get(providers[j]);
                if (provider == null) {
                    provider = new HashMap();
                    group.put(providers[j], provider);
                }
            }
        }

        JavascriptTree tree = new JavascriptTree("Panels", 0);
        for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext();) {
            String providerGroupName = (String) itGroups.next();
            Map providerGroup = (Map) groups.get(providerGroupName);
            JavascriptTree providerGroupTree = new JavascriptTree(providerGroupName, 0);
            for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext();) {
                PanelProvider provider = (PanelProvider) itProviders.next();
                String providerName = provider.getResource(provider.getDescription(), currentLocale);
                JavascriptTree providerTree = new JavascriptTree(providerName, provider.getId());
                providerGroupTree.addChildren(providerTree);
            }
            tree.addChildren(providerGroupTree);
        }

        cache = tree.toString();

        Map langMap = (Map) treeOfInstancesForNewInstance.get(workspace.getId());
        if (langMap == null) {
            langMap = new HashMap();
            treeOfInstancesForNewInstance.put(workspace.getId(), langMap);
        }
        langMap.put(currentLocale, cache);

        log.debug("Finished computation of Javascript tree for new panel instance in " + (System.currentTimeMillis() - startTime) + " ms.");
        return cache;
    }

    public static String getTreeOfComponents() {
        Map mappings = Application.lookup().getGlobalFactory().getTree().getTreeMappings();
        JavascriptTree[] children = getTreeForMap(mappings);
        if (children.length == 1) {
            return children[0].toString();
        } else {
            JavascriptTree tree = new JavascriptTree("Components", "");
            for (int i = 0; i < children.length; i++) {
                JavascriptTree child = children[i];
                tree.addChildren(child);
            }
            return tree.toString();
        }
    }

    protected static JavascriptTree[] getTreeForMap(Map map) {
        if (map == null || map.isEmpty())
            return null;
        List trees = new ArrayList();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (value instanceof Map) {
                JavascriptTree tree = new JavascriptTree(key, "");
                JavascriptTree[] children = getTreeForMap((Map) value);
                for (int i = 0; i < children.length; i++) {
                    JavascriptTree javascriptTree = children[i];
                    tree.addChildren(javascriptTree);
                }
                trees.add(tree);
            } else /*It is a Component*/ {
                JavascriptTree tree = new JavascriptTree(key, ((Component) value).getName());
                trees.add(tree);
            }
        }
        return (JavascriptTree[]) trees.toArray(new JavascriptTree[trees.size()]);
    }
}
