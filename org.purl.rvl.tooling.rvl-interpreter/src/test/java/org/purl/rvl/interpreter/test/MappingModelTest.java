package org.purl.rvl.interpreter.test;

import static org.junit.Assert.assertNotNull;

import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.tooling.ModelBuilder;
import org.purl.rvl.tooling.util.CustomRecordFormatter;
import org.purl.rvl.tooling.util.PrintUtils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

/**
 * @author Jan Polowinski
 *
 */
public class MappingModelTest {

	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static {
	
		//LOGGER.setLevel(Level.SEVERE); 
		//LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE); 
		LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINEST);

		
		// In order to show log entrys of the fine level, we need to create a new handler as well
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINEST);
        
        CustomRecordFormatter formatter = new CustomRecordFormatter();
        handler.setFormatter(formatter); // out-comment this line to use the normal formatting with method and date
        
        LOGGER_RVL_PACKAGE.setUseParentHandlers(false); // otherwise double output of log entries
        LOGGER_RVL_PACKAGE.addHandler(handler);
		
	}
	
	Model model;

	@Before
	public void setUp() throws Exception {
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.initTestModels();
		
		model = modelBuilder.getMappingsModel();
	}

	@Test @Ignore
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
		PrintUtils.listAllMappings(model);
	}
	
	@Test @Ignore
	public void testPrintMappingWithURI(){
		//RVLUtils.printMappingWithURI(model, "http://purl.org/rvl/example-mappings/PMwithNamedSubmappingToNamedMappingOnConnector");
		//RVLUtils.printMappingWithURI(model, "http://purl.org/rvl/example-mappings/PMwithAnonymousSubmappingToNamedMappingOnConnector");
		//RVLUtils.printMappingWithURI(model, "http://purl.org/rvl/example-mappings/PMwithAnonSubmappingOnConnector");
		PrintUtils.printMappingWithURI(model, "http://purl.org/rvl/example-mappings/PMwithValueMappingOfRanges");
	}
	
	@Test @Ignore 
	public void testJena(){
		System.out.println("Access via Jena:");
		System.out.println();
		ModelCom jenaModelCom = (ModelCom) model.getUnderlyingModelImplementation();
	
		Property subMappingProperty =  jenaModelCom.getProperty("http://purl.org/rvl/subMapping");
		Property onTriplePartProperty =  jenaModelCom.getProperty("http://purl.org/rvl/subMapping-onTriplePart");
		Property onRoleProperty =  jenaModelCom.getProperty("http://purl.org/rvl/subMapping-onRole");
		Property mappingProperty =  jenaModelCom.getProperty("http://purl.org/rvl/subMapping-mapping");
		
		Resource resource =  jenaModelCom.getResource("http://purl.org/rvl/example-mappings/PMwithNamedSubmappingToNamedMappingOnConnector");
		Statement subMappingStatement =  jenaModelCom.getProperty(resource, subMappingProperty);
		RDFNode subMappingNode = subMappingStatement.getObject();
		
		System.out.println("all statements with subject: " + subMappingNode);
		StmtIterator it = jenaModelCom.listStatements(subMappingNode.asResource(), null, (RDFNode) null);
		while (it.hasNext()) {
			System.out.println("  "+it.next());
		}
		
		System.out.println();
		
		Property p = onRoleProperty;
		System.out.println("statements with subject: " + subMappingNode + " and property " + p);
		StmtIterator it2 = jenaModelCom.listStatements(subMappingNode.asResource(), p, (RDFNode) null);
		while (it2.hasNext()) {
			System.out.println("  "+it2.next());
		}
		
		System.out.println();
		
		//OntClass mappingClass = jenaModel.getOntClass("http://purl.org/rvl/example-mappings/PMwithNamedSubmappingToNamedMappingOnConnector");
		//OntResource sourcePropertyValue = (OntResource) thisInstance.getPropertyValue(m.getProperty("http://purl.org/rvl/sourceProperty"));
		System.out.println("Mapping: " + resource);
		System.out.println("  SubMapping: " + subMappingNode.asResource());
		System.out.println("    -onRole: " + jenaModelCom.getProperty(subMappingNode.asResource(), onRoleProperty).getObject());
		System.out.println("    -mapping: " + jenaModelCom.getProperty(subMappingNode.asResource(), mappingProperty).getObject());
		System.out.println("    -onTriplePart: " + jenaModelCom.getProperty(subMappingNode.asResource(), onTriplePartProperty).getObject());
	}
	
	

	
}
