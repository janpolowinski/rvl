package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3Generator;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.avm2d3.GraphicType;
import org.purl.rvl.tooling.commons.FileRegistry;

/**
 * @author Jan Polowinski
 *
 */
@XmlRootElement
public class VisProject {
	
	private final  FileRegistry dataFileRegistry = new FileRegistry("data files"); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry("mapping files"); // Mapping files (each interpreted as a mapping set)
	private D3Generator d3Generator;
	private Reasoning reasoningDataModel = Reasoning.rdfs;
	
	private String id;
	private String name;
	private String description;
	private String defaultGraphicType;


	/**
	 * only for jaxb
	 */
	public VisProject() {
	}
	
	/**
	 * @param reasoningDataModel
	 * @param name
	 */
	public VisProject(String name) {
		super();
		this.name = name;
		this.id = name;
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
	
	@XmlTransient
	public Reasoning getReasoningDataModel() {
		return this.reasoningDataModel;
	}


	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}


	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the dataFileRegistry
	 */
	@XmlTransient
	public FileRegistry getDataFileRegistry() {
		return dataFileRegistry;
	}


	/**
	 * @return the mappingFileRegistry
	 */
	@XmlTransient
	public FileRegistry getMappingFileRegistry() {
		return mappingFileRegistry;
	}

	@XmlTransient
	public D3Generator getD3Generator() {
		if (null == d3Generator) {
			deriveDefaultGenerator();
		}
		return d3Generator;
	}

	public void setD3Generator(D3Generator d3Generator) {
		this.d3Generator = d3Generator;
	}

	/**
	 * @return the defaultGraphicType
	 */
	public String getDefaultGraphicType() {
		if (null == defaultGraphicType) {
			deriveDefaultGraphicType();
		}
		return defaultGraphicType;
	}

	private void deriveDefaultGraphicType() {
		this.defaultGraphicType = getD3Generator().getDefaultGraphicType();
	}
	
	private void deriveDefaultGenerator() {
		if (null == d3Generator) {
			if (
				null != defaultGraphicType && 
				(
				defaultGraphicType.equals(GraphicType.COLLAPSIBLE_TREE) ||
				defaultGraphicType.equals(GraphicType.CIRCLE_PACKING_ZOOMABLE)
				)
			) {
				d3Generator = new D3GeneratorTreeJSON();
			} else {
				d3Generator = new D3GeneratorDeepLabelsJSON();
			}
		}
	}

	/**
	 * @param defaultGraphicType the defaultGraphicType to set
	 */
	public void setDefaultGraphicType(String defaultGraphicType) {
		this.defaultGraphicType = defaultGraphicType;
	}

	/**
	 * @deprecated - work with the default graphic type instead
	 * 
	 * Returns the path of the html file to be used for rendering the d3 graphic. When this was not 
	 * set, the default defined by the generator will be used.
	 * 
	 * @return the path of the html file to be used for rendering the d3 graphic
	 */
	@XmlTransient
	public String getD3GraphicFile() {
		if (null != defaultGraphicType)
			return defaultGraphicType + "/index.html";
		else
			return null;
	}

}
