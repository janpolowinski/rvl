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

	public String toStringSummary() {
		return "Identity Mapping " + this.toString();
	}
	
	public IdentityMappingX(Identitymapping delegatee) {
		super(delegatee);
	}
	
	protected Identitymapping getDelegatee() {
		return (Identitymapping) super.getDelegatee().castTo(Identitymapping.class);
	}
}
