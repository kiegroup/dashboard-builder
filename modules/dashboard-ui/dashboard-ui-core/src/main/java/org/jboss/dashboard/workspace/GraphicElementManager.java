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

import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor;

import java.io.File;

/**
 * Definition for graphic resources Manager class.
 */
public interface GraphicElementManager {

    /**
     * Returns all elements installed in the system
     */
    GraphicElement[] getElements();

    /**
     * Return all elements belonging to given workspace
     *
     * @param workspaceId Set to null to indicate elements that apply to all workspaces.
     * @return all graphic resources managed by this manager belonging to given workspace
     */
    GraphicElement[] getElements(String workspaceId);

    /**
     * Return all elements belonging to given workspace and section
     *
     * @param workspaceId  Set to null to indicate elements that apply to all workspaces.
     * @param sectionId Set to null to indicate elements that apply to all sections.
     * @return all graphic resources managed by this manager belonging to given workspace and section
     */
    GraphicElement[] getElements(String workspaceId, Long sectionId);

    /**
     * Return all elements belonging to given workspace, section, and panel
     *
     * @param workspaceId  Set to null to indicate elements that apply to all workspaces.
     * @param sectionId Set to null to indicate elements that apply to all sections.
     * @param panelId Set to null to indicate elements that apply to all panel instances.
     * @return all graphic resources managed by this manager belonging to given workspace, section and panel
     */
    GraphicElement[] getElements(String workspaceId, Long sectionId, Long panelId);

    /**
     * Get the elements visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     * Assume current workspace, section and panel.
     *
     * @return the elements visible for a given context
     */
    GraphicElement[] getAvailableElements();

    /**
     * Get the elements visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     *
     * @return the elements visible for a given context
     */
    GraphicElement[] getAvailableElements(String workspaceId, Long sectionId, Long panelId);

    /**
     * Get the elements manageable for a given context. These are the base elements plus the workspace, section and panel
     * elements. Assume current workspace, section and panel.
     *
     * @return the elements manageable for a given context
     */
    GraphicElement[] getManageableElements();

    /**
     * Get the elements manageable for a given context. These are the base elements plus the workspace, section and panel
     * elements.
     *
     * @return Get the elements manageable for a given context
     */
    GraphicElement[] getManageableElements(String workspaceId, Long sectionId, Long panelId);

    /**
     * Get an element visible for a given context. These are the base elements plus the workspace, section and panel
     * elements. But hierarchy applies: Panel elements may hide section ones, workspace ones and global ones.
     * Assume current workspace, section and panel.
     *
     * @param id Desired element id
     * @return an element visible for a given context, if any
     */
    GraphicElement getAvailableElement(String id);

    /**
     * Get an element by dbid.
     */
    GraphicElement getElementByDbid(String dbid);

    /**
     * Returns an element by its id and context.
     */
    GraphicElement getElement(String id, String workspaceId, Long sectionId, Long panelId);

    /**
     * Returns the default element, that is, the one whose id is 'default' and context info is null.
     */
    GraphicElement getDefaultElement();

    /**
     * Determines if given element is a base element. These elements are
     * special, as they hold a main copy on HD, and are copied to DB on startup. That's why they can't be edited
     * in admin pages, because the HD copy is intended not to be modified in any way.
     *
     * @return true if an element is a base element
     */
    boolean isBaseElement(GraphicElement element);

    /**
     * Return the base directory for elements, once deployed.
     */
    String getElementsDir();

    /**
     * Create or update existing element
     */
    void createOrUpdate(GraphicElement element) throws Exception;

    /**
     * Delete existing element
     */
    void delete(GraphicElement element);

    /**
     * Determine the scope for the element handled by this manager.
     *
     * @return a GraphicElementScopeDescriptor indicating the scope managed by this manager
     */
    GraphicElementScopeDescriptor getElementScopeDescriptor();

    /**
     * Add a file inside given element
     *
     * @param name    resource name to use
     * @param file    file to add
     * @param element graphic element to modify
     */
    void addFileToResource(String name, File file, GraphicElement element, String descriptorFileName) throws Exception;
}
