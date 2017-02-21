/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui;

import javax.servlet.http.HttpServletResponse;

import org.jboss.dashboard.ui.controller.SecureHeaderFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecureHeaderTest extends UITestBase {

    @Mock
    HttpServletResponse response;

    @Mock
    HTTPSettings httpSettings;

    @Test
    public void testFrameOptionsSameOrigin() throws Exception {
        when(httpSettings.getXFrameOptions()).thenReturn("SAMEORIGIN");
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response).setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
    }

    @Test
    public void testFrameOptionsDeny() throws Exception {
        when(httpSettings.getXFrameOptions()).thenReturn("DENY");
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response).setHeader("X-FRAME-OPTIONS", "DENY");
    }

    @Test
    public void testFrameOptionsDisabled() throws Exception {
        when(httpSettings.getXFrameOptions()).thenReturn(null);
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response, never()).setHeader(eq("X-FRAME-OPTIONS"), anyString());
    }

    @Test
    public void testXSSMode() throws Exception {
        when(httpSettings.isXSSProtectionEnabled()).thenReturn(true);
        when(httpSettings.isXSSProtectionBlock()).thenReturn(true);
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response).setHeader("X-XSS-Protection", "1; mode=block");
    }

    @Test
    public void testXSSSameOrigin() throws Exception {
        when(httpSettings.isXSSProtectionEnabled()).thenReturn(true);
        when(httpSettings.isXSSProtectionBlock()).thenReturn(false);
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response).setHeader("X-XSS-Protection", "1");
    }

    @Test
    public void testXSSProtectionDisabled() throws Exception {
        when(httpSettings.isXSSProtectionEnabled()).thenReturn(false);
        SecureHeaderFilter.applyHeaders(httpSettings, response);
        verify(response, never()).setHeader(eq("X-XSS-Protection"), anyString());
    }
}
