package org.purl.rvl.java.rvl.mapping;

import java.util.logging.Logger;

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
		
		try {
	
			switch (valueMapping.getAddressedSourceValueSituation()) {
			
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
					"situation " + getNameForValueSituation(valueMapping.getAddressedSourceValueSituation());
			logger.warning(warning);
			s += warning;
		}
	
		return s;
	}

	public static String printAddressedTargetValues(Logger logger, ValueMappingX valueMapping) {
	String s = "";
		
		try {
	
			switch (valueMapping.getAddressedTargetValueSituation()) {
			
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
					"situation " + getNameForValueSituation(valueMapping.getAddressedTargetValueSituation());
			logger.warning(warning);
			s += warning;
		}
	
		return s;
	}
	
	

}
