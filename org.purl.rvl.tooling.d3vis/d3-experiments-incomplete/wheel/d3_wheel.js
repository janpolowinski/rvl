// This component is based on the 
// Coffee Flavour Wheel by Jason Davies,
// http://www.jasondavies.com/coffee-wheel/
// License: http://www.jasondavies.com/coffee-wheel/LICENSE.txt

// Edited and prepared for the RVL ontowiki extension by: Jan Polowinski
// remarks: seems to be limited to a few levels at the moment
// instances are shown by default as leaves in the hierarchy (this is not part of the processed mappings)
// colour can not be actually mapped (just alternating at the moment)
// size is not used

var d3_wheel = {              
    
    makeVis : function (vis,force,property,rname,mappings,rawData){        
        var data = new Array();
        for(var i=0; i < rawData.length; i++){   
            if(rawData[i].props[property] !== undefined)
                data.push(rawData[i]);
        }
        var categories = new Array();
        for(var i=0; i < data.length; i++){   
            if(jQuery.inArray(data[i].props[property].value,categories) == -1)
                categories.push(data[i].props[property].value);            
        }
		
		// needed for alternating the color of wheel elements
		var nextColor;
		
        var tNodes = new Array();
		tNodes.push({"name" : "Books", "children" : new Array()});
        for(var i=0; i < categories.length; i++)
            tNodes[0].children.push({"name" : categories[i],"children" : new Array()});        
        for(var i=0; i < data.length; i++){   
            for(var j=0; j < tNodes[0].children.length; j++){
                if(tNodes[0].children[j].name == data[i].props[property].value){
					// alternate the color on the second level (should be the last level, actually)
					nextColor = (j%2 == 0) ? "#cffcff" : "#fccfcc" ;
                    tNodes[0].children[j]["children"].push({"name" : data[i].label, "colour" : nextColor});
                    break;
                }
            }
        }

		var width = 800,
		    height = width,
		    radius = width / 2,
		    x = d3.scale.linear().range([0, 2 * Math.PI]),
		    y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, radius]),
		    padding = 5,
		    duration = 1000;
		
		vis.attr("width", width + padding * 2)
		    .attr("height", height + padding * 2);
			
		var vis1 = 	vis
		  .append("g")
		  .attr("transform", "translate(" + [radius + padding , radius + padding ] + ")");
		

		var partition = d3.layout.partition()
		    .sort(null)
		    .value(function(d) { return 5.8 - d.depth; });
		
		var arc = d3.svg.arc()
		    .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
		    .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
		    .innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
		    .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });
		
		// access a JSON file for testing purposes	
		/*
		d3.json("http://localhost/ontowiki/wheel.json", function(json) {
		  json.x0 = 0;
		  json.y0 = 0;
		  update(root = json);
		});
		*/
		
		// tNodes.x0 = 0; tNodes.y0 = 0; // seems to be unnecessary
 		update(root = tNodes); // required!
	
		function update(source){		
		  var nodes = partition.nodes({children: root});
		
		  var path = vis1.selectAll("path").data(nodes);
		  path.enter().append("path")
		      .attr("id", function(d, i) { return "path-" + i; })
		      .attr("d", arc)
		      .attr("fill-rule", "evenodd")
		      .style("fill", colour)
		      .on("click", click);
		
		  var text = vis1.selectAll("text").data(nodes);
		  var textEnter = text.enter().append("text")
		      .style("fill-opacity", 1)
		      .style("fill", 
			    function(d) {
		        return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
		      	}
			  )
		      .attr("text-anchor", function(d) {
		        return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
		      })
		      .attr("dy", ".2em")
		      .attr("transform", function(d) {
		        var multiline = (d.name || "").split(" ").length > 1,
		            angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
		            rotate = angle + (multiline ? -.5 : 0);
		        return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
		      })
		      .on("click", click);
		  textEnter.append("tspan")
		      .attr("x", 0)
		      .text(function(d) { return d.depth ? d.name.split(" ")[0] : ""; });
		  textEnter.append("tspan")
		      .attr("x", 0)
		      .attr("dy", "1em")
		      .text(function(d) { return d.depth ? d.name.split(" ")[1] || "" : ""; });
		
		  function click(d) { 
		    path.transition()
		      .duration(duration)
		      .attrTween("d", arcTween(d));
		
		    // Somewhat of a hack as we rely on arcTween updating the scales.
		    text.style("visibility", function(e) {
		          return isParentOf(d, e) ? null : d3.select(this).style("visibility");
		        })
		      .transition()
		        .duration(duration)
		        .attrTween("text-anchor", function(d) {
		          return function() {
		            return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
		          };
		        })
		        .attrTween("transform", function(d) {
		          var multiline = (d.name || "").split(" ").length > 1;
		          return function() {
		            var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
		                rotate = angle + (multiline ? -.5 : 0);
		            return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
		          };
		        })
		        .style("fill-opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
		        .each("end", function(e) {
		          d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
		        });
				
		  }
		};
		
		function isParentOf(p, c) {
		  if (p === c) return true;
		  if (p.children) {
		    return p.children.some(function(d) {
		      return isParentOf(d, c);
		    });
		  }
		  return false;
		}
		
		function colour(d) {
		  if (d.children) {
		    // There is a maximum of two children!
		    var colours = d.children.map(colour),
		        a = d3.hsl(colours[0]),
		        b = d3.hsl(colours[1]);
		    // L*a*b* might be better here...
		    return d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);
		  }
		  return d.colour || "#fff";
		return "#ccc";
		}
		
		// Interpolate the scales!
		function arcTween(d) {
		  var my = maxY(d),
		      xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
		      yd = d3.interpolate(y.domain(), [d.y, my]),
		      yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
		  return function(d) {
		    return function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
		  };
		}
		
		function maxY(d) {
		  return d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;
		}
		
		// http://www.w3.org/WAI/ER/WD-AERT/#color-contrast
		function brightness(rgb) {
		  return rgb.r * .299 + rgb.g * .587 + rgb.b * .114;
		}

    
    },
    
    updatePositions : function (vis1){
    }    
}     

