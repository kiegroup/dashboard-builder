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
package org.jboss.dashboard.ui.formatters;

import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.components.PaginationComponentHandler;
import org.jboss.dashboard.ui.components.PaginationContentProvider;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PaginationComponentFormatter extends Formatter {

    @Inject
    private transient Logger log;

    public static final String PARAM_ELEMENT = "element";
    public static final String PARAM_INDEX = "index";
    public static final String PARAM_POSITION = "position";
    public static final String PARAM_INVERSE_POSITION = "inversePosition";

    @Inject
    private MessagesComponentHandler messagesComponentHandler;

    @Inject
    private PaginationComponentHandler paginationComponentHandler;

    public MessagesComponentHandler getMessagesComponentHandler() {
        return messagesComponentHandler;
    }

    public void setMessagesComponentHandler(MessagesComponentHandler messagesComponentHandler) {
        this.messagesComponentHandler = messagesComponentHandler;
    }

    public PaginationComponentHandler getPaginationComponentHandler() {
        return paginationComponentHandler;
    }

    public void setPaginationComponentHandler(PaginationComponentHandler paginationComponentHandler) {
        this.paginationComponentHandler = paginationComponentHandler;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        PaginationContentProvider contentProvider = getPaginationComponentHandler().getContentProvider();
        boolean paginationError = false;
        int providerLength = 0;
        if ((contentProvider != null)) {
            providerLength = getPaginationComponentHandler().getCache().getSize();
            if ((providerLength > 0)) {
                renderFragment("outputStart");
                List elements = null;
                try {
                    getPaginationComponentHandler().calculateValues();
                    elements = contentProvider.getSublist(getPaginationComponentHandler().getElementToStart(), getPaginationComponentHandler().getElementToEnd());
                } catch (Exception e) {
                    log.warn("Error getting elements to paginate: ", e);
                    paginationError = true;
                    getMessagesComponentHandler().addError("pagination.error");
                    renderFragment("outputError");
                }
                if (elements != null && !paginationError) {
                    if (paginationComponentHandler.isShowHeader())
                        renderPaginationHeader(contentProvider, providerLength);
                    if (contentProvider.getPageHeader() != null) includePage(contentProvider.getPageHeader());
                    for (int i = 0; i < elements.size(); i++) {
                        if (contentProvider.getPageForElement() != null) {
                            setAttribute(PARAM_ELEMENT, elements.get(i));
                            setAttribute(PARAM_INDEX, i);
                            int position = (getPaginationComponentHandler().getCurrentPage() - 1) * getPaginationComponentHandler().getPageSize() + i;
                            setAttribute(PARAM_POSITION, position + 1);
                            setAttribute(PARAM_INVERSE_POSITION, providerLength - position);
                            includePage(contentProvider.getPageForElement());
                            if (i < elements.size() - 1 && contentProvider.getPageSeparator() != null)
                                includePage(contentProvider.getPageSeparator());
                        }
                    }
                    if (contentProvider.getPageBottom() != null) includePage(contentProvider.getPageBottom());
                    renderPaginationBottom();
                }
                renderFragment("outputEnd");
            }
        }
        if (!paginationError && contentProvider != null && providerLength == 0 && contentProvider.getPageEmpty() != null)
            includePage(contentProvider.getPageEmpty());
    }

    protected void renderPaginationHeader(PaginationContentProvider contentProvider, int length) {
        boolean useForm = !paginationComponentHandler.isUsedInsideForm();
        renderFragment("outputPaginationHeaderStart");
        if (useForm) renderFragment("outputPaginationHeaderFormStart");
        else renderFragment("outputPaginationHeaderNoFormStart");
        for (int i = 0; i < getPaginationComponentHandler().getResultsPerPage().length; i++) {
            setAttribute("selected", getPaginationComponentHandler().getResultsPerPage()[i] == getPaginationComponentHandler().getPageSize() ? "selected" : "");
            setAttribute("value", getPaginationComponentHandler().getResultsPerPage()[i]);
            renderFragment("outputPaginationSizeOption");
        }

        String results = (getPaginationComponentHandler().getElementToStart() + 1) + "-" + (getPaginationComponentHandler().getElementToEnd());
        if (useForm) renderFragment("outputPaginationHeaderFormEnd");
        else renderFragment("outputPaginationHeaderNoFormEnd");
        setAttribute("resultsShown", results);
        setAttribute("totalResults", length);
        renderFragment("outputPaginationHeaderEnd");
    }

    protected void renderPaginationBottom() {
        setAttribute("moveLeft", getPaginationComponentHandler().getCurrentPage() != 1);
        renderFragment("outputPaginationBottomStart");

        int startPage = 1;
        int currentPage = getPaginationComponentHandler().getCurrentPage();
        int lastPage = getPaginationComponentHandler().calculateLastPage();

        if (lastPage > getPaginationComponentHandler().getPagesToShow()) {
            int distanceLeft, distanceRight;
            if (getPaginationComponentHandler().getPagesToShow() % 2 != 0) {
                distanceLeft = (int) Math.floor((double) getPaginationComponentHandler().getPagesToShow() / 2.0);
                distanceRight = distanceLeft;
            } else {
                distanceLeft = getPaginationComponentHandler().getPagesToShow() / 2;
                distanceRight = distanceLeft - 1;
            }

            if (currentPage - distanceLeft < 1) {
                while (currentPage - distanceLeft < 1) {
                    distanceLeft--;
                    distanceRight++;
                }
            } else if (currentPage + distanceRight > lastPage) {
                while (currentPage + distanceRight > lastPage) {
                    distanceLeft++;
                    distanceRight--;
                }
            }
            startPage = currentPage - distanceLeft;
            lastPage = currentPage + distanceRight;
        }

        for (int i = startPage; i <= lastPage; i++) {
            setAttribute("npage", i);
            setAttribute("selected", i == currentPage);
            renderFragment("outputPaginationPage");
        }

        setAttribute("moveRight", getPaginationComponentHandler().getCurrentPage() < lastPage);
        renderFragment("outputPaginationBottomEnd");
    }

}
