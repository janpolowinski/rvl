/**
 * 
 */
package org.purl.rvl.tooling.avm;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.interpreter.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.interpreter.gen.viso.graphic.Color;
import org.purl.rvl.interpreter.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.interpreter.rvl.PropertyToGraphicAttributeMapping;
import org.purl.rvl.interpreter.viso.graphic.GraphicObject;
import org.purl.rvl.interpreter.viso.graphic.GraphicSpace;

/**
 * @author Jan Polowinski
 *
 */
public class AVM {
	
	private static Model model;
	private static Model modelVISO;
	
	final private static String tmpAvmModelFileName = "avm.ttl";
	private static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json";
	
	final public static String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl"; // HACK: references the file in the vocabularies project
	final public static String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl"; // HACK: references the file in the vocabularies project
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl"; // HACK: references the file in the vocabularies project
	final public static String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";

	static final String NL =  System.getProperty("line.separator");
	
	
	private static void initRDF2GoModel() throws ModelRuntimeException {
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

	}
	
	private static GraphicSpace mainGS;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initRDF2GoModel();

		mainGS = new GraphicSpace();
		mainGS.addGraphicObject(new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_for_" + "res1", true));
		mainGS.addGraphicObject(new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_for_" + "res2", true));
		
		GraphicObject coloredGO = new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_for_" + "res3", true);
		Color newColor = new Color(model,"http://purl.org/rvl/example-avm/ANewColorRed", true);
		coloredGO.setColornamed(newColor);
		
		coloredGO.addContains(new GraphicObject(model, "http://purl.org/rvl/example-avm/GO_for_res3", true));

		  
		Set<PropertyToGraphicAttributeMapping> setOfSimpleP2GAMappings = getAllP2GAMappingsWithExplicitMappings();

		for (Iterator<PropertyToGraphicAttributeMapping> iterator = setOfSimpleP2GAMappings
				.iterator(); iterator.hasNext();) {
			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping) iterator
					.next();
			System.out.println(p2gam);

		}
		  
		
		
		createTestGOs();
		useLinkingDirected();
		//useContainment();
		generateJSONforD3();
		listAllGOs();
		
		//writeToFile();
	}
	

	
	public static void createTestGOs(){
		Random random = new Random();
		float positionX = 0;
		
		// get all available colors
		ClosableIterator<?extends Color> colorIt = Color.getAllInstances_as(modelVISO).asClosableIterator();
//		while (colorIt.hasNext()) {
//			Color color = (Color) colorIt.next();
//			System.out.println(color);
//			//System.out.println("Types:" + color.getAllType_as().asArray());
//		}
		
		// get all available colors
		colorIt = Color.getAllInstances_as(modelVISO).asClosableIterator();
	   for (int i = 0; i < 15; i++) {
	    int aID = random.nextInt(100000);
	    GraphicObject go = new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_" + aID, true);
	    go.setLabel("GO " + i);
	    if (colorIt.hasNext()) go.setColornamed(colorIt.next());
	    positionX = i*50;
	    go.setXposition(positionX);
	    go.setHeight(positionX);
	   }
	}
	
	/**
	 * Use binary (old) containment relation
	 */
	public static void useContainment(){
		//Model tmpModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		//tmpModel.open();
		//tmpModel.addAll(model.iterator()); tmpModel.addAll(modelVISO.iterator()); // causes all instances to be iterated below!!!! not only GOs!!!

		
		//ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> goIt = org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
		//ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containedIt = org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();

//		while (goIt.hasNext() && containedIt.hasNext()) {
//			GraphicObject container = (GraphicObject) goIt.next().castTo(GraphicObject.class);
//			GraphicObject containee = (GraphicObject) containedIt.next().castTo(GraphicObject.class);
//			if(containedIt.hasNext()) container.setContains(containee);
//			System.out.println(container);
//		}
		
		// TODO: refactor to use closable iterator directly
		List<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containerList = org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		List<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containeeList = org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		
		Iterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containerIt = containerList.iterator();
		Iterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> containeeIt = containeeList.iterator();
		System.out.println("The first GO (" + containerIt.next() + ") will not be contained by another GO.");
		
		while (containerIt.hasNext() &&  containeeIt.hasNext()) {
			GraphicObject container = (GraphicObject) containerIt.next().castTo(GraphicObject.class);
			GraphicObject containee = (GraphicObject) containeeIt.next().castTo(GraphicObject.class);
			container.addContains(containee);
			//System.out.println(containee);
		}
	}
	
	
	/**
	 * Use n-ary linking-directed relation
	 */
	public static void useLinkingDirected(){
				
		List<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> startNodeList = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		List<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> endNodeList = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		
		Iterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> startNodeIt = startNodeList.iterator();
		Iterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> endNodeIt = endNodeList.iterator();
		System.out.println("The last GO (" + endNodeIt.next() + ") will not be linked to another GO.");
		
		while (startNodeIt.hasNext() &&  endNodeIt.hasNext()) {
			GraphicObject startNode = (GraphicObject) startNodeIt.next().castTo(GraphicObject.class);
			GraphicObject endNode = (GraphicObject) endNodeIt.next().castTo(GraphicObject.class);
			DirectedLinking dlRel = new DirectedLinking(model, true);
			
			// add exemplary colors
			org.purl.rvl.interpreter.viso.graphic.Color newColor = 
					//new org.purl.rvl.interpreter.viso.graphic.Color(0,0,255, model, true);
					new org.purl.rvl.interpreter.viso.graphic.Color(model, "http://purl.org/viso/graphic/Red", true);
			newColor.setColorRGB("#FF00FF");
			Color red = Color.getInstance(model, new URIImpl("http://purl.org/viso/graphic/Red"));
			System.out.println("color label: " + red.getAllLabel_as().firstValue());
			startNode.setColornamed(newColor);
			startNode.setColornamed(red);
			
			// create a connector and add exemplary color
			GraphicObject connector = new GraphicObject(model, true);
			connector.setColornamed(red);
			connector.setColorhslhue(new Float(155));
			connector.setColorhslsaturation(new Float(100));
			connector.setColorhsllightness(new Float(50));
			
			// configure the relation
			dlRel.setStartnode(startNode);
			dlRel.setEndnode(endNode);
			dlRel.setLinkingconnector(connector);
			startNode.setLinkedto(dlRel);
			endNode.setLinkedfrom(dlRel);
		}
	}
	
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

