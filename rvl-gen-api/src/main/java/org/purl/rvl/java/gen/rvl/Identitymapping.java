/*
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 1895 2013-02-09 17:39:56Z max.at.xam.de@gmail.com $) on 17.08.15 19:15
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
 * Comment from schema: Also referred to as "Passthrough Mapping" in some tools.
 *
 * This class manages access to these properties:
 * <ul>
 *   <li>Targetattribute</li>
 * </ul>
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 17.08.15 19:15
 */
public class Identitymapping extends PropertyMapping {

    private static final long serialVersionUID = -5007079440900112795L;

    /** http://purl.org/rvl/IdentityMapping */
	public static final URI RDFS_CLASS = new URIImpl("http://purl.org/rvl/IdentityMapping", false);

    /** http://purl.org/rvl/targetAttribute */
	public static final URI TARGETATTRIBUTE = new URIImpl("http://purl.org/rvl/targetAttribute", false);

    /**
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available.
     */
    public static final URI[] MANAGED_URIS = {
      new URIImpl("http://purl.org/rvl/targetAttribute", false)
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
	protected Identitymapping (Model model, URI classURI, Resource instanceIdentifier, boolean write) {
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
	public Identitymapping (Model model, Resource instanceIdentifier, boolean write) {
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
	public Identitymapping (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public Identitymapping (Model model, BlankNode bnode, boolean write) {
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
	public Identitymapping (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of Identitymapping or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0]
	 */
	public static Identitymapping getInstance(Model model, Resource instanceResource) {
		return Base.getInstance(model, instanceResource, Identitymapping.class);
	}

	/**
	 * Create a new instance of this class in the model.
	 * That is, create the statement (instanceResource, RDF.type, http://purl.org/rvl/IdentityMapping).
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
	public static ReactorResult<? extends Identitymapping> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, Identitymapping.class );
	}

    /**
	 * Remove triple {@code (this, rdf:type, Identitymapping)} from this instance. Other triples are not affected.
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
     * Check if {@code Targetattribute} has at least one value set.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-static]
     */
	public static boolean hasTargetattribute(Model model, Resource instanceResource) {
		return Base.has(model, instanceResource, TARGETATTRIBUTE);
	}

    /**
     * Check if {@code Targetattribute} has at least one value set.
     * @return true if this property has at least one value
	 *
	 * [Generated from RDFReactor template rule #get0has-dynamic]
     */
	public boolean hasTargetattribute() {
		return Base.has(this.model, this.getResource(), TARGETATTRIBUTE);
	}

    /**
     * Check if {@code Targetattribute} has the given value (maybe among other values).
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-static]
     */
	public static boolean hasTargetattribute(Model model, Resource instanceResource, Node value ) {
		return Base.hasValue(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Check if {@code Targetattribute} has the given value (maybe among other values).
	 * @param value the value to be checked
     * @return true if this property contains (maybe among other) the given value
	 *
	 * [Generated from RDFReactor template rule #get0has-value-dynamic]
     */
	public boolean hasTargetattribute( Node value ) {
		return Base.hasValue(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}

     /**
     * Get all values of property {@code Targetattribute} as an Iterator over RDF2Go nodes.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static]
     */
	public static ClosableIterator<Node> getAllTargetattribute_asNode(Model model, Resource instanceResource) {
		return Base.getAll_asNode(model, instanceResource, TARGETATTRIBUTE);
	}

    /**
     * Get all values of property {@code Targetattribute} as a ReactorResult of RDF2Go nodes.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get7static-reactor-result]
     */
	public static ReactorResult<Node> getAllTargetattribute_asNode_(Model model, Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, TARGETATTRIBUTE, Node.class);
	}

    /**
     * Get all values of property {@code Targetattribute} as an Iterator over RDF2Go nodes
     * @return a ClosableIterator of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic]
     */
	public ClosableIterator<Node> getAllTargetattribute_asNode() {
		return Base.getAll_asNode(this.model, this.getResource(), TARGETATTRIBUTE);
	}

    /**
     * Get all values of property {@code Targetattribute} as a ReactorResult of RDF2Go nodes.
     * @return a List of RDF2Go Nodes
	 *
	 * [Generated from RDFReactor template rule #get8dynamic-reactor-result]
     */
	public ReactorResult<Node> getAllTargetattribute_asNode_() {
		return Base.getAll_as(this.model, this.getResource(), TARGETATTRIBUTE, Node.class);
	}
 
    /**
     * Get all values of property {@code Targetattribute}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get11static]
     */
	public static ClosableIterator<GraphicAttribute> getAllTargetattribute(Model model, Resource instanceResource) {
		return Base.getAll(model, instanceResource, TARGETATTRIBUTE, GraphicAttribute.class);
	}

    /**
     * Get all values of property {@code Targetattribute} as a ReactorResult of {@linkplain GraphicAttribute}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get11static-reactorresult]
     */
	public static ReactorResult<GraphicAttribute> getAllTargetattribute_as(Model model, Resource instanceResource) {
		return Base.getAll_as(model, instanceResource, TARGETATTRIBUTE, GraphicAttribute.class);
	}

    /**
     * Get all values of property {@code Targetattribute}.
     * @return a ClosableIterator of $type
	 *
	 * [Generated from RDFReactor template rule #get12dynamic]
     */
	public ClosableIterator<GraphicAttribute> getAllTargetattribute() {
		return Base.getAll(this.model, this.getResource(), TARGETATTRIBUTE, GraphicAttribute.class);
	}

    /**
     * Get all values of property {@code Targetattribute} as a ReactorResult of {@linkplain GraphicAttribute}.
     * @return a ReactorResult of $type which can conveniently be converted to iterator, list or array
	 *
	 * [Generated from RDFReactor template rule #get12dynamic-reactorresult]
     */
	public ReactorResult<GraphicAttribute> getAllTargetattribute_as() {
		return Base.getAll_as(this.model, this.getResource(), TARGETATTRIBUTE, GraphicAttribute.class);
	}

 
    /**
     * Adds a value to property {@code Targetattribute} as an RDF2Go {@linkplain Node}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1static]
     */
	public static void addTargetattribute(Model model, Resource instanceResource, Node value) {
		Base.add(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Adds a value to property {@code Targetattribute} as an RDF2Go {@linkplain Node}.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #add1dynamic]
     */
	public void addTargetattribute(Node value) {
		Base.add(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
    /**
     * Adds a value to property {@code Targetattribute} from an instance of {@linkplain GraphicAttribute}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
     * @param value
	 *
	 * [Generated from RDFReactor template rule #add3static]
     */
	public static void addTargetattribute(Model model, Resource instanceResource, GraphicAttribute value) {
		Base.add(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Adds a value to property {@code Targetattribute} from an instance of {@linkplain GraphicAttribute}.
	 *
	 * [Generated from RDFReactor template rule #add4dynamic]
     */
	public void addTargetattribute(GraphicAttribute value) {
		Base.add(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
  

    /**
     * Sets a value of property {@code Targetattribute} from an RDF2Go {@linkplain Node}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be set
	 *
	 * [Generated from RDFReactor template rule #set1static]
     */
	public static void setTargetattribute(Model model, Resource instanceResource, Node value) {
		Base.set(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Sets a value of property {@code Targetattribute} from an RDF2Go {@linkplain Node}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set1dynamic]
     */
	public void setTargetattribute(Node value) {
		Base.set(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
    /**
     * Sets a value of property {@code Targetattribute} from an instance of {@linkplain GraphicAttribute}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set3static]
     */
	public static void setTargetattribute(Model model, Resource instanceResource, GraphicAttribute value) {
		Base.set(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Sets a value of property {@code Targetattribute} from an instance of {@linkplain GraphicAttribute}.
     * First, all existing values are removed, then this value is added.
     * Cardinality constraints are not checked, but this method exists only for properties with
     * no {@code minCardinality} or {@code minCardinality == 1}.
	 * @param value the value to be added
	 *
	 * [Generated from RDFReactor template rule #set4dynamic]
     */
	public void setTargetattribute(GraphicAttribute value) {
		Base.set(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
  


    /**
     * Removes a value of property {@code Targetattribute} as an RDF2Go {@linkplain Node}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1static]
     */
	public static void removeTargetattribute(Model model, Resource instanceResource, Node value) {
		Base.remove(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Removes a value of property {@code Targetattribute} as an RDF2Go {@linkplain Node}.
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove1dynamic]
     */
	public void removeTargetattribute(Node value) {
		Base.remove(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
    /**
     * Removes a value of property {@code Targetattribute} given as an instance of {@linkplain GraphicAttribute}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove3static]
     */
	public static void removeTargetattribute(Model model, Resource instanceResource, GraphicAttribute value) {
		Base.remove(model, instanceResource, TARGETATTRIBUTE, value);
	}

    /**
     * Removes a value of property {@code Targetattribute} given as an instance of {@linkplain GraphicAttribute}.
	 * @param value the value to be removed
	 *
	 * [Generated from RDFReactor template rule #remove4dynamic]
     */
	public void removeTargetattribute(GraphicAttribute value) {
		Base.remove(this.model, this.getResource(), TARGETATTRIBUTE, value);
	}
  
    /**
     * Removes all values of property {@code Targetattribute}.
     * @param model an RDF2Go model
     * @param instanceResource an RDF2Go resource
	 *
	 * [Generated from RDFReactor template rule #removeall1static]
     */
	public static void removeAllTargetattribute(Model model, Resource instanceResource) {
		Base.removeAll(model, instanceResource, TARGETATTRIBUTE);
	}

    /**
     * Removes all values of property {@code Targetattribute}.
	 *
	 * [Generated from RDFReactor template rule #removeall1dynamic]
     */
	public void removeAllTargetattribute() {
		Base.removeAll(this.model, this.getResource(), TARGETATTRIBUTE);
	}
 }