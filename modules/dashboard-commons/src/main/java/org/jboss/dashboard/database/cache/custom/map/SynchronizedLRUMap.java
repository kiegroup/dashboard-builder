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
package org.jboss.dashboard.database.cache.custom.map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMapIterator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * Synchronizes ALL public methods, not only those defined by Map interface
 */

public class SynchronizedLRUMap extends LRUMap {
    public SynchronizedLRUMap(int size) {
        super(size);
    }

    public synchronized Object get(Object key) {
        return super.get(key);
    }

    public synchronized boolean isFull() {
        return super.isFull();
    }

    public synchronized int maxSize() {
        return super.maxSize();
    }

    public synchronized boolean isScanUntilRemovable() {
        return super.isScanUntilRemovable();
    }

    public synchronized Object clone() {
        return super.clone();
    }


    public synchronized String toString() {
        return super.toString();
    }


    public synchronized int hashCode() {
        return super.hashCode();
    }


    public synchronized boolean equals(Object obj) {
        return super.equals(obj);
    }


    public synchronized Collection values() {
        return super.values();
    }


    public synchronized Set keySet() {
        return super.keySet();
    }


    public synchronized Set entrySet() {
        return super.entrySet();
    }

    public synchronized Object remove(Object key) {
        return super.remove(key);
    }


    public synchronized void putAll(Map map) {
        super.putAll(map);
    }


    public synchronized Object put(Object key, Object value) {
        return super.put(key, value);
    }


    public synchronized boolean containsKey(Object key) {
        return super.containsKey(key);
    }


    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }


    public synchronized int size() {
        return super.size();
    }


    public synchronized OrderedMapIterator orderedMapIterator() {
        return super.orderedMapIterator();
    }


    public synchronized MapIterator mapIterator() {
        return super.mapIterator();
    }

    public synchronized Object previousKey(Object key) {
        return super.previousKey(key);
    }


    public synchronized Object nextKey(Object key) {
        return super.nextKey(key);
    }


    public synchronized Object lastKey() {
        return super.lastKey();
    }


    public synchronized Object firstKey() {
        return super.firstKey();
    }


    public synchronized void clear() {
        super.clear();
    }

    public synchronized boolean containsValue(Object value) {
        return super.containsValue(value);
    }

}
