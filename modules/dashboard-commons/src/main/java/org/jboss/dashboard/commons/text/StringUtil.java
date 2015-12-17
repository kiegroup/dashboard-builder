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

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * Utilities to work with strings.
 */
public final class StringUtil {

    /**
     * Justify text to the left side.
     */
    public static final int LEFT_JUSTIFY = 0;

    /**
     * Justify text to the right side.
     */
    public static final int RIGHT_JUSTIFY = 1;

    /**
     * Center text.
     */
    public static final int CENTER_JUSTIFY = 2;

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

    public static final String[] JAVA_KEYWORDS = {"abstract", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue", "default", "do", "double", "else",
            "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try", "void",
            "volatile", "while"};


    /**
     * Constructs an empty object.
     * This constructor is private to prevent instantiating this class.
     */
    private StringUtil() {
    }

    /**
     * Replaces a character of a String with characters in the specified
     * new substring.
     *
     * @param origStr string that contains the replaceable substring
     * @param oldChar character to search in the orig String
     * @param newStr  substring of the new characters
     * @return if the parameters are correct returns the new string; otherwise
     *         if origStr parameter is null returns null. If newstr parameter is
     *         null returns origStr parameter
     * @deprecated replaced by replaceAll()
     */
    public static String replace(String origStr, char oldChar, String newStr) {
        return replaceAll(origStr, oldChar, newStr);
    }

    /**
     * Replaces a character of a String with characters in the specified
     * new substring.
     *
     * @param origStr string that contains the replaceable substring
     * @param oldChar character to search in the orig String
     * @param newStr  substring of the new characters
     * @return if the parameters are correct returns the new string; otherwise
     *         if origStr parameter is null returns null. If newstr parameter is
     *         null returns origStr parameter
     */
    public static String replaceAll(String origStr, char oldChar, String newStr) {
        if (origStr == null) {
            return null;
        } else if (newStr == null) {
            return origStr;
        }

        StringBuffer buffer = new StringBuffer(origStr);
        int index = origStr.indexOf(oldChar);
        int replIndex;
        int insertPadding = 0;
        int padding = newStr.length() - 1;
        while (index > -1) {
            replIndex = index + insertPadding;
            buffer.replace(replIndex, (replIndex + 1), newStr);
            index++;
            index = origStr.indexOf(oldChar, index);
            insertPadding += padding;
        }
        return buffer.toString();
    }

    /**
     * Replacing first ocurrence of oldStr in origStr with newStr
     *
     * @param origStr original string
     * @param oldStr  substring to search in the orig String
     * @param newStr  new substring
     * @return a new string with the first ocurrence of oldStr replaced by newStr
     */
    public static String replaceFirst(String origStr, String oldStr, String newStr) {

        if (origStr == null) {
            return null;
        } else if (oldStr == null || newStr == null) {
            return origStr;
        }

        String newText = null;
        int index = origStr.indexOf(oldStr);

        switch (index) {
            case -1:
                return origStr;
            case 0:
                newText = newStr + origStr.substring(oldStr.length());
                return newText;
            default:
                newText = origStr.substring(0, index) + newStr +
                        origStr.substring(index + oldStr.length());
                return newText;
        }
    }

    /**
     * Replaces the characters in a substring of a String with characters in
     * the specified new substring.
     *
     * @param origStr original string
     * @param oldStr  substring to search in the orig String
     * @param newStr  new substring
     * @return if the parameters are correct returns the new string; otherwise
     *         if orig parameter is null returns null. If str or newstr parameters are
     *         null returns orig parameter
     * @deprecated replaced by replaceAll()
     */
    public static String replace(String origStr, String oldStr, String newStr) {
        return replaceAll(origStr, oldStr, newStr);
    }

    /**
     * Replaces the characters in a substring of a String with characters in
     * the specified new substring.
     *
     * @param origStr original string
     * @param oldStr  substring to search in the orig String
     * @param newStr  new substring
     * @return if the parameters are correct returns the new string; otherwise
     *         if orig parameter is null returns null. If str or newstr parameters are
     *         null returns orig parameter
     */
    public static String replaceAll(String origStr, String oldStr,
                                    String newStr) {

        if (origStr == null) {
            return null;
        } else if (oldStr == null || newStr == null) {
            return origStr;
        }

        StringBuffer buf = new StringBuffer(origStr);
        int inicio = origStr.indexOf(oldStr);

        if (inicio == -1) {
            return origStr;
        }

        while (inicio != -1) {
            buf.replace(inicio, inicio + oldStr.length(), newStr);
            inicio = buf.toString().indexOf(oldStr, inicio + newStr.length());
        }

        return buf.toString();
    }

    /**
     * Remove the first ocurrence of a substring from a string
     *
     * @param origStr   original string
     * @param removeStr substring to remove from the orig String
     * @return an string without the first ocurrence of the substring passed
     *         as a parameter
     */
    public static String removeFirst(String origStr, String removeStr) {
        return replaceFirst(origStr, removeStr, "");
    }

    /**
     * Remove all the ocurrences of a substring from a string
     *
     * @param origStr   original string
     * @param removeStr substring to remove from the orig String
     * @return an string without all the ocurrences of the substring passed
     *         as a parameter
     */
    public static String removeAll(String origStr, String removeStr) {
        return replaceAll(origStr, removeStr, "");
    }

