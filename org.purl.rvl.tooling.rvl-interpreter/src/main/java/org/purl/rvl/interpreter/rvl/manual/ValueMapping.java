package org.purl.rvl.interpreter.rvl.manual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.interpreter.mapping.ExplicitValueMapping;
import org.purl.rvl.interpreter.rvl.Valuemapping;

public class ValueMapping extends Valuemapping {
	
	static final String NL =  System.getProperty("line.separator");

	public ValueMapping(Model model, URI classURI, Resource instanceIdentifier,
			boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMapping(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMapping(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMapping(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMapping(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public Collection<ExplicitValueMapping> getCalculatedValueMappings(){
		List<ExplicitValueMapping> vml = new ArrayList<ExplicitValueMapping>();
		
		if(this.getAllDiscretize_as().firstValue()!=null && this.getAllDiscretize_as().firstValue() == true)
			System.out.println("the target value range should be discretised here ...");
		
		vml.add(new ExplicitValueMapping("forest","green"));
		vml.add(new ExplicitValueMapping("desert","yellow"));
		vml.add(new ExplicitValueMapping("sea","blue"));
		return vml;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += getCalculatedValueMappings() + NL;
		return s + "describe VM here" + NL;
	}

}
