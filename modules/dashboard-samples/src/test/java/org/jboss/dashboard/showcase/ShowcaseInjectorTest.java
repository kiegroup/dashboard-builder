/**
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
package org.jboss.dashboard.showcase;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.jboss.dashboard.i18n.KpisFileConverter;
import org.jboss.dashboard.i18n.WorkspaceFileConverter;
import org.jboss.dashboard.i18n.XmlToBundleConverter;
import org.jboss.dashboard.test.MavenProjectHelper;
import org.junit.Test;

public class ShowcaseInjectorTest {

    @Test
    public void testInject() throws Exception {
        File rootDir = MavenProjectHelper.getModuleDir("modules/dashboard-samples");

        // Process the Showcase KPIs file
        XmlToBundleConverter converter = new KpisFileConverter();
        converter.bundleDir = new File(rootDir, "src/main/resources/org/jboss/dashboard/showcase/kpis");
        converter.xmlFile = new File(rootDir, "src/test/resources/test.kpis");
        Map<Locale,Properties> bundles = converter.read(); // Read literals from bundle files.
        converter.inject(bundles); // Inject bundles into the target XML file

        // Process the Showcase Workspace file
        converter = new WorkspaceFileConverter();
        converter.bundleDir = new File(rootDir, "src/main/resources/org/jboss/dashboard/showcase/workspace");
        converter.xmlFile = new File(rootDir, "src/test/resources/test.workspace");
        bundles = converter.read(); // Read literals from bundle files.
        converter.inject(bundles); // Inject bundles into the target XML file
    }
}