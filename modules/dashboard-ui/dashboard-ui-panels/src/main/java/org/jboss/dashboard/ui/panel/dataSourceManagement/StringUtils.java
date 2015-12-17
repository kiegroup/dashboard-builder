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
package org.jboss.dashboard.ui.panel.dataSourceManagement;

public class StringUtils {
    public static final String SPECIAL_CHARS = String.copyValueOf(new char[] {

        '\u00A0', // NO-BREAK SPACE

        '\u00A1', // INVERTED EXCLAMATION MARK

        '\u00A2', // CENT SIGN

        '\u00A3', // POUND SIGN

        '\u00A4', // CURRENCY SIGN

        '\u00A5', // YEN SIGN

        '\u00A6', // BROKEN BAR

        '\u00A7', // SECTION SIGN

        '\u00A8', // DIAERESIS

        '\u00A9', // COPYRIGHT SIGN

        '\u00AA', // FEMININE ORDINAL INDICATOR

        '\u00AB', // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK

        '\u00AC', // NOT SIGN

        '\u00AD', // SOFT HYPHEN

        '\u00AE', // REGISTERED SIGN

        '\u00AF', // MACRON

        '\u00B0', // DEGREE SIGN

        '\u00B1', // PLUS-MINUS SIGN

        '\u00B2', // SUPERSCRIPT TWO

        '\u00B3', // SUPERSCRIPT THREE

        '\u00B4', // ACUTE ACCENT

        '\u00B5', // MICRO SIGN

        '\u00B6', // PILCROW SIGN

        '\u00B7', // MIDDLE DOT

        '\u00B8', // CEDILLA

        '\u00B9', // SUPERSCRIPT ONE

        '\u00BA', // MASCULINE ORDINAL INDICATOR

        '\u00BB', // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK

        '\u00BC', // VULGAR FRACTION ONE QUARTER

        '\u00BD', // VULGAR FRACTION ONE HALF

        '\u00BE', // VULGAR FRACTION THREE QUARTERS

        '\u00BF', // INVERTED QUESTION MARK    }

        '\u00D7', // MULTIPLICATION SIGN

        '\u00F7' // DIVISION SIGN

    });

