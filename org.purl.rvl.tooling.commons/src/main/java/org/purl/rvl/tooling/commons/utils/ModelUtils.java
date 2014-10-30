/**
 * 
 */
package org.purl.rvl.tooling.commons.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.tooling.commons.ResourcesCache;

/**
 * @author Jan Polowinski
 *
 */
public class ModelUtils {
	
	private static ModelUtils instance = new ModelUtils(); // only for resource localisation via getClass().getResourceAsStream()...

	private final static Logger LOGGER = Logger.getLogger(ModelUtils.class.getName());

	public static void readFromAnySyntax(Model model, String fileName) throws ModelRuntimeException, IOException {
		
		File file = new File(fileName);
		readFromAnySyntax(model, file);
	
	}

	public static void readFromAnySyntax(Model model, File file) throws ModelRuntimeException, IOException {
			
		String extension = FilenameUtils.getExtension(file.getName());

		Syntax syntax;
		
		if (extension.equals("ttl") || extension.equals("n3")) {
			syntax = Syntax.Turtle; 
		} else if (extension.equals("owl")) {
			syntax = Syntax.RdfXml; 
		} else if (extension.equals("tmp")) {
			syntax = Syntax.Turtle; 
			LOGGER.warning("assuming turtle syntax"); // FIXME
		} else {
			throw new IOException("Unsupported file extension '" + extension + "'");
		}
		
		if (file.isAbsolute()) {
			model.readFrom(getFromWithinJars(file), syntax);
		} else {
			model.readFrom(new FileReader(file), syntax);
		}

	}

	private static InputStream getFromWithinJars(File file) {
		
		InputStream absolutePath = instance.getClass().getResourceAsStream(file.getPath());
//		String theString = IOUtils.toString(absolutePath, "utf-8");
		
		return absolutePath;
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

	/**
	 * Get all types as a set of resources.
	 * 
	 * @param modelData
	 * @param instance
	 * @return - all types of instance as a set of resources
	 */
	public static Set<Resource> getTypes(Model modelData, Resource instance) {
		
		Set<Resource> types = new HashSet<Resource>();
		
		ClosableIterator<Statement> it =  modelData.findStatements(instance, RDF.type, Variable.ANY);
		
		while (it.hasNext()) {
			types.add(it.next().getObject().asResource());
		}
		
		return types;
	}

	public static <T extends Thing1> T tryReplaceWithCashedInstanceForSameURI(Thing1 instance, Class<T> clasz) {
		
		T castedInstance = (T) instance.castTo(clasz);
		
		return (T) ResourcesCache.getInstance().tryReplaceOrCache(castedInstance);
		
		/*	if (T extends Thing1) {
			castedInstance = (T) instance.castTo(clasz);
		}
		else {
			castedInstance = (T) instance;
		}*/
	}

	public static <T extends org.purl.rvl.java.gen.viso.graphic.Thing1> T tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(org.purl.rvl.java.gen.viso.graphic.Thing1 instance, Class<T> clasz) {
		
		T castedInstance = (T) instance.castTo(clasz);
		
		return (T) ResourcesCache.getInstance().tryReplaceOrCache(castedInstance);
	}

	public static String getGoodNodeLabel(Node node, Model model){
		
		
		String label;
		
		try {	 
			// somehow causes runtime exception with jena when not casted to resource as below
			label =  RDFTool.getGoodLabel(node.asResource(), model);
			
		} catch (ClassCastException e) {
			
			// when this didn't work for some reason, but it's still a resource
			
			try {
				
				// get the local/short name after # or the last /
				label = RDFTool.getShortName(node.asURI().toString());
				
			} catch (Exception e1) {
				
				// seems not to be a resource ...
				
				try {
					
					label = node.asLiteral().getValue();
					
				} catch (Exception e2) {
					
					// when nothing helps, toString it
					
					label = node.toString();
	
				}
				
			}
			
		}
	
		return label;
	}
	
}
