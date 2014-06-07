/**
 * 
 */
package org.purl.rvl.tooling.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;

/**
 * @author Jan Polowinski
 *
 */
public class ModelUtils {

	private final static Logger LOGGER = Logger.getLogger(ModelUtils.class.getName());

	public static void readFromAnySyntax(Model model, String fileName) {
		
		File file = new File(fileName);
		readFromAnySyntax(model, file);
	
	}

	public static void readFromAnySyntax(Model model, File file) {
	
		try {
			
			String extension = FilenameUtils.getExtension(file.getName());
			
			if (extension.equals("ttl") || extension.equals("n3")) {
				model.readFrom(new FileReader(file),
						Syntax.Turtle);
			} else {
				model.readFrom(new FileReader(file),
						Syntax.RdfXml);
			}
			
		} catch (FileNotFoundException e) {
			LOGGER.info("File could not be read into the model, since it wasn't found: " +  file.getPath());
		} catch (IOException e) {
			LOGGER.info("File could not be read into the model: " +  file.getPath());
			e.printStackTrace();
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

	/**
	 * Write a model to file in Turtle serialisation
	 * @param fileName
	 */
	private static void writeModelToTurtleFile(Model modelToWrite, String fileName) {
		
		try {
			
			FileWriter writer = new FileWriter(fileName);
			modelToWrite.writeTo(writer, Syntax.Turtle);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @param extendeeModel - reasoning may be none or rdfs
	 * @param extenderModel - reasoning may be none or rdfs
	 * @return Returns a model containing only the extra statements that are created when combining the two inferred models.
	 */
	public static Model getExtraStatementModel(Model extendeeModel, Model extenderModel) {
		
		// reason on models (they could be non-reasoning models)
		
		Model inferenceExtendeeModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		inferenceExtendeeModel.open();
		inferenceExtendeeModel.addModel(extendeeModel);
	
		Model inferenceExtenderModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		inferenceExtenderModel.open();
		inferenceExtenderModel.addModel(extenderModel);
	
		// manifest inferred statements into new models
		
		Model inferredExtendeeModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredExtendeeModel.open();
		inferredExtendeeModel.addModel(inferenceExtendeeModel);
	
		Model inferredExtenderModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredExtenderModel.open();
		inferredExtenderModel.addModel(inferenceExtenderModel);
		
		printModelInfo("extendee model", extendeeModel, false);
		printModelInfo("extender model", extenderModel, false);
		printModelInfo("inference extendee model", inferenceExtendeeModel, false);
		printModelInfo("inference extender model", inferenceExtenderModel, false);
		printModelInfo("inferred extendee model", inferredExtendeeModel, false);
		printModelInfo("inferred extender model", inferredExtenderModel, false);
				
		// combined models (with and without reasoning)
		
		Model combinedModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		combinedModel.open();
		combinedModel.addModel(inferredExtenderModel);
		combinedModel.addModel(inferredExtendeeModel);
		
		Model inferenceCombinedModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		inferenceCombinedModel.open();
		inferenceCombinedModel.addModel(combinedModel);
	
		// manifest combined model
		Model inferredCombinedModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		inferredCombinedModel.open();
		inferredCombinedModel.addModel(inferenceCombinedModel);
	
		printModelInfo("combined model", combinedModel, false);
		printModelInfo("inference combined model", inferenceCombinedModel, false);	
		printModelInfo("inferred combined model", inferredCombinedModel, false);
		
		Diff diff = combinedModel.getDiff(inferredCombinedModel.iterator());
		Iterable<Statement> addedIt = diff.getAdded();
		
		// extra statements are those added 
	
		Model extraMappingStatements = RDF2Go.getModelFactory().createModel(Reasoning.none);
		extraMappingStatements.open();
		extraMappingStatements.addAll(addedIt.iterator());
		
		printModelInfo("extra statements model", extraMappingStatements, true);
	
		return extraMappingStatements;
	}

	public static void printModelInfo(String modelName, Model model, boolean printEachStatement) {
	
	int i = 0;
		
		LOGGER.finest(modelName + " size of manifested statements (not inferred): " + model.size());
		
		if(printEachStatement) {
			
			ClosableIterator<Statement> it = model.findStatements(
					Variable.ANY,
					Variable.ANY,
					Variable.ANY
					);
			
			while (it.hasNext()) {
				Statement stmt = (Statement) it.next();
				LOGGER.finest(i + ": statement incl. inferred ones: " + stmt);
				i++;
			}
		}
	}

	public static boolean hasType(
			Model model, org.ontoware.rdf2go.model.node.Resource resource,
			org.ontoware.rdf2go.model.node.Resource type) {
	
			return model.contains(resource, RDF.type, type);
		
	}
	
}
