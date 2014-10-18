package org.purl.rvl.interpreter.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.exception.IncompleteColorValuesException;
import org.purl.rvl.java.viso.graphic.ColorX;
import org.purl.rvl.tooling.model.ModelManager;
import org.purl.rvl.tooling.util.PrintUtils;

/**
 * @author Jan Polowinski
 *
 */
public class ColorTest {
	
	Model model;

	@Before
	public void setUp() throws Exception {
		
		ModelManager modelManager = ModelManager.getInstance();
		//modelManager.initTestModels();
		
		model = modelManager.getVISOModel();
	}

	@Test
	public void testToHexString() {
		ColorX color = new ColorX(100,100,100,model,false);
		
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
		PrintUtils.listAllColors(model);
	}

}
