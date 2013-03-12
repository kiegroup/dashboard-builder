<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ page import="org.jboss.dashboard.ui.components.PaginationComponentHandler" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib prefix="static" uri="static-resources.tld" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="org.jboss.dashboard.ui.components.messages" id="defaultBundle" locale="<%=LocaleManager.currentLocale()%>"/>

<factory:property property="paginationComponentFormatter" id="paginationComponentFormatter">
<mvc:formatter name="<%=paginationComponentFormatter%>">
    <mvc:fragment name="outputStart">
        <factory:property property="showBorder" id="showBorder">
            <div class="<%=Boolean.TRUE.equals(showBorder)?"skn-table_border":""%>" style="width:100%; background-color:#FFFFFF;">
        </factory:property>
    </mvc:fragment>
    <mvc:fragment name="outputError">
        <factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="i18nBundle" propValue="org.jboss.dashboard.ui.components.messages" />
        <factory:setProperty bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"
                     property="clearAfterRender" propValue="true" />
        <factory:useComponent bean="org.jboss.dashboard.ui.components.MessagesComponentHandler"/>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderStart">
            <div style="padding:4px;">
                <table style="border:0px; margin:0px; width:100%;" cellpadding="0" cellspacing="0">
                    <tr>
                        <td style="width:50%;">
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderFormStart">
                            <form style="margin:0px; padding: 0px;" action="<factory:formUrl/>" method="POST" id="<factory:encode name="selectionNPages"/>">
                                <factory:handler  action="changePageSize"/>
                                <i18n:message key="pagination.resultsPerPage">!!!ResultsForEachPage</i18n:message>
                                <select class="skn-input" name="<factory:bean property="pageSize"/>" onchange="submitAjaxForm(this.form);">
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderNoFormStart">
                            <i18n:message key="pagination.resultsPerPage">!!!ResultsForEachPage</i18n:message>
                            <select class="skn-input" name="<factory:bean property="pageSize"/>"
                                onchange="sendFormToHandler(this.form, '<factory:currentComponent/>','changePageSize');">
    </mvc:fragment>
    <mvc:fragment name="outputPaginationSizeOption">
        <mvc:fragmentValue name="value" id="value">
                                    <option value="<%=value%>" <mvc:fragmentValue name="selected"/>>
                                        <%=value%>
                                    </option>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderFormEnd">
                                </select>
                            </form>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderNoFormEnd">
                            </select>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationHeaderEnd">
                        </td>
                        <td style="vertical-align:middle; width:50%; text-align:right;">
                            <i18n:message key="pagination.showingResults">!!!showingResults</i18n:message>&nbsp;
                            <mvc:fragmentValue name="resultsShown"/> &nbsp;
                            <i18n:message key="pagination.of">!!!of</i18n:message>&nbsp;
                            <mvc:fragmentValue name="totalResults"/>
                        </td>
                    </tr>
                </table>
            </div>
            <div style="width:100%;">
    </mvc:fragment>
    <mvc:fragment name="outputPaginationBottomStart">
        <mvc:fragmentValue name="moveLeft" id="moveLeft">
            </div>
            <div style="width:100%; text-align:center;">
<%
    if (moveLeft!=null && ((Boolean)moveLeft).booleanValue()) {
%>
                <a id="<factory:encode name="firstPage"/>" href="<factory:url action="firstPage"/>">
                    <img border="0" src="<static:image relativePath="general/12x12/ico-page_first.png"/>">
                </a>
                <a id="<factory:encode name="previousPage"/>" href="<factory:url action="previousPage"/>">
                    <img border="0" src="<static:image relativePath="general/12x12/ico-page_previous.png"/>">
                </a>
                <script defer="true">
                    setAjax("<factory:encode name='firstPage'/>");
                    setAjax("<factory:encode name='previousPage'/>");
                </script>
<%
    } else {
%>
                <img border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" src="<static:image relativePath="general/12x12/ico-page_first.png"/>">
                <img border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" src="<static:image relativePath="general/12x12/ico-page_previous.png"/>">
<%
    }
%>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationPage">
        <mvc:fragmentValue name="npage" id="npage">
        <mvc:fragmentValue name="selected" id="selected">
                <a href="<factory:url action="selectPage"><factory:param name="<%=PaginationComponentHandler.SELECT_PAGE%>" value="<%=npage%>"/></factory:url>"
                   id="<factory:encode name='<%="selectPage_"+npage%>'/>">
<%
    if (selected != null && ((Boolean)selected).booleanValue()) {
%>
                    <b>
                        <%=((Integer) npage).intValue()%>
                    </b>
<%
    } else {
%>
                    <%=((Integer) npage).intValue()%>
<%
    }
%>
                </a>
                <script defer="true">
                    setAjax("<factory:encode name='<%="selectPage_"+npage%>'/>");
                </script>
        </mvc:fragmentValue>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputPaginationBottomEnd">
        <mvc:fragmentValue name="moveRight" id="moveRight">
<%
    if (moveRight!=null && ((Boolean)moveRight).booleanValue()) {
%>
                <a id="<factory:encode name="nextPage"/>" href="<factory:url action="nextPage"/>">
                    <img border="0" src="<static:image relativePath="general/12x12/ico-page_following.png"/>">
                </a>
                <a id="<factory:encode name="lastPage"/>" href="<factory:url action="lastPage"/>">
                    <img border="0" src="<static:image relativePath="general/12x12/ico-page_last.png"/>">
                </a>
                <script defer="defer">
                    setAjax("<factory:encode name="nextPage"/>");
                    setAjax("<factory:encode name="lastPage"/>");
                </script>
<%
    } else {
%>
                <img border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" src="<static:image relativePath="general/12x12/ico-page_following.png"/>">
                <img border="0" style="opacity:0.5;-moz-opacity:0.5;filter:alpha(opacity=50);" src="<static:image relativePath="general/12x12/ico-page_last.png"/>">
<%
    }
%>
            </div>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </div>
    </mvc:fragment>
</mvc:formatter>
</factory:property>