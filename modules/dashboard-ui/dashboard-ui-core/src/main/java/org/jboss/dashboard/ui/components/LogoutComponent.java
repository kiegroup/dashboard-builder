package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.users.UserStatus;

/**
 * Created by IntelliJ IDEA.
 * User: jschatte
 * Date: 4/03/13
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class LogoutComponent extends UIComponentHandlerFactoryElement {

    private String componentIncludeJSP;

    public void actionLogout(CommandRequest request) {
        UserStatus.lookup().closeSession();
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    @Override
    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }
}
