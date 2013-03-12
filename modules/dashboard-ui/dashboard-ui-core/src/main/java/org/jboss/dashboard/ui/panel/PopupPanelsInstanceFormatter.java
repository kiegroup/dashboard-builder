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
package org.jboss.dashboard.ui.panel;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.PanelInstance;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class PopupPanelsInstanceFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PopupPanelsInstanceFormatter.class.getName());

    public static int instancesInPage = 9;
    public static int truncateSize = 30;

    private PopupPanelsHandler handler;

    public PopupPanelsHandler getHandler() {
        return handler;
    }

    public void setHandler(PopupPanelsHandler handler) {
        this.handler = handler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {

        Map categories = handler.prepareGroupsMap();
        setAttribute("categoryId", getHandler().getShowedGroupId());
        if (categories != null) {
            for (Iterator it = categories.keySet().iterator(); it.hasNext(); ) {
                Map providers = (Map) categories.get(it.next());
                if (providers != null) {

                    for (Iterator it2 = providers.keySet().iterator(); it2.hasNext(); ) {
                        PanelProvider provider = (PanelProvider) it2.next();
                        Map providerMap = (Map) providers.get(provider);
                        if (provider.getId().equals(getHandler().getShowedPanelInstanceId())) {
                            if (getHandler().getShowedPanelSubgroupId()==null) {
                                renderInstances(provider.getId(), "", (Map) providerMap.get(""));
                            } else {
                                for (Iterator it3 = providerMap.keySet().iterator(); it3.hasNext(); ) {
                                    String groupName = (String) it3.next();
                                    if (groupName != null && !groupName.equals("") && groupName.equals(handler.getShowedPanelSubgroupId()))
                                        renderInstances(provider.getId(), groupName, (Map) providerMap.get(groupName));
                                }
                            }
                        }

                    }

                }
            }

            renderFragment("outputEnd");
        }
    }

    protected void renderInstances(String providerId, String groupName, Map instances) {
        int uid = 0;
        String id = providerId;
        if (groupName != null && !"".equals(groupName)) id += "_" + groupName;
//        setAttribute("id", id);
//        renderFragment("outputStartDiv");
        setAttribute("id", id);
        setAttribute("uid", uid++);
        setAttribute("providerId", providerId);
        renderFragment("outputNewPanels");
        if (instances != null) {
            List sortedInstances = new ArrayList(instances.size());

            for (Iterator itInstance = instances.keySet().iterator(); itInstance.hasNext(); ) {
                Long instanceId = (Long) itInstance.next();
                PanelInstance instance = (PanelInstance) instances.get(instanceId);
                String title = (String) LocaleManager.lookup().localize(instance.getTitle());
                Map mapRepresentation = new HashMap();
                mapRepresentation.put("title", title);
                mapRepresentation.put("instanceId", instanceId);
                sortedInstances.add(mapRepresentation);
            }
            Collections.sort(sortedInstances, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map) o1).get("title")).compareTo(((Map) o2).get("title"));
                }
            });

            String pageToShow = getHandler().getShowedPanelInstancePage();
            int currentPage = 0;
            try {
                currentPage = Integer.parseInt(pageToShow);
                if (currentPage < 0) currentPage = 0;
                int lastPage = sortedInstances.size() / instancesInPage;
                if (currentPage > lastPage) currentPage = lastPage;
            } catch (Exception e) {
                currentPage = 0;
            }
            int i;
            int j = 0;
            Map panelStatistics = calculatePanelsStatistics(handler.getNavigationManager().getCurrentWorkspace());

            for (i = (currentPage * instancesInPage); (i < sortedInstances.size() && i < (currentPage * instancesInPage) + instancesInPage); i++, j++) {
                Map map = (Map) sortedInstances.get(i);

                setAttribute("name", truncateString((String) map.get("title"), truncateSize));
                setAttribute("title", map.get("title"));
                Integer count = (Integer) panelStatistics.get(map.get("instanceId"));

                if (count == null) count = 0;

                setAttribute("instancesCount", count);
                setAttribute("uid", uid++);
                setAttribute("instanceId", map.get("instanceId"));
                setAttribute("id", id);
                if (j % 2 == 0)
                    setAttribute("position", "even");
                else
                    setAttribute("position", "odd");

                renderFragment("outputInstance");
            }
            renderPagination(i, currentPage, sortedInstances.size(), providerId);

        } else {
            renderFragment("endWithoutPagination");
        }
        setAttribute("id", id);
        setAttribute("providerId", providerId);
        setAttribute("maxUid", uid);
        renderFragment("outputEndDiv");


    }

    public void renderPagination(int lastShowedItem, int currentPage, int elements, String providerId) {
        if (elements > instancesInPage) {
            renderFragment("startPagination");
            if (currentPage == 0 && elements > instancesInPage) {
                renderFragment("outputPreviousPageDisabled");
            } else {
                setAttribute("page", currentPage - 1);
                setAttribute("categoryId", getHandler().getShowedGroupId());
                setAttribute("subCategoryId", getHandler().getShowedPanelSubgroupId());
                setAttribute("providerId", providerId);
                renderFragment("outputPreviousPageEnabled");
            }
            if (lastShowedItem < elements) { //
                setAttribute("page", currentPage + 1);
                setAttribute("categoryId", getHandler().getShowedGroupId());
                setAttribute("subCategoryId", getHandler().getShowedPanelSubgroupId());
                setAttribute("providerId", providerId);
                renderFragment("outputNextPageEnabled");
            } else {
                renderFragment("outputNextPageDisabled");
            }
            renderFragment("endPagination");
        } else {
            renderFragment("endWithoutPagination");

        }
    }

    private String truncateString(String cad, int size) {
        if (cad.length() > size) {
            String truncateStr = "";
            int i = 0;
            StringTokenizer st = new StringTokenizer(cad, " ");
            String aux = "";
            while (st.hasMoreTokens()) {
                aux = st.nextToken();
                if (truncateStr.length() + aux.length() + 1 > size) {
                    return truncateStr + "...";
                }
                truncateStr += " " + aux;
            }
            return truncateStr;
        }
        return cad;

    }
    protected Map calculatePanelsStatistics(WorkspaceImpl workspace) {
        Set sections = workspace.getSections();
        HashMap result = new HashMap();
        for (Iterator iterator = sections.iterator(); iterator.hasNext();) {
            Section section = (Section) iterator.next();
            Set panels = section.getPanels();
            for (Iterator iterator1 = panels.iterator(); iterator1.hasNext();) {
                Panel panel = (Panel) iterator1.next();
                Long instanceId = panel.getInstanceId();
                Integer instanceCount = (Integer) result.get(instanceId);
                if (instanceCount == null) {
                    result.put(instanceId, new Integer(1));
                } else {
                    result.put(instanceId, new Integer(1 + instanceCount.intValue()));
                }
            }
        }
        return result;
    }
}