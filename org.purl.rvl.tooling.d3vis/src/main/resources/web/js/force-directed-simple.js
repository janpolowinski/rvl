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

		  	    	alert(graph.nodes[0].uri + " " + graph.nodes[1].uri);
	
	
		/*********************************/
		/* LOCAL ADAPTED PLUGINS         */
	    /*********************************/
	
	    var forceVar = force.nodes(graph.nodes);
		if (drawLinks) {
		  	forceVar.links(graph.links)
		}
	    forceVar.start();



	    
		/* nodes */ 
		
		// DATA JOIN
		var boundNodes = vis.selectAll(".node")
	        .data(graph.nodes);
	        //.data(data, function(d) { return d; });
	        

		// UPDATE	
		boundNodes.selectAll("svg:circle")
      		.style("fill", "grey")
			;
		
		// ENTER
		var nodeEnter = boundNodes.enter()
	        .append("g")
		    .attr("class", "node")
		    .append("svg:circle").attr("r", 20)
      		.style("fill", "red")
   		    //.transition().duration(100000).style("fill", "white")
   		    //.transition().duration(10000).styleTween("fill", function() { return d3.interpolate("white", "red"); })
			;
	      	
	    // ENTER + UPDATE
	      	
	      	
	    // EXIT
	    var nodeExit = boundNodes.exit();

	    nodeExit.selectAll("svg:circle")
      		.style("fill", function(d) { return "lightsteelblue"; })
      		.transition().duration(1000).style("fill", "white")
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