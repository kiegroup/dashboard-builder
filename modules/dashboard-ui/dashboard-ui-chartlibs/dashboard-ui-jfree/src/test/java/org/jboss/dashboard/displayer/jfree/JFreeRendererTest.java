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
package org.jboss.dashboard.displayer.jfree;

import org.jboss.dashboard.annotation.Install;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JFreeRendererTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(JFreeRendererTest.class.getPackage())
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
                .addPackage("org.jboss.dashboard.domain")
                .addPackage("org.jboss.dashboard.domain.date")
                .addPackage("org.jboss.dashboard.domain.label")
                .addPackage("org.jboss.dashboard.domain.numeric")
                .addPackage("org.jboss.dashboard.export")
                .addPackage("org.jboss.dashboard.function")
                .addPackage("org.jboss.dashboard.kpi")
                .addPackage("org.jboss.dashboard.provider")
                .addPackage("org.jboss.dashboard.annotation")
                .addPackage("org.jboss.dashboard.annotation.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject  @Install
    protected JFreeDisplayerRenderer renderer;

    @Test
    public void checkBean() {
        assertThat(renderer).isNotNull();
        assertThat(renderer.getUid()).isEqualTo(JFreeDisplayerRenderer.UID);
    }
}