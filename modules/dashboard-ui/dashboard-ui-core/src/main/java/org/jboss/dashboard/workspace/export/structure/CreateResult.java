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
package org.jboss.dashboard.workspace.export.structure;

/**
 * Implementation of a Workspace creation result, used on Import process.
 */
public class CreateResult extends ImportExportResult {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreateResult.class.getName());

    private Object objectCreated;

    public CreateResult() {
    }

    public CreateResult(Exception exception) {
        super(exception);
    }

    public Object getObjectCreated() {
        return objectCreated;
    }

    public void setObjectCreated(Object objectCreated) {
        this.objectCreated = objectCreated;
    }

    public void setException(Exception e) {
        super.setException(e);
    }
}
