/******************************/
/* CREDITS        			  */
/******************************/
            	
/* after an example from http://bl.ocks.org/mbostock/7607535 */

/******************/
/* SETTINGS       */
/******************/

//shapes as overlayed transparent svg symbol not to destroy circle-packing layout
var shapeAsOverlay = true;

// circle packing
var margin = 10,
    outerDiameter = 960,
    innerDiameter = outerDiameter - margin - margin;


/*****************************************/
/* GLOBAL VARS & ADAPTED PLUGINS         */
/*****************************************/

var x = d3.scale.linear()
    .range([0, innerDiameter]);

var y = d3.scale.linear()
    .range([0, innerDiameter]);

var color = d3.scale.linear()
    .domain([-1, 5])
    .range(["hsl(152,0%,80%)", "hsl(228,0%,40%)"])
    .interpolate(d3.interpolateHcl);

var pack = d3.layout.pack()
    .padding(2)
    .size([innerDiameter, innerDiameter])
    //.value(function(d) { return d.size; })
	.value(function(d) { return "100"; })
	

/*****************************************/
/* UPDATE FUNCTION                       */
/*****************************************/

loadCirclePackingZoomable = function(error, root) {
	
	// override global settings
	setLabelingImpl("simple"); // complex labeling not yet fully implemented
	
     var focus = root,
     	 nodes = pack.nodes(root);
	  
 	 var symbolFunction = d3.svg.symbol()
						.size(2000)
						.type(function (d) {return d.shape_d3_name})
					;

 	var nodeEnter = vis.append("g").selectAll("circle")
     .data(nodes)
     .enter().append("circle")
     .attr("class", function(d) { return d.parent ? d.children ? "node" : "node node--leaf" : "node node--root"; })
     .classed("packedCircle",true)
     .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
     .attr("r", function(d) { return d.r; })
     .style("fill", function(d) { return d.children ? color(d.depth) : "white"; })
     //.style("fill", function(d) { return d.color_rgb_hex_combined; })
     .on("click", function(d) { 
   	  //return zoom(focus == d ? root : d); 							    // original
   	  //return zoom(focus == d ? root : d.children ? d: d.parent); 		    // don't zoom into leaf nodes.
   	  return zoom((focus == d.parent && !d.children) ? d.parent.parent : 
   				  (focus == d || !d.children) ?  d.parent : d );            // don't zoom into leaf nodes and zoom to parent when clicking on the same level
      })
	  .on("mouseover", function(d) {
	  		
			// highlight this node
			//d3.select(this).attr("class", "node highlighted");
			
			// highlight identical nodes	
			vis.selectAll(".node").filter(function(node) {
			    return node.uri === d.uri;
           })
			.classed("highlighted identical", true);
			
			/*
			vis.selectAll(".node--leaf").filter(function(node) {
			    return node.uri === d.uri;
           })
			.attr("class", "node node--leaf");*/
			
			// extend label to full label
			//d3.select(this).selectAll("text").text(function(d){return d.full_label});		
				
	  })
	  .on("mouseout", function(d) {
			//d3.select(this).attr("class", "node");
			vis.selectAll(".node")
			.classed("highlighted identical", false);
			/*
			vis.selectAll(".node--leaf").attr("class", "node node--leaf");*/
			
	  })
	  ;

	// when using transparent shape overlay
 	if (shapeAsOverlay) {
		var symbolContainer = vis.append("g").selectAll(".svgSymbolContainer")
		.data(nodes).enter()
		.append("g")
		.filter(function(d) { return d.shape_d3_name != "circle" && d.shape_d3_name !=  undefined; })
		.attr("class","svgSymbolContainer")
		.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
		;
	 
	 	symbolContainer.avmShapedWithUseSVG()
	 	.avmShapedWithUseSVGUpdateWithoutSelectingSymbol()
		//.attr("transform", function(d) { return "scale(" + d.width/SYMBOL_WIDTH +  ")"; })
		.attr("transform", function(d) { return "scale(5)"; })
		.style("fill-opacity","0.33")
		.style("stroke-opacity","0")
		.style("pointer-events","none")
		;
 	}
	 
 
 // text label new
 if (settings.layout.labeling == "simple") {
		
		vis.append("g").selectAll(".nodeLabelContainer")
		  .data(nodes).enter()
		    .append("g")
			.filter(function(d) { return d.labels != null ;})
			.classed("nodeLabelContainer", true)
			.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
			.style("fill-opacity", function(d) { return d.parent === root ? 1 : 0; })
			.style("display", function(d) { return d.parent === root ? null : "none"; })
			.selectAll(".nodeLabelText")
			.data(function(d) { return d.labels }).enter()
			  .avmLabeledCT(true) // TODO make dependent on whether d has children, e.g.: function(d) { return d.children || d._children ; } , however here we are alread in d.labels!
			  .classed("nodeLabelText label", true)
			  .style("visibility", "visible") // because this is set to hidden in avmLabeledCT, which is reused here atm
			  ;

	}
 
	// tooltip
	nodeEnter.avmTitled();
	  

 d3.select(window)
     .on("click", function() { zoom(root); });
 
 d3.select(window)
    .on("load", function() { zoom(root); });

 function zoom(d, i) {
 	
   var focus0 = focus;
   focus = d;

   var k = innerDiameter / d.r / 2;
   x.domain([d.x - d.r, d.x + d.r]);
   y.domain([d.y - d.r, d.y + d.r]);
   d3.event.stopPropagation();

   var transition = d3.selectAll("path,circle,g.nodeLabelContainer,.svgSymbolContainer").transition()
       .duration(d3.event.altKey ? 7500 : 750)
       .attr("transform", function(d) { return "translate(" + x(d.x) + "," + y(d.y) + ")"; });

   transition.filter("circle")
       .attr("r", function(d) { return k * d.r; });

   transition.filter(".nodeLabelContainer")
     .filter(function(d) { return d.parent === focus || d.parent === focus0; })
       .style("fill-opacity", function(d) { return d.parent === focus ? 1 : 0; })
       .each("start", function(d) { if (d.parent === focus) this.style.display = "inline"; })
       .each("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
   
	// when using transparent shape overlay
 	if (shapeAsOverlay) {
	    transition.filter(".svgSymbolContainer")
	    .filter(function(d) { return d.parent === focus || d.parent === focus0; })
	      .style("fill-opacity", function(d) { return d.parent === focus ? 1 : 0; })
	      .each("start", function(d) { if (d.parent === focus) this.style.display = "inline"; })
	      .each("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
 	}
	
 }

}