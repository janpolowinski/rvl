/**************************/
/* GENERAL SETTINGS       */
/**************************/

var alignedConnectorLabeling = false; 

var NODE_SIZE; // TODO D3 symbol functions consider area using Math.sqrt(). Area of svg symbols in use elements is simply width*height
var LABEL_ICON_SUPER_IMPOSITION_FAKTOR = 1/1.3; // e.g. 1/2 : Overlap by 1/2 label icon size

// SVG symbols
var SYMBOL_WIDTH = 25; // width of the symbols in the Symbols.svg without scaling in px

// canvas
var width = 900,
    height = 800,
    m = [30, 30, 30, 30]
	;

var BASE_PATH_SVG_FILE = "svg/symbols.svg#"; // this probably does not work for the static-example-files anymore

var baseUrlBackend = "/semvis/";

var layoutSettings = {
		"connector" : "straight", // other connector settings: "arc"
		"labeling" : "complex", // other labeling settings: "simple"; may be overridden by graphic loading functions
		"drawlinks" : true	// choose whether only nodes should be drawn and layouted
		};

var settings = {
		"layout" : layoutSettings
};


/******************************/
/* GLOBAL "MEMBER" VARS       */
/******************************/


//var mInfo;
var svg;
var vis;
var labelContainerSpace;
var currentGraphicType = "force-directed-graph";
        