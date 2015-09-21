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