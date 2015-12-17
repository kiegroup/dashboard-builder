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
package org.jboss.dashboard.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * List designed to manage large amount of instances.
 * <p>Internally, it only stores identifiers (as a list passed at initialization).
 * Instances are only retrieved when invoking the <i>get(int index)</i>
 * method of the list. This load-on-demand technique is also known as lazy materialization.
 * <p>Instances can be added or removed from list via class methods.
 * You can both add the instance to the list or instance identifier.
 * If you need to retrieve the list of identifiers stored internally use the <i>getIds</i>
 * method which returns an array of strings.
 */
public abstract class LazyList extends ArrayList {

    /**
     * List of instances already retrieved by the lazy calls.
     * It acts as an instance cache and an instance prefetch buffer.
     */
    protected List instanceList;

    /**
     * Flag indicating if duplicated elements are allowed.
     */
    protected boolean discardDuplicates;

    /**
     * The number of elements fetched when invoking the get(index) method.
     */
    protected int fetchSize;

    /**
     * Constructs an empty list.
     *
     * @param discardDuplicates Ensure list not contains duplicates.
     */
    public LazyList(boolean discardDuplicates) {
        super();
        this.discardDuplicates = discardDuplicates;
        this.fetchSize = 10;
        this.instanceList = new ArrayList();
    }

    /**
     * The number of elements fetched when invoking the get(index) method.
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * The number of elements fetched when invoking the get(index) method.
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * Constructs a list from the identifiers of the specified collection,
     * in the order they are returned by the collection's iterator.
     * The <tt>ArrayList</tt> instance has an initial capacity of
     * 110% the size of the specified collection.
     *
     * @param c the collection of ids.
     * @param discardDuplicates Ensure list not contains duplicates.
     */
    public LazyList(Collection c, boolean discardDuplicates) {
        this(discardDuplicates);
        addAll(c);
    }

    /**
     * Check if object is an business object or an identifier.
     */
    public abstract boolean isInstance(Object o);

    /**
     * Retreives the identifier of an instance.
     *
     * @return An identifier.
     */
    public abstract String getInstanceId(Object o);

    /**
     * Loads the instances with the given identifiers.
     *
     * @return A list of full instances (no lazy).
     */
    public abstract List loadInstances(String[] ids);

    /**
     * Creates a brand new lazy list initialiaed with the given list of identifiers.
     */
    public abstract LazyList createLazyList(Collection c);


    /**
     * Retrieve identifiers stored internally in the lazy list.
     *
     * @return An array of identifiers.
     */
    public String[] getIds() {
        String[] ids = new String[size()];
        toArray(ids);
        return ids;
    }

    /**
     * Retrieve identifier stored at given position.
     *
     * @param index
     * @return An identifier.
     */
    public String getId(int index) {
        return (String) super.get(index);
    }

    /**
     * Remove identifier from list.
     * This implementation is based in the ArrayList.indexOf(String)/remove(index) methods
     * We do not use remove(Object) method because invokes internally to the get(i) method which forces the retrieval
     * of instances and causes a low performace.
     */
    public boolean removeId(String id) {
        boolean modified = true;

        // Ensure all repeated ids are removed.
        while (true) {
            // NOTE: use of remove(String) has to be implemented with ArrayList.indexOf(String)/remove(index)
            // because remove(Object) invokes internally to the get(i) method which forces the retrieval of
            // instances and causes a low performace.
            int index = indexOf(id);
            if (index != -1) {
                instanceList.remove(index);
                super.remove(index);
                modified = true;
                continue;
            }
            break;
        }
        return modified;
    }

    /**
     * Retrieves the instance at specified position.
     *
     * @param index
     * @return A instance.
     */
    public Object get(int index) {
        // Check if element is cached.
        Object instance = instanceList.get(index);
        if (instance != null) return instance;

        // Fetch instances...
        int fetchFrom = index;
        int fetchTo = fetchFrom + fetchSize;
        if (fetchTo > this.size()) fetchTo = this.size();
        LazyList ids = (LazyList) this.subList(fetchFrom, fetchTo);
        List loaded = loadInstances(ids.getIds());
        for (int i = fetchFrom; i < fetchTo; i++) {
            instanceList.set(i, loaded.get(i - fetchFrom));
        }

        // Return instance already fetched.
        return instanceList.get(index);
    }

