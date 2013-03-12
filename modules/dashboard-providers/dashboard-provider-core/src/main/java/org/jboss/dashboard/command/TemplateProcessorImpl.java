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

import org.jboss.dashboard.annotation.config.Config;
import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class TemplateProcessorImpl implements TemplateProcessor {

    // Logger
    protected static transient Log log = LogFactory.getLog(TemplateProcessorImpl.class);

    @Inject @Config("{")
    protected String commandStartTag;

    @Inject @Config("}")
    protected String commandEndTag;

    public String getCommandStartTag() {
        return commandStartTag;
    }

    public void setCommandStartTag(String commandStartTag) {
        this.commandStartTag = commandStartTag;
    }

    public String getCommandEndTag() {
        return commandEndTag;
    }

    public void setCommandEndTag(String commandEndTag) {
        this.commandEndTag = commandEndTag;
    }

    /**
     * Process the given template using the specified command processor.
     * @return The template after applying the command processor to all the commands found within the template.
     */
    public String processTemplate(String template, CommandProcessor commandProcessor) throws Exception {
        if (template == null || template.trim().length() == 0) return template;

        StringBuffer result = new StringBuffer();
        int begin = template.indexOf(commandStartTag);
        if (begin == -1) return template;
        
        int end = template.indexOf(commandEndTag);
        result.append(template.substring(0, begin));

        while (begin != -1 && end != -1) {
            
            // Get expression.
            String match = template.substring(begin + commandStartTag.length(), end);
            String[] expression = StringUtils.split(match, ',');
            for (int i=0; i<expression.length; i++) expression[i] = expression[i].trim();

            // Process expression.
            if (expression.length > 0) {
                String exprResult = commandProcessor.processCommand(expression);
                if (exprResult != null && exprResult.trim().length() > 0) result.append(exprResult);
            }

            // Get the next command.
            begin = template.indexOf(commandStartTag, end);
            if (begin != -1) result.append(template.substring(end + commandEndTag.length(), begin));
            else result.append(template.substring(end + commandEndTag.length()));
            end = template.indexOf(commandEndTag, begin);
        }
        return result.toString();
    }

    public List<Command> getCommands(String template) {
        try {
            final List<Command> results = new ArrayList<Command>();
            processTemplate(template, new CommandProcessorImpl() {
                public String processCommand(Command command) throws Exception {
                    results.add(command);
                    return null;
                }
            });
            return results;
        } catch (Exception e) {
            log.error(e);
            return Collections.EMPTY_LIST;
        }
    }

    public boolean containsCommandArg(String template, final String arg, final int pos) {
        try {
            final Boolean[] result = new Boolean[] {Boolean.FALSE};
            processTemplate(template, new CommandProcessor() {
                public String processCommand(String[] command) {
                    if (command.length > pos && command[pos].equals(arg)) {
                        result[0] = Boolean.TRUE;
                    }
                    return null;
                }
            });
            return result[0].booleanValue();
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }
}
