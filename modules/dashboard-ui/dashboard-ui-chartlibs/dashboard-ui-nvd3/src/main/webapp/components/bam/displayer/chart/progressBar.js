function CreateProgressBars(containerId, name, startDate, endDate, size, done, progress, callback) 
{
    if(name.indexOf('.jboss.dashboard.domain') > 0){
        return false;
    }
	size = Math.max(1, size);
	var totalDays = Math.max(1, Math.ceil(Math.abs(new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));
	var elapsedDays = Math.max(0, Math.ceil(((new Date()).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));	
	var elapsedPercent = Math.ceil(Math.max(Math.min((elapsedDays * 100) / totalDays, 100), 1));
	var donePercent = Math.ceil((Math.min((done * 100) / size, 100)));
	var progressPercent = Math.ceil((Math.min((progress * 100) / size, 100)));	
    
    if(progressPercent + donePercent > 100){
        progressPercent = 100-donePercent;
    }
    
    var elapsedColor = "#00AA00";    
    var performanceIndex = (donePercent+progressPercent) / elapsedPercent;
    if(performanceIndex >= 1)
        elapsedColor = "#00AA00";
    else if(performanceIndex < 1 && elapsedPercent >= 100)
        elapsedColor = "#000000";
    else if(performanceIndex < 1 && performanceIndex >= 0.85)
        elapsedColor = "#EEDD22";
    else if(performanceIndex < 0.85)
        elapsedColor = "#FF6666";
    
	var div = d3.select("#" + containerId).insert("div", "div").style("cursor", "pointer");

	div.append("span")	
			.append("div")	
				.style("display", "inline-block")
				.style("font", "12px Helvetica Neue")
				.style("text-align", "right")
				.style("padding", "1px")
				.style("padding-right", "5px")
				.style("vertical-align", "top")
				.style("height", "35px")
                .style("width", "150px")
				.html(name+'<BR>('+startDate+' - '+endDate+')');

	var bars = 
	div.append("span")
			.style("display", "inline-block")
			.style("text-align", "left")
			.style("width", "150px");
	 
	var innerDiv = bars.append("div")
			.attr("class", "progress")
			.style("border", "solid 1px #ccc")	  
			.style("background", elapsedColor)
			.style("width", "150px")	  
			.style("height", "25px")	  
			.append("div").style('overflow', 'hidden');
			
	innerDiv.append("div")
				.style("height", "25px")
				.style("width", donePercent + "%")      
				.style("background", "#22EE22")
                .style("display", "inline-block")
                .attr("title", "Done: " + donePercent + "%");
	innerDiv.append("div")
				.style("height", "25px")
				.style("width", progressPercent + "%")      
				.style("background", "#CCFF33")
                .style("display", "inline-block")
                .attr("title", "In Progress: " + progressPercent + "%");

    if(callback){
        div.on("click", function() {
          callback(name);
          d3.event.stopPropagation();
        });
     }
}