/**
 * 
 */
package org.purl.rvl.interpreter.viso.graphic;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.interpreter.gen.viso.graphic.Shape;

/**
 * @author Jan
 *
 */
public class ShapeX extends Shape {

	/**
	 * @param model
	 * @param classURI
	 * @param instanceIdentifier
	 * @param write
	 */
	public ShapeX(Model model, URI classURI, Resource instanceIdentifier,
			boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param instanceIdentifier
	 * @param write
	 */
	public ShapeX(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param uriString
	 * @param write
	 * @throws ModelRuntimeException
	 */
	public ShapeX(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param bnode
	 * @param write
	 */
	public ShapeX(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param model
	 * @param write
	 */
	public ShapeX(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toD3Name() {
		return "circle";
	}

	public static String getDefaultD3Name() {
		return "circle";
	}
	

}
