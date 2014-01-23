/**
 * 
 */
package org.purl.rvl.tooling.avm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.tooling.util.AVMUtils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorTreeJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	static final int MAX_DEPTH = 5;	
	
	private int currentDepth = 0;
	
	/**
	 * @param model
	 * @param modelVISO
	 */
	public D3GeneratorTreeJSON(Model model, Model modelVISO) {
		super(model, modelVISO);
	}
	

	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	/*public String generateJSONforD3SingleRootNode(){
		
		JSONObject d3data = new JSONObject();

		GraphicObject rootNode = AVMUtils.getRootNodeGraphicObject(model);
		if (null != rootNode) {
			// generate JSON root object
			d3data.put("label", rootNode.getLabel());
			d3data.put("children", generateChildrenListFor(rootNode));
		}
		else {
			LOGGER.severe("No root node found!");
		}

		return d3data.toJSONString();
	}*/
	
	
	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	public String generateJSONforD3(){
		
		// generate empty JSON root object, containing all actual root nodes
		JSONObject d3data = new JSONObject();

		Set<GraphicObject> rootNodeSet = AVMUtils.getRootNodesGraphicObject(model);
		if (null!=rootNodeSet && !rootNodeSet.isEmpty()) {
			
			List listOfRootNodes = new LinkedList();
			
			for (Iterator<GraphicObject> iterator = rootNodeSet.iterator(); iterator.hasNext();) {
				
				GraphicObject actualRootNode = (GraphicObject) iterator.next();
				
				Map actualRootNodeObject = new LinkedHashMap();
				actualRootNodeObject.put("label", actualRootNode.getLabel());
				actualRootNodeObject.put("children", generateChildrenListFor(actualRootNode));
				
				listOfRootNodes.add(actualRootNodeObject);

			}
			d3data.put("children", listOfRootNodes);
		}
		else {
			LOGGER.severe("No root nodes found!");
		}

		return d3data.toJSONString();
	}
	
	
	private List generateChildrenListFor(GraphicObject parentGO) {
		
		List listOfChildren = new LinkedList();
		Set<DirectedLinking> directedLinkingsFromHere = AVMUtils.getDirectedLinkingRelationsFrom(model, parentGO);
		
		if(!directedLinkingsFromHere.isEmpty() && currentDepth<=MAX_DEPTH) {
			
			currentDepth ++;
			
			for (Iterator<DirectedLinking> iterator = directedLinkingsFromHere.iterator(); iterator
					.hasNext();) {
				DirectedLinking directedLinking = (DirectedLinking) iterator.next();
		
				/*
				GraphicObject endNode = (GraphicObject) directedLinking.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
				GraphicObject connector = (GraphicObject) directedLinking.getAllLinkingconnector_as().firstValue().castTo(GraphicObject.class);
				
				*/
				
				listOfChildren.add(generateObjectFor(directedLinking));
			}
			
			currentDepth --;
		}
		
		return  listOfChildren;
	}
	
	
	private Map generateObjectFor(DirectedLinking directedLinking) {
		
		GraphicObject endNode = (GraphicObject) directedLinking.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
		GraphicObject connector = (GraphicObject) directedLinking.getAllLinkingconnector_as().firstValue().castTo(GraphicObject.class);
		
		
		//color
		String endNodeColorRGBHex = endNode.getColorHex();
		// shape
		String endNodeShapeD3Name = endNode.getShape();
		//connector color
		String connectorColorRGBHex = connector.getColorHex();

		Map child = new LinkedHashMap();
		child.put("label", endNode.getLabel());
		child.put("color_rgb_hex", endNodeColorRGBHex);
		child.put("shape_d3_name", endNodeShapeD3Name);
		child.put("connector_color_rgb_hex", connectorColorRGBHex);
		
		// break possible circles
		child.put("children", generateChildrenListFor(endNode));
		
		return child;
	}
	

	
}
