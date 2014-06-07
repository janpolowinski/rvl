package org.purl.rvl.java.rvl;

import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.UnsupportedSelectorTypeException;
import org.purl.rvl.java.rvl.filter.SubjectFilter;
import org.purl.rvl.tooling.util.AVMUtils;

/**
 * @author Jan Polowinski
 *
 */
public class PropertyMappingX extends
		org.purl.rvl.java.gen.rvl.PropertyMapping  implements MappingIF {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 4491101288581389956L;

private final static Logger LOGGER = Logger.getLogger(PropertyMappingX.class .getName()); 

static final String NL =  System.getProperty("line.separator");
	
	public PropertyMappingX(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMappingX(Model model, Resource instanceIdentifier,
			boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMappingX(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMappingX(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMappingX(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toStringDetailed() {
		String s ="";
		
		// try to get the string description from the (manual) MappingX class, which is not in the super-class hierarchy
		MappingX m = (MappingX) this.castTo(MappingX.class);
		s += m.toStringDetailed();

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
		
		Property sp = this.getAllSourceproperty_as().firstValue();
		//Property tgr = this.getAllTargetgraphicrelation_abstract__as().firstValue();
		s += "     source property: " + sp.asURI() + NL;
		//s += "     target graphic relation: " + this.getAllTargetgraphicrelation_abstract__as().firstValue() + NL ;

				
		return s;
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

	public boolean isDisabled() {
		if (this.hasDisabled()) {
			return this.getAllDisabled_as().firstValue();
		} else return false;
	}

	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		if (hasSourceproperty())
			return this.getAllSourceproperty_as().firstValue();
		else 
			throw new InsufficientMappingSpecificationException();
	}

	public Property getTargetGraphicRelation() throws InsufficientMappingSpecificationException {
		if (hasTargetgraphicrelation_abstract_())
			return (Property)this.getAllTargetgraphicrelation_abstract__as().firstValue().castTo(Property.class);
		else 
			throw new InsufficientMappingSpecificationException();
	}
	
	
	public Literal getSubjectFilter(){
		if (hasSubjectfilter()) {
			return getAllSubjectfilter_asNode_().firstValue().asLiteral();
		} else return null;
	}

	// TODO multiple inheritedBy values should be allowed
	public Property getInheritedBy() {
		if (this.hasInheritedby()) {
			return (Property)getAllInheritedby_as().firstValue().castTo(Property.class);
		} else return null;
	}

	public String toStringSummary() {
		return AVMUtils.getGoodLabel(this, model);
	}

//	public PropertyMappingX tryReplaceWithCashedInstanceForSameURI(MappingIF mapping) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
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

}
