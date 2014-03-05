package org.purl.rvl.java.viso.graphic;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.exception.IncompleteColorValuesException;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.ColorUtils;

import javax.swing.SwingUtilities;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
public class GraphicObject extends
		org.purl.rvl.java.gen.viso.graphic.GraphicObject {
	
	private static final long serialVersionUID = -9112269162175431905L;
	
	private final static Logger LOGGER = Logger.getLogger(GraphicObject.class.getName()); 
	static final String NL =  System.getProperty("line.separator");
	
	Resource representedResource;

	public GraphicObject(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, Resource instanceIdentifier, boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public GraphicObject(Model model, boolean write) {
		super(model, write);
		this.setColornamed(Color.getDefaultColor(OGVICProcess.getInstance().getModelVISO()));
	}

	

	public String toStringDetailed() {
		String s = "########################################" + NL;
		s += super.toString() + NL;
		s += "Label: " + this.getAllLabel_as().firstValue() + NL;
		if(hasColornamed())
			s += this.getAllColornamed_as().firstValue().castTo(Color.class);
		// containment (binary)
		try {
			ClosableIterator<? extends org.purl.rvl.java.gen.viso.graphic.GraphicObject> containeeIt =
					getAllContains_as().asClosableIterator();
			while (containeeIt.hasNext()) {
				GraphicObject containee = (GraphicObject) containeeIt.next().castTo(GraphicObject.class);
				s += "---contains--->" + containee.getAllLabel_as().firstValue() + NL;
			}	
		} catch (Exception e) {
			System.err.println("no containee or no containee label?");
		}
		// linking (n-ary)
		try {
			ClosableIterator<? extends DirectedLinking> nodeIt =
					getAllLinkedto_as().asClosableIterator();
			while (nodeIt.hasNext()) {
				DirectedLinking dlRel = (DirectedLinking) nodeIt.next().castTo(DirectedLinking.class); // TODO wieso liess sich GO zu DLRel casten???
				GraphicObject endNode = (GraphicObject) dlRel.getAllEndnode_as().firstValue().castTo(GraphicObject.class);
				
				s += "---linked-to--->" + endNode.asURI() + NL;
			}	
		} catch (Exception e) {
			System.err.println("sth wrong with printing the linking relation");
		}
		
		return s;
	}
	
	//@XmlElement(name="label")
	public String getLabel() {
		
		return  AVMUtils.getGoodLabel(this.asURI(),model); // sending a GraphicObject instead of a URI, causes class cast problems
				//this.getAllLabel_as().firstValue().toString();
		
	}
	
	//@XmlElement(name="color_rgb_hex")
	public String getColorHex() {
		String colorHex = "";

		if(this.hasColornamed()) {
			Color colorNamed = getColorNamed();
			
			try {
				colorHex = colorNamed.toHexString();
			} catch (Exception e) {
				LOGGER.finest("Couldn't get color value(s) for " + colorNamed.asURI()  + " (" +  e.getMessage() + ") . Default will be used.");
			}
		}
		
		if (colorHex.equals("")) {
			colorHex = org.purl.rvl.java.viso.graphic.Color.getDefaultColorHex();
		}
		return colorHex;
	}
	
	//@XmlElement(name="shape_d3_name")
	public String getShape() {
		String shapeD3Name = "";
		
		if(this.hasShapenamed()) {
			ShapeX shape = (ShapeX) this.getAllShapenamed_as().firstValue().castTo(ShapeX.class);
			try {
				shapeD3Name = shape.toD3Name();
			}
			catch (Exception e) {
				LOGGER.finest("Couldn't get shape value for " + shape.asURI()  + " (incomplete?). Default will be used.");
			}
		}
		if (shapeD3Name.equals("")) {
			shapeD3Name = ShapeX.getDefaultD3Name();
		}
		return shapeD3Name;
	}

	public void setRepresents(org.ontoware.rdf2go.model.node.Resource resource) {
		this.representedResource = resource;
	}
	
	private Resource getRepresentedResource() {
		return representedResource;
	}

	public Color getColorNamed() {
		if (hasColornamed()) {
			
			// TODO: problem: when AVM does not include VISO, a GO cannot calculate its color values. merge models? or enrich for ease case?
			Color colorInCurrentModel = (Color)this.getAllColornamed_as().firstValue().castTo(Color.class);
			
			// "enrich" the color object by data from the VISO model
			Model modelVISO = OGVICProcess.getInstance().getModelVISO();
			Color colorInVISO = (Color)Color.getInstance(modelVISO, colorInCurrentModel.asURI()).castTo(Color.class);
			
			// get cached named color
			colorInVISO = colorInVISO.tryReplaceWithCashedInstanceForSameURI(colorInVISO);

			return colorInVISO;
		}
		else return null;
	}

	public float getColorHSLLightness() {
		
		float defaultLightnessValue = 50;
		
		if(this.hasColorhsllightness()) {
			Float lightness = this.getAllColorhsllightness_as().firstValue();
			LOGGER.finest(" Found HSL lightness value of " + lightness);
			return lightness;
		}
		else {
			LOGGER.finest("Couldn't get HSL lightness value. Default ("+ defaultLightnessValue +") will be used.");
			return defaultLightnessValue;
		}
	}

	public String getColorRGBHexCombinedWithHSLValues() {
		
		Color baseColor;
		String colorHexString = "";
		java.awt.Color awtColor = null;
		float lightnessInPercent;
		
		//LOGGER.finest("start getting combined color ");
		
		
		// get the base color from a named color (if there is one) or the default color otherwise
		if (hasColornamed()) {
			baseColor = getColorNamed();
		} else {
			baseColor = Color.getDefaultColor(OGVICProcess.getInstance().getModelVISO());
		}
		// TODO check for RGB-hex and R,G,B values directly added to the GO
	
		try {
			
			awtColor = baseColor.getColor_as_JavaAWT();
			
		} catch (IncompleteColorValuesException e) {
			LOGGER.finest("Problem getting awt color for color " + baseColor.asURI() + ": " + e.getMessage());
		}
		
		if (null!=awtColor && hasColorhsllightness()){
			
			lightnessInPercent = getColorHSLLightness();
			
			//LOGGER.finest("Applying a lightness value of " + lightnessInPercent);
			
			float[] hslFloats = new float[3];
					
			ColorUtils.rgbToHsl(awtColor.getRGB(), hslFloats) ;
			
			hslFloats[2] = lightnessInPercent/100;
			
			//LOGGER.finest("HSL values: H:" + hslFloats[0] + " S:" + hslFloats[1] + " L:" + hslFloats[2] + "");
			
			int colorAsInteger = ColorUtils.hslToRgb(hslFloats);
			
			colorHexString = Integer.toHexString(colorAsInteger & 0xffffff);
			
			//LOGGER.finest("combined color hex after int to hex: " + colorHexString);
			
			if (colorHexString.length() < 6) {
				colorHexString = "000000".substring(0, 6 - colorHexString.length()) + colorHexString;
			  }
			colorHexString = "#" + colorHexString;
			
			LOGGER.finest("combined color hex final: " + colorHexString);
			
			
		} else {
			
			try {
				
				colorHexString =  baseColor.toHexString();
				LOGGER.finest("named color hex: " + colorHexString);
				
			} catch (IncompleteColorValuesException e) {
				
				LOGGER.finest("Problem getting hex value for color " + baseColor.asURI() + ": " + e.getMessage());
			}
		}


		
		//LOGGER.finest("end getting combined color ");
		
		return colorHexString;
	}


}
