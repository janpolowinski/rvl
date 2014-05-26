/*************************************************/
/* own "plugins" to handle AVM based on D3       */
/*************************************************/
    
(function() {
	
	 /* highlighting */
	  d3.selection.prototype.highlight = function(node) {
	    return this.classed('highlighted', true);
	  };
	  
	  /* highlighting identical elements (same URI attached) */
	  d3.selection.prototype.avmHighlightIdentical = function(duri) {

		  return this.filter(function(node) {
			    //alert(duri + " =? "+  node.uri); // TODO seems that evtl. to many nodes are compared here!
			    return node.uri === duri;
	      })
		  .highlight().classed("identical", true);
		  
	  };
	  
	  /* labeling with title-tag (tooltip) */
	  d3.selection.prototype.avmTitled = function() {
		  return this.append("svg:title").text(function(d) { return d.full_label; });
	  };
		    
	  /* labeling with SVG text CT */
	  d3.selection.prototype.avmLabeledCT = function() {
		  return this.append("svg:text")
			.attr("class", "nodeLabel")
	 		.attr("x", function(d) { return d.children || d._children ? -10 : 10; })
	     	.attr("dy", ".35em")
			.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
		    .text(function(d) { return d.label; })
			.style("visibility", "hidden")
			;
	  };
	  
	  /* labeling with SVG text FDG */
	  d3.selection.prototype.avmLabeledFDG = function() {
		  return this.append("svg:text")
			.attr("class", "nodeLabel")
	 		.attr("dx", 10)
	     	.attr("dy", 0)
			//.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
		    .text(function(d) { return d.label; })
			//.style("visibility", "hidden")
			;
	  };
	  
	  /* labeling with SVG text FDG 2 */
	  d3.selection.prototype.avmLabeledFDG2 = function(labelShapeSize) {
		  return this.append("svg:text").
		  	attr("class", "nodeLabel") // TODO class was label ... clean up various label CSS classes
			.attr("x", function(d){return labelShapeSize/2;})  // dx is relative positiong, x is absolute
			.attr("y", function(d){return labelShapeSize/2;})
		    .text(function(d){return d.label; })
			.style("text-anchor", 
				function(d){
					var labelPosition = d.label_position;
					if(labelPosition.indexOf("Right") != -1){
						return "start" ;
					} else if(labelPosition.indexOf("Left") != -1){
						return "end" ;
					}
					else return "middle" ;
				})
			;
	  };
	  
	  
	  /* labeling with HTML text */
	  d3.selection.prototype.avmLabeledHTML = function(nodeShapeSize) {
		  
		  var containerDiv =  this.append("div")
		    //.attr("class","labelContainer topRight")
			.attr("class", function(d){ return "labelContainer " + d.label_position;})
			.style("text-align","center")
			.style("height",nodeShapeSize +"px")
			.style("width",nodeShapeSize +"px");
		  
		  containerDiv.append("div")
		  			.attr("class", "htmlTextLabel")
					//.style("width", nodeShapeSize + "px") // does only work well for xCenter position
					.html(function(d){ return d.label;});
		  
		  return containerDiv;
	  };
	  
	  /* labeling with SVG text */
	  d3.selection.prototype.avmLabeledSVG = function(nodeShapeSize,labelShapeSize) {
		  
		  var containerDiv = this.append("div")
			.attr("class", function(d){ return "labelContainer " + d.label_position ;})
			.style("height",nodeShapeSize +"px")
			.style("width",nodeShapeSize +"px");
			
		var labelContainerSVG = containerDiv.append("svg")
			.attr("class", "svgLabelText")
			//.attr("width",100 +"px")
			//.attr("height",25 +"px")
			.attr("width",labelShapeSize +"px") // are these sizes sensible?
			.attr("height",labelShapeSize +"px");
			//.style("margin-left",-100 + "px")
			//.style("margin-bottom",-0.2*nodeShapeSize + "px");
										
			// The label and a copy of the label as shadow for better readability
			labelContainerSVG.avmLabeledFDG2(labelShapeSize).attr("class", "nodeLabelShadow");
			labelContainerSVG.avmLabeledFDG2(labelShapeSize);
		  
		    return containerDiv;
	  };
	  
	  /* labeling with SVG icon  */
	  d3.selection.prototype.avmLabeledSVGIcon = function(nodeShapeSize,labelShapeSize) {
		  
		var containerDiv = this.append("div")
			.attr("class", function(d){ return "labelContainer " + d.label_position;})
			.style("height",nodeShapeSize +"px")
			.style("width",nodeShapeSize +"px");
			
		containerDiv.append("svg")
			.attr("class", "svgLabelIcon")
			.attr("width",labelShapeSize +"px")
			.attr("height",labelShapeSize +"px")
			.style("margin",-labelShapeSize/2.75 + "px")
			//.avmLabeledWithCircle(labelShapeSize);
			.append("path")
	  			.attr("class", "label")
	 			.attr("d", avmDefaultSizeLabelSymbolFunction)
	 			.style("fill", "red")
				.attr("transform", function(d){ return "translate(" + labelShapeSize/2 + "," + labelShapeSize/2 + ")";});
		  
		  return containerDiv;
	  };

	   
	  /* label aligned at the connector path */
	  d3.selection.enter.prototype.avmLabeledConnectorAligned = function() {
		  
		  var text =  this.append("svg:text")
			.attr("class", "path_label");
		  
		  text.append("svg:textPath")
			.attr("startOffset", "50%")
			.attr("text-anchor", "middle")
			.attr("xlink:href", function(d){
			      	return "#" + createIDForLink(d);
			  	}).style("fill", "#fff")
			.style("font-family", "Arial")
			.text(function (d) {
	           		 return d.label;
	        		}
				);
		  
		  return text;
	  };
	  
	  /* label aligned at the connector path */ // TODO merge
	  d3.selection.enter.prototype.avmLabeledConnectorAligned2 = function() {
		  
		  var text =  this.append("svg:text")
			.attr("class", "path_label");
		  
		  text.append("svg:textPath")
			.attr("startOffset", "50%")
			.attr("text-anchor", "middle")
			.attr("xlink:href",	function(d){
			      	return "#" + createIDForLink2(d);
			  	}).style("fill", "#fff")
			.style("font-family", "Arial")
			.text(function (d) {
	           		 return d.target.connector_label;
	        		}
				);
		  
		  return text;
	  };
	  
	  /* setting the icon label as a circle */
	  d3.selection.prototype.avmLabeledWithCircle = function(labelShapeSize) {
		 	return this.append("svg:circle")
			.attr("r", labelShapeSize/2)
			.attr("cx",labelShapeSize/2)
			.attr("cy",labelShapeSize/2)
			.style("fill", function(d) { return d.color_rgb_hex_combined; });
	  };
	  
	  /* setting the shape by reusing an SVG symbol */ // TODO: this also sets color and node-class at the moment
	  d3.selection.prototype.avmShapedWithUseSVG = function() {
		 	return this.append("use")
			  .attr("xlink:href", function(d) { return "../../svg/symbols.svg#" + d.shape_d3_name; })
			  //.attr("xlink:href", function(d) { return "../../svg/symbols.svg#clock"; })
	   	 	  .attr("class", function(d) { return "node svgSymbol"; })
		      .style("fill", function(d) { return d.color_rgb_hex_combined; })
		     ;
	  };
	  
	  /* setting the shape by reusing an SVG symbol */ // TODO: this also sets color at the moment; merge with above
	  d3.selection.prototype.avmShapedWithUseSVGPure = function() {
		 	return this.append("use")
			  .attr("xlink:href", function(d) { return "../../svg/symbols.svg#" + d.shape_d3_name; })
			  //.attr("xlink:href", function(d) { return "../../svg/symbols.svg#clock"; })
	   	 	  .attr("class", "svgSymbol")
		      .style("fill", function(d) { return d.color_rgb_hex_combined; })
		     ;
	  };
	  
	  /* setting the shape by a path object */ // TODO: this also sets color and class node at the moment ; seems broken
	  d3.selection.prototype.avmShapedWithPath = function(symbolFunction) {
		 	return this.append("path")
		 	    //.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
		 		//.attr("d", d3.svg.symbol());
			   .attr("class", function(d) { return "node";})
    		   .attr("d", symbolFunction)
    		    .style("fill", function(d) { return d.color_rgb_hex_combined; })
		     ;
	  };
	  
	  
	  /* // replaced by path from symbol factory below
      var circle = nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      //.style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });
	  .style("fill", function(d) { return d.color_rgb_hex_combined });  */
	 
	 /* // shapes from shape generator
	 	var symbol = nodeEnter.append("path")
    //.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
    //.attr("d", d3.svg.symbol());
	  .attr("class", function(d) { return "node" })
	 .attr("d", symbolFunction)
	 .style("fill", function(d) { return d.color_rgb_hex_combined; })
	 ;	*/
	  
	  /* setting the shape by a path object */ // TODO: this also sets color and class node at the moment ; seems broken
	  d3.selection.prototype.avmProvideMarkerCollection = function() {

	  var defs = this.append("svg:defs");
	  
	  defs.append("svg:defs").selectAll("marker")
	      .data(["arrow", "uml_generalization_arrow", "arrow_small_triangle"])
	      .enter().append("svg:marker")
	      .attr("id", String)
	      .attr("viewBox", "0 -5 10 10")
	      .attr("refX", refX)
	      .attr("refY", refY)
	      .attr("markerWidth", markerWidth)
	      .attr("markerHeight", markerHeight)
	      .attr("orient", "auto")
	      .append("svg:path")
		    .attr("markerUnits","userSpaceOnUse") /* seems to have no effect */
	        .attr("d", "M0,-5L10,0L0,5L0,-5Z");
	  
	  defs.select("#arrow_small_triangle").select("path").attr("d", "M0,-3L6,0L0,3L0,-3Z");
	  
	  return defs;
	};
	
	
	  
	})();

