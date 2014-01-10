/**
 * 
 */
package org.purl.rvl.tooling.avm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.interpreter.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.interpreter.viso.graphic.GraphicObject;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorSimpleJSON extends D3GeneratorBase {

	
	/**
	 * Saves a String to JSON file
	 */
	public void writeJSONToFile(String fileContent){
		try {
			FileWriter writer = new FileWriter(OGVICProcess.jsonFileRelName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			System.out.println("JSON written to " + OGVICProcess.jsonFileRelName);
			// System.out.println(fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param model
	 * @param modelVISO
	 */
	public D3GeneratorSimpleJSON(Model model, Model modelVISO) {
		super(model, modelVISO);
	}

	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	public void generateJSONforD3(){
		
		// evtl. move the following ...
			org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject[] goArray = 
					org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asArray();
			
			Map<GraphicObject, Integer> goMap = new HashMap<GraphicObject, Integer>(50);
					
			// save GOs into a map to allow for looking up the index
			for (int i = 0; i < goArray.length; i++) {
				GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
				goMap.put(startNode,i);
			}
			
		// here starts the JSON encoding:
		
		JSONObject d3data=new JSONObject();
		List listOfNodes = new LinkedList();
		List listOfLinks = new LinkedList();
		
		// generate JSON node entries
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
			
			//color
			String startNodeColorRGBHex = startNode.getColorHex();
			// shape
			String startNodeShapeD3Name = startNode.getShape();
			
			Map node = new LinkedHashMap();
			node.put("label", startNode.getAllLabel_as().firstValue());
			node.put("color_rgb_hex", startNodeColorRGBHex);
			node.put("shape_d3_name", startNodeShapeD3Name);
			
			listOfNodes.add(node);
		}
		d3data.put("nodes", listOfNodes);

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
					Map link = new LinkedHashMap();
					link.put("source", i);
					link.put("target", goMap.get(endNode));
					link.put("value", "1");
					listOfLinks.add(link);				}
			} catch (Exception e) {
				System.err.println("No links could be generated." + e);
			}		
		}
				
		System.out.print(d3data);
		writeJSONToFile(d3data.toJSONString());
	}
	
}
