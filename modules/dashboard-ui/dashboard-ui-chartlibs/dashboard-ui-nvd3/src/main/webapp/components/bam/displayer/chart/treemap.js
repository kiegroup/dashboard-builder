var loadTreemap = (function(container, jsonDataset, width, height, filterCallback, removeFilterCallback, clearFilterCallback){	
	var currentZoomed = null;
	var previousZoomed = null;
	var currentRect = null;
	var currentRectOriginalColor = null;
    var chartWidth = width;
    var chartHeight = height;
    var xscale = d3.scale.linear().range([0, chartWidth]);
    var yscale = d3.scale.linear().range([0, chartHeight]);
    var color = d3.scale.category10();
    var headerHeight = 20;
    var headerColor = "#555555";
	var selectedHeaderColor = "#000000";
    var transitionDuration = 500;
    var root;
    var node;

	var treemap = d3.layout.treemap()
			.round(false)
			.size([chartWidth, chartHeight])
			.sticky(true)
			.value(function(d) {
				return 1;
			});
		
		var chart = d3.select(container)
			.append("svg:svg")
			.attr("width", chartWidth)
			.attr("height", chartHeight)
			.append("svg:g");
	
	
	loadChart(container,width,height,jsonDataset);	
		
	function loadChart(container, data){
	
		data = jsonDataset;
        node = root = data;
        var nodes = treemap.nodes(root);

        var children = nodes.filter(function(d) {
            return !d.children;
        });
        var parents = nodes.filter(function(d) {
            return d.children;
        });

        // create parent cells
        var parentCells = chart.selectAll("g.cell.parent")
            .data(parents, function(d) {
                return "p-" + d.name;
            });
        var parentEnterTransition = parentCells.enter()
            .append("g")
            .attr("class", "cell parent")
            .on("click", function(d) {
                zoom(d,this);
            });
        parentEnterTransition.append("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", function(d) { return d.dy; })
            .style("fill", function(d){ return headerColor; });
        
		parentEnterTransition.append('text')
            .attr("class", "label")
            .attr("transform", "translate(3, 13)")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .text(function(d) {
                return d.name;
            });		
		
        // update transition
        var parentUpdateTransition = parentCells.transition().duration(transitionDuration);
        parentUpdateTransition.select(".cell")
            .attr("transform", function(d) {
                return "translate(" + d.dx + "," + d.y + ")";
            });
        parentUpdateTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", function(d) { return d.dy; })
            .style("fill", headerColor);
				
		parentUpdateTransition.select(".label")
            .attr("transform", "translate(3, 13)")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", headerHeight)
            .text(function(d) {
                return d.name;
            });
			
        // remove transition
        parentCells.exit()
            .remove();

        // create children cells
        var childrenCells = chart.selectAll("g.cell.child")
            .data(children, function(d) {
                return "c-" + d.name + (d.parent ? d.parent.name : '');
            });
        // enter transition
		var childEnterTransition = childrenCells.enter()
            .append("g")
            .attr("class", "cell child")
            .on("click", function(d) {
                zoom(d,this);
            })
            .append("svg")
            .attr("class", "clip");		
			
        childEnterTransition.append("rect")
            .classed("background", true)
            .style("fill", function(d) {
                return color(d.parent.name);
            });

		childEnterTransition.append('text')
			.attr("class", "label")
			.attr('x', function(d) {
				return d.dx / 2;
			})
			.attr('y', function(d) {
				return d.dy / 2;
			})
			.style("color", function(d) {
				return idealTextColor(color(d.parent.name));
			})
			.attr("dy", ".35em")
			.attr("text-anchor", "middle")
			.style("display", "")
			.text(function(d) {
				return d.name;
			});

        // update transition
        var childUpdateTransition = childrenCells.transition().duration(transitionDuration);
        childUpdateTransition.select(".cell")
            .attr("transform", function(d) {
                return "translate(" + d.x  + "," + d.y + ")";
            });
        childUpdateTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, d.dx);
            })
            .attr("height", function(d) {
                return d.dy;
            })
            .style("fill", function(d) {
                return color(d.parent.name);
            });
			
		childUpdateTransition.select(".label")
			.attr('x', function(d) {
				return d.dx / 2;
			})
			.attr('y', function(d) {
				return d.dy / 2;
			})
			.attr("dy", ".35em")
			.attr("text-anchor", "middle")
			.style("display", "")
			.text(function(d) {
				return d.name;
			});

		// exit transition
        childrenCells.exit()
            .remove();

        zoom(node,this);
    }	
	

    //and another one
    function textHeight(d) {
        var ky = chartHeight / d.dy;
        yscale.domain([d.y, d.y + d.dy]);
        return (ky * d.dy) / headerHeight;
    }


    function getRGBComponents (color) {
        var r = color.substring(1, 3);
        var g = color.substring(3, 5);
        var b = color.substring(5, 7);
        return {
            R: parseInt(r, 16),
            G: parseInt(g, 16),
            B: parseInt(b, 16)
        };
    }


    function idealTextColor (bgColor) {
        var nThreshold = 105;
        var components = getRGBComponents(bgColor);
        var bgDelta = (components.R * 0.299) + (components.G * 0.587) + (components.B * 0.114);
        return ((255 - bgDelta) < nThreshold) ? "#000000" : "#ffffff";
    }	
	
    function zoom(d, m) {
		if(currentZoomed && d.name == currentZoomed.name){
			d = root;			
			clearFilterCallback();
		}
		else{
			if(!!currentZoomed && currentZoomed != root){
				removeFilterCallback(currentZoomed);				
			}			
			if(d != root){			
                filterCallback(d);
			}			
		}
		previousZoomed = currentZoomed;
		currentZoomed = d;
				
		var selectedItem = d;
		if(d.parent && d.parent != root){
			d = d.parent;
		}		
		
		treemap
            .padding([headerHeight/(chartHeight/d.dy), 8, 8, 8])
            .nodes(d);

        // moving the next two lines above treemap layout messes up padding of zoom result
        var kx = chartWidth  / d.dx;
        var ky = chartHeight / d.dy;
        var level = d;

        xscale.domain([d.x, d.x + d.dx]);
        yscale.domain([d.y, d.y + d.dy]);

        if (node != level) {
            chart.selectAll(".cell.child .label")
                  .style("display", "")
				  .style("color", function(d) {
                            return idealTextColor(color(d.parent.name));
                        });
        }

        var zoomTransition = chart.selectAll("g.cell").transition().duration(transitionDuration)
            .attr("transform", function(d) {
                return "translate(" + xscale(d.x) + "," + yscale(d.y) + ")";
            })
            .each("end", function(d, i) {
                if (!i && (level !== self.root)) {
					chart.selectAll(".cell.child")
						.filter(function(d) {
							return d.parent === self.node; // only get the children for selected group
						})
						.select(".label")
						.style("display", "")
						.style("fill", function(d) {
							return idealTextColor(color(d.parent.name));
						});
                }
            });
				
		zoomTransition.select(".label")
			.attr("width", function(d) {
				return Math.max(0.01, (kx * d.dx));
			})
			.attr("height", function(d) {
				return d.children ? headerHeight : Math.max(0.01, (ky * d.dy));
			})
			.text(function(d) {
				return d.name;
			});
		zoomTransition.select(".child .label")
			.attr("x", function(d) {
				return kx * d.dx / 2;
			})
			.attr("y", function(d) {
				return ky * d.dy / 2;
			});
		
        // update the width/height of the rects
        zoomTransition.select("rect")
            .attr("width", function(d) {
                return Math.max(0.01, kx * d.dx);
            })
            .attr("height", function(d) {
                return d.children ? (ky*d.dy) : Math.max(0.01, ky * d.dy);
            })
            
        
		if(!!currentRect && !!previousZoomed && currentRect != document){
			if(!previousZoomed.children){
				d3.select(currentRect).select('rect')
					.style("opacity", 1);
			}			
			else{
				d3.select(currentRect).select('rect')
					.style("fill", headerColor);					
			}
		}
		if(m && currentRect != document && currentRect != m){
			currentRectOriginalColor = d3.select(m).select('rect').style('fill');
			if(!!d.parent == true)
			{
				if(!currentZoomed.children){
					d3.select(m).select('rect')
						.style("opacity", 0.5);
				}
				else{					
					d3.select(m).select('rect')
						.style('fill', selectedHeaderColor);
				}				
			}
		}		
		currentRect = m;		
		node = d;
		if(!d.parent){
			previousZoomed = null;
			currentZoomed = null;
		}

        if (d3.event) {
            d3.event.stopPropagation();
        }
    }
	
});