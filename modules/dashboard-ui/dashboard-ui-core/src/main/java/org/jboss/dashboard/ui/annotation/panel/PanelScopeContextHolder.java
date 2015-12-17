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
package org.jboss.dashboard.ui.annotation.panel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PanelScopeContextHolder implements Serializable {

    private static Logger log = LoggerFactory.getLogger(PanelScopeContextHolder.class);
    private static String ATTR_PREFFIX = "_cdi_";
    private static PanelScopeContextHolder INSTANCE;

    private ThreadLocal backup = new ThreadLocal();

    public synchronized static PanelScopeContextHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PanelScopeContextHolder();
        }
        return INSTANCE;
    }

    public String getBeanKey(Bean bean) {
        if (!StringUtils.isBlank(bean.getName())) return ATTR_PREFFIX + bean.getName();
        return ATTR_PREFFIX + bean.getBeanClass().getName();
    }

    public <T> T getBeanInstance(Bean<T> bean) {
        String key = getBeanKey(bean);
        HttpSession session = getSession();
        if (session != null) {
            BeanHolder<T> beanHolder = (BeanHolder<T>) session.getAttribute(key);
            return beanHolder == null ? null : beanHolder.instance;
        } else {
            Map m = (Map) backup.get();
            if (m == null) backup.set(m = new HashMap());
            BeanHolder<T> beanHolder = (BeanHolder<T>) m.get(key);
            return beanHolder == null ? null : beanHolder.instance;
        }
    }

    public <T> T registerBeanInstance(Bean<T> bean, CreationalContext<T> ctx, T obj) {
        BeanHolder beanHolder = new BeanHolder();
        beanHolder.bean = bean;
        beanHolder.ctx = ctx;
        beanHolder.instance = obj;

        String key = getBeanKey(bean);
        HttpSession session = getSession();
        if (session != null) {
            session.setAttribute(key, beanHolder);
        } else {
            Map m = (Map) backup.get();
            if (m == null) backup.set(m = new HashMap());
            m.put(key, beanHolder);
        }
        return obj;
    }

    public <T> void destroyBean(HttpSession session, String name) {
        if (session != null && isPanelScopedBean(name)) {
            BeanHolder<T> beanHolder = (BeanHolder<T>) session.getAttribute(name);
            if (beanHolder != null) {
                session.removeAttribute(name);
                beanHolder.bean.destroy(beanHolder.instance, beanHolder.ctx);
            }
        }
    }

    public boolean isPanelScopedBean(String name) {
        return name != null && name.startsWith(ATTR_PREFFIX);
    }

    public void clear() {
        Map m = (Map) backup.get();
        if (m != null) {
            m.clear();
        }
    }

    protected HttpSession getSession() {
        RequestContext reqCtx = RequestContext.lookup();
        if (reqCtx != null) {
            CommandRequest request = reqCtx.getRequest();
            if (request != null) {
                Panel currentPanel = RequestContext.lookup().getActivePanel();
                if (currentPanel != null) {
                    return currentPanel.getPanelSession();
                } else {
                    if (log.isDebugEnabled()) log.debug("Using a PanelScoped bean outside a panel. Will default to SessionScoped.");
                    return request.getSessionObject();
                }
            }
        }
        return null;
    }

    /**
     * Wrap necessary properties so we can destroy the bean later:
     */
    public static class BeanHolder<T> {

        Bean<T> bean;
        CreationalContext<T> ctx;
        T instance;
    }
}
