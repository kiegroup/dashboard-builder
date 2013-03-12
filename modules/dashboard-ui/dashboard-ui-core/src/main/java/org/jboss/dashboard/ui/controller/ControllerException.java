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
package org.jboss.dashboard.ui.controller;

/**
 * Exception thrown by controller classes.
 */
public class ControllerException extends Exception {
    /**
     * ControllerException constructor comment.
     */
    public ControllerException() {
        super();
    }

    /**
     * ControllerException constructor comment.
     *
     * @param s java.lang.String
     */
    public ControllerException(String s) {
        super(s);
    }
}