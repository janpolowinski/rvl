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

var localSymbolFunction = avmDefaultSizeSymbolFunction
  .size(2*Math.PI*cRadius*cRadius)
;

var force = self.force = d3.layout.force()
    .charge(-900)
    .linkDistance(200)
    .size([width, height])
	;				 

/*****************************************/
/* UPDATE FUNCTION                       */
/*****************************************/

loadForceDirectedGraph = function(error, graph) {
	//d3.json("../../gen/json/graph-data.json", function(error, graph) {
	//d3.json("../../example-data/graph-data-labeling-advanced.json", function(error, graph) {
	
		/*********************************/
		/* LOCAL ADAPTED PLUGINS         */
	    /*********************************/
	
	    var forceVar = force.nodes(graph.nodes);
		if (drawLinks) {
		  	forceVar.links(graph.links)
		}
	    forceVar.start();
			  
			  
	    /* links/edges with label and tooltip */
	    
	    /* // alternative: lines instead of paths
		  var link = vis.selectAll(".link")
		      .data(graph.links)
		      .enter().append("line")
		      .attr("class", "link")
		      //.style("stroke-width", function(d) { return Math.sqrt(d.value); })
			  ;
		*/
	
		var connectorGroup = vis.selectAll(".connectorGroup")
	    .data(graph.links)
	    .enter().append("svg:g")
	    .attr("class", function (d) { return "connectorGroup link" + d.type; })  
		;
		
	    var path = connectorGroup.append("svg:path")
	    .attr("class", function (d) { return "link link" + d.type + " " +  d.shape_d3_name; })  
		//.style("stroke", function(d) { return d.color_rgb_hex; })
		.style("stroke", function(d) { return d.color_rgb_hex_combined; })
		.style("stroke-width", function(d) { return d.width })
		.attr("marker-end", function (d) {
	    	return "url(#" + d.shape_d3_name + ")";
		})
		// approach using mid-markers did not suceed -> a marker on each path-node is not desirable
		/*.attr("marker-mid", function (d) {
			return "url(#" + "markerSquare" + ")";
		})*/
		.attr("id", function(link){
	    	return createIDForLink(link);
		})
		;
		
		if (alignedConnectorLabeling) { 
		
			/* text label aligned at the connector path */
			var path_label = vis.append("svg:g")
		 		.selectAll(".path_label")
				.data(force.links()).enter()
				.avmLabeledConnectorAligned(
					function (d) {
		          		 return "not yet implemented";
		    		},
		    		function(d) {
				      	return "#" + createIDForLink(d);
				  	}
				);
		}	
					
		var connectorLabelGroup = connectorGroup.append("svg:g")
			.attr("class", function(d) { return "connectorLabelGroup" })
			.attr("class", "label");
	
		/* icon labeling of connectors */	
		var connectorLabelSymbol = connectorLabelGroup
		  .filter(function(d) { return d.labels != null ;})
		  .selectAll(".iconLabelNew")
		  .data(function(d) { return d.labels }).enter()
		  .avmShapedWithUseSVG() // enter version of the function called here!
	 	  .attr("transform", "scale(1.5)")
		  .classed("iconLabelNew", true)
		  ;
		
		/* alternative to aligned labeling : simple text labeling of connectors */	
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
		//path.avmTitled();
	
			
		/* manual node positioning */
		
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
	    
	    /* highlighting */ // TODO make reusable 
	    
	    function defaultStartHighlightingInGraph(d) { 
	    	
	    	var thisNode = d3.select(this);
		    var allNodes = vis.selectAll(".node");
		    var allLinks = vis.selectAll(".link");
		    var thisGraph = graph; // TODO included in d anyway?
	        
	        thisNode.highlight();
		
		     // Figure out the neighboring node id's with brute strength because the graph is small
		     var nodeNeighbors = thisGraph.links.filter(function(link) {
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
			allNodes.avmHighlightIdentical(d.uri);
			
			// extend label to full label
			thisNode.selectAll("text.nodeLabel").text(function(d){return d.text_value_full});			
	
		}
	    
		/* nodes */ 
		
		var nodeEnter = vis.selectAll(".node")
	        .data(graph.nodes).enter()
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
	      	;
		
		/* node shape as text */
		var textAsShape = nodeEnter
			.filter(function(d) { return d.shape_text_value != null ;})
			.avmShapedWithText();
	
		/* node shape as path generated by symbol factory */
		//var symbol = nodeEnter.avmShapedWithPath(localSymbolFunction);
	 
		 		if (complexLabeling) {
		 
		 			 // complex labeling
			 
			 var labelContainerContainer = d3
				.select("#labelContainerSpace")
				.selectAll(".labelContainerContainer")
				.data(graph.nodes).enter()
				.append("div")
				.filter(function(d) { return d.labels != null ;})
				.attr("class","labelContainerContainer")
				.style("height", function(d) { return d.width + "px";})
				.style("width", function(d) { return d.width + "px";}); // TODO height
		        		 		  	
			labelContainerContainer.avmLabeledComplex();
			
	        // show tooltip with full label (since extending labels not implemented for complex labels)
	    	nodeEnter.avmTitled();
		}
			
		else if (simpleLabeling) {
		
	    	// simple node labeling and tooltips 
	
	    	// The label and a copy of the label as shadow for better readability
	    	/* nodeEnter
			.filter(function(d) { return d.labels != null ;})
			.selectAll(".nodeLabelShadow")
			.data(function(d) { return d.labels }).enter()
			.avmLabeledFDG().classed("nodeLabelShadow", true);*/
	    	
			nodeEnter
			.filter(function(d) { return d.labels != null ;})
			.selectAll(".nodeLabelText")
			.data(function(d) { return d.labels ; }).enter()
			.avmLabeledFDG().classed("nodeLabelText label", true);
			
		}
	
		  
		// Use elliptical arc path segments to doubly-encode directionality.
		tick = function() {
			
			/* position the connector paths */
	        path.attr("d", function (d) {
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
	    
			//symbol2
			//connectorLabelGroup.attr("transform", calculateTranslationToArcCenter);
			// or
	        connectorLabelGroup.attr("transform", function(d){
				return calculateTranslationToArcCenter(d);
				//return calculateTranslationToLineCenter(d);
			});
	
			/* position complex labels */
			if(complexLabeling) {
		        labelContainerContainer
				.style("top", function(d){
					return d.y - d.width/2 + "px";
				})
				.style("left", function(d){
					return d.x - d.width/2 + "px";
				});
			}
	        
			/* position the nodes */
	        nodeEnter.attr("transform", function (d) {
	            return "translate(" + d.x + "," + d.y + ")";
	        });
	
	    };
		
		force.on("tick", tick);
	
	}