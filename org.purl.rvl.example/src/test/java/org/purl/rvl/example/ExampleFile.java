package org.purl.rvl.example;

import java.io.File;

public class ExampleFile {
	
	public static String EXAMPLE_FOLDER = "src/test/resources";
	
	public static String get(String pathRelativeToResourcesFolder){
		
		return EXAMPLE_FOLDER + File.separator + pathRelativeToResourcesFolder ; 
		
	}

}
