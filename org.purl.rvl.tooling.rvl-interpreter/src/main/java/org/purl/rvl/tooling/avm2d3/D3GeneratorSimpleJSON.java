/**
 * 
 */
package org.purl.rvl.tooling.avm2d3;

import java.util.HashMap;
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
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.D3Utils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorSimpleJSON extends D3GeneratorBase {
	
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorSimpleJSON.class .getName()); 
	
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
	

	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	public String generateJSONforD3(){
		
		Set<GraphicObjectX> goSet = AVMUtils.getRelevantGraphicObjects(modelAVM);
		GraphicObjectX[] goArray = new GraphicObjectX[goSet.size()];
		Map<GraphicObjectX, Integer> goMap = new HashMap<GraphicObjectX, Integer>(50);	
		
		// we need an array, not a set below ...
		int j = 0;
		for (Iterator<GraphicObjectX> iterator = goSet.iterator(); iterator.hasNext();) {
			GraphicObjectX graphicObject = (GraphicObjectX) iterator.next();
			goArray[j] = graphicObject;
			j++;
		}
				
		// save GOs into a map to allow for looking up the index
		for (int i = 0; i < goArray.length; i++) {
			GraphicObjectX startNode = goArray[i];
			goMap.put(startNode,i);
		}
		
		JSONObject d3data=new JSONObject();
		List listOfNodes = new LinkedList();
		List listOfLinks = new LinkedList();
		
		// generate JSON node entries
		for (int i = 0; i < goArray.length; i++) {
			
			GraphicObjectX startNode = goArray[i];
			
			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			startNode = startNode.tryReplaceWithCashedInstanceForSameURI(startNode);
			
			// width (used for calculating label size)
			float startNodeWidth = startNode.hasWidth()? startNode.getWidth() : getDefaultWidthNodes();
			
			Map node = new LinkedHashMap();
			putGraphicAttributes(node, startNode);
			node.put("uri", startNode.getRepresentedResource().toString());
			node.put("display_label_text", true);
			node.put("label", D3Utils.shortenLabel(startNode.getLabel()));
			node.put("full_label", startNode.getLabel() + " (ID: " + startNode.getRepresentedResource() + ")");
			
			// temp label positioning using the attachedBy information
			if (startNode.hasLabeledwith()){
				
				Labeling nAryLabeling = startNode.getAllLabeledwith_as().firstValue(); // TODO only one label handled!

				try {
					
					GraphicObjectX label = (GraphicObjectX) nAryLabeling.getAllLabelinglabel_as().firstValue().castTo(GraphicObjectX.class);
					
					GraphicObjectToObjectRelation attachementRelation = nAryLabeling.getAllLabelingattachedBy_as().firstValue();
					
					node.put("label_shape_d3_name", label.getShape());
					node.put("label_color_rgb_hex_combined", label.getColorRGBHexCombinedWithHSLValues());
					node.put("label_width", startNodeWidth*LABEL_ICON_SIZE_FACTOR);
					
					if (attachementRelation.asURI().equals(Containment.RDFS_CLASS)) {
						node.put("display_label_text", true);
						node.put("label_position", "centerCenter"); /* temp, should be label.shape_d3_name*/	
					} else if (attachementRelation.asURI().equals(Superimposition.RDFS_CLASS)) {
						node.put("display_label_icon", true);
						node.put("label_position", "centerRight"); /* temp, should be label.shape_d3_name*/	
						node.put("width", 30);
						
					}
				
				}
				catch (NullPointerException e ){
					
					LOGGER.severe("Problem getting label from labeling relation, labeling " + nAryLabeling + " will be ignored.");
				}
					
				// ... other positions ...
				
			} else {
				
				// default label positioning
				node.put("label_position", "topLeft"); /* temp, should be label.shape_d3_name*/	
				
			}
			
			listOfNodes.add(node);
		}
		d3data.put("nodes", listOfNodes);

		
		/* this does not seem to work properly, since inverse relation linkedTo is not calculated correctly 
		
		// generate JSON link entries iterating startNodes 
		for (int i = 0; i < goArray.length; i++) {
			GraphicObjectX startNode = goArray[i];
			try {
				ClosableIterator<? extends DirectedLinking> dlRelIt =
						startNode.getAllLinkedto_as().asClosableIterator();
				while (dlRelIt.hasNext()) {
					DirectedLinking dlRel = (DirectedLinking) dlRelIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
					LOGGER.info("Generating JSON link for " + dlRel);
					GraphicObjectX endNode = (GraphicObjectX) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObjectX.class);
					GraphicObjectX connector = (GraphicObjectX) dlRel.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
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
				GraphicObjectX startNode = (GraphicObjectX) dlRel.getAllStartnode_as().firstValue().castTo(GraphicObjectX.class);
				GraphicObjectX endNode = (GraphicObjectX) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObjectX.class);
				GraphicObjectX connector = (GraphicObjectX) dlRel.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				putGraphicAttributes(link,connector);
				link.put("type", "Directed");
				link.put("arrow_type", connector.getShape());
				//link.put("type", dlRel.getRDFSClassURI().toString());
				link.put("source", goMap.get(startNode));
				link.put("target", goMap.get(endNode));
				link.put("value", "1");
				link.put("label", D3Utils.shortenLabel(connector.getLabel()));
				link.put("full_label", connector.getLabel() + " (ID: " + connector.getRepresentedResource() + ")");
				
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
				
				GraphicObjectX node1 = null;
				GraphicObjectX node2 = null;
				
				if(nodes.size() == 2) {
				
					node1 = (GraphicObjectX) nodes.get(0).castTo(GraphicObjectX.class);
					node2 = (GraphicObjectX) nodes.get(1).castTo(GraphicObjectX.class);
					
				} else {
					LOGGER.warning("Undirected Linkings with a number of nodes unequal 2 are not supported");
					continue;
				}
				
				GraphicObjectX connector = (GraphicObjectX) rel.getAllLinkingconnector_as().firstValue().castTo(GraphicObjectX.class);
				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				putGraphicAttributes(link,connector);
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Undirected");
				link.put("source", goMap.get(node1));
				link.put("target", goMap.get(node2));
				link.put("value", "1");
				link.put("label", connector.getLabel());
				
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
				
				GraphicObjectX container = (GraphicObjectX) rel.getAllContainmentcontainer_as().firstValue().castTo(GraphicObjectX.class);
				GraphicObjectX containee = (GraphicObjectX) rel.getAllContainmentcontainee_as().firstValue().castTo(GraphicObjectX.class);

				// get index of the endNode in the above generated Map
				Map link = new LinkedHashMap();
				putGraphicAttributes(link,containee);
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Containment");
				link.put("source", goMap.get(container));
				link.put("target", goMap.get(containee));
				link.put("value", "1");
				link.put("label", "contains");

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
