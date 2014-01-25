package org.purl.rvl.tooling.avm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.tooling.process.OGVICProcess;


public abstract class D3GeneratorBase {
	
	public static final int GENERATOR_SIMPLE_JSON = 1;
	public static final int GENERATOR_TREE_JSON = 2;

	protected static final String NL = System.getProperty("line.separator");

	protected Model model;
	protected Model modelVISO;
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	
	
	public D3GeneratorBase() {
		super();
	}

	
	/**
	 * @param model
	 * @param modelVISO
	 */
	public D3GeneratorBase(Model model, Model modelVISO) {
		super();
		this.model = model;
		this.modelVISO = modelVISO;
	}




	/**
	 * Saves a String to JSON file
	 */
	public void writeJSONToFile(String fileContent){
		try {
			String fileName = OGVICProcess.getInstance().getJsonFileNameRel();
			
			FileWriter writer = new FileWriter(fileName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			LOGGER.info("JSON written to " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init(Model model) {
		this.model = model;
	}
	
	public abstract String generateJSONforD3();
	
}