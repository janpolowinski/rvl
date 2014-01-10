package org.purl.rvl.tooling.avm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.interpreter.viso.graphic.GraphicObject;
import org.purl.rvl.interpreter.viso.graphic.GraphicSpace;


public class OGVICProcess {
	
	protected static Model model;
	protected static Model modelVISO;
	protected static ExampleAVMBuilder builder;
	protected static D3GeneratorBase d3Generator;
	
	protected static final String tmpAvmModelFileName = "avm.ttl";
	//protected static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-layouted-nodes/data.json";
	protected static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json";
	
	public static final String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl";
	public static final String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		initRDF2GoModel();
		
		
//		builder = new ExampleAVMBuilder(model, modelVISO);
//		builder.createTestGraphicObjects();
//		builder.createTestLinkingDirectedRelations();
		
		
		
		SimpleRVLInterpreter rvlInterpreter = new SimpleRVLInterpreter(model, modelVISO);
		rvlInterpreter.interpreteSimpleP2GArvlMappings();
		rvlInterpreter.interpretesP2GO2ORMappings();
		
		
		d3Generator = new D3GeneratorSimpleJSON(model, modelVISO);
		((D3GeneratorSimpleJSON)d3Generator).generateJSONforD3();
		d3Generator.writeAVMToFile();
		

		/*
		d3Generator = new D3JacksonBinding(model, modelVISO);
		Set<GraphicObject> goTestSet = builder.createTestGraphicObjects();
		GraphicSpace graphicSpace = new GraphicSpace(goTestSet);
		try {
			((D3JacksonBinding)d3Generator).writeGraphicSpaceAsJSON(graphicSpace, new File(jsonFileRelName));
		} catch (Exception e) {
			System.out.println("Problem writing to JSON file");
			e.printStackTrace();
		}
		*/

		//listAllGOs();
		
	}
	

	protected static void initRDF2GoModel() throws ModelRuntimeException {
		// explicitly specify to use a specific ontology api here:
		// RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		// RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.
	
		// create the RDF2GO Models
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();
	
			try {
				modelVISO.readFrom(new FileReader(VISO_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(VISO_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(RVL_LOCAL_REL),
						Syntax.RdfXml);
				model.readFrom(new FileReader(REXD_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(REM_LOCAL_REL),
						Syntax.Turtle);
			} catch (IOException e) {
				System.out.println("Problem reading one of the files into the model");
				e.printStackTrace();
			}
			
			ExampleAVMBuilder builder = new ExampleAVMBuilder(model,modelVISO);
	
	}
	
	/**
	 * List all Graphic Objects in the model
	 */
	protected static void listAllGOs() {	
		ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> goIt = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			GraphicObject go = (GraphicObject) goIt.next().castTo(GraphicObject.class);
			System.out.println(go);
		}		
	}
	
	/**
	 * List all Resources in the model
	 */
	private static void listAllResourcesWithTheirTypes(){	
		ClosableIterator<? extends Resource> resIt = 
			Resource.getAllInstance_as(model).asClosableIterator();
		while (resIt.hasNext()) {
			Resource res = (Resource) resIt.next();
	
			System.out.println(res);
			//System.out.println("Types:" + go.getAllType_as().asArray()[0].asURI());
			
			for (org.ontoware.rdfreactor.schema.rdfs.Class type : res.getAllType_as().asList()) {
				try {
					System.out.println("       T: " + type.asURI());
				} catch (ClassCastException e) {
					System.err.println("evtl. blanknote");
				}
			}
		}		
	}

}
