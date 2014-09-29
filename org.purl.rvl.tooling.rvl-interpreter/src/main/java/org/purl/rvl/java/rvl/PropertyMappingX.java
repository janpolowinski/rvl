package org.purl.rvl.java.rvl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.UnsupportedSelectorTypeException;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.PropertyMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.java.rvl.filter.SubjectFilter;
import org.purl.rvl.tooling.util.AVMUtils;

/**
 * @author Jan Polowinski
 *
 */
public class PropertyMappingX extends MappingX {

	private final static Logger LOGGER = Logger.getLogger(PropertyMappingX.class .getName()); 
	
	static final String NL =  System.getProperty("line.separator");
	
	private Set<SubMappingRelationX> subMappings; 

	public PropertyMappingX(PropertyMapping delegatee) {
		super((Mapping) delegatee.castTo(Mapping.class)); // TODO this cast is only necessary because PropertyMapping does not extend Mapping but A1. This is a bug in the RVL schema or the generator.
	}

	public String toStringDetailed() {
		
		String s ="";
		
		s += super.toStringDetailed();

		/*// letting the mapping print the affected resources is bad, since this depends on the used data model now, which may be unavailable in test scenarios
		// affected resources:
		Set<Resource> subjectSet;
		try {
			subjectSet = getAffectedResources();
			for (Iterator<Resource> iterator = subjectSet.iterator(); iterator.hasNext();) {
				Resource resource = (Resource) iterator.next();
				s += "     affects: " + resource +  NL;
			}
		} catch (InsufficientMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
			//e.printStackTrace();
		} */
		
		try {
			Property sp = getSourceProperty();
			s += "     source property: " + sp.asURI() + NL;
		} catch (InsufficientMappingSpecificationException e) {
			s += "     source property missing!" + NL;
		}
		//Property tgr = this.getAllTargetgraphicrelation_abstract__as().firstValue();
		
		//s += "     target graphic relation: " + this.getAllTargetgraphicrelation_abstract__as().firstValue() + NL ;
		
		return s;
	}
	
	private boolean hasSourceProperty() {
		return getDelegatee().hasSourceproperty();
	}

