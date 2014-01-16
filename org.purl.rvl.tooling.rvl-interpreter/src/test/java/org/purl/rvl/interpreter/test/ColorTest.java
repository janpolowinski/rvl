package org.purl.rvl.interpreter.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.java.exception.IncompleteColorValuesException;
import org.purl.rvl.java.viso.graphic.Color;
import org.purl.rvl.tooling.ModelBuilder;
import org.purl.rvl.tooling.util.AVMUtils;

public class ColorTest {
	
	Model model;

	@Before
	public void setUp() throws Exception {
		model = ModelBuilder.getVISOModel();
	}

	@Test
	public void testToHexString() {
		Color color = new Color(100,100,100,model,false);
		
		String expectedColorHexString = "#646464";
		String colorHexString = "";
		try {
			colorHexString = color.toHexString();
		} catch (IncompleteColorValuesException e) {
		}
		assertEquals("color hex string not as expected", expectedColorHexString ,colorHexString);
	}
	
	//@Test
	public void testListAllColors() {
		AVMUtils.listAllColors(model);
	}

}
