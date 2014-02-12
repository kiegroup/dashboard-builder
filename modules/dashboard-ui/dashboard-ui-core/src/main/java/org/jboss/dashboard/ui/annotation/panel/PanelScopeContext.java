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
package org.jboss.dashboard.ui.annotation.panel;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

public class PanelScopeContext implements Context, Serializable {

    private PanelScopeContextHolder panelScopeContextHolder;

    public PanelScopeContext() {
        this.panelScopeContextHolder = PanelScopeContextHolder.getInstance();
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        Bean<T> bean = (Bean<T>) contextual;
        return (T) panelScopeContextHolder.getBeanInstance(bean);
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;
        T obj = panelScopeContextHolder.getBeanInstance(bean);
        if (obj != null) return obj;

        T t = (T) bean.create(creationalContext);
        panelScopeContextHolder.registerBeanInstance(bean, creationalContext, t);
        return t;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return PanelScoped.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
