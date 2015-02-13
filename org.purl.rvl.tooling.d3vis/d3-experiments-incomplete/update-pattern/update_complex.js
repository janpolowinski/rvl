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

//function posSVG(el1, data) {
//	  var node = el1.selectAll("g").data(data, function(d) {return d.id ;});
//	  node.exit().remove();
//	  node.enter().append("svg:g").append("svg:text").attr("y", "1em").attr("x", 14); // .text("entered");
//	  node.selectAll("text").text(function(d) { return d.text; });
//	}

//d3.select("#list").call(list, [0, 1, 2, 3, 4]);