	/**
	 * TODO: not in use at the moment. DELETE? Merge?
	 * Get the resources affected by this property mapping, i.e.,
	 * all resources used as a subject in statements with the source property 
	 * defined in this mapping.
	 * @return
	 */
	/*public Set<Resource> getAffectedResources() throws InsufficientMappingSpecificationException {
		
		Set<Statement> statementSet = new HashSet<Statement>();
		Set<Resource> subjectSet = new HashSet<Resource>();
		Property sp = null;
		
		try {
			sp = getAllSourceproperty_as().firstValue();
		}
		finally {
			if(sp==null) {
				LOGGER.warning("Mapping has missing sourceproperty");
				throw new InsufficientMappingSpecificationException();
			}
		}				
		
		org.ontoware.rdf2go.model.node.Resource selectorClass = null;
		
		if(this.hasSubjectfilter()) {
			
			DatatypeLiteral selector = getSubjectFilterSPARQL();
			String selectorString = selector.getValue();
			 selectorClass = new URIImpl(selectorString).asResource();
			
			LOGGER.info("Applying subject filter. Only resources with the type " + selectorClass + " will be affected by the mapping (and thus shown, which is not the default behavior --> TODO!)");
			// TODO: at the moment the selector will be interpreted as a constraint on the type of resources (a class name is expected)
			
		}

		
		// build a statement set by find methode or SPARQL

		// consider inherited relations, including those between classes (someValueFrom ...)
		if(this.hasInheritedby()) {
			
			try{
				
				Property inheritedBy = (Property)this.getAllInheritedby_as().firstValue().castTo(Property.class);
				
				// temp only support some and all values from ...
				if (!(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
						|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())	)) {
					LOGGER.warning("inheritedBy is set to a value, currently not supported.");
					return subjectSet;
				}
				
				statementSet = RVLUtils.findRelationsOnClassLevel(OGVICProcess.getInstance().getModelSet(), OGVICProcess.GRAPH_DATA ,sp.asURI(), inheritedBy); // TODO may also throw exceptions which are wrongly catch below!

			}
			catch (Exception e) {
				LOGGER.warning("Problem evaluating inheritedBy setting - not a rdf:Property?");
			}
		}
		else {
			ClosableIterator<Statement> it = OGVICProcess.getInstance().getModelData().findStatements(Variable.ANY, sp.asURI(), Variable.ANY);
			while (it.hasNext()) {
				statementSet.add(it.next());
			}
		}
		
		
		// iterate the statement set
		
		for (Iterator<Statement> stmtSetIt = statementSet.iterator(); stmtSetIt.hasNext();) {
			
			Statement statement = (Statement) stmtSetIt.next();
			
			Resource subject = statement.getSubject();
			
			// TODO hack: ignore statements with subjects other than those starting with the data graph URI
			//String uriStartString = OGVICProcess.getInstance().getUriStart();
			
			int ignoredResources = 0;
			
			try{

				// check starts with constraint (workaround) and subjectFilter
				if (
					//subject.toString().startsWith(uriStartString)
					//&& 
					(null==selectorClass || RVLUtils.hasType(model, subject, selectorClass ))
					) {
					subjectSet.add(subject);
					LOGGER.finest("found affected resource (matching subfilter): " + subject.toString());
				} else {
					LOGGER.finest("ignored un-affected resource (not matching subfilter): " + subject.toString());
				}
				
				
			}
			catch (ClassCastException e) {
				ignoredResources++;
				LOGGER.finest("ignoring resource (may be a blank node): " + subject);
			}	
			if (ignoredResources>0)
				LOGGER.finer(ignoredResources + " resources have been ignored for subject " + subject +  " when calculating the affected mappings (may be it was a blank node?).");
		}
	
		return subjectSet;
	} */


	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		if (hasSourceProperty())
			return getDelegatee().getAllSourceproperty_as().firstValue();
		else 
			throw new InsufficientMappingSpecificationException("No source property could be found.");
	}

	public Property getTargetGraphicRelation() throws InsufficientMappingSpecificationException {
		if (getDelegatee().hasTargetgraphicrelation_abstract_())
			return (Property)getDelegatee().getAllTargetgraphicrelation_abstract__as().firstValue().castTo(Property.class);
		else 
			throw new InsufficientMappingSpecificationException(this, "Missing target graphic relation.");
	}
	
	
	public Literal getSubjectFilter(){
		if (getDelegatee().hasSubjectfilter()) {
			return getDelegatee().getAllSubjectfilter_asNode_().firstValue().asLiteral();
		} else return null;
	}

	// TODO multiple inheritedBy values should be allowed
	public Property getInheritedBy() {
		if (getDelegatee().hasInheritedby()) {
			return (Property)getDelegatee().getAllInheritedby_as().firstValue().castTo(Property.class);
		} else return null;
	}

	public String toStringSummary() {
		return AVMUtils.getGoodNodeLabel(getDelegatee(), getDelegatee().getModel());
	}

	/**
	 * @return the filter string for the subject in SPARQL
	 */
	public String getSubjectFilterString() {
		
		try {
			
			return new SubjectFilter(this).getFilterString();
			
		} catch (UnsupportedSelectorTypeException e) {
			
			LOGGER.severe("Will ignore filter. Reason: " + e.getMessage());
			return "";
		}
	}
	
	public Set<SubMappingRelationX> getSubMappings() {
		
		if (null != subMappings) {
			return subMappings;
		}
		
		Set<SubMappingRelationX> subMappingRelationsX = new HashSet<SubMappingRelationX>();
		if (getDelegatee().hasSub_mapping()) {
			ClosableIterator<Sub_mappingrelation> subMappingRelations =  getDelegatee().getAllSub_mapping_as().asClosableIterator();
			while (subMappingRelations.hasNext()) {
				
				Sub_mappingrelation rel = (Sub_mappingrelation) subMappingRelations
						.next();
				
				SubMappingRelationX relX = new SubMappingRelationX(rel);
				
				if (!relX.hasSubMapping() || !relX.hasOnRole()) {
					LOGGER.warning("Ignored incomplete submapping " + relX.toString() + ", since no submapping was found or onRole is not specified.");
					continue;
				}
				
				subMappingRelationsX.add(relX);
			}
			
			subMappings = subMappingRelationsX;
			
			return subMappings;
			
		} else  {
			return null;
		}	
	}
	
	public boolean hasSubMapping(){
		return getDelegatee().hasSub_mapping();
	}

	public boolean hasSubjectFilter() {
		return getDelegatee().hasSubjectfilter();
	}

	public boolean hasInheritedby() {
		return getDelegatee().hasInheritedby();
	}
	
	protected PropertyMapping getDelegatee() {
		return (PropertyMapping) delegatee.castTo(PropertyMapping.class);
	}

}
