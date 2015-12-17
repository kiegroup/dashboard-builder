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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

public class ByteArrayResource extends Resource {

    private ResourceName resName;
    protected byte[] array;
    protected String name;

    public static ByteArrayResource getInstance(ResourceName resName, byte[] array, String name) {
        if (resName == null || array == null || name == null)
            throw new NullPointerException();
        return new ByteArrayResource(resName, array, name);
    }


    private ByteArrayResource(ResourceName resName, byte[] array, String name) {
        this.resName = resName;
        this.array = array;
        this.name = name;
    }

    public CommandResponse getResourceAsResponse() {
        SendStreamResponse response = new SendStreamResponse(new ByteArrayInputStream(array), "inline; filename=" + name + ";");
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(name);
        if (contentType != null) response.setContentType(contentType);
        return response;
    }

    public InputStream getResourceAsStream() {
        return new ByteArrayInputStream(array);
    }


    public String getResourceName() {
        return resName.toString();
    }

    public String getResourceFileName() {
        return name;
    }

    /**
     * The resource portable name (without workspace info)
     *
     * @return The resource portable name (without workspace info)
     */
    public String getPortableResourceName() {
        return resName.getPortableResourceName();
    }
}
