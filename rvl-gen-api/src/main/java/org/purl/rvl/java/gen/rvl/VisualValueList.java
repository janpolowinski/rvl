/*
 * generated by http://RDFReactor.semweb4j.org ($Id: CodeGenerator.java 1895 2013-02-09 17:39:56Z max.at.xam.de@gmail.com $) on 18.08.15 11:57
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
 *
 * This class was generated by <a href="http://RDFReactor.semweb4j.org">RDFReactor</a> on 18.08.15 11:57
 */
public class VisualValueList extends Thing1 {

    private static final long serialVersionUID = 5733763764613682785L;

    /** http://purl.org/viso/graphic/Visual_Value_List */
	public static final URI RDFS_CLASS = new URIImpl("http://purl.org/viso/graphic/Visual_Value_List", false);

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
	protected VisualValueList (Model model, URI classURI, Resource instanceIdentifier, boolean write) {
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
	public VisualValueList (Model model, Resource instanceIdentifier, boolean write) {
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
	public VisualValueList (Model model, String uriString, boolean write) throws ModelRuntimeException {
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
	public VisualValueList (Model model, BlankNode bnode, boolean write) {
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
	public VisualValueList (Model model, boolean write) {
		super(model, RDFS_CLASS, model.newRandomUniqueURI(), write);
	}

    ///////////////////////////////////////////////////////////////////
    // typing

	/**
	 * Return an existing instance of this class in the model. No statements are written.
	 * @param model an RDF2Go model
	 * @param instanceResource an RDF2Go resource
	 * @return an instance of VisualValueList or null if none existst
	 *
	 * [Generated from RDFReactor template rule #class0]
	 */
	public static VisualValueList getInstance(Model model, Resource instanceResource) {
		return Base.getInstance(model, instanceResource, VisualValueList.class);
	}

	/**
	 * Create a new instance of this class in the model.
	 * That is, create the statement (instanceResource, RDF.type, http://purl.org/viso/graphic/Visual_Value_List).
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
	public static ReactorResult<? extends VisualValueList> getAllInstances_as(Model model) {
		return Base.getAllInstances_as(model, RDFS_CLASS, VisualValueList.class );
	}

    /**
	 * Remove triple {@code (this, rdf:type, VisualValueList)} from this instance. Other triples are not affected.
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
	 * @return all A's as RDF resources, that have a relation 'Targetvaluecycle' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllTargetvaluecycle_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Valuemapping.TARGETVALUECYCLE, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Targetvaluecycle' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllTargetvaluecycle_Inverse() {
		return Base.getAll_Inverse(this.model, Valuemapping.TARGETVALUECYCLE, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Targetvaluecycle' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllTargetvaluecycle_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, Valuemapping.TARGETVALUECYCLE, objectValue, Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Targetvaluelist' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllTargetvaluelist_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Valuemapping.TARGETVALUELIST, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Targetvaluelist' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllTargetvaluelist_Inverse() {
		return Base.getAll_Inverse(this.model, Valuemapping.TARGETVALUELIST, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Targetvaluelist' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllTargetvaluelist_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, Valuemapping.TARGETVALUELIST, objectValue, Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Targetvalueorderedset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllTargetvalueorderedset_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Valuemapping.TARGETVALUEORDEREDSET, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Targetvalueorderedset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllTargetvalueorderedset_Inverse() {
		return Base.getAll_Inverse(this.model, Valuemapping.TARGETVALUEORDEREDSET, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Targetvalueorderedset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllTargetvalueorderedset_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, Valuemapping.TARGETVALUEORDEREDSET, objectValue, Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Targetvalues_abstract_' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllTargetvalues_abstract__Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Valuemapping.TARGETVALUES_ABSTRACT_, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Targetvalues_abstract_' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllTargetvalues_abstract__Inverse() {
		return Base.getAll_Inverse(this.model, Valuemapping.TARGETVALUES_ABSTRACT_, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Targetvalues_abstract_' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllTargetvalues_abstract__Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, Valuemapping.TARGETVALUES_ABSTRACT_, objectValue, Resource.class);
	}


	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as RDF resources, that have a relation 'Targetvalueset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1static]
	 */
	public static ClosableIterator<Resource> getAllTargetvalueset_Inverse(Model model, Object objectValue) {
		return Base.getAll_Inverse(model, Valuemapping.TARGETVALUESET, objectValue);
	}

	/**
	 * @return all A's as RDF resources, that have a relation 'Targetvalueset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse1dynamic]
	 */
	public ClosableIterator<Resource> getAllTargetvalueset_Inverse() {
		return Base.getAll_Inverse(this.model, Valuemapping.TARGETVALUESET, this.getResource() );
	}

	/**
	 * @param model an RDF2Go model
	 * @param objectValue
	 * @return all A's as a ReactorResult, that have a relation 'Targetvalueset' to this VisualValueList instance
	 *
	 * [Generated from RDFReactor template rule #getallinverse-as1static]
	 */
	public static ReactorResult<Resource> getAllTargetvalueset_Inverse_as(Model model, Object objectValue) {
		return Base.getAll_Inverse_as(model, Valuemapping.TARGETVALUESET, objectValue, Resource.class);
	}



}