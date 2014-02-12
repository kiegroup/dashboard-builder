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

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CSRF Token generator & validator.
 */
@SessionScoped
public class CSRFTokenGenerator implements Serializable {

    public static CSRFTokenGenerator lookup() {
        return CDIBeanLocator.getBeanByType(CSRFTokenGenerator.class);
    }

    protected String tokenName = "csrf";
    protected List<String> tokenList = new ArrayList<String>();
    protected int maxTokens = 1000;
    protected int tokenSize = 8;

    @PostConstruct
    protected void init() {
        generateToken();
    }

    public String getTokenName() {
        return tokenName;
    }

    public synchronized String generateToken() {
        long seed = (long) (Math.random() * Math.pow(10, tokenSize));
        String token = StringUtils.leftPad(String.valueOf(seed), tokenSize, "0");

        if (tokenList.size() == maxTokens) tokenList.remove(0);
        tokenList.add(token);
        return token;
    }

    public synchronized String getLastToken() {
        return tokenList.get(tokenList.size()-1);
    }

    public synchronized boolean isValidToken(String token) {
        return tokenList.contains(token);
    }
}
