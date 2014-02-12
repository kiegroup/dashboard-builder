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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.responses.SendErrorResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.commons.text.Base64;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Resource;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Session;
import org.slf4j.Logger;

@ApplicationScoped
@Named("resources_handler")
public class ResourcesHandler extends BeanHandler {

    @Inject
    private transient Logger log;

    @PostConstruct
    public void start() throws Exception {
        super.start();
        setUseActionShortcuts(false);
    }

    public CommandResponse actionRetrieve(CommandRequest request) throws Exception {
        String name = request.getParameter("resName");
        log.debug("Retrieving resource " + name);
        Resource res = UIServices.lookup().getResourceManager().getResource(name);
        if (res != null)
            return res.getResourceAsResponse();
        log.warn("Not found resource " + name + " (" + new String(Base64.decode(name)) + ") ");
        return new SendErrorResponse(404);
    }

    public CommandResponse actionDownload(CommandRequest request) throws Exception {
        final String dbid = request.getParameter("dbid");
        final GraphicElement[] element = new GraphicElement[]{null};
        if (dbid != null) {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    element[0] = (GraphicElement) session.get(GraphicElement.class, Long.decode(dbid));
                }
            }.execute();
            if (element[0] != null) {
                return new SendStreamResponse(new ByteArrayInputStream(element[0].getZipFile()), "inline; filename=" + URLEncoder.encode(element[0].getId()) + ".zip;");
            }
        }
        return null;
    }
}
