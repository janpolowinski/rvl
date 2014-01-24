package org.purl.rvl.java.viso.graphic;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
public class GraphicObject extends
		org.purl.rvl.java.gen.viso.graphic.GraphicObject {
	
	private static final long serialVersionUID = -9112269162175431905L;
	
	private final static Logger LOGGER = Logger.getLogger(GraphicObject.class.getName()); 
	static final String NL =  System.getProperty("line.separator");
	
	Resource representedResource;

	public GraphicObject(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, boolean write) {
		super(model, write);
		this.setColornamed(Color.getDefaultColor(model));
	}

	
	@Override
	public String toString() {
		String s = "########################################" + NL;
		s += super.toString() + NL;
		s += "Label: " + this.getAllLabel_as().firstValue() + NL;
		if(hasColornamed())
			s += this.getAllColornamed_as().firstValue().castTo(Color.class);
		// containment (binary)
		try {
			ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containeeIt =
					getAllContains_as().asClosableIterator();
			while (containeeIt.hasNext()) {
				GraphicObject containee = (GraphicObject) containeeIt.next().castTo(GraphicObject.class);
				s += "---contains--->" + containee.getAllLabel_as().firstValue() + NL;
			}	
		} catch (Exception e) {
			System.err.println("no containee or no containee label?");
		}
		// linking (n-ary)
		try {
			ClosableIterator<? extends DirectedLinking> nodeIt =
					getAllLinkedto_as().asClosableIterator();
			while (nodeIt.hasNext()) {
				DirectedLinking dlRel = (DirectedLinking) nodeIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
				GraphicObject endNode = (GraphicObject) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
				
				s += "---linked-to--->" + endNode.asURI() + NL;
			}	
		} catch (Exception e) {
			System.err.println("sth wrong with printing the linking relation");
		}
		
		return s;
	}
	
	@XmlElement(name="label")
	public String getLabel() {
		
		return this.getAllLabel_as().firstValue().toString();
		
	}
	
	@XmlElement(name="color_rgb_hex")
	public String getColorHex() {
		String colorHex = "";

		if(this.hasColornamed()) {
			org.purl.rvl.java.viso.graphic.Color startNodeColor = 
				(org.purl.rvl.java.viso.graphic.Color) this.getAllColornamed_as().firstValue().castTo(org.purl.rvl.java.viso.graphic.Color.class);
			try {
				colorHex = startNodeColor.toHexString();
			} catch (Exception e) {
				System.err.println("Couldn't get color values (incomplete?). Default will be used.");
			}
		}
		
		if (colorHex.equals("")) {
			colorHex = org.purl.rvl.java.viso.graphic.Color.getDefaultColorHex();
		}
		return colorHex;
	}
	
	@XmlElement(name="shape_d3_name")
	public String getShape() {
		String shapeD3Name = "";
		try {
			if(this.hasShapenamed()) {
				ShapeX startNodeShape = (ShapeX) this.getAllShapenamed_as().firstValue().castTo(ShapeX.class);
				shapeD3Name = startNodeShape.toD3Name();
			}
		} catch (Exception e) {
			System.err.println("Couldn't get shape value (incomplete?). Default will be used.");
		}
		if (shapeD3Name.equals("")) {
			shapeD3Name = ShapeX.getDefaultD3Name();
		}
		return shapeD3Name;
	}

	public void setRepresents(org.ontoware.rdf2go.model.node.Resource resource) {
		this.representedResource = resource;
	}
	
	private Resource getRepresentedResource() {
		return representedResource;
	}


}