    /**
     * Replace all the occurences of oldSubstring with newSubstring in the
     * values of a parameter mapping structure
     *
     * @param params       the parameters mapping structure that contains
     *                     pairs of attr -> value
     * @param oldSubstring the string to replace
     * @param newSubstring the new string
     * @return new mapping structure (the original structure isn't changed).
     *         The values than aren't String are unchanged
     */
    public static Map replaceParamsValues(Map params, String oldSubstring,
                                          String newSubstring) {
        if (params == null) {
            return null;
        }
        Object param, value;
        Map newParams = new HashMap();
        Iterator i = params.keySet().iterator();

        while (i.hasNext()) {
            param = i.next();
            value = params.get(param);
            if (value == null) {
                continue;
            }

            if (value instanceof java.lang.String) {
                newParams.put(param, replaceAll(value.toString(),
                        oldSubstring, newSubstring));
            } else {
                newParams.put(param, value);
            }
        }

        return newParams;
    }

    /**
     * Parses an ASCII template that contains parameters like
     * <code>[#param#]</code> and replace them with the values associated
     * in <code>params</code>.
     * <p/>
     * <p>The substitution of <code>[#param#]</code> is performed this
     * way:
     * <p/>
     * <ul>
     * <li>
     * If <code>params</code> contains a key named <code>param</code> and
     * its associated value is a non-<code>null</code> reference,
     * <code>[#param#]</code> is substituted by the
     * result of applying <code>toString()</code> to that reference.
     * </li>
     * <li>
     * Otherwise, either there is no key named <code>param</code> in
     * <code>params</code> or its associated value is <code>null</code>,
     * <code>[#param#]</code> is replaced with
     * the literal <code>&quot;null&quot;</code>.
     * </li>
     * </ul>
     *
     * @param template The parametrized template
     * @param params   The parameters structure
     * @return The template with parameters replaced
     */
    public static String parseASCIITemplate(String template, Map params) {
        return parseASCIITemplate(template, params, "[#", "#]", true);
    }

    /**
     * @param template  the parametrized template
     * @param params    the parameters structure
     * @param leftMark  the left delimiter of the parameters to replace
     * @param rightMark the right delimiter of the parameters to replace
     * @return The template with parameters replaced
     * @see #parseASCIITemplate(String,Map)
     */
    public static String parseASCIITemplate(String template, Map params,
                                            String leftMark, String rightMark) {
        return parseASCIITemplate(template, params, leftMark, rightMark, true);
    }

    /**
     * @param template        the parametrized template
     * @param params          the parameters structure
     * @param leftMark        the left delimiter of the parameters to replace
     * @param rightMark       the right delimiter of the parameters to replace
     * @param nullifyNotFound if true, change the not found params to "null";
     *                        if false, don't change
     * @return The template with parameters replaced
     * @see #parseASCIITemplate(String,Map)
     */
    public static String parseASCIITemplate(String template, Map params,
                                            String leftMark, String rightMark, boolean nullifyNotFound) {
        if (params == null) {
            if (!nullifyNotFound) {
                return template;
            } else {
                // Como hay que cambiar los par�metros por "null", creamos una
                // tabla de par�metros vac�a
                params = new HashMap();
            }
        }

        StringBuffer buffer = new StringBuffer(template);
        int leftIndex, rightIndex = 0;

        while ((leftIndex = indexOf(leftMark, rightIndex, buffer)) != -1) {
            rightIndex = indexOf(rightMark, leftIndex + leftMark.length(), buffer);
            if (rightIndex == -1) {
                break; // no right mark, this is likely to mean bad syntax
            }
            String param = buffer.substring(leftIndex + leftMark.length(), rightIndex);
            rightIndex += rightMark.length();
            if (params.containsKey(param)) {
                Object ref = params.get(param);
                String value = ref == null ? "null" : ref.toString();
                buffer.replace(leftIndex, rightIndex, value);
                rightIndex -= leftMark.length() + param.length() + rightMark.length();
                rightIndex += value.length();
            } else if (nullifyNotFound) {
                String value = "null";
                buffer.replace(leftIndex, rightIndex, value);
                rightIndex -= leftMark.length() + param.length() + rightMark.length();
                rightIndex += value.length();
            }
        }

        return buffer.toString();
    }

    /**
     * Returns the position of the first ocurrence of the substring into
     * the stringbuffer, or -1 if does not ocurr.
     * <br>
     * Returns the smaller i such that
     * <p/>
     * buf.substring(i, str.length()).equals(str),
     * <p/>
     * holds, and -1 if no such i exists.
     * <br>
     * Assumes str and buf are non-null.
     *
     * @param str substring to find
     * @param buf buffer to search into
     * @return the position of subStr into buf, or -1 if it does not ocurr
     */
    public static int indexOf(String str, StringBuffer buf) {
        return indexOf(str, 0, buf);
    }

    /**
     * Returns the position of the first ocurrence of the substring into
     * the stringbuffer, starting at fromIndex, or -1 if does not ocurr.
     * <br>
     * Returns the smaller i greater than or equals to fromIndex such that
     * <p/>
     * buf.substring(i, str.length()).equals(str),
     * <p/>
     * holds, and -1 if no such i exists.
     * <p/>
     * Assumes str and buf are non-null.
     *
     * @param str       substring to find
     * @param fromIndex the index to start the search from
     * @param buf       buffer to search into
     * @return the position of subStr into buf, or -1 if it does not ocurr
     */
    public static int indexOf(String str, int fromIndex, StringBuffer buf) {

        fromIndex = Math.max(fromIndex, 0);

        // begin degenerate cases
        if (fromIndex >= buf.length()) {
            if (fromIndex == 0 && str.length() == 0) {
                return 0;
            }
            return -1;
        }
        if (str.length() == 0) {
            return fromIndex;
        }
        // end degenerate cases

        int max = buf.length() - str.length();
        int i = fromIndex;
        while (true) {
            // look for the next occurrence of str.charAt(0)
            while (i <= max && buf.charAt(i) != str.charAt(0)) {
                ++i;
            }
            if (i > max) {
                return -1;
            }
            // once anchored, check the rest
            int j = i + 1, k = 1;
            while (k < str.length() && buf.charAt(j) == str.charAt(k)) {
                ++j;
                ++k;
            }
            if (k == str.length()) {
                return i; // all of them matched
            }
            ++i; // failed, try again
        }
    }

