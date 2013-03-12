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
<%@ page import="org.jboss.dashboard.ui.components.RedirectionHandler"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>

<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jboss.dashboard.ui.panel.advancedHTML.HTMLDriverEditFormatter">
    <mvc:fragment name="outputStart">
        <div style="width:100%"><center>
        <form name="<panel:encode name="htmlForm"/>" method="post" action="<mvc:fragmentValue name="url"/>">
    </mvc:fragment>
    <mvc:fragment name="languagesOutputStart">
        <table cellpadding="10" cellspacing=0 width="100%" border="0" style="background-color:#efefde;">
        <tr><td width="50%">&nbsp;</td>
    </mvc:fragment>
    <mvc:fragment name="selectedLanguageOutput">
        <td width="1px" align="center" style="white-space:nowrap;" class="skn-important">&nbsp;&nbsp;<mvc:fragmentValue name="langName"/>&nbsp;&nbsp;</td>
    </mvc:fragment>
    <mvc:fragment name="languageOutput">
        <td width="1px" align="center">&nbsp;&nbsp;<a
                onclick="document.<panel:encode name="htmlForm"/>.action='<mvc:fragmentValue name="url"/>';document.<panel:encode name="htmlForm"/>.submit();return false;"
                href="#"><mvc:fragmentValue name="langName"/></a>&nbsp;&nbsp;</td>
    </mvc:fragment>
    <mvc:fragment name="languagesOutputEnd">
        <td width="50%">&nbsp;</td>
        </tr>
        </table>
    </mvc:fragment>
    <mvc:fragment name="output">
        <textarea id="<panel:encode name='Content_html' />" name="<mvc:fragmentValue name="contentParamName"/>"
                  style="WIDTH: 100%; HEIGHT: 500px" rows="20" cols="60"><mvc:fragmentValue name="content"/></textarea>
        <input class="skn-button" type="submit" name="submitInput"
               value="<i18n:message key="ui.saveChanges"/>">
        <script type="text/javascript" language="Javascript" defer>
            var sBasePath = 'fckeditor/';
            var oFCKeditor = new FCKeditor('<panel:encode name='Content_html' />', '100%', '500') ;
            oFCKeditor.BasePath = sBasePath;
            oFCKeditor.Config['CustomConfigurationsPath'] = '<factory:url bean="org.jboss.dashboard.ui.components.RedirectionHandler" action="redirectToSection" friendly="false"><factory:param name="<%=RedirectionHandler.PARAM_PAGE_TO_REDIRECT%>" value="/fckeditor/custom/fckConfig.jsp"/></factory:url>';
            oFCKeditor.Config['minimizedToolbarSet']= 'miniForHTMLPanel';
            oFCKeditor.Config['maximizedToolbarSet']= 'fullForHTMLPanel';
            oFCKeditor.ToolbarSet = 'miniForHTMLPanel';
            oFCKeditor.Config["DefaultLanguage"] = '<%=LocaleManager.currentLang()%>';
            oFCKeditor.ReplaceTextarea();
            if (document.<panel:encode name="htmlForm"/>.<mvc:fragmentValue name="contentParamName"/>.style.display == 'none')
                document.<panel:encode name="htmlForm"/>.removeChild(document.<panel:encode name="htmlForm"/>.submitInput);
        </script>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </form>
        </center></div>
    </mvc:fragment>
</mvc:formatter>
