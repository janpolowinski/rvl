package org.purl.rvl.interpreter.viso.graphic;

import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.interpreter.gen.viso.graphic.DirectedLinking;

public class GraphicObject extends
		org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject {
	
	static final String NL =  System.getProperty("line.separator");

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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		String s = "########################################" + NL;
		s += super.toString() + NL;
		s += "Label: " + this.getAllLabel_as().firstValue() + NL;
		try{
			s += "Color (named): " + this.getAllColornamed_as().firstValue().getAllLabel_as().firstValue() + NL;
		}
		catch (NullPointerException e) {
			System.err.println("no color or no label of color");
		}
		// containment (binary)
		try {
			ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containeeIt =
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
}