    // --*-- String utilities from ExtString.java at util0.4.jar --*--

    /**
     * Repeat a String n times.
     *
     * @param str String to repeat
     * @param num number times to repeat
     * @return a String who repeats <I>str</I>, <I>num</I> times.
     */
    public static String repeat(String str, int num) {
        StringBuffer str_ret = new StringBuffer(str.length() * num);

        for (int i = 0; i < num; i++) {
            str_ret.append(str);
        }

        return str_ret.toString();
    }

    /**
     * Upper case the first character in a String.
     *
     * @param str String to manipulate
     * @return the String <I>str</I> with first char in UpperCase and the rest
     *         like the original String.
     */
    public static String firstUp(String str) {
        return (str.substring(0, 1).toUpperCase() + str.substring(1, str.length()));
    }

    /**
     * Lower case the first character in a String.
     *
     * @param str String to manipulate
     * @return the String <I>str</I> with first char in LowerCase and the rest
     *         like the original String.
     */
    public static String firstLow(String str) {
        return (str.substring(0, 1).toLowerCase() + str.substring(1, str.length()));
    }

    /**
     * Upper case the first character in a String, lower the rest of the string.
     *
     * @param str String to manipulate
     * @return the String <I>str</I> with first char in UpperCase and the rest
     *         in LowerCase.
     */
    public static String lowerFirstUp(String str) {
        return (str.substring(0, 1).toUpperCase() +
                str.substring(1, str.length()).toLowerCase());
    }

    /**
     * Remove white space from both ends of this string and converts all of the
     * characters in this String to upper case.
     *
     * @param str String to manipulate
     * @return the String <I>str</I> with trim (without spaces at rigth and at
     *         left) and in UpperCase.
     */
    public static String trimUpperCase(String str) {
        return (str.trim().toUpperCase());
    }

    /**
     * Remove white space from both ends of this string and converts all of the
     * characters in this String to lower case.
     *
     * @param str String to manipulate
     * @return the String <I>str</I> with trim (without spaces at rigth and at
     *         left) and in LowerCase.
     */
    public static String trimLowerCase(String str) {
        return (str.trim().toLowerCase());
    }

    /**
     * Searches into a String for the prefix before the specified substring.
     *
     * @param str  String to manipulate
     * @param find String to find in <I>str</I>
     * @return the first part of String <I>str</I> before the first String
     *         <I>find</I> include into.
     *         If <I>str</I> doesn't contain <I>find</I> return all <I>str</I>
     *         exactly.
     */
    public static String findFirst(String str, String find) {
        int index = str.indexOf(find);
        if (index > -1) {
            return (str.substring(0, index));
        } else {
            return str;
        }
    }

    /**
     * Search into a String for the suffix after the first occurrence of the
     * specified substring.
     *
     * @param str  String to manipulate
     * @param find String to find in <I>str</I>
     * @return the rest part of String <I>str</I> after the first String
     *         <I>find</I> include into.
     *         If <I>str</I> doesn't contain <I>find</I> return all <I>str</I>
     *         exactly.
     */
    public static String findRest(String str, String find) {
        int index = str.indexOf(find);
        if (index > -1) {
            return (str.substring(index + find.length()));
        } else {
            return str;
        }
    }

    /**
     * Search into a String for the suffix after the last occurrence of the
     * specified substring.
     *
     * @param str  String to manipulate
     * @param find String to find in <I>str</I>
     * @return the last part of String <I>str</I> after the last String
     *         <I>find</I> include into.
     *         If <I>str</I> doesn't contain <I>find</I> return all <I>str</I>
     *         exactly.
     */
    public static String findLast(String str, String find) {
        int index = str.lastIndexOf(find);
        if (index > -1) {
            return (str.substring(index + find.length()));
        } else {
            return str;
        }
    }

    /**
     * Return a vector with <I>str</I> divided in fragments, separated by
     * <I>delimiter</I>.
     * If <I>str</I> doesn't contain <I>delimiter</I> return a vector with one
     * element: <I>str</I> exactly.
     *
     * @param str       String to manipulate
     * @param delimiter String who delimiters the Strings into <I>str</I>
     * @return vector with severals fragmentss of a String as elements of
     *         this vector
     */
    public static Vector getTokensFromString(String str, String delimiter) {
        StringTokenizer st = new StringTokenizer(str, delimiter);
        Vector vector = new Vector(st.countTokens());
        while (st.hasMoreTokens()) {
            vector.addElement(st.nextToken().trim());
        }
        return vector;
    }

    /**
     * Return a vector with <I>str</I> divided in fragments.
     * The fragments are separate by <I>blanks and other usual delimiters</I>:
     * " \t\n\r\f"; the space character, the tab character, the newline character,
     * the carriage-return character and the form-feed character.
     *
     * @param str String to manipulate
     * @return vector with severals fragments of a String as elements
     *         of this vector
     * @see java.util.StringTokenizer
     *      If <I>str</I> doesn't contain <I>delimiter</I> return a vector with one
     *      element: <I>str</I> exactly.
     */
    public static Vector getTokensFromString(String str) {
        if (str == null) {
            return new Vector();
        }

        StringTokenizer st = new StringTokenizer(str);
        Vector vector = new Vector(st.countTokens());
        while (st.hasMoreTokens()) {
            vector.addElement(st.nextToken());
        }
        return vector;
    }

