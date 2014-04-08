package org.purl.rvl.tooling.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections.ListUtils;
import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.ModelValueFactory;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Class;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.sparql.query.SPARQLQuery;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.SPARQLselector;
import org.purl.rvl.java.rvl.Mapping;
import org.purl.rvl.java.rvl.PropertyMapping;
import org.purl.rvl.java.rvl.PropertyToGO2ORMapping;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.tooling.process.OGVICProcess;

public class RVLUtils {
	
	private final static Logger LOGGER = Logger.getLogger(RVLUtils.class.getName()); 
	
	public static void listAllMappings(Model model) {

		System.out.println("");
		System.out.println("List of all mappings in the model" +
				" (including subclasses when reasoning is on):");
		System.out.println("");

		// get references for all objects of the Mapping class
		ReactorResult<? extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = Mapping
				.getAllInstances_as(model);

		// get and print all mapping instances
		ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings
				.asClosableIterator();
		Mapping mapping;
		while (mappingIterator.hasNext()) {
			mapping = (Mapping) mappingIterator.next().castTo(Mapping.class);
			if(!mapping.isDisabled()) {
				//mappingToStringAsSpecificAsPossible(mapping);
				System.out.println(mappingToStringAsSpecificAsPossible(mapping)); // TODO causes exception
				//System.out.println(mapping.toString());
			}
		}
	}

	  public static void printMappingWithURI(Model model, String uriString){
		  
			System.out.println("");
			System.out.println("Trying to get and print mapping with the URI " + uriString + ":");
			System.out.println("");
			
		  	org.purl.rvl.java.gen.rvl.Mapping mapping = Mapping.getInstance(model, new URIImpl(uriString));
		  	System.out.println(mappingToStringAsSpecificAsPossible((Mapping) mapping.castTo(Mapping.class)));
	  }
	  
	  public static void printMapping(org.purl.rvl.java.gen.rvl.Mapping mapping){  
		  printMapping((Mapping)mapping.castTo(Mapping.class));
	  }
	  
	  
	  public static void printMapping(Mapping mapping){
		  
			System.out.println("");
			System.out.println("Mapping details: ");
			System.out.println("");
			
			System.out.println(mappingToStringAsSpecificAsPossible((Mapping) mapping.castTo(Mapping.class)));
	  }
	  
