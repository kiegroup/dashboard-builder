function CreateRepeaterItem(containerId, value, drillDown) 
{
	var div = d3.select("#" + containerId).insert("div", "div");

	div.append("span").html(value.replace('#containerId', containerId));    
}

function RDrill(containerId, key, value)
{
	form = document.getElementById('form'+containerId);
	form.elements[key].value = value;
    submitAjaxForm(form);
}