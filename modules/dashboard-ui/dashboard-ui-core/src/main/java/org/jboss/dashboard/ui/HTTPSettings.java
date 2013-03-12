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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Class that defines some application set-up parameters.
 */
@ApplicationScoped
@Named("httpSettings")
public class HTTPSettings {

    public static HTTPSettings lookup() {
        return (HTTPSettings) CDIBeanLocator.getBeanByName("httpSettings");
    }

    public static final String AJAX_AREA_PREFFIX = "AJAX_area_for_";

    private String downloadDir = "WEB-INF/tmp";
    private int maxPostSize = 10240;
    private boolean multipartProcessing = true;
    private String encoding = "UTF-8";

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public int getMaxPostSize() {
        return maxPostSize;
    }

    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
    }

    public boolean isMultipartProcessing() {
        return multipartProcessing;
    }

    public void setMultipartProcessing(boolean multipartProcessing) {
        this.multipartProcessing = multipartProcessing;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
