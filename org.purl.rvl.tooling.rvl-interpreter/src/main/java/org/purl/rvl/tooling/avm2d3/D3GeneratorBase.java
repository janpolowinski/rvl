package org.purl.rvl.tooling.avm2d3;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.java.VISOGRAPHIC;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;


/**
 * @author Jan Polowinski
 *
 */
public abstract class D3GeneratorBase implements D3Generator {
	
	protected Model modelAVM;
	protected Model modelVISO;
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName());

	static final String NL = System.getProperty("line.separator");

	protected static final float DEFAULT_WITH_NODES = 20;
	protected static final float DEFAULT_WITH_CONNECTORS = 7;

	protected static final float LABEL_ICON_SIZE_FACTOR = (float) 0.75; 
	
	
	public D3GeneratorBase() {
		super();
	}

	
	/**
	 * @param modelAVM
	 * @param modelVISO
	 */
	public D3GeneratorBase(Model model, Model modelVISO) {
		super();
		this.modelAVM = model;
		this.modelVISO = modelVISO;
	}




	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#writeJSONToFile(java.lang.String)
	 */
	public void writeJSONToFile(String fileContent){
		try {
			String fileName = OGVICProcess.getInstance().getJsonFileNameRel();
			
			FileWriter writer = new FileWriter(fileName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			LOGGER.info("JSON written to " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#init(org.ontoware.rdf2go.model.Model)
	 */
	public void init(Model modelAVM) {
		this.modelAVM = modelAVM;
	}
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#generateJSONforD3()
	 */
	public abstract String generateJSONforD3();
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#getGenJSONFileName()
	 */
	public abstract String getGenJSONFileName();
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#getDefaultD3GraphicFile()
	 */
	public abstract String getDefaultD3GraphicFile();


	/**
	 * @param graphicObject
	 * @param endNodeColorRGBHex
	 * @param endNodeShapeD3Name
	 * @param map
	 */
	protected void putGraphicAttributes(Map map, GraphicObjectX graphicObject) {
		
		//color
		String endNodeColorRGBHex = graphicObject.getColorHex();
		
		map.put("color_rgb_hex", endNodeColorRGBHex);
		map.put("color_rgb_hex_combined", graphicObject.getColorRGBHexCombinedWithHSLValues());
		
		// shape
		String endNodeShapeD3Name = graphicObject.getShape();
	
		map.put("shape_d3_name", endNodeShapeD3Name);
		
		// dimensions
		final float width;
		if (graphicObject.hasWidth()) {
			width = graphicObject.getWidth();
		} else {
			width = graphicObject.hasRole(VISOGRAPHIC.ROLE_LINKING_CONNECTOR)? getDefaultWidthConnectors() : getDefaultWidthNodes();
		}

		map.put("width", width);
	}
	
	protected float getDefaultWidthNodes(){
		return DEFAULT_WITH_NODES;
	}
	
	protected float getDefaultWidthConnectors(){
		return DEFAULT_WITH_CONNECTORS;
	}
}