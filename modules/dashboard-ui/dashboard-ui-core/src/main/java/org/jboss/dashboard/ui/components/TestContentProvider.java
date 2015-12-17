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
package org.jboss.dashboard.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dashboard.ui.annotation.panel.PanelScoped;

@PanelScoped
public class TestContentProvider implements PaginationContentProvider {

    private String pageHeader = "/components/pagination/test/head.jsp";
    private String pageForElement = "/components/pagination/test/element.jsp";
    private String pageBottom = "/components/pagination/test/bottom.jsp";
    private String pageSeparator;
    private String pageEmpty;

    private List values;
    private int max = 120;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        initValues();
    }

    public String getPageBottom() {
        return pageBottom;
    }

    public void setPageBottom(String pageBottom) {
        this.pageBottom = pageBottom;
    }

    public String getPageForElement() {
        return pageForElement;
    }

    public void setPageForElement(String pageForElement) {
        this.pageForElement = pageForElement;
    }

    public String getPageHeader() {
        return pageHeader;
    }

    public void setPageHeader(String pageHeader) {
        this.pageHeader = pageHeader;
    }

    private void initValues() {
        values = new ArrayList();
        /*Random rnd = new Random();
        while (max <= 10) {
            max = rnd.nextInt(40);
        } */
        for (int i = 0; i < max; i++) {
            values.add(String.valueOf(i));
        }
    }

    public List getSublist(int x, int y) {
        if (values == null) initValues();
        if (getLength() <= y) y = getLength();
        return values.subList(x, y);
    }

    public int getLength() {
        if (values == null) initValues();
        return values.size();
    }

    public String getPageSeparator() {
        return pageSeparator;
    }

    public void setPageSeparator(String pageSeparator) {
        this.pageSeparator = pageSeparator;
    }

    public String getPageEmpty() {
        return pageEmpty;
    }

    public void setPageEmpty(String pageEmpty) {
        this.pageEmpty = pageEmpty;
    }
}
