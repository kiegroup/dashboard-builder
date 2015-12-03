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
<style type="text/css">

        svg {
            overflow: hidden;
        }

        rect {
            pointer-events: all;
            cursor: pointer;
            stroke: #EEEEEE;
        }

		.parent .label {
            color: #FFFFFF;
			stroke: #FFFFFF;
            fill: #FFFFFF;            
        }

        .labelbody {
            background: transparent;
        }

        .label {
			stroke: #000000;
            fill: #000000;
			stroke-width: 0;
            margin: 2px;
            white-space: pre;
            overflow: hidden;
            text-overflow: ellipsis;            
        }

        .child .label {
            white-space: pre-wrap;
            text-align: center;
            text-overflow: ellipsis;
        }

        .cell {
            font-size: 11px;
            cursor: pointer
        }

</style>
<script type="text/javascript" defer="defer">
	
	chartData<%=chartId%> = [
        {
            key: "<%= displayer.getTitle() %>",
            values: [
                <% for(int i=0; i < xvalues.size(); i++) 
                   { 
                        if( i != 0 ) out.print(","); 
               %>
                    "<%= xvalues.get(i).replace("\"","\\\"") %>"
                <% } %>
            ]
        }
    ];
	
	var dataset<%=chartId%> = {};
    for(var i = 0; i < chartData<%=chartId%>[0].values.length; i++){
        var keyValues = chartData<%=chartId%>[0].values[i].split(',');
        var currentParent = dataset<%=chartId%>;
        for(var j = 0; j < keyValues.length; j++){
            var field = keyValues[j].split('=')[0].trim();
            var name = keyValues[j].split('=')[1].trim();
            
            if(field != 'Root' && !!currentParent.children){
                if(currentParent.children.length == 0 || currentParent.children[currentParent.children.length - 1]['name'] != name)
                {
                    var child = {
                        "name": name,
                        "field": field
                    };
                    if(j < keyValues.length - 1){
                        child['children'] = [];
                    }
                    currentParent.children.push(child);
                }			
                currentParent = currentParent.children[currentParent.children.length - 1];
            }
            else{
                currentParent['name'] = name;
                if(field != 'Root')
                {
                    currentParent['field'] = field;
                }
                if(!currentParent.children)
                {
                    currentParent['children'] = [];
                }
            }
        }
    }
	
	
	d3.select("#<%=chartId%>").select('svg').remove();
    document.getElementById("tooltip<%=chartId%>").style.display = "none";
    
	loadTreemap("#<%=chartId%>", dataset<%=chartId%>, <%= displayer.getWidth() %>, <%= displayer.getHeight() %>, filterCallback, removeFilterCallback, clearFilterCallback)	

	function filterCallback(d,i){
        console.log(d);
        
        if(i==null){
            i=0;
        }
        if(window.blockedForm == true && i<10){
            setTimeout(function(){
                filterCallback(d, i+1);
            }, 500);
            return false;
        }
        
		var obj = GetFilterField(d.field);
        if(!!obj){
            var opts = obj.options;
            for(var j = 0; j < opts.length; j++) {
                if(opts[j].getAttribute('title') == d.name){
                    obj.selectedIndex = j.toString();
                    submitAjaxForm(obj.form);
                    break;
                }
            }
        }
        else{
            removeFilterCallback(d);
            setTimeout(function(){
                    WaitReload(d.field, 0, filterCallback, d);
                }, 500);    
        }
        return false;
	} 

    var clearingStarted=false;
    
	function removeFilterCallback(d){
        clearingStarted=true;
		var objs = document.getElementsByName('filteredPropertyToDelete');    
		if(!!objs && objs.length > 0){
			var obj = objs[0];
			obj.value = d.field.toLowerCase();			
			submitAjaxForm(obj.form);
            clearingStarted=false;
			return true;
		}
        return false;
	}
    
    
	function clearFilterCallback(){
        clearingStarted=true;
		var _fps = document.getElementsByName('_fp');
        for(var j = 0; j< _fps.length; j++)
        {
            if(_fps[j].value == 'clear'){
                submitAjaxForm(_fps[j].form);
                clearingStarted=false;
                break;
            }
        }
	}	

    function GetFilterField(key){
        var objs = document.getElementsByName('value_'+key.toLowerCase());    
        if(!!objs && objs.length > 0){
            return objs[0];
        }
        return null;
    }

    function WaitReload(key, i, callback, param){
        var obj = GetFilterField(key);        
        if(!!obj){        
            callback(param);
        }
        else{
            if(i < 25){
                setTimeout(function(){
                    WaitReload(key, i+1, callback, param);
                }, 200)
            }
        }
    }		
</script>
