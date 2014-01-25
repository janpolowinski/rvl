package org.purl.rvl.tooling.process;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

public class FileRegistry {
	
	private final static Logger LOGGER = Logger.getLogger(FileRegistry.class.getName()); 
	
	Set<File> registeredFiles;
	
	public void addFile(File file) {
		registeredFiles.add(file);
	}
	
	public void addFile(String fileName) {
		
		File file = new File(fileName);
		if (file.exists()) {

			LOGGER.info("Adding file to the registry: " + fileName);
			addFile(file);
			
		} else {
			LOGGER.severe("File not found: " + fileName);
		}
	}
	
	public Set<File> getFiles() {
		return registeredFiles;
	}

	/**
	 * @param registeredFiles
	 */
	public FileRegistry() {
		super();
		this.registeredFiles = new HashSet<File>();
	}

}
