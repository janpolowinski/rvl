package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;

import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3Generator;
import org.purl.rvl.tooling.commons.FileRegistry;

/**
 * @author Jan Polowinski
 *
 */
public class VisProject {
	
	private final  FileRegistry dataFileRegistry = new FileRegistry("data files"); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry("mapping files"); // Mapping files (each interpreted as a mapping set)
	private D3Generator d3Generator;
	private Reasoning reasoningDataModel = Reasoning.rdfs;
	private String name;
	private String d3GraphicFile;

	
	/**
	 * @param reasoningDataModel
	 * @param name
	 */
	public VisProject(String name) {
		super();
		this.name = name;
	}


	
	public void registerMappingFile(String fileName) throws FileNotFoundException{
		this.mappingFileRegistry.addFile(fileName);
	}
	
	
	public void registerDataFile(String fileName) throws FileNotFoundException{
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


	/**
	 * Returns the path of the html file to be used for rendering the d3 graphic. When this was not 
	 * set, the default defined by the generator will be used.
	 * 
	 * @return the path of the html file to be used for rendering the d3 graphic
	 */
	public String getD3GraphicFile() {
		return d3GraphicFile;
	}


	/**
	 * @param d3GraphicFile - the path of the html file to be used for rendering the d3 graphic.
	 */
	public void setD3GraphicFile(String d3GraphicFile) {
		this.d3GraphicFile = d3GraphicFile;
	}

	


}
