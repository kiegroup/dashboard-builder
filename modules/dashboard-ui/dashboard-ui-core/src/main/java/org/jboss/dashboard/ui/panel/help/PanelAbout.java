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
package org.jboss.dashboard.ui.panel.help;

/**
 * 
 *
 */
public interface PanelAbout {
    public static final String PROP_AUTHOR = "author";
    public static final String PROP_COMPANY = "company";
    public static final String PROP_URL = "url";
    public static final String PROP_LICENSE = "license";

    /**
     * Get supported properties
     *
     * @return A list of supported property keys
     */
    public String[] getProperties();

    /**
     * Get property by key
     *
     * @param key
     * @return Value for given key
     */
    public String getProperty(String key);
}
