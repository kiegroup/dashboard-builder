/**
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

import javax.servlet.http.HttpServletRequest;

import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.workspace.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class URLMarkupGeneratorTest {

    @Mock
    HttpServletRequest request;

    URLMarkupGenerator urlMarkupGenerator;

    @Before
    public void setUp() throws Exception {
        urlMarkupGenerator = new URLMarkupGenerator();
        when(request.getContextPath()).thenReturn("ctx");
    }

    @Test
    public void testInternalRequest() throws Exception {
        when(request.getRequestURI()).thenReturn("ctx/" + URLMarkupGenerator.COMMAND_RUNNER);
        assertTrue(urlMarkupGenerator.isInternalRequest(request));
    }

    @Test
    public void testFriendlyUrlRequest() throws Exception {
        when(request.getRequestURI()).thenReturn("ctx/workspace");
        assertFalse(urlMarkupGenerator.isInternalRequest(request));
    }

    @Test
    public void testPanelActionRequest() throws Exception {
        when(request.getParameter(Parameters.DISPATCH_IDPANEL)).thenReturn("panel");
        when(request.getParameter(Parameters.DISPATCH_ACTION)).thenReturn("action");
        assertFalse(urlMarkupGenerator.isInternalRequest(request));
    }

    @Test
    public void testBeanActionRequest() throws Exception {
        when(request.getParameter(FactoryURL.PARAMETER_BEAN)).thenReturn("bean");
        when(request.getParameter(FactoryURL.PARAMETER_ACTION)).thenReturn("action");
        assertFalse(urlMarkupGenerator.isInternalRequest(request));
    }
}
