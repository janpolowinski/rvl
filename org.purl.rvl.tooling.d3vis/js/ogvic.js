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
			.attr("x", function(d){return labelShapeSize/2})  // dx is relative positiong, x is absolute
			.attr("y", function(d){return labelShapeSize/2})
		    .text(function(d){return d.label })
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
			.attr("class", function(d){ return "labelContainer " + d.label_position})
			.style("text-align","center")
			.style("height",nodeShapeSize +"px")
			.style("width",nodeShapeSize +"px");
		  
		  containerDiv.append("div")
		  			.attr("class", "htmlTextLabel")
					//.style("width", nodeShapeSize + "px") // does only work well for xCenter position
					.html(function(d){ return d.label});
		  
		  return containerDiv;
	  };

	  
	  /* setting the shape by reusing an SVG symbol */ // TODO: this also sets color and node-class at the moment
	  d3.selection.prototype.avmShapedWithUseSVG = function() {
		 	return this.append("use")
			  .attr("xlink:href", function(d) { return "../../svg/symbols.svg#" + d.shape_d3_name; })
			  //.attr("xlink:href", function(d) { return "../../svg/symbols.svg#clock"; })
	   	 	  .attr("class", function(d) { return "node svgSymbol" })
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
			   .attr("class", function(d) { return "node" })
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

	  
	})();



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