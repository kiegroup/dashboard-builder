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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject @Config("WEB-INF/tmp")
    private String downloadDir;

    @Inject @Config("10240") /* In Kb */
    private int maxPostSize;

    @Inject @Config("true")
    private boolean multipartProcessing;

    @Inject @Config("UTF-8")
    private String encoding;

    @Inject @Config("false")
    private boolean XSSProtectionEnabled;

    @Inject @Config("true")
    private boolean XSSProtectionBlock;

    /**
     * There are three possible values for the X-Frame-Options headers:<ul>
     *  <li>DENY, which prevents any domain from framing the content.</li>
     *  <li>SAMEORIGIN, which only allows the current site to frame the content.</li>
     *  <li>ALLOW-FROM uri, which permits the specified 'uri' to frame this page. (e.g., ALLOW-FROM http://www.example.com) The ALLOW-FROM option is a relatively recent addition (circa 2012) and may not be supported by all browsers yet. BE CAREFUL ABOUT DEPENDING ON ALLOW-FROM. If you apply it and the browser does not support it, then you will have NO clickjacking defense in place.</li>
     * </ul>
     */
    @Inject @Config("")
    private String XFrameOptions;

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

    public boolean isXSSProtectionEnabled() {
        return XSSProtectionEnabled;
    }

    public void setXSSProtectionEnabled(boolean XSSProtectionEnabled) {
        this.XSSProtectionEnabled = XSSProtectionEnabled;
    }

    public boolean isXSSProtectionBlock() {
        return XSSProtectionBlock;
    }

    public void setXSSProtectionBlock(boolean XSSProtectionBlock) {
        this.XSSProtectionBlock = XSSProtectionBlock;
    }

    public String getXFrameOptions() {
        return XFrameOptions;
    }

    public void setXFrameOptions(String XFrameOptions) {
        this.XFrameOptions = XFrameOptions;
    }
}