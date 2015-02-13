    var graph;
    var myNodes = [];
    
    var newNodes1 = [{"id": "test", "text" : "test1", "x" : 100},{"id": "Emma", "text" : "Emma1", "x" : 200},{"id": "Alex", "text" : "Alex1", "x" : 300}];
    var newNodes2 = [{"id": "test", "text" : "test2", "x" : 100},{"id": "Emma", "text" : "Emma2", "x" : 200},{"id": "Alex", "text" : "Alex2", "x" : 300}];
    var newNodes2b = [{"id": "test", "text" : "test2b", "x" : 100}];

    
    
    var containsArrayThisElement = function (arrayToCheck, element) {
	    var containedElement = null;
	    for (var i = 0, len = arrayToCheck.length; i < len; ++i) {
	        if (arrayToCheck[i].id==element.id) {
	        	containedElement = arrayToCheck[i];
	        }
		}
	    
	    if (containedElement!=null) {
	    	console.log("found array element " + containedElement.text + " when looking for " + element.text);
	    }
	    	
	    return containedElement;
	}
    
    var copyForceSettings = function (positionedObject, newObject) {
    	
    	console.log("positioned object " + positionedObject.index + " " + positionedObject.x + ", " + positionedObject.y + " " + positionedObject.px + ", " + positionedObject.py);

    	newObject.index = positionedObject.index ; 
    	newObject.x = positionedObject.x ; 
    	newObject.y = positionedObject.y ; 
    	newObject.px = positionedObject.px ; 
    	newObject.py = positionedObject.py ; 
    	newObject.fixed = positionedObject.fixed ; 
    	newObject.weight = positionedObject.weight ; 
    	
    	console.log("new object " + newObject.index + " " + newObject.x + ", " + newObject.y + " " + newObject.px + ", " + newObject.py);
//    	
//    	from the docu at https://github.com/mbostock/d3/wiki/Force-Layout:
//        index - the zero-based index of the node within the nodes array.
//        x - the x-coordinate of the current node position.
//        y - the y-coordinate of the current node position.
//        px - the x-coordinate of the previous node position.
//        py - the y-coordinate of the previous node position.
//        fixed - a boolean indicating whether node position is locked.
//        weight - the node weight; the number of associated links.
	    return newObject;
	} 
    
    function myGraph() {

        // Add and remove elements on the graph object
        this.addNode = function (id) {
        	myNodes.push({"id": id, "text" : id});
            update();
        };
        
        // Add and remove elements on the graph object
        this.replaceNodes = function () {
        	
        	var newNodes = [{"id": "test", "text" : "testr", "x" : 100},{"id": "Emma", "text" : "Emmar", "x" : 200},{"id": "Alex", "text" : "Alexr", "x" : 300}];
    		myNodes.length = 0;
    		
    	    for (var i = 0, len = newNodes.length; i < len; ++i) {
    	    	myNodes.push(newNodes[i]);
    	    }
    	    
            update(myNodes);
        };
        
        this.modifyNodes = function (newNodes) {

        	var result = [];

    		if (myNodes.length==0) {
    			result = newNodes;
    		}
    		else {
    			for (var i = 0, len = newNodes.length; i < len; ++i) {
    				
    				var newElement = newNodes[i];
    				var existingElement = containsArrayThisElement(myNodes, newElement);
    				
    		        if (null!=existingElement) {
//    		            result.push(existingElement); // changes to the properties of this object will be lost!
    		            result.push(copyForceSettings(existingElement, newElement)); // should copy all settings from the old force-positioned object -> problem: all objects get the same settings of one old object
//    		            result.push(newElement);
    		        } else {
    		        	result.push(newElement); // OK: this new object will get defaults on next force tick
    		        }
    			}
    		}

    		
    		myNodes.length = 0;
    		
    	    for (var i = 0, len = result.length; i < len; ++i) {
    	    	myNodes.push(result[i]);
    	    }
    	    
            update(myNodes);
        };
        
        


        // set up the D3 visualisation in the specified element
        var w = 960, h = 450;

        var vis = d3.select("body")
                .append("svg:svg")
                .attr("width", w)
                .attr("height", h)
                .attr("id", "svg")
                .attr("pointer-events", "all")
                .attr("viewBox", "0 0 " + w + " " + h)
                .attr("perserveAspectRatio", "xMinYMid")
                .append('svg:g');

        var force = d3.layout.force();
        
        force
        .gravity(.01)
        .charge(-80000)
        .friction(0)
        .linkDistance( function(d) { return d.value * 10 } )
        .size([w, h]);
        
        force.nodes(myNodes);
        
        //force.start();

        var update = function (myNodesArg) {

            var node = vis.selectAll("g.node")
                    .data(myNodesArg, function (d) {
                       return d.id;
                    });
            
            node.selectAll(".textClass").text(function (d) {
                return d.text + " updated";
            });

            var nodeEnter = node.enter().append("g")
                    .attr("class", "node")
                    .call(force.drag);

                var nodeEnterText = nodeEnter.append("text")
                    .attr("class", "textClass")
                    .attr("x", 14)
                    .attr("y", ".31em")
                    ;
                
                nodeEnterText.text(function (d) {
                    return d.text + " entered";
                });
                
                
                node.attr("transform", function (d) {
                    return "translate(" + d.x + ",20)";
                });
                
                
//                node.selectAll(".textClass").text(function (d) {
//                    return d.text;
//                });

            node.exit().remove();

            force.on("tick", function () {
            	
                node.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });

            // Restart the force layout.
            //force.start();
        };


        // Make it all go
        update(myNodes);
    }

    function drawGraph() {

        graph = new myGraph("#svgdiv");

//        graph.addNode('test');
//        graph.addNode('Emma');
//        graph.addNode('Alex');
//        graph.addNode('Alertex');
//        graph.addNode('Aler33tex');
        
        // callback for the changes in the network
        var step = -1;
        function nextval()
        {
            step++;
            return 0 + (1500*step); // initial time, wait time
        }
        
        /*setTimeout(function() {
        	 graph.addNode('Jan');
        }, nextval());*/
        
//        setTimeout(function() {
//       	 graph.replaceNodes();
//       }, nextval());
        

        setTimeout(function() {
          	 graph.modifyNodes(newNodes1);
          }, nextval());
        
        
        
//        setTimeout(function() {
//          	 graph.modifyNodes(newNodes2);
//          }, nextval());

    }

    function addNodes() {
        d3.select("svg")
                .remove();
         drawGraph();
    }
    
    