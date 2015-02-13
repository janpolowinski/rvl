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
    
     
    function myGraph() {
        
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
    		            result.push(newElement);
    		        } else {
    		        	result.push(newElement); 
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
                    ;

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
        };


        // Make it all go
        update(myNodes);
    }

    function drawGraph() {
        graph = new myGraph("#svgdiv");
    }

    function addNodes() {
        d3.select("svg")
                .remove();
         drawGraph();
    }
    

    