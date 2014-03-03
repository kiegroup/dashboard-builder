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
package org.jboss.dashboard.displayer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.dashboard.annotation.Install;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DataDisplayerManagerImpl implements DataDisplayerManager {

    protected DataDisplayerType[] displayerTypeArray;
    protected DataDisplayerRenderer[] displayerRendererArray;

    @Inject @Install
    protected Instance<DataDisplayerType> dataDisplayerTypes;

    @Inject @Install
    protected Instance<DataDisplayerRenderer> dataDisplayerRenderers;

    @PostConstruct
    protected void init() {
        List<DataDisplayerType> _types = new ArrayList<DataDisplayerType>();
        for (DataDisplayerType type: dataDisplayerTypes) {
            if (!type.getSupportedRenderers().isEmpty()) {
                _types.add(type);
            }
        }
        displayerTypeArray = new DataDisplayerType[_types.size()];
        for (int i=0;i<_types.size();i++) displayerTypeArray[i] = _types.get(i);

        List<DataDisplayerRenderer> rendList = new ArrayList<DataDisplayerRenderer>();
        for (DataDisplayerRenderer rend : dataDisplayerRenderers) rendList.add(rend);
        displayerRendererArray = rendList.toArray(new DataDisplayerRenderer[0]);
    }

    public DataDisplayerType[] getDataDisplayerTypes() {
        return displayerTypeArray;
    }

    public DataDisplayerRenderer[] getDataDisplayerRenderers() {
        return displayerRendererArray;
    }

    public DataDisplayerType getDisplayerTypeByUid(String uid) {
        if (StringUtils.isBlank(uid)) return null;
        for (DataDisplayerType type : displayerTypeArray) {
            if (type.getUid().equals(uid)) return type;
        }
        return null;
    }

    public DataDisplayerRenderer getDisplayerRendererByUid(String uid) {
        if (StringUtils.isBlank(uid)) return null;
        for (DataDisplayerRenderer rend : displayerRendererArray) {
            if (rend.getUid().equals(uid)) return rend;
        }
        return null;
    }
}
