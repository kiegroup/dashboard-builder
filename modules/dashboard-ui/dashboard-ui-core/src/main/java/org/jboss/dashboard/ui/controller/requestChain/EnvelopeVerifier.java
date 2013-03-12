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
package org.jboss.dashboard.ui.controller.requestChain;

import org.jboss.dashboard.ui.taglib.EnvelopeFooterTag;
import org.jboss.dashboard.ui.taglib.EnvelopeHeadTag;

public class EnvelopeVerifier extends RequestChainProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(EnvelopeVerifier.class.getName());

    protected boolean processRequest() throws Exception {
        Boolean headToken = (Boolean) getRequest().getAttribute(EnvelopeHeadTag.ENVELOPE_TOKEN);
        if (headToken != null && Boolean.TRUE.equals(headToken)) {
            Boolean footerToken = (Boolean) getRequest().getAttribute(EnvelopeFooterTag.ENVELOPE_TOKEN);
            if (footerToken == null || Boolean.FALSE.equals(footerToken)) {
                log.error("Invalid envelope: <panel:envelopeFooter/> tag MUST be included just before the </body> tag both in full.jsp AND shared.jsp.");
            }
        }
        return true;
    }
}
