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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.EnvelopesManager;
import org.hibernate.Session;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 *
 */
public class EnvelopeContentTag extends TagSupport {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(EnvelopeContentTag.class.getName());

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData tagData) {
            VariableInfo[] info = new VariableInfo[]{};
            return info;
        }
    }

    public int doStartTag() throws JspException {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Throwable {
                pageContext.include("/templates/content.jsp");
                EnvelopesManager envelopesManager = UIServices.lookup().getEnvelopesManager();
                if (envelopesManager.getFinalBodyIncludePages() != null) {
                    for (int i = 0; i < envelopesManager.getFinalBodyIncludePages().length; i++) {
                        String page = envelopesManager.getFinalBodyIncludePages()[i];
                        pageContext.include(page);
                    }
                }
            }}.execute();
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }
}
