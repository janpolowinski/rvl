package org.purl.rvl.tooling.avm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.tooling.OGVICProcess;


public class D3GeneratorBase {
	
	public static final int GENERATOR_SIMPLE_JSON = 1;
	public static final int GENERATOR_TREE_JSON = 2;

	protected static final String NL = System.getProperty("line.separator");

	protected Model model;
	protected Model modelVISO;
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	
	/**
	 * @param model
	 * @param modelVISO
	 */
	public D3GeneratorBase(Model model, Model modelVISO) {
		super();
		this.model = model;
		this.modelVISO = modelVISO;
	}

	public D3GeneratorBase() {
		super();
	}



	/**
	 * Saves a String to JSON file
	 */
	public void writeJSONToFile(String fileContent){
		try {
			String fileName = OGVICProcess.JSON_FILE_NAME_REL;
			
			FileWriter writer = new FileWriter(fileName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			LOGGER.info("JSON written to " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}