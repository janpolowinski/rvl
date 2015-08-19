package org.purl.rvl.interpreter.test;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.commons.utils.CustomRecordFormatter;
import org.purl.rvl.tooling.commons.utils.ModelUtils;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

/**
 * @author Jan Polowinski
 *
 */
public class ModelRepoTest {
	
	private static final URIImpl GRAPH_MAPPING_ENRICHED_WITH_RVL = new URIImpl("http://purl.org/rvl/example/mapping/enriched/");
	

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
	 * @throws IOException 
	 * @throws ModelRuntimeException 
	 */
	public static void main(String[] args) throws ModelRuntimeException, IOException {
		
		// we use sesame here, since jena seems not to properly support SPARQL queries on model sets (e.g. named graph queries caused problems)
		//RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl() );
		RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory());
		
		
		/*Properties p = new Properties(); 
		p.setProperty("back-end", "memory");
		ModelSet modelSet = RDF2Go.getModelFactory().createModelSet(p);*/
		
		//newTest();
		//lldTest();
		owlReasoningTest();
		
	}
	
	public static void owlReasoningTest() throws ModelRuntimeException, IOException {

		// extendee
		Model extendeeModel = RDF2Go.getModelFactory().createModel(
				Reasoning.owl);
		extendeeModel.open();
		ModelUtils.readFromAnySyntax(extendeeModel,
				"../org.purl.rvl.vocabulary/experiments/example-data/model_repo_test_extendee.ttl");

		ModelUtils.printModelInfo("extendee model", extendeeModel, true);

		
	}
	
	public static void newTest() throws ModelRuntimeException, IOException {

		// extendee
		Model extendeeModel = RDF2Go.getModelFactory().createModel(
				Reasoning.none);
		extendeeModel.open();
		ModelUtils.readFromAnySyntax(extendeeModel,
				"../org.purl.rvl.vocabulary/experiments/example-data/model_repo_test_extendee.ttl");

		ModelUtils.printModelInfo("extendee model", extendeeModel, false);

		// extender
		Model extenderModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		extenderModel.open();
		ModelUtils.readFromAnySyntax(extenderModel,
				"../org.purl.rvl.vocabulary/experiments/example-data/model_repo_test_extender.ttl");

		ModelUtils.printModelInfo("extender model", extenderModel, false);
		
		
		ModelUtils.printModelInfo("extra statement model", ModelUtils.getExtraStatementModel(extendeeModel, extenderModel), true);

	}

	
	public static void oldTest() throws ModelRuntimeException, IOException{
		
		ModelSet modelSet = RDF2Go.getModelFactory().createModelSet();
		modelSet.open();
		
		// data
		Model dataModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		dataModel.open();
		//ModelUtils.readFromAnySyntax(dataModel, ExampleData.LLD_TEST);
		
		// mapping
		Model mappingModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		mappingModel.open();
		//ModelUtils.readFromAnySyntax(mappingModel, ExampleMapping.LLD );
		ModelUtils.readFromAnySyntax(mappingModel, "../org.purl.rvl.vocabulary/experiments/example-data/model_repo_test_extendee.ttl" );
		
		ModelUtils.printModelInfo("mapping model", mappingModel, false);
		
		// mapping inferred
		Model inferredMappingModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		inferredMappingModel.open();
		inferredMappingModel.addModel(mappingModel);
		
		ModelUtils.printModelInfo("inferred mapping model", inferredMappingModel, false);
		
		
		// rvl
		Model rvlModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		rvlModel.open();
		//ModelUtils.readFromAnySyntax(rvlModel, OGVICProcess.RVL_LOCAL_REL );
		ModelUtils.readFromAnySyntax(rvlModel, "../org.purl.rvl.vocabulary/experiments/example-data/model_repo_test_extender.ttl" );
		
		ModelUtils.printModelInfo("rvl model", rvlModel, false);
		
		Model inferredRVLModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredRVLModel.open();
		inferredRVLModel.addModel(rvlModel);
		
		ModelUtils.printModelInfo("inferred rvl model", inferredRVLModel, false);
				
		// mapping + rvl
		Model mappingsAndRVLModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		mappingsAndRVLModel.open();
		mappingsAndRVLModel.addModel(inferredRVLModel);
		mappingsAndRVLModel.addModel(mappingModel);

		ModelUtils.printModelInfo("mappings + rvl model", mappingsAndRVLModel, false);

		
		Model inferredMappingsAndRVLModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredMappingsAndRVLModel.open();
		inferredMappingsAndRVLModel.addModel(mappingsAndRVLModel);
		
		ModelUtils.printModelInfo("inferred mappings + rvl model", inferredMappingsAndRVLModel, false);
		
		
		Model inferredRVLModelAndNoneInferredMappingsModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredRVLModelAndNoneInferredMappingsModel.open();
		inferredRVLModelAndNoneInferredMappingsModel.addModel(inferredRVLModel);
		inferredRVLModelAndNoneInferredMappingsModel.addModel(mappingModel);
		
		ModelUtils.printModelInfo("none-inferred mappings + rvl model", inferredRVLModelAndNoneInferredMappingsModel, false);
		
		
		
		Diff diff = inferredRVLModelAndNoneInferredMappingsModel.getDiff(inferredMappingsAndRVLModel.iterator());
		
		Iterable<Statement> addedIt = diff.getAdded();

		
		// enriched mappings
		Model extraMappingStatements = RDF2Go.getModelFactory().createModel(Reasoning.none);
		extraMappingStatements.open();
		extraMappingStatements.addAll(addedIt.iterator());
		
		ModelUtils.printModelInfo("extraMappingStatements-model", extraMappingStatements, true);
		
		// enriched mappings
		Model enrichedMappings = RDF2Go.getModelFactory().createModel(Reasoning.none);
		enrichedMappings.open();
		enrichedMappings.addModel(inferredMappingModel);
		enrichedMappings.addAll(addedIt.iterator());
		
		ModelUtils.printModelInfo("enriched mappings model", enrichedMappings, true);

		int i = 0;
		for (Statement statement : addedIt) {
			i++;
			LOGGER.finest("diff : added statement ("+ i +"): " + statement );
			
		}
		
		Iterable<Statement> removedIt = diff.getRemoved();
		
		for (Statement statement : removedIt) {
			LOGGER.finest("diff : removed statement: " + statement );
		}
		
		LOGGER.finest("enriched mappings model size: " + enrichedMappings.size());
		
		/*
		
		modelSet.addModel(dataModel, OGVICProcess.GRAPH_DATA);
		modelSet.addModel(mappingModel, OGVICProcess.GRAPH_MAPPING);
		modelSet.addModel(rvlModel, OGVICProcess.GRAPH_RVL_SCHEMA);
		modelSet.addModel(enrichedMappings, GRAPH_MAPPING_ENRICHED_WITH_RVL);
		
		
		
		

		
		
		System.out.println("Property mappings in the enriched mapping model: ");
		
		// find stmts filtered
		ClosableIterator<Statement> iteratorPM = mappingsAndRVLModel.findStatements(
				Variable.ANY,
				RDF.type,
				PropertyMappingX.RDFS_CLASS
				);

		while (iteratorPM.hasNext()) {
			
			Statement pmStmt = (Statement) iteratorPM.next();
			
			System.out.println(pmStmt);
			
		}
		
		System.out.println("Property mappings in the model sets enriched mapping graph: ");
		
		// find stmts filtered
		ClosableIterator<Statement> iteratorPMInModelSet = modelSet.findStatements(
				GRAPH_MAPPING_ENRICHED_WITH_RVL,
				Variable.ANY,
				RDF.type,
				PropertyMappingX.RDFS_CLASS
				);

		while (iteratorPMInModelSet.hasNext()) {
			
			Statement pmStmt = (Statement) iteratorPMInModelSet.next();
			
			System.out.println(pmStmt);
			
		}*/
		

		//listModelStatements("data model",dataModel);
		//printModelSet(modelSet);
		
		/*
		
		// find stmts filtered
		ClosableIterator<Statement> iterator = modelSet.findStatements(
				GRAPH_DATA,
				Variable.ANY,
				Variable.ANY,
				Variable.ANY
				);

		while (iterator.hasNext()) {
			
			Statement dataStatement = (Statement) iterator.next();
			
			System.out.println(dataStatement);
			
		}
		
		*/
		
		/*
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
			
		} */
		
	}

}
