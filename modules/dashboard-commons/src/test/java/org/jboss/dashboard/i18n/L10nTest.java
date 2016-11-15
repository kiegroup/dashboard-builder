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
package org.jboss.dashboard.i18n;

import org.jboss.dashboard.LocaleManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.fest.assertions.api.Assertions.*;

@RunWith(Arquillian.class)
public class L10nTest {

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
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
    }

    @Test
    public void checkDefaultLocale() {
        localeManager.setDefaultLocale(Locale.ENGLISH);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
    }

    @Test
    public void checkUnsupportedLocale() {
        localeManager.setDefaultLocale(Locale.ITALY);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
    }

    @Test
    public void checkSupportedVariant() {
        localeManager.setDefaultLocale(Locale.UK);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(Locale.ENGLISH)).isTrue();
        ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.i18n.l10ntest", localeManager.getDefaultLocale());
        String string1 = bundle.getString("string1");
        assertThat(string1.equals(STRING1_EN));
    }

    @Test
    public void checkSupportedByDefault() {
        localeManager.setDefaultLocale(LOCALE_ES_AR);
        localeManager.init();
        assertThat(localeManager.getDefaultLocale().equals(LOCALE_ES)).isTrue();
        ResourceBundle bundle = localeManager.getBundle("org.jboss.dashboard.i18n.l10ntest", localeManager.getDefaultLocale());
        String string1 = bundle.getString("string1");
        assertThat(string1.equals(STRING1_ES));
    }

    @Test
    public void testSupportedLanguages() {
        String[] langIds = localeManager.getPlatformAvailableLangs();
        assertThat(langIds).isEqualTo(new String[] {"en","es","de","fr","pt","ja"});
    }
}
