function list(ul, data) {
  var li = ul.selectAll("li").data(data, function(d) {return d.id ;});
  li.exit().remove();
  li.enter().append("li");
  li.text(function(d) { return d.text; });
}

function posSVG(el1, data) {
	  var node = el1.selectAll("text").data(data, function(d) {return d.id ;});
	  node.exit().remove();
	  node.enter().append("svg:text").attr("y", "1em"); // .text("entered");
	  node.text(function(d) { return d.text; }).attr("x", function(d) { return d.x; });
	}

function posSVGGrouped(el1, data) {
	
	  var node = el1.selectAll("g").data(data, function(d) {return d.id ;});
	  //var myText = node.selectAll("text").data(function(d) { return d; });
	  
	  // exit
	  node.exit().remove();
	  
	  // enter
	  var nodeEnter = node.enter().append("svg:g");
	  
	  var textEnter = nodeEnter
	  			.selectAll("text").data(data, function(d) {return d.id ;})
	  			.enter()
	  			.append("svg:text").attr("x","30").attr("y", "1em")
	  			.text(function(d) { return "neu" })
	  			;
		
	  
	  // update
	  var textUpdate = node
	  			.selectAll("text")
	  			.data(data, function(d) {return d.id ;})
	  			.text(function(d) { return d.text })
	  			;
	  
	  // enter + update
	  node.attr("transform", function (d) {
          return "translate(" + d.x + ",20)";
      });
	  
	}

//d3.select("#list").call(list, [0, 1, 2, 3, 4]);