/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.panel.AjaxRefreshManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UITestBase {

    @Mock
    protected CDIBeanLocator beanLocator;

    @Mock
    protected NavigationManager navigationManager;

    @Mock
    protected AjaxRefreshManager ajaxRefreshManager;

    @Mock
    protected HTTPSettings httpSettings;

    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanLocator = beanLocator;
        when(beanLocator.lookupBeanByType(NavigationManager.class)).thenReturn(navigationManager);
        when(beanLocator.lookupBeanByType(AjaxRefreshManager.class)).thenReturn(ajaxRefreshManager);
        when(beanLocator.lookupBeanByType(HTTPSettings.class)).thenReturn(httpSettings);
    }
}
