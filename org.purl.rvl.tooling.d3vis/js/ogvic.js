/* own "plugins" to handle AVM based on D3 */    
    
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
		    
	  /* labeling with SVG text */
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
	  
	  /* setting the shape by reusing an SVG symbol */ // TODO: this also sets color and node-class at the moment
	  d3.selection.prototype.avmShapedWithUseSVG = function() {
		 	return this.append("use")
			  .attr("xlink:href", function(d) { return "../../svg/symbols.svg#" + d.shape_d3_name; })
			  //.attr("xlink:href", function(d) { return "../../svg/symbols.svg#clock"; })
	   	 	  .attr("class", function(d) { return "node svgSymbol" })
		      .style("fill", function(d) { return d.color_rgb_hex_combined; })
		     ;
	  };

	  
	})();