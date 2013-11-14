package org.purl.rvl.interpreter.viso.graphic;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

public class Color extends org.purl.rvl.interpreter.gen.viso.graphic.Color {

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

	
	public java.awt.Color getColor_as_JavaAWT(){
		
		String rgb = getAllColorRGB_as().firstValue();
		
		if(null!=rgb){
			return java.awt.Color.decode(rgb);
		}
		else if(hasColorRGBRed() && hasColorRGBGreen() && hasColorRGBBlue()) {
				float r = getAllColorRGBRed_as().firstValue();
				float g = getAllColorRGBGreen_as().firstValue();
				float b = getAllColorRGBBlue_as().firstValue(); 
				return new java.awt.Color(r,g,b);
		} 
		else {
			return null;
		}
	}
	

}
