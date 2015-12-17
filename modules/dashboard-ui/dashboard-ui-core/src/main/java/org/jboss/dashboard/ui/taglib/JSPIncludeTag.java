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
package org.jboss.dashboard.ui.taglib;

import javax.servlet.jsp.JspException;

/**
 * A tag that adds error handling support and profiling instrumentation to JSPs.
 */
public class JSPIncludeTag extends BaseTag {

    /** The JSP to include. */
    protected String page = null;

    /** The JSP flush flag. */
    protected Boolean flush = false;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Boolean getFlush() {
        return flush;
    }

    public void setFlush(Boolean flush) {
        this.flush = flush;
    }

    public int doStartTag() throws JspException {
        super.jspInclude(page);
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}
