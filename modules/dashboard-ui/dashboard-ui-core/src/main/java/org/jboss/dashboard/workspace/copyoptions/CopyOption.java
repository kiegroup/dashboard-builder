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
package org.jboss.dashboard.workspace.copyoptions;

/**
 * This is the superclass for all CopyOption classes that may be defined.
 * <p/>
 * <p/>
 * Date: 25-may-2004
 * Time: 12:50:26
 */
public abstract class CopyOption {
    public static final BasicSectionCopyOption DEFAULT_SECTION_COPY_OPTION_SAME_WORKSPACE = new BasicSectionCopyOption(false);
    public static final BasicSectionCopyOption DEFAULT_SECTION_COPY_OPTION_OTHER_WORKSPACE = new BasicSectionCopyOption(true);


}
