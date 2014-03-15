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
package org.jboss.dashboard.commons.message;

import org.jboss.dashboard.LocaleManager;

import java.util.*;

/**
 * A list of messages.
 */
public class MessageList extends ArrayList<Message> {

    private Date created = new Date();

    public MessageList() {
        super();
    }

    public MessageList(Collection<Message> c) {
        super(c);
    }

    public Date getCreationDate() {
        return this.created;
    }

    public boolean addAll(Collection<Message> c, boolean discardDuplicates) {
        if (c == null || c.isEmpty()) return false;
        if (!discardDuplicates) return super.addAll(c);

        int initialSize = size();
        for (Message m : c) {
            if (!contains(m)) add(m);
        }
        return initialSize != size();
    }

    /**
     * Retrieves from the list of messages those elements that produces the specified message code.
     * @param messageCode The message code.
     * @return A list of Message instances.
     */
    public List<Message> getMessagesWithCode(String messageCode) {
        List<Message> results = new ArrayList<Message>();
        for (Message message : this) {
            if (message.getMessageCode().equals(messageCode)) results.add(message);
        }
        return results;
    }

    /**
     * Retrieves from the list of messages those elements that produces the specified message code.
     * @param element The process element.
     * @return A list of Message instances.
     */
    public List<Message> getMessagesForElement(Object element) {
        List<Message> results = new ArrayList<Message>();
        for (Message message : this) {
            Object[] elements = message.getElements();
            if (elements != null) {
                for (Object e : elements) {
                    if (element.equals(e)) {
                        results.add(message);
                        break;
                    }
                }
            }
        }
        return results;
    }

    public boolean hasErrors() {
        return containsMessagesOfType(Message.ERROR);
    }

    public boolean containsMessagesOfType(int type) {
        Iterator<Message> it = iterator();
        while (it.hasNext()) {
            Message message = it.next();
            if (message.getMessageType() == type) return true;
        }
        return false;
    }

    public void removeMessagesOfType(int type) {
        Iterator<Message> it = iterator();
        while (it.hasNext()) {
            Message message = it.next();
            if (message.getMessageType() == type) it.remove();
        }
    }

    public void keepOnlyMessagesOfType(int type) {
        Iterator<Message> it = iterator();
        while (it.hasNext()) {
            Message message = it.next();
            if (message.getMessageType() != type) it.remove();
        }
    }

    public List<Message> getMessagesOfType(int type) {
        List<Message> result = new ArrayList<Message>();
        for (Message message : this) {
            if (message.getMessageType() == type) {
                result.add(message);
            }
        }
        return result;
    }

    /**
     * Method that looks up the messages of the specified type for the specified element, and returns them in a String[].
     * If the element is null, or no messages were found, null is returned
     */
    public String[] getMessagesOfTypeForElement(Object element, int type) {
        String[] messages = null;
        // Go find messages for element with specified type
        if (element != null) {
            MessageList l = new MessageList(this.getMessagesForElement(element));
            l.keepOnlyMessagesOfType(type);
            // Convert messages to String[]
            if (!l.isEmpty()) {
                Locale locale = LocaleManager.currentLocale();
                messages = new String[l.size()];
                for (int i = 0; i < l.size(); i++) {
                    messages[i] = l.get(i).getMessage(locale);
                }
            }
        }
        return messages;
    }
}
