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
    .size([width, height])
	;				 

/*****************************************/
/* UPDATE FUNCTION                       */
/*****************************************/


loadForceDirectedSimple = function(error, graph) {

	alert("loading");
	
		/*********************************/
		/* LOCAL ADAPTED PLUGINS         */
	    /*********************************/
	
	    var forceVar = force.nodes(graph.nodes);
		if (drawLinks) {
		  	forceVar.links(graph.links)
		}
	    forceVar.start();

	updateForceDirectedSimple(error, graph);
}

updateForceDirectedSimple = function(error, graph) {

		alert("updating");
		//alert(graph.nodes[0].uri + " " + graph.nodes[1].uri);
	


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
	    
	    
	    

	    
		/* nodes */ 
		
		// DATA JOIN
		var boundNodes = vis.selectAll(".node")
	        //.data(graph.nodes);
	        .data(graph.nodes, function(d) { return d.uri; });
	        

		// UPDATE	
		boundNodes.selectAll("text")
      		.style("fill", "gray")
			;
		
		// ENTER
		var nodeEnter = boundNodes.enter()
	        .append("g")
		    .attr("class", "node")
		    .call(node_drag)
   		    //.transition().duration(10000).styleTween("fill", function() { return d3.interpolate("white", "red"); })
		    .append("text")
		    	.style("fill", "green")
		    	.style("opacity", function(d) { return 0; })
      			.transition().duration(1000).style("opacity", 1)
		    	//.style("fill", "green")
		    	//.transition().duration(4000).style("fill", "gray")
      			.text(function(d){return d.uri ; })
				;
	      	
	    // ENTER + UPDATE
	    //boundNodes.selectAll("text")
	   // 	.text(function(d){return d.uri ; });
	      	
	    // EXIT
	    var nodeExit = boundNodes.exit();

	    nodeExit
	    	//.selectAll("text")
      		.style("opacity", function(d) { return 1; })
      		.transition().duration(2000).style("opacity", 0)
      		.remove();
      		;
      		
		  //nodeExit.remove();
		
			
		  
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
	
	}