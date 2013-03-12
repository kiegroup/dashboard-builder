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
package org.jboss.dashboard.ui.config;

import java.util.List;

public interface Tree {
    /**
     * Get the list of root nodes
     *
     * @return A list of TreeNode elements
     */
    List getRootNodes();

    /**
     * Get a TreeNode given its path. If it doesn't exist, return null
     *
     * @param path Tree path to resolve
     * @return a TreeNode given its path. If it doesn't exist, return null
     */
    TreeNode getNodeByPath(String path);

    /**
     * Get all TreeNodes given their path. If it doesn't exist, return empty Array
     *
     * @param path Tree path to resolve
     * @return array with all nodes contained in this path
     */
    TreeNode[] getNodesByPath(String path);
}
