function toDate(txt){
    var members = txt.split('-');
    return new Date(members[0], members[1]-1, members[2])
}

function dateDiff(d1, d2) {
    var t2 = toDate(d2).getTime();
    var t1 = toDate(d1).getTime();
    return parseInt((t2-t1)/(24*3600*1000));
}

function getDateDayAndMonth(txt){
    var members = txt.split('-');
    return members[2] + '/' + members[1];
}

function getDateFull(txt){
    var members = txt.split('-');
    return ('0' + members[2]).slice(-2) + '/' + ('0' + members[1]).slice(-2) + '/' + ('0000' + members[0]).slice(-4);
}

function dateAddMonth(dt, useMonthStart){
    var currDate = toDate(dt);
    var currDay   = currDate.getDate();
    var currMonth = currDate.getMonth() + 1;
    var currYear  = currDate.getFullYear();
    
    currMonth = currMonth + 1;
    if (currMonth > 12)
    { 
        currMonth = 1;
        currYear = currYear + 1;
    }
    if(!!useMonthStart){
        currDay = '01';
    }
    return ('0000' + currYear).slice(-4) + '-' + ('0' + currMonth).slice(-2) + '-' + ('0' + currDay).slice(-2); 
}

var daySize = 4;

function CreateGanttAxis(containerId, initialDate, endingDate, rowCount, locale)
{
    initialDate = initialDate.substr(0, 8) + '01';
    endingDate = endingDate.substr(0, 10);
    if(endingDate.substr(8, 2) != "01"){
        endingDate = dateAddMonth(endingDate).substr(0, 8) + '01';
    }
    
    var today = new Date();
    var todayTxt = today.getFullYear() + '-' + (today.getMonth()+1) + '-' + today.getDate();
    
    var margin = 170+15;
    var padding = dateDiff(initialDate, todayTxt)*daySize + margin;
    var height = rowCount * 31 + 10;    
    var totalSize = dateDiff(initialDate, endingDate)*daySize;
    
    var mainDiv = d3.select("#" + containerId).insert("div", "div")
                .style("position", "relative")
                .style("width", (totalSize + 170+50) + "px");
    
    //Create Today
    if(todayTxt >= initialDate && todayTxt <= endingDate)
    mainDiv.append("div")
            .style("z-index", "9999")
            .style("position", "absolute")
            .style("width", "3px")
            .style("height", height + "px")
            .style("left", padding + "px")
            .style("background-color", "#000000")
            .attr("title", getDateFull(todayTxt));
    
    var months = Math.ceil(dateDiff(initialDate, endingDate)/30);
    var currDate = initialDate;
    for(var i = 0; i<months; i++){
        var monthPadd = dateDiff(initialDate, currDate)*daySize + margin;
        mainDiv.append("div")
                    .style("position", "absolute")
                    .style("width", "1px")
                    .style("height", height + "px")
                    .style("left", monthPadd + "px")
                    .style("background-color", "#CCCCCC");
        mainDiv.append("div")
                    .style("position", "absolute")
                    .style("width", "1px")
                    .style("top", height + "px")
                    .style("left", (monthPadd-25) + "px")
                    .style("width", "50px")
                    .style("font", "10px Helvetica Neue")
                    .html(getDateFull(currDate));
    
        currDate = dateAddMonth(currDate);
    }
    
    var legendDiv = d3.select("#" + containerId).append("div")
                .style("width", "100%")
                .style("margin-top", "45px")
                .style("text-align", "center");                
    
    CreateLegendItem(legendDiv, "#000000", "Vencido");
    CreateLegendItem(legendDiv, "#FF6666", "Atrasado");
    CreateLegendItem(legendDiv, "#EEDD22", "Alerta");
    CreateLegendItem(legendDiv, "#22EE22", "Concluido");
    CreateLegendItem(legendDiv, "#CCFF33", "Em Andamento");
    CreateLegendItem(legendDiv, "#9999EE", "Planejado");
}

function CreateLegendItem(container, color, text){
    container.append("div")	
            .style("display", "inline-block")
            .style("height", "15px")
            .style("width", "15px")
            .style("margin-right", "5px")            
            .style("background-color", color)
    container.append("div")	
            .style("display", "inline-block")
            .style("height", "25px")
            .style("vertical-align", "middle")
            .style("margin-right", "5px")
            .html(text)
}

