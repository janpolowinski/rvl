/*
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 1895 2013-02-09 17:39:56Z max.at.xam.de@gmail.com $) on 27.02.14 09:19
 */
package org.purl.rvl.java.gen.rvl;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.Base;
import org.ontoware.rdfreactor.runtime.ReactorResult;

/**
 * This class manages access to these properties:
 * <ul>
 *   <li>Similarproperty</li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 27.02.14 09:19
 */
public class Unifywithinverseandpickone extends Simplification {

    private static final long serialVersionUID = 825232213842867858L;

    /** http://purl.org/rvl/UnifyWithInverseAndPickOne */
	public static final URI RDFS_CLASS = new URIImpl("http://purl.org/rvl/UnifyWithInverseAndPickOne", false);

    /** http://purl.org/rvl/similarProperty */
	public static final URI SIMILARPROPERTY = new URIImpl("http://purl.org/rvl/similarProperty", false);

    /**
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available.
     */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://purl.org/rvl/similarProperty", false)
    };


	// protected constructors needed for inheritance

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.semweb4j.org
	 * @param classURI URI of RDFS class
	 * @param instanceIdentifier Resource that identifies this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c1]
	 */
	protected Unifywithinverseandpickone (Model model, URI classURI, Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
	}

	// public constructors

	/**
	 * Returns a Java wrapper over an RDF object, identified by URI.
	 * Creating two wrappers for the same instanceURI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param instanceIdentifier an RDF2Go Resource identifying this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c2]
	 */
	public Unifywithinverseandpickone (Model model, Resource instanceIdentifier, boolean write) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}


	/**
	 * Returns a Java wrapper over an RDF object, identified by a URI, given as a String.
	 * Creating two wrappers for the same URI is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param uriString a URI given as a String
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 * @throws ModelRuntimeException if URI syntax is wrong
	 *
	 * [Generated from RDFReactor template rule #c7]
	 */
	public Unifywithinverseandpickone (Model model, String uriString, boolean write) throws ModelRuntimeException {
		super(model, RDFS_CLASS, new URIImpl(uriString,false), write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by a blank node.
	 * Creating two wrappers for the same blank node is legal.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param bnode BlankNode of this instance
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c8]
	 */
	public Unifywithinverseandpickone (Model model, BlankNode bnode, boolean write) {
		super(model, RDFS_CLASS, bnode, write);
	}

	/**
	 * Returns a Java wrapper over an RDF object, identified by
	 * a randomly generated URI.
	 * Creating two wrappers results in different URIs.
	 * @param model RDF2GO Model implementation, see http://rdf2go.ontoware.org
	 * @param write if true, the statement (this, rdf:type, TYPE) is written to the model
	 *
	 * [Generated from RDFReactor template rule #c9]
	 */
	public Unifywithinverseandpickone (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of Unifywithinverseandpickone or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0]
	 */
	public static Unifywithinverseandpickone getInstance(Model model, Resource instanceResource) {
		return Base.getInstance(model, instanceResource, Unifywithinverseandpickone.class);
	}

	/**
	 * Create a new instance of this class in the model.
	 * That is, create the statement (instanceResource, RDF.type, http://purl.org/rvl/UnifyWithInverseAndPickOne).
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class1]
	 */
	public static void createInstance(Model model, Resource instanceResource) {
		Base.createInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return true if instanceResource is an instance of this class in the model
	 *
	 * [Generated from RDFReactor template rule #class2]
	 */
	public static boolean hasInstance(Model model, Resource instanceResource) {
		return Base.hasInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as RDF resources
	 *
	 * [Generated from RDFReactor template rule #class3]
	 */
	public static ClosableIterator<Resource> getAllInstances(Model model) {
		return Base.getAllInstances(model, RDFS_CLASS, Resource.class);
	}

	/**
	 * @param model an RDF2Go model
	 * @return all instances of this class in Model 'model' as a ReactorResult,
	 * which can conveniently be converted to iterator, list or array.
	 *
	 * [Generated from RDFReactor template rule #class3-as]
	 */
	public static ReactorResult<? extends Unifywithinverseandpickone> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, Unifywithinverseandpickone.class );
	}

    /**
	 * Remove triple {@code (this, rdf:type, Unifywithinverseandpickone)} from this instance. Other triples are not affected.
	 * To delete more, use deleteAllProperties
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class4]
	 */
	public static void deleteInstance(Model model, Resource instanceResource) {
		Base.deleteInstance(model, RDFS_CLASS, instanceResource);
	}

	/**
	 * Delete all triples {@code (this, *, *)}, i.e. including {@code rdf:type}.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #class5]
	 */
	public static void deleteAllProperties(Model model,	Resource instanceResource) {
		Base.deleteAllProperties(model, instanceResource);
	}

    ///////////////////////////////////////////////////////////////////
    // property access methods


    /**
     * Check if {@code Similarproperty} has at least one value set.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static]
     */
	public static boolean hasSimilarproperty(Model model, Resource instanceResource) {
		return Base.has(model, instanceResource, SIMILARPROPERTY);
	}

    /**
     * Check if {@code Similarproperty} has at least one value set.
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic]
     */
	public boolean hasSimilarproperty() {
		return Base.has(this.model, this.getResource(), SIMILARPROPERTY);
	}

    /**
     * Check if {@code Similarproperty} has the given value (maybe among other values).
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static]
     */
	public static boolean hasSimilarproperty(Model model, Resource instanceResource, Node value ) {
		return Base.hasValue(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Check if {@code Similarproperty} has the given value (maybe among other values).
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic]
     */
	public boolean hasSimilarproperty( Node value ) {
		return Base.hasValue(this.model, this.getResource(), SIMILARPROPERTY, value);
	}

     /**
     * Get all values of property {@code Similarproperty} as an Iterator over RDF2Go nodes.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static]
     */
	public static ClosableIterator<Node> getAllSimilarproperty_asNode(Model model, Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, SIMILARPROPERTY);
	}

    /**
     * Get all values of property {@code Similarproperty} as a ReactorResult of RDF2Go nodes.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result]
     */
	public static ReactorResult<Node> getAllSimilarproperty_asNode_(Model model, Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SIMILARPROPERTY, Node.class);
	}

    /**
     * Get all values of property {@code Similarproperty} as an Iterator over RDF2Go nodes
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic]
     */
	public ClosableIterator<Node> getAllSimilarproperty_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), SIMILARPROPERTY);
	}

    /**
     * Get all values of property {@code Similarproperty} as a ReactorResult of RDF2Go nodes.
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result]
     */
	public ReactorResult<Node> getAllSimilarproperty_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), SIMILARPROPERTY, Node.class);
	}
 
    /**
     * Get all values of property {@code Similarproperty}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static]
     */
	public static ClosableIterator<Thing1> getAllSimilarproperty(Model model, Resource instanceResource) {
		return Base.getAll(model, instanceResource, SIMILARPROPERTY, Thing1.class);
	}

    /**
     * Get all values of property {@code Similarproperty} as a ReactorResult of {@linkplain Thing1}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult]
     */
	public static ReactorResult<Thing1> getAllSimilarproperty_as(Model model, Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, SIMILARPROPERTY, Thing1.class);
	}

    /**
     * Get all values of property {@code Similarproperty}.
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic]
     */
	public ClosableIterator<Thing1> getAllSimilarproperty() {
		return Base.getAll(this.model, this.getResource(), SIMILARPROPERTY, Thing1.class);
	}

    /**
     * Get all values of property {@code Similarproperty} as a ReactorResult of {@linkplain Thing1}.
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult]
     */
	public ReactorResult<Thing1> getAllSimilarproperty_as() {
		return Base.getAll_as(this.model, this.getResource(), SIMILARPROPERTY, Thing1.class);
	}

 
    /**
     * Adds a value to property {@code Similarproperty} as an RDF2Go {@linkplain Node}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static]
     */
	public static void addSimilarproperty(Model model, Resource instanceResource, Node value) {
		Base.add(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Adds a value to property {@code Similarproperty} as an RDF2Go {@linkplain Node}.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic]
     */
	public void addSimilarproperty(Node value) {
		Base.add(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
    /**
     * Adds a value to property {@code Similarproperty} from an instance of {@linkplain Thing1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @param value
	 *
	 * [Generated from RDFReactor template rule #add3static]
     */
	public static void addSimilarproperty(Model model, Resource instanceResource, Thing1 value) {
		Base.add(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Adds a value to property {@code Similarproperty} from an instance of {@linkplain Thing1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
	 *
	 * [Generated from RDFReactor template rule #add4dynamic]
     */
	public void addSimilarproperty(Thing1 value) {
		Base.add(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
  

    /**
     * Sets a value of property {@code Similarproperty} from an RDF2Go {@linkplain Node}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static]
     */
	public static void setSimilarproperty(Model model, Resource instanceResource, Node value) {
		Base.set(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Sets a value of property {@code Similarproperty} from an RDF2Go {@linkplain Node}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic]
     */
	public void setSimilarproperty(Node value) {
		Base.set(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
    /**
     * Sets a value of property {@code Similarproperty} from an instance of {@linkplain Thing1}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static]
     */
	public static void setSimilarproperty(Model model, Resource instanceResource, Thing1 value) {
		Base.set(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Sets a value of property {@code Similarproperty} from an instance of {@linkplain Thing1}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic]
     */
	public void setSimilarproperty(Thing1 value) {
		Base.set(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
  


    /**
     * Removes a value of property {@code Similarproperty} as an RDF2Go {@linkplain Node}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static]
     */
	public static void removeSimilarproperty(Model model, Resource instanceResource, Node value) {
		Base.remove(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Removes a value of property {@code Similarproperty} as an RDF2Go {@linkplain Node}.
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic]
     */
	public void removeSimilarproperty(Node value) {
		Base.remove(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
    /**
     * Removes a value of property {@code Similarproperty} given as an instance of {@linkplain Thing1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static]
     */
	public static void removeSimilarproperty(Model model, Resource instanceResource, Thing1 value) {
		Base.remove(model, instanceResource, SIMILARPROPERTY, value);
	}

    /**
     * Removes a value of property {@code Similarproperty} given as an instance of {@linkplain Thing1}.
     * Comment from schema: This class acts as a catch-all for all properties, for which no domain has specified.
     *
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic]
     */
	public void removeSimilarproperty(Thing1 value) {
		Base.remove(this.model, this.getResource(), SIMILARPROPERTY, value);
	}
  
    /**
     * Removes all values of property {@code Similarproperty}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static]
     */
	public static void removeAllSimilarproperty(Model model, Resource instanceResource) {
		Base.removeAll(model, instanceResource, SIMILARPROPERTY);
	}

    /**
     * Removes all values of property {@code Similarproperty}.
	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic]
     */
	public void removeAllSimilarproperty() {
		Base.removeAll(this.model, this.getResource(), SIMILARPROPERTY);
	}
 }