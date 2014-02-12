package org.jboss.dashboard.ui.annotation.panel;

import java.io.Serializable;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class PanelScopeExtension implements Extension, Serializable {

    public void registerContext(@Observes final AfterBeanDiscovery event) {
        event.addContext(new PanelScopeContext());
    }
}