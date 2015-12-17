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
package org.jboss.dashboard.provider;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of data property formatters.
 */
@ApplicationScoped
@Named("dataFormatterRegistry")
public class DataFormatterRegistry {

    /**
     * GoF singleton pattern.
     */
    public static DataFormatterRegistry lookup() {
        return (DataFormatterRegistry) CDIBeanLocator.getBeanByName("dataFormatterRegistry");
    }

    /** The default general-purpose property formatter */
    protected DataPropertyFormatter defaultPropertyFormatter;

    /** The registry of custom property formatters */
    Map<String,DataPropertyFormatter> customFormatterMap;
    
    @Inject @Install
    protected Instance<DataPropertyFormatter> dataPropertyFormatters;

    @PostConstruct
    protected void init() {
        customFormatterMap = new HashMap<String, DataPropertyFormatter>();
        for (DataPropertyFormatter formatter : dataPropertyFormatters) {
            String[] propIds = formatter.getSupportedPropertyIds();
            if (propIds == null) {
                defaultPropertyFormatter = formatter;
            } else {
                for (int i = 0; i < propIds.length; i++) {
                    String propId = propIds[i];
                    customFormatterMap.put(propId, formatter);
                }
            }
        }
    }
    
    public DataPropertyFormatter getPropertyFormatter(String propId) {
        DataPropertyFormatter propFormatter = customFormatterMap.get(propId);
        if (propFormatter != null) return propFormatter;
        return defaultPropertyFormatter;
    }
}
