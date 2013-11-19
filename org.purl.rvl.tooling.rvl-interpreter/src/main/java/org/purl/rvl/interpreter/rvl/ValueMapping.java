package org.purl.rvl.interpreter.rvl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Container;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.interpreter.gen.rvl.Valuemapping;
import org.purl.rvl.interpreter.gen.rvl.VisualValueList;
import org.purl.rvl.interpreter.mapping.CalculatedValueMapping;

/**
 * @author Jan Polowinski
 *
 */
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
	private Set<Node> targetValuesUnorderedSet; // disctinction necessary? or just store collection?
	int ac;
	
	// Scale of Measurement
	static final int SOM_UNKNOWN = 0;
	static final int SOM_NOMINAL = 1;
	static final int SOM_ORDINAL = 2;
	static final int SOM_QUANTITATIVE = 3;
	
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
				Container svs = 
						this.getAllSourcevalueset_as().firstValue();
				if (null!=svs) {
					 // TODO add all values set via sourceValue to the container and store it as our new unordered set
				}
			}
		}
		// when no source value is defined
		else {
			if(this.hasSourcevalueset()) {
				Container con = 
						this.getAllSourcevalueset_as().firstValue();
			con=this.getAllSourcevalueset_as().firstValue();
				if (null!=con) {
					List<Node> vvlJavaList = con.getAllMember_asNode_().asList();
					sourceValuesUnorderedSet = new HashSet<Node>(vvlJavaList);
                    vvlJavaList.clear();
                   // removeAllExcludesourcevalue();
				}
				//List<Node> cvn =this.getAllExcludesourcevalue_asNode_().asList();
			  // remove(sourceValuesUnorderedSet);
				//System.out.println(cvn);
			}
			else{
				if(this.hasSourcefilter()) {
					determineScaleOfMeasurementOfSourceValues();
				}
				else {
					if(this.hasSourcevalueorderedset()) {
						List<Node> lrs=this.getAllSourcevalueorderedset_asNode_().asList();
						int q=lrs.hashCode();
						//System.out.println("The ordered set is:"+q);
					}
				    	else {
						 if(this.hasSourceinterval()) {
							determineScaleOfMeasurementOfSourceValues();
							if(determineScaleOfMeasurementOfSourceValues()== SOM_ORDINAL) {
								List<Node> cvn =this.getAllExcludesourcevalue_asNode_().asList();
								cvn.clear();
								//removeAllExcludesourcevalue();
							}
							 else {int l=CONTINUOUS_RANGE; }
						}
					}
				}
			}
		}
	}
	
	/**
	 * determines the target values to be used in in this value mapping 
	 * (as a basis for calculating the VM)
	 */
	private void determineAdressedTargetValues() {
		// is a target value defined?
		long numberOfTargetValues = this.getAllTargetvalue_asNode_().asList().size(); 
		if (numberOfTargetValues >= 1) {
			
			// is exactly 1 target value defined?
			if (numberOfTargetValues == 1) {
				addressedTargetValueSituation = 4;
			} 
			// if multiple target values are defined ...
			 else {
				addressedTargetValueSituation = 3;
				// store all values set via targetValue as our new unordered set
				List<Node> ls = this.getAllTargetvalue_asNode_().asList(); // why Node? -> values may be resources or literals
				targetValuesUnorderedSet = new HashSet<Node>(ls);
				
				// do we need to merge them with an additionally defined set?
				VisualValueList vvl = 
						this.getAllTargetvalueset_as().firstValue();
				if (null!=vvl) {
					 // TODO merge all values set via "targetValue" with the "visual value list" and store it as our new unordered set
					List<Node> vvlJavaList = vvl.getAllMember_asNode_().asList();
					targetValuesUnorderedSet.addAll(vvlJavaList);
				}
			}
		}
		// when "target value" is not defined
		else {
			// if a "target value set" is defined
			if(this.hasTargetvalueset()) {
				// TODO refactor redundant code
				VisualValueList vvl = 
						this.getAllTargetvalueset_as().firstValue();
				if (null!=vvl) {
					List<Node> vvlJavaList = vvl.getAllMember_asNode_().asList();
					targetValuesUnorderedSet = new HashSet<Node>(vvlJavaList);
				}

			}
			
		}
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
		s += "  used in PM: " + getPropertyMapping().getAllLabel_as().firstValue() + NL;
		s += "  SoM of SP: " + getSomName(determineScaleOfMeasurementOfSourceValues()) + NL;
		s += "  SoM of TV: " + getSomName(determineScaleOfMeasurementOfTargetValues()) + NL;
		getPropertyMapping();
		return s + "further describe VM here" + NL;
	}
	
	private int determineScaleOfMeasurementOfTargetValues() {
		
		return SOM_UNKNOWN;
	}

	/**
	 * Determine the Scale of Measurement (SoM) of the source values 
	 * handled by this {@link ValueMapping}. At the moment only the globally set SoM is 
	 * considered, which is stated for the source property.
	 * @return
	 */
	 private int determineScaleOfMeasurementOfSourceValues() {	
		// is there a global SoM setting for the source property?
		// such as:  ex:size rdfs:subPropertyOf viso-data:hasQuantitativeSoM
		Property sp = getPropertyMapping().getAllSourceproperty_as().firstValue(); 
		// TODO: reuse these properties!!
		final Property hasNominalSoM = new Property(model,new URIImpl("http://purl.org/viso/data/hasNominalSoM"),false);
		final Property hasOrdinalSoM = new Property(model,new URIImpl("http://purl.org/viso/data/hasOrdinalSoM"),false);
		final Property hasQuantitativeSoM = new Property(model,new URIImpl("http://purl.org/viso/data/hasQuantitativeSoM"),false);
		
		ClosableIterator<Property> spSubPropIterator = sp.getAllSubPropertyOf();
	
		while (spSubPropIterator.hasNext()) {
			Property spSubProp = (Property) spSubPropIterator.next();
			if (spSubProp.equals(hasNominalSoM))
				return SOM_NOMINAL;
			else if (spSubProp.equals(hasOrdinalSoM))
				return SOM_ORDINAL;
			else if (spSubProp.equals(hasQuantitativeSoM)) 
				return SOM_QUANTITATIVE;
		}
		
		// TODO: Add other ways of calculating SoM.
		
		return SOM_UNKNOWN;
		
	}

	private PropertyMapping getPropertyMapping(){
		Resource res = this.getAllValuemapping_Inverse().next()	;
		return new PropertyMapping(model,res,false);
	}
	
	private String getSomName(int somID){
		switch (somID) {
		case 1: return "nominal";
		case 2: return "ordinal";
		case 3: return "quantitative";
		default: return "unknown";
		}
	}

}
