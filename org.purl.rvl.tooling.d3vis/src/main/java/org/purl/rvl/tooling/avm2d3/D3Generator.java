package org.purl.rvl.tooling.avm2d3;

import org.purl.rvl.exception.OGVICModelsException;
import org.purl.rvl.exception.d3.D3GeneratorException;
import org.purl.rvl.tooling.model.ModelManager;

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

	public abstract void init(ModelManager modelManager);

	public abstract String generateJSONforD3() throws D3GeneratorException, OGVICModelsException;

	public abstract String getGenJSONFileName();

	public abstract String getDefaultD3GraphicFile();

}