package org.purl.rvl.java.rvl.mapping;

import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.VISODATA;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMappingUtils {

	public static String getSomName(int somID) {
		
		switch (somID) {
		
			case ValueMappingX.SOM_NOMINAL:
				return "nominal";
			case ValueMappingX.SOM_ORDINAL:
				return "ordinal";
			case ValueMappingX.SOM_QUANTITATIVE:
				return "quantitative";
			default:
				return "unknown";
		}
	}

	public static String getNameForValueSituation(int situationID) {
	
		switch (situationID) {
		
			case ValueMappingX.UNKNOWN:
				return "unknown";
			case ValueMappingX.CONTINUOUS_RANGE:
				return "cont. range";
			case ValueMappingX.ORDERED_SET:
				return "ordered set or list";
			case ValueMappingX.UNORDERED_SET:
				return "set";
			case ValueMappingX.SINGLE_VALUE:
				return "single value";
			default:
				return "unknown";
		}
	}

	public static String getMappingCaseName(int caseID) {
		
		switch (caseID) {
		
		case ValueMappingX.CC: return "CC";
		case ValueMappingX.CC_D: return "CCd";
		case ValueMappingX.CO: return "CO";
		case ValueMappingX.CU: return "CU";
		case ValueMappingX.OC: return "OC";
		case ValueMappingX.OO: return "OO";
		case ValueMappingX.OU: return "OU";
		case ValueMappingX.UC: return "UC";
		case ValueMappingX.UO: return "UO";
		case ValueMappingX.UU: return "UU";
		case ValueMappingX.SS: return "SS (single values mapped)";
			default:
				return "unknown";
		}
	}

	public static String printAddressedSourceValues(Logger logger, ValueMappingX valueMapping) {
		
		String s = "";
		int valueSituation;
		
		try {
			
			valueSituation = valueMapping.getAddressedSourceValueSituation();
			
			try {
	
				switch (valueSituation) {
				
					case ValueMappingX.SINGLE_VALUE:
						s += "Single source value: " ;
						s += valueMapping.getSourceValuesSingleValue().toString();
						break;
		
					case ValueMappingX.UNORDERED_SET:
						s += "Source value unordered set: ";
						s += valueMapping.getSourceValuesUnorderedSet().toString();
						break;
		
					case ValueMappingX.ORDERED_SET:
						s += "Source value ordered set: ";
						s += valueMapping.getSourceValuesOrderedSet().toString();
						break;
		
					case ValueMappingX.CONTINUOUS_RANGE:
						s += "Continuous range of source values: ";
						s += valueMapping.getSourceValuesContinuousInterval().toString();
						break;
				}
			
			} catch (NullPointerException e) {
				
				String warning = "Could not describe the addressed source values for the " +
						"situation " + getNameForValueSituation(valueSituation);
				
				logger.warning(warning);
				
				s += warning;
			} 
			
		} catch (InsufficientMappingSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return s;
	}

	public static String printAddressedTargetValues(Logger logger, ValueMappingX valueMapping) {
		
		String s = "";
		int valueSituation;
		
		try {
			
			valueSituation = valueMapping.getAddressedTargetValueSituation();
				
			try {
		
				switch (valueSituation) {
				
					case ValueMappingX.SINGLE_VALUE:
						s += "Single target value: " ;
						s += valueMapping.getTargetValuesSingleValue().toString();
						break;
		
					case ValueMappingX.UNORDERED_SET:
						s += "Target value unordered set: ";
						s += valueMapping.getTargetValuesUnorderedSet().toString();
						break;
		
					case ValueMappingX.ORDERED_SET:
						s += "Target value list: ";
						s += valueMapping.getTargetValuesList().toString();
						break;
		
					case ValueMappingX.CONTINUOUS_RANGE:
						s += "Continuous range of target values: ";
						s += valueMapping.getTargetValuesContinuousInterval().toString();
						break;
				}
			
			} catch (NullPointerException e) {
				
				String warning = "Could not describe the addressed target values for the " +
						"situation " + getNameForValueSituation(valueSituation);
				
				logger.warning(warning);
				
				s += warning;
			}
		
		} catch (InsufficientMappingSpecificationException e1) {
			
			logger.warning(e1.getMessage());
			
			s+= "(Insufficiently specified mapping) ";
		}
	
		return s;
	}
	
	
	/**
	 * Get the Scale of Measurement which is (eventually) stated explicitly for the property
	 * 
	 * @param property - the property to examine
	 * @return - an integer representing the scale of measurement
	 */
	public static int getExplicitlyStatedScaleOfMeasurement(Property property) {
		
		ClosableIterator<Property> subPropIt = property.getAllSubPropertyOf();

		while (subPropIt.hasNext()) {
			Property spSubProp = (Property) subPropIt.next();
			if (spSubProp.asURI().equals(VISODATA.HAS_NOMINAL_VALUE))
				return ValueMappingX.SOM_NOMINAL;
			else if (spSubProp.asURI().equals(VISODATA.HAS_ORDINAL_VALUE))
				return ValueMappingX.SOM_ORDINAL;
			else if (spSubProp.asURI().equals(VISODATA.HAS_QUANTITATIVE_VALUE))
				return ValueMappingX.SOM_QUANTITATIVE;
		}
		
		return ValueMappingX.SOM_UNKNOWN;
	}
	
	

}
