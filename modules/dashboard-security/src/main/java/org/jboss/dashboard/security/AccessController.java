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
package org.jboss.dashboard.security;

import java.security.Permission;
import javax.security.auth.Subject;

/**
 * An access controller takes care about access to protected resources
 * such as files, web pages, etc...  The <code>java.security.Permission</code>
 * interface defines who can access to a resource and, optionally,
 * which actions can execute.
 */
public interface AccessController {

    Policy getPolicy();
    void checkPermission(Subject usr, Permission perm);
    boolean hasPermission(Subject usr, Permission perm);
}
