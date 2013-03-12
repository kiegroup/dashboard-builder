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
package org.jboss.dashboard.ui.components.js;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.config.Config;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;

@ApplicationScoped
public class JSIncluder {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(JSIncluder.class.getName());

    public static final String HEAD = "head";
    public static final String BOTTOM = "bottom";

    @Inject @Config("/javascript/Head.js")
    private String headScriptFile;

    @Inject @Config("/javascript/Bottom.js")
    private String bottomScriptFile;

    @Inject @Config("false")
    private boolean existHead;

    @Inject @Config("false")
    private boolean existBottom;

    @Inject @Config("/components/bam/displayer/chart/gauge/raphael.2.1.0.min.js," +
                    "/components/bam/displayer/chart/gauge/justgage.1.0.1.min.js," +
                    "/components/bam/displayer/chart/nvd3/lib/d3.v2.min.js," +
                    "/components/bam/displayer/chart/nvd3/nv.d3.min.js," +
                    "/components/bam/displayer/chart/nvd3/src/tooltip.js," +
                    "/components/bam/displayer/chart/nvd3/src/utils.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/axis.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/discreteBar.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/discreteBarChart.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/legend.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/scatter.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/line.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/lineChart.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/pie.js," +
                    "/components/bam/displayer/chart/nvd3/src/models/pieChart.js," +
                    "/js/lib/scriptaculous-js-1.9.0/prototype.js," +
                    "/js/lib/scriptaculous-js-1.9.0/scriptaculous.js," +
                    "/js/lib/scriptaculous-js-1.9.0/effects.js," +
                    "/js/lib/scriptaculous-js-1.9.0/dragdrop.js," +
                    "/common/rs/popup.js," +
                    "/fckeditor/fckeditor.js")
    private String[] pagesToIncludeInHeader;

    @Inject @Config("/components/colorpicker/js/colorPicker.jsp")
    private String[] jspPagesToIncludeInHeader;

    @Inject @Config("")
    private String[] jspPagesToIncludeInBottom;

    @Inject @Config("")
    private String[] pagesToIncludeInBottom;

    @PostConstruct
    public void start() throws Exception {
        setExists(HEAD, deployJS(HEAD));
        setExists(BOTTOM, deployJS(BOTTOM));
    }

    public String getJSFileURL(String position) {
        return getJSFilePath(position);
    }

    public String getJSFilePath(String position) {
        if (HEAD.equals(position)) return headScriptFile;
        else if (BOTTOM.equals(position)) return bottomScriptFile;
        return null;
    }

    public String[] getJSPFilesPath(String position) {
        if (HEAD.equals(position)) return jspPagesToIncludeInHeader;
        else if (BOTTOM.equals(position)) return jspPagesToIncludeInBottom;
        return null;
    }

    public boolean checkAndDeploy(String position) {
        if (!getExists(position)) return setExists(position, deployJS(position));
        return true;
    }

    protected boolean getExists(String position) {
        if (HEAD.equals(position)) return existHead;
        else if (BOTTOM.equals(position)) return existBottom;
        return false;
    }
    
    protected boolean setExists(String position, boolean value) {
        if (HEAD.equals(position)) existHead = value;
        else if (BOTTOM.equals(position)) existBottom = value;
        return value;
    }
    
    public boolean deployJS(String position) {
        if (HEAD.equals(position)) return deployJS(headScriptFile, pagesToIncludeInHeader);
        else if (BOTTOM.equals(position)) return deployJS(bottomScriptFile, pagesToIncludeInBottom);

        log.warn("Unable to deploy JS option '" + position +"'");
        return false;
    }

    protected boolean deployJS(String scriptFile, String[] pagesToInclude) {
        if (StringUtils.isEmpty(scriptFile) || ArrayUtils.isEmpty(pagesToInclude)) return false;

        BufferedWriter out = null;

        try {
            File destFile = getScriptFile(scriptFile);
            out = new BufferedWriter(new FileWriter(destFile));

            for (String pageToInclude : pagesToInclude) {
                BufferedReader in = new BufferedReader(new FileReader(Application.lookup().getBaseAppDirectory() + pageToInclude));
                try {
                    String l;
                    while ((l = in.readLine()) != null) {
                        out.write(l.trim());
                        out.newLine();
                    }
                } catch (Exception e) {
                    log.error("Error writting JS file '" + pageToInclude + "': ", e);
                } finally {
                    try {in.close();} catch (Exception ex) {}
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error writting JS file: ", e);
        } finally {
            if (out != null) try {out.close();} catch (Exception ex){}
        }

        return false;
    }

    private File getScriptFile(String scriptFile) throws Exception {
        File destFile = new File(Application.lookup().getBaseAppDirectory() + scriptFile);

        if (destFile.exists()) destFile.delete();
        if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();

        destFile.createNewFile();
        
        return destFile; 
    }

    public String[] getPagesToIncludeInHeader() {
        return pagesToIncludeInHeader;
    }

    public void setPagesToIncludeInHeader(String[] pagesToIncludeInHeader) {
        this.pagesToIncludeInHeader = pagesToIncludeInHeader;
    }

    public String[] getPagesToIncludeInBottom() {
        return pagesToIncludeInBottom;
    }

    public void setPagesToIncludeInBottom(String[] pagesToIncludeInBottom) {
        this.pagesToIncludeInBottom = pagesToIncludeInBottom;
    }

    public String[] getJspPagesToIncludeInHeader() {
        return jspPagesToIncludeInHeader;
    }

    public void setJspPagesToIncludeInHeader(String[] jspPagesToIncludeInHeader) {
        this.jspPagesToIncludeInHeader = jspPagesToIncludeInHeader;
    }

    public String[] getJspPagesToIncludeInBottom() {
        return jspPagesToIncludeInBottom;
    }

    public void setJspPagesToIncludeInBottom(String[] jspPagesToIncludeInBottom) {
        this.jspPagesToIncludeInBottom = jspPagesToIncludeInBottom;
    }

    public String getHeadScriptFile() {
        return headScriptFile;
    }

    public void setHeadScriptFile(String headScriptFile) {
        this.headScriptFile = headScriptFile;
    }

    public String getBottomScriptFile() {
        return bottomScriptFile;
    }

    public void setBottomScriptFile(String bottomScriptFile) {
        this.bottomScriptFile = bottomScriptFile;
    }

    public boolean isExistHead() {
        return existHead;
    }

    public void setExistHead(boolean existHead) {
        this.existHead = existHead;
    }

    public boolean isExistBottom() {
        return existBottom;
    }

    public void setExistBottom(boolean existBottom) {
        this.existBottom = existBottom;
    }
}
