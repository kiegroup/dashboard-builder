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
package org.jboss.dashboard.provider;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.function.ScalarFunctionManager;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    protected DataProviderManager dataProviderManager;

    @Inject
    protected ScalarFunctionManager scalarFunctionManager;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
    }

    @Test
    public void listBeans() {
        DataProviderServices providerServices = DataProviderServices.lookup();

        System.out.println("\nData provider types");
        System.out.println("-----------------------");
        DataProviderType[] dataProviders = providerServices.getDataProviderManager().getDataProviderTypes();
        for (DataProviderType providerType : dataProviders) {
            System.out.println(providerType.getUid());
        }

        System.out.println("Scalar functions");
        System.out.println("------------------------");
        ScalarFunction[] scalarFunctions = providerServices.getScalarFunctionManager().getAllScalarFunctions();
        for (ScalarFunction scalarFunction : scalarFunctions) {
            System.out.println(scalarFunction.getCode());
        }
    }
}