package org.purl.rvl.interpreter.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.impl.jena.ModelFactoryImpl;
import org.ontoware.rdf2go.impl.jena.ModelSetImplJena;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.CustomRecordFormatter;

public class ModelRepoTest {
	
	private static final URIImpl GRAPH_MAPPING = new URIImpl("http://purl.org/rvl/example/mapping/");
	private static final URIImpl GRAPH_DATA = new URIImpl("http://purl.org/rvl/example/data/");

	private final static Logger LOGGER = Logger.getLogger(ModelRepoTest.class.getName()); 
	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static final String NL =  System.getProperty("line.separator");
	
	
    static {
    	  	
		LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINEST);

		
		// In order to show log entrys of the fine level, we need to create a new handler as well
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINEST);
        
        CustomRecordFormatter formatter = new CustomRecordFormatter();
        handler.setFormatter(formatter); // out-comment this line to use the normal formatting with method and date
        
        LOGGER_RVL_PACKAGE.setUseParentHandlers(false); // otherwise double output of log entries
        LOGGER_RVL_PACKAGE.addHandler(handler);
		
        }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// we use sesame here, since jena seems not to support SPARQL queries on model sets
		//RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl() );
		RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory());
		
		
		/*Properties p = new Properties(); 
		p.setProperty("back-end", "memory");
		ModelSet modelSet = RDF2Go.getModelFactory().createModelSet(p);*/
		
		ModelSet modelSet = RDF2Go.getModelFactory().createModelSet();
		modelSet.open();
		
		// data
		Model dataModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		dataModel.open();
		readFromAnySyntax(dataModel, ExampleData.SLUB_TEST);
		
		// mapping
		Model mappingModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		mappingModel.open();
		readFromAnySyntax(mappingModel, ExampleMapping.SLUB );
		
		// rvl
		Model rvlModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		rvlModel.open();
		readFromAnySyntax(rvlModel, OGVICProcess.RVL_LOCAL_REL );
		
		modelSet.addModel(dataModel, GRAPH_DATA);
		modelSet.addModel(mappingModel, GRAPH_MAPPING);
		//modelSet.addModel(rvlModel, new URIImpl("http://purl.org/rvl/"));
		
		
		
		//listModelStatements("data model",dataModel);
		//printModelSet(modelSet);
		
		/*
		// find stmts filtered
		ClosableIterator<Statement> iterator = modelSet.findStatements(
				new URIImpl("http://purl.org/rvl/example/data/"),
				Variable.ANY,
				Variable.ANY,
				Variable.ANY
				);

		while (iterator.hasNext()) {
			
			Statement dataStatement = (Statement) iterator.next();
			
			System.out.println(dataStatement);
			
		}
		
		*/
		
		// sparql-query filtered
		
		LOGGER.finest("Size of model set: " + modelSet.size());
		
		Set<Statement> stmtSetFromSPARQL = 
				findStatementsPreferingThoseUsingASubProperty(
						modelSet,
						RDF.type,
						GRAPH_MAPPING,
						//new URIImpl("http://www.openarchives.org/OAI/2.0/header"),
						null);
		
		for (Iterator<Statement> iterator2 = stmtSetFromSPARQL.iterator(); iterator2
				.hasNext();) {
			Statement statement = (Statement) iterator2.next();
			
			System.out.println(statement.getContext() + ": " + statement);
			
		}
		
	}
	
	private static void listModelStatements(String context, Model model){
		
		System.out.println("Listing statements in model with context " + context);
		
		ClosableIterator<Statement> iterator = model.iterator();
		
		while (iterator.hasNext()) {
			Statement statement = (Statement) iterator.next();
			
			System.out.println(statement);
			
		}
		
	}
	
	private static void printModelSet(ModelSet modelSet){
		
		System.out.println("Listing statements in ModelSet");
		
		ClosableIterator<Statement> iterator = modelSet.iterator();
		
		while (iterator.hasNext()) {
			Statement statement = (Statement) iterator.next();
			
			
			System.out.println(statement.getContext() + ": " + statement);
			
		}
		
	}
	
	
	private static void readFromAnySyntax(Model model, String fileName) {
		
		File file = new File(fileName);
		readFromAnySyntax(model, file);

	}
	
	
	
	private static void readFromAnySyntax(Model model, File file) {

		try {
			
			String extension = FilenameUtils.getExtension(file.getName());
			
			if (extension.equals("ttl") || extension.equals("n3")) {
				model.readFrom(new FileReader(file),
						Syntax.Turtle);
			} else {
				model.readFrom(new FileReader(file),
						Syntax.RdfXml);
			}
		
			LOGGER.info("Reading file into (some) model: " + file.getPath());
			
		} catch (FileNotFoundException e) {
			LOGGER.info("File could not be read into the model, since it wasn't found: " +  file.getPath());
		} catch (IOException e) {
			LOGGER.info("File could not be read into the model: " +  file.getPath());
			e.printStackTrace();
		}

	}

	private static Set<Statement> findStatementsPreferingThoseUsingASubProperty(
			ModelSet modelSet,
			URI spURI,
			URI fromGraph, 
			org.ontoware.rdf2go.model.node.Resource selectorClass
			) {
		
		Set<Statement> stmtSet = new HashSet<Statement>();
		
		LOGGER.finest("Size of model set: " + modelSet.size());
		
		try {
	
			
			String query = "" + 
					" SELECT " +
					" DISTINCT ?src ?s ?p ?o " + 
					" FROM NAMED " + fromGraph.toSPARQL() + // note: without GRAPH phrase below, only FROM works, not FROM NAMED
					" WHERE { " + 
					" GRAPH ?src { " +
					"";
			if (selectorClass != null) {
				query += 
					" ?s " + RDF.type.toSPARQL()  + " " + selectorClass.toSPARQL() + " . ";
			}
			query += 
					" ?s ?p ?o . " + 
					" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
					" FILTER NOT EXISTS { " + 
							" ?s ?pp ?o . " + 
					        " ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p " +		 
					" FILTER(?pp != ?p) " +
					" } " +
					" FILTER(?s != ?o) " + // TODO: this stops reflexive arcs completely! make optional
					" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ...
					" FILTER isIRI(?o) " + // .. or objects! make optional!
					" } " + 
					" } " + 
					" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ";
			
			
			/*
			String query = "" + 
					" SELECT DISTINCT ?src ?s ?p ?o " + 
					" FROM NAMED " + fromGraph.toSPARQL() + // note: without GRAPH phrase below, only FROM works, not FROM NAMED
					" WHERE { " + 
					" GRAPH ?src { ";
			query += 
					" ?s ?p ?o . " +
					" } "  + 
					" } " ;
					*/
					
								
			
			LOGGER.fine("Query statements in graph " + fromGraph + " with property (respectively most specific subproperty of) :" + spURI);
			LOGGER.finest("Query: " + query);
			
			
			QueryResultTable explMapResults = modelSet.sparqlSelect(query);
			
			for (QueryRow row : explMapResults) {
				LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					//System.out.println(row.getValue("src").asURI());
					Statement stmt = new StatementImpl(row.getValue("src").asURI(), row.getValue("s").asURI(), row.getValue("p").asURI(), row.getValue("o"));
					LOGGER.finer("added Statement: " + stmt.toString());
					stmtSet.add(stmt);
				} catch (ClassCastException e){
					LOGGER.finer("Skipped statement for linking (blank node casting to URI?): " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get statements for linking (blank node?): " + e.getMessage());
		} 
		
		return stmtSet;
	}

}
