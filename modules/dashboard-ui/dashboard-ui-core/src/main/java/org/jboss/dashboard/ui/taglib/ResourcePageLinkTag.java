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

import org.jboss.dashboard.ui.resources.Resource;
import org.jboss.dashboard.ui.resources.UrlResource;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class ResourcePageLinkTag extends ResourceLinkTag {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourcePageLinkTag.class.getName());

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return new VariableInfo[0];
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.String", true, VariableInfo.AT_END)
                });
        }
    }

    protected String getResourceUrl() throws Exception {
        Resource resource = getResource();
        if (resource == null)
            return null;
        if (!(resource instanceof UrlResource)) {
            log.error("Cannot use resource as a page. Must be UrlResource.");
            throw new Exception("Cannot use resource as a page. Must be UrlResource.");
        }
        String url = ((UrlResource) resource).getResourcePage(pageContext.getRequest(), pageContext.getResponse());
        log.debug("Generated resource url: " + url);
        return url;
    }

}
