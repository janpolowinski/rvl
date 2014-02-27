package org.purl.rvl.java.rvl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Container;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.Interval;
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

	
	static final int NOT_CALCULATED = -1;
	static final int UNKNOWN = 0;
	
	// ADDRESSED SOURCE AND TARGET VALUES
	static final int CONTINUOUS_RANGE = 1;
	static final int ORDERED_SET = 2;
	static final int UNORDERED_SET = 3;
	static final int SINGLE_VALUE = 4;
	
	// MAPPING SITUATIONS FROM MATRIX
	static final int CC = 1;
	static final int CC_D = 2;
	static final int CO = 3;
	static final int CU = 4;
	static final int OC = 5;
	static final int OO = 6;
	static final int OU = 7;
	static final int UC = 8;
	static final int UO = 9;
	static final int UU = 10;
	static final int SS = 11;

	
	
	// SCALE OF MEASUREMENT
	static final int SOM_UNKNOWN = 0;
	static final int SOM_NOMINAL = 1;
	static final int SOM_ORDINAL = 2;
	static final int SOM_QUANTITATIVE = 3;
	
	// TODO: reuse these properties!!
	final Property HAS_NOMINAL_VALUE = new Property(model, new URIImpl(
			"http://purl.org/viso/data/has_nominal_value"), false);
	final Property HAS_ORDINAL_VALUE = new Property(model, new URIImpl(
			"http://purl.org/viso/data/has_ordinal_value"), false);
	final Property HAS_QUANTITATIVE_VALUE = new Property(model, new URIImpl(
			"http://purl.org/viso/data/has_quantitative_value"), false);

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

	private Set<Statement> statementSet;  // the set of statements that the property mapping currently affects
	
	
	
	

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

	
	/**
	 * Determine the Scale of Measurement (SoM) of the target graphic relation handled by
	 * this {@link ValueMapping}. Here only the globally set SoM is
	 * considered, which is stated as a super-property of the target graphic relation.
	 * 
	 * @return the scale of measurement ID as integer
	 */
	private int getExplicitScaleOfMeasurementOfTargetGR() {
		
		// is there a global SoM setting for the source property?
		// such as: ex:size rdfs:subPropertyOf viso-data:has_quantitative_value
		Property targetGraphicRelation;
		
		try {
			
			targetGraphicRelation = ((org.purl.rvl.java.rvl.PropertyMapping)getPropertyMapping().castTo(org.purl.rvl.java.rvl.PropertyMapping.class)).getTargetGraphicRelation();
			
			return getExplicitlyStatedScaleOfMeasurement(targetGraphicRelation);
		
		} catch (InsufficientMappingSpecificationException e) {
			// TODO Auto-generated catch block
			LOGGER.warning(e.getMessage() + " --> Could not determine scale of measurement for target graphic relation.");
		}

		// TODO: Add other ways of calculating SoM.

		return SOM_UNKNOWN;

	}

	/**
	 * (TODO: Determine the Scale of Measurement (SoM) of the source values handled by
	 * this {@link ValueMapping}.) Here only the globally set SoM is
	 * considered, which is stated for the source property.
	 * 
	 * @return the scale of measurement ID as integer
	 */
	private int getExplicitScaleOfMeasurementOfSourceProperty() {
		
		// is there a global SoM setting for the source property?
		// such as: ex:size rdfs:subPropertyOf viso-data:hasQuantitativeSoM
		Property sp;
		
		try {
			
			sp = ((org.purl.rvl.java.rvl.PropertyMapping)getPropertyMapping().castTo(org.purl.rvl.java.rvl.PropertyMapping.class)).getSourceProperty();
			
			return getExplicitlyStatedScaleOfMeasurement(sp);
		
		} catch (InsufficientMappingSpecificationException e) {
			// TODO Auto-generated catch block
			LOGGER.warning(e.getMessage() + " --> Could not determine scale of measurement.");
		}

		// TODO: Add other ways of calculating SoM.

		return SOM_UNKNOWN;

	}

	/**
	 * Get the Scale of Measurement which is (eventually) stated explicitly for the property
	 * @param property
	 * @return
	 */
	private int getExplicitlyStatedScaleOfMeasurement(Property property) {
		
		ClosableIterator<Property> subPropIt = property.getAllSubPropertyOf();

		while (subPropIt.hasNext()) {
			Property spSubProp = (Property) subPropIt.next();
			if (spSubProp.equals(HAS_NOMINAL_VALUE))
				return SOM_NOMINAL;
			else if (spSubProp.equals(HAS_ORDINAL_VALUE))
				return SOM_ORDINAL;
			else if (spSubProp.equals(HAS_QUANTITATIVE_VALUE))
				return SOM_QUANTITATIVE;
		}
		
		return SOM_UNKNOWN;
	}

	private List<Node> getSourceValues() {
		return this.getAllSourcevalue_asNode_().asList();
	}

	private Set<Node> getSourceValueSet() {
		
		HashSet<Node> set = null;
		
		if (this.hasSourcevalueset()) {

			List<Node> sourceValueSetJavaList = RVLUtils.rdfs2JavaList(this.getAllSourcevalueset_as().firstValue());
			
			set = new HashSet<Node>(sourceValueSetJavaList);
			
			LOGGER.fine("Unordered set of source values: " + set);
		}
		return set;
	}
	
