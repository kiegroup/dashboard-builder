function CreateRepeaterItem(containerId, value, drillDown) 
{
	var div = d3.select("#" + containerId).insert("div", "div");

	div.append("span").html(value.replace(/''''/g, '"').replace(/#containerId/g, containerId));    
}

function GetFilterField(key){
    var objs = document.getElementsByName('value_'+key.toLowerCase());    
    if(objs && objs.length > 0){
        return objs[0];
    }
    return null;
}

function RDrill(containerId, key, value)
{
    /*Must have a filter in the column*/
    ClearFilters();
    setTimeout(function(){
                WaitReload(containerId, key, value, 0);
            }, 200);
    return false;
}

function DoDrill(containerId, key, value)
{
    var obj = GetFilterField(key);
    if(obj){
        var opts = obj.options;
        for(var j = 0; j < opts.length; j++) {
            if(opts[j].getAttribute('title') == value) {
                obj.selectedIndex = j;
                obj.onchange();
                break;
            }
        }
    }
    return false;
}

function ClearFilters(){
    var _fps = document.getElementsByName('_fp');
    for(var j = 0; j< _fps.length; j++)
    {
        if(_fps[j].value == 'clear'){
            submitAjaxForm(_fps[j].parentElement);            
            break;
        }
    }
}

function WaitReload(containerId, key, value, i){
    var obj = GetFilterField(key);
    if(obj){
        DoDrill(containerId, key, value);
    }
    else{
        if(i < 25){
            setTimeout(function(){
                WaitReload(containerId, key, value, i+1);
            }, 200)
        }
    }
}