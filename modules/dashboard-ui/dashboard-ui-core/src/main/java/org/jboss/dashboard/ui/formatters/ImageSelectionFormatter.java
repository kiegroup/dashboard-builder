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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.ResourceGalleryManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Set;

/**
 * This class extends Formatter to provide support for the rendering of the edit page of advanced HTML panel.
 * <p/>
 * It expects the following input parameters:
 * <ul>
 * <li> numCols. String representation of a positive integer. Number of columns to use for resources.
 * </ul>
 * It serves the following output fragments, with given output attributes:
 * <ul>
 * <li> outputStart. At the beginning, when there are galleries to show.
 * <li> empty. At the beginning, when there are no galleries to show.
 * <li> galleryOutputStart. At the beginning, for every gallery, when there are galleries to show. It receives the following attributes:
 * <ul>
 * <li> galleryName. Displayable gallery name.
 * <li> galleryIndex. 0-based gallery index.
 * </ul>
 * <li> galleryResourceRowStart. Before starting a row with resources
 * <p/>
 * <li> galleryResourceOutput. After the languages. It receives the following attributes:
 * <ul>
 * <li> resourceUrl. URL for the resource shown.
 * <li> resourceId. ID for the resource shown.
 * <li> resourceIndex. 0-based resource index.
 * <li> galleryIndex. 0-based gallery index.
 * <li> resourceUID. Unique page ID for the resource shown.
 * </ul>
 * <p/>
 * <li> galleryResourceRowEnd. After finishing a row with resources
 * <li> galleryOutputEnd. At the end, for every gallery, when there are galleries to show.
 * <li> outputEnd. At the end, when there are galleries to show.
 * </ul>
 */
public class ImageSelectionFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ImageSelectionFormatter.class.getName());
    private NavigationManager navigationManager;

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String numCols = (String) getParameter("numCols");
        numCols = numCols == null ? "3" : numCols;
        int cols = Integer.parseInt(numCols);
        Section currentSection = getNavigationManager().getCurrentSection();
        ResourceGalleryManager resourceGalleryManager = UIServices.lookup().getResourceGalleryManager();
        GraphicElement[] galleries =
                currentSection != null ?
                        resourceGalleryManager.getAvailableElements(currentSection.getWorkspace().getId(), currentSection.getId(), null) :
                        resourceGalleryManager.getAvailableElements();

        if (galleries.length > 0) {
            renderFragment("outputStart");
            for (int i = 0; i < galleries.length; i++) {
                GraphicElement gallery = galleries[i];
                setAttribute("galleryName", gallery.getDescription().get(getLang()));
                setAttribute("galleryIndex", i);
                renderFragment("galleryOutputStart");
                Set resourcesSet = gallery.getResources();
                String[] resources = (String[]) resourcesSet.toArray(new String[resourcesSet.size()]);
                Arrays.sort(resources);
                for (int j = 0; j < resources.length; j++) {
                    if (j % cols == 0)
                        renderFragment("galleryResourceRowStart");
                    String resourceId = resources[j];
                    Resource resource = gallery.getResource(gallery.getRelativeResourceName(resourceId), getLang());
                    String resourceURL = resource.getResourceUrl(request, response, true);
                    //Make sure it is a relative url like Controller?mvchandler=...
                    if (resourceURL.startsWith(request.getContextPath())) {
                        resourceURL = resourceURL.substring((request.getContextPath()).length());
                    }
                    while (resourceURL.startsWith("/")) resourceURL = resourceURL.substring(1);
                    setAttribute("resourceUrl", resourceURL);
                    setAttribute("resourceId", resourceId);
                    setAttribute("galleryIndex", i);
                    setAttribute("resourceIndex", j);
                    setAttribute("resourceUID", "res_" + i + "_" + j);//Resource unique identifier within current page.
                    renderFragment("galleryResourceOutput");
                    if ((j + 1) % cols == 0)
                        renderFragment("galleryResourceRowEnd");
                }
                renderFragment("galleryOutputEnd");
            }
            renderFragment("outputEnd");
        } else {
            renderFragment("empty");
        }
    }
}