package org.purl.rvl.tooling.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.sail.rdbms.managers.UriManager;
import org.purl.rvl.java.exception.IncompleteColorValuesException;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.viso.graphic.Color;
import org.purl.rvl.java.viso.graphic.GraphicObject;

public class AVMUtils {
	
	private final static Logger LOGGER = Logger.getLogger(AVMUtils.class.getName()); 
	
	public static void listAllColors(Model model) {
		System.out.println("List of all colors in the model:");
		System.out.println();
		
		ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.Color> goIt = 
				org.purl.rvl.java.gen.viso.graphic.Color.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			Color color = (Color) goIt.next().castTo(Color.class);
			LOGGER.info(color.toString());
		}	
	}
	
	/**
	 * List all Graphic Objects in the model
	 */
	static void listAllGOs(Model model) {	
		
		System.out.println("List of all Graphic Objects in the model:");
		System.out.println();
		
		ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> goIt = 
				org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			GraphicObject go = (GraphicObject) goIt.next().castTo(GraphicObject.class);
			System.out.println(go);
		}		
	}
	
	/**
	 * List all Resources in the model
	 */
	static void listAllResourcesWithTheirTypes(Model model){	
		
		System.out.println("List of all rdfreactor ... Resources in the model:");
		System.out.println();
		
		ClosableIterator<? extends Resource> resIt = 
			Resource.getAllInstance_as(model).asClosableIterator();
		while (resIt.hasNext()) {
			Resource res = (Resource) resIt.next();
	
			LOGGER.info(res.toString());
			//LOGGER.info("Types:" + go.getAllType_as().asArray()[0].asURI());
			
			for (org.ontoware.rdfreactor.schema.rdfs.Class type : res.getAllType_as().asList()) {
				try {
					LOGGER.info("T: " + type.asURI());
				} catch (ClassCastException e) {
					LOGGER.severe("evtl. blanknote");
				}
			}
		}		
	}
	
	/**
	 * Get only the GraphicObjects that need to be displayed. Remove objects
	 * playing the role of connectors for example.
	 * 
	 * @return
	 */
	public static Set<GraphicObject> getRelevantGraphicObjects(Model model) {

		Set<GraphicObject> gos = new HashSet<GraphicObject>();

		// get all subjects and the sv/tv table via SPARQL
		String query = "" + 
				"SELECT DISTINCT ?go " + 
				"WHERE { " +
				"	?go a " + GraphicObject.RDFS_CLASS.toSPARQL() + " ." +
//				"	?someRelation " + DirectedLinking.STARTNODE.toSPARQL() + " ?go ." +
				"	FILTER NOT EXISTS { ?someRelation " + DirectedLinking.LINKINGCONNECTOR .toSPARQL() + " ?go . }" +
				"} ";
		LOGGER.finest("query for relevant GOs: " + query);

		QueryResultTable explMapResults = model.sparqlSelect(query);
		for (QueryRow row : explMapResults) {
			GraphicObject go = (GraphicObject) GraphicObject.getInstance(model, row
					.getValue("go").asURI()).castTo(GraphicObject.class);
			gos.add(go);
			LOGGER.finest("Found relevant GO: " + row.getValue("go").toString() + " (" + go.getLabel() + ")");
		}

		return gos;
	}

	/**
	 * Returns a root node, i.e without incoming link, but with at least one outgoing link
	 * 
	 * @param model
	 * @return
	 */
	public static GraphicObject getRootNodeGraphicObject(Model model) {
				
		Set<GraphicObject> rootNodes = getRootNodesGraphicObject(model);
		GraphicObject pickedRootNode = null;
		
		if (rootNodes.size()>1) {
			LOGGER.info("multiple root node found. will return one of them");
		}

		for (Iterator<GraphicObject> iterator = rootNodes.iterator(); iterator.hasNext();) {
			pickedRootNode = (GraphicObject) iterator.next();
			break; // TODO: select best - not first
		}
		
		return pickedRootNode;
	}
	
	public static Set<GraphicObject> getRootNodesGraphicObject(Model model) {
		
		if (null==model) {
			LOGGER.warning("Model was null. Couldn't get root nodes.");
			return null;
		}
		
		Set<GraphicObject> rootNodes = new HashSet<GraphicObject>();

		String query = "" + 
				"SELECT DISTINCT ?go " + 
				"WHERE { " +
				"	?go a " + GraphicObject.RDFS_CLASS.toSPARQL() + " ." +
				"	?someRelation " + DirectedLinking.STARTNODE .toSPARQL() + " ?go ." + 
						// (some relation points to the go as a startNode)
				"	FILTER NOT EXISTS { ?someOtherRelation " + DirectedLinking.ENDNODE .toSPARQL() + " ?go . }" + 
						// (no relation points to the go as an endNode)
				"} ";
		LOGGER.finest("query for root nodes: " + query);

		QueryResultTable rootNodeResults = model.sparqlSelect(query);
		for (QueryRow row : rootNodeResults) {
			
			GraphicObject possibleRootNode = (GraphicObject) GraphicObject.getInstance(model, row
					.getValue("go").asURI()).castTo(GraphicObject.class);
			
			LOGGER.finer("Found possible root node " + possibleRootNode.asURI() + " (" + possibleRootNode.getLabel() +")");
			rootNodes.add(possibleRootNode);
		}

		return rootNodes;
	}


	public static Set<DirectedLinking> getDirectedLinkingRelationsFrom(
			Model model, GraphicObject parentGO) {
		
		Set<DirectedLinking> dlFromHere = new HashSet<DirectedLinking>();

		String query = "" + 
				"SELECT DISTINCT ?dl " + 
				"WHERE { " +
				"	?dl a " + DirectedLinking.RDFS_CLASS.toSPARQL() + " ." +
				"	?dl " + DirectedLinking.STARTNODE .toSPARQL() + parentGO.toSPARQL() + " ." + 
						// (some relation points to the go as a startNode)
				"} ";
		
		String parentGOLabel = "";
		try{parentGOLabel = parentGO.getLabel() ; } catch (Exception e) {}
		LOGGER.finer("Query for directed linking relations with start node " + parentGOLabel + " " + parentGO.asURI());
		LOGGER.finest("Query: " + query);

		QueryResultTable results = model.sparqlSelect(query);
		for (QueryRow row : results) {
			dlFromHere.add(DirectedLinking.getInstance(model, row
					.getValue("dl").asURI()));
		}
		
		return dlFromHere;
	}
	
	
	public static String getOrGenerateDefaultLabelString(Model model, org.ontoware.rdf2go.model.node.Resource resource){
		
		String genLabel = getGoodLabel(resource,model);

		return genLabel;
	}

	public static String getLocalName(Node node, Model model){
		
		String localName = "local-name-could-not-be-fetched";
		
		try {	 
			localName =  RDFTool.getShortName(node.asURI().toString());
		}
		catch (Exception e) {
			LOGGER.finest("Local name could not be fetched. Blank node?");
			localName = node.toString();
		}

		return localName;
	}
	
	public static String getGoodLabel(Node node, Model model){
		
		
		String label = "no-good-label-could-be-calculated";
		
		try {	 
			// somehow causes runtime exception with jena when not casted to resource as below
			label =  RDFTool.getGoodLabel(node.asResource(), model);
		}
		catch (Exception e) {
			
			LOGGER.finest("No good label could be calculated.");
			
			try {

				label = getLocalName(node, model);
				
			} catch (Exception e1) {
				
				LOGGER.finest("Local name could not be calculated.");
				label = node.toString();
			}
		}

		return label;
	}




}
