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
package org.jboss.dashboard.displayer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public abstract class AbstractDataDisplayerType implements DataDisplayerType {

    protected List<DataDisplayerRenderer> displayerRenderers;

    public List<DataDisplayerRenderer> getSupportedRenderers() {
        return displayerRenderers;
    }

    public DataDisplayerRenderer getDefaultRenderer() {
        if (CollectionUtils.isEmpty(displayerRenderers)) {
            throw new RuntimeException("No default renderer found for " + getUid() + " displayers");
        }
        return displayerRenderers.get(0);
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getUid()).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (getUid() == null) return false;

            AbstractDataDisplayerType other = (AbstractDataDisplayerType) obj;
            return getUid().equals(other.getUid());
        }
        catch (ClassCastException e) {
            return false;
        }
    }
}
