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
package org.jboss.dashboard.toolkit.factory.util;

import sun.misc.CRC16;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash extends TextConverterFunction {
    
    public String convertValue(String value) {
        if (value == null || "".equals(value)) return "";
        return digestPassword(value);
    }

    public String digestPassword(String password) {
        try {
            if (password == null || "".equals(password)) {
                return "";
            }
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] shaPassword = md.digest(password.getBytes());
    
            CRC16 crcCalculator = new CRC16();
            for (int i = 0; i < shaPassword.length; i++) {
                byte cipheredByte = shaPassword[i];
                crcCalculator.update(cipheredByte);
            }
            //Add another constant byte to make sure we don't use empty byte[]
            crcCalculator.update((byte) 0x69);
            byte[] cipheredAndCRCBytes = new byte[shaPassword.length + 2];
            System.arraycopy(shaPassword, 0, cipheredAndCRCBytes, 0, shaPassword.length);
            cipheredAndCRCBytes[cipheredAndCRCBytes.length - 2] = (byte) (crcCalculator.value >> 8);
            cipheredAndCRCBytes[cipheredAndCRCBytes.length - 1] = (byte) (crcCalculator.value & 0xff);
            return encode(cipheredAndCRCBytes);
        }
        catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public static String encode(byte[] raw) {
        StringBuffer encoded = new StringBuffer();
        for (int i = 0; i < raw.length; i += 3) {
            encoded.append(encodeBlock(raw, i));
        }
        return encoded.toString();
    }

    protected static char[] encodeBlock(byte[] raw, int offset) {
        int block = 0;
        int slack = raw.length - offset - 1;
        int end = (slack >= 2) ? 2 : slack;
        for (int i = 0; i <= end; i++) {
            byte b = raw[offset + i];
            int neuter = (b < 0) ? b + 256 : b;
            block += neuter << (8 * (2 - i));
        }
        char[] base64 = new char[4];
        for (int i = 0; i < 4; i++) {
            int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
            base64[i] = getChar(sixbit);
        }
        if (slack < 1) base64[2] = '=';
        if (slack < 2) base64[3] = '=';
        return base64;
    }

    protected static char getChar(int sixBit) {
        if (sixBit >= 0 && sixBit <= 25) return (char) ('A' + sixBit);
        if (sixBit >= 26 && sixBit <= 51) return (char) ('a' + (sixBit - 26));
        if (sixBit >= 52 && sixBit <= 61) return (char) ('0' + (sixBit - 52));
        if (sixBit == 62) return '+';
        if (sixBit == 63) return '/';
        return '?';
    }
}
