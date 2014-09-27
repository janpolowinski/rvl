package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 *
 */
public class MappingX {
	
	private Mapping delegatee;

	static final String NL =  System.getProperty("line.separator");

	/**
	 * 
	 */
	private static final long serialVersionUID = -1457148355972827796L;

	public String toStringDetailed() {
		String s = "################################################" + NL;
		String label = delegatee.getAllLabel_as().firstValue();
		Boolean includeInLegend = delegatee.getAllIncludeinlegend_as().firstValue();
		
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
		if (delegatee.hasDisabled()) {
			return delegatee.getAllDisabled_as().firstValue();
		} else return false;
	}

	public String toStringAsSpecificAsPossible(){
		
		 String s = "";
		  
		// print as P2GAM (value mappings ... )
		if(delegatee.isInstanceof(Property_to_Graphic_AttributeMapping.RDFS_CLASS)) {

			PropertyToGraphicAttributeMappingX p2gam = 
					(PropertyToGraphicAttributeMappingX) delegatee.castTo(PropertyToGraphicAttributeMappingX.class);
			
			// caching
			// TODO reenable caching? 
			//p2gam = RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2gam, PropertyToGraphicAttributeMappingX.class);
			
			s += p2gam.toStringDetailed();
		}
		
		// print as P2GO2ORM (submappings ... )
		else if(delegatee.isInstanceof(Property_to_Graphic_Object_to_Object_RelationMapping.RDFS_CLASS)) {
			
			PropertyToGO2ORMappingX p2go2orm = 
					(PropertyToGO2ORMappingX) delegatee.castTo(PropertyToGO2ORMappingX.class);
			
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

	public URI asURI() {
		return delegatee.asURI();
	}

}
