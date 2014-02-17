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
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class PopupPanelsGroupFormatter extends Formatter {

    @Inject
    private PopupPanelsHandler handler;

    public PopupPanelsHandler getHandler() {
        return handler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        Locale currentLocale = LocaleManager.currentLocale();
        Map groups = handler.prepareGroupsMap();

        if (groups == null || groups.size() == 0) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            //Render
            List sortedGroups = new ArrayList(groups.keySet().size());
            int uid = 0;
            for (Iterator itGroups = groups.keySet().iterator(); itGroups.hasNext(); ) {
                String groupId = (String) itGroups.next();
                Map providerGroup = (Map) groups.get(groupId);
                Map group = new HashMap();
                group.put("groupId", groupId);
                group.put("uid", new Integer(uid++));
                group.put("name", UIServices.lookup().getPanelsProvidersManager().getGroupDisplayName(groupId, currentLocale));
                group.put("groupThumbnail", UIServices.lookup().getPanelsProvidersManager().getProviderGroupImage(groupId));
                group.put("providerGroup", providerGroup);
                sortedGroups.add(group);
            }
            Collections.sort(sortedGroups, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map) o1).get("name")).compareTo(((Map) o2).get("name"));
                }
            });
            for (int i = 0; i < sortedGroups.size(); i++) {
                Map map = (Map) sortedGroups.get(i);
                String groupId = (String) map.get("groupId");
                setAttribute("groupId", groupId);
                setAttribute("uid", map.get("uid"));
                setAttribute("name", map.get("name"));
                setAttribute("groupThumbnail", map.get("groupThumbnail"));
                if (groupId.equals(handler.getShowedGroupId())) {
                    renderFragment("outputOpenedCategory");
                } else {
                    renderFragment("outputCategoryStart");
                }
                Map providerGroup = (Map) map.get("providerGroup");

                List sortedProviders = new ArrayList(providerGroup.keySet().size());
                for (Iterator itProviders = providerGroup.keySet().iterator(); itProviders.hasNext(); ) {
                    PanelProvider provider = (PanelProvider) itProviders.next();
                    String providerName = provider.getResource(provider.getDescription(), currentLocale);
                    Map providerMap = new HashMap();
                    providerMap.put("thumbnail", provider.getThumbnail());
                    providerMap.put("provider", provider);
                    providerMap.put("name", providerName);
                    providerMap.put("categoryId", groupId);
                    providerMap.put("providerId", provider.getId());
                    sortedProviders.add(providerMap);
                }
                Collections.sort(sortedProviders, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Comparable) ((Map) o1).get("name")).compareTo(((Map) o2).get("name"));
                    }
                });
                for (int j = 0; j < sortedProviders.size(); j++) {
                    int uidForProvider = uid++;
                    Map providerMap = (Map) sortedProviders.get(j);
                    PanelProvider provider = (PanelProvider) providerMap.get("provider");

                    setAttribute("name", providerMap.get("name"));
                    setAttribute("title", providerMap.get("name"));
                    setAttribute("uid", uidForProvider);
                    setAttribute("categoryId", groupId);
                    setAttribute("providerId", provider.getId());
                    setAttribute("thumbnail", provider.getThumbnail());
                    if (provider.getId().equals(getHandler().getShowedPanelInstanceId())) {
                        setAttribute("isSelected", Boolean.TRUE);
                    } else {
                        setAttribute("isSelected", Boolean.FALSE);
                    }

                    renderFragment("outputProviderStart");

                    if (handler.getShowedPanelInstanceId() != null && handler.getShowedPanelInstanceId().equals(provider.getId()))  {

                        Map instanceGroups = (Map) providerGroup.get(provider);
                        List sortedInstanceGroups = new ArrayList();
                        for (Iterator itInstanceGroups = instanceGroups.keySet().iterator(); itInstanceGroups.hasNext(); ) {
                            String instanceGroupName = (String) itInstanceGroups.next();
                            if (!"".equals(instanceGroupName)) {
                                sortedInstanceGroups.add(instanceGroupName);
                            }
                        }
                        Collections.sort(sortedInstanceGroups);
                        for (int k = 0; k < sortedInstanceGroups.size(); k++) {
                            String instanceGroupName = (String) sortedInstanceGroups.get(k);
                            setAttribute("categoryId", groupId);
                            setAttribute("providerId", provider.getId());
                            setAttribute("subcategoryId", instanceGroupName);
                            setAttribute("name", instanceGroupName);
                            setAttribute("uid", uidForProvider);
                            if(instanceGroupName.equals(handler.getShowedPanelSubgroupId())){
                                setAttribute("isSelected", Boolean.TRUE);
                            } else {
                                setAttribute("isSelected", Boolean.FALSE);
                            }
                            renderFragment("outputGroup");
                        }
                    }

                    //renderFragment("outputProviderEnd");
                }

                //renderFragment("outputCategoryEnd");
            }
            renderFragment("outputEnd");
        }
    }

    protected boolean isSelected(String handlerValue, String currentValue) {
        if (currentValue == null || currentValue.equals("")) return false;
        if (handlerValue == null || handlerValue.equals("")) return false;
        return currentValue.equals(handlerValue);
    }
}
