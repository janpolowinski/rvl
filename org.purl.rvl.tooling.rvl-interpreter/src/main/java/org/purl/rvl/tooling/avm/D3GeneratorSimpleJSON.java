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
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObject;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorSimpleJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	
	
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
	public String generateJSONforD3(){
		
		// evtl. move the following ...
			org.purl.rvl.java.gen.viso.graphic.GraphicObject[] goArray = 
					org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asArray();
			
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
					GraphicObject connector = (GraphicObject) dlRel.getAllLinkingconnector_as().firstValue().castTo(GraphicObject.class);
					// get index of the endNode in the above generated Map
					Map link = new LinkedHashMap();
					link.put("source", i);
					link.put("target", goMap.get(endNode));
					link.put("value", "1");
					link.put("color_rgb_hex", connector.getColorHex());
					listOfLinks.add(link);				}
			} catch (Exception e) {
				LOGGER.warning("No links could be generated." + e);
			}		
		}
		
		d3data.put("links", listOfLinks);
		
		return d3data.toJSONString();
	}
	
}
