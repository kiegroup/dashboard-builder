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
<%@ taglib uri="factory.tld" prefix="factory"%>
<%@ taglib uri="bui_taglib.tld" prefix="panel"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>

<!-- Submit action of the KPIEditor component-->
<form method="post"  id="<panel:encode name="submit"/>" action="<panel:link action="submit"/>">
<panel:hidden action="submit" />
    <factory:useComponent bean="org.jboss.dashboard.ui.components.KPIEditor"/>
</form>
<script defer="true">
    setAjax("<panel:encode name="submit"/>");
</script>