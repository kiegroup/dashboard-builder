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
package org.jboss.dashboard.security.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PermissionTestSuite extends TestSuite {

    public static void main(String[] args) {
        try {
            junit.textui.TestRunner.run(suite());
        } catch (Throwable e) {
            System.out.println("Error ejecutando el suit. Exception: " + e);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (22/05/01 17:17:56)
     */
    public static Test suite() throws Throwable {
        try {
            TestSuite suite = new TestSuite();

            suite.addTest(new PermissionTestCase("testPanelPermission"));

            return suite;
        } catch (Throwable e) {
            System.out.println("TestSuite.Error: ");
            e.printStackTrace();
            throw e;
        }
    }
}
