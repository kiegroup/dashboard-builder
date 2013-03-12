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
<%@ taglib prefix="mvc" uri="mvc_taglib.tld"%>

<mvc:formatter name="org.jboss.dashboard.ui.panel.navigation.treeMenu.TreeMenuFormatter">
    <mvc:formatterParam name="allSectionsOpen" value='<%=request.getAttribute("openAllSections")%>'/>
    <mvc:fragment name="pageStart">
        <div width="100%">
    </mvc:fragment>
    <mvc:fragment name="beforeTabulation">
        <table cellspacing="0" cellpadding="0" width="100%"><tr><td>
    </mvc:fragment>
    <mvc:fragment name="afterTabulation">
        </td>
    </mvc:fragment>
    <mvc:fragment name="childrenStart">
        <td>
    </mvc:fragment>
    <mvc:fragment name="childrenEnd">
        </td></tr></table>        
    </mvc:fragment>
    <mvc:fragment name="pageEnd">
        </div>
    </mvc:fragment>
</mvc:formatter>

