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

/**
 * Allows formatting a String to a name acomplishing java names conventions.
 */
public class JavaNamesFormatter {

    /**
     * log
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavaNamesFormatter.class.getName());

    /**
     * JavaFormatter constructor comment.
     */
    private JavaNamesFormatter() {
        super();
    }


    /**
     * Description of the Method
     *
     * @param name                   Description of the Parameter
     * @param firstLetterIsUpperCase Description of the Parameter
     * @return Description of the Return Value
     */
    public static String toJavaName(String name, boolean firstLetterIsUpperCase) {

        StringBuffer res = new StringBuffer();

        boolean nextIsUpperCase = firstLetterIsUpperCase;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (nextIsUpperCase) {
                c = Character.toUpperCase(c);
            }

            if (Character.isLetter(c)) {
                res.append(c);
                nextIsUpperCase = false;
            } else {
                nextIsUpperCase = true;
            }
        }

        return res.toString();
    }

    /**
     * Utility function to get the name of the setter
     * corresponding a given property.
     *
     * @param propertyName The name of the property, e.g., "name".
     * @return The name of the method that sets that
     *         property, for instance, "setName".
     */
    public static String setterName(String propertyName) {
        return "set" + StringUtil.firstUp(propertyName);
    }

    /**
     * Utility function to get the name of the getter
     * corresponding a given property.
     *
     * @param propertyName The name of the property, e.g., "name".
     * @return The name of the method that gets that
     *         property, for instance, "getTitle".
     */
    public static String getterName(String propertyName) {
        return "get" + StringUtil.firstUp(propertyName);
    }
}
