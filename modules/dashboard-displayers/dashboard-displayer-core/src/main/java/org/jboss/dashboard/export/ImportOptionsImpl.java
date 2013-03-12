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
package org.jboss.dashboard.export;

/**
 * Import options.
 */
public class ImportOptionsImpl implements ImportOptions {

    protected boolean ignoreKPIs;
    protected boolean ignoreDataProviders;

    public ImportOptionsImpl() {
        ignoreKPIs = false;
        ignoreDataProviders = false;
    }

    public boolean ignoreKPIs() {
        return ignoreKPIs;
    }

    public void setIgnoreKPIs(boolean ignoreKPIs) {
        this.ignoreKPIs = ignoreKPIs;
    }

    public boolean ignoreDataProviders() {
        return ignoreDataProviders;
    }

    public void setIgnoreDataProviders(boolean ignoreDataProviders) {
        this.ignoreDataProviders = ignoreDataProviders;
    }
}
