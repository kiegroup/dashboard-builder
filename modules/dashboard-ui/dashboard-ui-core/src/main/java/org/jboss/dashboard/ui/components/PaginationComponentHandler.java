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
package org.jboss.dashboard.ui.components;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.controller.CommandRequest;

@PanelScoped
@Named("pagination_handler")
public class PaginationComponentHandler extends UIBeanHandler {

    public static final String SELECT_PAGE = "param_select_page";

    @Inject @Config("/components/pagination/show.jsp")
    private String componentIncludeJSP;

    private PaginationContentProvider contentProvider;
    private int[] resultsPerPage = new int[]{5, 10, 15, 20};
    private int currentPage = 1;
    private int pageSize = 5;
    private int pagesToShow = 5;
    private boolean usedInsideForm = false;
    private boolean showHeader = true;
    private boolean showBorder = true;

    public PaginationComponentHandlerCache getCache() {
        PaginationComponentHandlerCache cache = CDIBeanLocator.getBeanByType(PaginationComponentHandlerCache.class);
        if (cache.isNew()) {
            cache.initialize(contentProvider);
        }
        return cache;
    }

    public void actionChangePageSize(CommandRequest request) {
        calculateValues();
    }

    public void actionFirstPage(CommandRequest request) {
        calculateValues();
        currentPage = 1;
    }

    public void actionLastPage(CommandRequest request) {
        calculateValues();
        currentPage = calculateLastPage();
    }

    public void actionNextPage(CommandRequest request) {
        calculateValues();
        if (currentPage < getCache().getSize()) currentPage++;
    }

    public void actionPreviousPage(CommandRequest request) {
        calculateValues();
        if (currentPage > 0) currentPage--;
    }

    public void actionSelectPage(CommandRequest request) {
        calculateValues();
        String value = request.getRequestObject().getParameter(SELECT_PAGE);
        if (value != null && !"".equals(value.trim())) {
            int page = Integer.decode(value).intValue();
            if (page <= getCache().getSize()) currentPage = page;
        }
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public PaginationContentProvider getContentProvider() {
        return contentProvider;
    }

    public void setContentProvider(PaginationContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public int getPagesToShow() {
        return pagesToShow;
    }

    public void setPagesToShow(int pagesToShow) {
        this.pagesToShow = pagesToShow;
    }

    public boolean isUsedInsideForm() {
        return usedInsideForm;
    }

    public void setUsedInsideForm(boolean usedInsideForm) {
        this.usedInsideForm = usedInsideForm;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int[] getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int[] resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getElementToStart() {
        int element = (pageSize * (currentPage - 1));
        int length = getCache().getSize();
        while (element > length) {
            currentPage--;
            element = (pageSize * (currentPage - 1));
        }
        return element;
    }

    public int getElementToEnd() {
        int element = getElementToStart() + pageSize;
        int length = getCache().getSize();
        while (element > length) {
            element--;
        }
        return element;
    }

    public void calculateValues() {
        int length = getCache().getSize();
        while (length < getElementToStart()) {
            currentPage--;
        }
    }

    public int calculateLastPage() {
        double value = (double) getCache().getSize() / (double) getPageSize();
        int pages = (int) Math.floor(value);
        if (value > pages) pages++;
        return pages;
    }

    /*
    * This is a method to change the TestContentProvider's size. Use it only on tests
    * */
    public void actionUpdateMax(CommandRequest request) {
        String elements = request.getRequestObject().getParameter("elements");
        if (elements != null && !"".equals(elements) && getContentProvider() instanceof TestContentProvider) {
            TestContentProvider cp = (TestContentProvider) getContentProvider();
            cp.setMax(Integer.decode(elements).intValue());
        }

    }
}
