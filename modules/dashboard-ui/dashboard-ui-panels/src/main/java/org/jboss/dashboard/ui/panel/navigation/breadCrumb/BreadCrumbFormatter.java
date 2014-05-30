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
package org.jboss.dashboard.ui.panel.navigation.breadCrumb;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.Section;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadCrumbFormatter extends Formatter {

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        List<Section> pagesList = new ArrayList<Section>();
        Section page = getSection();
        do {
            pagesList.add(page);
            page = page.getParent();
        } while (page != null);

        Collections.reverse(pagesList);
        List<SectionBreadCrumbItem> itemsList = new ArrayList<SectionBreadCrumbItem>();
        for (Section section : pagesList) {
            itemsList.add(new SectionBreadCrumbItem(section, UIServices.lookup().getUrlMarkupGenerator(), getLocaleManager()));
        }
        BreadCrumbRenderingInfo renderingInfo = new BreadCrumbRenderingInfo();
        renderingInfo.setRenderingItems(itemsList);
        renderingInfo.setSeparator(((BreadCrumbDriver) getPanel().getProvider().getDriver()).getSeparator(getPanel()));
        renderingInfo.setInitialTrimDepth(((BreadCrumbDriver) getPanel().getProvider().getDriver()).getInitialTrimDepth(getPanel()));
        renderingInfo.setItemTemplate(((BreadCrumbDriver) getPanel().getProvider().getDriver()).getTemplate(getPanel()));
        renderItems(renderingInfo);
    }

    protected void renderItems(BreadCrumbRenderingInfo renderingInfo) {
        List<SectionBreadCrumbItem> itemsList = renderingInfo.getItemsToRender();
        if (itemsList != null && !itemsList.isEmpty()) {
            renderFragment("outputStart");
            for (int i = 0; i < itemsList.size(); i++) {
                BreadCrumbItem item = (BreadCrumbItem) itemsList.get(i);
                if (i > 0)
                    renderSeparator(renderingInfo);
                renderItem(renderingInfo, item);
            }
            renderFragment("outputEnd");
        }
    }

    protected void renderSeparator(BreadCrumbRenderingInfo renderingInfo) {
        writeToOut(renderingInfo.getSeparator());
    }

    protected void renderItem(BreadCrumbRenderingInfo renderingInfo, BreadCrumbItem item) {
        writeToOut(renderingInfo.getTextForItem(item));
    }
}
