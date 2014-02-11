package org.purl.rvl.java.rvl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
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
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.java.gen.rvl.Interval;
import org.purl.rvl.java.gen.rvl.PropertyMapping;
import org.purl.rvl.java.gen.rvl.Valuemapping;
import org.purl.rvl.java.gen.rvl.VisualValueList;
import org.purl.rvl.java.mapping.CalculatedValueMapping;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class ValueMapping extends Valuemapping implements MappingIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final String NL = System.getProperty("line.separator");
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapping.class.getName()); 

	// ADDRESSED SOURCE AND TARGET VALUES
	static final int NOT_CALCULATED = -1;
	static final int UNKNOWN = 0;
	static final int CONTINUOUS_RANGE = 1;
	static final int ORDERED_SET = 2;
	static final int UNORDERED_SET = 3;
	static final int SINGLE_VALUE = 4;
	
	// SCALE OF MEASUREMENT
	static final int SOM_UNKNOWN = 0;
	static final int SOM_NOMINAL = 1;
	static final int SOM_ORDINAL = 2;
	static final int SOM_QUANTITATIVE = 3;
	
	// TODO: reuse these properties!!
	final Property HAS_NOMINAL_SOM = new Property(model, new URIImpl(
			"http://purl.org/viso/data/hasNominalSoM"), false);
	final Property HAS_ORDINAL_SOM = new Property(model, new URIImpl(
			"http://purl.org/viso/data/hasOrdinalSoM"), false);
	final Property HAS_QUANTITATIVE_SOM = new Property(model, new URIImpl(
			"http://purl.org/viso/data/hasQuantitativeSoM"), false);

	private int addressedSourceValueSituation = NOT_CALCULATED;
	private int addressedTargetValueSituation = NOT_CALCULATED;

	// distinction into set, list necessary? or just store always a collection?
	private Node sourceValuesSingleValue;
	private Set<Node> sourceValuesUnorderedSet; 
	private List<Node> sourceValuesOrderedSet;
	private IntervalX sourceValuesContinuousInterval;

	private Node targetValuesSingleValue;
	private Set<Node> targetValuesUnorderedSet; 
	private List<Node> targetValuesList;
	private IntervalX targetValuesContinuousInterval;


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

	
	public Collection<CalculatedValueMapping> getCalculatedValueMappings() {
		
		calculateValueMappings();
		return cvms;
	}

	/**
	 * Calculate concrete, explicit CalculatedValueMappings which represents
	 * pairs of a (domain data) value and a graphic attribute value. The
	 * calculated values are stored.
	 * 
	 * @return
	 */
	private void calculateValueMappings() {

		getAddressedSourceValueSituation();
		getAddressedTargetValueSituation();

		cvms = new HashSet<CalculatedValueMapping>();
		
		LOGGER.info("Calculating value mappings ... (to be implemented)");


	}
	
	
	
	
	
	/**
	 * determines the target values to be used in in this value mapping (as a
	 * basis for calculating the VM)
	 */
	private int determineAdressedTargetValues() {
		
		LOGGER.info("Determining Target Value (Situation) for " + this.getPropertyMapping().asURI());

		int addressedValueSituation = ValueMapping.UNKNOWN;

		// are values defined via rvl:targetValue?
		List<Node> singleTargetValueList = this.getAllTargetvalue_asNode_().asList();
		long numberOfSingleTargetValues = singleTargetValueList.size();
		
		if (numberOfSingleTargetValues >= 1) {

			// is exactly 1 rvl:targetValue defined?
			if (numberOfSingleTargetValues == 1) {
				addressedValueSituation = ValueMapping.SINGLE_VALUE;
				targetValuesSingleValue = singleTargetValueList.get(0);

			}
			// if multiple rvl:targetValue are defined ...
			else {
				addressedValueSituation = ValueMapping.UNORDERED_SET;
				// store all values set via targetValue as our new unordered set
				// TODO: problem at the moment strings (literals) and resources
				// are allowed, therefore node is used here.
				targetValuesUnorderedSet = new HashSet<Node>(singleTargetValueList);
				// do we need to merge them with an additionally defined set?
				
				// merge with rvl:targetValueSet if this was also defined ...
				if (this.hasTargetvalueset()) {
					targetValuesUnorderedSet.addAll(getTargetValueSet());
				}
				
				// TODO exclude target values
			}
		}
		
		// if no values are defined via rvl:targetValue
		else {
			if (this.hasTargetvalueset() && !this.hasTargetvalueorderedset()) { // also ordered sets are sets!
				
				addressedValueSituation = ValueMapping.UNORDERED_SET;
				targetValuesUnorderedSet = getTargetValueSet();
				
				// TODO: exclude target values
				
			} 
			
			// if no values are defined via rvl:targetValueSet
			else {

				//if (this.hasTargetvalueorderedset()) {
				if (this.hasTargetvalues_abstract_()) {
					
					// TODO handle cycles etc. here and above : if ((this.hasTargetvaluelist()) || (this.hasTargetvalueorderedset()) || (this.hasTargetvaluecycle())) 
					
					addressedValueSituation = ValueMapping.ORDERED_SET;
					
					// TODO: exclude target values
					
					targetValuesList = getTargetValueList();
				}
			
				// if no values are defined via rvl:targetValueOrderedSet
				else {
					
					if (this.hasTargetvalueinterval()) {
						
						if (determineScaleOfMeasurementOfTargetValues() == SOM_ORDINAL) {
							
							addressedValueSituation = ValueMapping.ORDERED_SET;
							
							targetValuesList = calculateOrderedSetFromRange();
							
							// TODO: exclude target values
							
						} else {
							
							addressedValueSituation = ValueMapping.CONTINUOUS_RANGE;
							
							targetValuesContinuousInterval = new IntervalX(getTargetValueInterval());
							
						}
						
					}
				}
			}
		}

		return addressedValueSituation;
	}

	


	private int determineAdressedSourceValues() {
		
		LOGGER.info("Determining Source Value (Situation) for " + this.getPropertyMapping().asURI());

		int addressedValueSituation = ValueMapping.UNKNOWN;

		// are values defined via rvl:sourceValue?
		List<Node> singleSourceValueList = this.getAllSourcevalue_asNode_().asList();
		long numberOfSingleSourceValues = singleSourceValueList.size();
		
		if (numberOfSingleSourceValues >= 1) {

			// is exactly 1 rvl:sourceValue defined?
			if (numberOfSingleSourceValues == 1) {
				addressedValueSituation = ValueMapping.SINGLE_VALUE;
				sourceValuesSingleValue = singleSourceValueList.get(0);

			}
			// if multiple rvl:sourceValue are defined ...
			else {
				addressedValueSituation = ValueMapping.UNORDERED_SET;
				// store all values set via sourceValue as our new unordered set
				// TODO: problem at the moment strings (literals) and resources
				// are allowed, therefore node is used here.
				sourceValuesUnorderedSet = new HashSet<Node>(singleSourceValueList);
				// do we need to merge them with an additionally defined set?
				
				// merge with rvl:sourceValueSet if this was also defined ...
				if (this.hasSourcevalueset()) {
					sourceValuesUnorderedSet.addAll(getSourceValueSet());
				}
				
				// TODO exclude source values
			}
		}
		
		// if no values are defined via rvl:sourceValue
		else {
			if (this.hasSourcevalueset() && !this.hasSourcevalueorderedset()) { // also ordered sets are sets!
				
				addressedValueSituation = ValueMapping.UNORDERED_SET;
				sourceValuesUnorderedSet = getSourceValueSet();
				
				// TODO: exclude source values
				
			} 
			
			// if no values are defined via rvl:sourceValueSet
			else {
				
				if (this.hasSourcefilter()) {
					
					determineScaleOfMeasurementOfSourceValues();
					
					// TODO handle filters
					
				} 
				
				// if no values are defined via rvl:sourceFilter
				else {

					if (this.hasSourcevalueorderedset()) {
						
						addressedValueSituation = ValueMapping.ORDERED_SET;
						
						// TODO: exclude source values
						
						sourceValuesOrderedSet = getSourceValueOrderedSet();
					}
				
					// if no values are defined via rvl:sourceValueOrderedSet
					else {
						
						if (this.hasSourceinterval()) {
							
							if (determineScaleOfMeasurementOfSourceValues() == SOM_ORDINAL) {
								
								addressedValueSituation = ValueMapping.ORDERED_SET;
								
								sourceValuesOrderedSet = calculateOrderedSetFromRange();
								
								// TODO: exclude source values
								
							} else {
								
								addressedValueSituation = ValueMapping.CONTINUOUS_RANGE;
								
								sourceValuesContinuousInterval = new IntervalX(getSourceValueInterval());
								
							}
							
						}
					}
				}
			}
		}

		return addressedValueSituation;
	}
	

	/**
	 * Checks whether this value mapping only defines a 1-to-1 manual mapping of
	 * values
	 * 
	 * @return
	 */
	public Boolean isManualValueMapping() {
		if (singleSourceValueDefined() && singleTargetValueDefined())
			return true;
		else
			return false;
	}

	private boolean singleTargetValueDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean singleSourceValueDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	private int determineScaleOfMeasurementOfTargetValues() {

		return SOM_UNKNOWN;
	}

	/**
	 * Determine the Scale of Measurement (SoM) of the source values handled by
	 * this {@link ValueMapping}. At the moment only the globally set SoM is
	 * considered, which is stated for the source property.
	 * 
	 * @return
	 */
	private int determineScaleOfMeasurementOfSourceValues() {
		
		// is there a global SoM setting for the source property?
		// such as: ex:size rdfs:subPropertyOf viso-data:hasQuantitativeSoM
		Property sp;
		
		try {
			
			sp = ((org.purl.rvl.java.rvl.PropertyMapping)getPropertyMapping().castTo(org.purl.rvl.java.rvl.PropertyMapping.class)).getSourceProperty();
			
			ClosableIterator<Property> spSubPropIterator = sp.getAllSubPropertyOf();
	
			while (spSubPropIterator.hasNext()) {
				Property spSubProp = (Property) spSubPropIterator.next();
				if (spSubProp.equals(HAS_NOMINAL_SOM))
					return SOM_NOMINAL;
				else if (spSubProp.equals(HAS_ORDINAL_SOM))
					return SOM_ORDINAL;
				else if (spSubProp.equals(HAS_QUANTITATIVE_SOM))
					return SOM_QUANTITATIVE;
			}
		
		} catch (InsufficientMappingSpecificationExecption e) {
			// TODO Auto-generated catch block
			LOGGER.warning(e.getMessage() + " --> Could not determine scale of measurement.");
		}

		// TODO: Add other ways of calculating SoM.

		return SOM_UNKNOWN;

	}

	private PropertyMapping getPropertyMapping() {
		Resource res = this.getAllValuemapping_Inverse().next();
		return new PropertyMapping(model, res, false);
	}

	
	
	private String getSomName(int somID) {
		
		switch (somID) {
		
			case SOM_NOMINAL:
				return "nominal";
			case SOM_ORDINAL:
				return "ordinal";
			case SOM_QUANTITATIVE:
				return "quantitative";
			default:
				return "unknown";
		}
	}

	private String getNameForValueSituation(int situationID) {

		switch (situationID) {
		
			case UNKNOWN:
				return "unknown";
			case CONTINUOUS_RANGE:
				return "cont. range";
			case ORDERED_SET:
				return "ordered set or list";
			case UNORDERED_SET:
				return "set";
			case SINGLE_VALUE:
				return "single value";
			default:
				return "unknown";
		}
	}

	public boolean isDisabled() {
		if (this.hasDisabled()) {
			return this.getAllDisabled_as().firstValue();
		} else
			return false;
	}
	
	private Set<Node> getSourceValueSet() {
		
		HashSet<Node> set = null;
		
		if (this.hasSourcevalueset()) {

			List<Node> sourceValueSetJavaList = RVLUtils.rdfs2JavaList(this.getAllSourcevalueset_as().firstValue());
			
			set = new HashSet<Node>(sourceValueSetJavaList);
		}
		return set;
	}
	
