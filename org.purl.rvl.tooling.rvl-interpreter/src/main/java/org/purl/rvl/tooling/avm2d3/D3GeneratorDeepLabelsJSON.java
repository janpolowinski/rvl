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
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorDeepLabelsJSON extends D3GeneratorBase {
	
	
	final static Logger LOGGER = Logger.getLogger(D3GeneratorDeepLabelsJSON.class .getName()); 
	
	public D3GeneratorDeepLabelsJSON() {
		super();
	}
	
	/**
	 * @param modelAVM
	 * @param modelVISO
	 */
	public D3GeneratorDeepLabelsJSON(Model model, Model modelVISO) {
		super(model, modelVISO);
	}
	

	/**
	 * Generates JSON using SimpleJSON (Jackson JSON-Binding-Version also exists)
	 */
	public String generateJSONforD3(){
		
		List<GraphicObjectX> goList = AVMUtils.getRelevantGraphicObjects(modelAVM);
		GraphicObjectX[] goArray = new GraphicObjectX[goList.size()];
		Map<GraphicObjectX, Integer> goMap = new HashMap<GraphicObjectX, Integer>(50);	
		
		// we need an array, not a set below ...
		int j = 0;
		for (Iterator<GraphicObjectX> iterator = goList.iterator(); iterator.hasNext();) {
			GraphicObjectX graphicObject = iterator.next();
			goArray[j] = graphicObject;
			j++;
		}
				
		// save GOs into a map to allow for looking up the index
		for (int i = 0; i < goArray.length; i++) {
			GraphicObjectX startNode = goArray[i];
			goMap.put(startNode,i);
		}
		
		JSONObject d3data = new JSONObject();
		List<Map<String,Object>> listOfNodes = new LinkedList<Map<String,Object>>();
		List<Map<String,Object>> listOfLinks = new LinkedList<Map<String,Object>>();
		
		// generate JSON node entries
		for (int i = 0; i < goArray.length; i++) {
			
			GraphicObjectX startNode = goArray[i];
			
			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			startNode = RVLUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(startNode, GraphicObjectX.class);
			
			// width (used for calculating label size)
			float startNodeWidth = startNode.hasWidth()? startNode.getWidth() : getDefaultWidthNodes();
			
			Map<String,Object> node = new LinkedHashMap<String,Object>();
			//putGraphicAttributes(node, startNode);
			//node.put("uri", startNode.getRepresentedResource().toString());
			//putLabels(startNode, startNodeWidth, node);
			putAttributesLabelsRepresentedResource(startNode, node);
			
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
				
				Map<String,Object> link = new LinkedHashMap<String,Object>();
				
				putGraphicAttributes(link,connector);
				putLabels(connector, getDefaultWidthConnectors(), link);
				putRepresentedResource(link, connector);
				
				link.put("type", "Directed");
				//link.put("type", dlRel.getRDFSClassURI().toString());
				link.put("source", goMap.get(startNode));
				link.put("target", goMap.get(endNode));
				link.put("value", "1");
				//link.put("text_value", D3Utils.shortenLabel(connector.getLabel()));
				//link.put("text_value_full", connector.getLabel() + " (ID: " + connector.getRepresentedResource() + ")");
				
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
				Map<String,Object> link = new LinkedHashMap<String,Object>();

				putGraphicAttributes(link,connector);
				putLabels(connector, getDefaultWidthConnectors(), link);
				putRepresentedResource(link, connector);
				
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Undirected");
				link.put("source", goMap.get(node1));
				link.put("target", goMap.get(node2));
				link.put("value", "1");
				link.put("text_value", connector.getLabel());
				
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
				
				Map<String,Object> link = new LinkedHashMap<String,Object>();
				
				putAttributesLabelsRepresentedResource(containee,link);
				
				//link.put("type", rel.getRDFSClassURI().toString());
				link.put("type", "Containment");
				link.put("source", goMap.get(container));
				link.put("target", goMap.get(containee));
				link.put("value", "1");
				link.put("text_value", "contains");

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
