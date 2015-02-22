/******************************/
/* CREDITS        			  */
/******************************/
                    	
/* after an example from http://jsfiddle.net/Y9Qq3/2/ http://jsfiddle.net/nrabinowitz/VYaGg/2/ */

/*****************************************/
/* SETTINGS                              */
/*****************************************/

// force-directed-graph
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

//var localSymbolFunction = avmDefaultSizeSymbolFunction
//.size(2*Math.PI*cRadius*cRadius)
//;

var force = self.force = d3.layout.force()
//   .charge(-900)
     .charge(-4000)
     .linkDistance(100)
//   .linkDistance( function(d) { return d.value * 10 } )
     .gravity(.005)
     .friction(0.075)
    .size([width, height])
	;				 

var myNodes = [];
var myLinks = [];
var node_hash = [];

/*****************************************/
/* UPDATE FUNCTION                       */
/*****************************************/

var loaded = false;
var forceVar;

loadForceDirectedGraph = function(error, graph) {
	
	if (!loaded) {
		
		// override global settings
		complexLabeling = true; // not yet fully implemented
		simpleLabeling = false;

		//alert("loading");
		
			/*********************************/
			/* LOCAL ADAPTED PLUGINS         */
		    /*********************************/
		
		    forceVar = force.nodes(myNodes);
			if (drawLinks) {
			  	forceVar.links(myLinks)
			}

	    loaded = true;
	}

	updateForceDirectedGraph(error, graph);
}

