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
public class MessageList extends ArrayList {

    private Date created = new Date();

    public MessageList() {
        super();
    }

    public MessageList(Collection c) {
        super(c);
    }

    public Date getCreationDate() {
        return this.created;
    }

    public boolean addAll(Collection c, boolean discardDuplicates) {
        if (c == null || c.isEmpty()) return false;
        if (!discardDuplicates) return super.addAll(c);

        int initialSize = size();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!contains(o)) add(o);
        }
        return initialSize != size();
    }

    /**
     * Retrieves from the list of messages those elements that produces the specified message code.
     * @param messageCode The message code.
     * @return A list of Message instances.
     */
    public List getMessagesWithCode(String messageCode) {
        List results = new ArrayList();
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            if (message.getMessageCode().equals(messageCode)) results.add(message);
        }
        return results;
    }

    /**
     * Retrieves from the list of messages those elements that produces the specified message code.
     * @param element The process element.
     * @return A list of Message instances.
     */
    public List getMessagesForElement(Object element) {
        List results = new ArrayList();
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            Object[] elements = message.getElements();
            if (elements != null) {
                for (int i = 0; i < elements.length; i++) {
                    if (element.equals(elements[i])) {
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
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            if (message.getMessageType() == type) return true;
        }
        return false;
    }

    public void removeMessagesOfType(int type) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            if (message.getMessageType() == type) it.remove();
        }
    }

    public void keepOnlyMessagesOfType(int type) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            if (message.getMessageType() != type) it.remove();
        }
    }

    public List getMessagesOfType(int type) {
        List l = new ArrayList();
        Iterator it = iterator();
        while (it.hasNext()) {
            Message message = (Message) it.next();
            if (message.getMessageType() == type) {
                l.add(message);
            }
        }
        return l;
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
                    messages[i] = ((Message)l.get(i)).getMessage(locale);
                }
            }
        }
        return messages;
    }
}
