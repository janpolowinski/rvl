package org.purl.rvl.tooling.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class FileRegistry {
	
	//private static FileRegistry instance = new FileRegistry("dummy");
	
	private String name = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private final static Logger LOGGER = Logger.getLogger(FileRegistry.class.getName()); 
	
	Set<File> registeredFiles;
	
	public void addFile(File file) {
		registeredFiles.add(file);
	}
	
	public void addFile(String fileName) throws FileNotFoundException {
		
		File file = new File(fileName);
		
		if (file.isAbsolute()) {
			
			// for absolute paths (absolute: where the main method is called)
			
			if (checkFileExistenceWithinJar(file)) {
				
				LOGGER.finer("Adding file to the " + name + " registry: " + fileName);
				addFile(file);
					
			} else {
				throw new FileNotFoundException("File not found (" + name + " registry) : " + fileName);
			}

		} else { 
			
			// for relative paths
			
			if (file.exists()) {
				
				LOGGER.finer("Adding file to the " + name + " registry: " + fileName);
				addFile(file);
				
			} else {
				throw new FileNotFoundException("File not found (" + name + " registry) : " + fileName);
			}
		}			
	}
	
	private boolean checkFileExistenceWithinJar(File file) {
		InputStream absolutePath = this.getClass().getResourceAsStream(file.getPath());
		if (null == absolutePath) {
			return false;
		} else {
			return true;
		}
	}

	public Set<File> getFiles() {
		return registeredFiles;
	}

	/**
	 * @param registeredFiles
	 */
	public FileRegistry(String name) {
		super();
		this.registeredFiles = new HashSet<File>();
		this.name = name;
	}

}
