package org.purl.rvl.java.rvl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.GraphicAttribute;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Valuemapping;
import org.purl.rvl.java.mapping.CalculatedValueMapping;


public class PropertyToGraphicAttributeMapping extends
		Property_to_Graphic_AttributeMapping implements MappingIF {
	
	
	private final static Logger LOGGER = Logger.getLogger(PropertyToGraphicAttributeMapping.class .getName()); 
	
	private static final long serialVersionUID = 5391124674649010787L;
	static final String NL =  System.getProperty("line.separator");
	
	Map<Node, Node> explicitlyMappedValues;
	Map<Node, Node> calculatedMappedValues;

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
	
	/**
	 * Creates and returns a map of URIs of all explicitly mapped values 
	 * and their target graphic values.
	 * @return
	 */
	public Map<Node, Node> getExplicitlyMappedValues(){
		
		// try to get calculated values
		try {
			explicitlyMappedValues = getCalculatedValues(null);
		} catch (Exception e) {
			LOGGER.fine("Could not calculate values, will try to return explicit values."); // TODO check this ...
		}
		
		// when this fails, try to get explicit ones // TODO should be explicit first! rework this ...
		if ((null == explicitlyMappedValues || explicitlyMappedValues.isEmpty()) && hasValuemapping()) {
						
			// TODO evtl. check for blank nodes since the toSPARQL() issued an exception in some cases

			explicitlyMappedValues = new HashMap<Node, Node>();
			
			// get all subjects and the sv/tv table via SPARQL // TODO here we take all value mappigns with a source and target value, but these may include those with more than one sv/tv value set!
			String querySubjectsAndSVtoTVMapForGivenProperty = "" +
					"SELECT DISTINCT ?sv ?tv " +
					"WHERE { " +
				    	toSPARQL() + " <" + VALUEMAPPING + "> ?vm ." + 
				    "	?vm <" + ValueMapping.SOURCEVALUE + "> ?sv . " +
				    "	?vm <" + ValueMapping.TARGETVALUE + "> ?tv . " + 
					"} ";
			
			QueryResultTable explMapResults = model.sparqlSelect(querySubjectsAndSVtoTVMapForGivenProperty);
			for(QueryRow row : explMapResults) {
				explicitlyMappedValues.put(row.getValue("sv"),row.getValue("tv"));
			}
			
			LOGGER.fine("Created value map: " + explicitlyMappedValuesToString());
		}

		return explicitlyMappedValues;
		
		
		/*
		// TRIAL WITH COMPLEX SPARQL QUERY: leads to concurrent modification exception wehen GOs are edited:
		// get mapping and source property
		Property_to_Graphic_AttributeMapping p2gam = Property_to_Graphic_AttributeMapping.getInstance(model, new URIImpl("http://purl.org/rvl/example-mappings/PMWithExplicitValueMappingsAsBlankNodesToColorNamed"));
		System.out.println(p2gam.getAllLabel_as().firstValue());
		Property sp = p2gam.getAllSourceproperty_as().firstValue();
		System.out.println("sp: " + sp);
		
		// get all subjects and the sv/tv table via SPARQL
		String querySubjectsAndSVtoTVMapForGivenProperty = "" +
				"SELECT DISTINCT ?s ?o ?tv " +
				"WHERE { " +
			    	p2gam.toSPARQL() + " <" + PropertyToGraphicAttributeMapping.VALUEMAPPING + "> ?vm ." + 
	//		    	p2gam.toSPARQL() +  " <" + ValueMapping.TARGETVALUE + "> ?ta. " + 
			    "	?vm <" + ValueMapping.SOURCEVALUE + "> ?o . " +
			    "	?vm <" + ValueMapping.TARGETVALUE + "> ?tv . " + 
			    "	?s <" + sp + "> ?o . " +
				"} ";
				
		System.out.println(querySubjectsAndSVtoTVMapForGivenProperty);
		
		Random r = new Random();

		QueryResultTable explMapResults = model.sparqlSelect(querySubjectsAndSVtoTVMapForGivenProperty);
		for(QueryRow row : explMapResults) {
			System.out.println(row);
			// create a new GO
			GraphicObject go = new GraphicObject(model, "http://purl.org/rvl/example-avm/GO_for_" + r.nextInt(), false);
			// relate it to the resource
			// TODO
			// set target attribute TODO: generic TODO: so many casts necessary???
			Color tv = Color.getInstance(model, (Resource)row.getValue("tv"));
//			go.setColornamed(tv);
			goSet.add(go);	
			//System.out.println(go);
		}
		
		*/
	}

	public Map<Node, Node> getCalculatedValues(Set<Statement> theStatementWithOurObject) {
		
		if (null == explicitlyMappedValues && hasValuemapping()) {

			explicitlyMappedValues = new HashMap<Node, Node>();
			
			// TODO: we ignore other value mapping than the first at the moment! sometimes multiple are allowed!
			
			Collection<CalculatedValueMapping> cvms = getFirstValueMapping().getCalculatedValueMappings(theStatementWithOurObject);
			
			for (Iterator<CalculatedValueMapping> iterator = cvms.iterator(); iterator.hasNext();) {
				CalculatedValueMapping calculatedValueMapping = (CalculatedValueMapping) iterator.next();
				explicitlyMappedValues.put(calculatedValueMapping.getSourceValue(),calculatedValueMapping.getTargetValue());
			}

		}

		return explicitlyMappedValues;
	}

	private ValueMapping getFirstValueMapping() {
		return (ValueMapping)this.getAllValuemapping_as().firstValue().castTo(ValueMapping.class);
	}

	public String toString(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMapping class, which is not in the super-class hierarchy
		PropertyMapping pm = (PropertyMapping) this.castTo(PropertyMapping.class);
		s += pm.toString();
		
		//targetAttribute is specific to P2GAM
		GraphicAttribute tga = this.getAllTargetattribute_as().firstValue();
		String tgaString = tga.getAllLabel_as().count()>0 ? tga.getAllLabel_as().firstValue() : tga.toString();
		s += "     Target graphic attribute: " + tgaString + NL ;
		
		if(this.hasValuemapping()) {

			if(!getExplicitlyMappedValues().isEmpty()){
				s += explicitlyMappedValuesToString();
			}
			else {
				s += "     (value mappings not yet calculated ...)" + NL;
				
				s += "     Value mappings:" + NL;
				ClosableIterator<Valuemapping> vmIterator = this.getAllValuemapping_as().asClosableIterator();
				while (vmIterator.hasNext()) {
					ValueMapping vm = (ValueMapping) vmIterator.next().castTo(ValueMapping.class);
					s += "" + vm + NL;
				}
			}
		}
		else {
			s += "     (with no explicit value mappings)" + NL;
		}
		
		/*
		// seems to cause an exception, but not on every machine?! "java.lang.UnsupportedOperationException: Variable (Singleton) cannot be used for SPARQL queries"
		s += "Explicit (simple 1-1) VMs:" + NL;
		Map<Node, Node> map = getExplicitlyMappedValues();
		Set<Entry<Node, Node>> set = map.entrySet();
		for (Iterator<Entry<Node, Node>> iterator = set.iterator(); iterator.hasNext();) {
			Entry<Node, Node> svURItvURIPair = (Entry<Node, Node>) iterator.next();
			s+= "	" + svURItvURIPair.getKey() + " --> " + svURItvURIPair.getValue() + NL;
		}
		*/
		return s;
	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.java.rvl.MappingIF#isDisabled()
	 */
	public boolean isDisabled() {
		if (this.hasDisabled()) {
			return this.getAllDisabled_as().firstValue();
		} else return false;
	}

	public GraphicAttribute getTargetAttribute() throws InsufficientMappingSpecificationException {
		if (hasTargetattribute()) {
			return this.getAllTargetattribute_as().firstValue();
		} else 
			throw new InsufficientMappingSpecificationException();
	}

	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		return ((PropertyMapping) this.castTo(PropertyMapping.class)).getSourceProperty();
	}

	public String explicitlyMappedValuesToString() {
		
		if (null == this.explicitlyMappedValues) {
			explicitlyMappedValues = this.getExplicitlyMappedValues();	
		}
		
		String s = "";

		if(!explicitlyMappedValues.isEmpty()){

			s += "Map of explicit source and target values: " + NL;
			
			for (Entry<Node, Node> entry : explicitlyMappedValues.entrySet()) {
				Node sv = entry.getKey();
				Node tv = entry.getValue();
				s += sv + " --> " + tv + NL;
			}
			
		} else {
			s += "No explicitly mapped source values: " + NL;
		}

		return s;
	}


}
