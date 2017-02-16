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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenGenerator;
import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenProcessor;
import org.jboss.dashboard.ui.controller.requestChain.SessionInitializer;
import org.jboss.dashboard.workspace.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CSRFTokenProcessorTest {

    @Mock
    HttpServletRequest httpRequest;

    @Mock
    URLMarkupGenerator urlMarkupGenerator;

    @Mock
    SessionInitializer sessionInitializer;

    @Mock
    CSRFTokenGenerator tokenGenerator;

    CSRFTokenProcessor tokenProcessor;

    @Before
    public void setUp() throws Exception {
        tokenProcessor = spy(new CSRFTokenProcessor(true, urlMarkupGenerator, sessionInitializer, tokenGenerator));

        doReturn(httpRequest).when(tokenProcessor).getHttpRequest();
        when(httpRequest.getServletPath()).thenReturn("/");
        when(sessionInitializer.isNewSession(httpRequest)).thenReturn(false);
        when(urlMarkupGenerator.isInternalRequest(httpRequest)).thenReturn(false);
    }

    @Test
    public void testNewSession() throws Exception {
        when(sessionInitializer.isNewSession(httpRequest)).thenReturn(true);
        assertTrue(tokenProcessor.processRequest());
    }

    @Test
    public void testInternalRequest() throws Exception {
        when(urlMarkupGenerator.isInternalRequest(httpRequest)).thenReturn(true);
        assertTrue(tokenProcessor.processRequest());
    }

    @Test(expected = ServletException.class)
    public void testCsrfRequired() throws Exception {
        when(httpRequest.getParameter(tokenGenerator.getTokenName())).thenReturn(null);
        when(httpRequest.getParameter(Parameters.AJAX_ACTION)).thenReturn("action");
        tokenProcessor.processRequest();
    }

    @Test(expected = ServletException.class)
    public void testInvalidToken() throws Exception {
        when(httpRequest.getParameter(tokenGenerator.getTokenName())).thenReturn("1");
        when(tokenGenerator.isValidToken(anyString())).thenReturn(false);
        tokenProcessor.processRequest();
    }

    @Test
    public void testValidToken() throws Exception {
        when(httpRequest.getParameter(tokenGenerator.getTokenName())).thenReturn("1");
        when(tokenGenerator.isValidToken(anyString())).thenReturn(true);
        assertTrue(tokenProcessor.processRequest());
    }
}
