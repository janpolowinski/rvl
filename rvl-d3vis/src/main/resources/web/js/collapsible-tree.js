/******************************/
/* CREDITS        			  */
/******************************/
            	
/* after an example from http://bl.ocks.org/mbostock/4339083 */


/******************/
/* SETTINGS       */
/******************/

// collapsible tree
var w = width - m[1] - m[3],
    h = height - m[0] - m[2],
    i = 0,
    root
    ;
    
var alignedConnectorLabeling = false;
    
//force-directed-graph
var lineWidth = 7,
	markerWidth = 3,
    markerHeight = 3,
    cRadius = 10,
	refX = cRadius + (markerWidth * 1.3) + 0.155 * cRadius, /* works well for stroke widths between 5 and 20 */
	// ALT refX = cRadius + (markerWidth * 1.5), 
	// ALT3 refX = cRadius + (markerWidth * 2),
	// ALT1: refX = cRadius + markerWidth * Math.sqrt(lineWidth) ,
	refY = - (cRadius*cRadius) / 375,  /* works well for stroke widths between 5 and 20 */
	// ALT1: refY = -Math.sqrt(cRadius),
	// ALT2: refY = -Math.sqrt(cRadius) * 0.8 + 20/lineWidth, /* works well for stroke width 7px */
    drSub = cRadius + refY, /* works well for stroke widths between 5 and 20 */
	ICONSIZE = 0 // TODO does not actually work. hack!
	;	

/*****************************************/
/* GLOBAL VARS & ADAPTED PLUGINS         */
/*****************************************/

var tree = d3.layout.tree()
    .size([h, w]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });


/*****************************************/
/* LOAD AND UPDATE FUNCTIONS             */
/*****************************************/

loadCollapsibleTree = function(error, json) {	
	
	// override global settings
	setLabelingImpl("simple"); // complex labeling not yet fully implemented
	
	root = json;
	root.x0 = h / 2;
	root.y0 = 0;

	updateCollapsibleTree(root);
}

