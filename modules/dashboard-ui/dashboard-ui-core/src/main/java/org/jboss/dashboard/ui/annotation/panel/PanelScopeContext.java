package org.jboss.dashboard.ui.annotation.panel;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.slf4j.Logger;

public class PanelScopeContext implements Context, Serializable {

    private Logger log = org.slf4j.LoggerFactory.getLogger(getClass().getSimpleName());

    private PanelScopeContextHolder panelScopeContextHolder;

    public PanelScopeContext() {
        log.info("Init");
        this.panelScopeContextHolder = PanelScopeContextHolder.getInstance();
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        if (panelScopeContextHolder.getBeans().containsKey(bean.getBeanClass())) {
            return (T) panelScopeContextHolder.getBean(bean.getBeanClass()).instance;
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        if (panelScopeContextHolder.getBeans().containsKey(bean.getBeanClass())) {
            return (T) panelScopeContextHolder.getBean(bean.getBeanClass()).instance;
        } else {
            T t = (T) bean.create(creationalContext);
            PanelScopeContextHolder.PanelScopeInstance panelInstance = new PanelScopeContextHolder.PanelScopeInstance();
            panelInstance.bean = bean;
            panelInstance.ctx = creationalContext;
            panelInstance.instance = t;
            panelScopeContextHolder.putBean(panelInstance);
            return t;
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return PanelScoped.class;
    }

    public boolean isActive() {
        return true;
    }
}
