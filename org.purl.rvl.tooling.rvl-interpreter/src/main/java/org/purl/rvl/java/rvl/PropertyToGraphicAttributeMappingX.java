package org.purl.rvl.java.rvl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Valuemapping;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.rvl.mapping.CalculatedValueMapping;
import org.purl.rvl.tooling.query.mapping.MappingQuery;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;


/**
 * @author Jan Polowinski
 *
 */
public class PropertyToGraphicAttributeMappingX extends PropertyMappingX {
	
	private Property_to_Graphic_AttributeMapping  delegatee;
	
	private final static Logger LOGGER = Logger.getLogger(PropertyToGraphicAttributeMappingX.class .getName()); 
	
	private static final long serialVersionUID = 5391124674649010787L;
	static final String NL =  System.getProperty("line.separator");
	
	private Map<Node, Node> explicitlyMappedValues;
	private Map<Node, Node> calculatedMappedValues;
	private Map<Node, Node> mappedValues;
	private Map<Property,Map<Node, Node>> extendedMappedValuesByExtensionProperty;


	private ValueMappingX getFirstValueMapping() {
		return (ValueMappingX)delegatee.getAllValuemapping_as().firstValue().castTo(ValueMappingX.class);
	}

	
	/**
	 * TODO: clean up
	 * TODO: this only needs to be public since it is used in the parameter mapping evaluation, where
	 * we do not want to calculate over value mappings !?
	 * Creates and returns a map of URIs of all explicitly mapped values (from 1-1 value mappings)
	 * and their target graphic values.
	 * @return
	 */
	public Map<Node, Node> getExplicitlyMappedValues() {
		
		Model model = delegatee.getModel();

		if (null == explicitlyMappedValues || explicitlyMappedValues.isEmpty()) {
			
			LOGGER.finest("Collecting explicitly mapped values ...");
						
			explicitlyMappedValues = new HashMap<Node, Node>();
			
			if (!hasExplicitValueMapping()) {
				return explicitlyMappedValues;
			}
			
			// TODO get value mappings without using sparql
			Property_to_Graphic_AttributeMapping thisGen = ( (Property_to_Graphic_AttributeMapping)delegatee.castTo(Property_to_Graphic_AttributeMapping.class));
			
			try {
				
				BlankNode thisBlank = thisGen.asBlankNode();
				ClosableIterator<Statement> stmtIt =  model.findStatements(thisBlank,delegatee.VALUEMAPPING,Variable.ANY);
				
				while (stmtIt.hasNext()) {
					
					Statement statement = (Statement) stmtIt.next();
					
					LOGGER.finest(statement.toString());
					
					Node sv = RDFTool.getSingleValue(model, statement.getObject().asResource(), ValueMappingX.SOURCEVALUE);
					Node tv = RDFTool.getSingleValue(model, statement.getObject().asResource(), ValueMappingX.TARGETVALUE);
					
					explicitlyMappedValues.put(sv,tv);
				}
				
				LOGGER.fine("Created value map: " + explicitlyMappedValuesToString());
				
				return explicitlyMappedValues;
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			// TODO mappings that are blank nodes issue a class cast exception when toSPARQLed below!
			try {
				delegatee.toSPARQL();
			}
			catch (UnsupportedOperationException e) {
				LOGGER.severe("Problem creating the SPARQL representation of the mapping " + this);
				return explicitlyMappedValues;
			}
			
			// get all subjects and the sv/tv table via SPARQL // TODO here we take all value mappings with a source and target value, but these may include those with more than one sv/tv value set!
			String querySubjectsAndSVtoTVMapForGivenProperty = "" +
					"SELECT DISTINCT ?sv ?tv " +
					"WHERE { " +
						delegatee.toSPARQL() + " <" + delegatee.VALUEMAPPING + "> ?vm ." + 
				    "	?vm <" + ValueMappingX.SOURCEVALUE + "> ?sv . " +
				    "	?vm <" + ValueMappingX.TARGETVALUE + "> ?tv . " + 
					"} ";
			
			QueryResultTable explMapResults = model.sparqlSelect(querySubjectsAndSVtoTVMapForGivenProperty);
			
			for(QueryRow row : explMapResults) {
				explicitlyMappedValues.put(row.getValue("sv"),row.getValue("tv"));
			}
			
			LOGGER.fine("Collected the following explicitly mapped values: " + explicitlyMappedValuesToString());
			
		} else {
			LOGGER.fine("Will use the following cached explicitly mapped values: " + explicitlyMappedValuesToString());
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
			    	p2gam.toSPARQL() + " <" + PropertyToGraphicAttributeMappingX.VALUEMAPPING + "> ?vm ." + 
	//		    	p2gam.toSPARQL() +  " <" + ValueMappingX.TARGETVALUE + "> ?ta. " + 
			    "	?vm <" + ValueMappingX.SOURCEVALUE + "> ?o . " +
			    "	?vm <" + ValueMappingX.TARGETVALUE + "> ?tv . " + 
			    "	?s <" + sp + "> ?o . " +
				"} ";
				
		System.out.println(querySubjectsAndSVtoTVMapForGivenProperty);
		
		Random r = new Random();

		QueryResultTable explMapResults = model.sparqlSelect(querySubjectsAndSVtoTVMapForGivenProperty);
		for(QueryRow row : explMapResults) {
			System.out.println(row);
			// create a new GO
			GraphicObjectX go = new GraphicObjectX(model, "http://purl.org/rvl/example-avm/GO_for_" + r.nextInt(), false);
			// relate it to the resource
			// TODO
			// set target attribute TODO: generic TODO: so many casts necessary???
			ColorX tv = ColorX.getInstance(model, (Resource)row.getValue("tv"));
//			go.setColornamed(tv);
			goSet.add(go);	
			//System.out.println(go);
		}
		
		*/
	}

	/**
	 * Calculates the resource-node-graphic-value-node-pairs from implicit value mappings
	 * TODO: CURRENTLY ONLY ONE VALUE MAPPING IS EVALUATED!
	 * TODO: we ignore other value mapping than the first at the moment! sometimes multiple are allowed!
	 * @param the set of statements that the property mapping currently affects
	 * @throws InsufficientMappingSpecificationException 
	 */
	public void calculateValues(Set<Statement> affectedStatements) throws InsufficientMappingSpecificationException {
	
			calculatedMappedValues = new HashMap<Node, Node>();
			
			Collection<CalculatedValueMapping> cvms = getFirstValueMapping()
					.getCalculatedValueMappings(affectedStatements, (PropertyMappingX)delegatee.castTo(PropertyMappingX.class));
			
			for (Iterator<CalculatedValueMapping> iterator = cvms.iterator(); iterator.hasNext();) {
				CalculatedValueMapping calculatedValueMapping = (CalculatedValueMapping) iterator.next();
				calculatedMappedValues.put(calculatedValueMapping.getSourceValue(),calculatedValueMapping.getTargetValue());
			}
	}

	/**
	 * TODO when the affected statements change the mappings need to be recalculated!
	 * Gets a set of resource-node-graphic-value-node-pairs (possibly calculated from implicit value mappings).
	 * @return - the set of resource-node-graphic-value-node-pairs as it was calculated the last time the calculation was triggered
	 * @throws InsufficientMappingSpecificationException 
	 */
	private Map<Node, Node> getCalculatedValues(Set<Statement> affectedStatements) throws InsufficientMappingSpecificationException {
		
		if ((null == calculatedMappedValues || calculatedMappedValues.isEmpty()) && delegatee.hasValuemapping()) {

			calculateValues(affectedStatements);
		}

		return calculatedMappedValues; 
	}
	
	/**
	 * @return - the resource-node-graphic-value-node-pairs if they have been calculated before, or null otherwise.
	 * @throws InsufficientMappingSpecificationException 
	 */
	public Map<Node, Node> getCalculatedValues() throws InsufficientMappingSpecificationException {

		return getCalculatedValues(null); 
	}
	
	/**
	 * Gets a set of all resource-node-graphic-value-node-pairs including the explicitly mapped ones and those
	 * calculated from implicit value mappings.
	 * @return - a Map of graphic-values by resource node
	 * @throws InsufficientMappingSpecificationException 
	 */
	public Map<Node, Node> getMappedValues() throws InsufficientMappingSpecificationException {
		
		if (null == mappedValues) {
			
			LOGGER.fine("Collecting from implicit and explicit value mappings ...");
			
			mappedValues = new HashMap<Node, Node>();
			
			if (delegatee.hasValuemapping()) {
				
				mappedValues.putAll(getCalculatedValues());
				mappedValues.putAll(getExplicitlyMappedValues());
			}

		} else {
			
			LOGGER.finest("Will use the following cached mapped values: " + mappedValues);
		}
			
		if (null == mappedValues || mappedValues.isEmpty()) {
			LOGGER.warning("Could neither find explicit mappings nor values be calculated " +
					"from implicit value mappings (will return empty set)"); 
		}

		return mappedValues;
	}

	/**
	 * Returns the extended mapping table offering also tv for subclasses of the sv.
	 * Values are cached per extension property.
	 * 
	 * @param modelSet
	 * @param property
	 * @return the extended value mappings for the given extension property
	 * @throws InsufficientMappingSpecificationException 
	 */
	public Map<Node, Node> getExtendedMappedValues(Sparqlable modelSet, Property property) throws InsufficientMappingSpecificationException {
		
		if (null == extendedMappedValuesByExtensionProperty) {
			
			extendedMappedValuesByExtensionProperty = new HashMap<Property, Map<Node,Node>>();
		} 
		
		if (extendedMappedValuesByExtensionProperty.containsKey(property)) {
			
			return extendedMappedValuesByExtensionProperty.get(property);
			
		} else {
			
			Map<Node, Node> svUriTVuriMap = RVLUtils.extendMappingTable(
					modelSet, this.getMappedValues(), property );
			
			extendedMappedValuesByExtensionProperty.put(property, svUriTVuriMap);
	
			return svUriTVuriMap;
		}
		
	}

	public String toStringDetailed(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMappingX class, which is not in the super-class hierarchy
		PropertyMappingX pm = (PropertyMappingX) delegatee.castTo(PropertyMappingX.class);
		s += pm.toStringDetailed();
		
		//targetAttribute is specific to P2GAM
		try {
			GraphicAttribute tga = getTargetAttribute();
			String tgaString = tga.getAllLabel_as().count()>0 ? tga.getAllLabel_as().firstValue() : tga.toString();
			s += "     Target graphic attribute: " + tgaString + NL ;
		} catch (InsufficientMappingSpecificationException e1) {
			s += "     Target graphic attribute missing." + NL ;
		}

		if (this.hasValuemapping()) {

			try {
				if(!getMappedValues().isEmpty()){
					//s += mappedValuesToString();
				}
			} catch (InsufficientMappingSpecificationException e) {
				s += "     An insuffiently specified mapping was found." +NL;
			}
				
			s += "     Value mappings:" + NL;
			
			ClosableIterator<Valuemapping> vmIterator = getValueMappingsAsCI();
			
			while (vmIterator.hasNext()) {
				ValueMappingX vm = (ValueMappingX) vmIterator.next().castTo(ValueMappingX.class);
				s += "" + vm + NL;
			}
		}
		else {
			s += "     (with no value mappings)" + NL;
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

	public boolean hasValuemapping() {
		return delegatee.hasValuemapping();
	}


	public GraphicAttribute getTargetAttribute() throws InsufficientMappingSpecificationException {
		if (hasTargetAttribute()) {
			return (GraphicAttribute) delegatee.getAllTargetattribute_as().firstValue().castTo(GraphicAttribute.class);
		} else 
			throw new InsufficientMappingSpecificationException("No target graphic attribute set, but this is mandatory.");
	}

	private boolean hasTargetAttribute() {
		return delegatee.hasTargetattribute();
	}


	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		return ((PropertyMappingX) delegatee.castTo(PropertyMappingX.class)).getSourceProperty();
	}

	public String explicitlyMappedValuesToString() {
		
		if (null == this.explicitlyMappedValues) {
			explicitlyMappedValues = this.getExplicitlyMappedValues();	
		}
		
		String s = "";

		if (!explicitlyMappedValues.isEmpty()){

			s += "Map of explicitly mapped source values: " + NL;
			s += valueMapToString(explicitlyMappedValues);
			
		} else {
			s += "No explicitly mapped source values. " + NL;
		}

		return s;
	}

	public String toStringSummary() {
		return AVMUtils.getGoodNodeLabel(delegatee, delegatee.getModel());
	}
	
	/**
	 * Check if a mapping has at least one value mapping that defines exactly 1 source value 
	 * -> then it will probably not have implicit value mappings that require calculation.
	 * 
	 * TODO: this assumption is too strong, when the final specification should allow mixing of 
     * explicit and implicit value mappings!
     * 
	 * @return - true if the mapping has at least one explicit value mapping
	 */
	private boolean hasExplicitValueMapping() {
		
		if (! hasValuemapping()) {
			
			return false;
			
		} else {
			
			// does not support blank nodes (but submappings may be anonymous!)
			//return MappingQuery.askIfHasExplicitValueMapping(model, this);
			
			// get value mappings without using SPARQL and check if one of them has only 1 source value
			
				List<ValueMappingX> valueMappings = getValueMappings();
				
				for (ValueMappingX valueMappingX : valueMappings) {
					
					int cardinalityOfSourceValueProperty = valueMappingX.getAllSourcevalue_asNode_().asArray().length;
					
					if (cardinalityOfSourceValueProperty == 1) {
						return true;	
					}
				}

		}
		
		return false;
	}

	/**
	 * @return a List of all value mappings as ValueMappingX
	 */
	private List<ValueMappingX> getValueMappings() {
		
		List<ValueMappingX> valueMappingsX = new ArrayList<ValueMappingX>();
		
		List<Valuemapping> valueMappings = delegatee.getAllValuemapping_as().asList();
		
		for (Valuemapping valuemapping : valueMappings) {
			valueMappingsX.add((ValueMappingX)valuemapping.castTo(ValueMappingX.class));
		}
		
		return valueMappingsX;
	}

	
	private ClosableIterator<Valuemapping> getValueMappingsAsCI() {
		return delegatee.getAllValuemapping_as().asClosableIterator();
	}


	public static String valueMapToString(Map<Node, Node> valueMap) {
		
		String s = "";
		
		for (Entry<Node, Node> entry : valueMap.entrySet()) {
			Node sv = entry.getKey();
			Node tv = entry.getValue();
			s += sv + " --> " + tv + NL;
		}
		
		return s;
	}

}