    public static final String TILDED_CHARS = String.copyValueOf(new char[] {

        '\u00C0', // 0x00C0 # LATIN CAPITAL LETTER A WITH GRAVE

        '\u00C1', // 0x00C1 # LATIN CAPITAL LETTER A WITH ACUTE

        '\u00C2', // 0x00C2 # LATIN CAPITAL LETTER A WITH CIRCUMFLEX

        '\u00C3', // 0x00C3 # LATIN CAPITAL LETTER A WITH TILDE

        '\u00C4', // 0x00C4 # LATIN CAPITAL LETTER A WITH DIAERESIS



        '\u00C8', // 0x00C8 # LATIN CAPITAL LETTER E WITH GRAVE

        '\u00C9', // 0x00C9 # LATIN CAPITAL LETTER E WITH ACUTE

        '\u00CA', // 0x00CA # LATIN CAPITAL LETTER E WITH CIRCUMFLEX

        '\u00CB', // 0x00CB # LATIN CAPITAL LETTER E WITH DIAERESIS



        '\u00CC', // 0x00CC # LATIN CAPITAL LETTER I WITH GRAVE

        '\u00CD', // 0x00CD # LATIN CAPITAL LETTER I WITH ACUTE

        '\u00CE', // 0x00CE # LATIN CAPITAL LETTER I WITH CIRCUMFLEX

        '\u00CF', // 0x00CF # LATIN CAPITAL LETTER I WITH DIAERESIS



        '\u00D2', // 0x00D2 # LATIN CAPITAL LETTER O WITH GRAVE

        '\u00D3', // 0x00D3 # LATIN CAPITAL LETTER O WITH ACUTE

        '\u00D4', // 0x00D4 # LATIN CAPITAL LETTER O WITH CIRCUMFLEX

        '\u00D5', // 0x00D5 # LATIN CAPITAL LETTER O WITH TILDE

        '\u00D6', // 0x00D6 # LATIN CAPITAL LETTER O WITH DIAERESIS



        '\u00D9', // 0x00D9 # LATIN CAPITAL LETTER U WITH GRAVE

        '\u00DA', // 0x00DA # LATIN CAPITAL LETTER U WITH ACUTE

        '\u00DB', // 0x00DB # LATIN CAPITAL LETTER U WITH CIRCUMFLEX

        '\u00DC', // 0x00DC # LATIN CAPITAL LETTER U WITH DIAERESIS



        '\u00E0', // 0x00E0 # LATIN SMALL LETTER A WITH GRAVE

        '\u00E1', // 0x00E1 # LATIN SMALL LETTER A WITH ACUTE

        '\u00E2', // 0x00E2 # LATIN SMALL LETTER A WITH CIRCUMFLEX

        '\u00E3', // 0x00E3 # LATIN SMALL LETTER A WITH TILDE

        '\u00E4', // 0x00E4 # LATIN SMALL LETTER A WITH DIAERESIS



        '\u00E8', // 0x00E8 # LATIN SMALL LETTER E WITH GRAVE

        '\u00E9', // 0x00E9 # LATIN SMALL LETTER E WITH ACUTE

        '\u00EA', // 0x00EA # LATIN SMALL LETTER E WITH CIRCUMFLEX

        '\u00EB', // 0x00EB # LATIN SMALL LETTER E WITH DIAERESIS



        '\u00EC', // 0x00EC # LATIN SMALL LETTER I WITH GRAVE

        '\u00ED', // 0x00ED # LATIN SMALL LETTER I WITH ACUTE

        '\u00EE', // 0x00EE # LATIN SMALL LETTER I WITH CIRCUMFLEX

        '\u00EF', // 0x00EF # LATIN SMALL LETTER I WITH DIAERESIS



        '\u00F2', // 0x00F2 # LATIN SMALL LETTER O WITH GRAVE

        '\u00F3', // 0x00F3 # LATIN SMALL LETTER O WITH ACUTE

        '\u00F4', // 0x00F4 # LATIN SMALL LETTER O WITH CIRCUMFLEX

        '\u00F5', // 0x00F5 # LATIN SMALL LETTER O WITH TILDE

        '\u00F6', // 0x00F6 # LATIN SMALL LETTER O WITH DIAERESIS



        '\u00F9', // 0x00F9 # LATIN SMALL LETTER U WITH GRAVE

        '\u00FA', // 0x00FA # LATIN SMALL LETTER U WITH ACUTE

        '\u00FB', // 0x00FB # LATIN SMALL LETTER U WITH CIRCUMFLEX

        '\u00FC', // 0x00FC # LATIN SMALL LETTER U WITH DIAERESIS



        '\u00C7', // 0x00C7 # LATIN CAPITAL LETTER C WITH CEDILLA

        '\u00E7', // 0x00E7 # LATIN SMALL LETTER C WITH CEDILLA

        '\u00D1', // 0x00D1 # LATIN CAPITAL LETTER N WITH TILDE

        '\u00F1'  // 0x00F1 # LATIN SMALL LETTER N WITH TILDE

    });



    public static final String NON_TILDED_CHARS = "AAAAAEEEEIIIIOOOOOUUUUaaaaaeeeeiiiiooooouuuuCcNn";

    public static String toJavaIdentifier(String str) {

        if (str == null || str.trim().equals("")) return null;

        StringBuffer buf = new StringBuffer(str);

        int bufIdx = 0;

        while (bufIdx < buf.length()) {

            char c = buf.charAt(bufIdx);



            // Replace tilded by non-tilded chars.

            int tilded = TILDED_CHARS.indexOf(c);

            if (tilded!=-1 && tilded<NON_TILDED_CHARS.length()) {

                buf.deleteCharAt(bufIdx);

                c = NON_TILDED_CHARS.charAt(tilded);

                buf.insert(bufIdx++, c);

                continue;

            }

            // Discard special chars and non-valid java identifiers.

            int special = SPECIAL_CHARS.indexOf(c);

            if (special!=-1 || !Character.isJavaIdentifierPart(c)) {

                buf.deleteCharAt(bufIdx);

                continue;

            }

            // Adjust buffer index.

            bufIdx++;

        }

        if (buf.length() == 0) return "";

        while (buf.length() > 0 && !Character.isJavaIdentifierStart(buf.charAt(0))) buf.deleteCharAt(0);

        return buf.toString();

    }

}
