function checkBrowser() {
	if (navigator.userAgent.indexOf("Firefox") == -1) {
		alert('Please use Firefox to experiment with this prototype for now. Complex labeling is not yet supported in browsers such as Chrome or Safari.');
	}
}

function getProjectByID(projects, id) {
	return projects.filter(function(project) {
		return project.id === id;
	})[0];
}

function showErrorMessage(error) {
	alert("Sorry, an error occured. Status: " + error.statusText + " (" + error.statusText + "). " + error.responseText)
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


/* OGVIC-specific */

// show all projects or only public ones
function getProjectsEndpoint() {
	var allProjects = getParameterByName("allProjects");
	if (allProjects != null && allProjects == "true") {
		return baseUrlBackend + "projects";
	} else {
		return baseUrlBackend + "projects/public";
	}
}