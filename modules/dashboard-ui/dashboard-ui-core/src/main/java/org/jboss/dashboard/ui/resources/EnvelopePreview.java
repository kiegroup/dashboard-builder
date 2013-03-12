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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.factory.Factory;

import java.io.File;
import java.io.Serializable;

/**
 *
 */
public class EnvelopePreview extends GraphicElementPreview implements Serializable, ResourceHolder {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(EnvelopePreview.class.getName());

    public EnvelopePreview(File f, String workspaceId, Long sectionId, Long panelId, String id) {
        super(f, workspaceId, sectionId, panelId, id);
        boolean isRoot = UserStatus.lookup().isRootUser();
        if (!isRoot && status == STATUS_OK) {
            byte[] jsp = (byte[]) resourcesDeployed.get("FULL_PAGE");
            if (new String(jsp).indexOf("<%") != -1)
                status = STATUS_JSP_INSECURE;
            if (new String(jsp).indexOf("<jsp:include") != -1)
                status = STATUS_JSP_INSECURE;
            jsp = (byte[]) resourcesDeployed.get("SHARED_PAGE");
            if (new String(jsp).indexOf("<%") != -1)
                status = STATUS_JSP_INSECURE;
            if (new String(jsp).indexOf("<jsp:include") != -1)
                status = STATUS_JSP_INSECURE;
        }
    }

    /**
     * Convert this preview into an envelope.
     *
     * @return
     */
    public Envelope toEnvelope() {
        return (Envelope) toElement();
    }

    protected String getDescriptorFilename() {
        return Envelope.DESCRIPTOR_FILENAME;
    }

    protected GraphicElement makeNewElement() {
        return new Envelope();
    }
}
