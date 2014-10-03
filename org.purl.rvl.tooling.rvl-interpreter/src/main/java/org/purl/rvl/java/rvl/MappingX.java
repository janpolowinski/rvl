package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Mapping;

/**
 * @author Jan Polowinski
 *
 */
public class MappingX {
	
	private Mapping delegatee;

	static final String NL =  System.getProperty("line.separator");

	public MappingX(Mapping delegatee) {
		super();
		this.delegatee = delegatee;
	}

	public String toStringDetailed() {
		String s = "################################################" + NL;
		String label = getDelegatee().getAllLabel_as().firstValue();
		Boolean includeInLegend = getDelegatee().getAllIncludeinlegend_as().firstValue();
		
			s += "     ID: " + this.toString() + NL;
		
		// label
		if (null != label) {
			s += "     Label: " + label + NL;
		} else {
			s += "     Mapping without label (" + super.toString() + ")" + NL;
		}
		// legends
		if (null!=includeInLegend && includeInLegend) {
			s += "     Mapping will be included in legends." + NL;
		}
		
		return s;
	}

	public boolean isDisabled() {
		if (getDelegatee().hasDisabled()) {
			return getDelegatee().getAllDisabled_as().firstValue();
		} else return false;
	}

	public String toStringAsSpecificAsPossible(){
	
	 String s = "";
		  
		// print as P2GAM (value mappings ... )
		if (this instanceof PropertyToGraphicAttributeMappingX) {

			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) this;
			
			// caching
			// TODO reenable caching? 
			//p2gam = RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2gam, PropertyToGraphicAttributeMappingX.class);
			
			s += p2gam.toStringDetailed();
		}
		
		// print as P2GO2ORM (submappings ... )
		else if (this instanceof PropertyToGO2ORMappingX) {
			
			PropertyToGO2ORMappingX p2go2orm =  (PropertyToGO2ORMappingX) this;
			
			// caching
			// TODO reenable caching? 
			//p2go2orm = RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2go2orm, PropertyToGO2ORMappingX.class);
						
			s += p2go2orm.toStringDetailed();
		}
		
		// print as general mapping
		else {
			s += this.toStringDetailed();
		}
		
		return s;
	  }

	public URI asURI() throws ClassCastException {
		return getDelegatee().asURI();
	}

	@Override
	public String toString() {
		return "wrapped " + delegatee.toString();
	}

	/** Access the wrapped/adapted generated mapping 
	 * @return the delegatee
	 */
	protected Mapping getDelegatee() {
		return delegatee;
	}

}
