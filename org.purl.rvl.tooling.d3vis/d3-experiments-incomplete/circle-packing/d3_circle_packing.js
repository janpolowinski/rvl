// This component is based on the 
// Coffee Flavour Wheel by Jason Davies,
// http://www.jasondavies.com/coffee-wheel/
// License: http://www.jasondavies.com/coffee-wheel/LICENSE.txt

// Edited and prepared for the RVL ontowiki extension by: Jan Polowinski
// remarks:
// diameter should be dynamic and = width of free space
// size is fixed at the moment
// only 1 Level of each relation can be shown a containment! additionally, when some of potential instances miss a property (e.g. one book has no category assigned, the system will fail some point. No vis is generated, not even rawdata offered.

var d3_circle_packing = {              
    
    makeVis : function (vis,force,property,rname,mappings,rawData){        
	
		
        var data = new Array();
        for(var i=0; i < rawData.length; i++){   
            if(rawData[i].props[property] !== undefined)
                data.push(rawData[i]);
        }
        var categories = new Array();
        for(var i=0; i < data.length; i++){   
            if(jQuery.inArray(data[i].props[property].value,categories) == -1)
                categories.push(data[i].props[property].value);            
        }
		
        var tNodes = {"name" : "Books", "children" : new Array()};
        for(var i=0; i < categories.length; i++)
            tNodes.children.push({"name" : categories[i],"children" : new Array()});        
        for(var i=0; i < data.length; i++){   
            for(var j=0; j < tNodes.children.length; j++){
                if(tNodes.children[j].name == data[i].props[property].value){
                    tNodes.children[j]["children"].push({"name" : data[i].label, "size" : 30});
                    break;
                }
            }
        }

		var diameter = 700,
		    format = d3.format(",d");
		
		var pack = d3.layout.pack()
		    .size([diameter - 4, diameter - 4])
		    .value(function(d) { return d.size; });
		
		// the vis variable already contains the svg node
		vis.attr("width", diameter)
		   .attr("height", diameter)
		   .append("g")
		     .attr("transform", "translate(2,2)");
			 
			 
		
		 update(root = tNodes); // use real data required!
		 function update(source){
		 	//d3.json("http://localhost/ontowiki/circle_packing.json", function(error, root) { // use test data instead
				
				var node = vis.datum(root).selectAll(".node").data(pack.nodes).enter().append("g").attr("class", function(d){
					return d.children ? "node" : "leaf node";
				}).attr("transform", function(d){
					return "translate(" + d.x + "," + d.y + ")";
				});
				
				node.append("title").text(function(d){
					return d.name + (d.children ? "" : ": " + format(d.size));
				});
				
				node.append("circle").attr("r", function(d){
					return d.r;
				});
				
				node.filter(function(d){
					return !d.children;
				}).append("text").attr("dy", ".3em").style("text-anchor", "middle").text(function(d){
					return d.name.substring(0, d.r / 3);
				});
			//});
			};
		
		//d3.select(self.frameElement).style("height", diameter + "px");

    
    },
    
    updatePositions : function (vis){
    }    
}     

