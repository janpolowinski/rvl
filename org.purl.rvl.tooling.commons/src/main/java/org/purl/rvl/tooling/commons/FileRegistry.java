package org.purl.rvl.tooling.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.purl.rvl.tooling.commons.utils.FileResourceUtils;

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
		
		if (FileResourceUtils.exists(file)) {
			LOGGER.finer("Adding file to the " + name + " registry: " + fileName);
			addFile(file);
		} else {
			throw new FileNotFoundException("Couldn't add file to " + name + " registry. "
					+ "File not found anywhere: " + fileName);
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