updateCollapsibleTree = function(source) {				
		
  var duration = d3.event && d3.event.altKey ? 5000 : 500;

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse();

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { d.y = d.depth * 180; });

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });
	  

	function activate(d, active) {
		d.active = active;
		d.parent && activate(d.parent, active);
	}
	
	function activateChildren(d, active) {
		d.active = active;
		d.children && d.children.forEach(function(child) {
			activate(child, active);
			});
	}


  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) { toggle(d); updateCollapsibleTree(d); })
	  .on("mouseover", function(d) {
	  	
		    var thisNode = d3.select(this);
			var nodesToFilter = vis.selectAll(".node");
			var linksToFilter = vis.selectAll(".linking_connector");
	  		
			// highlight this node
			thisNode.highlight();

			// highlight identical nodes	
			nodesToFilter.avmHighlightIdentical(d.uri);

			// highlight parent and children nodes and the path
			
			activate(d, true);
			activateChildren(d, true);

			nodesToFilter.filter(function(node) {
			    return node.active;
            }).highlight();
			
			linksToFilter.filter(function(d) { 
				return d.target.active;
			}).highlight();
				
	  })
	  .on("mouseout", function(d) {
		    defaultStopHighlighting(d);
			activate(d, false);
			activateChildren(d, false);
	  });


		// shapes as reused svg-groups
		var symbol = nodeEnter.avmShapedWithUseSVG()		      
			//.attr("width", "200 px").attr("height", "200 px") // does not seem to work (Firefox at least)
			;
	       
		nodeEnter.select("g.scaleGroup")
		   .attr("transform", function(d) { return "scale(" + d.width/SYMBOL_WIDTH +  ")"; })
	       .avmShapedWithUseSVGUpdateWithoutSelectingSymbol()
	       ;
		
		//var symbol = nodeEnter.avmShapedWithPath(avmDefaultSizeSymbolFunction);
		
		if (settings.layout.labeling == "complex") {
	 
 			 // complex labeling
			 
			 var labelContainerContainer = d3
				.select("#labelContainerSpace")
				.selectAll(".labelContainerContainer")
				.data(nodes).enter()
				.append("div")
				.filter(function(d) { return d.labels != null ;})
				.attr("class","labelContainerContainer")
				.style("height", function(d) { return d.width + "px";})
				.style("width", function(d) { return d.width + "px";}); // TODO height
			        		 		  	
				labelContainerContainer.avmLabeledComplex();
	 		}

		if (settings.layout.labeling == "simple") {
			
			// simple labeling
			
			// The label and a copy of the label as shadow for better readability
			
			/*
			nodeEnter
				.filter(function(d) { return d.labels != null ;})
				.selectAll(".nodeLabelShadow")
				.data(function(d) { return d.labels }).enter()
				.avmLabeledCT(true) // TODO make dependent on whether d has children, e.g.: function(d) { return d.children || d._children ; } , however here we are alread in d.labels!
				.classed("nodeLabelShadow", true);*/
				
			nodeEnter
				.filter(function(d) { return d.labels != null ;})
				.selectAll(".nodeLabelText")
				.data(function(d) { return d.labels }).enter()
				.avmLabeledCT(true) // TODO make dependent on whether d has children, e.g.: function(d) { return d.children || d._children ; } , however here we are alread in d.labels!
				.classed("nodeLabelText label", true);

		}
		
		// tooltip
		nodeEnter.avmTitled();

		// Transition nodes to their new position.
		var nodeUpdate = node.transition().duration(duration).attr("transform",
				function(d) {
					return "translate(" + d.y + "," + d.x + ")";
				});

		nodeUpdate.select("path").style("visibility", "visible");

		nodeUpdate.selectAll("text").style("visibility", "visible");

		// Transition exiting nodes to the parent's new position.
		var nodeExit = node.exit().transition().duration(duration).attr(
				"transform", function(d) {
					return "translate(" + source.y + "," + source.x + ")";
				}).remove();

		nodeExit.select("path").style("visibility", "hidden");

		nodeExit.selectAll("text").style("visibility", "hidden");

		// Update the links…
		var link = vis.selectAll("path.linking_connector").data(tree.links(nodes),
				function(d) {
					return d.target.id;
				});

		// Enter any new links at the parent's previous position.
		var linkEnter = link.enter().insert("svg:path", "g")
			.attr("class","linking_connector")
			.style("stroke", function(d) {
				return d.target.connector.color_rgb_hex_combined
			}) // works -> get the link color from the endNode (target)
			.style("stroke-width", function(d) {
				return d.target.connector.width
			})
			.attr("d", function(d) {
				var o = {
					x : source.x0,
					y : source.y0
				};
				return diagonal({
					source : o,
					target : o
				});
			})
		.attr("id", function(d){
        	return createIDForLink2(d);
    	})
        .attr("marker-end", function (d) {
        	return "url(#" + d.target.connector.arrow_type + ")";
    	})
        ;

		linkEnter.transition().duration(duration).attr("d", diagonal);

		// TODO sets multiple title tags, but only one can be displayed, pick first one? merge with similar functions
		linkEnter
		  .filter(function(d) { return null != d.target.connector && null != d.target.connector.labels ;})
		  .selectAll(".titleLabelNew")
		  .data(function(d) { return d.target.connector.labels }).enter()
		  .append("svg:title")
		  .filter(function(d) { return d.type == "text_label" ;})
		  .classed("titleLabelNew", true)
		  .text(function(d){return d.text_value_full ; })
		  ;
		
		// Transition links to their new position.
		link.transition().duration(duration).attr("d", diagonal);

		// Transition exiting nodes to the parent's new position.
		link.exit().transition().duration(duration).attr("d", function(d) {
			var o = {
				x : source.x,
				y : source.y
			};
			return diagonal({
				source : o,
				target : o
			});
		}).remove();

		// Stash the old positions for transition.
		nodes.forEach(function(d) {
			d.x0 = d.x;
			d.y0 = d.y;
			
			/* position complex labels */ // TODO not the best place for this! will not be correctly updated
			if(settings.layout.labeling == "complex") {
		        labelContainerContainer
				.style("top", function(d){
					return d.x + "px";
				})
				.style("left", function(d){
					return d.y + "px";
				});
			}
		});
		
		
		if (alignedConnectorLabeling) {
			
			/* label aligned at the connector path */
			var path_label = vis.append("svg:g")
		 		.selectAll(".path_label")
				.data(tree.links(nodes)).enter()
				.avmLabeledConnectorAligned(
					function (d) {
		          		 //return d.target.connector.labels[0]....;
						return "not implemented";
		    		},
		    		function(d) {
				      	return "#" + createIDForLink2(d);
				  	}
				);
		}

	}
