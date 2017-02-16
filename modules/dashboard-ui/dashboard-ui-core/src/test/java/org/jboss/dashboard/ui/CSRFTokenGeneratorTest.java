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

import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CSRFTokenGeneratorTest {

    CSRFTokenGenerator tokenGenerator;

    @Before
    public void setUp() throws Exception {
        tokenGenerator = new CSRFTokenGenerator();
    }

    @Test
    public void testTokenGeneration() throws Exception {
        String tokenA = tokenGenerator.generateToken();
        String tokenB = tokenGenerator.generateToken();
        assertNotEquals(tokenA, tokenB);
        assertEquals(tokenA.length(), 8);
        assertEquals(tokenB.length(), 8);
    }

    @Test
    public void testTokenRetrieval() throws Exception {
        String tokenA = tokenGenerator.getLastToken();
        String tokenB = tokenGenerator.getLastToken();
        assertEquals(tokenA, tokenB);
    }

    @Test
    public void testTokenReset() throws Exception {
        String tokenA = tokenGenerator.getLastToken();
        assertTrue(tokenGenerator.isValidToken(tokenA));

        tokenGenerator.resetToken();

        String tokenB = tokenGenerator.getLastToken();
        assertNotEquals(tokenA, tokenB);
        assertFalse(tokenGenerator.isValidToken(tokenA));
        assertTrue(tokenGenerator.isValidToken(tokenB));
    }
}
