package org.purl.rvl.tooling.avm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.tooling.OGVICProcess;


public class D3GeneratorBase {

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
	 * Saves the whole Model to a tmp file 
	 * TODO: does not currently filter out non-avm triples!
	 */
	public void writeAVMToFile(){

	    try {
	     FileWriter writer = new FileWriter(OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
	     model.writeTo(writer, Syntax.Turtle);
	    } catch (IOException e) {
	     e.printStackTrace();
	    }
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