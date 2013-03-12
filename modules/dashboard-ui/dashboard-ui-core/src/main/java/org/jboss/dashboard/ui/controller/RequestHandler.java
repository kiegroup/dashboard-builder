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
package org.jboss.dashboard.ui.controller;

/**
 * Each request handler (usually a group of related commands) must implement this interface.
 * Typically a common base class will provide a implementation for all of this methods,
 * just letting the subclasses to provide the implementation of the action methods,
 * overriding  some of the following methods only if necessary.
 * <p/>
 */
public interface RequestHandler {

    /**
     * Identifier for show screen action
     */
    public static final String ACTION_SHOW_SCREEN = "show-screen";


    /**
     * This method is called by the controller servlet when a screen display is requested
     *
     * @param req CommandRequest request object just processed.
     * @return CommandResponse object, Response to be generated.
     */
    CommandResponse actionShowScreen(CommandRequest req) throws Exception;


    /**
     * This method is called after invoking the actionXXXX method, which returned a
     * CommandAction object (or null).
     * <p/>
     * This is intended to be a Command-group level filter for all the actions generated, or
     * perform a process after the command execution.
     *
     * @param req    CommandRequest request object just processed.
     * @param action CommandResponse action action returned by command.
     * @return CommandResponse object, usually the same as the input one,
     *         or null if the action execution must be cancelled.
     */
    CommandResponse afterInvokeAction(CommandRequest req, CommandResponse action);


    /**
     * This method is called before invoking the actionXXXX method.
     *
     * @param req CommandRequest request object just processed.
     * @return the CommandResponse to execute, or null to continue action execution
     */
    CommandResponse beforeInvokeAction(CommandRequest req);

    /**
     * This method is called to execute the command requested.
     *
     * @param req CommandRequest request to be executed.
     * @return CommandResponse object, Response to be generated.
     */
    CommandResponse execute(CommandRequest req) throws Exception;

    /**
     * Returns true if current action must handle the show screen command
     *
     * @param req
     * @return boolean
     */
    boolean isShowScreenAction(CommandRequest req);

}