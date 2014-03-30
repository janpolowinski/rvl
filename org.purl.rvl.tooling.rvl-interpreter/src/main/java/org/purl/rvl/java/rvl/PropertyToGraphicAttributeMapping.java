package org.purl.rvl.java.rvl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.velocity.runtime.resource.ResourceCache;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.GraphicAttribute;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Valuemapping;
import org.purl.rvl.java.mapping.CalculatedValueMapping;
import org.purl.rvl.tooling.process.ResourcesCache;
import org.purl.rvl.tooling.util.AVMUtils;


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
		
		// check cash ...
		if (null != explicitlyMappedValues) {
			LOGGER.fine("Found cached value mappings: " + explicitlyMappedValues);
			return explicitlyMappedValues;
		} else {
			LOGGER.fine("Newly calculating value mappings ...");
		}
		
		if (!hasValuemapping()) {
			explicitlyMappedValues = new HashMap<Node, Node>();
			return explicitlyMappedValues;
		}
		
		// try to get explicit ones
		if (null == explicitlyMappedValues || explicitlyMappedValues.isEmpty()) {
						
			explicitlyMappedValues = new HashMap<Node, Node>();
			
			// TODO get value mappings without using sparql
			Property_to_Graphic_AttributeMapping thisGen = ( (Property_to_Graphic_AttributeMapping)this.castTo(Property_to_Graphic_AttributeMapping.class));
			
			try {
				BlankNode thisBlank = thisGen.asBlankNode();
				ClosableIterator<Statement> stmtIt =  model.findStatements(thisBlank,VALUEMAPPING,Variable.ANY);
				
				while (stmtIt.hasNext()) {
					
					Statement statement = (Statement) stmtIt.next();
					
					LOGGER.finest(statement.toString());
					
					Node sv = RDFTool.getSingleValue(model, statement.getObject().asResource(), ValueMapping.SOURCEVALUE);
					Node tv = RDFTool.getSingleValue(model, statement.getObject().asResource(), ValueMapping.TARGETVALUE);
					
					explicitlyMappedValues.put(sv,tv);
				}
				
				LOGGER.fine("Created value map: " + explicitlyMappedValuesToString());
				
				return explicitlyMappedValues;
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			// TODO mappings that are blank nodes issue a class cast exception when toSPARQLed below!
			try {
				this.toSPARQL();
			}
			catch (UnsupportedOperationException e) {
				LOGGER.severe("Problem creating the SPARQL representation of the mapping " + this);
				return explicitlyMappedValues;
			}
			
			// get all subjects and the sv/tv table via SPARQL // TODO here we take all value mappings with a source and target value, but these may include those with more than one sv/tv value set!
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
		
		// when this fails, try to get calculated values
		if (null == explicitlyMappedValues || explicitlyMappedValues.isEmpty()) {
			
			LOGGER.fine("Could not find explicitly stated 1-1 value mappings, will try to calculate value mappings ... "); // TODO check this ...
			
			try {
				explicitlyMappedValues = getCalculatedValues(null); // TODO null OK???
			} catch (Exception e) {
				LOGGER.fine("Could neither find explicit nor calculate value mappings (will return empty set)"); 
			}
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

	/**
	 * Gets a set of explicit value mappings (resource-node-graphic-value-node-pairs) from implicit value mappings.
	 * TODO: CURRENTLY ONLY ONE VALUE MAPPING IS EVALUATED!
	 * @param the set of statements that the property mapping currently affects
	 * @return
	 */
	public Map<Node, Node> getCalculatedValues(Set<Statement> affectedStatements) {
		
		if ((null == explicitlyMappedValues || explicitlyMappedValues.isEmpty()) && hasValuemapping()) {

			explicitlyMappedValues = new HashMap<Node, Node>();
			
			// TODO: we ignore other value mapping than the first at the moment! sometimes multiple are allowed!
			
			Collection<CalculatedValueMapping> cvms = getFirstValueMapping().getCalculatedValueMappings(affectedStatements);
			
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

	public String toStringDetailed(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMapping class, which is not in the super-class hierarchy
		PropertyMapping pm = (PropertyMapping) this.castTo(PropertyMapping.class);
		s += pm.toStringDetailed();
		
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
			s += "No explicitly mapped source values. " + NL;
		}

		return s;
	}

	public String toStringSummary() {
		return AVMUtils.getGoodLabel(this, model);
	}

	
	public PropertyToGraphicAttributeMapping tryReplaceWithCashedInstanceForSameURI(PropertyToGraphicAttributeMapping mapping) {
		return (PropertyToGraphicAttributeMapping) ResourcesCache.getInstance().tryReplaceOrCache(mapping);
	}
}
