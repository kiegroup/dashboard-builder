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
package org.jboss.dashboard.pojo;

import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StartableBean2 implements Startable {

    @Inject @Config("false")
    protected boolean isStarted;

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public void start() {
        isStarted = true;
    }

    public boolean isStarted() {
        return isStarted;
    }
}
