function CreateProgressBars(containerId, name, startDate, endDate, size, done, callback) 
{
	size = Math.max(1, size);
	var totalDays = Math.max(1, Math.ceil(Math.abs(new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));
	var elapsedDays = Math.max(0, Math.ceil(((new Date()).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));	
	var elapsedPercent = Math.ceil(Math.max(Math.min((elapsedDays * 100) / totalDays, 100), 1));
	var donePercent = Math.ceil((Math.min((done * 100) / size, 100)));	

	var div = d3.select("#" + containerId).insert("div", "div");

	div.append("span")	
			.append("div")	
				.style("display", "inline-block")
				.style("font", "12px Helvetica Neue")
				.style("text-align", "right")
				.style("padding", "1px")
				.style("padding-right", "5px")
				.style("vertical-align", "top")
				.style("height", "40px")
                .style("width", "150px")
				.html(name+'<BR>('+endDate+')');

	var bars = 
	div.append("span")
			.style("display", "inline-block")
			.style("text-align", "left")
			.style("width", "150px");
	 
	bars.append("div")	
			.attr("class", "progress")
			.style("border", "solid 1px #ccc")	  
			.style("background", "#eee")
			.style("width", "150px")	  
			.style("height", "25px")	  
			.append("div")
				.style("height", "25px")
				.style("width", donePercent + "%")      
				.style("background", "#3182bd");

	bars.append("div")
			.style("margin", "5 1 1 1")
			.style("height", "3px")
			.style("width", elapsedPercent + "%")      
			.style("background", "#3182bd");
    
    if(callback){
        div.on("click", function() {
          callback(name);
          d3.event.stopPropagation();
        });
     }
}