/*
 *  The SearchControl manages searchers and draws a UI for them.  However,
 *  searchers can be used by themselves without the SearchControl.  This is
 *  called using a "Raw Searcher".  When doing this, you must handle and draw
 *  the search results manually.
 */

google.load('search', '1');

var imageSearch;


function searchComplete(){
	
    // Check that we got results
    if (imageSearch.results && imageSearch.results.length > 0) {

    	// Loop through our results, printing them to the page.
    	var results = imageSearch.results;
    	var result = results[0];
    	
        
    	// imageView
    	
    	// Grab our content div, clear it.
        var imageView = document.getElementById('imageView');
        imageView.innerHTML = '';

        var imgContainer = document.createElement('div');
        imgContainer.title=result.titleNoFormatting;
        
        //var title = document.createElement('div');
        // We use titleNoFormatting so that no HTML tags are left in the title
        //title.innerHTML = result.titleNoFormatting;
        
        var newImg = document.createElement('img');
        // There is also a result.url property which has the escaped version
//      newImg.src = result.tbUrl; 	// thumbnails
        newImg.src = result.url;	// full picture
        
        var fullImageLink = document.createElement('a');
        fullImageLink.href=result.url;
        
        //imgContainer.appendChild(title);
        imgContainer.appendChild(fullImageLink);
        fullImageLink.appendChild(newImg);
        
        // Put our title + image in the content div
        imageView.appendChild(imgContainer);
        
        
        // imageCanvas - do the same for the background image canvas
        var imageCanvas = document.getElementById('imageCanvas');
        imageCanvas.innerHTML = '';
        var imgContainerCanvas = document.createElement('div');
        var newImgCanvas = document.createElement('img');
        //newImgCanvas.src = result.tbUrl; 	// thumbnails
        newImgCanvas.src = result.url;		// full picture
        imgContainerCanvas.appendChild(newImgCanvas);
        imageCanvas.appendChild(imgContainerCanvas);

    }
}


function OnLoad(){
    // Our ImageSearch instance.
    imageSearch = new google.search.ImageSearch();
    
    // Restrict to extra large images only
    imageSearch.setRestriction(google.search.ImageSearch.RESTRICT_IMAGESIZE, google.search.ImageSearch.IMAGESIZE_MEDIUM);
    
    // Here we set a callback so that anytime a search is executed, it will call
    // the searchComplete function and pass it our ImageSearch searcher.
    // When a search completes, our ImageSearch object is automatically
    // populated with the results.
    imageSearch.setSearchCompleteCallback(this, searchComplete, null);
    
    // Find me a beautiful car.
    //imageSearch.execute("plant bark biology miscroscope");
}


google.setOnLoadCallback(OnLoad);


