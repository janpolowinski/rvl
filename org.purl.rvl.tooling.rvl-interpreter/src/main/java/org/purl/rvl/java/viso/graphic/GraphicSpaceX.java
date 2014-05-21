package org.purl.rvl.java.viso.graphic;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Jan Polowinski
 *
 */
public class GraphicSpaceX {
	
	private Set<GraphicObjectX> gos;

	/**
	 * @param gos
	 */
	public GraphicSpaceX(Set<GraphicObjectX> gos) {
		super();
		this.gos = gos;
	}

	/**
	 * 
	 */
	public GraphicSpaceX() {
		super();
		this.gos = new HashSet<GraphicObjectX>();
	}

	public void addGraphicObject(GraphicObjectX graphicObject) {
		gos.add(graphicObject);
	}

	public void addGraphicObjects(Set<GraphicObjectX> goSet) {
		this.gos.addAll(goSet);
	}

	/**
	 * @return the gos
	 */
	public Set<GraphicObjectX> getGos() {
		return gos;
	}

	/**
	 * @param gos the gos to set
	 */
	public void setGos(Set<GraphicObjectX> gos) {
		this.gos = gos;
	}

}
