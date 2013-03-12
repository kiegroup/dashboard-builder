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
<script language="javascript" type="text/javascript">
    //script for datasource management panel
    var testQuerys = new Array();

		testQuerys[0]= "";
    testQuerys[1]= "SELECT 1"; <%-- test query for mysql --%>
    testQuerys[2]= "SELECT 1"; <%-- test query for postgresql --%>
    testQuerys[3]= "SELECT SYSDATE FROM DUAL"; <%-- test query for oracle --%>
    testQuerys[4]= "SELECT 1"; <%-- test query for sqlServer --%>
		testQuerys[5]= "SELECT 1"; <%-- test query for h2 --%>

    function changeTestQuery(select, id) {
        var textArea = document.getElementById(id);
        textArea.value = testQuerys[select.selectedIndex]
    }
		function changeDriverClass(select, id) {
			var className=select.options[select.selectedIndex].value;
			var input=document.getElementById(id);
			input.value=className;
		}
</script>