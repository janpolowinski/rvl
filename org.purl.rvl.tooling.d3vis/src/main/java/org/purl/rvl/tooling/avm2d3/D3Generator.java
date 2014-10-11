package org.purl.rvl.tooling.avm2d3;

import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.exception.d3.D3GeneratorException;

/**
 * @author Jan Polowinski
 *
 */
public interface D3Generator {

	public static final int GENERATOR_SIMPLE_JSON = 1;
	public static final int GENERATOR_TREE_JSON = 2;

	/**
	 * Saves a String to JSON file
	 */
	public abstract void writeJSONToFile(String fileContent, String destFileName);

	public abstract void init(Model modelAVM);

	public abstract String generateJSONforD3() throws D3GeneratorException;

	public abstract String getGenJSONFileName();

	public abstract String getDefaultD3GraphicFile();

}