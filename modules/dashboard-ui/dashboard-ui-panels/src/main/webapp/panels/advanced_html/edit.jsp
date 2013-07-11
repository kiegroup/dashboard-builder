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
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<i18n:bundle id="bundle" baseName="org.jboss.dashboard.ui.panel.messages" locale="<%=LocaleManager.currentLocale()%>"/>
<mvc:formatter name="org.jboss.dashboard.ui.panel.advancedHTML.HTMLDriverEditFormatter">
    <mvc:fragment name="outputStart">
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
        <textarea id="<panel:encode name='htmlContent' />" name="<mvc:fragmentValue name="contentParamName"/>"><mvc:fragmentValue name="content"/></textarea>
        <script type="text/javascript" language="Javascript" defer="true">

            CKEDITOR.replace('<panel:encode name='htmlContent' />', {
                language: '<%=LocaleManager.currentLang()%>',
                contentsCss: '<resource:link category="skin" resourceId="CSS"/>',
                extraPlugins: 'stylesheetparser',
                customConfig: '',
                width: '100%',
                height: 500,
                allowedContext: true,
                baseFloatZIndex: 20000002, // greater than modal dialog's
                resize_enabled: false,
                startupMode: 'wysiwyg',
                startupShowBorders: false,
                startupFocus: true,
                toolbarLocation: 'top',
                toolbarCanCollapse: false,
                toolbarStartupExpanded: true,
                toolbarGroups: [
                    { name: 'document',    groups: [ 'document', 'doctools' ] },
                    { name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
                    { name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
                    { name: 'forms'  },
                    { name: 'tools'  },
                    '/',
                    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                    { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] },
                    { name: 'insert',     groups: [ 'links', 'insert' ] },
                    '/',
                    { name: 'mode' },
                    { name: 'styles' },
                    { name: 'colors' },
                    { name: 'others' }
                ]
            });
        </script>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </form>
    </mvc:fragment>
</mvc:formatter>
