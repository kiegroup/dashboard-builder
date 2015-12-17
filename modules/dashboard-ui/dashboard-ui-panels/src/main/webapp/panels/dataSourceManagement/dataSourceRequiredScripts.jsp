<%--

    Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.

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

  testQuerys[0] = "";
  testQuerys[1] = "SELECT 1 FROM SYSIBM.SYSDUMMY1";  <%-- db2 --%>
  testQuerys[2] = "SELECT 1";  <%-- h2 --%>
  testQuerys[3] = "SELECT 1";  <%-- mysql --%>
  testQuerys[4] = "SELECT SYSDATE FROM DUAL";  <%-- test query for oracle --%>
  testQuerys[5] = "SELECT 1";  <%-- postgresql --%>
  testQuerys[6] = "SELECT 1";  <%-- sqlServer --%>
  testQuerys[7] = "SELECT 1";  <%-- Sybase --%>
  testQuerys[8] = "SELECT 1";  <%-- Teiid --%>

  function changeTestQuery(select, id) {
      var textArea = document.getElementById(id);
      textArea.value = testQuerys[select.selectedIndex];
  }
  function changeDriverClass(select, id) {
      var className = select.options[select.selectedIndex].value;
      var input = document.getElementById(id);
      input.value = className;
  }
</script>