	  public static String mappingToStringAsSpecificAsPossible(Mapping mapping){
		
		 String s = "";
		  
		// print as P2GAM (value mappings ... )
		if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping p2gam = 
					(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.class);
			s += p2gam.toStringDetailed();
		}
		// print as P2GO2ORM (submappings ... )
		else if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGO2ORMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGO2ORMapping p2go2orm = 
					(org.purl.rvl.java.rvl.PropertyToGO2ORMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGO2ORMapping.class);
			s += p2go2orm.toStringDetailed();
		}
		// print as general mapping
		else {
			s += mapping.toStringDetailed();
		}
		
		return s;
		
	  }
	
	public static Set<Statement> findStatementsPreferingThoseUsingASubProperty(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI
			) {
		
			Set<Statement> stmtSet = new HashSet<Statement>();
			
			String fromGraphString = " ";
			if (null!=fromGraph) {
				fromGraphString = "FROM NAMED " + fromGraph.toSPARQL(); // note: without GRAPH phrase below, only FROM works, not FROM NAMED
			}
		
		try {
	
			String query = "" + 
					" SELECT DISTINCT ?src ?s ?p ?o " + 
					" " + fromGraphString + 
					" WHERE { " +
					" GRAPH ?src { " +
					" ?s ?p ?o . " + 
					" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
					" FILTER NOT EXISTS { " + 
							" ?s ?pp ?o . " + 
					        " ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p " +		 
					" FILTER(?pp != ?p) " +
					" } " +
					" FILTER(?s != ?o) " + // TODO: this stops reflexive arcs completely! make optional
					" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ...
					" FILTER isIRI(?o) " +  // .. or objects! make optional!
					" } " + 
					" } " + 
					" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ";
			LOGGER.fine("Query statements with property (respectively most specific subproperty of) :" + spURI);
			LOGGER.finest("Query :" + query);
			
			/*
			 SELECT DISTINCT ?s ?p ?o WHERE { 
				?s ?p ?o .
				?p rdfs:subPropertyOf* <http://purl.org/rvl/example-data/cites> . 
				FILTER NOT EXISTS {
				    ?s ?pp ?o  .
	   				?pp rdfs:subPropertyOf+ ?p . 
					FILTER (?pp != ?p)
				}
			}
			*/
			
			QueryResultTable explMapResults = modelOrModelSet.sparqlSelect(query);
			
			for (QueryRow row : explMapResults) {
				LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					
					URI context = null; 
					if (null!=fromGraph){
						context = row.getValue("src").asURI();
					}
						
					Statement stmt = new StatementImpl(
							context,
							row.getValue("s").asURI(),
							row.getValue("p").asURI(),
							row.getValue("o")
							);
					
					LOGGER.finest("build Statement: " + stmt.toString());
					
					//if(row.getValue("s").asURI().toString().startsWith(OGVICProcess.getInstance().getUriStart())) {
						stmtSet.add(stmt);
						//LOGGER.finer("added Statement: " + stmt.toString());
					//} else {
						//LOGGER.finer("skipped Statement: " + stmt.toString());
					//}
				} catch (ClassCastException e){
					LOGGER.finer("Skipped statement for linking (blank node casting to URI?): " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get statements for linking (blank node?): " + e.getMessage());
		} 
		
		return stmtSet;
	}
	
	public static Set<Statement> findRelationsOnClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI, 
			org.ontoware.rdfreactor.schema.rdfs.Property inheritedBy
			) {
		return findRelationsOnClassLevel(modelOrModelSet, fromGraph, spURI, inheritedBy, null, null);
	}


	public static Set<Statement> findRelationsOnClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI, 
			org.ontoware.rdfreactor.schema.rdfs.Property inheritedBy,
			org.ontoware.rdf2go.model.node.Resource subject,
			org.ontoware.rdf2go.model.node.Node object
			) {
		
		QueryResultTable results = null;
		Set<Statement> stmtSet = new HashSet<Statement>();
		String subjectString = "?s";
		String objectString = "?o";
		String query;
		
		// temp only support some and all values from ...
		if (!(
				inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
				|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())
				|| inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)	
				
			)) {
			LOGGER.warning("inherited by is set to a value, currently not supported.");
			return stmtSet;
		}
		
		// named graphs
		
		String fromGraphString = " ";
		if (null!=fromGraph) {
			fromGraphString = "FROM NAMED " + fromGraph.toSPARQL(); // note: without GRAPH phrase below, only FROM works, not FROM NAMED
		}

		if (inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)) {
			
			// inheritedBy -> domain-range
			
			//LOGGER.finest("domain range to be implemented");
			//return stmtSet;
			
			// test s,o-constraints
			/*subject = new org.ontoware.rdfreactor.schema.rdfs.Resource(
					OGVICProcess.getInstance().getModelData(),
					RVL.NS + "ValueMapping", false	);*/
			
			query = "" + 
					" SELECT DISTINCT ?src ?s ?p ?o " + 
					" " + fromGraphString + 
					" WHERE { " +
					" GRAPH ?src { " +
					" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " . " +
					" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.domain.toSPARQL() +  " ?s  . " +  
					" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.range.toSPARQL() +  " ?o  " +  
					" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ... ;
					" FILTER isIRI(?o) "; // TODO: this stops blank nodes as objects ... ;
					// constrain object and subject of the "statements" if set
					if (null!=subject) {query += " FILTER (?s = " +  subject.toSPARQL() + ") "; }
					if (null!=object) {query += " FILTER (?o = " + object.toSPARQL() + ") "; }
					query += 
					" } " +
					" } ";

		} else {
			
			// inheritedBy -> other supported values
			
			query = "" + 
				" SELECT DISTINCT ?src ?s ?p ?o " + 
				" " + fromGraphString + 
				" WHERE { " +
				" GRAPH ?src { " +
				" ?s " + Class.SUBCLASSOF.toSPARQL() + " ?restrictionClass . " +
				" ?restrictionClass a " + Restriction.RDFS_CLASS.toSPARQL() + " . " +  
				" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " . " +
				" ?restrictionClass " + Restriction.ONPROPERTY.toSPARQL() + " ?p . " + 
				" ?restrictionClass " + inheritedBy.toSPARQL() +  " ?o  " +  
				" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ... ;
				" FILTER isIRI(?o) " ; // TODO: this stops blank nodes as subjects ... ;
				// constrain object and subject of the "statements" if set
				if (null!=subject) {query += " FILTER (?s = " +  subject.toSPARQL() + ") "; }
				if (null!=object) {query += " FILTER (?o = " + object.toSPARQL() + ") "; }
				query += 
				" } " +
				" } ";
		}


		try {			
						
			LOGGER.finer("Query for getting relations on class level for (subproperties of*) " + spURI);
			LOGGER.finest("Query: " + query);

			results = modelOrModelSet.sparqlSelect(query);
			
			for (QueryRow row : results) {
				//LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					
					URI context = null; 
					if (null!=fromGraph){
						context = row.getValue("src").asURI();
					}
					
					Statement stmt = new StatementImpl(context, row.getValue("s").asURI(), row.getValue("p").asURI(), row.getValue("o"));
					LOGGER.finer("build Statement: " + stmt.toString());
					
						//if(row.getValue("s").asURI().toString().startsWith(OGVICProcess.getInstance().getUriStart())) {
							stmtSet.add(stmt);
							//LOGGER.finer("added Statement: " + stmt.toString());
						//} else {
						//	LOGGER.finer("skipped Statement: " + stmt.toString());
						//}
						
				} catch (Exception e) {
					LOGGER.warning("Problem building Statement for : " + row + "(" + e.getMessage() + ")" );
				}
			}
				
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get relations on class level (blank node?): " + e.getMessage());
		} catch (Exception e){
			LOGGER.warning("Problem with query to get relations on class level: " + e.getMessage());
		}
		
		return stmtSet;
	}
	
	public static Set<Statement> findRelationsOnInstanceOrClassLevel(
			Model model,
			URI fromGraph,
			PropertyMapping pm
			) throws InsufficientMappingSpecificationException {
		
		return findRelationsOnInstanceOrClassLevel(model, fromGraph, pm, false, null, null);
		
	}

	// this was p2gam specific, now replaced by generic method  for pm, delete soon
