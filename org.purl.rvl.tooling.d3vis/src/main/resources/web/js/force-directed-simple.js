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

var force = self.force = d3.layout.force()
    .charge(-900)
    .linkDistance(200)
     .gravity(.01)
//     .charge(-80000)
     .friction(0)
//     .linkDistance( function(d) { return d.value * 10 } )
    .size([width, height])
	;				 

var myNodes = [];

/*****************************************/
/* UPDATE FUNCTION                       */
/*****************************************/

var loaded = false;
var forceVar;

loadForceDirectedSimple = function(error, graph) {
	
	if (!loaded) {

		//alert("loading");
		
			/*********************************/
			/* LOCAL ADAPTED PLUGINS         */
		    /*********************************/
		
		    forceVar = force.nodes(myNodes);
			//if (drawLinks) {
			//  	forceVar.links(graph.links)
			//}
		    
	    
	    loaded = true;
	}

	updateForceDirectedSimple(error, graph);
}

updateForceDirectedSimple = function(error, graph) {
	
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
	    
	    /**************************************************/
	    
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

	    
	    var containsArrayThisElement = function (arrayToCheck, element) {
		    var containedElement = null;
		    for (var i = 0, len = arrayToCheck.length; i < len; ++i) {
		        if (arrayToCheck[i].uri==element.uri) {
		        	containedElement = arrayToCheck[i];
		        }
			}
		    return containedElement;
		}
	    
	    var copyForceSettings = function (positionedObject, newObject) {
		   
	    	newObject.index = positionedObject.index ; 
	    	newObject.x = positionedObject.x ; 
	    	newObject.y = positionedObject.y ; 
	    	newObject.px = positionedObject.px ; 
	    	newObject.py = positionedObject.py ; 
	    	newObject.fixed = positionedObject.fixed ; 
	    	newObject.weight = positionedObject.weight ; 
//	    	
//	    	from the docu at https://github.com/mbostock/d3/wiki/Force-Layout:
//	        index - the zero-based index of the node within the nodes array.
//	        x - the x-coordinate of the current node position.
//	        y - the y-coordinate of the current node position.
//	        px - the x-coordinate of the previous node position.
//	        py - the y-coordinate of the previous node position.
//	        fixed - a boolean indicating whether node position is locked.
//	        weight - the node weight; the number of associated links.
		    return newObject;
		} 
		
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

		//alert("updating");
		//alert(graph.nodes[0].uri + " " + graph.nodes[1].uri);
	
		//forceVar = force.nodes(myNodes);
		//forceVar.start();
		
		/* nodes */ 
		
		// DATA JOIN
		var boundNodes = vis.selectAll(".node")
	        //.data(graph.nodes);
	        .data(myNodes, function(d) { return d.uri; });
	        

		// UPDATE	
		boundNodes.select("text") // here (or below in the update) must be select not selectAll
      		//.style("fill", "gray")
	    	.transition().duration(1000)
	    	.style("fill", function(d){return d.color_rgb_hex_combined ; })
	    	 //.transition().duration(10000).styleTween("fill", function() { return d3.interpolate("white", "red"); })
			;
		
		// ENTER
		var nodeEnter = boundNodes.enter()
	        .append("g")
		    .attr("class", "node")
		    .call(node_drag)
		    ;
		    
		 var textEnter = nodeEnter  
		     .append("text")
		     	.attr("class","text")
		    	//.style("fill", "green")
		    	.style("fill", function(d){return d.color_rgb_hex_combined ; })
		    	.style("opacity", 0.5) // fading in only works when no other transition (e.g. from update gets into the way)
      			.transition().duration(1000).style("opacity", 1)
				;
		 
		  // sub-selection
		  // must be created after nodeEnter init
		  var label = boundNodes
		  			.filter(function(d) { return d.labels != null ; })
		  			.selectAll(".label")
		  			.data(function(d) {return d.labels ;})
		  			;
		  
		  var labelEnter = label
			.enter()
			.append("svg:text").attr("x","30").attr("y", "3em")
			.attr("class","label")
			.attr("fill", "red")
			;
	
	     
		 var circleEnter = nodeEnter  
	        .avmShapedWithUseSVG()
	    	.style("opacity", 0) // fading in only works when no other transition (e.g. from update gets into the way)
  			.transition().duration(1000).style("opacity", 1)
			;

		 
		 
	    // ENTER + UPDATE
	    boundNodes.select(".text") // here (or above in the update) must be select not selectAll
	    	.text(function(d){return d.shape_d3_name ; })
	    	;
	    
		  label
		    //.filter(function(d) { return d.text_value != null ; })
			.text(function(d) { return d.text_value; })
			;
	    
	    boundNodes.avmShapedWithUseSVGUpdate();
	      	
	    // EXIT
	    var nodeExit = boundNodes.exit();

	    nodeExit
      		.style("opacity", function(d) { return 1; })
      		.transition().duration(2000).style("opacity", 0)
      		.remove();
      		;
//		  nodeExit.remove();
		
			
		  
		tick = function() {
				        
			/* position the nodes */
	        /*nodeEnter.attr("transform", function (d) {
	            return "translate(" + d.x + "," + d.y + ")";
	        });*/
	        
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