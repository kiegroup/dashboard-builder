/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.PropertyReadTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertyReadTagTest extends UITestBase {

    @Mock
    LocaleManager localeManager;

    @Test
    public void testPropRead_JA() throws Exception {
        String literal = "ダッシュボードショーケース";
        when(localeManager.localize(anyMap())).thenReturn(literal);
        Map map = new HashMap();
        map.put("ja", literal);

        String result = PropertyReadTag.formatValue(localeManager, map, true);
        assertEquals(result, literal);
    }

    @Test
    public void testPropRead_ZH() throws Exception {
        String literal = "展示架";
        when(localeManager.localize(anyMap())).thenReturn(literal);
        Map map = new HashMap();
        map.put("zh", literal);

        String result = PropertyReadTag.formatValue(localeManager, map, true);
        assertEquals(result, literal);
    }
}
