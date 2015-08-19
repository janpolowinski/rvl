package org.purl.rvl.java.rvl.filter;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.purl.rvl.exception.UnsupportedSelectorTypeException;
import org.purl.rvl.java.rvl.PropertyMappingX;

public class SubjectFilter extends StatementFilter {
	
	private final static Logger LOGGER = Logger.getLogger(SubjectFilter.class.getName());
	private PropertyMappingX pm; 

	public SubjectFilter(PropertyMappingX pm) {
		this.pm = pm;
	}
	
	/**
	 * @return - A string of SPARQL statements that can be used in a SPARQL query to restrict the variable "?s"
	 * @throws UnsupportedSelectorTypeException 
	 */
	public String getFilterString() throws UnsupportedSelectorTypeException{

		String selectorSPARQLString;
		Literal selector = pm.getSubjectFilter();
		URI selectorType = getSelectorType(selector);
		 
		String selectorValue = selector.getValue();
		String filterSubjectVarString = "?s";
		 
		LOGGER.info("Processing selector type " + selectorType);
						
		if(selectorType.toString().equals("http://purl.org/rvl/fslSelector")) {
			
			 // RVLs basic "fsl selector"
			 
			 String[] fslParts = selectorValue.split("::");
			 URI filterPredicate = new URIImpl(fslParts[0]).asURI();
			 URI filterObject = new URIImpl(fslParts[1]).asURI();
		
			 selectorSPARQLString = 
					 filterSubjectVarString + 
					 " " + filterPredicate.toSPARQL() + " " + 
					 filterObject.toSPARQL() + " . " +
					 " FILTER( ?s !=" + filterObject.toSPARQL() + ") "; // HACK: we exclude reflexive occurences for now to avoid showing classes being the subclass of itself ...
			 
		 } 
		 else if(selectorType.toString().equals("http://purl.org/rvl/sparqlSelector")) {
			 
			 // RVLs basic "sparql selector"
			 
			 selectorSPARQLString = selectorValue;
			 
		 }
		 else if(selectorType.toString().equals("http://purl.org/rvl/classSelector")){
			 
			 // default: class selector (a class name is expected)
			 // will be interpreted as a constraint on the type of resources 
			 
			 selectorSPARQLString = filterSubjectVarString + " " + RDF.type.toSPARQL() + " " + new URIImpl(selectorValue).asResource().toSPARQL() + " . " ;
		 }
		 else {
			
			 throw new UnsupportedSelectorTypeException("The selector type " + selectorType + " is noty yet supported.");
			 
		}
		
		LOGGER.info("Applying subject filter. Only resources matching " + selectorSPARQLString + 
				" will be affected by the mapping " +
				"(and thus shown, which is not the default behavior --> TODO!)");
		
		return selectorSPARQLString;
		
	}

	/**
	 * Get selector type 
	 * 
	 * @param selector
	 * @return selectorType - the defined selector type if available, otherwise the default one (currently a simple class selector)
	 */
	protected URI getSelectorType(Literal selector) {
		
		URI selectorType = new URIImpl("http://purl.org/rvl/classSelector");
		
		try {
			
			DatatypeLiteral typedSelector = selector.asDatatypeLiteral();
			selectorType = typedSelector.getDatatype();
			
		} catch (Exception e){}
		
		return selectorType;
	}

}
