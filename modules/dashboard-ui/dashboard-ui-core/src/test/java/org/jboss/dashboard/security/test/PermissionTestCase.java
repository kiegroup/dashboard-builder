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

import org.jboss.dashboard.security.PanelPermission;
import junit.framework.Assert;
import junit.framework.TestCase;

public class PermissionTestCase extends TestCase {

    public PermissionTestCase(String s) {
        super(s);
    }

    public void testPanelPermission() throws Exception {
        PanelPermission pp = new PanelPermission("101.101.*", "view, maximize,!edit");
        PanelPermission p1 = new PanelPermission("101.101.103", "view, !edit");
        PanelPermission p2 = new PanelPermission("101.101.103", "view, edit");
        PanelPermission p3 = new PanelPermission("101.101.103", "view, !maximize");
        PanelPermission p4 = new PanelPermission("101.101.103", "edit");

        // Permission action status
        Assert.assertTrue(pp.isActionGranted("view"));
        Assert.assertTrue(!pp.isActionDenied("view"));
        Assert.assertTrue(!pp.isActionUndefined("view"));
        Assert.assertTrue(!pp.isActionGranted("edit"));
        Assert.assertTrue(pp.isActionDenied("edit"));
        Assert.assertTrue(!pp.isActionUndefined("edit"));
        Assert.assertTrue(!pp.isActionGranted("delete"));
        Assert.assertTrue(!pp.isActionDenied("delete"));
        Assert.assertTrue(pp.isActionUndefined("delete"));

        // Grant action
        pp.grantAction("edit");
        Assert.assertTrue(pp.isActionGranted("edit"));
        Assert.assertTrue(!pp.isActionDenied("edit"));
        Assert.assertTrue(!pp.isActionUndefined("edit"));

        // Permission implication
        Assert.assertTrue(!pp.implies(p1));
        Assert.assertTrue(pp.implies(p2));
        Assert.assertTrue(!pp.implies(p3));
        Assert.assertTrue(pp.implies(p4));
    }
}
