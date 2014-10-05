 
// color utilities

 function calculateHighContrastColor(backgroundColor) {
	 
	 var backgroundColorRGB = d3.rgb(backgroundColor);

	 // this may be not the best formula
	 
	 // 0.2126 * (r/255)^2.2  +  0.7151 * (g/255)^2.2  +  0.0721 * (b/255)^2.2;
	 /*var l1 = 
		 0.2126 * Math.pow((backgroundColorRGB.r/255), 2.2) + 
		 0.7151 * Math.pow((backgroundColorRGB.g/255), 2.2) + 
		 0.0721 * Math.pow((backgroundColorRGB.b/255), 2.2);

	   return contrastRatio <= 0.18 ? "#eee" : "#222";

	  */
		 
	 // another from http://www.w3.org/TR/2014/NOTE-WCAG20-TECHS-20140408/G18
	 
	 function relativeLuminance(r,g,b) {
		  return 0.2126 * r + 0.7152 * g + 0.0722 * b ;
	 }
	
	 function luminanceLetter(s) {
		return s <= 0.03928 ? s/12.92 : Math.pow(((s+0.055)/1.055), 2.4);
	 }
	 
	 var rs = backgroundColorRGB.r/255, 
	 	 gs = backgroundColorRGB.g/255, 
	 	 bs = backgroundColorRGB.b/255;
	 	
	 var r = luminanceLetter(rs), g = luminanceLetter(gs), b = luminanceLetter(bs);
	 var rBlack = luminanceLetter(0), gBlack = luminanceLetter(0), bBlack = luminanceLetter(0);
 
	 var l1  =  relativeLuminance(r,g,b);
	 var l2  =  relativeLuminance(rBlack,gBlack,bBlack);
	 
	 //(L1 + 0.05) / (L2 + 0.05), 
	 var contrastRatio = (l1 + 0.05) / (l2 + 0.05); 
	 
	 // sth. seems to be wrong with this one as well (cf. RO-4b):
	 return contrastRatio >= 4.5 ? "black" : "white"; 
 }