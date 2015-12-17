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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.taglib.LocalizeTag;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.jboss.dashboard.LocaleManager;

/**
 * This class extends Formatter to provide support for rendering graphic resources.
 * <p/>
 * It expects the following input parameters:
 * <ul>
 * <li>workspaceId. Workspace id where items should belong. Null to include all.
 * <li>sectionId. Section id where items should belong. Null to include all.
 * <li>panelId. Panel id  where items should belong. Null to include all.
 * <li>resourceType. Type of resource to show (skin, layout, ...). Null to include all.
 * <li>includeBase. Determine if base resources must be included
 * </ul>
 * <p/>
 * It serves the following output fragments, with given output parameters:
 * <ul>
 * <li> outputStart. At the beginning of the iteration, if the list is not empty
 * <li> output. For every item in the list. It receives the following attributes:
 * <ul>
 * <li> graphicElement. Element to show.
 * <li> description. Element description in current language.
 * <li> id. Element id
 * <li> class. Element class
 * <li> type. Element type
 * <li> index. 0-based position of item in the list.
 * <li> count. 1-based position of item in the list.
 * </ul>
 * <li> outputEnd. At the end of the iteration, if the list is not empty.
 * <li> empty.If the list is empty.
 * </ul>
 */
public class RenderResourcesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    /**
     * Perform the required logic for this Formatter. Inside, the methods
     * setAttribute and renderFragment are intended to be used to generate the
     * output and set parameters for this output.
     * Method getParameter is intended to retrieve input parameters by name.
     * <p/>
     * Exceptions are to be catched inside the method, and not to be thrown, normally,
     * formatters could use a error fragment to be displayed when an error happens
     * in displaying. But if the error is unexpected, it can be wrapped inside a
     * FormatterException.
     *
     * @param request  user request
     * @param response response to the user
     * @throws org.jboss.dashboard.ui.taglib.formatter.FormatterException
     *          in case of an unexpected exception.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Object oWorkspaceId = getParameter("workspaceId");
        String workspaceId = oWorkspaceId == null ? null : String.valueOf(oWorkspaceId);

        Object oSectionId = getParameter("sectionId");
        Long sectionId = oSectionId == null ? null : Long.valueOf(String.valueOf(oSectionId));

        Object oPanelId = getParameter("panelId");
        Long panelId = oPanelId == null ? null : Long.valueOf(String.valueOf(oPanelId));

        Object oResourceType = getParameter("resourceType");
        String resourceType = oResourceType == null ? null : String.valueOf(oResourceType);

        Object oIncludeBase = getParameter("includeBase");
        boolean includeBase = oIncludeBase == null || Boolean.valueOf(String.valueOf(oIncludeBase)).booleanValue();

        List resourcesToShow = new ArrayList();

        GraphicElementManager[] resourcesManagers = UIServices.lookup().getGraphicElementManagers();
        for (int i = 0; i < resourcesManagers.length; i++) {
            GraphicElementManager manager = resourcesManagers[i];
            GraphicElement[] elements = manager.getElements(workspaceId, sectionId, panelId);
            for (int j = 0; j < elements.length; j++) {
                GraphicElement graphicElement = elements[j];
                if ((resourceType != null && resourceType.equals(graphicElement.getCategoryName())) || resourceType == null) {
                    if (manager.isBaseElement(graphicElement) && !includeBase) {
                        log.debug("Excluding global element from selection.");
                    } else {
                        resourcesToShow.add(graphicElement);
                        log.debug("Adding element to selection.");
                    }
                }
            }
        }

        if (resourcesToShow.isEmpty()) {
            renderFragment("empty");
        } else {
            renderFragment("outputStart");
            for (int i = 0; i < resourcesToShow.size(); i++) {
                GraphicElement graphicElement = (GraphicElement) resourcesToShow.get(i);
                setAttribute("graphicElement", graphicElement);
                setAttribute("description", LocalizeTag.getLocalizedValue(graphicElement.getDescription(), LocaleManager.currentLang(), true));
                setAttribute("id", graphicElement.getId());
                setAttribute("class", graphicElement.getClass().getName());
                setAttribute("type", graphicElement.getCategoryName());
                setAttribute("count", i + 1);
                setAttribute("index", i);
                renderFragment("output");
            }
            renderFragment("outputEnd");
        }
    }
}
