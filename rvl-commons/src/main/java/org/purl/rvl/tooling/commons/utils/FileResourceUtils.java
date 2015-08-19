/**
 * 
 */
package org.purl.rvl.tooling.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	/**
	 * Return true if the file could be found in one of the places where we look for files:
	 * 	- for absolute paths look: 1) in the file system (also tmp folder is addressed absolutely) 2) within the jars
	 *  - for relative paths look: 2) relative to the calling class
	 * @param file
	 * @return
	 */
	public static boolean exists(File file) {
		// TODO this can't handle real file-system-absolute files anymore!

			if (file.isAbsolute()) { 
				// for absolute paths (absolute: where the main method is called)
//				try {
				if (checkFileExistenceWithinJar(file)) return true;
				else return false;
//				} catch (Exception e) {};
			} else {
				// for relative paths
				if (file.exists()) return true;
				else return false;
			} 
	}
	
	/**
	 * Return true if the file could be found in one of the places where we look for files:
	 * 	- for absolute paths look: 1) in the file system (also tmp folder is addressed absolutely) 2) within the jars
	 *  - for relative paths look: 2) relative to the calling class
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static InputStream getInputStream(File file) throws FileNotFoundException {
		// TODO this can't handle real file-system-absolute files anymore!

			if (file.isAbsolute()) { 
				// for absolute paths (absolute: where the main method is called)
//				try {
				if (checkFileExistenceWithinJar(file)) {
					return FileResourceUtils.getFromWithinJars(file);
				}
				else { 
					return new FileInputStream(file);
				}
//				} catch (Exception e) {};
			} else {
				// for relative paths
				if (file.exists()) {
					return new FileInputStream(file);
				}
			}
			return null; 
	}


	public static boolean checkFileExistenceWithinJar(File file) {
		InputStream absolutePath = instance.getClass().getResourceAsStream(file.getPath());
		if (null == absolutePath) {
			return false;
		} else {
			return true;
		}
	}
}
