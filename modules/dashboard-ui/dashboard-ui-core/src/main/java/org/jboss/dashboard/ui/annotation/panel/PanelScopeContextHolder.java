package org.jboss.dashboard.ui.annotation.panel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

public class PanelScopeContextHolder implements Serializable {

    private static PanelScopeContextHolder INSTANCE;
    private Map<Class, PanelScopeInstance> beans; // we will have only one instance of a type so the key is a class

    private PanelScopeContextHolder() {
        beans = Collections.synchronizedMap(new HashMap<Class, PanelScopeInstance>());
    }

    public synchronized static PanelScopeContextHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PanelScopeContextHolder();
        }
        return INSTANCE;
    }

    public Map<Class, PanelScopeInstance> getBeans() {
        return beans;
    }

    public PanelScopeInstance getBean(Class type) {
        return getBeans().get(type);
    }

    public void putBean(PanelScopeInstance panelInstance) {
        getBeans().put(panelInstance.bean.getBeanClass(), panelInstance);
    }

    void destroyBean(PanelScopeInstance panelScopeInstance) {
        getBeans().remove(panelScopeInstance.bean.getBeanClass());
        panelScopeInstance.bean.destroy(panelScopeInstance.instance, panelScopeInstance.ctx);
    }

    /**
     * Wrap necessary properties so we can destroy the bean later:
     */
    public static class PanelScopeInstance<T> {

        Bean<T> bean;
        CreationalContext<T> ctx;
        T instance;
    }
}
