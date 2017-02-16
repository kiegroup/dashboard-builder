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
package org.jboss.dashboard.ui.controller.requestChain;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.events.SectionChangedEvent;
import org.jboss.dashboard.ui.events.WorkspaceChangedEvent;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import java.io.Serializable;

/**
 * CSRF Token generator & validator.
 */
@SessionScoped
public class CSRFTokenGenerator implements Serializable {

    public static CSRFTokenGenerator lookup() {
        return CDIBeanLocator.get().lookupBeanByType(CSRFTokenGenerator.class);
    }

    protected String tokenName = "csrf";
    protected String activeToken = null;
    protected int tokenSize = 8;

    public String getTokenName() {
        return tokenName;
    }

    public synchronized String getLastToken() {
        if (activeToken == null) {
            activeToken = generateToken();
        }
        return activeToken;
    }

    public synchronized boolean isValidToken(String token) {
        return activeToken == null || activeToken.equals(token);
    }

    public synchronized void resetToken() {
        activeToken = null;
    }

    public String generateToken() {
        long seed = (long) (Math.random() * Math.pow(10, tokenSize));
        return StringUtils.leftPad(String.valueOf(seed), tokenSize, "0");
    }

    // Tokens turn to be stale after a navigation change

    private void onWorkspaceChanged(@Observes WorkspaceChangedEvent event) {
        resetToken();
    }

    private void onSectionChanged(@Observes SectionChangedEvent event) {
        resetToken();
    }
}
