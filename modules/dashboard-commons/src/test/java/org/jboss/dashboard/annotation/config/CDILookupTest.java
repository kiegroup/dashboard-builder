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
package org.jboss.dashboard.annotation.config;

import org.jboss.dashboard.annotation.Gps;
import org.jboss.dashboard.annotation.Radio;
import org.jboss.dashboard.pojo.Bean;
import org.jboss.dashboard.pojo.CellPhone;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CDILookupTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.jboss.dashboard.pojo")
                .addPackage("org.jboss.dashboard.annotation")
                .addPackage("org.jboss.dashboard.annotation.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    BeanManager beanManager;

    @Inject @Gps
    Instance<CellPhone> cellPhonesWithGps;

    @Inject @Radio
    Instance<CellPhone> cellPhonesWithRadio;

    Bean bean;

    @Before
    public void setUp() {
        CDIBeanLocator.beanManager = beanManager;
        bean = (Bean) CDIBeanLocator.getBeanByType(Bean.class);
    }
    
    @Test
    public void checkBean() {
        assertThat(bean).isNotNull();
        assertThat(cellPhonesWithRadio).hasSize(2);
        assertThat(cellPhonesWithGps).hasSize(2);
    }
}
