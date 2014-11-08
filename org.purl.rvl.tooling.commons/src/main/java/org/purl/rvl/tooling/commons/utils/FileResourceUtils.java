/**
 * 
 */
package org.purl.rvl.tooling.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * @author Jan Polowinski
 *
 */
public class FileResourceUtils {
	
	private static FileResourceUtils instance = new FileResourceUtils(); // only for resource localisation via getClass().getResourceAsStream()...

	private final static Logger LOGGER = Logger.getLogger(FileResourceUtils.class.getName());

	private static final String PATH_EXAMPLE_DATA = "/example-data";
	private static final String PATH_EXAMPLE_MAPPINGS = "/example-mappings";
	
	
	public static InputStream getFromWithinJars(File file) {
		InputStream stream = instance.getClass().getResourceAsStream(file.getPath());
		return stream;
	}
	
	
	public static InputStream getFromWithinJars(String fileName) {
		return getFromWithinJars(new File(fileName)); 
	}
	
	public static String getFromWithinJarsAsString(File file) throws IOException {
		InputStream stream = getFromWithinJars(file); 
		return IOUtils.toString(stream, "utf-8");
	}
	
	public static String getFromWithinJarsAsString(String fileName) throws IOException {
		InputStream stream = getFromWithinJars(fileName); 
		return IOUtils.toString(stream, "utf-8");
	}


	public static String getFromExampleData(String string) throws IOException {
		return getFromWithinJarsAsString(PATH_EXAMPLE_DATA + "/" + string);
	}


	public static String getFromExampleMappings(String string) throws IOException {
		return getFromWithinJarsAsString(PATH_EXAMPLE_MAPPINGS + "/" + string);
	}
}
