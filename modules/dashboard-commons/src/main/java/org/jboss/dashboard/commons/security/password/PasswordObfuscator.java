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
package org.jboss.dashboard.commons.security.password;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.security.cipher.PBEWithMD5AndDESCipher;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named("passwordObfuscator")
public class PasswordObfuscator {

    public static PasswordObfuscator lookup() {
        return (PasswordObfuscator) CDIBeanLocator.getBeanByName("passwordObfuscator");
    }

    @Inject @Config("true")
    protected boolean enabled;

    @Inject @Config("OBF:")
    protected String prefix;

    protected PBEWithMD5AndDESCipher cipher = new PBEWithMD5AndDESCipher();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public PBEWithMD5AndDESCipher getCipher() {
        return cipher;
    }

    public void setCipher(PBEWithMD5AndDESCipher cipher) {
        this.cipher = cipher;
    }

    public boolean isObfuscated(String password) {
        return password.startsWith(prefix);
    }

    public String obfuscate(String password) {
        try {
            if (!isEnabled() || isObfuscated(password)) return password;

            String obfPwd = cipher.encrypt(password);
            return prefix + obfPwd;
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    public String deobfuscate(String password) {
        try {
            if (!isObfuscated(password)) return password;

            String obfPwd = password.substring(prefix.length());
            return cipher.decrypt(obfPwd);
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }
}
