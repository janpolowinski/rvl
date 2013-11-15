package org.purl.rvl.interpreter.rvl;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.interpreter.gen.rvl.GraphicAttribute;
import org.purl.rvl.interpreter.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.interpreter.gen.rvl.Valuemapping;


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
		
		// try to get the string description from the (manual) PropertyMapping class, which is not in the super-class hierarchy
		PropertyMapping pm = (PropertyMapping) this.castTo(PropertyMapping.class);
		s += pm.toString();
		
		//targetAttribute is specific to P2GAM
		GraphicAttribute tga = this.getAllTargetattribute_as().firstValue();
		String tgaString = tga.getAllLabel_as().count()>0 ? tga.getAllLabel_as().firstValue() : tga.toString();
		s += "from P2GAM(m): target graphic attribute: " + tgaString + NL ;
		
		s += "from P2GAM(m):There are the following VMS:" + NL;
		ClosableIterator<Valuemapping> vmIterator = this.getAllValuemapping_as().asClosableIterator();
		while (vmIterator.hasNext()) {
			ValueMapping vm = (ValueMapping) vmIterator.next().castTo(ValueMapping.class);
			s += "		" + vm +  NL;
		}
		return s;
	}

}
