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
package org.jboss.dashboard.commons.text;

import java.io.UnsupportedEncodingException;

/**
 * Utility class providing base64-encoding methods. It uses Apache commons Base64 as underlying library.
 */
public class Base64 {

    /**
     * Decodes a BASE64 string.
     *
     * @param base64 Codified string
     * @return Array of bytes of decodified string
     */
    public static byte[] decode(String base64) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(base64.getBytes());
    }

    /**
     * Encodes an array of bytes into a BASE64 string
     *
     * @param raw Array of bytes
     * @return Codified string
     */
    public static String encode(byte[] raw) {
        byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(raw);
        try {
            return new String(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // It must never happen
            return new String(encoded);
        }
    }
}
