package org.purl.rvl.java.rvl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.InsufficientResourcesException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.tooling.OGVICProcess;

public class PropertyMapping extends
		org.purl.rvl.java.gen.rvl.PropertyMapping {
	
private final static Logger LOGGER = Logger.getLogger(PropertyMapping.class .getName()); 

static final String NL =  System.getProperty("line.separator");
	
	public PropertyMapping(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMapping(Model model, Resource instanceIdentifier,
			boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMapping(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMapping(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyMapping(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		String s ="";
		
		// try to get the string description from the (manual) Mapping class, which is not in the super-class hierarchy
		Mapping m = (Mapping) this.castTo(Mapping.class);
		s += m.toString();

		// affected resources:
		Set<Resource> subjectSet;
		try {
			subjectSet = getAffectedResources();
			for (Iterator<Resource> iterator = subjectSet.iterator(); iterator.hasNext();) {
				Resource resource = (Resource) iterator.next();
				s += "     affects: " + resource +  NL;
			}
		} catch (InsufficientMappingSpecificationExecption e) {
			LOGGER.warning(e.getMessage());
			//e.printStackTrace();
		}
		
		Property sp = this.getAllSourceproperty_as().firstValue();
		//Property tgr = this.getAllTargetgraphicrelation_abstract__as().firstValue();
		s += "     source property: " + sp.getAllLabel_as().firstValue() + NL;
		//s += "     target graphic relation: " + this.getAllTargetgraphicrelation_abstract__as().firstValue() + NL ;

				
		return s;
	}
	
	/**
	 * Get the resources affected by this property mapping, i.e.,
	 * all resources used as a subject in statements with the source property 
	 * defined in this mapping.
	 * @return
	 */
	public Set<Resource> getAffectedResources() throws InsufficientMappingSpecificationExecption {
		
		Set<Resource> subjectSet = new HashSet<Resource>();
		Property sp = null;
		
		try {
			sp = getAllSourceproperty_as().firstValue();
		}
		finally {
			if(sp==null) {
				LOGGER.warning("Mapping has missing sourceproperty");
				throw new InsufficientMappingSpecificationExecption();
			}
		}					

		ClosableIterator<Statement> spStIt = model.findStatements(Variable.ANY, sp.asURI(), Variable.ANY);
		while (spStIt.hasNext()) {
			Statement statement = (Statement) spStIt.next();
			Resource subject = statement.getSubject();
			//System.out.println(statement.getSubject());
			
			try{
				// TODO hack: ignore statements with subjects other than those starting with the data graph URI
				if (subject.asURI().toString().startsWith(OGVICProcess.REXD_URI)) {
					subjectSet.add(subject);
				}
				else
					LOGGER.finest("ignored affected resource " + subject + " not starting with " + OGVICProcess.REXD_URI);
			}
			catch (ClassCastException e) {
				LOGGER.info("ignoring resource (may be a blank node): " + subject);
			}	
		}
		return subjectSet;
	}
	

}