private Set<Node> getTargetValueSet() {
	
	HashSet<Node> set = null;
	List<Node> list = null;
	
	if (this.hasTargetvalueset()) {
			
			list = RVLUtils.rdfs2JavaList(
					(org.ontoware.rdfreactor.schema.rdfs.List)
					this.getAllTargetvalueset_as().firstValue().castTo(
							org.ontoware.rdfreactor.schema.rdfs.List.class
							)
					);
		
		set = new HashSet<Node>(list);
	}
	return set;
}
	
private List<Node> getSourceValueOrderedSet() {
		
		List<Node> orderedSet = null;
		
		if (this.hasSourcevalueorderedset()) {
			
			orderedSet = RVLUtils.rdfs2JavaList(this.getAllSourcevalueorderedset_as().firstValue());
			
		}
		return orderedSet; 
	}

private List<Node> getTargetValueList() {
	
	List<Node> list = null;
	
	// TODO handle cylcles etc. as well: 	
	
	//if (this.hasTargetvalueorderedset()) {
	if (this.hasTargetvalues_abstract_()) {
		
		list = RVLUtils.rdfs2JavaList(
				(org.ontoware.rdfreactor.schema.rdfs.List)
				//this.getAllTargetvalueorderedset_as().firstValue().castTo(
				this.getAllTargetvalues_abstract__as().firstValue().castTo(
						org.ontoware.rdfreactor.schema.rdfs.List.class
						)
				);
	}
	
	if (invertOrderOfTargetValues()) {
		Collections.reverse(list);
	}
	
	return list; 
}