function CreateGantt(containerId, index, name, startDate, endDate, size, done, progress, initialDate, endingDate, locale, callback) 
{
    initialDate = initialDate.substr(0, 8) + '01';
    endingDate = endingDate.substr(0, 10);
    if(endingDate.substr(8, 2) != "01"){
        endingDate = dateAddMonth(endingDate).substr(0, 8) + '01';
    }
    
    startDate = startDate.substr(0, 10);
    endDate = endDate.substr(0, 10);
    
    if(name.indexOf('.jboss.dashboard.domain') > 0){
        return false;
    }
	size = Math.max(1, size);
    var today = new Date();
    if(name.indexOf('(') != -1 && name.indexOf(')') != -1){
        var todayTxt = name.split('(')[1].split(')')[0];
        today = toDate(todayTxt);
    }
    
	var totalDays = Math.max(1, Math.ceil(Math.abs(new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));
	var elapsedDays = Math.max(0, Math.ceil(((today).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24)));	
	var elapsedPercent = Math.ceil(Math.max(Math.min((elapsedDays * 100) / totalDays, 100), 1));
	var donePercent = Math.ceil((Math.min((done * 100) / size, 100)));
	var progressPercent = Math.ceil((Math.min((progress * 100) / size, 100)));
    var graphTotalDays = Math.max(1, Math.ceil(Math.abs(new Date(endingDate).getTime() - new Date(initialDate).getTime()) / (1000 * 3600 * 24)));
    
    daySize = (parseInt(d3.select("#" + containerId).style('width').replace('px','')) - 170-50) / graphTotalDays;
    
    if(progressPercent + donePercent > 100){
        progressPercent = 100-donePercent;
    }
    
    var elapsedColor = "#9999EE";    
    var innerTextColor = "#000000";
    var performanceIndex = (donePercent+progressPercent) / elapsedPercent;
    if(performanceIndex >= 1)
        elapsedColor = "#9999EE";
    else if(performanceIndex < 1 && elapsedPercent >= 100){
        elapsedColor = "#000000";
        innerTextColor = "#CC0000"; 
    }
    else if(performanceIndex < 1 && performanceIndex >= 0.85)
        elapsedColor = "#EEDD22";
    else if(performanceIndex < 0.85)
        elapsedColor = "#FF6666";
    
    var totalSize = dateDiff(initialDate, endingDate)*daySize;
    var startPadding = dateDiff(initialDate, startDate)*daySize;
    var itemSize = dateDiff(startDate, endDate)*daySize
    var div = d3.select("#" + containerId).insert("div", "div")
                .style("cursor", "pointer")
                .style("width", (totalSize + 170+50) + "px")
                .style("padding-top", "3px")
                .style("padding-bottom", "3px")
                .style("text-align", "left");
    if(index % 2 == 1){
        div.style('background-color', '#F9F9F9')
    }

	div.append("div")	
				.style("display", "inline-block")
				.style("font", "14px Helvetica Neue")
				.style("text-align", "right")
				.style("height", "25px")
                .style("width", "170px")
                .style("overflow", "hidden")
                .style("position", "relative")
                .append("div")
                    .style("padding-top", "2px")
                    .style("position", "absolute")
                    .style("left", "0px")
                    .style("top", "4px")
                    .style("text-align", "right")
                    .style("width", "170px")
                    .html(name);
                
    div.append("div")	
				.style("display", "inline-block")
				.style("height", "25px")
                .style("width", "15px")
                .style("overflow", "hidden");
    
	var bars = div.append("div")
			.style("display", "inline-block")
			.style("text-align", "left")
            .style("width", (startPadding+itemSize+10) + "px");
	 
    bars.append("div")
			.style("width", startPadding + "px")
            .style("display", "inline-block")
			.style("height", "25px");
  
	var innerDiv = bars.append("div")
			.attr("class", "progress")
            .style("display", "inline-block")
			.style("background", elapsedColor)
			.style("width", itemSize + "px")	  
			.style("height", "25px")
            .style("position", "relative")
            .append("div").style('overflow', 'hidden');

    var dt1Position = "3px";
    var dt2Position = (itemSize-25)+"px";
    if(itemSize < 40){
        dt1Position = "-25px";
        dt2Position = (itemSize+5)+"px";
    }
    else if(itemSize < 70){        
        dt2Position = (itemSize+5)+"px";
    }
    
    innerDiv.append("div")
        .style("font", "10px Helvetica Neue")
        .style("position", "absolute")
        .style("left", dt1Position)
        .style("top", "5px")
        .style("color", innerTextColor)
        .html(getDateDayAndMonth(startDate));
    
    innerDiv.append("div")
        .style("font", "10px Helvetica Neue")
        .style("position", "absolute")
        .style("left", dt2Position)
        .style("top", "5px")
        .style("color", innerTextColor)
        .html(getDateDayAndMonth(endDate));
    
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