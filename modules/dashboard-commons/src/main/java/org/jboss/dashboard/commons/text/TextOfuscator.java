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
package org.jboss.dashboard.commons.text;

public class TextOfuscator {
    public static String obfuscatedPrefix = "HID:";

    /**
     * TextOfuscator constructor comment.
     */
    private TextOfuscator() {
        super();
    }

    /* ------------------------------------------------------------ */
    public static String deobfuscate(String s) {
        if (s.startsWith(obfuscatedPrefix))
            s = s.substring(obfuscatedPrefix.length());
        else
            return s;

        byte[] b = new byte[s.length() / 2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            String x = s.substring(i, i + 4);
            int i0 = Integer.parseInt(x, 36);
            int i1 = (i0 / 256);
            int i2 = (i0 % 256);
            b[l++] = (byte) ((i1 + i2 - 254) / 2);
        }

        return new String(b, 0, l);
    }

    /**
     * Insert the method's description here.
     * Creation date: (20/3/01 12:30:26)
     *
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Faltan argumentos");
        } else {
            String obf = obfuscate(obfuscatedPrefix + args[0]);
            System.out.println(obf);
//		System.out.println( deobfuscate(obf) );
        }
    }
/* ------------------------------------------------------------ */

    public static String obfuscate(String s) {
        if (s.startsWith(obfuscatedPrefix))
            s = s.substring(obfuscatedPrefix.length());
        else
            return new String(s);

        StringBuffer buf = new StringBuffer();
        byte[] b = s.getBytes();

        synchronized (buf) {
            buf.append(obfuscatedPrefix);
            for (int i = 0; i < b.length; i++) {
                byte b1 = b[i];
                byte b2 = b[s.length() - (i + 1)];
                int i1 = (int) b1 + (int) b2 + 127;
                int i2 = (int) b1 - (int) b2 + 127;
                int i0 = i1 * 256 + i2;
                String x = Integer.toString(i0, 36);

                switch (x.length()) {
                    case 1:
                        buf.append('0');
                    case 2:
                        buf.append('0');
                    case 3:
                        buf.append('0');
                    default :
                        buf.append(x);
                }
            }
            return buf.toString();
        }
    }
}
