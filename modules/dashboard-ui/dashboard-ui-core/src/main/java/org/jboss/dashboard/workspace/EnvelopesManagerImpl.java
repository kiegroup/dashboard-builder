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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.workspace.EnvelopesManager;
import org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor;
import org.jboss.dashboard.ui.resources.Envelope;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class EnvelopesManagerImpl extends GraphicElementManagerImpl implements EnvelopesManager {

    @Inject @Config("WEB-INF/etc/envelopes")
    private String envelopesDir;

    @Inject @Config("")
    private String[] beforeHeaderIncludePages;

    @Inject @Config("/templates/styles.jsp," +
                    "/components/bam/displayer/chart/nvd3_css.jsp")
    private String[] headerIncludePages;

    @Inject @Config("")
    private String[] finalBodyIncludePages;

    @Inject @Config("/templates/js.jsp")
    private String scriptsIncludePage;

    public void start() throws Exception {
        super.classToHandle = Envelope.class;
        super.baseDir = envelopesDir;
        super.start();
    }

    /**
     * Determine the scope for the element handled by this manager.
     *
     * @return the scope for the element handled by this manager.
     */
    public GraphicElementScopeDescriptor getElementScopeDescriptor() {
        return GraphicElementScopeDescriptor.SECTION_SCOPED;
    }

    public String[] getHeaderIncludePages() {
        return headerIncludePages;
    }

    public void setHeaderIncludePages(String[] headerIncludePages) {
        this.headerIncludePages = headerIncludePages;
    }

    public String[] getFinalBodyIncludePages() {
        return finalBodyIncludePages;
    }

    public void setFinalBodyIncludePages(String[] finalBodyIncludePages) {
        this.finalBodyIncludePages = finalBodyIncludePages;
    }

    public String getScriptsIncludePage() {
        return scriptsIncludePage;
    }

    public void setScriptsIncludePage(String scriptsIncludePage) {
        this.scriptsIncludePage = scriptsIncludePage;
    }

    public String[] getBeforeHeaderIncludePages() {
        return beforeHeaderIncludePages;
    }

    public void setBeforeHeaderIncludePages(String[] beforeHeaderIncludePages) {
        this.beforeHeaderIncludePages = beforeHeaderIncludePages;
    }

    public List<String> getHeaderPagesToInclude() {
        List result = new ArrayList();
        if (getHeaderIncludePages() != null) {
            for (int i = 0; i < getHeaderIncludePages().length; i++) {
                result.add(getHeaderIncludePages()[i]);
            }
        }

        NavigationManager navigationManager = NavigationManager.lookup();
        if (navigationManager != null && navigationManager.getCurrentSection() != null) {
            Set panels = navigationManager.getCurrentSection().getPanels();
            if (panels != null) {
                for (Iterator it = panels.iterator(); it.hasNext();) {
                    Panel panel = (Panel) it.next();
                    String page = panel.getProvider().getPage(PanelDriver.PAGE_HEADER);
                    if (page != null && !"".equals(page.trim()) && !result.contains(page)) result.add(page);
                }
            }
        }
        return result;
    }
}
