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
package org.jboss.dashboard.displayer;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@RunWith(Arquillian.class)
public class ServicesLookupTest {

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
    }

    @Test
    public void listBeans() {
        DataDisplayerServices displayerServices = DataDisplayerServices.lookup();

        System.out.println("\nData displayer types");
        System.out.println("-------------------------");
        DataDisplayerType[] displayerType = displayerServices.getDataDisplayerManager().getDataDisplayerTypes();
        for (DataDisplayerType dataDisplayerType : displayerType) {
            System.out.println(dataDisplayerType.getUid());
        }
        System.out.println("\nDisplayer renderers");
        System.out.println("------------------------");
        DataDisplayerRenderer[] displayerRenderers = displayerServices.getDataDisplayerManager().getDataDisplayerRenderers();
        for (DataDisplayerRenderer renderer : displayerRenderers) {
            System.out.println(renderer.getUid());
        }
    }
}