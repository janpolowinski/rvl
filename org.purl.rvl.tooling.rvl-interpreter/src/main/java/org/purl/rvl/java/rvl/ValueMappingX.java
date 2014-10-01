package org.purl.rvl.java.rvl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.Interval;
import org.purl.rvl.java.gen.rvl.Valuemapping;
import org.purl.rvl.java.rvl.mapping.CalculatedValueMapping;
import org.purl.rvl.java.rvl.mapping.ValueMapperCC_CCd;
import org.purl.rvl.java.rvl.mapping.ValueMapperCO;
import org.purl.rvl.java.rvl.mapping.ValueMapperCU;
import org.purl.rvl.java.rvl.mapping.ValueMapperOC_UC;
import org.purl.rvl.java.rvl.mapping.ValueMapperOO;
import org.purl.rvl.java.rvl.mapping.ValueMapperUU_OU_UO;
import org.purl.rvl.java.rvl.mapping.ValueMappingUtils;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class ValueMappingX extends Valuemapping {
	
	private static final long serialVersionUID = 1L;
	static final String NL = System.getProperty("line.separator");
	private final static Logger LOGGER = Logger.getLogger(ValueMappingX.class.getName()); 
	
	public static final int NOT_CALCULATED = -1;
	public static final int UNKNOWN = 0;
	
	// ADDRESSED SOURCE AND TARGET VALUES
	public static final int CONTINUOUS_RANGE = 1;
	public static final int ORDERED_SET = 2;
	public static final int UNORDERED_SET = 3;
	public static final int SINGLE_VALUE = 4;
	
	// MAPPING SITUATIONS FROM MATRIX
	public static final int CC = 1;
	public static final int CC_D = 2;
	public static final int CO = 3;
	public static final int CU = 4;
	public static final int OC = 5;
	public static final int OO = 6;
	public static final int OU = 7;
	public static final int UC = 8;
	public static final int UO = 9;
	public static final int UU = 10;
	public static final int SS = 11;

	// SCALE OF MEASUREMENT
	public static final int SOM_UNKNOWN = 0;
	public static final int SOM_NOMINAL = 1;
	public static final int SOM_ORDINAL = 2;
	public static final int SOM_QUANTITATIVE = 3;

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
	
	private Set<CalculatedValueMapping> cvms; // cache calculated mappings
	private PropertyMappingX currentParentPropertyMapping; // reference to the property mapping, for which the value mapping is currently evaluated
	private Set<Statement> affectedStatements;  // the set of statements that the property mapping currently affects


	public ValueMappingX(Model model, URI classURI, Resource instanceIdentifier,
			boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMappingX(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMappingX(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMappingX(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public ValueMappingX(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Determine the Scale of Measurement (SoM) of the target graphic relation handled by
	 * this {@link ValueMappingX}. Here only the globally set SoM is
	 * considered, which is stated as a super-property of the target graphic relation.
	 * 
	 * @return the scale of measurement ID as integer
	 */
	private int getExplicitScaleOfMeasurementOfTargetGR(Property targetGraphicRelation) {
		
		// is there a global SoM setting for the source property?
		// such as: ex:size rdfs:subPropertyOf viso-data:has_quantitative_value
		
		return ValueMappingUtils.getExplicitlyStatedScaleOfMeasurement(targetGraphicRelation);

		// TODO: Add other ways of calculating SoM.
	}

	/**
	 * (TODO: Determine the Scale of Measurement (SoM) of the source values handled by
	 * this {@link ValueMappingX}.) Here only the globally set SoM is
	 * considered, which is stated for the source property.
	 * 
	 * @return the scale of measurement ID as integer
	 */
	private int getExplicitScaleOfMeasurementOfSourceProperty(Property sp) {
		
		// is there a global SoM setting for the source property?
		// such as: ex:size rdfs:subPropertyOf viso-data:hasQuantitativeSoM
		
		return ValueMappingUtils.getExplicitlyStatedScaleOfMeasurement(sp);

		// TODO: Add other ways of calculating SoM.
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
				this.getAllTargetvalues_abstract__as().firstValue().castTo( // TODO wont work in SESAME(?) either
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
 * @throws InsufficientMappingSpecificationException 
 */
public int getAddressedSourceValueSituation() throws InsufficientMappingSpecificationException {
	
	if (this.addressedSourceValueSituation == NOT_CALCULATED) {
		addressedSourceValueSituation = determineAdressedSourceValues();
	}
	return addressedSourceValueSituation;
}

public Node getSourceValuesSingleValue() {
	return sourceValuesSingleValue;
}

public void setSourceValuesSingleValue(Node sourceValuesSingleValue) {
	this.sourceValuesSingleValue = sourceValuesSingleValue;
}

public Set<Node> getSourceValuesUnorderedSet() {
	return sourceValuesUnorderedSet;
}

public void setSourceValuesUnorderedSet(Set<Node> sourceValuesUnorderedSet) {
	this.sourceValuesUnorderedSet = sourceValuesUnorderedSet;
}

public List<Node> getSourceValuesOrderedSet() {
	return sourceValuesOrderedSet;
}

public void setSourceValuesOrderedSet(List<Node> sourceValuesOrderedSet) {
	this.sourceValuesOrderedSet = sourceValuesOrderedSet;
}

public IntervalX getSourceValuesContinuousInterval() {
	return sourceValuesContinuousInterval;
}

public void setSourceValuesContinuousInterval(IntervalX sourceValuesContinuousInterval) {
	this.sourceValuesContinuousInterval = sourceValuesContinuousInterval;
}

public Node getTargetValuesSingleValue() {
	return targetValuesSingleValue;
}

public void setTargetValuesSingleValue(Node targetValuesSingleValue) {
	this.targetValuesSingleValue = targetValuesSingleValue;
}

public Set<Node> getTargetValuesUnorderedSet() {
	return targetValuesUnorderedSet;
}

public void setTargetValuesUnorderedSet(Set<Node> targetValuesUnorderedSet) {
	this.targetValuesUnorderedSet = targetValuesUnorderedSet;
}

public List<Node> getTargetValuesList() {
	return targetValuesList;
}

public void setTargetValuesList(List<Node> targetValuesList) {
	this.targetValuesList = targetValuesList;
}

public IntervalX getTargetValuesContinuousInterval() {
	return targetValuesContinuousInterval;
}

public void setTargetValuesContinuousInterval(IntervalX targetValuesContinuousInterval) {
	this.targetValuesContinuousInterval = targetValuesContinuousInterval;
}

/**
 * @return the addressedTargetValueSituation
 * @throws InsufficientMappingSpecificationException 
 */
public int getAddressedTargetValueSituation() throws InsufficientMappingSpecificationException {
	if (this.addressedTargetValueSituation == NOT_CALCULATED) {
		addressedTargetValueSituation = determineAdressedTargetValues();
	}
	return addressedTargetValueSituation;
}

/**
 * determines the target values to be used in in this value mapping (as a
 * basis for calculating the VM)
 * @throws InsufficientMappingSpecificationException 
 */
private int determineAdressedTargetValues() throws InsufficientMappingSpecificationException {
	
	LOGGER.finer("Determining addressed target value situation");

	int addressedValueSituation = ValueMappingX.UNKNOWN;

	// are values defined via rvl:targetValue?
	List<Node> singleTargetValueList = getTargetValues();
	long numberOfSingleTargetValues = singleTargetValueList.size();
	
	if (numberOfSingleTargetValues >= 1) {

		// is exactly 1 rvl:targetValue defined?
		if (numberOfSingleTargetValues == 1) {
			addressedValueSituation = ValueMappingX.SINGLE_VALUE;
			targetValuesSingleValue = singleTargetValueList.get(0);

		}
		// if multiple rvl:targetValue are defined ...
		else {
			addressedValueSituation = ValueMappingX.UNORDERED_SET;
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
			
			addressedValueSituation = ValueMappingX.UNORDERED_SET;
			targetValuesUnorderedSet = getTargetValueSet();
			
			// TODO: exclude target values
			
		} 
		
		// if no values are defined via rvl:targetValueSet
		else {

			//if (this.hasTargetvalueorderedset()) {
			if (this.hasTargetvalues_abstract_()) { // this.hasTargetvalues_abstract_() alone does not work any more after introducing model set probably because now sesame is used
				
				// TODO handle cycles etc. here and above : if ((this.hasTargetvaluelist()) || (this.hasTargetvalueorderedset()) || (this.hasTargetvaluecycle())) 
				
				addressedValueSituation = ValueMappingX.ORDERED_SET;
				
				// TODO: exclude target values
				
				targetValuesList = getTargetValueList();
			}
		
			// if no values are defined via rvl:targetValueOrderedSet
			else {
				
				if (this.hasTargetvalueinterval()) {
					
					if (getExplicitScaleOfMeasurementOfTargetGR(currentParentPropertyMapping.getTargetGraphicRelation()) == SOM_ORDINAL) {
						
						addressedValueSituation = ValueMappingX.ORDERED_SET;
						
						targetValuesList = calculateOrderedSetFromRange();
						
						// TODO: exclude target values
						
					} else {
						
						addressedValueSituation = ValueMappingX.CONTINUOUS_RANGE;
						
						targetValuesContinuousInterval = new IntervalX(getTargetValueInterval());
						
					}
					
				}
			}
		}
	}

	return addressedValueSituation;
}

private int determineAdressedSourceValues() throws InsufficientMappingSpecificationException {
	
	LOGGER.finer("Determining addressed source value situation");

	int addressedValueSituation = ValueMappingX.UNKNOWN;

	// are values defined via rvl:sourceValue?
	List<Node> singleSourceValueList = getSourceValues();
	long numberOfSingleSourceValues = singleSourceValueList.size();
	
	if (numberOfSingleSourceValues >= 1) {

		// is exactly 1 rvl:sourceValue defined?
		if (numberOfSingleSourceValues == 1) {
			addressedValueSituation = ValueMappingX.SINGLE_VALUE;
			sourceValuesSingleValue = singleSourceValueList.get(0);

		}
		// if multiple rvl:sourceValue are defined ...
		else {
			addressedValueSituation = ValueMappingX.UNORDERED_SET;
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
			
			addressedValueSituation = ValueMappingX.UNORDERED_SET;
			sourceValuesUnorderedSet = getSourceValueSet();
			
			// TODO: exclude source values
			
		} 
		
		// if no values are defined via rvl:sourceValueSet
		else {
			
			if (this.hasSourcefilter()) {
				
				getExplicitScaleOfMeasurementOfSourceProperty(currentParentPropertyMapping.getSourceProperty());
				
				// TODO handle filters
				
			} 
			
			// if no values are defined via rvl:sourceFilter
			else {

				if (this.hasSourcevalueorderedset()) {
					
					addressedValueSituation = ValueMappingX.ORDERED_SET;
					
					// TODO: exclude source values
					
					sourceValuesOrderedSet = getSourceValueOrderedSet();
				}
			
				// if no values are defined via rvl:sourceValueOrderedSet
				else {
					
					if (this.hasSourceinterval()) {
						
						if (getExplicitScaleOfMeasurementOfSourceProperty(currentParentPropertyMapping.getSourceProperty()) == SOM_ORDINAL) {
							
							addressedValueSituation = ValueMappingX.ORDERED_SET;
							
							sourceValuesOrderedSet = calculateOrderedSetFromRange();
							
							// TODO: exclude source values
							
						} else {
							
							addressedValueSituation = ValueMappingX.CONTINUOUS_RANGE;
							
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
 * @throws InsufficientMappingSpecificationException 
 */
/*public Boolean isManualValueMapping() throws InsufficientMappingSpecificationException {
	
	if (addressedSourceValueSituation == NOT_CALCULATED || addressedTargetValueSituation == NOT_CALCULATED) {
		determineAdressedSourceValues();
		determineAdressedTargetValues();
	}
	
	if (addressedSourceValueSituation == SINGLE_VALUE && 
		addressedTargetValueSituation == SINGLE_VALUE)
		return true;
	else
		return false;
}*/


private int calculateMappingSituation() throws InsufficientMappingSpecificationException{
	
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
 * @param affectedStatements - the set of statements that the property mapping currently affects 
 * @param propertyMapping - the PropertyMapping currently processing the value mapping (multiple PM may share the same VM)
 * 
 * @return
 * @throws InsufficientMappingSpecificationException 
 */
private Set<CalculatedValueMapping> calculateValueMappings(Set<Statement> affectedStatements, PropertyMappingX propertyMapping) 
		throws InsufficientMappingSpecificationException {
	
	// TODO: when value mappings are actually reused, keeping this usage specific 
	// information in the state of the VM here will not work!
	this.currentParentPropertyMapping = propertyMapping;
	this.affectedStatements = affectedStatements;

	try {
		
		cvms = calculateValueMappingsForCase(calculateMappingSituation());
		
	} catch (UnexpressiveMappingSpecificationException e) {
		
		LOGGER.warning("Value mappings couldn't be calculated: " + e.getMessage());
		
	}
	
	return cvms;

}


public Set<Statement> getAffectedStatements() {
	return affectedStatements;
}

public void setAffectedStatements(Set<Statement> affectedStatements) {
	this.affectedStatements = affectedStatements;
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
	
	LOGGER.info("Calculating value mappings for mapping case " + ValueMappingUtils.getMappingCaseName(caseID));
	
	if (UNKNOWN == caseID) {
		
		LOGGER.severe("Could not calculate value mappings, since mapping case was unknwon");
		return cvms;
		
	} else if (SS == caseID){
		
		LOGGER.info("1-1 Value mappings should currently be handled separately and will not be considered here.");
		return cvms;
		
	} else if (OO == caseID){
		
		return new ValueMapperOO().calculateValueMappings(this);

	} else if (UU == caseID || OU == caseID || UO == caseID){

		return new ValueMapperUU_OU_UO().calculateValueMappings(this);
			
	} else if (OC == caseID || UC == caseID) {
		
		return new ValueMapperOC_UC().calculateValueMappings(this);

	} else if ((CC == caseID || CC_D == caseID ) && null != this.affectedStatements) {
		
		return new ValueMapperCC_CCd().calculateValueMappings(this);
		
	} else if (CO == caseID  && null != this.affectedStatements) { // || CU == caseID) {

		return new ValueMapperCO().calculateValueMappings(this);
		
	} else if (CU == caseID && null != this.affectedStatements) {
		
		return new ValueMapperCU().calculateValueMappings(this);
		
	}

	LOGGER.finest("Calculated value mappings: " + cvms);
	
	if (null == this.affectedStatements) {
		LOGGER.severe("Statement set was not available for calculation (probably null passed as a parameter). Mappings from continuous values may have been skipped.");
	}
		
	return cvms;

}

/**
 * @param affectedStatements
 * @param propertyMapping - the PropertyMapping currently processing the value mapping (multiple PM may share the same VM)
 * @return
 * @throws InsufficientMappingSpecificationException
 */
public Collection<CalculatedValueMapping> getCalculatedValueMappings(Set<Statement> affectedStatements, PropertyMappingX propertyMapping) 
		throws InsufficientMappingSpecificationException {
	
	if (null == cvms) {
		cvms = calculateValueMappings(affectedStatements, propertyMapping);
	}
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
 * @return the number of discrete steps (for discretization)
 */
public int getDiscreteStepCount() {
	
	if (this.hasDiscreteStepCount()) {
		return getAllDiscretestepcount_as().firstValue();
	} else {
		return -1;
	}
}

public String toStringDetailed() {
	
	String s = "";
	
	try {
		
	s += "        SoM of SP: "
			+ ValueMappingUtils.getSomName(getExplicitScaleOfMeasurementOfSourceProperty(currentParentPropertyMapping.getSourceProperty())) + NL;
	s += "        SoM of TV: "
			+ ValueMappingUtils.getSomName(getExplicitScaleOfMeasurementOfTargetGR(currentParentPropertyMapping.getTargetGraphicRelation())) + NL;
	s += "        addressed SV situation: "
			+ ValueMappingUtils.getNameForValueSituation(getAddressedSourceValueSituation())
			+ " (" + ValueMappingUtils.printAddressedSourceValues(LOGGER, this) + ")" 
			+ NL;
	s += "        addressed TV situation: "
			+ ValueMappingUtils.getNameForValueSituation(getAddressedTargetValueSituation())
			+ " (" + ValueMappingUtils.printAddressedTargetValues(LOGGER, this) + ")" 
			+ NL;
	s += "        mappings case: "
			+ ValueMappingUtils.getMappingCaseName(calculateMappingSituation()) 
			+ NL;
	
		s += "        calculated value mappings: "
				+ calculateValueMappingsForCase(calculateMappingSituation()) 
				+ NL;
	} catch (UnexpressiveMappingSpecificationException e) {
		s+= "(Unexpressive mapping)" + NL;
	} catch (InsufficientMappingSpecificationException e) {
		s+= "(Insufficiently specified mapping)" + NL;
	}
	
	return s + NL;
}

}