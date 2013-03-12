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
package org.jboss.dashboard.ui.panel.navigation.treeMenu;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class TreeMenuFormatter extends Formatter {

    protected boolean isEditMode = false;
    protected boolean allSectionsOpen = false;
    private List openedPages = new ArrayList();

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        String editModeStr = (String) getParameter("editMode");
        isEditMode = editModeStr != null && Boolean.valueOf(editModeStr).booleanValue();
        String allSectionsOpenStr = (String) getParameter("allSectionsOpen");
        allSectionsOpen = allSectionsOpenStr != null && Boolean.valueOf(allSectionsOpenStr).booleanValue();

        String markOppenedSections = getPanel().getParameterValue(TreeMenuDriver.PARAM_MARK_OPENED_SECTIONS);

        Section section = getSection();
        openedPages.add(section.getDbid());


        if (Boolean.parseBoolean(markOppenedSections)) {
            while ((section = section.getParent()) != null) {
                openedPages.add(section.getDbid());
            }
        }

        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
        Section[] rootSections = workspace.getAllRootSections();
        renderFragment("outputStart");
        if (isEditMode) {
            if (rootSections != null) {
                for (int i = 0; i < rootSections.length; i++) {
                    Section rootSection = rootSections[i];
                    setAttribute("pageId", rootSection.getId());
                    setAttribute("checked", isChecked(rootSection));
                    setAttribute("sectionName", getLocalizedValue(rootSection.getTitle()));
                    renderFragment("pageStart");
                    renderSectionEditionMode(rootSection, 1);
                    renderFragment("pageEnd");
                }
            }
        } else {
            if (rootSections != null) {
                for (int i = 0; i < rootSections.length; i++) {
                    Section rootSection = rootSections[i];
                    renderSection(rootSection, 1);
                }
            }
        }
        if (isEditMode)
            setAttribute("checked", areAllChecked());
        renderFragment("outputEnd");
    }

    protected void renderSectionEditionMode(Section section, int level) {
        if (isShowable(section)) {
            if (hasVisibleChildren(section) && isOpen(section)) {
                renderFragment("beforeTabulation");
                List children = section.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Section childSection = (Section) children.get(i);
                    setAttribute("checked", isChecked(childSection));
                    setAttribute("sectionName", getLocalizedValue(childSection.getTitle()));
                    setAttribute("pageId", childSection.getId());
                    renderFragment("childrenStart");
                    renderSectionEditionMode(childSection, level + 1);
                }
                renderFragment("afterTabulation");
            }
        }
    }

    protected void renderSection(Section section, int level) {
        if (isShowable(section)) {
            setAttribute("pageId", section.getId());
            setAttribute("checked", isChecked(section));
            renderFragment("pageStart");
            printSection(section, level);
            if (hasVisibleChildren(section) && isOpen(section)) {
                renderFragment("beforeTabulation");
                renderTabulation();
                renderFragment("afterTabulation");
                renderFragment("childrenStart");
                List children = section.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Section childSection = (Section) children.get(i);
                    renderSection(childSection, level + 1);
                }
                renderFragment("childrenEnd");
            }
            renderFragment("pageEnd");
        }
    }

    protected void renderTabulation() {
        TreeMenuDriver driver = (TreeMenuDriver) getPanel().getProvider().getDriver();
        writeToOut(driver.getTabulationString(getPanel()));
    }

    protected void printSection(Section section, int level) {
        TreeMenuDriver driver = (TreeMenuDriver) getPanel().getProvider().getDriver();
        String sectionPattern = driver.getPatternForPage(section, level, getPanel(), openedPages.contains(section.getDbid()));
        String sectionText = driver.performReplacementsInPattern(sectionPattern, section, getLocalizedValue(section.getTitle()));
        writeToOut(sectionText);
    }

    /**
     * Determine if a page has visible children
     *
     * @param section page to be considered
     * @return true if the page has visible children
     */
    public boolean hasVisibleChildren(Section section) {
        List l = section.getChildren();
        if (l == null) return false;
        for (int i = 0; i < l.size(); i++) {
            Section childSection = (Section) l.get(i);
            if (isShowable(childSection))
                return true;
        }
        return false;
    }

    /**
     * Determines if a page is showable.
     *
     * @param section page to be considered
     * @return true if the page is showable
     */
    public boolean isShowable(Section section) {
        if (!section.isVisible().booleanValue())
            return false;
        if (isEditMode)
            return true;
        if (!isChecked(section) && !areAllChecked())
            return false;
        SectionPermission viewPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
        return UserStatus.lookup().hasPermission(viewPerm);
    }

    /**
     * Determines if a page is current.
     *
     * @param section page to be considered
     * @return true if the page is current
     */
    public boolean isCurrent(Section section) {
        return !isEditMode && section.equals(getSection());
    }

    /**
     * Determines if a page is checked.
     *
     * @param section page to be considered
     * @return true if the page is checked
     */
    public boolean isChecked(Section section) {
        return ((TreeMenuDriver) getPanel().getProvider().getDriver()).isChecked(getPanel(), section.getId().toString());
    }

    public boolean areAllChecked() {
        return ((TreeMenuDriver) getPanel().getProvider().getDriver()).isChecked(getPanel(), "*");
    }

    private List openSections;

    /**
     * Determine if a page is open in the menu
     *
     * @param section page to be considered
     * @return true if the page is open in the menu
     */
    boolean isOpen(Section section) {
        if (isEditMode || allSectionsOpen)
            return true;

        if (openSections == null) {
            openSections = calculateOpenSections();
        }
        for (int i = 0; i < openSections.size(); i++) {
            Long openSectionId = (Long) openSections.get(i);
            if (openSectionId.equals(section.getId()))
                return true;
        }
        return false;
    }

    protected List calculateOpenSections() {
        List l = new ArrayList();
        Section currentSection = getSection();
        if (currentSection != null) {
            l.add(currentSection.getId());
            Section s = currentSection;
            while ((s = s.getParent()) != null) {
                l.add(s.getId());
            }
        }
        return l;
    }

}
