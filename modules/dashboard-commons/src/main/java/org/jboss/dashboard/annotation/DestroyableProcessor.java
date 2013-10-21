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

/**
 * Process application shutdown.
 *
 * NOTE: BZ-1014612
 */
public class DestroyableProcessor {

    private static transient Logger log = LoggerFactory.getLogger(DestroyableProcessor.class.getName());

    @Inject
    protected Instance<Destroyable> destroyables;

    private final DestroyableComparator destroyableComparator = new DestroyableComparator();
    
    public void destroyBeans() {
        // Sort beans by priority
        List<Destroyable> destroyableList = new ArrayList<Destroyable>();
        for (Destroyable destroyable : destroyables) destroyableList.add(destroyable);
        Collections.sort(destroyableList, destroyableComparator);

        // Start the beans
        for (Destroyable destroyable : destroyableList) {
            try {
                log.debug("Destroying " + destroyable.getPriority() + " priority bean " + destroyable.getClass().getName());
                destroyable.destroy();
            } catch (Exception e) {
                log.error("Error destroying bean " + destroyable.getClass().getName(), e);
            }
        }
    }

    private class DestroyableComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            try {
                Destroyable s1 = (Destroyable) o1;
                Destroyable s2 = (Destroyable) o2;
                return ComparatorUtils.compare(s1.getPriority().getWeight(), s2.getPriority().getWeight(), -1);
            } catch (ClassCastException e) {
                return 0;
            }
        }
    }
}
