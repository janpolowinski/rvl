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
	  // TODO so far the first text label is used when it is a text label, however when it's an icon label, this will fail
	  d3.selection.prototype.avmTitled = function() {
		  return this
		  .filter(function(d) { return d.labels != null && d.labels[0].type == "text_label" ; })
		  .append("svg:title").text(function(d) { return d.labels[0].text_value_full; });
	  };
		    
	  /* labeling with SVG text CT */
	  d3.selection.enter.prototype.avmLabeledCT = function(hasChildren) {

		  var text =  this
		  	.append("svg:text")
			.attr("class", "nodeLabel")
	 		.attr("x", function() { return hasChildren ? -10 : 10; })
	     	.attr("dy", ".35em")
			.attr("text-anchor", function() { return hasChildren ? "end" : "start"; })
		    .text(function(d) { return d.text_value; })
			.style("visibility", "hidden")
			;

		  return text;
	  };
	  
	  /* labeling with SVG text FDG */
	  d3.selection.enter.prototype.avmLabeledFDG = d3.selection.prototype.avmLabeledFDG = function() {
		  
		  var text = this
		  	.append("svg:text")
			.attr("class", "nodeLabel")
			//.attr("x","30").attr("y", "3em")
	 		.attr("dx", 10)
	     	.attr("dy", 0)
			//.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
		    //.text(function(d) { return d.text_value; })
			;
		  
		  return text;
	  };
	  
	  /* labeling with SVG text FDG */
	  // TODO duplicated code from avmLabeledFDG
	  d3.selection.prototype.avmLabeledFDGUpdate = function() {
		  
		  var text = this
		  	//.select("text")
		    .filter(function(d) { return d.text_value != null ; })
			//.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
		    .text(function(d) { return d.text_value; })
			//.style("visibility", "hidden")
			;
		  
		  return text;
	  };
	  
	  /* labeling with text and/or icon using "complex" labels from the labels-array */
	  d3.selection.prototype.avmLabeledComplex = function() { // TODO not yet updateable
		  
		 var 
			addHTMLTextLabel = true,
			addSVGTextLabel = true, 
			addSVGIconLabel = true;	
		  
		 	// SVG icon label in html div 
			if (addSVGIconLabel) {
				
				this
					.selectAll(".iconLabelContainer")
					.data(function(d) { return d.labels ; }).enter()
					.avmLabeledSVGIcon();
			}
   
			// SVG text label in html div (cropped in webkit, maybe create a much bigger SVG inside the div, or modify clipping) 
			if (addSVGTextLabel) {
			    
			    this
					.selectAll(".textSVGLabelContainer")
					.data(function(d) { return d.labels ; }).enter()
					.avmLabeledSVGText();
			}

			// HTML label
			if (addHTMLTextLabel) {
				
				this
					.selectAll(".textHTMLLabelContainer")
					.data(function(d) { return d.labels ; }).enter()
					.avmLabeledHTMLText();
			}

		  return this;
	  };
	  
	  /* labeling with SVG text FDG 2 */
	  d3.selection.prototype.avmLabeledFDG2 = function(labelShapeSize) {
		  return this.append("svg:text").
		  	attr("class", "nodeLabel") // TODO class was label ... clean up various label CSS classes
			.attr("x", function(d){return labelShapeSize/2;})  // dx is relative positioning, x is absolute
			.attr("y", function(d){return labelShapeSize/2;})
		    .text(function(d){return d.text_value; })
			.style("text-anchor", 
				function(d){
					var labelPosition = d.position;
					if(labelPosition.indexOf("Right") != -1){
						return "start" ;
					} else if(labelPosition.indexOf("Left") != -1){
						return "end" ;
					}
					else return "middle" ;
				})
			.style("fill", function(d){
				return d.color_rgb_hex_combined;
				})
			;
	  };
	  
	  /* add label positioning div */
	  d3.selection.enter.prototype.avmLabelPositioning = function() {
		  
		  return this.append("div")
			.attr("class", function(d){ return "labelContainer " + d.position ;})
			//.style("height", function(d){return d.parent.width + "px";})
			//.style("width", function(d){return d.parent.width + "px";})
			.style("height", function(d){return "100%";})
			.style("width", function(d){return "100%";})
			;
	  };
	  
	  
	  /* labeling with HTML text (version for enter-selection-array) */
	  d3.selection.enter.prototype.avmLabeledHTMLText = function() {
		  
		  // TODO: reuse avmLabelPositioning
		  var containerDiv =  this
		  	.append("div")
			.filter(function(d) { return d.type == "text_label" ;})
			.filter(function(d) { return d.position == "centerCenter" ;})
			.attr("class", function(d){ return "labelContainer textHTMLLabelContainer " + " " + d.position;})
			.style("text-align","center")
			.style("height", function(d){return "100%";}) // use the whole area defined by the labeling base 
			.style("width", function(d){return "100%";})
			;
		  
		  containerDiv.append("div")
		  			.attr("class", "htmlTextLabel")
		  			.style("color", function(d){
		  				var textColor = d.color_rgb_hex_combined;
		  				var bgColor = this.parentNode.parentNode.__data__.color_rgb_hex_combined;
		  				//var parentNode2 = this.parentNode.parentNode;
		  				//console.log(parentNode2.__data__.color_rgb_hex_combined); // TODO why does parentNode2.data() not work???
		  				if (textColor == null) textColor = calculateHighContrastColor(bgColor);
		  				return textColor;
		  				})
					//.style("width", NODE_SIZE + "px") // does only work well for xCenter position
					.html(function(d){ return d.text_value_full;});
		  
		  return containerDiv;
	  };
	  
	 
	  
	  /* labeling with SVG text (version for enter-selection-array) */
	  d3.selection.enter.prototype.avmLabeledSVGText = function() {
		  
		var containerDiv = this
			.avmLabelPositioning()
			.classed("textSVGLabelContainer",true);
			
		var labelContainerSVG = containerDiv
			.filter(function(d) { return d.type == "text_label" ;})
			.filter(function(d) { return d.position != "centerCenter" ;})
			.append("svg")
			.attr("class", "svgLabelText")
			.attr("width",0 +"px") // text needs to have width and height = 0 for correct positioning atm
			.attr("height",0 +"px")
			//.style("margin-left",-100 + "px")
			//.style("margin-bottom",-0.2*NODE_SIZE + "px");
			;
										
			// The label and a copy of the label as shadow for better readability
			//labelContainerSVG.avmLabeledFDG2(function(d){ return d.width; }).attr("class", "nodeLabelShadow");
			labelContainerSVG.avmLabeledFDG2(function(d){ return d.width; }).classed("label", true);
		  
		    return containerDiv;
	  };
	  
	  
	  /* labeling with SVG icon (version for enter-selection-array)  */
	  d3.selection.enter.prototype.avmLabeledSVGIcon = function() {
		  
		var containerDiv = this
				.avmLabelPositioning()
				.classed("iconLabelContainer",true);
			
		var innerSVG = containerDiv
			.filter(function(d) { return d.type === "icon_label" ;})
			.append("svg")
			.attr("class", "svgLabelIcon")
			.attr("width",function(d){ return d.width + "px"; })
			.attr("height",function(d){ return d.width + "px"; })
			.style("margin",function(d){ return -1*d.width*LABEL_ICON_SUPER_IMPOSITION_FAKTOR + "px"; })
			//.style("margin","0px")
			
			.append("svg:g") // translating here allows for using transform two times (for scale, translate. alternative could be use of matrix)
				.attr("transform", function(d){ return "translate(" + d.width/2 + "," + d.width/2 + ")";});
				//.attr("x", function(d){ return (d.width)/2;}) // somehow offers other results than translate
		        //.attr("y", function(d){ return (d.width)/2;})
			
				
			innerSVG.append("use")
			  .attr("xlink:href", function(d) { return BASE_PATH_SVG_FILE + d.shape_d3_name; })
			  //.attr("xlink:href", function(d) { return BASE_PATH_SVG_FILE + "clock"; })
	   	 	  .attr("class", function(d) { return "label svgSymbol"; })
		      .style("fill", function(d) { return d.color_rgb_hex_combined; })
		      .attr("transform", function(d) { return "scale(" + (d.width/SYMBOL_WIDTH) +  ")"; })
			  ;
		
		// OLD: path instead of symbol. advantage: shape can be calculated on square-pixels (area) 
//		.append("path")
//  		.attr("class", "label")
// 			.attr("d", avmDefaultSizeLabelSymbolFunction)
// 			.style("fill", "red")
//			.attr("transform", function(d){ return "translate(" + labelShapeSize/2 + "," + labelShapeSize/2 + ")";});
		
		
		  // trial: limited support for labeled labels up to 2 levels:

		  innerSVG
		    .filter(function(d) { return d.labels != null ;})
			.selectAll(".nodeLabelText")
			.data(function(d) { return d.labels; }).enter()
			// TODO: this is a hack to quickly allow text labels CENTER/RIGHT from icon labels
			.append("svg:g").attr("transform","translate(17,7.5)")
			.avmLabeledFDG().classed("nodeLabelText label iconLabelText", true)
			;
		
		  return containerDiv;
	  };
	   
	  
	  /* label aligned at the connector path  (enter-version) */
	  d3.selection.enter.prototype.avmLabeledConnectorAligned = function(labelText,id) {
		  
		  var text =  this.append("svg:text")
			.attr("class", "path_label");
		  
		  text.append("svg:textPath")
			.attr("startOffset", "50%")
			.attr("text-anchor", "middle")
			.attr("xlink:href",	id)
			.style("fill", "#fff")
			.style("font-family", "Georgia")
			.text(labelText);
		  
		  return text;
	  };
	  
	  /* setting the icon label as a circle */ // does this actually work? not in use at the moment!
	  d3.selection.prototype.avmLabeledWithCircle = function(labelShapeSize) {
		 	return this.append("svg:circle")
			.attr("r", labelShapeSize/2)
			.attr("cx",labelShapeSize/2)
			.attr("cy",labelShapeSize/2)
			.applyGraphicAttributesNonSpatial2SVG();
	  };
	  
	  /* setting the shape by reusing an SVG symbol */ 
	  d3.selection.prototype.avmShapedWithUseSVG = 
	  d3.selection.enter.prototype.avmShapedWithUseSVG = function() {
		 	return this.append("use")
		 	  .filter(function(d) { return null != d.shape_d3_name ;})
			  .attr("xlink:href", function(d) { return BASE_PATH_SVG_FILE + d.shape_d3_name; })
	   	 	  .attr("class", "svgSymbol")
		      .applyGraphicAttributesNonSpatial2SVG()
		     ;
	  };
	  
	  /* setting the shape by reusing an SVG symbol (without appending a new "use" object) */ 
	  // TODO duplicated code from avmShapedWithUseSVG
	  d3.selection.prototype.avmShapedWithUseSVGUpdate = function() {
		 	return this.select(".svgSymbol")
		 	  .filter(function(d) { return null != d.shape_d3_name ;})
			  .attr("xlink:href", function(d) { return BASE_PATH_SVG_FILE + d.shape_d3_name; })
	   	 	  .attr("class", "svgSymbol")
		      .applyGraphicAttributesNonSpatial2SVG()
		     ;
	  };
	  
	  /* setting the shape as a text based on text_value */
	  d3.selection.prototype.avmShapedWithText = function() { 	
		 	return this.append("svg:text")
			.attr("class", "textNode")
	 		.attr("dx", 10)
	     	.attr("dy", 0)
			;
	  };
	  
	  /* setting the shape as a text based on text_value */
	  // TODO duplicated code from avmShapedWithText
	  d3.selection.prototype.avmShapedWithTextUpdate = function() { 	
		 	return this.select(".textNode")
		 	.filter(function(d) { return null != d.shape_text_value ;})
		    .text(function(d) { return d.shape_text_value; })
			.applyGraphicAttributesNonSpatial2SVG()
			.filter(function (d) { return null != d.width;})
			.style("font-size", function (d) { return d.width + "px";})
			;
	  };
	  
	  /* setting the shape by a path object */ // TODO: this also sets color and class node at the moment ; seems broken
	  d3.selection.prototype.avmShapedWithPath = function(symbolFunction) {
		 	return this.append("path")
		 	    //.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
		 		//.attr("d", d3.svg.symbol());
			   .attr("class", function(d) { return "node";})
    		   .attr("d", symbolFunction)
			   .applyGraphicAttributesNonSpatial2SVG()
		     ;
	  };
	  
	  /* setting graphic attributes on SVG (except position, dimensions ... */
	  d3.selection.prototype.applyGraphicAttributesNonSpatial2SVG = function() {
		 	return this
    		   .style("fill", function(d) { return d.color_rgb_hex_combined; })
    		   //.transition().duration(1000)
    		   //.style("fill", function(d){return d.color_rgb_hex_combined ; })
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
	  
		  defs.selectAll("marker")
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
	.type(function (d) {return d.shape_d3_name;})
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
 
 /* prepare for force layout on update */
 var copyForceSettings = function (positionedObject, newObject) {
	   
 	newObject.index = positionedObject.index ; 
 	newObject.x = positionedObject.x ; 
 	newObject.y = positionedObject.y ; 
 	newObject.px = positionedObject.px ; 
 	newObject.py = positionedObject.py ; 
 	newObject.fixed = positionedObject.fixed ; 
 	newObject.weight = positionedObject.weight ; 
// 	
// 	from the docu at https://github.com/mbostock/d3/wiki/Force-Layout:
//     index - the zero-based index of the node within the nodes array.
//     x - the x-coordinate of the current node position.
//     y - the y-coordinate of the current node position.
//     px - the x-coordinate of the previous node position.
//     py - the y-coordinate of the previous node position.
//     fixed - a boolean indicating whether node position is locked.
//     weight - the node weight; the number of associated links.
	    return newObject;
	} 
 
 /* create a unique ID for a link */ 
 /* for link arrays referencing nodes by IDs, not index */ 
 function createIDForLink(d) {
	return d.source_uri + "_" + d.target_uri;
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
	
	//shorten labels // TODO: this is no highlighting stuff
	d3.selectAll(".node").selectAll("text.nodeLabel").
		text(function(d){return d.text_value;});
 }
 
 /* array helper */
 
 var containsArrayThisElement = function (arrayToCheck, element) {
	    var containedElement = null;
	    for (var i = 0, len = arrayToCheck.length; i < len; ++i) {
	        if (arrayToCheck[i].uri==element.uri) {
	        	containedElement = arrayToCheck[i];
	        }
		}
	    return containedElement;
 }
 
 
 /*
	var filterFunction = function (element) {
	    var contains = false;
	    
	    for (var i = 0, len = beta.length; i < len; ++i) {
	        if (beta[i].uri==element.uri) {
	            return true;
	        }
		}
	    
	    return false;
	} */

 