/**************************/
/* ADAPTED PLUGINS        */
/**************************/	

var avmDefaultSizeSymbolFunction = d3.svg.symbol()
	.size(300) // square pixels!
	.type(function (d) {return d.shape_d3_name;})
	//.type(function (d) {return "diamond"})
	;

var avmDefaultSizeLabelSymbolFunction = d3.svg.symbol()
	.size(250)
	.type(function (d) {return d.label_shape_d3_name;})
	;


/**************************/
/* NORMAL FUNCTIONS       */
/**************************/	

/* helper function to calculate the position of labels at the center of arcs */ 
 function calculateTranslationToArcCenter(d) {
	
	//alert("dsourcex" + d.source.x);
	
	var x = 100, y = 110;
	
 	var manualArcLabelPositionCorrection = 27, // works well for stroke widths between 5 and 20
 		iconsize = 0; // TODO: remove global 
 		
		// switch direction if not in positive x-direction
		var sx,sy,tx,ty;
		if (d.source.x <= d.target.x ) {
			sx = d.source.x;
			sy = d.source.y;
			tx = d.target.x;
			ty = d.target.y;
		} else {
			sx = d.target.x;
			sy = d.target.y;
			tx = d.source.x;
			ty = d.source.y;
		}
		
		var dx = tx - sx,
         dy = (ty - sy),
         dr = Math.sqrt(dx * dx + dy * dy);
		
		var xmid = sx + (dx - iconsize) / 2;
	    var ymid = sy + (dy - iconsize) / 2;
		
		var c = manualArcLabelPositionCorrection;
		
		var m = -(dx/dy);
		
		var b = Math.sqrt((c*c)/(m*m+1));
		var a = Math.sqrt((c*c)-b*b); 
		
		// hack: strech for larger node distances
		b = b*0.005*dr;
		a = a*0.005*dr;
		
		//mInfo.html("m = " + m + "<br> b = " + b + "<br>a = " + a);
		
		// adapt position differently depending on 4 quadrants
		//var x,y;
		if (d.source.x <= d.target.x) {
			if (m > 0) {
				x = xmid - b;
				y = ymid - a;
			}
			else if (m <= 0) {
				x = xmid + b;
				y = ymid - a;
			}
		}
		else {
			if (m < 0) {
				x = xmid - b;
				y = ymid + a;
			}
			else if (m >= 0) {
				x = xmid + b;
				y = ymid + a;
			}
		} 

   return "translate(" + x + "," + y + ")";
 }
 
 /* helper function to calculate the position of labels at the center of lines */ 
 function calculateTranslationToLineCenter(d) {

	var iconsize = 0;
	var x = d.source.x + (d.target.x - d.source.x - iconsize) / 2;
    var y = d.source.y + (d.target.y - d.source.y - iconsize) / 2;
	
    return "translate(" + x + "," + y + ")";
 }
 
 /* create a unique ID for a link */ 
 function createIDForLink(d) {
	return d.source.index + "_" + d.target.index;
	// return "1_0";
 }
 
 // TODO merge?
 function createIDForLink2(d) {
		return d.source.id + "_" + d.target.id;
		// return "1_0";
 }
 
/* toggle nodes in collapsible tree */ 
 
// Toggle children.
function toggle(d) {
	if (d.children) {
		d._children = d.children;
		d.children = null;
	} else {
		d.children = d._children;
		d._children = null;
	}
}
	
// toggle all	
 function toggleAll(d) {
	    if (d.children) {
	      d.children.forEach(toggleAll);
	      toggle(d);
	    }
	  }


 /* highlighting, activating */
 
 function defaultStopHighlighting(d) {
 	
 	// remove highlighting
	d3.selectAll(".link").classed("highlighted identical", false);
	d3.selectAll(".node").classed("highlighted identical", false);
	

	// shorten labels // TODO: this is no highlighting stuff
	d3.select(this).selectAll("text").
		text(function(d){return d.label;});
 }
 
 
 