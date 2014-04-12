package org.purl.rvl.tooling.process;

import java.util.logging.Logger;

import org.ontoware.rdf2go.Reasoning;

public class VisProject {
	
	private final  FileRegistry dataFileRegistry = new FileRegistry(); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry(); // Mapping files (each interpreted as a mapping set)

	private Reasoning reasoningDataModel = Reasoning.rdfs;
	private String name;
	
	/**
	 * @param reasoningDataModel
	 * @param name
	 */
	public VisProject(String name) {
		super();
		this.name = name;
	}


	// LOGGING
	private final static Logger LOGGER = Logger.getLogger(OGVICProcess.class.getName()); 

	
	public void registerMappingFile(String fileName){
		this.mappingFileRegistry.addFile(fileName);
	}
	
	
	public void registerDataFile(String fileName){
		this.dataFileRegistry.addFile(fileName);
	}
	
	/**
	 * Turns reasoning on the data model on/off.
	 * @param reasoningDataModel
	 */
	public void setReasoningDataModel(Reasoning reasoningDataModel) {
		this.reasoningDataModel = reasoningDataModel;
	}
	
	public Reasoning getReasoningDataModel() {
		return this.reasoningDataModel;
	}


	public String getName() {
		return this.name;
	}


	/**
	 * @return the dataFileRegistry
	 */
	public FileRegistry getDataFileRegistry() {
		return dataFileRegistry;
	}


	/**
	 * @return the mappingFileRegistry
	 */
	public FileRegistry getMappingFileRegistry() {
		return mappingFileRegistry;
	}
}
