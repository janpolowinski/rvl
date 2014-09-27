/**
 * 
 */
package org.purl.rvl.java.rvl;

import org.purl.rvl.java.gen.rvl.Identitymapping;


/**
 * @author Jan Polowinski
 *
 */
public class IdentityMappingX extends PropertyMappingX {
	
	//protected Identitymapping delegatee;

	public String toStringSummary() {
		return "Identity Mapping " + this.toString();
	}
	
	public IdentityMappingX(Identitymapping delegatee) {
		super(delegatee);
		//this.delegatee = delegatee;
	}
	
	protected Identitymapping getDelegatee() {
		return (Identitymapping) delegatee.castTo(Identitymapping.class);
	}
}
