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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.LayoutRegion;
import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.workspace.LayoutRegion;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.ui.resources.Layout;
import org.hibernate.Session;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Properties;

public class RegionTag extends BaseTag {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RegionTag.class.getName());

    /**
     * Region
     */
    private String region = null;

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspException {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Throwable {
                Properties displayConfiguration = (Properties) Factory.lookup("org.jboss.dashboard.ui.formatters.DisplayConfiguration");
                pageContext.setAttribute(Parameters.RENDER_IDREGION, getRegion(), PageContext.REQUEST_SCOPE);

                String preview = (String) pageContext.getRequest().getAttribute("org.jboss.dashboard.ui.taglib.RegionTag.preview");
                if (preview != null && preview.trim().equalsIgnoreCase("true")) {
                    //PREVIEW
                    String layoutId = (String) pageContext.getRequest().getAttribute("org.jboss.dashboard.ui.taglib.RegionTag.layout");
                    Layout layout = (Layout) UIServices.lookup().getLayoutsManager().getAvailableElement(layoutId);
                    LayoutRegion layoutRegion = layout.getRegion(region);
                    String pageStr = "/section/render_region.jsp";

                    int dotIndex = pageStr.lastIndexOf('.');
                    pageStr = pageStr.substring(0, dotIndex) + "_preview" + pageStr.substring(dotIndex, pageStr.length());
                    pageContext.getRequest().setAttribute("layoutRegion", layoutRegion);
                    jspInclude(pageStr);
                    pageContext.getRequest().removeAttribute("layoutRegion");
                } else {
                    // NORMAL DISPLAY
                    Section section = NavigationManager.lookup().getCurrentSection();
                    if (section != null) {
                        LayoutRegion layoutRegion = section.getLayout().getRegion(getRegion());
                        if (layoutRegion != null) {
                            String pageStr = displayConfiguration.getProperty("regionRenderPage");
                            log.debug("REGION TAG: INCLUDING (" + layoutRegion.getId() + ") " + pageStr);
                            jspInclude(pageStr);
                        }
                    }
                }
            }}.execute();
            return EVAL_PAGE;
        } catch (Throwable e) {
            throw new JspException(e);
        }
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
