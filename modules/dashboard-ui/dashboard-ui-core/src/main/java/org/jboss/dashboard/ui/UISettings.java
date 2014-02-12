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
package org.jboss.dashboard.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

/**
 * Several UI display settings
 */
@ApplicationScoped
public class UISettings {

    public static UISettings lookup() {
        return CDIBeanLocator.getBeanByType(UISettings.class);
    }

    /**
     * Determines the page that renders every region.
     * Possible values are:<ul>
     * <li>/section/render_region.jsp: Old style. Panels have status bars</li>
     * <li>/section/render_simple_region.jsp: New style. Panels have a menu.</li>
     * </ul>
     */
    @Inject @Config("/section/render_simple_region.jsp")
    protected String regionRenderPage;

    /**
     * Determines the page that renders the menu for every panel
     * Possible values are:<ul>
     * <li>/section/render_panel_menu.jsp: Shows a menu like Windows context menus</li>
     * <li> /section/render_panel_mini_menu.jsp: Shows a mini menu with buttons only</li>
     * </ul>
     */
    @Inject @Config("/section/render_panel_menu.jsp")
    protected String panelMenuRenderPage;

    public String getRegionRenderPage() {
        return regionRenderPage;
    }

    public String getPanelMenuRenderPage() {
        return panelMenuRenderPage;
    }
}
