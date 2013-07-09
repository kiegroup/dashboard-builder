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
package org.jboss.dashboard.i18n;

import org.jboss.dashboard.LocaleManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class L10nTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass("org.jboss.dashboard.LocaleManager")
                .addPackage("org.jboss.dashboard.pojo")
                .addPackage("org.jboss.dashboard.annotation")
                .addPackage("org.jboss.dashboard.annotation.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static final Locale LOCALE_ES = new Locale("es");
    public static final Locale LOCALE_ES_AR = new Locale("es", "AR");
    public static final String STRING1_EN = "String 1";
    public static final String STRING1_ES = "Texto 1";

    @Inject
    BeanManager beanManager;

    @Inject
    protected LocaleManager localeManager;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
        localeManager.setDefaultLocaleId("en");
        localeManager.setInstalledLocaleIds(new String[]{"en", "es"});
    }

    @Test
    public void checkDefaultLocale() {
        Locale.setDefault(Locale.ENGLISH);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
    }

    @Test
    public void checkUnsupportedLocale() {
        Locale.setDefault(Locale.CANADA_FRENCH);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
    }

    @Test
    public void checkSupportedVariant() {
        Locale.setDefault(Locale.UK);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
        ResourceBundle bundle = ResourceBundle.getBundle("org.jboss.dashboard.i18n.l10ntest", localeManager.getDefaultLocale());
        String string1 = bundle.getString("string1");
        assertThat(string1.equals(STRING1_EN));
    }

    @Test
    public void checkSupportedByDefault() {
        Locale.setDefault(LOCALE_ES_AR);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(LOCALE_ES)).isTrue();
        ResourceBundle bundle = ResourceBundle.getBundle("org.jboss.dashboard.i18n.l10ntest", localeManager.getDefaultLocale());
        String string1 = bundle.getString("string1");
        assertThat(string1.equals(STRING1_ES));
    }
}
