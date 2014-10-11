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
import org.purl.rvl.exception.d3.D3GeneratorException;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.commons.utils.ModelUtils;
import org.purl.rvl.tooling.util.AVMUtils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorTreeJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorTreeJSON.class .getName()); 
	static final int MAX_DEPTH = 10;	
	
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
	 * Generates JSON using SimpleJSON
	 * @throws D3GeneratorException 
	 */
	public String generateJSONforD3() throws D3GeneratorException {
		
		// generate empty JSON root object, containing all actual root nodes
		JSONObject d3data = new JSONObject();

		Set<GraphicObjectX> rootNodeSet = AVMUtils.getRootNodesGraphicObject(modelAVM); 
		// TODO SEVERE: only some graphic relations considered by getRootNodesGraphicObject
		// like linking and containment !
		
		if (null != rootNodeSet && !rootNodeSet.isEmpty()) {
			
			List<Map<String,Object>> listOfRootNodes = new LinkedList<Map<String,Object>>();
			
			for (Iterator<GraphicObjectX> iterator = rootNodeSet.iterator(); iterator.hasNext();) {
				
				// the actual root nodes are handled separately since we need to take special care of 
				// rendering the relations from the structural (single) root node to the actual root nodes
				
				GraphicObjectX actualRootNode = (GraphicObjectX) iterator.next();
				
				// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
				actualRootNode = ModelUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(actualRootNode, GraphicObjectX.class);

				Map<String,Object> actualRootNodeObject = new LinkedHashMap<String,Object>();
				
				putAttributesLabelsRepresentedResource(actualRootNode, actualRootNodeObject);
			
				List<Map<String,Object>> childrenListLinking = generateChildrenListFor4Linking(actualRootNode);
				if (!childrenListLinking.isEmpty()) {
					actualRootNodeObject.put("type", "DirectedLinking");
					actualRootNodeObject.put("children", childrenListLinking);

					// special connectors from single structural root node to actual root nodes 
					Map<String,Object> connectorJSON = new LinkedHashMap<String,Object>();
					connectorJSON.put("arrow_type", "line");
					connectorJSON.put("width", "0"); // make this invisible
					connectorJSON.put("color_rgb_hex_combined", "#c00");
					actualRootNodeObject.put("connector", connectorJSON);
				}
				
				List<Map<String,Object>> childrenListContainment = generateChildrenListFor4Containment(actualRootNode);
				if (!childrenListContainment.isEmpty()) {
					actualRootNodeObject.put("type", "Containment");
					actualRootNodeObject.put("children", childrenListContainment);
				}
							
				listOfRootNodes.add(actualRootNodeObject);

			}
			d3data.put("children", listOfRootNodes);
		}
		else {
			throw new D3GeneratorException("No root nodes found!");
		}

		return d3data.toJSONString();
	}

	private List<Map<String,Object>> generateChildrenListFor4Linking(GraphicObjectX parentGO) {
		
		List<Map<String,Object>> listOfChildren = new LinkedList<Map<String,Object>>();
		
		Set<DirectedLinking> directedLinkingsFromHere = AVMUtils.getDirectedLinkingRelationsFrom(modelAVM, parentGO);
		
		if(!directedLinkingsFromHere.isEmpty() && currentDepth<=MAX_DEPTH) {
			
			currentDepth ++;
			
			for (Iterator<DirectedLinking> iterator = directedLinkingsFromHere.iterator(); iterator
					.hasNext();) {
				DirectedLinking directedLinking = (DirectedLinking) iterator.next();
				
				listOfChildren.add(generateObjectFor(directedLinking));
			}
			
			currentDepth --;
		}
		
		return  listOfChildren;
	}
	
	// cloned from linking, much redundant, similar code	
	private List<Map<String,Object>> generateChildrenListFor4Containment(GraphicObjectX parentGO) {
		
		List<Map<String,Object>> listOfChildren = new LinkedList<Map<String,Object>>();
		
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
	
	
	private Map<String,Object> generateObjectFor(DirectedLinking directedLinking) {
		
		GraphicObjectX endNode = (GraphicObjectX) directedLinking.getAllEndnode_as().firstValue().castTo(GraphicObjectX.class);
		GraphicObjectX connector = (GraphicObjectX) directedLinking.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
		
		// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
		endNode = ModelUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(endNode, GraphicObjectX.class);

		Map<String,Object> child = new LinkedHashMap<String,Object>();
		
		putAttributesLabelsRepresentedResource(endNode, child);
		
		// connector
		Map<String,Object> connectorJSON = new LinkedHashMap<String,Object>();
		connectorJSON.put("arrow_type", connector.getShape());
		//connectorJSON.put("width", connector.getWidth());
		//connectorJSON.put("color_rgb_hex", connector.getColorHex());
		//connectorJSON.put("color_rgb_hex_combined", connector.getColorRGBHexCombinedWithHSLValues());
		//putLabels(connector, getDefaultWidthNodes(), connectorJSON); // TODO width OK?
		putAttributesLabelsRepresentedResource(connector, connectorJSON);
		
		child.put("connector", connectorJSON);

		// break possible circles
		List<Map<String,Object>> childrenList = generateChildrenListFor4Linking(endNode);
		if (!childrenList.isEmpty()) {
			child.put("children", childrenList);
		}
		
		return child;
	}

	// cloned from linking, much redundant, similar code
	private Map<String,Object> generateObjectFor(Containment rel) {
		
		GraphicObjectX containee = (GraphicObjectX) rel.getAllContainmentcontainee_as().firstValue().castTo(GraphicObjectX.class);
		
		// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
		containee = ModelUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(containee, GraphicObjectX.class);

		Map<String,Object> child = new LinkedHashMap<String,Object>();

		putAttributesLabelsRepresentedResource(containee, child);
		
		// break possible circles
		List<Map<String,Object>> childrenList = generateChildrenListFor4Containment(containee);
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
