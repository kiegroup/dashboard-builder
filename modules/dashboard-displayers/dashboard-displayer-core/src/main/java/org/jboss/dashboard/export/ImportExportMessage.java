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
package org.jboss.dashboard.export;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.message.AbstractMessage;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.kpi.KPI;

import java.util.Locale;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contains the full list of possible message codes that could be generated during a export or import task.
 */
public class ImportExportMessage extends AbstractMessage {

    // ERRORS
    public static final String PROVIDER_CODE_NOT_FOUND      = "BAM_10000";
    public static final String PROVIDER_TYPE_NOT_FOUND      = "BAM_10001";
    public static final String DISPLAYER_TYPE_NOT_FOUND     = "BAM_10002";
    public static final String DISPLAYER_RENDERER_NOT_FOUND = "BAM_10003";

    private static List<String> ERRORS = Arrays.asList(new String[]{PROVIDER_CODE_NOT_FOUND, PROVIDER_TYPE_NOT_FOUND,
        DISPLAYER_TYPE_NOT_FOUND, DISPLAYER_RENDERER_NOT_FOUND});

    // WARNINGS
    public static final String PROVIDER_ALREADY_EXISTS = "BAM_90000";
    public static final String KPI_ALREADY_EXISTS      = "BAM_90001";

    private static List<String> WARNINGS = Arrays.asList(new String[] {PROVIDER_ALREADY_EXISTS, KPI_ALREADY_EXISTS});

    // INFOS
    public static final String PROVIDER_CREATED         = "BAM_00000";
    public static final String KPI_CREATED              = "BAM_00001";
    public static final String PROVIDER_UPDATED         = "BAM_00002";
    public static final String KPI_UPDATED              = "BAM_00003";

    private static List<String> INFOS = Arrays.asList(new String[]{PROVIDER_CREATED, KPI_CREATED, PROVIDER_UPDATED, KPI_UPDATED});

    /** The locale manager. */
    protected LocaleManager localeManager;

    public ImportExportMessage(String messageCode, Object[] elements) {
        super(messageCode, elements);
        localeManager = LocaleManager.lookup();
    }

    public int getMessageType() {
        if (WARNINGS.contains(messageCode)) return WARNING;
        if (INFOS.contains(messageCode)) return INFO;
        return ERROR;
    }

    public String getMessage(String messageCode, Locale l) {
        try {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.export.messages", l);
            return messageCode + " - " + i18n.getString(messageCode);
        } catch (Exception e) {
            return messageCode;
        }
    }

    public String toString(Object element, Locale l) {
        if (element == null) return "";
        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", l);

        if (element instanceof DataProvider) {
            DataProvider dp = (DataProvider) element;
            return dp.getCode() + ", " + dp.getDescription(l);
        }
        if (element instanceof KPI) {
            KPI kpi = (KPI) element;
            return kpi.getCode() + ", " + kpi.getDescription(l);
        }
        return element.toString();
    }
}