//	public static Set<Statement> findRelationsOnInstanceOrClassLevel(
//			Model model,
//			PropertyToGraphicAttributeMapping p2gam,
//			org.ontoware.rdf2go.model.node.Resource subject,
//			org.ontoware.rdf2go.model.node.Node object) throws InsufficientMappingSpecificationException {
//		
//			Set<Statement> statementSet = null;
//		
//			URI spURI = p2gam.getSourceProperty().asURI();
//			
//			org.ontoware.rdf2go.model.node.Resource selectorClass = null;
//			
//			if(p2gam.hasSubjectfilter()) {
//				
//				DatatypeLiteral selector = ((PropertyMapping)p2gam.castTo(PropertyMapping.class)).getSubjectFilterSPARQL();
//				String selectorString = selector.getValue();
//				 selectorClass = new URIImpl(selectorString).asResource();
//				
//				LOGGER.info("Applying subject filter. Only resources with the type " + selectorClass + " will be affected by the mapping (and thus shown, which is not the default behavior --> TODO!)");
//				// TODO: at the moment the selector will be interpreted as a constraint on the type of resources (a class name is expected)
//				
//			}
//			
//			if(p2gam.hasInheritedby()) {
//				
//				try{
//					
//					org.ontoware.rdfreactor.schema.rdfs.Property inheritedBy = 
//							(org.ontoware.rdfreactor.schema.rdfs.Property)p2gam.getAllInheritedby_as().firstValue().castTo(org.ontoware.rdfreactor.schema.rdfs.Property.class);
//					
//					// temp only support some and all values from ...
//					if (!(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
//							|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())	)) {
//						LOGGER.warning("inheritedBy is set to a value, currently not supported.");
//					} else {
//						statementSet =  findRelationsOnClassLevel(model, spURI, inheritedBy, subject, object);
//					}
//					
//				}
//				catch (Exception e) {
//					LOGGER.warning("Problem evaluating inheritedBy setting - not a rdf:Property?");
//				}
//			} 
//			
//			else {
//				
//				ClosableIterator<Statement> it = model.findStatements(
//						null==subject ? Variable.ANY : subject
//						,
//						spURI
//						,
//						null==object ? 	Variable.ANY : object
//						);
//				
//				statementSet = new HashSet<Statement>();
//				while (it.hasNext()) {
//					
//					Statement statement = it.next();
//					
//					// check starts with constraint (workaround) and subjectFilter
//					if (
//						statement.getSubject().toString().startsWith(OGVICProcess.getInstance().getUriStart())
//						&& (null==selectorClass || RVLUtils.hasType(model, statement.getSubject(), selectorClass ))
//						) {
//						statementSet.add(statement);
//						LOGGER.finest("added Statement (matching subfilter and starturi): " + statement.toString());
//					} else {
//						LOGGER.finest("skipped Statement (not matching subfilter or starturi): " + statement.toString());
//					}
//					
//					
//				}
//
//			}
//			
//			return statementSet;
//	}
	
	
	
	public static Set<Statement> findRelationsOnInstanceOrClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			PropertyMapping pm,
			boolean onlyMostSpecific, 
			org.ontoware.rdf2go.model.node.Resource subject,
			org.ontoware.rdf2go.model.node.Node object) throws InsufficientMappingSpecificationException {
		
			Set<Statement> statementSet = new HashSet<Statement>();
		
			URI spURI = pm.getSourceProperty().asURI();
			
			org.ontoware.rdf2go.model.node.Resource selectorClass = null;
			
			if(pm.hasSubjectfilter()) {
				
				DatatypeLiteral selector = pm.getSubjectFilterSPARQL();
				String selectorString = selector.getValue();
				 selectorClass = new URIImpl(selectorString).asResource();
				
				LOGGER.info("Applying subject filter. Only resources with the type " + selectorClass + " will be affected by the mapping (and thus shown, which is not the default behavior --> TODO!)");
				// TODO: at the moment the selector will be interpreted as a constraint on the type of resources (a class name is expected)
				
			}
			
			if (onlyMostSpecific) {
				 // get only the most specific statements and exclude those using a super-property instead
				statementSet.addAll(RVLUtils.findStatementsPreferingThoseUsingASubProperty(modelOrModelSet, fromGraph, spURI)); 
			}
			
			else {
				
				Model dataModel;
				try{
				ModelSet modelSet = (ModelSet)modelOrModelSet;
				dataModel = modelSet.getModel(OGVICProcess.GRAPH_DATA);
				} catch (Exception e) {
					LOGGER.severe("Could not get data model from modelOrModelSet, will use modelOrModelSet as if is was the data graph (model)");
					dataModel = (Model)modelOrModelSet;
				}
				
				
				ClosableIterator<Statement> it = dataModel.findStatements(
						null==subject ? Variable.ANY : subject
						,
						spURI
						,
						null==object ? 	Variable.ANY : object
						);
				
				while (it.hasNext()) {
					
					Statement statement = it.next();
					
					// check starts with constraint (workaround) and subjectFilter
					if (
						//statement.getSubject().toString().startsWith(OGVICProcess.getInstance().getUriStart())
						//&& 
						(null==selectorClass || RVLUtils.hasType(dataModel, statement.getSubject(), selectorClass ))
						) {
						statementSet.add(statement);
						LOGGER.finest("added Statement (matching subfilter): " + statement.toString());
					} else {
						LOGGER.finest("skipped Statement (not matching subfilter): " + statement.toString());
					}
					
					
				}

			}
			
			// consider inherited relations, including those between classes (someValueFrom ...)
			if(pm.hasInheritedby()) {
				
				try{
					Property inheritedBy = pm.getInheritedBy();
					
					// temp only support some and all values from ... // TODO these checks are also done in findRelationsOnClassLevel
					if (!(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
							|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())	
							|| inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)	
							)) {
						LOGGER.warning("inheritedBy is set to a value, currently not supported.");
					} else {
						statementSet.addAll(findRelationsOnClassLevel(
								modelOrModelSet,
								fromGraph,
								spURI,
								inheritedBy,
								subject,
								object)
								);
					}
					
				}
				catch (Exception e) {
					LOGGER.warning("Problem evaluating inheritedBy setting or getting relations on class level");
				}
			} 
			
			return statementSet;
	}

	


	public static boolean hasType(
			Model model, org.ontoware.rdf2go.model.node.Resource resource,
			org.ontoware.rdf2go.model.node.Resource type) {

			return model.contains(resource, new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), type);
		
	}

	public static List<Node> rdfs2JavaList(org.ontoware.rdfreactor.schema.rdfs.List rdfsList) {
		
		List<Node> javaList = rdfs2InvertedJavaList(rdfsList);
		Collections.reverse(javaList);
		
		return javaList ;
		
	}
	
	private static List<Node> rdfs2InvertedJavaList(org.ontoware.rdfreactor.schema.rdfs.List rdfsList) {
		
		List<Node> javaList = null;
		
		Node headRdfsList = rdfsList.getAllFirst_as().firstValue();
		org.ontoware.rdfreactor.schema.rdfs.List restRdfsList = rdfsList.getAllRest_as().firstValue();
		
		if (headRdfsList == null) {
			javaList = new LinkedList<Node>();
		}
		else {
			javaList = rdfs2InvertedJavaList(restRdfsList);
			javaList.add(headRdfsList);
		}
		
//		LOGGER.finest("Java list of nodes: " + javaList);
		return javaList;
		
	}

	
	public static List<Statement> getRolesAndGOsFor(
			Model modelAVM, Node rel, URI roleURI) {
		
			List<Statement> stmtList = new ArrayList<Statement>();
		
		try {
	
			String query = "" + 
					" SELECT DISTINCT ?s ?p ?o " + 
					" WHERE { " +
					" ?s ?p ?o . " + 
					" " + rel.toSPARQL() + " " + roleURI.toSPARQL() + " ?o " +
					" } ";
			

			QueryResultTable explMapResults = modelAVM.sparqlSelect(query);
			
			for (QueryRow row : explMapResults) {
				//LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					Statement stmt = new StatementImpl(null, row.getValue("s").asURI(), row.getValue("p").asURI(), row.getValue("o"));
					LOGGER.finest("build Statement: " + stmt.toString());
					stmtList.add(stmt);
				} catch (ClassCastException e){
					LOGGER.finer("Skipped statement (blank node casting to URI?): " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			System.out.println("test");
			LOGGER.warning("Problem with query to get statements: " + e.getMessage());
		} 
		
		return stmtList;
	}
	
	public static GraphicObject getGOForRole(
			Model modelAVM, Node rel, URI roleURI) {
		
			List<Statement> stmtList = getRolesAndGOsFor(modelAVM,rel,roleURI);
			
			Node goNode = stmtList.get(0).getObject();
			
			org.purl.rvl.java.gen.viso.graphic.GraphicObject goGen = GraphicObject.getInstance(modelAVM, goNode.asResource());
			
			GraphicObject go = (GraphicObject) goGen.castTo(GraphicObject.class);
			
			return go;

	}

}
