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
<%
	int decimalPrecision = 2;
    if (displayer.isAxisInteger()) decimalPrecision = 0;
%> 
<script type="text/javascript" defer="defer">
	
	chartData<%=chartId%> = [
        {
            key: "<%= displayer.getTitle() %>",
            values: [
                <% for(int i=0; i < xvalues.size(); i++) { if( i != 0 ) out.print(", "); %>
                {
                    "value" : "<%= xvalues.get(i) %>" ,                    
                }
                <% } %>
            ]
        }
    ];
	
	d3.select("#<%=chartId%>").select('svg').remove()
	for(i = chartData<%=chartId%>[0].values.length-1; i >=0 ; i--)
	{
		var item = chartData<%=chartId%>[0].values[i];
		CreateRepeaterItem('<%=chartId%>', item.value);
	}
</script>
