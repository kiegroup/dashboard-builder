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
package org.jboss.dashboard;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.displayer.DataDisplayerManager;
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.provider.DataProviderManager;
import org.jboss.dashboard.provider.DataProviderType;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ServicesLookupTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.jboss.dashboard.commons.message")
                .addPackage("org.jboss.dashboard")
                .addPackage("org.jboss.dashboard.security")
                .addPackage("org.jboss.dashboard.users")
                .addPackage("org.jboss.dashboard.database")
                .addPackage("org.jboss.dashboard.database.hibernate")
                .addPackage("org.jboss.dashboard.log")
                .addPackage("org.jboss.dashboard.profiler")
                .addPackage("org.jboss.dashboard.scheduler")
                .addPackage("org.jboss.dashboard.error")
                .addPackage("org.jboss.dashboard.filesystem")
                .addPackage("org.jboss.dashboard.command")
                .addPackage("org.jboss.dashboard.dataset")
                .addPackage("org.jboss.dashboard.displayer")
                .addPackage("org.jboss.dashboard.displayer.annotation")
                .addPackage("org.jboss.dashboard.displayer.chart")
                .addPackage("org.jboss.dashboard.displayer.table")
                .addPackage("org.jboss.dashboard.displayer.jfree")
                .addPackage("org.jboss.dashboard.displayer.nvd3")
                .addPackage("org.jboss.dashboard.displayer.gauge")
                .addPackage("org.jboss.dashboard.displayer.ofc2")
                .addPackage("org.jboss.dashboard.domain")
                .addPackage("org.jboss.dashboard.domain.date")
                .addPackage("org.jboss.dashboard.domain.label")
                .addPackage("org.jboss.dashboard.domain.numeric")
                .addPackage("org.jboss.dashboard.export")
                .addPackage("org.jboss.dashboard.function")
                .addPackage("org.jboss.dashboard.kpi")
                .addPackage("org.jboss.dashboard.provider")
                .addPackage("org.jboss.dashboard.provider.csv")
                .addPackage("org.jboss.dashboard.provider.sql")
                .addPackage("org.jboss.dashboard.annotation")
                .addPackage("org.jboss.dashboard.annotation.config")
                .addPackage("org.jboss.dashboard.pojo")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected BeanManager beanManager;

    @Inject
    protected DataProviderManager dataProviderManager;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Inject
    protected DataDisplayerManager dataDisplayerManager;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
    }

    @Test
    public void listBeans() {
        DataProviderServices providerServices = DataProviderServices.lookup();
        DataDisplayerServices displayerServices = DataDisplayerServices.lookup();

        System.out.println("Scalar functions");
        System.out.println("------------------------");
        ScalarFunction[] scalarFunctions = providerServices.getScalarFunctionManager().getAllScalarFunctions();
        for (int i = 0; i < scalarFunctions.length; i++) {
            ScalarFunction scalarFunction = scalarFunctions[i];
            System.out.println(scalarFunction.getCode());
        }
        System.out.println("\nData provider types");
        System.out.println("-----------------------");
        DataProviderType[] dataProviders = providerServices.getDataProviderManager().getDataProviderTypes();
        for (int i = 0; i < dataProviders.length; i++) {
            DataProviderType providerType = dataProviders[i];
            System.out.println(providerType.getUid());
        }
        System.out.println("\nData displayer types");
        System.out.println("-------------------------");
        DataDisplayerType[] displayerType = displayerServices.getDataDisplayerManager().getDataDisplayerTypes();
        for (int i = 0; i < displayerType.length; i++) {
            DataDisplayerType dataDisplayerType = displayerType[i];
            System.out.println(dataDisplayerType.getUid());
        }
        System.out.println("\nDisplayer renderers");
        System.out.println("------------------------");
        DataDisplayerRenderer[] displayerRenderers = displayerServices.getDataDisplayerManager().getDataDisplayerRenderers();
        for (int i = 0; i < displayerRenderers.length; i++) {
            DataDisplayerRenderer renderer = displayerRenderers[i];
            System.out.println(renderer.getUid());
        }
    }
}