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
package org.jboss.dashboard.commons.xml;

public class XMLUtils {

    /*
    * Some XML generation methods.
    */

    public static String header() {
        return header("ISO-8859-1");
    }

    public static String header(String encoding) {
        return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n";
    }

    public static void tag(StringBuffer buf, String tag, String attr, String value) {
        buf.append("<" + tag);
        if (attr != null) {
            buf.append(" " + attr + "=" + "\"" + value + "\"");
        }
        buf.append(">\n");
    }

    public static void tag(StringBuffer buf, String tag, String content) {
        buf.append("<" + tag + ">");
        if (content != null) {
            XMLUtils.content(buf, content);
        }
        buf.append("</" + tag + ">\n");
    }

    public static void content(StringBuffer buf, String content) {
        if (content != null) {
            buf.append("<![CDATA[" + content + "]]>");
        }
    }

    public static void endtag(StringBuffer buf, String tag) {
        buf.append("</" + tag + ">\n");
    }


    private static String spaces = null;

    static {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 100; i++)
            buf.append(" ");

        spaces = buf.toString();
    }

    public static String spaces(int n) {
        return spaces.substring(0, n);
    }
}
