package org.purl.rvl.java.viso.graphic;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.viso.graphic.Shape;

/**
 * @author Jan Polowinski
 *
 */
public class ShapeX extends Shape {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2635892870816043355L;

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
		
		/*
	    circle - a circle.
	    cross - a Greek cross or plus sign.
	    diamond - a rhombus.
	    square - an axis-aligned square.
	    triangle-down - a downward-pointing equilateral triangle.
	    triangle-up - an upward-pointing equilateral triangle.
		*/
		
		if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Cross")) return "plus";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Circle")) return "circle";
		//else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/EquiliteralTriangleDown")) return "triangle-down";
		//else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/EquiliteralTriangle")) return "triangle-up";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Square")) return "square";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Minus")) return "minus";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Plus")) return "plus";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Rhomb")) return "rhomb";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Diamond")) return "diamond";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/FlowArrow")) return "flow-arrow";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Heart")) return "heart";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Dislike")) return "dislike";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Hate")) return "dislike";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/BrokenHeart")) return "dislike";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Clock")) return "clock";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/CircleP")) return "circle-p";
		else if (this.asURI().toString().equals("http://purl.org/viso/addon/shapes/bio/Aliphatic_Shape")) return "aliphatic";
		else if (this.asURI().toString().equals("http://purl.org/viso/addon/shapes/bio/Aromatic_Shape")) return "aromatic";
		else if (this.asURI().toString().equals("http://purl.org/viso/addon/shapes/bio/Hydrophil")) return "raindrop";
		
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Star18")) return "star-18";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/XMark")) return "x-mark";
		
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Pentagon")) return "pentagon";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Hexagon")) return "hexagon";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/AVMGraphicObject")) return "avm_go";
		
		
		// shapes used to define connectors primarily
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/UMLGeneralization")) return "uml_generalization_arrow";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/UMLAssociation")) return "arrow";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/ArrowSmall")) return "arrow_small_triangle";
		else if (this.asURI().toString().equals("http://purl.org/viso/shape/commons/Line")) return "line";
		
		else return "circle";
	}

	public static String getDefaultD3Name() {
		return "circle";
	}
	

}
