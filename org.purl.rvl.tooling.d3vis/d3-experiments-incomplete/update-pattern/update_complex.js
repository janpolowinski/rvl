function list(ul, data) {
  var li = ul.selectAll("li").data(data, function(d) {return d.id ;});
  li.exit().remove();
  li.enter().append("li");
  li.text(function(d) { return d.text; });
}

//d3.select("#list").call(list, [0, 1, 2, 3, 4]);