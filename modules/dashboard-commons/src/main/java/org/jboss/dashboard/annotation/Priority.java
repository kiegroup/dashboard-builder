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
package org.jboss.dashboard.annotation;

public enum Priority {
    URGENT,
    HIGH,
    NORMAL,
    LOW;

    int getWeight() {
        if (this.equals(URGENT)) return 10;
        if (this.equals(HIGH)) return 7;
        if (this.equals(NORMAL)) return 5;
        if (this.equals(LOW)) return 3;
        return 5;
    }
}
