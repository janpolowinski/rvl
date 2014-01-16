package org.purl.rvl.interpreter.test;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.tooling.ModelBuilder;
import org.purl.rvl.tooling.util.RVLUtils;

public class MappingModelTest {
	
	Model model;

	@Before
	public void setUp() throws Exception {
		model = ModelBuilder.getModel();
	}

	@Test
	public void create10Mappings() {
		
		Random random = new Random();
		
		// create 10 new Mapping instances
		for (int i = 0; i < 10; i++) {
			
			// create a new ID
			int aID = random.nextInt(100000);
			
			org.purl.rvl.java.gen.rvl.Mapping mapping = 
					new org.purl.rvl.java.gen.rvl.Mapping(
							model,
							"http://purl.org/rvl/example-mappings/Mapping" + aID,
							true
							);
			mapping.setLabel("This is a new Mapping " + i);
			mapping.setIncludeinlegend(true);
			
			System.out.println(mapping.getClass().getName() + ": " + mapping);
			assertNotNull("Mapping " + mapping + " has no label" , mapping.getAllLabel_as().firstValue());
		}
		

	}
	
	@Test
	public void testListAllMappings() {
		
		System.out.println("");
		System.out.println("List of all mappings in the model:");
		System.out.println("");
		
		RVLUtils.listAllMappings(model);
	}
	
}