private List<Node> getSourceValueOrderedSet() {
		
		List<Node> orderedSet = null;
		
		if (this.hasSourcevalueorderedset()) {
			
			orderedSet = RVLUtils.rdfs2JavaList(this.getAllSourcevalueorderedset_as().firstValue());
			
			LOGGER.fine("Ordered set of source values: " + orderedSet);
			
		}
		return orderedSet; 
	}

private Interval getSourceValueInterval() {
	
	Interval interval = null;
	
	if (this.hasSourceinterval()) {
		
		interval = getAllSourceinterval_as().firstValue();
		
	}
	return interval;
}

private List<Node> getTargetValues() {
	return this.getAllTargetvalue_asNode_().asList();
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
		
		LOGGER.fine("Unordered set of target values: " + set);
		
	}
	return set;
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
	
	LOGGER.fine("List of target values: " + list);
	
	return list; 
}


private Interval getTargetValueInterval() {
	
	Interval interval = null;
	
	if (this.hasTargetvalueinterval()) {
		
		interval = getAllTargetvalueinterval_as().firstValue();
		
	}
	return interval;
}

private List<Node> calculateOrderedSetFromRange() {
	LOGGER.warning("ordered set cannot yet be derived from an  ordinal range.");
	return null;
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

/**
 * determines the target values to be used in in this value mapping (as a
 * basis for calculating the VM)
 */
private int determineAdressedTargetValues() {
	
	LOGGER.info("Determining Target Value (Situation) for " + this.getPropertyMapping().asURI());

	int addressedValueSituation = ValueMapping.UNKNOWN;

	// are values defined via rvl:targetValue?
	List<Node> singleTargetValueList = getTargetValues();
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
					
					if (getExplicitScaleOfMeasurementOfTargetGR() == SOM_ORDINAL) {
						
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
	List<Node> singleSourceValueList = getSourceValues();
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
				
				getExplicitScaleOfMeasurementOfSourceProperty();
				
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
						
						if (getExplicitScaleOfMeasurementOfSourceProperty() == SOM_ORDINAL) {
							
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
 * values. Currently triggers determining the source and target value situation.
 * 
 * @return
 */
public Boolean isManualValueMapping() {
	
	if (addressedSourceValueSituation == NOT_CALCULATED || addressedTargetValueSituation == NOT_CALCULATED) {
		determineAdressedSourceValues();
		determineAdressedTargetValues();
	}
	
	if (addressedSourceValueSituation == SINGLE_VALUE && 
		addressedTargetValueSituation == SINGLE_VALUE)
		return true;
	else
		return false;
}


public int calculateMappingSituation(){
	
	int svSituation = determineAdressedSourceValues();
	int tvSituation = determineAdressedTargetValues();
	
	if(svSituation == CONTINUOUS_RANGE && tvSituation == CONTINUOUS_RANGE && isDiscretize()) {
		return CC_D;
	} else if(svSituation == CONTINUOUS_RANGE && tvSituation == CONTINUOUS_RANGE) {
		return CC;
	} else if(svSituation == CONTINUOUS_RANGE && tvSituation == ORDERED_SET) {
		return CO;
	} else if(svSituation == CONTINUOUS_RANGE && tvSituation == UNORDERED_SET) {
		return CU;
	} else if(svSituation == ORDERED_SET && tvSituation == CONTINUOUS_RANGE) {
		return OC;
	} else if(svSituation == ORDERED_SET && tvSituation == ORDERED_SET) {
		return OO;
	} else if(svSituation == ORDERED_SET && tvSituation == UNORDERED_SET) {
		return OU;
	} else if(svSituation == UNORDERED_SET && tvSituation == CONTINUOUS_RANGE) {
		return UC;
	} else if(svSituation == UNORDERED_SET && tvSituation == ORDERED_SET) {
		return UO;
	} else if(svSituation == UNORDERED_SET && tvSituation == UNORDERED_SET) {
		return UU;
	} else if(svSituation == SINGLE_VALUE && tvSituation == SINGLE_VALUE) {
		return SS;
	} else {
		return UNKNOWN; 
	}
}

/**
 * Calculate concrete, explicit CalculatedValueMappings which represents
 * pairs of a (domain data) value and a graphic attribute value. The
 * calculated values are stored.
 * @param theStatementWithOurObject 
 * 
 * @return
 */
private void calculateValueMappings(Set<Statement> statementSet) {
	
	this.statementSet = statementSet;

	try {
		cvms = calculateValueMappingsForCase(calculateMappingSituation());
	} catch (UnexpressiveMappingSpecificationException e) {
		LOGGER.warning("Value mappings couldn't be calculated: " + e.getMessage());
	}

}


/**
 * Calculate concrete, explicit CalculatedValueMappings which represents
 * pairs of a (domain data) value and a graphic attribute value. The
 * calculated values are stored.
 * @return 
 * 
 * @return
 * @throws UnexpressiveMappingSpecificationException 
 */
private Set<CalculatedValueMapping> calculateValueMappingsForCase(int caseID) throws UnexpressiveMappingSpecificationException {

	cvms = new HashSet<CalculatedValueMapping>();
	
	LOGGER.info("Calculating value mappings for mapping case " + getMappingCaseName(caseID));
	
	if (SS == caseID){
		
		LOGGER.info("1-1 Value mappings should currently  be handled separately as simple PGAM and will not be considered here.");
		
	} else if (OO == caseID){
		
		Iterator<Node> svIt = sourceValuesOrderedSet.iterator();
		Iterator<Node> tvIt = targetValuesList.iterator();
				
		if (sourceValuesOrderedSet.size() <= targetValuesList.size() ) {
			
			// TODO ignored stretching for the moment
			while (svIt.hasNext() && tvIt.hasNext()) {
				
				Node sv = svIt.next();
				Node tv = tvIt.next();
				
				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
			}
		} 
		else { // sourceValuesOrderedSet.size() > targetValuesList.size()

			while (svIt.hasNext() && tvIt.hasNext()) {
				
				LOGGER.warning("Not enough distinct target values defined (" + targetValuesList.size() + ") " +
						"for the number of source values (" +  sourceValuesOrderedSet.size() + "), " +
						"will cycle values.");

				Node sv = svIt.next();
				Node tv = tvIt.next();

				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
				
				// reset tv iterator if necessary
				if(!tvIt.hasNext()) tvIt = targetValuesList.iterator();
			}
		}
	} else if (UU == caseID || OU == caseID || UO == caseID){

			
			Iterator<Node> svIt;
			Iterator<Node> tvIt;
			int numberOfSv ;
			int numberOfTv ;
			
			if (null != sourceValuesOrderedSet) {
				svIt = sourceValuesOrderedSet.iterator();
				numberOfSv = sourceValuesOrderedSet.size();
			} 
			else {
				svIt = sourceValuesUnorderedSet.iterator();
				numberOfSv = sourceValuesUnorderedSet.size();
			}
			
			if (null != targetValuesList) {
				tvIt = targetValuesList.iterator();
				numberOfTv = targetValuesList.size();	
			} else {
				tvIt = targetValuesUnorderedSet.iterator();
				numberOfTv = targetValuesUnorderedSet.size();	
			}


			if (numberOfTv == 1) { // all sv get the (same) tv
				
				Node tv = tvIt.next();
				
				while (svIt.hasNext()) {
					
					Node sv = svIt.next();
					cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
				}

			} else if (numberOfSv <= numberOfTv) {
				
				// TODO ignored shuffling to random for the moment
				while (svIt.hasNext() && tvIt.hasNext()) {
					
					Node sv = svIt.next();
					Node tv = tvIt.next();
					
					cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
				}

			} else { // numberOfSv > numberOfTv
				
				throw new UnexpressiveMappingSpecificationException();

			}
			
	} else if (OC == caseID || UC == caseID) {
		
		Iterator<Node> svIt;
		int numberOfSv ;
		int numberOfTv ;
		float lowerBoundValue;
		float upperBoundValue;
		int discreteStepCount = 1;
		float discreteStepSize = -1; 

		if (null != sourceValuesOrderedSet) {
			svIt = sourceValuesOrderedSet.iterator();
			numberOfSv = sourceValuesOrderedSet.size();
		} 
		else {
			svIt = sourceValuesUnorderedSet.iterator();
			numberOfSv = sourceValuesUnorderedSet.size();
		}
		
		// TODO check for discrete step count here
		discreteStepCount = numberOfTv = numberOfSv;
		LOGGER.finest("discrete step count: " + discreteStepCount );

		try {
			
			lowerBoundValue = targetValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("lower bound: " + lowerBoundValue );
			
			upperBoundValue = targetValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("upper bound: " + upperBoundValue );
			
			discreteStepSize = (upperBoundValue - lowerBoundValue)/(discreteStepCount-1);
			LOGGER.finest("discrete step size: " + discreteStepSize );
			
			int step = 0;
			
			while (svIt.hasNext()) {
				
				Node sv = svIt.next();
				Literal tvLiteral = new DatatypeLiteralImpl((lowerBoundValue + step*discreteStepSize) + "", new URIImpl("http://www.w3.org/2001/XMLSchema#float"));
				cvms.add(new CalculatedValueMapping(sv.asURI(), tvLiteral));
				step++;
			}
			
		} catch (Exception e) {
			LOGGER.finest("lower or upper bound is not a literal," +
					" will try to get values from the resource ... TOBEIMPLEMENTED");
		}

	} else if ((CC == caseID || CC_D == caseID ) && null != this.statementSet) {
		
		Set<Statement> statementSet = this.statementSet;
		
		int discreteStepCount = -1; 
		float discreteStepSizeSv = -1; 
		float discreteStepSizeTv = -1; 
		
		float svLowerBoundValue;
		float svUpperBoundValue;
		float tvLowerBoundValue;
		float tvUpperBoundValue;

		try {
			svLowerBoundValue = sourceValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("sv lower bound: " + svLowerBoundValue );
			svUpperBoundValue = sourceValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("sv upper bound: " + svUpperBoundValue );
			tvLowerBoundValue = targetValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("tv lower bound: " + tvLowerBoundValue );
			tvUpperBoundValue = targetValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("tv upper bound: " + tvUpperBoundValue );
			
			for (Iterator<Statement> iterator = statementSet.iterator(); iterator.hasNext();) {
				
				Statement statement = (Statement) iterator.next();
				
				DatatypeLiteral sv = statement.getObject().asDatatypeLiteral();
				float svValue = Float.parseFloat(sv.getValue());
				float tvValue;
				
				LOGGER.finest("Calculating continuous tv for continuous sv-value " + svValue);
				
				Literal tvLiteral = null;
				
				// TODO evaluate out of range settings - crop / cut settings
				
				if (svValue <= svLowerBoundValue) {
					
					tvValue = tvLowerBoundValue;
					
				} else if (svValue >= svUpperBoundValue) {
					
					tvValue = tvUpperBoundValue;
					
				} else { // normal case within ranges
				
					if (isDiscretize() && hasDiscreteStepCount()) {
						
						float svRange = svUpperBoundValue-svLowerBoundValue;
						float tvRange = tvUpperBoundValue-tvLowerBoundValue;
						
						discreteStepCount = getDiscreteStepCount();
						
						if (discreteStepCount >= 2) {
							
							LOGGER.finest("discrete step count: " + discreteStepCount );
							
							discreteStepSizeSv = svRange/(discreteStepCount);
							LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
							
							discreteStepSizeTv = tvRange/(discreteStepCount-1);
							LOGGER.finest("discrete step size tv: " + discreteStepSizeTv );
							
							// the follwoing does not work for sv equal svLowerBoundValue (case caught above)
							tvValue = tvLowerBoundValue + ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)) * discreteStepSizeTv; // TODO this does apparently not work for step count  = {0,1} -> div/0 -> catch earlier
							
							LOGGER.finest("tvValue: " + tvValue );
						}
						else {
							throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
						}
						
					} else {
							float svRange = svUpperBoundValue-svLowerBoundValue;
							float tvRange = tvUpperBoundValue-tvLowerBoundValue;
							float stretchFactor =  tvRange/svRange;
							tvValue = svValue * stretchFactor;
					}
				}
				
				tvLiteral = new DatatypeLiteralImpl(tvValue + "", new URIImpl("http://www.w3.org/2001/XMLSchema#float"));
				
				if (null != tvLiteral) {
					cvms.add(new CalculatedValueMapping(sv, tvLiteral));
				}
				
			}
			
		} catch (UnexpressiveMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			LOGGER.finest("sv/tv lower/upper bound  or sv itself is not a literal or stepcount is < 2," +
					" will try to get values from the resource ... TOBEIMPLEMENTED");
		}
		
		
	} else if (CO == caseID  && null != this.statementSet) { // || CU == caseID) {
		
		Set<Statement> statementSet = this.statementSet;
		
		int discreteStepCount = -1; 
		float discreteStepSizeSv = -1; 
		
		float svLowerBoundValue;
		float svUpperBoundValue;
		
		int numberOfTv ;
		
		// get the number of target values
		if (null != targetValuesList) {
			numberOfTv = targetValuesList.size();
		} 
		else {
			numberOfTv = targetValuesUnorderedSet.size();
		}		
		
		try {
			svLowerBoundValue = sourceValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("sv lower bound: " + svLowerBoundValue );
			svUpperBoundValue = sourceValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("sv upper bound: " + svUpperBoundValue );
			
			for (Iterator<Statement> iterator = statementSet.iterator(); iterator.hasNext();) {
				
				Statement statement = (Statement) iterator.next();
				
				DatatypeLiteral sv = statement.getObject().asDatatypeLiteral();
				float svValue = Float.parseFloat(sv.getValue());
				
				LOGGER.finest("Selecting discrete tv for continuous sv-value " + svValue);
				
				Node tv = null;
				
				// TODO evaluate out of range settings - crop / cut settings
				
				if (svValue <= svLowerBoundValue) {
					
					tv = targetValuesList.get(0);
					
				} else if (svValue >= svUpperBoundValue) {
					
					tv = targetValuesList.get(targetValuesList.size()-1);
					
				} else { 
				
					if (hasDiscreteStepCount()) {
						
						discreteStepCount = getDiscreteStepCount(); // TODO
						
					} else {
						
						discreteStepCount = numberOfTv;
						
						float svRange = svUpperBoundValue-svLowerBoundValue;
						
						if (discreteStepCount >= 2) {
							
							LOGGER.finest("discrete step count: " + discreteStepCount );
							
							discreteStepSizeSv = svRange/(discreteStepCount);
							LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
							
							int tvListPosition = ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)); // this does not work for sv equal svLowerBoundValue (case caught above)
							tv = targetValuesList.get(tvListPosition);				
							LOGGER.finest("tv: " + tv );
							
						}
						else {
							throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
						}
		
					}
				}
				
				if (null != tv) {
					cvms.add(new CalculatedValueMapping(sv, tv));
				}
				
			}
			
		} catch (UnexpressiveMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			LOGGER.finest("sv lower/upper bound  or sv itself is not a literal");
		}
		
		
		
	} else if (CU == caseID && null != this.statementSet) {
		
		Set<Statement> statementSet = this.statementSet;
		
		int discreteStepCount = -1; 
		float discreteStepSizeSv = -1; 
		
		float svLowerBoundValue;
		float svUpperBoundValue;

		int numberOfTv ;
		
		// get the number of target values
		if (null != targetValuesList) {
			numberOfTv = targetValuesList.size();
		} 
		else {
			numberOfTv = targetValuesUnorderedSet.size();
		}		
		
		try {
			svLowerBoundValue = sourceValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("sv lower bound: " + svLowerBoundValue );
			svUpperBoundValue = sourceValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("sv upper bound: " + svUpperBoundValue );
			
			for (Iterator<Statement> iterator = statementSet.iterator(); iterator.hasNext();) {
				
				Statement statement = (Statement) iterator.next();
				
				DatatypeLiteral sv = statement.getObject().asDatatypeLiteral();
				float svValue = Float.parseFloat(sv.getValue());
				
				LOGGER.finest("Selecting discrete, unordered tv for continuous sv-value " + svValue);
				
				List<Node> targetValuesUnorderedSetList = new ArrayList<Node>(targetValuesUnorderedSet);
				
				Node tv = null;
				
				// TODO evaluate out o range settings - crop / cut settings
			
				if (svValue <= svLowerBoundValue || svValue >= svUpperBoundValue) {
					
					// no value mappings calculated for out of range values 
					
				} else { 
		
					discreteStepCount = numberOfTv;
					
					float svRange = svUpperBoundValue-svLowerBoundValue;
					
					if (discreteStepCount >= 2) {
						
						LOGGER.finest("discrete step count: " + discreteStepCount );
						
						discreteStepSizeSv = svRange/(discreteStepCount);
						LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
						
						int svStep = ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)); // this does not work for sv equal svLowerBoundValue (case caught above)
						tv = (Node)targetValuesUnorderedSetList.get(svStep);				
						LOGGER.finest("tv: " + tv );
						
					}
					else if (discreteStepCount == 1) {
						tv = targetValuesUnorderedSetList.get(0);		
					} else { // (0 target values)
						throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
					}
	
				}
				
				if (null != tv) {
					cvms.add(new CalculatedValueMapping(sv, tv));
				}
				
			}
			
		} catch (UnexpressiveMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			LOGGER.finest("sv lower/upper bound  or sv itself is not a literal");
		}
	
	}

	LOGGER.finest("Calculated value mappings: " + cvms);
	
	if (null == this.statementSet) {
		LOGGER.severe("Statament set was not available for calculation (probably null passed as a parameter). Mappings from continuous values may have been skipped.");
	}
		
	return cvms;

}

