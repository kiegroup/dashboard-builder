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
package org.jboss.dashboard.command;

import java.util.List;

/**
 * A command processor is an interface used by the TemplateProcessor service.
 * Its main responsability is to transform command fragments coming from a text template.
 */
public interface CommandProcessor {

    void setCommandExecutionEnabled(boolean commandExecutionEnabled);
    boolean isCommandExecutionEnabled();
    List<Command> getSuccessfulCommands();
    List<Command> getFailedCommands();
    String processCommand(String[] command) throws Exception;
}
