    var graph;
    function myGraph() {

        // Add and remove elements on the graph object
        this.addNode = function (id) {
            nodes.push({"id": id});
            update();
        };
        
        // Add and remove elements on the graph object
        this.replaceNodes = function () {
        	//alert(nodes);
        	var newNodes = [{"id": "test1"},{"id": "Emma"},{"id": "Alex"}];
            force.nodes(newNodes);
            nodes = force.nodes();
            //alert(newNodes);
            update();
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
        var nodes = force.nodes();

        var update = function () {

            var node = vis.selectAll("g.node")
                    .data(nodes, function (d) {
                        return d.id;
                    });

            var nodeEnter = node.enter().append("g")
                    .attr("class", "node")
                    .call(force.drag);

                nodeEnter.append("svg:text")
                    .attr("class", "textClass")
                    .attr("x", 14)
                    .attr("y", ".31em")
                    .text(function (d) {
                        return d.id;
                    });

            node.exit().remove();

            force.on("tick", function () {
            	
                node.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });

            // Restart the force layout.
            force
                    .gravity(.01)
                    .charge(-80000)
                    .friction(0)
                    .linkDistance( function(d) { return d.value * 10 } )
                    .size([w, h])
                    .start();
        };


        // Make it all go
        update();
    }

    function drawGraph() {

        graph = new myGraph("#svgdiv");

        graph.addNode('Emma');
        graph.addNode('Alex');
        
        // callback for the changes in the network
        var step = -1;
        function nextval()
        {
            step++;
            return 2000 + (1500*step); // initial time, wait time
        }
        
        setTimeout(function() {
        	 graph.addNode('Jan');
        }, nextval());
        
        setTimeout(function() {
       	 graph.replaceNodes();
       }, nextval());

    }


    function addNodes() {
        d3.select("svg")
                .remove();
         drawGraph();
    }
