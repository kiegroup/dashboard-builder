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

import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StartableProcessor {

    private static transient Logger log = LoggerFactory.getLogger(StartableProcessor.class.getName());

    @Inject
    protected Instance<Startable> startables;

    private static final Comparator<Startable> STARTABLE_COMPARATOR = new Comparator<Startable>() {
        public int compare(Startable s1, Startable s2) {
            return ComparatorUtils.compare(s1.getPriority().getWeight(), s2.getPriority().getWeight(), -1);
        }
    };

    public void wakeUpStartableBeans() throws Exception {
        // Sort beans by priority from most urgent to least urgent
        List<Startable> startableList = new ArrayList<Startable>();
        for (Startable startable : startables) startableList.add(startable);
        Collections.sort(startableList, STARTABLE_COMPARATOR);

        // Start the beans
        for (Startable startable : startableList) {
            try {
                log.debug("Starting " + startable.getPriority() + " priority bean " + startable.getClass().getName());
                startable.start();
            } catch (Exception e) {
                log.error("Error starting bean " + startable.getClass().getName(), e);
            }
        }
    }
}
