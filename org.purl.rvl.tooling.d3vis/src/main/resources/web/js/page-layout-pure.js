/******************************/
/* BASIC PAGE BUILDING        */
/******************************/

function buildPageStructure() {
	
	//mInfo = d3.select("body").append("div");

	svg = d3.select("body").append("svg:svg")
		.attr("id", "svg")
	    .attr("width", width)
	    .attr("height", height);

	vis = svg
	    .append("svg:g")
	    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
	    
	labelContainerSpace = d3.select("body").append("div")
		.attr("id", "labelContainerSpace")
		.style("margin-top", m[0]+"px")
		.style("margin-left", m[3]+"px")
	    .attr("width", width + "px")
	    .attr("height", height + "px");
	
	// Define Markers to be used in arrow paths
	svg.avmProvideMarkerCollection();
}