    /**
     * Return a String who contains the strings in the Vector <I>vector</I>
     * separated by <I>delimiter</I>.
     *
     * @param vector    list of tokens
     * @param delimiter String who delimiters the Strings into <I>str</I>
     * @return Composed string
     */
    public static String getStringFromTokens(Vector vector, String delimiter) {
        int nelem = vector.size();
        // Initial capacity, at least include the delimiters
        StringBuffer str_ret = new StringBuffer(nelem * delimiter.length());

        for (int i = 0; i < nelem; i++) {
            str_ret.append(vector.elementAt(i));

            // The last delimiter isn't added
            if (i < nelem - 1) {
                str_ret.append(delimiter);
            }
        }
        return str_ret.toString();
    }

    /**
     * @see #substring(String,int,int)
     * @deprecated Use substring instead
     */
    public static String subString(String str, int beginIndex, int endIndex) {
        return substring(str, beginIndex, endIndex);
    }

    /**
     * Return a new String that is a substring of this string.
     * If the endIndex is out of index don�t throw a Exception
     *
     * @param str        Cadena de Texto
     * @param beginIndex the begining index, inclusive
     * @param endIndex   the ending index, exclusive
     * @return the specified substring
     */
    public static String substring(String str, int beginIndex, int endIndex) {
        if (str.length() < endIndex) {
            endIndex = str.length();
        }

        return str.substring(beginIndex, endIndex);
    }

    /**
     * Return a paragraph who insert spaces at the beginning of lines.
     * The amount of spaces that will be inserted is equals to the length of
     * all lines, ended with '\n', in the <i>textBefore</i> string, i.e, if
     * textBefore="in\nden\nstr" seven spaces will be inserted to indent the lines
     * of <i>textToModify</i> string.
     *
     * @param textBefore   String where extract the spaces to insert
     * @param textToModify String to modify
     * @return indented string
     * @deprecated Use indentParagraph(String,int) instead
     */
    public static String indentParagraph(String textBefore, String textToModify) {
        int indexU = textBefore.lastIndexOf('\n');
        return indentParagraph(textToModify, indexU + 1);
    }

    /**
     * Insert "indentSize" spaces before every line of textToModify.
     * If indentSize is negative or 0, the string is return unchanged
     *
     * @param textToModify string to indent
     * @param indentSize   number of spaces to insert
     * @return a string whith the lines indented
     */
    public static String indentParagraph(String textToModify, int indentSize) {
        int indexU;
        StringBuffer text;
        String textBegin;
        String textRest;
        String blanks;

        if (indentSize > 0) {
            blanks = repeat(" ", indentSize);
            indexU = textToModify.indexOf('\n');
            if (indexU > -1) {
                textRest = textToModify;
                text = new StringBuffer();
                while (indexU > -1) {
                    textBegin = textRest.substring(0, indexU + 1);
                    textRest = textRest.substring(indexU + 1).trim();
                    text.append(blanks).append(textBegin);
                    indexU = textRest.indexOf('\n');
                }
                text.append(blanks).append(textRest);

                return text.toString();
            } else {
                return textToModify;
            }
        } else {
            return textToModify;
        }
    }

    /**
     * Justify a string. If the size of the string is smaller than the parameter
     * "size", spaces are appended. If the string is larger than "size", the
     * string is truncated to size.
     *
     * @param str  string to justify
     * @param size size of line to justify to
     * @param type type of justify (LEFT_JUSTIFY, RIGHT_JUSTIFY or
     *             CENTER_JUSTIFY)
     * @return justified string
     */
    public static String justify(String str, int size, int type) {

        int spaces = size - str.length();

        if (spaces <= 0) {
            return str.substring(0, size);
        } else {
            StringBuffer justifiedStr = new StringBuffer();
            switch (type) {
                case RIGHT_JUSTIFY:
                    justifiedStr.append(repeat(" ", spaces));
                    justifiedStr.append(str);
                    break;
                case LEFT_JUSTIFY:
                    justifiedStr.append(str);
                    justifiedStr.append(repeat(" ", spaces));
                    break;
                case CENTER_JUSTIFY:
                    justifiedStr.append(repeat(" ", (spaces / 2)));
                    justifiedStr.append(str);
                    justifiedStr.append(repeat(" ", (spaces - (spaces / 2))));
                    break;
            }
            return justifiedStr.toString();
        }
    }


    /**
     * Remove all the ocurrences of a group of substring from a string
     *
     * @param str    original string
     * @param substr array of substrings to remove from the orig String
     * @return an string without all the ocurrences of the substring passed
     *         as a parameter
     */
    public static String removeAll(String str, String[] substr) {

        if (str == null || substr == null) {
            return str; // for the sake of robustness
        }

        StringBuffer buffer = new StringBuffer(str);
        int idx = 0;
        for (int i = 0; i < substr.length; i++) {
            idx = 0;
            while ((idx = indexOf(substr[i], idx, buffer)) != -1) {
                buffer.replace(idx, idx + substr[i].length(), "");
            }
        }
        return buffer.toString();
    }

    /**
     * Replaces the characters in all substrings of a String with a String.
     *
     * @param str original string
     * @param in  substrings to search in the orig String
     * @param out new String
     * @return if the parameters are correct returns the new string; otherwise
     *         if some of the parameters is null returns the original string
     */
    private static String replaceAll(String str, String[] in, String out) {
        if (str == null || in == null || out == null) {
            return str; // for the sake of robustness
        }

        StringBuffer buffer = new StringBuffer(str);

        int idx = 0;
        for (int i = 0; i < in.length; i++) {
            idx = 0;
            while ((idx = indexOf(in[i], idx, buffer)) != -1) {
                buffer.replace(idx, idx + in[i].length(), out);
            }
        }
        return buffer.toString();
    }

