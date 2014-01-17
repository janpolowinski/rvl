package org.purl.rvl.java.viso.graphic;

import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.exception.IncompleteColorValuesException;

public class Color extends org.purl.rvl.java.gen.viso.graphic.Color {

	private final static Logger LOGGER = Logger.getLogger(Color.class .getName()); 

	static final String NL =  System.getProperty("line.separator");
	
	int r = -1;
	int g = -1;
	int b = -1;

	/**
	 * @param model
	 * @param bnode
	 * @param write
	 */
	public Color(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param write
	 */
	public Color(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param instanceIdentifier
	 * @param write
	 */
	public Color(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param uriString
	 * @param write
	 * @throws ModelRuntimeException
	 */
	public Color(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param classURI
	 * @param instanceIdentifier
	 * @param write
	 */
	public Color(Model model, URI classURI, Resource instanceIdentifier,
			boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}
	
	public Color(float r, float g, float b, Model model, boolean write){
		super(model, write);
		setColorRGBRed(r);
		setColorRGBGreen(g);
		setColorRGBBlue(b);
	}
	
	public Color(double r, double g, double b, Model model, boolean write){
		super(model, write);
		setColorRGBRed(new Float(r));
		setColorRGBGreen(new Float(g));
		setColorRGBBlue(new Float(b));
	}

	
	public java.awt.Color getColor_as_JavaAWT() throws IncompleteColorValuesException {
		
		String rgb = getAllColorRGB_as().firstValue();
		
		if(null!=rgb){ 
			try {
				return java.awt.Color.decode(rgb);
			} catch (NumberFormatException e) {
				LOGGER.warning("Problem decoding the RGB value of " + super.toString());
				throw new IncompleteColorValuesException();
				//throw new NumberFormatException();
			}
		}
		else if(hasColorRGBRed() && hasColorRGBGreen() && hasColorRGBBlue()) {
				r = getAllColorRGBRed_as().firstValue().intValue();
				g = getAllColorRGBGreen_as().firstValue().intValue();
				b = getAllColorRGBBlue_as().firstValue().intValue(); 
				return new java.awt.Color(r,g,b);
		} 
		else {
			throw new IncompleteColorValuesException();
//			LOGGER.warning("Incomplete (or no) color values found for " + super.toString());
//			return null;
		}
	}
	
	public String toHexString() throws IncompleteColorValuesException {
		  String hexColour = Integer.toHexString(getColor_as_JavaAWT().getRGB() & 0xffffff);
		  if (hexColour.length() < 6) {
		    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		  }
		  return "#" + hexColour;
		}
	
	/*
	public String toHex(){
		// if r,g,b already set
		if (!(r<0 || g<0 || b<0))
			return String.format("#%02x%02x%02x", r, g, b);
		else {
			// TODO ugly ... side effects...
			java.awt.Color c = getColor_as_JavaAWT();
			return String.format("#%02x%02x%02x", r, g, b);
		}
	}
	*/
	
	public static String getDefaultColorHex() {
		// TODO evaluate RVL and VISO settings
		return "#cccccc";
	}
	
	@Override
	public String toString() {
		String s = "";
		if (hasLabels()) 
			s += "color " + getAllLabel_as().firstValue() + NL; 
		s += "      " + super.toString() + ", ";
		
		try{
			s += getColor_as_JavaAWT();
			try{
				s += ", hex: " + toHexString();
			}
			catch (NumberFormatException e) {
				LOGGER.warning("Problem getting hex color for " + super.toString() + ". Missing color values?");
			}
		}
		catch (IncompleteColorValuesException e) {
			LOGGER.warning("Problem creating AWT color for " + super.toString() + ". Missing color values?");
		}
		
		return s;
	}

	// TODO performance: reuse color objects?
	public static Color getDefaultColor(Model model) {
		return new org.purl.rvl.java.viso.graphic.Color(model, "http://purl.org/viso/graphic/Grey", true);
	}

}
