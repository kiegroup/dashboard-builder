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
import org.jboss.dashboard.ui.controller.responses.ShowJspInsidePanelContextResponse;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.Resource;
import org.jboss.dashboard.ui.resources.ResourceName;
import org.jboss.dashboard.ui.resources.ResourceGallery;

import java.io.File;

public class ImageUploadHandler extends HandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ImageUploadHandler.class.getName());

    private String name;
    private File file;
    private boolean operationSuccess;
    private String lastResourceUrl;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommandResponse actionUploadImage(CommandRequest request) throws Exception {
        if (name == null)
            addFieldError(new FactoryURL(getComponentName(), "name"), null, name);
        if (file == null)
            addFieldError(new FactoryURL(getComponentName(), "file"), null, file);

        GraphicElement galleryToAddTo = UIServices.lookup().getResourceGalleryManager().getDefaultElement();

        ResourceName resName = galleryToAddTo.getResourceName(name);
        Resource resource = galleryToAddTo.getResource(resName, null);

        if (resource != null)
            addFieldError(new FactoryURL(getComponentName(), "name"), null, file);

        if (this.getFieldErrors().isEmpty()) {
            UIServices.lookup().getResourceGalleryManager().addFileToResource(name, file, galleryToAddTo, ResourceGallery.DESCRIPTOR_FILENAME);
            setName(null);
            operationSuccess = true;
            resource = galleryToAddTo.getResource(resName, null);
            lastResourceUrl = resource.getResourceUrl(request.getRequestObject(), request.getResponseObject(), true);
            //Make sure it is a relative url like Controller?mvchandler=...
            if (lastResourceUrl.startsWith(request.getRequestObject().getContextPath())) {
                lastResourceUrl = lastResourceUrl.substring((request.getRequestObject().getContextPath()).length());
            }
            while (lastResourceUrl.startsWith("/")) lastResourceUrl = lastResourceUrl.substring(1);
        }
        return new ShowJspInsidePanelContextResponse("/fckeditor/custom/FCKSelectImage.jsp");
    }

    public boolean isNameError() {
        return hasError("name");
    }

    public boolean isFileError() {
        return hasError("file");
    }

    public boolean isOperationSuccess() {
        boolean b = operationSuccess;
        operationSuccess = false;
        return b;
    }

    public String getLastResourceUrl() {
        return lastResourceUrl;
    }
}
