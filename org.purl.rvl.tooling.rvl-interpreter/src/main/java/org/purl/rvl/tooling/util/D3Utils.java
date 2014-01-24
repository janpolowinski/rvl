package org.purl.rvl.tooling.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.sail.rdbms.managers.UriManager;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.viso.graphic.Color;
import org.purl.rvl.java.viso.graphic.GraphicObject;

public class D3Utils {
	
	private final static Logger LOGGER = Logger.getLogger(D3Utils.class.getName());
	
	public static int MAX_LABEL_LENGTH = 15;

	public static Object shortenLabel(String label) {
		return StringUtils.substring(label, 0, MAX_LABEL_LENGTH) + "...";
	}
	
	


}
