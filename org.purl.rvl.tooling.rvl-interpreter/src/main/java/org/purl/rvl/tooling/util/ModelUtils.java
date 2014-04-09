/**
 * 
 */
package org.purl.rvl.tooling.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;

/**
 * @author Jan
 *
 */
public class ModelUtils {

	private final static Logger LOGGER = Logger.getLogger(ModelUtils.class.getName());

	public static void readFromAnySyntax(Model model, String fileName) {
		
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

}
