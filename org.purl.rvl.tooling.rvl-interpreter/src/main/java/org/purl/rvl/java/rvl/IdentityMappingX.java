/**
 * 
 */
package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Identitymapping;
import org.purl.rvl.tooling.process.ResourcesCache;

/**
 * @author Jan Polowinski
 *
 */
public class IdentityMappingX extends Identitymapping {

	public IdentityMappingX(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
	}

	public IdentityMappingX(Model model, boolean write) {
		super(model, write);
	}

	public IdentityMappingX(Model model, Resource instanceIdentifier,
			boolean write) {
		super(model, instanceIdentifier, write);
	}

	public IdentityMappingX(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
	}

	public IdentityMappingX(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2172460196080099925L;

	public boolean isDisabled() {
		if (this.hasDisabled()) {
			return this.getAllDisabled_as().firstValue();
		} else return false;
	}

	public String toStringSummary() {
		return "Identity Mapping " + this.toString();
	}

	public IdentityMappingX tryReplaceWithCashedInstanceForSameURI(
			IdentityMappingX mapping) {
		return (IdentityMappingX) ResourcesCache.getInstance().tryReplaceOrCache(mapping);
	}
	
	

}
