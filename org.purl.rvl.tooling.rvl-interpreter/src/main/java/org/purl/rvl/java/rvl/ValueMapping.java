package org.purl.rvl.java.rvl;

import java.util.ArrayList;
import java.util.Collection;
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

	//private int addressedSourceValueSituation = 0;
	//private int addressedTargetValueSituation = 0;

	// distinction into set, list necessary? or just store always a collection?
	private Node sourceValuesSingleValue;
	private Set<Node> sourceValuesUnorderedSet; 
	private List<Node> sourceValuesOrderedSet;
	private Interval sourceValuesContinuousInterval;
	// how to store a range? using interval? also as a list?


	//private Set<Node> targetValuesUnorderedSet; // disctinction necessary? or just store collection?


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

		determineAdressedSourceValues();
		determineAdressedTargetValues();

		cvms = new HashSet<CalculatedValueMapping>();
		
		LOGGER.info("Calculating value mappings ... (to be implemented)");

		/*
		// discretisation?
		if (this.getAllDiscretize_as().firstValue() != null
				&& this.getAllDiscretize_as().firstValue() == true)
			System.out
					.println("the target value range should be discretised here ...");
					*/
	}
	
	
	
	
	
	
	private int determineAdressedTargetValues() {
		// TODO Auto-generated method stub
		return 0;
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
								
								sourceValuesContinuousInterval = getSourceValueInterval();
								
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
			
			// get the rvl:sourceValueSet as an rdfs-list (not a java list):
			org.ontoware.rdfreactor.schema.rdfs.List sourceValueSetList =
				this.getAllSourcevalueset_as().firstValue();
			
			List<Node> sourceValueSetJavaList = 
				sourceValueSetList.getAllMember_asNode_().asList();
			
			set = new HashSet<Node>(sourceValueSetJavaList);
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

/**
 * determines the target values to be used in in this value mapping (as a
 * basis for calculating the VM)
 */ /*
private int determineAdressedTargetValues() {

	int addressedTargetValueSituation = 0; // ValueMapping.UNKNOWN;

	// is a target value defined?
	long numberOfTargetValues = this.getAllTargetvalue_asNode_().asList()
			.size();
	if (numberOfTargetValues >= 1) {

		// is exactly 1 target value defined?
		if (numberOfTargetValues == 1) {
			addressedTargetValueSituation = ValueMapping.SINGLE_VALUE;
		}
		// if multiple target values are defined ...
		else {
			addressedTargetValueSituation = ValueMapping.UNORDERED_SET;
			// store all values set via targetValue as our new unordered set
			List<Node> ls = this.getAllTargetvalue_asNode_().asList(); // why
																		// Node?
																		// ->
																		// values
																		// may
																		// be
																		// resources
																		// or
																		// literals
			targetValuesUnorderedSet = new HashSet<Node>(ls);

			// do we need to merge them with an additionally defined set?
			// Set<Node> s=Sets.union(targetValuesUnorderedSet,
			// targetValueSet );
			List<Node> cvn = this.getAllExcludesourcevalue_asNode_()
					.asList();
			cvn.clear();
			VisualValueList vvl = this.getAllTargetvalueset_as()
					.firstValue();
			if (null != vvl) {
				// TODO merge all values set via "targetValue" with the
				// "visual value list" and store it as our new unordered set
				List<Node> vvlJavaList = vvl.getAllMember_asNode_()
						.asList();
				targetValuesUnorderedSet.addAll(vvlJavaList);
			}
		}
	}
	// when "target value" is not defined
	else {
		// if a "target value set" is defined
		if (this.hasTargetvalueset()) {
			// TODO refactor redundant code
			VisualValueList vvl = this.getAllTargetvalueset_as()
					.firstValue();
			if (null != vvl) {
				List<Node> vvlJavaList = vvl.getAllMember_asNode_()
						.asList();
				targetValuesUnorderedSet = new HashSet<Node>(vvlJavaList);
				vvlJavaList.clear();
			}
			addressedTargetValueSituation = ValueMapping.UNORDERED_SET;

		} else {
			if ((this.hasTargetvaluelist())
					|| (this.hasTargetvalueorderedset())
					|| (this.hasTargetvaluecycle())) {
				VisualValueList vvl = this
						.getAllTargetvalues_abstract__as().firstValue();
				List<Node> vvlJavaList = vvl.getAllMember_asNode_()
						.asList();
				targetValuesUnorderedSet = new HashSet<Node>(vvlJavaList);
				if (this.hasInvertorderoftargetvalues()) {
					// vvlJavaList = // Use ListUtils or iterate to invert
					// list
				}
				// else { Restricted list}
				addressedTargetValueSituation = ValueMapping.ORDERED_SET;
			} else {
				if (this.hasTargetvalueinterval()) {
					addressedTargetValueSituation = ValueMapping.CONTINUOUS_RANGE;
					determineScaleOfMeasurementOfSourceValues();
					if (determineScaleOfMeasurementOfSourceValues() == SOM_ORDINAL) {
						List<Node> cvn = this
								.getAllExcludesourcevalue_asNode_()
								.asList();
						cvn.clear();
					}
				} else {
					if (this.hasInvertorderoftargetvalues() == true) {
						VisualValueList vvl = this
								.getAllTargetvalueset_as().firstValue();
						List<Node> vvlJavaList = vvl.getAllMember_asNode_()
								.asList();
						// vvlJavaList=this.getAllinvert;
					} else {
						int list = CONTINUOUS_RANGE;
					}
				}
			}
		}

	}

	return addressedTargetValueSituation;
}*/

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

private String printAddressedSourceValues(int addressedSourceValueSituation) {
	
	String s = "";
	
	try {

		switch (addressedSourceValueSituation) {
		
			case SINGLE_VALUE:
				s += "*****Single source value: *******";
				s += sourceValuesSingleValue.toString();

			case UNORDERED_SET:
				s += "******Source value unordered set:********* \n";
				s += sourceValuesUnorderedSet.toString();

			case ORDERED_SET:
				s += "********Source value ordered set:**********\n";
				s += sourceValuesOrderedSet.toString();

			case CONTINUOUS_RANGE:
				s += "*****Continuous range of source values: *******";
				s += sourceValuesContinuousInterval.toString();
		}
	
	} catch (NullPointerException e) {
		
		String warning = "Could not describe the addressed source values for the " +
				"situation " + getNameForValueSituation(addressedSourceValueSituation);
		LOGGER.warning(warning);
		s += warning;
	}

	return s;
}

@Override
public String toString() {
	
	String s = "";
	
	// s += getCalculatedValueMappings() + NL;
	
	s += "        used in PM: "
			+ getPropertyMapping().getAllLabel_as().firstValue() + NL;
	s += "        SoM of SP: "
			+ getSomName(determineScaleOfMeasurementOfSourceValues()) + NL;
	s += "        SoM of TV: "
			+ getSomName(determineScaleOfMeasurementOfTargetValues()) + NL;
	s += "        addressed SV situation: "
			+ getNameForValueSituation(determineAdressedSourceValues())
			+ NL;
	s += "        addressed TV situation: "
			+ getNameForValueSituation(determineAdressedTargetValues())
			+ NL;
	
	s += "        " + printAddressedSourceValues(determineAdressedSourceValues()) + NL;
	
	return s + NL;
}

}