public Collection<CalculatedValueMapping> getCalculatedValueMappings(Set<Statement> statementSet) {
	
	calculateValueMappings(statementSet);
	return cvms;
}

private boolean invertOrderOfTargetValues() {

	boolean invert = false;
	
	if (this.hasInvertorderoftargetvalues()) {
		
		invert = getAllInvertorderoftargetvalues_as().firstValue();
		
	}
	return invert;
}

public boolean isDisabled() {
	if (this.hasDisabled()) {
		return this.getAllDisabled_as().firstValue();
	} else
		return false;
}

public boolean isDiscretize(){
	
	if (hasDiscretize()) {
		return this.getAllDiscretize_as().firstValue();
	} else 
		return false;
}

public boolean hasDiscreteStepCount() {
	return hasDiscretestepcount();
}

/**
 * @return the addressedTargetValueSituation
 */
public int getDiscreteStepCount() {
	
	if (this.hasDiscreteStepCount()) {
		return getAllDiscretestepcount_as().firstValue();
	} else {
		return -1;
	}
}

private PropertyMapping getPropertyMapping() {
	Resource res = this.getAllValuemapping_Inverse().next();
	return new PropertyMapping(model, res, false);
}

@Override
public String toString() {
	
	String s = "";
	
	// s += getCalculatedValueMappings() + NL;
	
	//s += "        used in PM: "
	//		+ getPropertyMapping().getAllLabel_as().firstValue() + NL;
	s += "        SoM of SP: "
			+ getSomName(getExplicitScaleOfMeasurementOfSourceProperty()) + NL;
	s += "        SoM of TV: "
			+ getSomName(getExplicitScaleOfMeasurementOfTargetGR()) + NL;
	s += "        addressed SV situation: "
			+ getNameForValueSituation(getAddressedSourceValueSituation())
			+ " (" + printAddressedSourceValues(getAddressedSourceValueSituation()) + ")" 
			+ NL;
	s += "        addressed TV situation: "
			+ getNameForValueSituation(getAddressedTargetValueSituation())
			+ " (" + printAddressedTargetValues(getAddressedTargetValueSituation()) + ")" 
			+ NL;
	s += "        mappings case: "
			+ getMappingCaseName(calculateMappingSituation()) 
			+ NL;
	try {
		s += "        calculated value mappings: "
				+ calculateValueMappingsForCase(calculateMappingSituation()) 
				+ NL;
	} catch (UnexpressiveMappingSpecificationException e) {
		s += "        " + e.getMessage() ;
	}
	
	
	
	
	return s + NL;
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

private String getMappingCaseName(int caseID) {
	
	switch (caseID) {
	
	case CC: return "CC";
	case CC_D: return "CCd";
	case CO: return "CO";
	case CU: return "CU";
	case OC: return "OC";
	case OO: return "OO";
	case OU: return "OU";
	case UC: return "UC";
	case UO: return "UO";
	case UU: return "UU";
	case SS: return "SS (single values mapped)";
		default:
			return "unknown";
	}
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

}