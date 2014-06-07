package org.purl.rvl.tooling.query;

import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.purl.rvl.tooling.process.OGVICProcess;

/**
 * @author Jan Polowinski
 *
 */
public abstract class SPARQLQueryBuilder {
	
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(SPARQLQueryBuilder.class.getName()); 
	
	protected SPARQLStringBuilder query;
	
	protected URI graphURI;

	public void addCommonPrefixes(){
		
		addPrefix("rdfs", RDFS.RDFS_NS);
		addPrefix("owl", "http://www.w3.org/2002/07/owl#");
		
	}
	
	protected void addPrefixesFromDataModel() {

		// getting the namespace from the model set does not seem to work (at least not when jena impl used)
		//ModelSet modelSet = OGVICProcess.getInstance().getModelSet();
		//Set<Entry<String, String>> namespacesfromDataModel = modelSet.getModel(OGVICProcess.GRAPH_DATA).getNamespaces().entrySet();
		
		// instead we get them from the data model directly
		Set<Entry<String, String>> namespacesfromDataModel = OGVICProcess.getInstance().getModelData().getNamespaces().entrySet();
		
		for (Entry<String, String> entry : namespacesfromDataModel) {
			addPrefix(entry.getKey(), entry.getValue());
		}

	}
	
	public void addPrefix(String prefix, URI namespace){
		query.append("PREFIX "+ prefix + ": " + namespace.toSPARQL());
	}
	
	public void addPrefix(String prefix, String namespace){
		query.append("PREFIX "+ prefix + ": <" + namespace + ">");
	}
	
	public abstract void startQuerySPARQL();
	
	
	protected void constrainToGraphSPARQL(URI graphURI){
		query.append(" GRAPH " + graphURI.toSPARQL() + " { ");
	}
	
	protected void spoVarTripleSPARQL(){
		query.append(" ?s ?p ?o . ");
	}
	
	private void closeSwiftBracketSPARQL(){
		query.append(" } ");
	}

	protected void closeGraphSPARQL(){
		query.append(" } ");
	}
	
	protected void endQuerySPARQL(){
		closeSwiftBracketSPARQL();
	}
	
	protected void limitSPARQL(){
		query.append(" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ");
	}

	abstract public String buildQuery();

	public void constrainToGraph(URI graphURI){
		this.graphURI = graphURI;
	}

	public class SPARQLStringBuilder {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		public SPARQLStringBuilder append(String sparqlLine){
			stringBuilder.append(sparqlLine).append(System.getProperty("line.separator"));
			return this;
		}
		
		public String toString() {
			return stringBuilder.toString();
		}
		
	}


}
