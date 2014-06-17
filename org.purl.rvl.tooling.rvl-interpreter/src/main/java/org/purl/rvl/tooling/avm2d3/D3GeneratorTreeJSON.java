/**
 * 
 */
package org.purl.rvl.tooling.avm2d3;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.D3Utils;
import org.purl.rvl.tooling.util.RVLUtils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorTreeJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorTreeJSON.class .getName()); 
	static final int MAX_DEPTH = 5;	
	
	private int currentDepth = 0;
	
	public D3GeneratorTreeJSON() {
		super();
	}
	
	/**
	 * @param modelAVM
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

		GraphicObjectX rootNode = AVMUtils.getRootNodeGraphicObject(modelAVM);
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

		Set<GraphicObjectX> rootNodeSet = AVMUtils.getRootNodesGraphicObject(modelAVM); // TODO SEVERE: only linking considered here!!!
		if (null!=rootNodeSet && !rootNodeSet.isEmpty()) {
			
			List listOfRootNodes = new LinkedList();
			
			for (Iterator<GraphicObjectX> iterator = rootNodeSet.iterator(); iterator.hasNext();) {
				
				GraphicObjectX actualRootNode = (GraphicObjectX) iterator.next();
				
				// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
				actualRootNode = RVLUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(actualRootNode, GraphicObjectX.class);

				Map actualRootNodeObject = new LinkedHashMap();
				actualRootNodeObject.put("uri", actualRootNode.getRepresentedResource().toString());
				actualRootNodeObject.put("label",D3Utils.shortenLabel(actualRootNode.getLabel()));
				actualRootNodeObject.put("full_label",actualRootNode.getLabel() + " (ID: " + actualRootNode.getRepresentedResource() + ")");
				putGraphicAttributes(actualRootNodeObject,actualRootNode);
				actualRootNodeObject.put("connector_label", actualRootNode.getLabel());
			//	actualRootNodeObject.put("connector_arrow_type", connector.getShape());
			//	actualRootNodeObject.put("connector_color_rgb_hex", connectorColorRGBHex);
			//	actualRootNodeObject.put("connector_color_rgb_hex_combined", actualRootNode.getColorRGBHexCombinedWithHSLValues());
				
				List childrenListLinking = generateChildrenListFor4Linking(actualRootNode);
				if (!childrenListLinking.isEmpty()) {
					actualRootNodeObject.put("type", "DirectedLinking");
					actualRootNodeObject.put("children", childrenListLinking);
				}
				
				List childrenListContainment = generateChildrenListFor4Containment(actualRootNode);
				if (!childrenListContainment.isEmpty()) {
					actualRootNodeObject.put("type", "Containment");
					actualRootNodeObject.put("children", childrenListContainment);
				}
				
							
				listOfRootNodes.add(actualRootNodeObject);

			}
			d3data.put("children", listOfRootNodes);
		}
		else {
			LOGGER.severe("No root nodes found!");
		}

		return d3data.toJSONString();
	}
	
	
	private List generateChildrenListFor4Linking(GraphicObjectX parentGO) {
		
		List listOfChildren = new LinkedList();
		
		Set<DirectedLinking> directedLinkingsFromHere = AVMUtils.getDirectedLinkingRelationsFrom(modelAVM, parentGO);
		
		if(!directedLinkingsFromHere.isEmpty() && currentDepth<=MAX_DEPTH) {
			
			currentDepth ++;
			
			for (Iterator<DirectedLinking> iterator = directedLinkingsFromHere.iterator(); iterator
					.hasNext();) {
				DirectedLinking directedLinking = (DirectedLinking) iterator.next();
		
				/*
				GraphicObjectX endNode = (GraphicObjectX) directedLinking.getAllEndnode_as().firstValue().castTo(GraphicObjectX.class);
				GraphicObjectX connector = (GraphicObjectX) directedLinking.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
				
				*/
				
				listOfChildren.add(generateObjectFor(directedLinking));
			}
			
			currentDepth --;
		}
		
		return  listOfChildren;
	}
	
	// cloned from linking, much redundant, similar code	
	private List generateChildrenListFor4Containment(GraphicObjectX parentGO) {
		
		List listOfChildren = new LinkedList();
		
		Set<Containment> containmentsFromHere = AVMUtils.getContainmentRelationsFrom(modelAVM, parentGO);
		
		if(!containmentsFromHere.isEmpty() && currentDepth<=MAX_DEPTH) {
			
			currentDepth ++;
			
			for (Iterator<Containment> iterator = containmentsFromHere.iterator(); iterator
					.hasNext();) {
				Containment containment = (Containment) iterator.next();
						
				listOfChildren.add(generateObjectFor(containment));
			}
			
			currentDepth --;
		}
		
		return  listOfChildren;
	}
	
	
	private Map generateObjectFor(DirectedLinking directedLinking) {
		
		GraphicObjectX endNode = (GraphicObjectX) directedLinking.getAllEndnode_as().firstValue().castTo(GraphicObjectX.class);
		GraphicObjectX connector = (GraphicObjectX) directedLinking.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
		
		// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
		endNode = RVLUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(endNode, GraphicObjectX.class);
		
		

		//connector color
		String connectorColorRGBHex = connector.getColorHex();

		Map child = new LinkedHashMap();
		child.put("uri", endNode.getRepresentedResource().toString());
		child.put("label",D3Utils.shortenLabel(endNode.getLabel()));
		child.put("full_label",endNode.getLabel() + " (ID: " + endNode.getRepresentedResource() + ")");
		putGraphicAttributes(child,endNode);
		child.put("connector_label", connector.getLabel());
		child.put("connector_full_label", connector.getLabel() + " (ID: " + connector.getRepresentedResource() + ")");
		child.put("connector_arrow_type", connector.getShape());
		child.put("connector_width", connector.getWidth());
		child.put("connector_color_rgb_hex", connectorColorRGBHex);
		child.put("connector_color_rgb_hex_combined", connector.getColorRGBHexCombinedWithHSLValues());
		
		// break possible circles
		List childrenList = generateChildrenListFor4Linking(endNode);
		if (!childrenList.isEmpty()) {
			child.put("children", childrenList);
		}
		
		return child;
	}

	// cloned from linking, much redundant, similar code
private Map generateObjectFor(Containment rel) {
		
		GraphicObjectX containee = (GraphicObjectX) rel.getAllContainmentcontainee_as().firstValue().castTo(GraphicObjectX.class);
		
		// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
		containee = RVLUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(containee, GraphicObjectX.class);
		
	
		Map child = new LinkedHashMap();
		child.put("uri", containee.getRepresentedResource().toString());
		child.put("label",D3Utils.shortenLabel(containee.getLabel()));
		child.put("full_label",containee.getLabel() + " (ID: " + containee.getRepresentedResource() + ")");

		putGraphicAttributes(child,containee);
		
		child.put("connector_arrow_type", "uml_generalization_arrow");
		child.put("connector_color_rgb_hex", "#ccc");
		
		// break possible circles
		List childrenList = generateChildrenListFor4Containment(containee);
		if (!childrenList.isEmpty()) {
			child.put("children", childrenList);
		}
		
		return child;
	}

@Override
public String getGenJSONFileName() {
	return "tree-data.json";
}

public String getDefaultD3GraphicFile(){
	return "collapsible_tree/index.html";
}
	

	
}
