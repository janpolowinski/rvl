/*
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 1895 2013-02-09 17:39:56Z max.at.xam.de@gmail.com $) on 16.08.15 12:18
 */
package org.purl.rvl.java.gen.viso.graphic;

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
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 16.08.15 12:18
 */
public class Saturation extends Thing1 {

    private static final long serialVersionUID = 4854871568172959071L;

    /** http://purl.org/viso/graphic/Saturation */
	public static final URI RDFS_CLASS = new URIImpl("http://purl.org/viso/graphic/Saturation", false);

    /**
     * All property-URIs with this class as domain.
     * All properties of all super-classes are also available.
     */
    public static final URI[] MANAGED_URIS = {

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
	protected Saturation (Model model, URI classURI, Resource instanceIdentifier, boolean write) {
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
	public Saturation (Model model, Resource instanceIdentifier, boolean write) {
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
	public Saturation (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public Saturation (Model model, BlankNode bnode, boolean write) {
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
	public Saturation (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of Saturation or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0]
	 */
	public static Saturation getInstance(Model model, Resource instanceResource) {
		return Base.getInstance(model, instanceResource, Saturation.class);
	}

	/**
	 * Create a new instance of this class in the model.
	 * That is, create the statement (instanceResource, RDF.type, http://purl.org/viso/graphic/Saturation).
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
	public static ReactorResult<? extends Saturation> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, Saturation.class );
	}

    /**
	 * Remove triple {@code (this, rdf:type, Saturation)} from this instance. Other triples are not affected.
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
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Colorsaturationnamed' to this Saturation instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllColorsaturationnamed_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, GraphicObject.COLORSATURATIONNAMED, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Colorsaturationnamed' to this Saturation instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllColorsaturationnamed_Inverse() {
		return Base.getAll_Inverse(this.model, GraphicObject.COLORSATURATIONNAMED, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Colorsaturationnamed' to this Saturation instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllColorsaturationnamed_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, GraphicObject.COLORSATURATIONNAMED, objectValue, Resource.class);
	}



}