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
package org.jboss.dashboard.commons.security.cipher;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.jboss.dashboard.commons.text.Base64;

public class PBEWithMD5AndDESCipher {

    private static final String CIPHER = "PBEWithMD5AndDES";
    private static final char[] SECRET = "jboss".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    public String encrypt(String text) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET));
        Cipher pbeCipher = Cipher.getInstance(CIPHER);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return Base64.encode(pbeCipher.doFinal(text.getBytes()));
    }

    public String decrypt(String text) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SECRET));
        Cipher pbeCipher = Cipher.getInstance(CIPHER);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(Base64.decode(text)));
    }

    public static void main(String[] args) throws Exception {
        PBEWithMD5AndDESCipher cipher = new PBEWithMD5AndDESCipher();
        String originalPassword = "secret";
        System.out.println("Original password: " + originalPassword);
        String encryptedPassword = cipher.encrypt(originalPassword);
        System.out.println("Encrypted password: " + encryptedPassword);
        String decryptedPassword = cipher.decrypt(encryptedPassword);
        System.out.println("Decrypted password: " + decryptedPassword);
    }
}
