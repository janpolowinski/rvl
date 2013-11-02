package org.purl.rvl.interpreter.rvl.manual;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.interpreter.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.interpreter.rvl.Valuemapping;


public class PropertyToGraphicAttributeMapping extends
		Property_to_Graphic_AttributeMapping {
	
	private static final long serialVersionUID = 5391124674649010787L;
	static final String NL =  System.getProperty("line.separator");

	public PropertyToGraphicAttributeMapping(Model model, boolean write) {
		super(model, write);
	}

	public PropertyToGraphicAttributeMapping(Model model,
			Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGraphicAttributeMapping(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		
		String s = "";
		
		// try to get the string description from the (manual) Mapping class, which is not in the super-class hierarchy
		Mapping m = (Mapping) this.castTo(Mapping.class);
		s += m.toString();
		
		// try to get the string description from the (manual) PropertyMapping class, which is not in the super-class hierarchy
		PropertyMapping pm = (PropertyMapping) this.castTo(PropertyMapping.class);
		s += pm.toString();
		
		s += "from PGAM(m):There are the following VMS:" + NL;
		ClosableIterator<Valuemapping> vmIterator = this.getAllValuemapping_as().asClosableIterator();
		while (vmIterator.hasNext()) {
			Valuemapping vm = (Valuemapping) vmIterator.next();
			s += "		" + vm +  NL;
		}
		return s;
	}

}
