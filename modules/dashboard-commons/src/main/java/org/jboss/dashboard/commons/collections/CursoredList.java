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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CursoredList implements Serializable {

    /**
     * Default page size.
     */
    private int DEFAULT_PAGE_SIZE = 10;

    /**
     * The collection of elements.
     */
    private List list;

    /**
     * Cursor current page number. From 1 to N.
     */
    private int pageNumber;

    /**
     * Cursor page size.
     */
    private int pageSize;


    public CursoredList(List l) {
        list = l;
        pageNumber = 1;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets current page.
     *
     * @param pageNumber The page number where cursor must be positioned.
     * @throws IllegalArgumentException Igf page number is out of bounds
     */
    public void setPageNumber(int pageNumber) throws ArrayIndexOutOfBoundsException {
        this.pageNumber = pageNumber;
        if (pageNumber < 1) throw new ArrayIndexOutOfBoundsException(pageNumber);
        if (pageNumber > getNumberOfPages()) throw new ArrayIndexOutOfBoundsException(pageNumber);

    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getNumberOfPages() {
        return ((list.size() - 1) / pageSize) + 1;
    }

    /**
     * Retrieve elements belonging to current cursor page.
     */
    public List getPage() {
        // Calculate page begin/end absolute positions.
        int pageBegin = (pageNumber - 1) * pageSize;
        int pageEnd = pageBegin + pageSize;
        if (pageEnd > list.size()) pageEnd = list.size();

        // Retrieve page elements.
        List pageList = new ArrayList();
        for (int i = pageBegin; i < pageEnd; i++)
            pageList.add(list.get(i));
        return pageList;
    }
}
