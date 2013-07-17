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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.FactoryWork;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.EnvelopesManager;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.VariableInfo;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class EnvelopeHeadTag extends TagSupport {
    private boolean allowScripts = true;
    private boolean allowPages = true;
    private boolean allowEnvelopes = true;

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EnvelopeHeadTag.class.getName());

    public static final String ENVELOPE_TOKEN = "envelopeHeadToken";

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData tagData) {
            VariableInfo[] info = new VariableInfo[]{};
            return info;
        }
    }

    public int doStartTag() throws JspException {
        Factory.doWork(new FactoryWork() { //In case this is called from a pure jsp...
            public void doWork() {
                pageContext.getRequest().setAttribute(ENVELOPE_TOKEN, Boolean.TRUE);
/*
                // Removed due to incompatibilities with some charting libraries
                try {
                    printBaseHref();
                } catch (IOException e) {
                    log.error("Error: ", e);
                }
*/

                EnvelopesManager envelopesManager = null;

                if (allowEnvelopes) {
                    envelopesManager = UIServices.lookup().getEnvelopesManager();

                    if (envelopesManager.getBeforeHeaderIncludePages() != null)
                        for (int i = 0; i < envelopesManager.getBeforeHeaderIncludePages().length; i++) {
                            String page = envelopesManager.getBeforeHeaderIncludePages()[i];
                            try {
                                pageContext.include(page);
                            } catch (ServletException e) {
                                log.error("Error including " + page + " : ", e);
                            } catch (IOException e) {
                                log.error("Error including " + page + " : ", e);
                            }
                        }
                }

                try {
                    if (allowScripts) {
                        pageContext.include(envelopesManager.getScriptsIncludePage());
                    }
                } catch (ServletException e) {
                    log.error("Error including " + envelopesManager.getScriptsIncludePage() + " : ", e);
                } catch (IOException e) {
                    log.error("Error including " + envelopesManager.getScriptsIncludePage() + " : ", e);
                }

                List headers;
                if (allowPages) {
                    headers = envelopesManager.getHeaderPagesToInclude();
                    if (headers != null)
                        for (int i = 0; i < headers.size(); i++) {
                            String page = (String) headers.get(i);
                            try {
                                pageContext.include(page);
                            } catch (ServletException e) {
                                log.error("Error including " + page + " : ", e);
                            } catch (IOException e) {
                                log.error("Error including " + page + " : ", e);
                            }
                        }
                }
            }
        });
        return SKIP_BODY;
    }

    protected void printBaseHref() throws IOException {
        URLMarkupGenerator urlMarkupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        ServletRequest request = pageContext.getRequest();
        String baseHref = urlMarkupGenerator.getBaseHref(request);
        StringBuffer sb = new StringBuffer();
        sb.append("<base href=\"").append(baseHref).append("\">");
        pageContext.getOut().println(sb);
    }


    public boolean isAllowScripts() {
        return allowScripts;
    }

    public void setAllowScripts(boolean allowScripts) {
        this.allowScripts = allowScripts;
    }

    public boolean isAllowPages() {
        return allowPages;
    }

    public void setAllowPages(boolean allowPages) {
        this.allowPages = allowPages;
    }

    public boolean isAllowEnvelopes() {
        return allowEnvelopes;
    }

    public void setAllowEnvelopes(boolean allowEnvelopes) {
        this.allowEnvelopes = allowEnvelopes;
    }
}