    /**
     * Removes the process from the collection
     *
     * @param o The instance to remove (the identifier is also accepted).
     * @return <tt>true</tt> if the collection contained the specified element.
     */
    public boolean remove(Object o) {
        if (isInstance(o)) return this.removeId(getInstanceId(o));
        else return this.removeId(o.toString());
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     *
     * @param o element whose presence in this List is to be tested.
     */
    public boolean contains(Object o) {
        if (isInstance(o)) return super.contains(getInstanceId(o));
        else return super.contains(o.toString());
    }

    /**
     * Searches for the first occurence of the given argument, testing
     * for equality using the <tt>equals</tt> method.
     *
     * @param elem an object.
     * @return the index of the first occurrence of the argument in this
     *         list; returns <tt>-1</tt> if the object is not found.
     * @see Object#equals(Object)
     */
    public int indexOf(Object elem) {
        if (isInstance(elem)) return super.indexOf(getInstanceId(elem));
        return super.indexOf(elem.toString());
    }

    /**
     * Returns the index of the last occurrence of the specified object in
     * this list.
     *
     * @param elem the desired element.
     * @return the index of the last occurrence of the specified object in
     *         this list; returns -1 if the object is not found.
     */
    public int lastIndexOf(Object elem) {
        if (isInstance(elem)) return super.lastIndexOf(getInstanceId(elem));
        return super.lastIndexOf(elem.toString());
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    public boolean add(Object o) {
        if (discardDuplicates && contains(o)) return false;
        if (isInstance(o)) {
            instanceList.add(o);
            return super.add(getInstanceId(o));
        }
        instanceList.add(null);
        if (o == null) return super.add(o);
        return super.add(o.toString());
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param o     element to be inserted.
     *              If trying to add an instance then its identifier is added instead of the whole instance.
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   <tt>(index &lt; 0 || index &gt; size())</tt>.
     */
    public void add(int index, Object o) {
        if (discardDuplicates && contains(o)) return;
        if (isInstance(o)) {
            instanceList.add(index, o);
            super.add(index, getInstanceId(o));
        }
        instanceList.add(index, null);
        if (o == null) {
            super.add(index, null);
        } else {
            super.add(index, o.toString());
        }
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this list, in the order that they are returned by the
     * specified Collection's Iterator.  The behavior of this operation is
     * undefined if the specified Collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified Collection is this list, and this
     * list is nonempty.)
     *
     * @param c the collection of both instances or ids.
     *          If trying to add an instance then its identifier is added instead of the whole instance.
     * @throws IndexOutOfBoundsException if index out of range <tt>(index
     *                                   &lt; 0 || index &gt; size())</tt>.
     */
    public boolean addAll(Collection c) {
        boolean modified = false;
        Object[] elements = c.toArray();
        for (int i = 0; i < elements.length; i++) modified = this.add(elements[i]);
        return modified;
    }

    /**
     * Not supported.
     */
    public boolean addAll(int index, Collection c) {
        return false;
    }

    /**
     * Removes from this collection all of its elements that are contained in
     * the specified collection (optional operation). <p>
     * <p/>
     * This implementation retrieves the elements from the target collection
     * and performs single remove operations for each one of these elements.
     *
     * @param c elements to be removed from this collection.
     * @return <tt>true</tt> if this collection changed as a result of the call.
     */
    public boolean removeAll(Collection c) {
        boolean modified = false;
        Object[] elements = c.toArray();
        for (int i = 0; i < elements.length; i++) modified = this.remove(elements[i]);
        return modified;
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws IndexOutOfBoundsException if index out of range
     *                                   <tt>(index &lt; 0 || index &gt;= size())</tt>.
     */
    public Object set(int index, Object element) {
        if (isInstance(element)) {
            instanceList.set(index, element);
            return super.set(index, getInstanceId(element));
        }
        instanceList.set(index, null);
        return super.set(index, element.toString());
    }

    /**
     * Returns a view of the portion of this list between <tt>fromIndex</tt>,
     * inclusive, and <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex</tt> and
     * <tt>toIndex</tt> are equal, the returned list is empty.)  The returned
     * list is new and changes in the returned list are NOT
     * reflected in this list, and vice-versa.
     */
    public List subList(int fromIndex, int toIndex) {
        ArrayList ids = new ArrayList();
        for (int i = fromIndex; i < toIndex; i++) ids.add(getId(i));
        return createLazyList(ids);
    }
}