	private static void listAllGOs(){	
		ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> goIt = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			GraphicObject go = (GraphicObject) goIt.next().castTo(GraphicObject.class);
			System.out.println(go);
		}		
	}
	
	/**
	 * save avm model to tmp file
	 */
	public static void writeToFile(){

	    try {
	     FileWriter writer = new FileWriter(tmpAvmModelFileName);
	     model.writeTo(writer, Syntax.Turtle);
	    } catch (IOException e) {
	     e.printStackTrace();
	    }
	    // close the model
	    // model.close();
	    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model
	}
	
	/**
	 * save to JSON file
	 */
	public static void writeJSONToFile(String fileContent){
		try {
			FileWriter writer = new FileWriter(jsonFileRelName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			System.out.println("JSON written to " + jsonFileRelName);
			// System.out.println(fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void generateJSONforD3(){
		
		org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject[] goArray = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asArray();
		
		Map<GraphicObject, Integer> goMap = new HashMap<GraphicObject, Integer>(50);
				
		// save GOs into a map to allow for looking up the index
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
			goMap.put(startNode,i);
		}
		
		String s = "";
		
		s += "{" + NL;
		s += "  \"nodes\":[" + NL;
		
		// generate JSON node entries
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
			
			//color
			String startNodeColorHex = "";
			try {
				if(startNode.hasColornamed()) {
					org.purl.rvl.interpreter.viso.graphic.Color startNodeColor = (org.purl.rvl.interpreter.viso.graphic.Color) startNode.getAllColornamed_as().firstValue().castTo(org.purl.rvl.interpreter.viso.graphic.Color.class);
					startNodeColorHex = startNodeColor.toHexString();
				}
			} catch (NullPointerException e) {
				System.err.println("No RGB values found, using default color");
			}
			if (startNodeColorHex.equals("")) {
				startNodeColorHex = org.purl.rvl.interpreter.viso.graphic.Color.getDefaultColorHex();
			}
			
			s += "    {\"name\":\"" + startNode.getAllLabel_as().firstValue() + "\", \"group\": \"" + startNodeColorHex + "\"}";
			if (i<goArray.length-1) s += ",";
			s += NL;
		}
		
		s += "  ]," + NL;
		s += "  \"links\":[" + NL;
		
		// generate JSON link entries
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
			try {
				ClosableIterator<? extends DirectedLinking> dlRelIt =
						startNode.getAllLinkedto_as().asClosableIterator();
				while (dlRelIt.hasNext()) {
					DirectedLinking dlRel = (DirectedLinking) dlRelIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
					GraphicObject endNode = (GraphicObject) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
					// get index of the endNode in the above generated Map
					s += "    {\"source\":" + i + ", \"target\":" + goMap.get(endNode) + ", \"value\":" + "1" + "}";
					//if (dlRelIt.hasNext()) 
						s += "," + NL;
				}
			} catch (Exception e) {
				System.err.println("linking to json error");
			}		
		}
		// hack: remove last "," and add new NL
		s=s.substring(0,s.lastIndexOf(",")) + NL;
		
		s += "  ]" + NL;
		s += "}" + NL;
		
		writeJSONToFile(s);
	}
	
	public void buildAVMFromSimpleRVLMappings(){
		
		
	}
	
	/**
	 * Get all the mappings that require no calculation, because they only have explicit 1-1-value-mappings
	 */
	public static Set<PropertyToGraphicAttributeMapping> getAllP2GAMappingsWithExplicitMappings(){
		
		Set<PropertyToGraphicAttributeMapping> mappingSet = new HashSet<PropertyToGraphicAttributeMapping>();

		String queryString = "" +
				"SELECT DISTINCT ?p2gam " +
				"WHERE { " +
				"    ?p2gam a <" + PropertyToGraphicAttributeMapping.RDFS_CLASS + "> . " +
				"    ?p2gam <" + PropertyToGraphicAttributeMapping.VALUEMAPPING + "> ?vm . " +
				"	{ " +
				"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
				"       WHERE " +
				"       { " +
				"	 		  ?vm <" + PropertyToGraphicAttributeMapping.SOURCEVALUE + "> ?sv  " +
				"       } " +
				"        GROUP BY ?vm " +
				"	} " +
				"    FILTER (?svCount = 1 ) " +
				"} " ;
		
		//System.out.println("All mappings with explicit value mappings (VMs with only 1 source value)");
		//System.out.println(queryString);
		
		QueryResultTable results = model.sparqlSelect(queryString);
//		for(QueryRow row : results) {System.out.println(row); }
//		for(String var : results.getVariables()) { System.out.println(var); }
		
		for(QueryRow row : results) {
			Property_to_Graphic_AttributeMapping p2gam = Property_to_Graphic_AttributeMapping.getInstance(model, (URI)row.getValue("p2gam"));
			mappingSet.add((PropertyToGraphicAttributeMapping)p2gam.castTo(PropertyToGraphicAttributeMapping.class));
			//System.out.println(row.getValue("p2gam"));
		}
		
		return mappingSet;
	}
	
}
