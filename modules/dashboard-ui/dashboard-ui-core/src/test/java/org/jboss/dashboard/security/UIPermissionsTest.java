/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jboss.dashboard.security;

import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIPermissionsTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
    }

    @Test
    public void testPanelPermission() throws Exception {
        PanelPermission pp = new PanelPermission("101.101.*", "view, maximize,!edit");
        PanelPermission p1 = new PanelPermission("101.101.103", "view, !edit");
        PanelPermission p2 = new PanelPermission("101.101.103", "view, edit");
        PanelPermission p3 = new PanelPermission("101.101.103", "view, !maximize");
        PanelPermission p4 = new PanelPermission("101.101.103", "edit");

        // Permission action status
        assertTrue(pp.isActionGranted("view"));
        assertTrue(!pp.isActionDenied("view"));
        assertTrue(!pp.isActionUndefined("view"));
        assertTrue(!pp.isActionGranted("edit"));
        assertTrue(pp.isActionDenied("edit"));
        assertTrue(!pp.isActionUndefined("edit"));
        assertTrue(!pp.isActionGranted("delete"));
        assertTrue(!pp.isActionDenied("delete"));
        assertTrue(pp.isActionUndefined("delete"));

        // Grant action
        pp.grantAction("edit");
        assertTrue(pp.isActionGranted("edit"));
        assertTrue(!pp.isActionDenied("edit"));
        assertTrue(!pp.isActionUndefined("edit"));

        // Permission implication
        assertTrue(!pp.implies(p1));
        assertTrue(pp.implies(p2));
        assertTrue(!pp.implies(p3));
        assertTrue(pp.implies(p4));
    }
}
