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
package org.jboss.dashboard.command;

import org.jboss.dashboard.DataProviderServices;

import javax.enterprise.inject.Instance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default command processor implementation.
 */
public class CommandProcessorImpl implements CommandProcessor {

    /** List of commands executed successfully. */
    protected List<Command> successfulCommands;

    /** List of commands failed. */
    protected List<Command> failedCommands;

    /** Flag that enables/disabled the command execution. */
    protected boolean commandExecutionEnabled;

    public CommandProcessorImpl() {
        successfulCommands = new ArrayList<Command>();
        failedCommands = new ArrayList<Command>();
        commandExecutionEnabled = true;
    }

    public void setCommandExecutionEnabled(boolean commandExecutionEnabled) {
        this.commandExecutionEnabled = commandExecutionEnabled;
    }

    public boolean isCommandExecutionEnabled() {
        return commandExecutionEnabled;
    }

    public List<Command> getSuccessfulCommands() {
        return successfulCommands;
    }

    public List<Command> getFailedCommands() {
        return failedCommands;
    }

    protected Command createCommand(String commandName) {
        Instance<CommandFactory> commandFactories = DataProviderServices.lookup().getCommandFactories();
        for (CommandFactory commandFactory : commandFactories) {
            Command command = commandFactory.createCommand(commandName);
            if (command != null) return command;
        }
        return null;
    }

    public String processCommand(String[] expression) throws Exception {
        String commandName = expression[0];
        Command command = buildCommand(expression);
        if (command == null) return "[" + commandName + ", command not supported]";
        return processCommand(command);
    }

    public Command buildCommand(String[] expression) throws Exception {
        String commandName = expression[0];
        Command command = createCommand(commandName);
        if (command == null) return null;

        List args = new ArrayList();
        args.addAll(Arrays.asList(expression));
        args.remove(0);
        command.setName(commandName);
        command.setArguments(args);
        return command;
    }

    public String processCommand(Command command) throws Exception {
        try {
            String commandResult = null;
            if (commandExecutionEnabled) {
                commandResult = command.execute();
            }
            successfulCommands.add(command);
            return commandResult;
        } catch (Exception e) {
            failedCommands.add(command);
            throw e;
        }
    }
}