updateForceDirectedGraph = function(error, graph) {
	
		/** DRAGGING *****************************************/
	
		var node_drag = d3.behavior.drag()
	        .on("dragstart", dragstart)
	        .on("drag", dragmove)
	        .on("dragend", dragend);
			
		function dragstart(d, i) {
	        forceVar.stop(); // stops the force auto positioning before you start dragging
	        d3.select(this).classed("dragged",true);
	    }
	
	    function dragmove(d, i) {
	        d.px += d3.event.dx;
	        d.py += d3.event.dy;
	        d.x += d3.event.dx;
	        d.y += d3.event.dy; 
	        tick(); // this is the key to make it work together with updating both px,py,x,y on d !
	    }
	
	    function dragend(d, i) {
	        d.fixed = true; // of course set the node to fixed so the force doesn't include the node in its auto positioning stuff
	        tick();
	        forceVar.resume();
	        d3.select(this).classed("dragged",false);
	    }
	    
	    /** HIGHLIGHTING **********************************/ // TODO make reusable 
	    
	    function defaultStartHighlightingInGraph(d) { 
	    	
	    	var thisNode = d3.select(this);
		    var allNodes = vis.selectAll(".node");
		    var allLinks = vis.selectAll(".link");
	        
	        thisNode.highlight();
		
		     // Figure out the neighboring node id's with brute strength because the graph is small
		     var nodeNeighbors = myLinks.filter(function(link) {
		         // Filter the list of links to only those links that have our target 
		         // node as a source or target
		         return link.source.index === d.index || link.target.index === d.index;})
		     .map(function(link) {
		         // Map the list of links to a simple array of the neighboring indices - this is
		         // technically not required but makes the code below simpler because we can use         
		         // indexOf instead of iterating and searching ourselves.
		         return link.source.index === d.index ? link.target.index : link.source.index; });
		     
		     // select neighboring nodes and apply styles
		     allNodes.filter(function(node) {
		         // filter selection to those holding a node with an
		         // index in the list of neighbors
		         return nodeNeighbors.indexOf(node.index) > -1;
		     })
			.highlight();
				
			 // select in and out links and apply styles
		     allLinks.filter(function(link) {
					return (link.source.index === d.index || link.target.index === d.index);
		     })
			.highlight();
	
			// highlighting of identical resources (same URI)
			//allNodes.avmHighlightIdentical(d.uri);  // not necessary for now
			
			// extend label to full label
			thisNode.selectAll("text.nodeLabel").text(function(d){return d.text_value_full}); // does not work yet for complex labeling	
	
		}
	    
	    /**************************************************/
	    
		var result = [];

		if (myNodes.length==0) {
			result = graph.nodes;
		}
		else {
			for (var i = 0, len = graph.nodes.length; i < len; ++i) {
				
				var newElement = graph.nodes[i];
				var existingElement = containsArrayThisElement(myNodes, newElement);
				
		        if (null!=existingElement) {
//		            result.push(existingElement); // changes to the properties of this object will be lost!
		            result.push(copyForceSettings(existingElement, newElement)); // should copy all settings from the old force-positioned object
		        } else {
		        	result.push(newElement); // OK: this new object will get defaults on next force tick
		        }
			}
		}

		myNodes.length = 0;
		
	    for (var i = 0, len = result.length; i < len; ++i) {
	    	myNodes.push(result[i]);
	    }
	    
	    myLinks.length = 0;
	    
	    // same for link update
	    for (var i = 0, len = graph.links.length; i < len; ++i) {
	    	myLinks.push(graph.links[i]);
	    }
	    
        // Create a hash that allows access to each node by its id // TODO use map function like for neighbored nodes?
        myNodes.forEach(function(d, i) {
          node_hash[d.uri] = d;
        });
      
        // Append the source object node and the target object node to each link records...
        myLinks.forEach(function(d, i) {
          d.source = node_hash[d.source_uri];
          d.target = node_hash[d.target_uri];
        });
	    
	    /* links */

		var boundConnectorGroups = vis.selectAll(".connectorGroup")
    		.data(myLinks) ;// , function(d) { return ????????; });
        
        // ENTER
	    
		var connectorGroupEnter = boundConnectorGroups
	    	.enter().append("svg:g")
	    	.attr("class", function (d) { return "connectorGroup link" + d.type; })  
	    	;
	    
	    var path = connectorGroupEnter.append("svg:path")
	    .attr("class", function (d) { return "link link" + d.type + " " +  d.shape_d3_name; })  
		.attr("id", function(link){
	    	return createIDForLink(link);
		})
		;

	    /* connector labeling */
	    
//		if (alignedConnectorLabeling) { 
//		
//		/* text label aligned at the connector path */
//		var path_label = vis.append("svg:g")
//	 		.selectAll(".path_label")
//			.data(force.links()).enter()
//			.avmLabeledConnectorAligned(
//				function (d) {
//	          		 return "not yet implemented";
//	    		},
//	    		function(d) {
//			      	return "#" + createIDForLink(d);
//			  	}
//			);
//	}
	    
	    var connectorLabelGroup = connectorGroupEnter.append("svg:g")
		   .attr("class", "label"); // TODO class needs to be label for CSS reasons. evtl. change later

		/* icon labeling of connectors */ // TODO: not yet updateable!
		var connectorLabelSymbol = connectorLabelGroup
		  .filter(function(d) { return d.labels != null ;})
		  .selectAll(".iconLabelNew")
		  .data(function(d) { return d.labels }).enter()
		  .avmShapedWithUseSVG() // enter-version of the function called here!
	 	  .attr("transform", "scale(1.5)")
		  .classed("iconLabelNew", true)
		  ;
		
		/* alternative to aligned labeling : simple text labeling of connectors */	// TODO: not yet updateable!
		var connectorLabelText = connectorLabelGroup
		  .filter(function(d) { return d.labels != null ;})
		  .selectAll(".textLabelNew")
		  .data(function(d) { return d.labels }).enter()
		  .append("text")
		  .filter(function(d) { return d.type == "text_label" ;})
		  .text(function(d){return d.text_value_full ; })
		  .classed("textLabelNew label", true)
		  ;
			
		// broken? shapes as paths
		// var connectorLabelSymbol = connectorLabelGroup.avmShapedWithPath();
		
	    
		/* alternative : very simple labeling of connectors by title-tag */	
//		path.avmTitled(); // TODO: not yet updateable!
	    
	    // ENTER + UPDATE
	    
		var boundPaths = boundConnectorGroups.select("path")
			.style("stroke", function(d) { return d.color_rgb_hex_combined; })
			.style("stroke-width", function(d) { return d.width })
			.attr("marker-end", function (d) {
		    	return "url(#" + d.shape_d3_name + ")";
			})
			// approach using mid-markers did not succeed -> a marker on each path-node is not desirable
			/*.attr("marker-mid", function (d) {
				return "url(#" + "markerSquare" + ")";
			})*/
			;
		
	    var boundConnectorLabelGroups = boundConnectorGroups.select("g.label");
		
		// EXIT
		boundConnectorGroups.exit()
		    .style("opacity", 1)
      		.transition().duration(2000).style("opacity", 0)
			.remove();

		
		/* nodes */ 
		
		// DATA JOIN
		var boundNodes = vis.selectAll(".node")
	        .data(myNodes, function(d) { return d.uri; });
	        

		// UPDATE	

		
		// ENTER
		var nodeEnter = boundNodes.enter()
	        .append("g")
		    .attr("class", "node")
		    .call(node_drag)
		    .on("mouseover", defaultStartHighlightingInGraph)
			.on("mouseout",  defaultStopHighlighting)
			.on("dblclick",  function(d) { 
				d.fixed = false;	
			})
		    ;
	
	     /* node shape as reuse svg symbol */
		 var symbolAsShape = nodeEnter  
		 	.filter(function(d) { return d.shape_d3_name != null ;})
	        .avmShapedWithUseSVG()
	        //.attr("width", "200 px").attr("height", "200 px") // does not seem to work (Firefox at least)
	      	.attr("transform", function(d) { return "scale(" + d.width/SYMBOL_WIDTH +  ")"; })
	    	.style("opacity", 0) // fading in only works when no other transition (e.g. from update gets into the way)
  			.transition().duration(1000).style("opacity", 1)
			;

		/* node shape as text */
		var textAsShape = nodeEnter
			.filter(function(d) { return d.shape_text_value != null ;})
			.avmShapedWithText()
			.style("opacity", 0) // fading in only works when no other transition (e.g. from update gets into the way)
      		.transition().duration(1000).style("opacity", 1)
      		;
		 
	    // ENTER + UPDATE
	    boundNodes.select(".text") // here (or above in the update) must be select not selectAll
	    	.text(function(d){return d.shape_d3_name ; })
	    	;
	    
	    boundNodes.avmShapedWithUseSVGUpdate();
	    boundNodes.avmShapedWithTextUpdate();
	      	
	    // EXIT
	    var nodeExit = boundNodes.exit();

	    nodeExit
      		.style("opacity", 1)
      		.transition().duration(2000).style("opacity", 0)
      		.remove();
      		;
		
		/* node shape as path generated by symbol factory */
		//var symbol = nodeEnter.avmShapedWithPath(localSymbolFunction);
	 
	      if (complexLabeling) { // TODO not yet fully updateable 
	    	  
	    	 var boundLabelContainerContainers = d3
				.select("#labelContainerSpace")
				.selectAll(".labelContainerContainer")
				.data(myNodes, function(d){return d.uri ;});
	    	 
	    	 // ENTER
			 
			 var labelContainerContainerEnter = boundLabelContainerContainers.enter()
			 	.append("div")
			 	.filter(function(d) { return d.labels != null ;}) // must not be before append div!
				.attr("class","labelContainerContainer")
				.avmLabeledComplex();
			 
			// ENTER + UPDATE
			
	        // show tooltip with full label (since extending labels not implemented for complex labels)
		   	nodeEnter.avmTitled();
		   	
		   	boundLabelContainerContainers
		   		.filter(function(d) { return d.width != null ;})
		   		.style("height", function(d) { return d.width + "px";})
		   		.style("width", function(d) { return d.width + "px";}); // TODO height
		   			   	
		   	// EXIT
		   	boundLabelContainerContainers.exit()
			    .style("opacity", 1)
	      		.transition().duration(2000).style("opacity", 0)
				.remove();
		   	
		}
			
		else if (simpleLabeling) { // and tooltips
	
	    	// The label and a copy of the label as shadow for better readability
	    	/* nodeEnter
			.filter(function(d) { return d.labels != null ;})
			.selectAll(".nodeLabelShadow")
			.data(function(d) { return d.labels }).enter()
			.avmLabeledFDG().classed("nodeLabelShadow", true);*/
			
		  // sub-selection
		  // must be created after nodeEnter init
		  var label = boundNodes
		  			.filter(function(d) { return d.labels != null ; })
		  			.selectAll(".nodeLabelText")
		  			.data(function(d) {return d.labels ;});
		  
		  var labelEnter = label
			.enter()
			.avmLabeledFDG().classed("nodeLabelText label", true);
	    
		  label
			.avmLabeledFDGUpdate() ; //.classed("nodeLabelText label", true);
		}
		  
		tick = function() {
			
			/* position the connector paths */
			boundPaths.attr("d", function (d) {
	            var dx = d.target.x - d.source.x,
	                dy = (d.target.y - d.source.y),
	                dr = Math.sqrt(dx * dx + dy * dy);
	            
	            // an arc
	            return "M" + d.source.x + "," + d.source.y + "A" + (dr - drSub) + "," + (dr - drSub) + " 0 0,1 " + d.target.x + "," + d.target.y;
	            
	            // a line
				//return "M" + d.source.x + "," + d.source.y + " L" + d.target.x + "," + d.target.y; 
	            
	         	// a line with an intermediate node
	            //return "M" + d.source.x + "," + d.source.y +
				   //" L" + (dr - drSub) + "," + (dr - drSub) +  
				   //" L" + d.target.x + "," + d.target.y; ; 	
				   
	        });
			
			boundConnectorLabelGroups.attr("transform", calculateTranslationToArcCenter);
			//boundConnectorLabelGroups.attr("transform", calculateTranslationToLineCenter);
	        
			/* position complex labels */
			if (complexLabeling) {
		        boundLabelContainerContainers
				.style("top", function(d){
					return d.y - d.width/2 + "px";
				})
				.style("left", function(d){
					return d.x - d.width/2 + "px";
				});
			}
			
	        /* position the nodes */
	        boundNodes.attr("transform", function (d) {
	            return "translate(" + d.x + "," + d.y + ")";
	        });
	        
	        nodeExit.attr("transform", function (d) {
	            return "translate(" + d.x + "," + d.y + ")";
	        });
	
	    };
		
		force.on("tick", tick);
		
		force.start();
	
	}