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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Section;

public class SectionBreadCrumbItem implements BreadCrumbItem {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SectionBreadCrumbItem.class.getName());

    private Section section;
    private URLMarkupGenerator urlMarkupGenerator;
    private LocaleManager localeManager;

    public SectionBreadCrumbItem(Section section, URLMarkupGenerator urlMarkupGenerator, LocaleManager localeManager) {
        this.section = section;
        this.urlMarkupGenerator = urlMarkupGenerator;
        this.localeManager = localeManager;
    }

    public String getURL() {
        return urlMarkupGenerator.getLinkToPage(section, true);
    }

    public String getName() {
        return (String) localeManager.localize(section.getTitle());
    }
}
