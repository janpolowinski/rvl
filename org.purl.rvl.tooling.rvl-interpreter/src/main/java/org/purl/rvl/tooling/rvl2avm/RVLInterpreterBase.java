package org.purl.rvl.tooling.rvl2avm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.UnsupportedMappingParameterValueException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Shape;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.MappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.rvl.SubMappingRelationX;
import org.purl.rvl.java.rvl.mapping.TupleSourceValueTargetValue;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 * 
 */
public abstract class RVLInterpreterBase implements RVLInterpreter {

	protected Model modelAVM;
	protected ModelSet modelSet;
	protected Model modelData;
	protected Model modelMappings;
	protected Model modelVISO;
	protected Map<Resource, GraphicObjectX> resourceGraphicObjectMap;
	protected Set<GraphicObjectX> graphicObjects;
	protected Random random;

	protected OGVICProcess ogvicProcess = OGVICProcess.getInstance();

	private final static Logger LOGGER = Logger.getLogger(RVLInterpreterBase.class.getName());
	static final String NL = System.getProperty("line.separator");

	public RVLInterpreterBase() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#init(org.ontoware.rdf2go.model.Model,
	 * org.ontoware.rdf2go.model.ModelSet)
	 */
	public void init(Model modelAVM, ModelSet modelSet) {
		this.modelAVM = modelAVM;
		this.modelSet = modelSet;
		this.modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);
		this.modelMappings = modelSet.getModel(OGVICProcess.GRAPH_MAPPING);
		this.modelVISO = modelSet.getModel(OGVICProcess.GRAPH_VISO);
		this.random = new Random();
		this.resourceGraphicObjectMap = new HashMap<Resource, GraphicObjectX>();
		this.graphicObjects = new HashSet<GraphicObjectX>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#interpretMappings()
	 */
	public void interpretMappings() {
		LOGGER.info("Starting mapping interpretation ... ");
		LOGGER.info("Interpreting mappings using " + this.getClass().getName());
		interpretMappingsInternal();
	}

	abstract protected void interpretMappingsInternal();

	/**
	 * Creates a GraphicObjectX for a Resource or returns the existing GraphicObjectX, if already created before
	 * 
	 * @param resource
	 * @return the GraphicObjectX representing the resource
	 */
	@Override
	public GraphicObjectX createOrGetGraphicObject(Resource resource) {

		if (resourceGraphicObjectMap.containsKey(resource)) {

			LOGGER.finest("Found existing GO for " + resource);
			return resourceGraphicObjectMap.get(resource);
		} else {

			GraphicObjectX go = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_" + random.nextInt(),
					true);

			// add to cache
			go = RVLUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(go, GraphicObjectX.class);

			go.setRepresents(resource);

			// set default shape here hardcoded to circles // TODO: make more flexible
			// the default shape will be removed, when a text-value is set by another mapping,
			// since in this case the text determines the shape
			go.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Circle", false));

			resourceGraphicObjectMap.put(resource, go);
			addToMainGraphicObjectSet(go);

			LOGGER.finer("Newly created GO for " + resource);

			return go;
		}
	}

	@Override
	public void addToMainGraphicObjectSet(GraphicObjectX newGraphicObject) {
		graphicObjects.add(newGraphicObject);
	}

	@Override
	public Set<GraphicObjectX> getMainGraphicObjectSet() {
		return graphicObjects;
	}

	@Override
	public String createNewInternalID() {
		return random.nextInt() + "";
	}

	/*
	 * protected Set<PropertyToGO2ORMappingX> getAllMappingsToLinking() {
	 * 
	 * Set<PropertyToGO2ORMappingX> mappingSet = new HashSet<PropertyToGO2ORMappingX>();
	 * 
	 * String queryString = "" + "SELECT DISTINCT ?mapping " + "WHERE { " + "    ?mapping a <" +
	 * PropertyToGO2ORMappingX.RDFS_CLASS + "> . " + "    ?mapping <" +
	 * PropertyToGO2ORMappingX.TARGETOBJECT_TO_OBJECTRELATION + "> <" + DirectedLinking.RDFS_CLASS + "> . " + "} " ;
	 * 
	 * LOGGER.finer("SPARQL: query all mappings to Directed Linking:" + NL + queryString);
	 * 
	 * QueryResultTable results = model.sparqlSelect(queryString); //for(QueryRow row : results) {LOGGER.info(row); }
	 * //for(String var : results.getVariables()) { LOGGER.info(var); }
	 * 
	 * for(QueryRow row : results) { Property_to_Graphic_Object_to_Object_RelationMapping mapping =
	 * Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(model, (URI)row.getValue("mapping"));
	 * mappingSet.add((PropertyToGO2ORMappingX)mapping.castTo(PropertyToGO2ORMappingX.class));
	 * //LOGGER.info("Found mapping to linking: " + row.getValue("mapping").toString()); }
	 * 
	 * return mappingSet; }
	 */

	protected void performDefaultLabelMappingForAllGOs() {

		// for (Map.Entry<Resource,GraphicObjectX> entry : resourceGraphicObjectMap.entrySet()) {
		// GraphicObjectX go = entry.getValue();
		// Resource resource = entry.getKey();

		for (GraphicObjectX go : getMainGraphicObjectSet()) {
			
			try {

				Resource resource = go.getRepresentedResource();

				// never default-label text shapes
				if (!go.hasTextvalue()) {
				
					// TODO: temp workaround: only do this when no other TEXT label already exists (with text_value attribute)
					if (!go.hasLabeledwith()) {
		
							performDefaultLabelMapping(go, resource);
		
					} else {
		
						Labeling rel = go.getAllLabeledwith_as().firstValue();
		
						if (rel.hasLabelinglabel()) {
		
							Thing1 label = rel.getAllLabelinglabel_as().firstValue();
							GraphicObjectX labelGO = (GraphicObjectX) label.castTo(GraphicObjectX.class);
		
							// also perform a default text label mapping when only an icon labeling exists
							if (null != labelGO && !labelGO.hasTextvalue()) {
								performDefaultLabelMapping(go, resource);
							}
		
						}
					}
				}
			
			} catch (MappingException e) {
				LOGGER.warning("Default labeling will be incomplete: " +  e.getMessage());
			}

		}
	}

	private void performDefaultLabelMapping(GraphicObjectX go, Resource resource) throws MappingException {

		if (null == resource) {
			throw new MappingException("Could perform default labeling. No represented Resource found "
					+ "for Graphic Object " + go);
		}

		// create an additional object here, don't reuse existing ones!
		GraphicObjectX label = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_Label_"
				+ this.createNewInternalID(), false);
		label.setTextvalue(AVMUtils.getGoodNodeLabel(resource, modelData));

		LOGGER.finest("Created new Label-GO for resource: " + resource.toString());

		Labeling rel = new Labeling(modelAVM, "http://purl.org/rvl/example-avm/Labeling_Relation_"
				+ createNewInternalID(), true);
		
		rel.setLabel("Labeling Relation");
		rel.setLabelinglabel(label);
		rel.setLabelingattachedBy(Superimposition.RDFS_CLASS);
		rel.setLabelingbase(go);

		go.addLabeledwith(rel);

	}

	/**
	 * Applies sub-mappings (if any exist) based on a "main" statement. An existing GO to append the sub-mapping, as
	 * well as a triple part (S,P,O) of the main statement (as a base for the mapping) is determined by the sub-mapping
	 * relation. TODO: linking-specific! only works on top of P2GOTORMs and only for sub-mappings that are P2GAMs
	 * 
	 * @param p2go2orm
	 * @param mainStatement
	 * @param dlRel
	 * @throws
	 */
	@Override
	public void applySubmappings(PropertyToGO2ORMappingX p2go2orm, Statement mainStatement, Object_to_ObjectRelation graphicRelation) {

		// TODO derive GO by onRole settings and the mainStatement? or just check if correct?

		Set<SubMappingRelationX> subMappingRelations = p2go2orm.getSubMappings();

		for (Iterator<SubMappingRelationX> iterator = subMappingRelations.iterator(); iterator.hasNext();) {

			SubMappingRelationX smr = (SubMappingRelationX) iterator.next();

			URI roleURI = smr.getOnRole().asURI();
			URI triplePartURI = smr.getOnTriplePart().asURI();

			// modelAVM.findStatements(dlRel,role,Variable.ANY); does not work somehow -> Jena mapping problems

			MappingX subMapping = smr.getSubMapping();

			if (subMapping.isDisabled()) {
				// LOGGER.info("The referenced submapping was disabled. Will ignore it");
				LOGGER.finest("The referenced submapping was disabled but will still be used."
						+ " TODO: implement 3 status ENABLED (default), DISABLED, USE-ONLY-AS-SUBMAPPING or better allow " +
						"disabling submapping relation");
				// continue;
			}

			// TODO can also be another P2GO2OR-mapping, TODO CAN ALSO BE AN IDENTITY MAPPING!! This crashes when
			// "subclasses" of
			// PM are stored in the cash, since they cannot automatically be casted to PM, because the JAVA-Class
			// hierarchy does not reflect the
			// subclass-hierarchy (wrappers inherit from generated classes instead!)
			// short time solution: only store PM to cash, not subclasses
			// long term solution restructure subclass-hierarchy. all wrappers use delegation instead of inheritance
			PropertyMappingX subMappingPM = (PropertyMappingX) subMapping.castTo(PropertyMappingX.class);

			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			// not already here, we need to know first, what kind of mapping we want to receive P2GAM, P2GOTORM? ... ) : 
			// mapping = RVLUtils.tryReplaceWithCashedInstanceForSameURI(mapping, mapping.getClass());

			try {

				if (roleURI.toString().equals("http://purl.org/viso/graphic/this")) {

					// apply submapping to this relation itself?
					//
					// currently we also allow submapping on the relation itself to "parameterize" the n-ary
					// relationship,
					// e.g. to set the attachment type for labeling relations

					LOGGER.finer("Applying submapping to the GR itself (role: " + roleURI + ") based on triple part "
							+ triplePartURI);

					applyMappingToNaryRelation(mainStatement, triplePartURI, graphicRelation, subMappingPM);

				} else {

					// otherwise apply it to a GO with the given role ...

					LOGGER.finer("Applying submapping to GO with the role " + roleURI + " based on triple part "
							+ triplePartURI);

					GraphicObjectX goToApplySubmapping = AVMUtils.getGOForRole(modelAVM, graphicRelation, roleURI);
					// TODO this is a simplification: multiple GOs may be affected, not only one

					applyMappingToGraphicObject(mainStatement, triplePartURI, goToApplySubmapping, subMappingPM);
					// this does not use the cashed mappings somehow:
					// goToApplySubmapping.setLabel(roleURI + " with an applied submapping: " + smr.toStringSummary());

				}

			} catch (InsufficientMappingSpecificationException e) {

				LOGGER.warning("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());

			} catch (UnsupportedMappingParameterValueException e) {

				LOGGER.warning("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());

			} catch (MappingException e) {

				LOGGER.warning("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());
			}

		}

	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#applyParameterToGraphicRelation(org.ontoware.rdf2go.model.node.Resource, org.ontoware.rdf2go.model.node.Node, org.ontoware.rdf2go.model.node.Node, org.purl.rvl.java.gen.viso.graphic.GraphicRelation)
	 */
	@Override
	public void applyParameterToGraphicRelation(Resource parameterProperty, Node parameterValue, Node sourceValue, Object_to_ObjectRelation graphicRelation) {
		
		if (null != parameterProperty && null != parameterValue && null != sourceValue && null != graphicRelation) {
			
			LOGGER.finest("Setting parameter " + parameterProperty + " to " + parameterValue + " for source value " + sourceValue);
			
			// if we are mapping to labeling_attachedBy 
			if (parameterProperty.asURI().equals(Labeling.LABELINGATTACHEDBY)) {
				
				GraphicObjectToObjectRelation attachementRelation = GraphicObjectToObjectRelation.getInstance(
						modelVISO, parameterValue.asURI());
				((Labeling) graphicRelation.castTo(Labeling.class)).setLabelingattachedBy(parameterValue);  
				
				LOGGER.finer("Set labeling attachment to " + attachementRelation + " for source value " + sourceValue + NL);
			}
			
			// if we are mapping to labeling_position
			if (parameterProperty.asURI().equals(Labeling.LABELINGPOSITION)) {
				
				((Labeling) graphicRelation.castTo(Labeling.class)).setLabelingposition(parameterValue); 

				LOGGER.finer("Set label position to " + parameterValue + " for source value " + sourceValue + NL);
			}
			
		} else {
			LOGGER.warning("Could not set parameter " + parameterProperty + " to " + parameterValue + " for source value " + sourceValue);
		}
		
	}

	@Override
	public void applyGraphicValueToGO(GraphicAttribute tga, Node tv, Node sv, GraphicObjectX go) {

		if (null != tga && null != tv && null != sv && null != go) {

			LOGGER.finest("Setting tv " + tv + " for sv " + sv);

			// if we are mapping to named colors
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
				Color color = Color.getInstance(modelVISO, tv.asURI());
				go.setColornamed(color);
				LOGGER.finer("Set color named to " + color + " for sv " + sv);
			}

			// if we are mapping to lightness
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/color_hsl_lightness")) {
				go.setColorhsllightness(tv);
				LOGGER.finer("Set color hsl lightness to " + tv.toString() + " for sv " + sv);
			}

			// if we are mapping to named shapes (will override text shapes)
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
				Shape shape = ShapeX.getInstance(modelVISO, tv.asURI());
				go.setShapenamed(shape);
				// remove existing text shapes
				go.removeAllTextvalue();
				LOGGER.finer("Set shape to " + shape + " for sv " + sv + " and removed all other shapes." + NL);
			}

			// if we are mapping to text shape (will override named shapes)
			// TODO handle language tags
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/text_value")) { // GraphicObject.TEXTVALUE
				String textValue = tv.asLiteral().getValue();
				go.setTextvalue(textValue);
				// remove existing named shapes (incl. the eventually set default shape)
				go.removeAllShapenamed();
				LOGGER.finer("Set shape to " + textValue + " for sv " + sv + " and removed all other shapes." + NL);
			}

			// if we are mapping to width
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/width")) {
				Float width = new Float(tv.asLiteral().getValue());
				go.setWidth(width);
				LOGGER.finer("Set width to float value " + width + " for sv " + sv + NL);
			}

		}

		else {
			LOGGER.warning("Could not set target value, since one of the required parameters was null.");
		}
	}

	/**
	 * @param tga
	 *            - the graphic attribute to set
	 * @param tv
	 *            - the graphic attribute value to apply
	 * @param mappedNode
	 *            - the node the mapping was originally applied to
	 * @param inheritedBy
	 *            - the required relation between the mapped node and nodes inheriting the mapping
	 */
	@Override
	public void applyGraphicValueToGOsRepresentingNodesRelatedVia(GraphicAttribute tga, Node tv, Resource mappedNode,
			Property inheritedBy) {

		Set<Resource> relatedResources = DataQuery.getRelatedResources(modelSet, mappedNode, inheritedBy);

		LOGGER.finest("related resources " + relatedResources.toString() + " will receive same tv (" + tv + ")");

		for (Resource resource : relatedResources) {
			applyGraphicValueToGO(tga, tv, mappedNode, createOrGetGraphicObject(resource));
		}
	}

	/**
	 * Hack: Copied from applyMappingToGraphicObject() to allow for "parameterizing" n-ary-relations by submappings.
	 * 
	 * @param mainStatement
	 * @param triplePartURI
	 * @param rel
	 * @param mapping
	 * @throws InsufficientMappingSpecificationException
	 * @throws UnsupportedMappingParameterValueException
	 */
	public void applyMappingToNaryRelation(Statement mainStatement, URI triplePartURI, Object_to_ObjectRelation rel,
			PropertyMappingX mapping) throws InsufficientMappingSpecificationException,
			UnsupportedMappingParameterValueException {

		if (mapping.isInstanceof(PropertyToGraphicAttributeMappingX.RDFS_CLASS)) {

			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) mapping
					.castTo(PropertyToGraphicAttributeMappingX.class);

			GraphicAttribute tga = p2gam.getTargetAttribute();
			Property sp = p2gam.getSourceProperty();

			// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings
			// instead

			if (null == tga) {
				throw new InsufficientMappingSpecificationException("no target graphic attribute set");
			}

			if (!p2gam.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"P2GAM with no value mappings at all are not yet supported (defaults needs to be implemented).");
			}

			Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();

			if (null == svUriTVuriMap || svUriTVuriMap.isEmpty()) {
				LOGGER.severe("Could not apply submappings since (explicit or calculated) value mappings were null");
				return;
			} else {
				LOGGER.finest(p2gam.explicitlyMappedValuesToString());
			}

			Node sourceValue = null, targetValue = null;

			if (sp.asURI().equals(RDF.ID)) { // special treatment of rdf:ID TODO: still necessary?

				if (triplePartURI.equals(RDF.subject)) {
					sourceValue = mainStatement.getSubject(); // TODO ID actually only fine when URIs!
				} else if (triplePartURI.equals(RDF.predicate)) {
					sourceValue = mainStatement.getPredicate();
				} else if (triplePartURI.equals(RDF.object)) {
					sourceValue = mainStatement.getObject();
				} else {
					throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
				}

				targetValue = svUriTVuriMap.get(sourceValue);

			} else { // other source properties than rdf:ID ...

				Resource superResource;
				TupleSourceValueTargetValue<Node, Node> svWithItsTv;

				if (triplePartURI.equals(RDF.subject)) {
					superResource = mainStatement.getSubject();
				} else if (triplePartURI.equals(RDF.predicate)) {
					superResource = mainStatement.getPredicate();
				} else if (triplePartURI.equals(RDF.object)) {
					try {
						// object could also be a literal
						superResource = (Resource) mainStatement.getObject().asResource();
					} catch (ClassCastException e) {
						throw new UnsupportedMappingParameterValueException(
								"Cannot cast the object of the super-statement (" + mainStatement.getObject()
										+ ") to a resource.");
					}
				} else {
					throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
				}
				ClosableIterator<Statement> stmtIterator = modelSet.findStatements(OGVICProcess.GRAPH_DATA,
						superResource.asResource(), sp.asURI(), Variable.ANY);

				svWithItsTv = lookUpTvForSv(stmtIterator, svUriTVuriMap);

				if (null == svWithItsTv) {
					LOGGER.info("No target value mapped to the object values matching " + superResource + " --" + sp
							+ "--> ?object ." + NL);
					return;
				}

				sourceValue = svWithItsTv.sourceValue;
				targetValue = svWithItsTv.targetValue;
			}

			if (null == targetValue) {
				LOGGER.finest("No target value found for source value " + sourceValue + " ," +
						" parameter sub-mapping cannot be applied.");
			} else {
				applyParameterToGraphicRelation(tga, targetValue, sourceValue, rel);
			}

		} // end if P2GAM
	}

	/**
	 * Called when handling sub-mappings.
	 * NOTE: Needs to properly handle other than P2GAMs. Cases for IdentityMappings and P2GOTORs quickly hacked.
	 *  
	 * @param mainStatement
	 * @param triplePartURI
	 * @param goToApplySubmapping
	 * @param subMapping
	 * @throws InsufficientMappingSpecificationException
	 * @throws MappingException
	 */
	public void applyMappingToGraphicObject(Statement mainStatement, URI triplePartURI,
			GraphicObjectX goToApplySubmapping, PropertyMappingX subMapping)
			throws InsufficientMappingSpecificationException, MappingException {

		Node triplePart;

		if (triplePartURI.equals(RDF.subject)) {
			triplePart = mainStatement.getSubject(); // TODO ID actually only fine when URIs!
		} else if (triplePartURI.equals(RDF.predicate)) {
			triplePart = mainStatement.getPredicate();
		} else if (triplePartURI.equals(RDF.object)) {
			triplePart = mainStatement.getObject();
		} else {
			throw new InsufficientMappingSpecificationException(
					"Only RDF subject/predicate/object allowed as triple part URI.");
		}

		Resource newSubjectResource = triplePart.asResource();

		if (subMapping.isInstanceof(IdentityMappingX.RDFS_CLASS)) { 

			IdentityMappingX idMapping = (IdentityMappingX) subMapping.castTo(IdentityMappingX.class);
			
			new IdentityMappingHandler(modelSet, this, modelAVM).encodeStatement(mainStatement, idMapping, goToApplySubmapping, newSubjectResource);

		} else if (subMapping.isInstanceof(PropertyToGraphicAttributeMappingX.RDFS_CLASS)) {
			
			try {
				
				// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
				PropertyToGraphicAttributeMappingX p2gam = RVLUtils.tryReplaceWithCashedInstanceForSameURI(subMapping, PropertyToGraphicAttributeMappingX.class);
				
				if (!p2gam.hasValuemapping()) {
					throw new InsufficientMappingSpecificationException(
							"P2GA-mappings with no value mappings at all are not supported.");
				}
				
				new MappingToP2GAMHandler(modelSet, this, modelAVM)
					.encodeStatement(mainStatement, p2gam, goToApplySubmapping, newSubjectResource);
				
			} catch (MappingException e) {
				LOGGER.warning("No resources will be affected by sub-mapping " + subMapping.asURI() + " (" + e.getMessage() + ")" );
			} 


		} else if (subMapping.isInstanceof(PropertyToGO2ORMappingX.RDFS_CLASS)) {

			PropertyToGO2ORMappingX p2go2orm = (PropertyToGO2ORMappingX) subMapping.castTo(PropertyToGO2ORMappingX.class);

			// TODO: Refactor VISO/RVL: we have to use the generic type Node here since there is a mismatch between the generated types returned by getTargetGraphicRelation and the superclass of Labeling
			Node targetGraphicRelation = p2go2orm.getTargetGraphicRelation();

			try {
				if (targetGraphicRelation.equals(Labeling.RDFS_CLASS)) {
				
					/*{ // other source properties than rdf:ID
						
						sv = triplePart; // TODO when sp rdf:ID: ID actually only fine when URIs!

						// 1. create the label
						// 2. call the submapping method with the same unchanged statement to set label text_value or
						// icon_shape etc ...

						Statement statement = mainStatement;
						
						LOGGER.fine("Statement to be mapped : " + statement);

						// Resource subject = statement.getSubject();
						Node object = statement.getObject();

						// create an additional object here, don't reuse existing ones!
						GraphicObjectX label = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_LabelLabel_"
								+ this.createNewInternalID(), false);
						
						LOGGER.finest("Created new Label-GO for (Label) object: " + object.toString());
						// TODO when sp rdf:ID: not created for object in all cases?!

						Labeling rel = new Labeling(modelAVM, "http://purl.org/rvl/example-avm/LabelingRelation_"
								+ this.createNewInternalID(), true);
						rel.setLabel(AVMUtils.getGoodNodeLabel(subMapping.getTargetGraphicRelation(), modelAVM));

						goToApplySubmapping.addLabeledwith(rel);
						rel.setLabelinglabel(label);
						rel.setLabelingattachedBy(Superimposition.RDFS_CLASS); // passing a node here
						rel.setLabelingbase(goToApplySubmapping);

						// set default shape of icon labels
						label.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Square", false));

						// submappings
						if (subMapping.hasSub_mapping()) {
							this.applySubmappings(p2go2orm, statement, rel);
						}
					} */

					// OLD: recursive does not yet work (no specific treatment of RDF:ID as a source property!)
					// after changes this seems to work, but in the labeling test the font size is not the same as before. WHY???
					// also the RDFID test fails ( but this seems to fail anyway)
					// passing the goToApplySubmapping it works ... WHY??? It should actually have been retrieved from the cash!?
					new MappingToLabelingHandler(modelSet, this, modelAVM).encodeStatement(mainStatement, p2go2orm, goToApplySubmapping);

				} else {
					throw new MappingException("P2GORM-Submappings other than mappings to Labeling not yet supported.");
				}

			} catch (InsufficientMappingSpecificationException e) {
				throw new MappingException("Problem interpreting submapping " + p2go2orm);
			}

		} else {
			throw new MappingException("Submappings other than P2GAM, P2GORM or Identitymappings not yet supported.");
		}

	}

	/**
	 * Handle inheritance of graphic values via arbitrary relations. All nodes related to the baseResource via the
	 * inheritedBy-relation are given the same tv (target value)
	 * 
	 * @param p2gam
	 * @param tga
	 * @param baseResource
	 * @param tv
	 * @throws InsufficientMappingSpecificationException
	 */
	@Override
	public void applyInheritanceOfTargetValue(PropertyToGraphicAttributeMappingX p2gam, Resource baseResource, Node tv)
			throws InsufficientMappingSpecificationException {

		Property inheritedBy = ((PropertyMappingX) p2gam.castTo(PropertyMappingX.class)).getInheritedBy();

		// temp only support some and allValuesFrom ... // TODO these checks are also done in findRelationsOnClassLevel
		if (null != inheritedBy
				&& !(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
						|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())
						|| inheritedBy.toString().equals(RVL.TBOX_RESTRICTION) || inheritedBy.toString().equals(
						RVL.TBOX_DOMAIN_RANGE))) {
			LOGGER.fine("Mapped value " + tv + " will be inherited to GOs representing nodes related to "
					+ baseResource + "("
					+ AVMUtils.getGoodNodeLabel(baseResource, modelSet.getModel(OGVICProcess.GRAPH_DATA)) + ") via "
					+ inheritedBy);

			GraphicAttribute tga = p2gam.getTargetAttribute();
			applyGraphicValueToGOsRepresentingNodesRelatedVia(tga, tv, baseResource, inheritedBy);

		}
	}

	/**
	 * Iterates through statement objects and returns a tuple consisting of the source value and a corresponding target
	 * value or null when none of the objects matched a source value in the map.
	 * 
	 * 	// TODO Multiple objects may match a mapped target value. For now only the first match will win!
	 *	// TODO: maybe not the most specific is mapped, therefore,
	 *	// we cannot simply check the map for the most specific results.
	 *	// e.g. Europe could be mapped, but Germany not, still the mapping for
	 *	// Europe should be applied (partOf). Or Animal may be mapped but not Cat.
	 *	// TODO: It may also be the case that two most specific values exist, e.g.
	 *	// two classes as types, where one is not a subclass of the other!
	 * 
	 * @param it
	 * @param svUriTVuriMap
	 * @return a tuple of a source and target value or null if no source value matching a target value was found
	 */
	@Override
	public TupleSourceValueTargetValue<Node, Node> lookUpTvForSv(ClosableIterator<Statement> it,
			Map<Node, Node> svUriTVuriMap) {

		TupleSourceValueTargetValue<Node, Node> svWithItsTv = null;
		Node sv;
		
		while (it.hasNext()) {
			sv = it.next().getObject();
			if (svUriTVuriMap.containsKey(sv)) {
				svWithItsTv = new TupleSourceValueTargetValue<Node, Node>(sv, svUriTVuriMap.get(sv));
				return svWithItsTv;
			}
		}

		return svWithItsTv;
	}

}
