package org.purl.rvl.tooling.process;

import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3Generator;

public class VisProject {
	
	private final  FileRegistry dataFileRegistry = new FileRegistry("data files"); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry("mapping files"); // Mapping files (each interpreted as a mapping set)
	private D3Generator d3Generator;
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


	public D3Generator getD3Generator() {
		return d3Generator;
	}
	
	public void setD3Generator(D3Generator d3Generator) {
		this.d3Generator = d3Generator;
	}



}
