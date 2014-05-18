package org.purl.rvl.java.viso.graphic;

import java.util.Set;
import java.util.HashSet;

public class GraphicSpace {
	
	private Set<GraphicObject> gos;

	/**
	 * @param gos
	 */
	public GraphicSpace(Set<GraphicObject> gos) {
		super();
		this.gos = gos;
	}

	/**
	 * 
	 */
	public GraphicSpace() {
		super();
		this.gos = new HashSet<GraphicObject>();
	}

	public void addGraphicObject(GraphicObject graphicObject) {
		gos.add(graphicObject);
	}

	public void addGraphicObjects(Set<GraphicObject> goSet) {
		this.gos.addAll(goSet);
	}

	/**
	 * @return the gos
	 */
	public Set<GraphicObject> getGos() {
		return gos;
	}

	/**
	 * @param gos the gos to set
	 */
	public void setGos(Set<GraphicObject> gos) {
		this.gos = gos;
	}

}