private boolean invertOrderOfTargetValues() {

	boolean invert = false;
	
	if (this.hasInvertorderoftargetvalues()) {
		
		invert = getAllInvertorderoftargetvalues_as().firstValue();
		
	}
	return invert;
}

private List<Node> calculateOrderedSetFromRange() {
	LOGGER.warning("ordered set cannot yet be derived from an  ordinal range.");
	return null;
}

private Interval getSourceValueInterval() {
	
	Interval interval = null;
	
	if (this.hasSourceinterval()) {
		
		interval = getAllSourceinterval_as().firstValue();
		
	}
	return interval;
}

private Interval getTargetValueInterval() {
	
	Interval interval = null;
	
	if (this.hasTargetvalueinterval()) {
		
		interval = getAllTargetvalueinterval_as().firstValue();
		
	}
	return interval;
}

private String printAddressedSourceValues(int addressedSourceValueSituation) {
	
	String s = "";
	
	try {

		switch (addressedSourceValueSituation) {
		
			case SINGLE_VALUE:
				s += "Single source value: " ;
				s += sourceValuesSingleValue.toString();
				break;

			case UNORDERED_SET:
				s += "Source value unordered set: ";
				s += sourceValuesUnorderedSet.toString();
				break;

			case ORDERED_SET:
				s += "Source value ordered set: ";
				s += sourceValuesOrderedSet.toString();
				break;

			case CONTINUOUS_RANGE:
				s += "Continuous range of source values: ";
				s += sourceValuesContinuousInterval.toString();
				break;
		}
	
	} catch (NullPointerException e) {
		
		String warning = "Could not describe the addressed source values for the " +
				"situation " + getNameForValueSituation(addressedSourceValueSituation);
		LOGGER.warning(warning);
		s += warning;
	}

	return s;
}


