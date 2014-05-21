package org.purl.rvl.tooling.util;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * @author Jan Polowinski
 *
 */
public class D3Utils {
	
	private final static Logger LOGGER = Logger.getLogger(D3Utils.class.getName());
	
	public static int MAX_LABEL_LENGTH = 15;

	public static Object shortenLabel(String label) {
		if(label.length() > MAX_LABEL_LENGTH) {
			return StringUtils.substring(label, 0, MAX_LABEL_LENGTH) + "...";
		}
		else return label;
	}
	
	


}
