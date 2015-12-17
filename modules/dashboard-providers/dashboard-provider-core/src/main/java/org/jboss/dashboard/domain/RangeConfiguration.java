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
package org.jboss.dashboard.domain;

import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.LocaleManager;

import java.util.*;

/**
 * The configuration for a given range property.   
 */
public class RangeConfiguration {

    /** Logger */
    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RangeConfiguration.class);

    protected DataProperty rangeProperty;
    protected String propertyId;
    protected Map<Locale, String> nameI18nMap;
    protected Map<Locale, String> unitI18nMap;
    protected String scalarFunctionCode;
    protected String numericMinInterval;
    protected String numericMaxInterval;
    protected String numericMinValue;
    protected String numericMaxValue;


    public RangeConfiguration() {
        nameI18nMap = new HashMap<Locale, String>();
        unitI18nMap = new HashMap<Locale, String>();
        rangeProperty = null;
    }

    public RangeConfiguration(DataProperty property, ScalarFunction scalarFunction, Map<Locale, String> unitI18nMap) {
        this();
        read(property, scalarFunction, unitI18nMap);
    }

    public DataProperty getRangeProperty() {
        return rangeProperty;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public Map<Locale, String> getNameI18nMap() {
        return nameI18nMap;
    }

    public void setNameI18nMap(Map<Locale, String> rangeNameI18nMap) {
        nameI18nMap.clear();
        nameI18nMap.putAll(rangeNameI18nMap);
    }

    public String getName(Locale l) {
        Object result = nameI18nMap.get(l);
        if (result == null && rangeProperty != null) result = rangeProperty.getName(l);
        if (result == null) result = LocaleManager.lookup().localize(nameI18nMap);
        return (String) result;
    }

    public void setName(String description, Locale l) {
        nameI18nMap.put(l, description);
    }

    public Map<Locale, String> getUnitI18nMap() {
        return unitI18nMap;
    }

    public void setUnitI18nMap(Map<Locale, String> unitI18nMap) {
        this.unitI18nMap.clear();
        this.unitI18nMap.putAll(unitI18nMap);
    }

    public String getUnit(Locale l) {
        Object result = unitI18nMap.get(l);
        if (result == null) result = LocaleManager.lookup().localize(unitI18nMap);
        if (result == null) result = "";
        return (String) result;
    }

    public void setUnit(String unit, Locale l) {
        unitI18nMap.put(l, unit);
    }

    public String getScalarFunctionCode() {
        return scalarFunctionCode;
    }

    public void setScalarFunctionCode(String scalarFunctionCode) {
        this.scalarFunctionCode = scalarFunctionCode;
    }

    public void clear() {
        rangeProperty = null;
        propertyId = null;
        nameI18nMap.clear();
        unitI18nMap.clear();
        scalarFunctionCode = null;
    }

    public void apply(DataProperty range) {
        Locale locale = LocaleManager.currentLocale();
        if (propertyId != null) range.setPropertyId(propertyId);
        String name = getName(locale);
        if (name != null) range.setName(name, locale);        
    }

    public void read(DataProperty range, ScalarFunction function, Map<Locale, String> unitI18n) {
        clear();
        rangeProperty = range;
        if (range != null) propertyId = range.getPropertyId();
        if (range != null) nameI18nMap.putAll(range.getNameI18nMap());
        if (unitI18n != null) unitI18nMap.putAll(unitI18n);
        if (function != null) scalarFunctionCode = function.getCode();
    }
}
