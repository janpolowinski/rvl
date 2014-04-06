package org.purl.rvl.tooling.rvl2avm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.GraphicObject;
import org.purl.rvl.tooling.process.OGVICProcess;

public class FakeRVLInterpreter extends RVLInterpreterBase {
		
	private final static Logger LOGGER = Logger.getLogger(FakeRVLInterpreter.class .getName()); 
	
	private Model modelVISO;
	
	/**
	 * @param model
	 * @param modelVISO
	 */
	public FakeRVLInterpreter() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreterBase#interpretMappings()
	 */
	@Override
	protected void interpretMappingsInternal() {
		LOGGER.info("Using fake interpreter: Will create some example AVM instead of interpreting RVL.");
		createTestGraphicObjects();
		createTestLinkingDirectedRelations();
		createTestContainmentRelations();
	}

	/**
	 * Creates example (n-ary) linking relations between the available GOs in the model
	 */
	private void createTestLinkingDirectedRelations() {
				
		List<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> startNodeList = 
				org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		List<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> endNodeList = 
				org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
		
		Iterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> startNodeIt = startNodeList.iterator();
		Iterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> endNodeIt = endNodeList.iterator();
		LOGGER.info("Creating Linking-relations. The last GO (" + endNodeIt.next() + ") will not be linked to another GO.");
		
		while (startNodeIt.hasNext() &&  endNodeIt.hasNext()) {
			GraphicObject startNode = (GraphicObject) startNodeIt.next().castTo(GraphicObject.class);
			GraphicObject endNode = (GraphicObject) endNodeIt.next().castTo(GraphicObject.class);
			DirectedLinking dlRel = new DirectedLinking(model, true);
			
			// add exemplary colors
			org.purl.rvl.java.viso.graphic.Color newColor = 
					//new org.purl.rvl.java.viso.graphic.Color(0,0,255, model, true);
					new org.purl.rvl.java.viso.graphic.Color(model, "http://purl.org/viso/graphic/Red", true);
			newColor.setColorRGB("#FF00FF");
			Color red = Color.getInstance(model, new URIImpl("http://purl.org/viso/graphic/Red"));
			//LOGGER.info("color label: " + red.getAllLabel_as().firstValue());
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
	
	private Set<org.purl.rvl.java.viso.graphic.GraphicObject> createTestGraphicObjects() {
		Random random = new Random();
		float positionX = 0;
		HashSet<org.purl.rvl.java.viso.graphic.GraphicObject> goSet = new HashSet<org.purl.rvl.java.viso.graphic.GraphicObject>();

		// get all available colors (TODO: this only works with an extra VISO-Model! -> otherwise concurrent modification exception ...)
		ClosableIterator<?extends Color> colorIt = Color.getAllInstances_as(modelVISO).asClosableIterator();
		
		for (int i = 0; i < 15; i++) {
			int aID = random.nextInt(100000);
			GraphicObject go = new GraphicObject(model,
					"http://purl.org/rvl/example-avm/GO_" + aID, true);
			go.setLabel("GO " + i);
			if (colorIt.hasNext()) {
				Color color = colorIt.next();
				LOGGER.finer("Created GO with color " + color.toString());
				go.setColornamed(color);

			}
			positionX = i * 50;
			go.setXposition(positionX);
			go.setHeight(positionX);
			goSet.add((org.purl.rvl.java.viso.graphic.GraphicObject) go
					.castTo(org.purl.rvl.java.viso.graphic.GraphicObject.class));
		}
	   
	   return goSet;
	}
	
	/**
	 * Creates example (old binary) containment relations between the available GOs in the model
	 */
	private void createTestContainmentRelations() {
			//Model tmpModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
			//tmpModel.open();
			//tmpModel.addAll(model.iterator()); tmpModel.addAll(modelVISO.iterator()); // causes all instances to be iterated below!!!! not only GOs!!!
	
			
			//ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> goIt = org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
			//ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containedIt = org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
	
	//		while (goIt.hasNext() && containedIt.hasNext()) {
	//			GraphicObject container = (GraphicObject) goIt.next().castTo(GraphicObject.class);
	//			GraphicObject containee = (GraphicObject) containedIt.next().castTo(GraphicObject.class);
	//			if(containedIt.hasNext()) container.setContains(containee);
	//			LOGGER.info(container);
	//		}
			
			// TODO: refactor to use closable iterator directly
			List<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containerList = org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
			List<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containeeList = org.purl.rvl.java.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asList();
			
			Iterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containerIt = containerList.iterator();
			Iterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containeeIt = containeeList.iterator();
			LOGGER.info("The first GO (" + containerIt.next() + ") will not be contained by another GO.");
			
			while (containerIt.hasNext() &&  containeeIt.hasNext()) {
				GraphicObject container = (GraphicObject) containerIt.next().castTo(GraphicObject.class);
				GraphicObject containee = (GraphicObject) containeeIt.next().castTo(GraphicObject.class);
				container.addContains(containee);
				//LOGGER.info(containee);
			}
		}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreterBase#init(org.ontoware.rdf2go.model.Model)
	 */
	@Override
	public void init(Model model, Model modelAVM, ModelSet modelSet) {
		// TODO Auto-generated method stub
		super.init(model,modelAVM, modelSet);
		this.modelVISO = OGVICProcess.getInstance().getModelVISO();
	}


}
