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
package org.jboss.dashboard.factory;

/**
 * Raised by the Factory when a lookup problem occurs within a lookup call.
 */
public class LookupException extends Exception {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LookupException.class.getName());

    

    public LookupException(String message) {
        super(message);
    }
}