    /**
     * Replaces the characters in all substrings of a String with characters in
     * the specified array of substrings.
     *
     * @param str original string
     * @param in  substrings to search in the orig String
     * @param out array of new substrings
     * @return if the parameters are correct returns the new string; otherwise
     *         if some of the parameters is null returns the original string
     */
    public static String replaceAll(String str, String[] in, String[] out) {
        if (str == null || in == null || out == null || in.length != out.length) {
            return str; // for the sake of robustness
        }

        StringBuffer buffer = new StringBuffer(str);

        int idx = 0;
        for (int i = 0; i < in.length; i++) {
            idx = 0;
            while ((idx = indexOf(in[i], idx, buffer)) != -1) {
                buffer.replace(idx, idx + in[i].length(), out[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns a String who replace Strings include into <I>str</I>,
     * which are into delimiters and known in <I>in</I>,
     * to corresponding String from <I>out</I>.
     * If in and out aren't of the same size, the string is returned
     * unchanged.
     *
     * @param str        String to manipulate
     * @param in         Vector which contains strings to find
     * @param out        Vector which contains strings to replace
     * @param beginDelim String who delimiters the begin of the substrings
     *                   into <I>str</I>
     * @param endDelim   String who delimiters the end of the substrings
     *                   into <I>str</I>
     * @return the new string
     */
    public static String replaceAll(String str, Vector in, Vector out,
                                    String beginDelim, String endDelim) {
        return replaceAll(str,
                (String[]) in.toArray(),
                (String[]) out.toArray(),
                beginDelim,
                endDelim);
    }

    /**
     * Returns a String who replace Strings include into <I>str</I>,
     * which are into delimiters and known in <I>in</I>,
     * to corresponding String from <I>out</I>.
     * <br>
     * If in and out aren't of the same size, the string is returned
     * unchanged.
     * <br>
     * beginDelim and endDelim can't be the same. If they are, a
     * IllegalArgumentException is throw.
     *
     * @param str        String to manipulate
     * @param in         array of strings to find
     * @param out        array of strings to replace
     * @param beginDelim String who delimiters the begin of the substrings
     *                   into <I>str</I>
     * @param endDelim   String who delimiters the end of the substrings
     *                   into <I>str</I>
     * @return the new string
     * @throws IllegalArgumentException if beginDelim and endDelim are equal
     */
    public static String replaceAll(String str, String[] in, String[] out,
                                    String beginDelim, String endDelim) {

        if (in.length != out.length) {
            return str;
        }

        if (beginDelim.equals(endDelim)) {
            throw new IllegalArgumentException(
                    "beginDelim and endDelim are equals: " + beginDelim);
        }

        StringBuffer str_ret = new StringBuffer();
        String sFound;
        String sOut;
        String lastSegment;
        int lastIndex = 0;
        int indexBegin = str.indexOf(beginDelim);
        int indexEnd = -1;
        int indexIn = -1;
        int index = -1;
        boolean found = false;
        int countIn = in.length;

        while (indexBegin > -1) {
            indexEnd = str.indexOf(endDelim, indexBegin);
            if (beginDelim.equals(endDelim) && beginDelim.equals("")) {
                indexEnd++;
            } else {
                index = str.indexOf(beginDelim,
                        indexBegin + beginDelim.length());
                while (index > -1 && index < indexEnd) {
                    indexBegin = index;
                    index = str.indexOf(beginDelim,
                            indexBegin + beginDelim.length());
                }
            }
            sFound = str.substring(indexBegin + beginDelim.length(), indexEnd);
            lastSegment = str.substring(lastIndex, indexBegin);
            sOut = "";
            found = false;
            indexIn = 0;
            while (!found && indexIn < countIn) {
                if (sFound.compareToIgnoreCase(in[indexIn]) == 0) {
                    sOut = indentParagraph(lastSegment, out[indexIn]);
                    found = true;
                }
                indexIn++;
            }
            //dejamos el tag para que se note el error de tag no existente
            if (!found) {
                sOut = beginDelim + sFound + endDelim;
            }
            //if no word to replace and after tag to replace you have a CR ignore
            if (found && sOut.compareTo("") == 0) {
                index = lastSegment.lastIndexOf('\n');
                if (index > -1 &&
                        lastSegment.substring(index + 1).trim().compareTo("") == 0) {
                    lastSegment = lastSegment.substring(0, index);
                }
            }

            str_ret.append(lastSegment).append(sOut);
            lastIndex = indexEnd + endDelim.length();
            indexBegin = str.indexOf(beginDelim, lastIndex);
        }
        if (lastIndex < str.length()) {
            str_ret.append(str.substring(lastIndex, str.length()));
        }

        return str_ret.toString();
    }

    /**
     * Delete all the words of a string smaller that a given size, and the extra
     * blank characters
     *
     * @param str  string to process
     * @param size size limit
     * @return a string with the words smaller than size deleted, and one space
     *         between every word
     */
    public static String eliminateWordsShorterThan(String str, int size) {
        StringBuffer newStr = new StringBuffer();
        String word;
        StringTokenizer stringTokenizer = new StringTokenizer(str);

        while (stringTokenizer.hasMoreTokens()) {
            word = stringTokenizer.nextToken().trim();
            if (word.length() >= size) {
                newStr.append(" ").append(word);
            }
        }
        return newStr.toString().trim();
    }

    /**
     * Replace all the ocurrences of the char ' with '',
     * and add a ' at the start and the end of the string.
     * <br>
     * For example:
     * <pre>
     * select * from tabla where nombre = 'Peter' and edad = 25 =>
     * <p/>
     * select * from tabla where nombre = ''Peter'' and edad = 25
     * </pre>
     *
     * @param str Original String
     * @return Converted String,
     */
    public static String filterSQLString(String str) {
        return replaceAll(str, "'", "''");
    }

    /**
     * Convert a string to SQL sentence string.
     * Replace all the ocurrences of the char ' with '',
     * and add a ' at the start and the end of the string.
     * <br>
     * For example:
     * <pre>
     * select * from tabla where nombre = 'Peter' and edad = 25 =>
     * <p/>
     * 'select * from tabla where nombre = ''Peter'' and edad = 25'
     * </pre>
     *
     * @param str Original String
     * @return Converted String,
     */
    public static String makeSQLString(String str) {
        StringBuffer buf = new StringBuffer(str.length());
        buf.append("'").append(filterSQLString(str)).append("'");

        return buf.toString();
    }

    // --*-- End String utilities from ExtString.java at util0.4.jar --*--

    /**
     * Fill a string with chars, in order to grow to a given size.
     * The chars are appended by the right.
     * If size is smaller that the actual size of the string, the string is
     * return unchanged
     *
     * @param str  original string
     * @param ch   char to fill with
     * @param size final size of the string
     * @return the string filled
     */
    public static String fill(String str, char ch, int size) {
        return fill(str, "" + ch, size, true);
    }

    /**
     * Fill a string repeating other string, in order to grow to a given size.
     * The chars are appended by the right.
     * If size is smaller that the actual size of the string, the string is
     * return unchanged
     *
     * @param str     original string
     * @param strFill string to fill with
     * @param size    final size of the string
     * @return the string filled
     */
    public static String fill(String str, String strFill, int size) {
        return fill(str, strFill, size, true);
    }

    /**
     * Fill a string repeating other string, in order to grow to a given size.
     * The chars are apended by the right or by the left, according to the
     * param "fillRight".
     * If size is smaller that the actual size of the string, the string is
     * return unchanged
     *
     * @param str       original string
     * @param strFill   string to fill with
     * @param size      final size of the string
     * @param fillRight if true, the chars are appended by the right;
     *                  if false, by the left
     * @return the string filled
     */
    public static String fill(String str, String strFill, int size,
                              boolean fillRight) {

        if (size <= str.length()) {
            return str;
        }

        int sizeGrow = size - str.length();
        int numTimes = sizeGrow / strFill.length();
        // Si no es exacto, hay que aumentar en 1 numTimes
        if ((numTimes * strFill.length()) < sizeGrow) {
            numTimes++;
        }
        String append = repeat(strFill, numTimes).substring(0, sizeGrow);
        if (fillRight) {
            return str + append;
        } else {
            return append + str;
        }
    }

    /**
     * It returns an array of subchains of maximum length in pixels.
     *
     * @param text        original string
     * @param fontMetrics scrren font of text.
     * @param maxWidth    maximum width of subchains in pixels.
     * @return array of subchains of maximum length in pixels.
     */
    public static String[] cutString(String text, FontMetrics fontMetrics,
                                     int maxWidth) {
        Vector strings = new Vector(0, 10);
        text = text.trim();
        while (!text.trim().equals("")) {
            // Search for the position where I must cut the chain.
            int cutPos = text.length();
            while (fontMetrics.stringWidth(text.substring(0, cutPos)) > maxWidth) cutPos--;

            // Search for a space or a separator.
            int sepPos = cutPos - 1;
            boolean foundSep = false;
            if (cutPos != text.length())
                while (sepPos > 0 && !foundSep) switch (text.charAt(sepPos)) {
                    case ' ':
                    case '.':
                    case ',':
                    case ';':
                    case ':':
                    case '?':
                        foundSep = true;
                        break;
                    default:
                        sepPos--;
                }

            // If I did not find then the separator, cut the string in cutPos.
            if (!foundSep)
                sepPos = cutPos;
            else
                sepPos++;

            strings.addElement(text.substring(0, sepPos).trim());
            text = text.substring(sepPos, text.length()).trim();
        }

        String[] result = new String[strings.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = (String) strings.elementAt(i);

        return result;
    }

    /**
     * Soundtrex algorithm.
     * Soundtrex transform a string into another string that it is phonetic and
     * lexical equivalent to the first string. That is, it extracts the accents,
     * it deletes multiple letters, etc. i.e, The soundtrex of 'ayamonte' is
     * AIAMONTE and the soundtrex of 'allamonte' is AIAMONTE too.
     * <br />
     * The application of this algorithm is to compare strings but in the
     * comparation it is considered common syntactic errors, i.e, there aren't
     * differences between 'gente' and 'guente'.
     * It can be used at search engines if the indexes are calculated from the
     * soundtrex string.
     *
     * @param original String to analyze
     * @return Soundtrex of the string parameter
     */
    public static String soundTrex(String original) {
        String result = original.toUpperCase();

        result = replaceAll(result, "CH", "TX");
        result = replaceAll(result, "V", "B");
        result = replaceAll(result, "NYA", "NA");
        result = replaceAll(result, "NYE", "NE");
        result = replaceAll(result, "NYI", "NI");
        result = replaceAll(result, "NYO", "NO");
        result = replaceAll(result, "NYU", "NU");
        result = replaceAll(result, "NB", "MB");
        result = replaceAll(result, "NP", "MP");
        result = replaceAll(result, "Z", "S");
        result = replaceAll(result, "CC", "CS");
        result = replaceAll(result, "H", "");
        result = replaceAll(result, "CI", "SI");
        result = replaceAll(result, "CE", "SE");
        result = replaceAll(result, "C", "K");
        result = replaceAll(result, "QU", "K");
        result = replaceAll(result, "V", "B");
        result = replaceAll(result, "LL", "I");
        result = replaceAll(result, "Y", "I");
        result = replaceAll(result, "J", "X");
        result = replaceAll(result, "GI", "XI");
        result = replaceAll(result, "GE", "XE");
        result = replaceAll(result, "GUI", "GI");
        result = replaceAll(result, "GUE", "GE");
        result = replaceAll(result, "L.L", "L");
        result = replaceAll(result, "�", "N");
        result = replaceAll(result, "�", "S");
        result = replaceAll(result, "RR", "R");
        result = replaceAll(result, "QUI", "KI");
        result = replaceAll(result, "QUE", "KE");
        result = replaceAll(result, "QUO", "KUO");
        result = replaceAll(result, "QUA", "KUA");

        result = replaceAll(result, "ADO", "AO");
        result = replaceAll(result, "ADA", "A");
        result = replaceAll(result, "ADE", "AE");
        result = replaceAll(result, "ADI", "AI");


        result = replaceAll(result, "-", " ");

        result = replaceAll(result, ":", "");


        result = " " + result + " ";


        result = replaceAll(result, "S ", ""); // S a final de palabra
        result = replaceAll(result, "R ", ""); // R a final de palabra
        result = replaceAll(result, "L ", ""); // L a final de palabra


        result = replaceAll(result, " EL ", " "); // Art�culo EL
        result = replaceAll(result, " LA ", " "); // Art�culo LA
        result = replaceAll(result, " LO ", " "); // Art�culo LO
        result = replaceAll(result, " DE ", " "); // DE
        result = replaceAll(result, " AL ", " "); // Contracci�n AL
        result = replaceAll(result, " LE ", " "); // LE

        result = replaceAll(result, " LOS ", " ");
        result = replaceAll(result, " LAS ", " ");
        result = replaceAll(result, " CON ", " ");
        result = replaceAll(result, " DEL ", " ");
        result = replaceAll(result, " VON ", " ");
        result = replaceAll(result, " VAN ", " ");
        result = replaceAll(result, " DER ", " ");

        // Dobles letras
        result = replaceAll(result, "AA", "A");
        result = replaceAll(result, "BB", "B");
        result = replaceAll(result, "CC", "C");
        result = replaceAll(result, "DD", "D");
        result = replaceAll(result, "EE", "E");
        result = replaceAll(result, "FF", "F");
        result = replaceAll(result, "GG", "G");
        result = replaceAll(result, "HH", "H");
        result = replaceAll(result, "II", "I");
        result = replaceAll(result, "JJ", "J");
        result = replaceAll(result, "KK", "K");
        result = replaceAll(result, "LL", "L");
        result = replaceAll(result, "MM", "M");
        result = replaceAll(result, "NN", "N");
        result = replaceAll(result, "��", "�");
        result = replaceAll(result, "OO", "O");
        result = replaceAll(result, "PP", "P");
        result = replaceAll(result, "QQ", "Q");
        result = replaceAll(result, "RR", "R");
        result = replaceAll(result, "SS", "S");
        result = replaceAll(result, "TT", "T");
        result = replaceAll(result, "UU", "U");
        result = replaceAll(result, "VV", "V");
        result = replaceAll(result, "WW", "W");
        result = replaceAll(result, "XX", "X");
        result = replaceAll(result, "YY", "Y");
        result = replaceAll(result, "ZZ", "Z");
        result = replaceAll(result, "��", "�");


        result = result.trim();

        result = replaceAll(result, "  ", " ");

        return result;
    }

    /**
     * Given a character array, this method constructs a new string without
     * trailing carriage return or newline characters.
     *
     * @param charArray the character array to construct string from
     * @return A string without trailing carriage returns or newlines.
     */
    public static String chomp(char[] charArray) {
        // length of String to return
        int length = charArray.length;

        // index of last char in buffer
        int endIndex = charArray.length - 1;

        // From end of char buffer...
        for (int i = endIndex; i > -1; i--) {
            // ...identify carriage return or newline...
            if (charArray[i] == '\r' || charArray[i] == '\n') {
                // ...decrement length of String to return
                length--;
            } else {
                // ...or exit loop
                break;
            }
        }

        return new String(charArray, 0, length);
    }

    /**
     * Get the number of ocurrences of a character into a string
     *
     * @param str string to search into
     * @param chr character to found
     * @return number of ocurrences of chr into str
     */
    public static int ocurrencesOf(String str, char chr) {
        int length = str.length();
        int numOcurrences = 0;
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) == chr) {
                numOcurrences++;
            }
        }
        return numOcurrences;
    }

    /**
     * Converts the first letter to lower case.
     */
    public static String lowerFirstLetter(String str) {
        if (StringUtils.isBlank(str)) return str;

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * Converts the first letter to upper case.
     */
    public static String upperFirstLetter(String str) {
        if (StringUtils.isBlank(str)) return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Converts the given string to a Java valid identifier.
     *
     * @param str string to process
     * @return A java based identifier.
     */
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

        // Avoid reserved java keywords.
        String javaId = buf.toString();
        if (isJavaKeyword(javaId)) {
            if (javaId.equals("class")) javaId = "clazz";
            else javaId = '_' + javaId;
        }
        return javaId;
    }

    /**
     * Checks if the specified string is a valid java identifier.
     *
     * @param str <i>(required)</i>.
     * @return Returns <code>true</code> iff the specified string is a valid java
     *         identifier.
     */
    public static boolean isJavaIdentifier(String str) {
        boolean valid = (str != null) && (str.length() > 0);
        if (valid) {
            char c = str.charAt(0);
            valid = Character.isJavaIdentifierStart(c);
            for (int i = str.length(); valid && (--i >= 1);) {
                valid = Character.isJavaIdentifierPart(c);
            }
        }
        return valid && !isJavaKeyword(str);
    }

    /**
     * Returns true if the given String is a Java keyword which will cause a
     * problem when used as a variable name.
     *
     * @param name the name to check
     * @return true if it is a keyword
     */
    public static final boolean isJavaKeyword(final String name) {
        if (name == null) {
            return false;
        }
        for (String KEYWORD : JAVA_KEYWORDS) {
            if (KEYWORD.equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given package name is valid or not.
     * Empty package names are considered valid!
     *
     * @param name name of package as String with periods
     * @return true if package name is valid
     */
    public static boolean isJavaPackage(final String name) {
        if (StringUtils.isBlank(name)) {
            return true;
        }
        if (".".equals(name)) {
            return false;
        }
        if (name.startsWith(".") || name.endsWith(".")) {
            return false;
        }
        boolean valid = true;
        String[] packageNameParts = name.split("\\.");
        for (int i = 0; i < packageNameParts.length; i++) {
            String packageNamePart = packageNameParts[i];
            valid &= isJavaIdentifier(packageNamePart);
        }
        return valid;
    }

    /**
     * Converts the given Package name to it's corresponding Path.
     * The path will be a relative path.
     *
     * @param packageName the package name to convert
     * @return a String containing the resulting patch
     */
    public static String toJavaPackagePath(final String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return packageName;
        }
        if (!isJavaPackage(packageName)) {
            String message = "Package name: " + packageName + " is not valid";
            throw new IllegalArgumentException(message);
        }
        return packageName.replace('.', File.separatorChar);
    }

    /**
     *  Get a valid java class name for the given string.
     *
     * @return a name which follows Java naming conventions
     */
    public static String toJavaClassName(final String name) {
        if ((name == null) || (name.length() <= 0)) {
            return name;
        }
        int colon = name.indexOf(':');
        if (colon != -1) return upperFirstLetter(toJavaIdentifier(name.substring(colon + 1)));
        return upperFirstLetter(toJavaIdentifier(name));
    }

    /**
     *  Get a valid java filed name for the given string.
     */
    public static String toJavaFieldName(String name) {
        if (name == null) return null;

        return lowerFirstLetter(StringUtil.toJavaIdentifier(name));
    }

    /**
     * Replace special characters with simple characters
     *
     * @param name string to replace
     * @return string with simple characters
     */
    public static String fixUnicodeCharacters(String name) {
        name = name.replaceAll("Á", "A");
        name = name.replaceAll("É", "E");
        name = name.replaceAll("Í", "I");
        name = name.replaceAll("Ó", "O");
        name = name.replaceAll("Ú", "U");
        name = name.replaceAll("á", "a");
        name = name.replaceAll("é", "e");
        name = name.replaceAll("í", "i");
        name = name.replaceAll("ó", "o");
        name = name.replaceAll("ú", "u");
        name = name.replaceAll("ñ", "n");
        name = name.replaceAll("Ñ", "N");
        name = name.replaceAll("Ü", "U");
        name = name.replaceAll("ü", "u");
        name = name.replaceAll("Ò", "O");
        name = name.replaceAll("ò", "o");
        name = name.replaceAll("Ï", "I");
        name = name.replaceAll("À", "A");
        name = name.replaceAll("à", "a");
        name = name.replaceAll("Ç", "C");
        name = name.replaceAll("È", "è");
        name = name.replaceAll("ç", "c");
        name = name.replaceAll("/", "_");
        name = name.replaceAll("\\\\", "_");
        return name;
    }

    /**
     * Convert special characters in unicode characters
     *
     * @param str string to convert
     * @return converted string
     */
    public static String replaceUnicodeCharacters(String str) {
        StringBuffer ostr = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            // Convert if the char need to be converted to unicode
            if ((ch >= 0x0020) && (ch <= 0x007e)){
                ostr.append(ch);
            } else {
                ostr.append("\\u"); // standard unicode format.
                String hex = Integer.toHexString(str.charAt(i) & 0xFFFF); // Get hex value of the char.
                for (int j = 0; j < 4 - hex.length(); j++) // Prepend zeros because unicode requires 4 digits
                    ostr.append("0");
                ostr.append(hex.toLowerCase()); // standard unicode format.
            }
        }

        return ostr.toString(); //Return the stringbuffer cast as a string.
    }

    public static String escapeQuotes(String str) {
        if (str == null) return "";
        str = StringUtils.replace(str, "'", "\\'");
        str = StringUtils.replace(str, "\"", "\\'");
        return str;
    }

    public static void main(String[] args) {

        System.out.println("---- Identifier ----");
        System.out.println(toJavaIdentifier("pepe"));
        System.out.println(toJavaIdentifier("Pepe"));
        System.out.println(toJavaIdentifier("long"));
        System.out.println(toJavaIdentifier("000000long"));
        System.out.println(toJavaIdentifier("000000kk"));

        System.out.println("---- Class ----");
        System.out.println(toJavaClassName("pepe"));
        System.out.println(toJavaClassName("Pepe"));
        System.out.println(toJavaClassName("class"));
        System.out.println(toJavaClassName("long"));
        System.out.println(toJavaClassName("0000long"));
        System.out.println(toJavaClassName("0000kk"));
        System.out.println(toJavaClassName("kXXX"));

        System.out.println("---- Field ----");
        System.out.println(toJavaFieldName("pepe"));
        System.out.println(toJavaFieldName("Pepe"));
        System.out.println(toJavaFieldName("class"));
        System.out.println(toJavaFieldName("Long"));
        System.out.println(toJavaFieldName("0000Long"));
        System.out.println(toJavaFieldName("0000kk"));
    }
}
