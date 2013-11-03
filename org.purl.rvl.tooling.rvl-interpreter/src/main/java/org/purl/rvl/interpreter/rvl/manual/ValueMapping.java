package org.purl.rvl.interpreter.rvl.manual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Container;
import org.purl.rvl.interpreter.mapping.CalculatedValueMapping;
import org.purl.rvl.interpreter.rvl.Valuemapping;

public class ValueMapping extends Valuemapping {

	static final String NL =  System.getProperty("line.separator");
	
	// SET OF ADDRESSED SOURCE VALUES:
	static final int UNKNOWN = 0;
	static final int CONTINUOUS_RANGE = 1;
	static final int ORDERED_SET = 2;
	static final int UNORDERED_SET = 3;
	static final int SINGLE_VALUE = 4;
	private int addressedSourceValueSituation = 0;
	// how to store a range? using interval? also as a list?
	 private Set<Node> sourceValuesUnorderedSet; // disctinction necessary? or just store collection?
	// single source values do not need to be stored
	
	// SET OF ADDRESSED TARGET VALUES:
	private int addressedTargetValueSituation = 0;
	// ...
	
	// cache calculated mappings
	private Set<CalculatedValueMapping> cvms;
	

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
	
	// TODO always return a collection?
	public Collection<CalculatedValueMapping> getCalculatedValueMappings(){
		calculateValueMappings();
		
		System.out.println("addressedSourceValueSituation: " + addressedSourceValueSituation);
		
		//cvms.add(new CalculatedValueMapping("forest","green"));
		//cvms.add(new CalculatedValueMapping("desert","yellow"));
		//cvms.add(new CalculatedValueMapping("sea","blue"));
		return cvms;
	}
	
	/**
	 * Calculate concrete, explicit CalculatedValueMappings which 
	 * represents pairs of a (domain data) value and a graphic attribute value.
	 * The calculated values are stored.
	 * @return
	 */
	private void calculateValueMappings() {
		
		determineAdressedSourceValues();
		determineAdressedTargetValues();

		cvms = new HashSet<CalculatedValueMapping>();
		
		// discretisation?
		if(this.getAllDiscretize_as().firstValue()!=null && this.getAllDiscretize_as().firstValue() == true)
			System.out.println("the target value range should be discretised here ...");
	}


	private void determineAdressedSourceValues() {
		// is a source value defined?
		long numberOfSourceValues = this.getAllSourcevalue_asNode_().asList().size(); 
		if (numberOfSourceValues >= 1) {
			
			// is exactly 1 source value defined?
			if (numberOfSourceValues == 1) {
				addressedSourceValueSituation = 4;
			} 
			// if multiple source values are defined ...
			else {
				addressedSourceValueSituation = 3;
				// store all values set via sourceValue as our new unordered set
				// TODO: problem at the moment strings (literals) and resources are allowed, therefore node is used here. resource does not work somehow
				List<Node> ls = this.getAllSourcevalue_asNode_().asList();
				sourceValuesUnorderedSet = new HashSet<Node>(ls);
				
				// do we need to merge them with an additionally defined set?
				Container svs = this.getAllSourcevalueset_as().firstValue();
				if (null!=svs) {
					 // TODO add all values set via sourceValue to the container and store it as our new unordered set
				}
			}
		}
		// when no source value is defined
		else {
			
		}
	}
	
	private void determineAdressedTargetValues() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Checks whether this value mapping only defines a 1-to-1 manual mappign of values
	 * @return
	 */
	public Boolean isManualValueMapping(){
		if (singleSourceValueDefined() && singleTargetValueDefined()) return true;
		else return false;
	}

	private boolean singleTargetValueDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean singleSourceValueDefined() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += getCalculatedValueMappings() + NL;
		return s + "describe VM here" + NL;
	}
	

}
