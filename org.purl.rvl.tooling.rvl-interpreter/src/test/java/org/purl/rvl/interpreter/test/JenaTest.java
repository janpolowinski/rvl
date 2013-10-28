package org.purl.rvl.interpreter.test;

import com.hp.hpl.jena.ontology.Individual;
import org.purl.rvl.interpreter.java.Rvl;
import org.w3c.dom.traversal.NodeIterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.impl.OntResourceImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class JenaTest {
	
	// paths
	final public static String PATH_TO_CASE_STUDIES = "file:///Users/Jan/Documents/EclipseWorkspace/SemVisRecherche/CaseStudies";
	final public static String PATH_TO_RVL_PROJECT = "file:///Users/Jan/Documents/TBCMEWorkspace/RVL";
	//final public static String PATH_TO_CASE_STUDIES = "file:///Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies";
	//final public static String PATH_TO_RVL_PROJECT = "file:///Users/Jan/VersionControlRepositories/git/rvl/org.purl.rvl.vocabulary";
	
	
	// example mappings
	final public static String EXAMPLE_MAPPING_SET_FOR_RVL = PATH_TO_RVL_PROJECT + "/rvl-example-mappings.ttl";
	final public static String EXAMPLE_MAPPING_BIO_AA_LOCAL = PATH_TO_CASE_STUDIES + "/Bio/AminoAcid/MappingExample/amino-acids-rvl-mapping-example.rdf";
	final public static String EXAMPLE_MAPPING_BIO_PO_LOCAL = PATH_TO_CASE_STUDIES + "/Bio/AminoAcid/MappingExample/amino-acids-rvl-mapping-example.rdf";
	final public static String EXAMPLE_MAPPING_BIO_ZEBRAFISH_LOCAL = PATH_TO_CASE_STUDIES + "/Bio/AminoAcid/MappingExample/amino-acids-rvl-mapping-example.rdf";
	final public static String EXAMPLE_MAPPING_REQ_STRO_LOCAL = PATH_TO_CASE_STUDIES + "/Requirements/stRO/MappingExample/ro-rvl-mapping-example.rdf";
	final public static String EXAMPLE_MAPPING_PUB_CITO = PATH_TO_CASE_STUDIES + "/Publication/SPAR/MappingExample/cito-rvl-mapping-example.rdf";
	final public static String EXAMPLE_MAPPING_EXTRA = PATH_TO_CASE_STUDIES + "/Extra/rvl-example-mappings.ttl";

	// example data
	final public static String EXAMPLE_DATA_SET_FOR_RVL_EXAMPLES = PATH_TO_RVL_PROJECT + "/rvl-example-data.ttl";
	final public static String BIO_AA_LOCAL = PATH_TO_CASE_STUDIES + "/Bio/AminoAcid/amino-acid.owl";

	// vocabularies
	final public static String RVL_LOCAL = PATH_TO_RVL_PROJECT + "/rvl.owl";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	   
		// create a Model without any inference
        //OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null ); 
        // create a Model with RDFS inference
        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_RDFS_INF, null ); 
        
        
        // read RVL schema
        //m.getDocumentManager().addAltEntry( "http://purl.org/rvl/", RVL_LOCAL );
        //m.read( "http://purl.org/rvl/" );
        m.read( RVL_LOCAL );
        //m.read( BIO_AA_LOCAL );
        
        // read example RVL files
        m.read( EXAMPLE_MAPPING_SET_FOR_RVL, "Turtle");
        m.read( EXAMPLE_DATA_SET_FOR_RVL_EXAMPLES, "Turtle");
        //m.read( EXAMPLE_MAPPING_BIO_AA_LOCAL );
        //m.read( EXAMPLE_MAPPING_BIO_PO_LOCAL );
        //m.read( EXAMPLE_MAPPING_BIO_ZEBRAFISH_LOCAL );
        //m.read( EXAMPLE_MAPPING_REQ_STRO_LOCAL );
        //m.read( EXAMPLE_MAPPING_PUB_CITO );
        //m.read( EXAMPLE_MAPPING_EXTRA, "Turtle");
        
		//m.write(System.out, "Turtle");
		
		// get the main mapping class
		OntClass mappingClass = m.getOntClass(Rvl.Mapping.getURI());
		// cannot be casted ...: OntClass mappingClass = (OntClass) Rvl.PropertyMapping;
		
		// show mapping subclasses and for each subclass ...
		ExtendedIterator<? extends OntResource> mappingClasses = mappingClass.listSubClasses();
		while (mappingClasses.hasNext())
		{
		  OntClass mappingSubClass = (OntClass) mappingClasses.next();
		  
		  // dont show VMs directly (they are shown in the context of PMs)
		  if(mappingSubClass.getURI().equals(Rvl.ValueMapping.getURI())) {
			  continue;
		  }
		  
		  System.out.println(mappingSubClass.getLocalName().toString() + "s:");
		  
		  // ... show single mapping instances ...
		  ExtendedIterator<? extends OntResource> mappings = mappingSubClass.listInstances();
			while (mappings.hasNext())
			{
			  Individual thisInstance = (Individual) mappings.next();
			  
			  System.out.println("   Mapping-Instance: " + thisInstance.getLocalName());// + " (" +thisInstance.toString() + ")");
			  
			  // ... and for PropertyMappings show the attached ValueMappings
			  if (thisInstance.hasRDFType(Rvl.PropertyMapping.getURI())){
				  
				  OntResource sourcePropertyValue = (OntResource) thisInstance.getPropertyValue(m.getProperty("http://purl.org/rvl/sourceProperty"));
				  OntResource targetGraphicRelationValue = (OntResource) thisInstance.getPropertyValue(m.getProperty("http://purl.org/rvl/targetGraphicRelation"));
				  // thanks to RDFS inference, the sub-properties of rvl:targetGraphicRelation such as rvl:targetAttribute will be considered as well
				  
				  System.out.println("          from \"" + sourcePropertyValue.getLocalName() + "\" to \"" + targetGraphicRelationValue.getLocalName() + "\"");
				  
				  ExtendedIterator<RDFNode> valueMappings = thisInstance.listPropertyValues(m.getProperty("http://purl.org/rvl/valueMapping"));
					while (valueMappings.hasNext())
					{
					  OntResource valueMapping = (OntResource) valueMappings.next();
					  OntResource sourceValueValue = (OntResourceImpl) valueMapping.getPropertyValue(Rvl.sourceValue);
					  OntResource targetValueValue = (OntResourceImpl) valueMapping.getPropertyValue(Rvl.targetValue);
					  OntResource sourceValueInterval = (OntResourceImpl) valueMapping.getPropertyValue(m.getProperty("http://purl.org/rvl/sourceValueInterval"));
					  OntResource targetValueInterval = (OntResourceImpl) valueMapping.getPropertyValue(m.getProperty("http://purl.org/rvl/targetValueInterval"));
					  OntResource targetValueCycleValue = (OntResourceImpl) valueMapping.getPropertyValue(m.getProperty("http://purl.org/rvl/targetValueCycle"));
					  					  
					  if (!valueMapping.isAnon()) {
						  System.out.println("          with ValueMapping: " + valueMapping.getLocalName());// + " (" +valueMapping.toString() + ")");
					  }
					  else {
						  System.out.println("          with anonymous ValueMapping:");
					  }

					  if (sourceValueValue!=null && targetValueValue!=null) {
						  // combine target values in a string
						  String target = "";
						  if(targetValueValue!=null) target += targetValueValue.getLocalName();
						  if(targetValueCycleValue!=null) target += "[cycles of values not yet supported]";//targetValueCycleValue;
						  System.out.println("               from \"" + sourceValueValue.getLocalName() + "\" to \"" + target +  "\"");
					  } else if (sourceValueInterval!=null || targetValueInterval!=null ) {
						  System.err.println("Value Mapping Intervals not yet fully interpreted");
						  
						  String intervalSettings = "";
						  if (sourceValueInterval!=null){
							  if (null!=sourceValueInterval.getPropertyValue(Rvl.lowerBoundExcl)) intervalSettings+="               from source interval [" + sourceValueInterval.getPropertyValue(Rvl.lowerBoundExcl);
							  if (null!=sourceValueInterval.getPropertyValue(Rvl.lowerBoundIncl)) intervalSettings+="               from source interval (" + sourceValueInterval.getPropertyValue(Rvl.lowerBoundIncl);
							  if (null!=sourceValueInterval.getPropertyValue(Rvl.lowerBoundExcl)) intervalSettings+="," + sourceValueInterval.getPropertyValue(Rvl.upperBoundExcl) + "]";
							  if (null!=sourceValueInterval.getPropertyValue(Rvl.lowerBoundIncl)) intervalSettings+="," + sourceValueInterval.getPropertyValue(Rvl.upperBoundIncl) + ")";
						  }
						  if (targetValueInterval!=null){
							  if (null!=targetValueInterval.getPropertyValue(Rvl.lowerBoundExcl)) intervalSettings+=" to target interval [" + targetValueInterval.getPropertyValue(Rvl.lowerBoundExcl);
							  if (null!=targetValueInterval.getPropertyValue(Rvl.lowerBoundIncl)) intervalSettings+=" to target interval (" + targetValueInterval.getPropertyValue(Rvl.lowerBoundIncl);
							  if (null!=targetValueInterval.getPropertyValue(Rvl.lowerBoundExcl)) intervalSettings+="," + targetValueInterval.getPropertyValue(Rvl.upperBoundExcl) + "]";
							  if (null!=targetValueInterval.getPropertyValue(Rvl.lowerBoundIncl)) intervalSettings+="," + targetValueInterval.getPropertyValue(Rvl.upperBoundIncl) + ")";
						  }
						  System.out.println(intervalSettings);
						  
					  } else
						  System.err.println("Problem(s) interpreting the settings of VM: " + valueMapping.getLocalName());
					}
					
					// space between mappings
					System.out.println("");
					
					// show calculated value mappings
					  
					/*
				  	// show all source values (Objects in Statements having as a predicate the sourceProperty)
				  	System.out.println("Source values for " + sourcePropertyValue.getLocalName() + ":");
				  	ExtendedIterator<RDFNode> allPotentialAvailableSourceValues = m.listObjectsOfProperty((Property) sourcePropertyValue);
				  	while (allPotentialAvailableSourceValues.hasNext()) {
				  		RDFNode potentialSourceValue = (RDFNode) allPotentialAvailableSourceValues.next();
				  		System.out.println(potentialSourceValue);
				  	}
				  	*/
					
					/*
					// list subjects
					System.out.println("subjects of sidechainstructure statements:");
					ResIterator iter = m.listSubjectsWithProperty(m.getProperty("http://www.w3.org/2002/07/owl#onProperty"));
					while (iter.hasNext()) {
					    Resource r = iter.nextResource();
					    System.out.println(r);
					}
					
					// list objects
					System.out.println("objects of sidechainstructure statements:");
					com.hp.hpl.jena.rdf.model.NodeIterator iter2 = m.listObjectsOfProperty(m.getProperty("http://www.w3.org/2002/07/owl#onProperty"));
					while (iter2.hasNext()) {
					    RDFNode r = iter2.next();
					    System.out.println(r);
					}
					*/
				
				  
				  	// show source values covered by the VM
				 
				  
				  	// show other Mappings, e.g. ResourceMappings ...
					
			  	}
			  
			}
			
		}
		
		
				
		
		
        
		// output the complete RVL schema as Turtle
        //m.write(System.out, "Turtle");
        
		/*
        // output all classes and their instances
		ExtendedIterator<OntClass> classes = m.listClasses();
		while (classes.hasNext())
		{
		  OntClass thisClass = (OntClass) classes.next();
		    System.out.println("Found class: " + thisClass.toString());

		  ExtendedIterator<? extends OntResource> instances = thisClass.listInstances();
		  while (instances.hasNext())
		  {
		    Individual thisInstance = (Individual) instances.next();
		    System.out.println("   Found instance: " + thisInstance.toString());
		  }
		}
		*/
		
		// output a property called by name via a generated static variable
		//Property test = Rvl.targetValue;
		//System.out.println(test);
		//System.out.println(Rvl.Mapping);
		


	}
	
	private void listSourceValues(Property sourceProperty) {
		System.out.println("to be done");
	}


}