private String printAddressedTargetValues(int addressedTargetValueSituation) {
String s = "";
	
	try {

		switch (addressedTargetValueSituation) {
		
			case SINGLE_VALUE:
				s += "Single target value: " ;
				s += targetValuesSingleValue.toString();
				break;

			case UNORDERED_SET:
				s += "Target value unordered set: ";
				s += targetValuesUnorderedSet.toString();
				break;

			case ORDERED_SET:
				s += "Target value list: ";
				s += targetValuesList.toString();
				break;

			case CONTINUOUS_RANGE:
				s += "Continuous range of target values: ";
				s += targetValuesContinuousInterval.toString();
				break;
		}
	
	} catch (NullPointerException e) {
		
		String warning = "Could not describe the addressed target values for the " +
				"situation " + getNameForValueSituation(addressedTargetValueSituation);
		LOGGER.warning(warning);
		s += warning;
	}

	return s;
}

@Override
public String toString() {
	
	String s = "";
	
	// s += getCalculatedValueMappings() + NL;
	
	//s += "        used in PM: "
	//		+ getPropertyMapping().getAllLabel_as().firstValue() + NL;
	s += "        SoM of SP: "
			+ getSomName(determineScaleOfMeasurementOfSourceValues()) + NL;
	s += "        SoM of TV: "
			+ getSomName(determineScaleOfMeasurementOfTargetValues()) + NL;
	s += "        addressed SV situation: "
			+ getNameForValueSituation(getAddressedSourceValueSituation())
			+ " (" + printAddressedSourceValues(getAddressedSourceValueSituation()) + ")" 
			+ NL;
	s += "        addressed TV situation: "
			+ getNameForValueSituation(getAddressedTargetValueSituation())
			+ " (" + printAddressedTargetValues(getAddressedTargetValueSituation()) + ")" 
			+ NL;
	
	
	return s + NL;
}



/**
 * @return the addressedSourceValueSituation
 */
public int getAddressedSourceValueSituation() {
	
	if (this.addressedSourceValueSituation == NOT_CALCULATED) {
		addressedSourceValueSituation = determineAdressedSourceValues();
	}
	return addressedSourceValueSituation;
}

/**
 * @return the addressedTargetValueSituation
 */
public int getAddressedTargetValueSituation() {
	if (this.addressedTargetValueSituation == NOT_CALCULATED) {
		addressedTargetValueSituation = determineAdressedTargetValues();
	}
	return addressedTargetValueSituation;
}

public boolean isDiscretize(){
	
	if (hasDiscretize()) {
		return this.getAllDiscretize_as().firstValue();
	} else return false;

}

}