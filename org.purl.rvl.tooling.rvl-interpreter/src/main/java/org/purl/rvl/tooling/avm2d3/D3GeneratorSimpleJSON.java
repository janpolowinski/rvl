/**
 * 
 */
package org.purl.rvl.tooling.avm2d3;

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
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.D3Utils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorSimpleJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	
	public D3GeneratorSimpleJSON() {
		super();
	}
	
	/**
	 * @param modelAVM
	 * @param modelVISO
	 */
	public D3GeneratorSimpleJSON(Model model, Model modelVISO) {
		super(model, modelVISO);
	}
	

	// TODO move to some upper class or AVMUTils
	private Set<GraphicObject> getAllGraphicObjects(){
		
		Set<GraphicObject> gos = new HashSet<GraphicObject>();
		
		org.purl.rvl.java.gen.viso.graphic.GraphicObject[] goArray = 
				org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(modelAVM).asArray();
		
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = (GraphicObject) goArray[i].castTo(GraphicObject.class);
			gos.add(startNode);
		}
		
		return gos;
	}

	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	public String generateJSONforD3(){
		
		Set<GraphicObject> goSet = AVMUtils.getRelevantGraphicObjects(modelAVM);
		GraphicObject[] goArray = new GraphicObject[goSet.size()];
		Map<GraphicObject, Integer> goMap = new HashMap<GraphicObject, Integer>(50);	
		
		// we need an array, not a set below ...
		int j = 0;
		for (Iterator<GraphicObject> iterator = goSet.iterator(); iterator.hasNext();) {
			GraphicObject graphicObject = (GraphicObject) iterator.next();
			goArray[j] = graphicObject;
			j++;
		}
				
		// save GOs into a map to allow for looking up the index
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = goArray[i];
			goMap.put(startNode,i);
		}
		
		JSONObject d3data=new JSONObject();
		List listOfNodes = new LinkedList();
		List listOfLinks = new LinkedList();
		
		// generate JSON node entries
		for (int i = 0; i < goArray.length; i++) {
			
			GraphicObject startNode = goArray[i];
			
			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			startNode = startNode.tryReplaceWithCashedInstanceForSameURI(startNode);
			
			//color
			String startNodeColorRGBHex = startNode.getColorHex();
			// shape
			String startNodeShapeD3Name = startNode.getShape();
			
			Map node = new LinkedHashMap();
			node.put("uri", startNode.getRepresentedResource().toString());
			node.put("label", D3Utils.shortenLabel(startNode.getLabel()));
			node.put("full_label", startNode.getLabel());
			node.put("color_rgb_hex", startNodeColorRGBHex);
			node.put("color_hsl_lightness", startNode.getColorHSLLightness());
			node.put("color_rgb_hex_combined", startNode.getColorRGBHexCombinedWithHSLValues());
			//node.put("width", 15);
			//node.put("width", startNode.getColorHSLLightness()+5);
			//node.put("heigth", startNode.getColorHSLLightness()+5);
			node.put("shape_d3_name", startNodeShapeD3Name);
			node.put("label_shape_d3_name", startNodeShapeD3Name); /* temp, should be label.shape_d3_name*/
			
			// temp label positioning using the attachedBy information
			if (startNode.hasLabeledwith()){
				
				Labeling nAryLabeling = startNode.getAllLabeledwith_as().firstValue(); // TODO only one label handled!
				
				GraphicObjectToObjectRelation attachementRelation = nAryLabeling.getAllLabelingattachedBy_as().firstValue();
				
				if (attachementRelation.asURI().equals(Containment.RDFS_CLASS)) {
					node.put("label_position", "centerCenter"); /* temp, should be label.shape_d3_name*/	
					node.put("width", 30);
				} else if (attachementRelation.asURI().equals(Superimposition.RDFS_CLASS)) {
					node.put("label_position", "centerRight"); /* temp, should be label.shape_d3_name*/	
					node.put("width", 30);
				}
					
				// ... other positions ...
				
			} else {
				
				// default label positioning
				node.put("label_position", "topLeft"); /* temp, should be label.shape_d3_name*/	
				node.put("width", 15);
			}
			
			listOfNodes.add(node);
		}
		d3data.put("nodes", listOfNodes);

		
		/* this does not seem to work properly, since inverse relation linkedTo is not calculated correctly 
		
		// generate JSON link entries iterating startNodes 
		for (int i = 0; i < goArray.length; i++) {
			GraphicObject startNode = goArray[i];
			try {
				ClosableIterator<? extends DirectedLinking> dlRelIt =
						startNode.getAllLinkedto_as().asClosableIterator();
				while (dlRelIt.hasNext()) {
					DirectedLinking dlRel = (DirectedLinking) dlRelIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
					LOGGER.info("Generating JSON link for " + dlRel);
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
		
		*/
		
		
		// directed linking: generate JSON link entries directly by iterating all DirectedLinking instances, not via startNodes 
		try {
			ClosableIterator<? extends DirectedLinking> dlRelIt =
					DirectedLinking.getAllInstances_as(modelAVM).asClosableIterator();
			while (dlRelIt.hasNext()) {
				DirectedLinking dlRel = (DirectedLinking) dlRelIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
				GraphicObject startNode = (GraphicObject) dlRel.getAllStartnode_as().firstValue().castTo(GraphicObject.class);
				GraphicObject endNode = (GraphicObject) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
				GraphicObject connector = (GraphicObject) dlRel.getAllLinkingconnector_as().firstValue().castTo(GraphicObject.class);
				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				link.put("type", "Directed");
				link.put("arrow_type", connector.getShape());
				//link.put("type", dlRel.getRDFSClassURI().toString());
				link.put("source", goMap.get(startNode));
				link.put("target", goMap.get(endNode));
				link.put("value", "1");
				link.put("label", D3Utils.shortenLabel(connector.getLabel()));
				link.put("full_label", connector.getLabel());
				link.put("color_hsl_lightness", connector.getColorHSLLightness());
				//link.put("color_rgb_hex", connector.getColorHex());
				link.put("color_rgb_hex_combined", connector.getColorRGBHexCombinedWithHSLValues());
				link.put("shape_d3_name", connector.getShape()); // TODO simplification. should use extra label GO
				link.put("width", connector.getWidth());
				listOfLinks.add(link);
				LOGGER.finer("Generated JSON link for " + dlRel + " (" + startNode.getLabel() + " --> " + endNode.getLabel() +")" );
				}
		} catch (Exception e) {
			LOGGER.warning("No JSON links could be generated. " + e.getMessage());
			e.printStackTrace();
		}		
		
		// undirected linking
		try {
			ClosableIterator<? extends UndirectedLinking> relIt =
					UndirectedLinking.getAllInstances_as(modelAVM).asClosableIterator();
			while (relIt.hasNext()) {
				UndirectedLinking rel = (UndirectedLinking) relIt.next().castTo(UndirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
				
				List<Thing1> nodes = rel.getAllLinkingnode_as().asList();
				
				GraphicObject node1 = null;
				GraphicObject node2 = null;
				
				if(nodes.size() == 2) {
				
					node1 = (GraphicObject) nodes.get(0).castTo(GraphicObject.class);
					node2 = (GraphicObject) nodes.get(1).castTo(GraphicObject.class);
					
				} else {
					LOGGER.warning("Undirected Linkings with a number of nodes unequal 2 are not supported");
					continue;
				}
				
				GraphicObject connector = (GraphicObject) rel.getAllLinkingconnector_as().firstValue().castTo(GraphicObject.class);
				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Undirected");
				link.put("source", goMap.get(node1));
				link.put("target", goMap.get(node2));
				link.put("value", "1");
				link.put("label", connector.getLabel());
				//link.put("color_rgb_hex", connector.getColorHex());
				link.put("color_rgb_hex_combined", connector.getColorRGBHexCombinedWithHSLValues());
				listOfLinks.add(link);
				LOGGER.finer("Generated JSON link for " + rel + " (" + node1.getLabel() + " --> " + node2.getLabel() +")" );
				}
		} catch (Exception e) {
			LOGGER.warning("No JSON links could be generated. " + e.getMessage());
			e.printStackTrace();
		}	
		
		
		// containment
		try {
			
			ClosableIterator<? extends Containment> relIt =
					Containment.getAllInstances_as(modelAVM).asClosableIterator();
			
			while (relIt.hasNext()) {
				
				Containment rel = (Containment) relIt.next().castTo(Containment.class); 
				
				GraphicObject container = (GraphicObject) rel.getAllContainmentcontainer_as().firstValue().castTo(GraphicObject.class);
				GraphicObject containee = (GraphicObject) rel.getAllContainmentcontainee_as().firstValue().castTo(GraphicObject.class);

				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Containment");
				link.put("source", goMap.get(container));
				link.put("target", goMap.get(containee));
				link.put("value", "1");
				link.put("label", "contains");
				link.put("color_rgb_hex", "#ccc");
				//link.put("color_rgb_hex_combined", "#ccc");
				listOfLinks.add(link);
				LOGGER.finer("Generated JSON link for " + rel + " (" + container.getLabel() + " contains " + containee.getLabel() +")" );
				}
		} catch (Exception e) {
			LOGGER.warning("No JSON links could be generated. " + e.getMessage());
			e.printStackTrace();
		}	
		
		d3data.put("links", listOfLinks);
		
		return d3data.toJSONString();
	}

	@Override
	public String getGenJSONFileName() {
		return "graph-data.json";
	}
	
	public String getDefaultD3GraphicFile(){
		return "force-directed-graph/index.html";
	}
	
}
