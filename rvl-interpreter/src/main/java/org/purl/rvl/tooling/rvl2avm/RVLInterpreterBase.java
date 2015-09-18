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
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.exception.UnsupportedMappingParameterValueException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Shape;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.MappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.rvl.SubMappingRelationX;
import org.purl.rvl.java.rvl.mapping.TupleSourceValueTargetValue;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.commons.Graph;
import org.purl.rvl.tooling.commons.utils.AVMUtils;
import org.purl.rvl.tooling.commons.utils.ModelUtils;
import org.purl.rvl.tooling.model.ModelManager;
import org.purl.rvl.tooling.query.data.DataQuery;

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

//	protected OGVICProcess ogvicProcess = OGVICProcess.getInstance();

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
	public void init(ModelManager modelManager) {
		this.modelAVM = modelManager.getAVMModel();
		this.modelSet = modelManager.getModelSet();
		this.modelData = modelSet.getModel(Graph.GRAPH_DATA);
		this.modelMappings = modelSet.getModel(Graph.GRAPH_MAPPING);
		this.modelVISO = modelSet.getModel(Graph.GRAPH_VISO);
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
			go = ModelUtils.tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(go, GraphicObjectX.class);

			go.setRepresentedResource(resource);

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
				
					// create a default label, if no other TEXT label already exists (with text_value attribute)
					if (!go.hasTextLabel()) {
							performDefaultLabelMapping(go, resource);
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
		GraphicObjectX label = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_"
				+ this.createNewInternalID(), true);
		label.setTextvalue(ModelUtils.getGoodNodeLabel(resource, modelData));

		LOGGER.finest("Created new Label-GO for resource: " + resource.toString());

		Labeling rel = new Labeling(modelAVM, "http://purl.org/rvl/example-avm/LabelingRel_"
				+ createNewInternalID(), true);
		
//		rel.setLabel("Labeling Relation");
		rel.setLabelinglabel(label);
		rel.setLabelingattachedBy(Superimposition.RDFS_CLASS);
		rel.setLabelingbase(go);

		go.addLabeledwith(rel);

	}
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#applySubmappings(org.purl.rvl.java.rvl.PropertyToGO2ORMappingX, org.ontoware.rdf2go.model.Statement, org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation)
	 */
	@Override
	public void applySubmappings(PropertyMappingX mapping,
			Statement mainStatement, Object_to_ObjectRelation graphicRelation) throws SubmappingException {
		
		applySubmappings(mapping, mainStatement, graphicRelation, null);
	}
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#applySubmappings(org.purl.rvl.java.rvl.PropertyToGO2ORMappingX, org.ontoware.rdf2go.model.Statement, org.purl.rvl.java.viso.graphic.GraphicObjectX)
	 */
	@Override
	public void applySubmappings(PropertyMappingX p2go2orm,
			Statement mainStatement, GraphicObjectX parentGO) throws SubmappingException {
		
		applySubmappings(p2go2orm, mainStatement, null, parentGO);
	}

	/**
	 * @throws SubmappingException 
	 * Applies sub-mappings (if any exist) based on a "main" statement. An existing GO to append the sub-mapping, as
	 * well as a triple part (S,P,O) of the main statement (as a base for the mapping) is determined by the sub-mapping
	 * relation (if not provided). TODO: linking-specific! only works on top of P2GOTORMs and only for sub-mappings that are P2GAMs
	 * 
	 * @param p2go2orm
	 * @param mainStatement
	 * @param dlRel
	 * @throws
	 */
	@Override
	public void applySubmappings(PropertyMappingX mapping, Statement mainStatement,
			Object_to_ObjectRelation graphicRelation, GraphicObjectX goToApplySubmappingArg) throws SubmappingException {

		// TODO derive GO by onRole settings and the mainStatement? or just check if correct?

		Set<SubMappingRelationX> subMappingRelations = mapping.getSubMappings();

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
			PropertyMappingX subMappingPM = (PropertyMappingX) subMapping;

			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			// not already here, we need to know first, what kind of mapping we want to receive P2GAM, P2GOTORM? ... ) : 
			// mapping = RVLUtils.tryReplaceWithCashedInstanceForSameURI(mapping, mapping.getClass());

			try {

				if (roleURI.toString().equals("http://purl.org/viso/graphic/this")) {

					// apply submapping to this relation itself
					
					// currently we also allow submapping on the relation itself to "parameterize" the n-ary
					// relationship, e.g. to set the attachment type for labeling relations

					LOGGER.finer("Applying submapping to the GR itself (role: " + roleURI + ") based on triple part "
							+ triplePartURI);

					applySubmappingToNaryRelation(mainStatement, triplePartURI, graphicRelation, subMappingPM);

				} else {
					
					final GraphicObjectX goToApplySubmapping;
					
					if (null == graphicRelation && null != goToApplySubmappingArg) {
					
						// apply submapping to the parent GO (this is needed for mappings to graphic attributes,
						// where not graphic relation is created)
						
						goToApplySubmapping = goToApplySubmappingArg;
						
						LOGGER.finer("Applying submapping to parent GO " + goToApplySubmapping + " based on triple part "
								+ triplePartURI);
						
					} else {

						// otherwise apply it to a GO with the given role ...
	
						LOGGER.finer("Applying submapping to GO with the role " + roleURI + " based on triple part "
								+ triplePartURI);

						goToApplySubmapping = AVMUtils.getGOForRole(modelAVM, graphicRelation, roleURI);
						// TODO this is a simplification: multiple GOs may be affected, not only one
					}
					
					applySubmappingToGraphicObject(mainStatement, triplePartURI, goToApplySubmapping, subMappingPM);
					// this does not use the cashed mappings somehow:
					// goToApplySubmapping.setLabel(roleURI + " with an applied submapping: " + smr.toStringSummary());					
				}

			} catch (InsufficientMappingSpecificationException e) {

				throw new SubmappingException("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());

			} catch (UnsupportedMappingParameterValueException e) {

				throw new SubmappingException("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());

			} catch (MappingException e) {

				throw new SubmappingException("Submapping " + subMappingPM + " could not be applied. Reason: " + e.getMessage());
			}

		}

	}

	/**
	 * NOTE: Cases for IdentityMappings and P2GOTORs quickly hacked.
	 *  
	 * @param mainStatement
	 * @param triplePartURI
	 * @param graphicObjToApplySubmapping
	 * @param subMapping
	 * @throws InsufficientMappingSpecificationException
	 * @throws MappingException
	 */
	public void applySubmappingToGraphicObject(Statement mainStatement, URI triplePartURI,
			GraphicObjectX graphicObjToApplySubmapping, PropertyMappingX subMapping)
			throws InsufficientMappingSpecificationException, MappingException {
	
		Resource newWorkResource = determineWorkResource(mainStatement, triplePartURI);
	
		if (subMapping instanceof IdentityMappingX) { 
	
			IdentityMappingX idMapping = (IdentityMappingX) subMapping;
			
			new IdentityMappingHandler(modelSet, this, modelAVM)
				.encodeStatement(mainStatement, idMapping, graphicObjToApplySubmapping, newWorkResource);
	
		} else if (subMapping instanceof PropertyToGraphicAttributeMappingX) {
	
			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			//PropertyToGraphicAttributeMappingX p2gam = RVLUtils.tryReplaceWithCashedInstanceForSameURI(subMapping, PropertyToGraphicAttributeMappingX.class);
			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) subMapping;
			
			if (!p2gam.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"P2GA-mappings with no value mappings at all are not supported.");
			}
			
			new MappingToP2GAMHandler(modelSet, this, modelAVM)
				.handleP2GAMMapping(p2gam, graphicObjToApplySubmapping, newWorkResource);
	
		} else if (subMapping instanceof PropertyToGO2ORMappingX ) {
	
			PropertyToGO2ORMappingX p2go2orm = (PropertyToGO2ORMappingX) subMapping;
	
			// TODO: Refactor VISO/RVL: we have to use the generic type Node here since there is a mismatch between the
			// generated types returned by getTargetGraphicRelation and the superclass of Labeling
			Node targetGraphicRelation = p2go2orm.getTargetGraphicRelation();
	
			// TODO code from SimpleRVLInterpreter->interpretP2GOTORMappings duplicated here 
			// since it needs to use encodeStatement(...) instead of handle..()...
			if (targetGraphicRelation.equals(Labeling.RDFS_CLASS)) {
				new MappingToLabelingHandler(modelSet, this, modelAVM)
					.encodeStatement(mainStatement, p2go2orm, graphicObjToApplySubmapping);
			}
			else if (targetGraphicRelation.equals(DirectedLinking.RDFS_CLASS)
					|| targetGraphicRelation.equals(UndirectedLinking.RDFS_CLASS)) {
				new MappingToLinkingHandler(modelSet, this, modelAVM)
					.handleP2GOTORMapping(p2go2orm);
					//.encodeStatement(mainStatement, p2go2orm, graphicObjToApplySubmapping); // TODO
			}
//			else if (targetGraphicRelation.equals(UndirectedLinking.RDFS_CLASS)) {
//				LOGGER.info("Ignored Mapping to Undirected Linking. Undirected Linking not yet implemented");
//			}
			else if (targetGraphicRelation.equals(Containment.RDFS_CLASS)) {
				new MappingToContainmentHandler(modelSet, this, modelAVM).handleP2GOTORMapping(p2go2orm);
			}
			else {
				throw new NotImplementedMappingFeatureException("Submappings to the graphic relation " 
						+ targetGraphicRelation + " not yet implemented.");
			}
	
		} else {
			throw new NotImplementedMappingFeatureException(
					"Submappings other than P2GAM, P2GORM or Identitymappings not yet supported.");
		}
	
	}

	/**
	 * Hack: Copied from applyMappingToGraphicObject() to allow for "parameterizing" n-ary-relations by submappings.
	 * 
	 * @param mainStatement
	 * @param triplePartURI
	 * @param rel
	 * @param mapping
	 * @throws UnsupportedMappingParameterValueException
	 * @throws MappingException 
	 */
	public void applySubmappingToNaryRelation(Statement mainStatement, URI triplePartURI, Object_to_ObjectRelation rel,
			PropertyMappingX mapping) throws UnsupportedMappingParameterValueException, MappingException {
	
			// there is not yet a special parameter-mapping, so we use P2GAMs here
			if (!(mapping instanceof PropertyToGraphicAttributeMappingX)) {
				
				throw new MappingException("Use P2GAM for value mappings of parameters until " +
						"a special paramter mapping class exists. Other mapping types are not supported.");
			}
	
			final PropertyToGraphicAttributeMappingX parameterMapping = (PropertyToGraphicAttributeMappingX) mapping;
	
			final Property targetParameter = parameterMapping.getTargetGraphicRelation();
	
			if (null == targetParameter) {
				throw new InsufficientMappingSpecificationException("No target parameter set.");
			}
	
			if (!parameterMapping.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"Parameter mappings with no value mappings at all are not yet supported" +
						" (defaults needs to be implemented).");
			}

			Resource newWorkResource = determineWorkResource(mainStatement, triplePartURI);
			
			new ParameterMappingHandler(modelSet, this, modelAVM)
				.handleParameterMapping(parameterMapping, rel, newWorkResource);

	}

	@Override
	public void applyGraphicValueToGO(Property tga, Node tv, Node sv, GraphicObjectX go) {

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
			
			// if we are mapping to area // TODO concurrent setting of width OR height should be allowed
			if (tga.asURI().toString().equals("http://purl.org/viso/graphic/area")) {
				Float area = new Float(tv.asLiteral().getValue());
				go.setArea(area);
				Float sqrtOfArea = new Float(2*Math.sqrt(area/Math.PI)); // TODO simplified: assuming a circle shape all the time
				go.setWidth(sqrtOfArea);
//				go.setHeight(sqrtOfArea);
				LOGGER.finer("Set area to float value " + area + " for sv " + sv + " and manipulated width." + NL);
			}

		}

		else {
			LOGGER.warning("Could not set target value, since one of the required parameters was null.");
		}
	}

	/**
	 * @param tga - the graphic attribute to set
	 * @param tv - the graphic attribute value to apply
	 * @param mappedNode - the node the mapping was originally applied to
	 * @param inheritedBy - the required relation between the mapped node and nodes inheriting the mapping
	 */
	@Override
	public void applyGraphicValueToGOsRepresentingNodesRelatedVia(Property tga, Node tv, Resource mappedNode,
			Property inheritedBy) {

		Set<Resource> relatedResources = DataQuery.getRelatedResources(modelSet, mappedNode, inheritedBy);

		LOGGER.finest("related resources " + relatedResources.toString() + " will receive same tv (" + tv + ")");

		for (Resource resource : relatedResources) {
			applyGraphicValueToGO(tga, tv, mappedNode, createOrGetGraphicObject(resource));
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

		Property inheritedBy = ((PropertyMappingX) p2gam).getInheritedBy();

		// temp only support some and allValuesFrom ... // TODO these checks are also done in findRelationsOnClassLevel
		if (null != inheritedBy
				&& !(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
						|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())
						|| inheritedBy.toString().equals(RVL.TBOX_RESTRICTION) || inheritedBy.toString().equals(
						RVL.TBOX_DOMAIN_RANGE))) {
			LOGGER.fine("Mapped value " + tv + " will be inherited to GOs representing nodes related to "
					+ baseResource + "("
					+ ModelUtils.getGoodNodeLabel(baseResource, modelSet.getModel(Graph.GRAPH_DATA)) + ") via "
					+ inheritedBy);

			Property tga = p2gam.getTargetGraphicRelation();
			applyGraphicValueToGOsRepresentingNodesRelatedVia(tga, tv, baseResource, inheritedBy);

		}
	}

	/**
	 * TODO is this still necessary? A similar mechanism is to extend the (value) mapping table, which works already for subClassOf.
	 * @seeAlso org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX.getExtendedMappedValues(Sparqlable, Property)
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
	 * @param iterator
	 * @param svUriTVuriMap
	 * @return a tuple of a source and target value or null if no source value matching a target value was found
	 */
	protected static TupleSourceValueTargetValue<Node, Node> lookUpTvForSv(final Iterator<Statement> iterator, final
			Map<Node, Node> svUriTVuriMap) {

		TupleSourceValueTargetValue<Node, Node> svWithItsTv = null;
		Node sourceValue;
		
		while (iterator.hasNext()) {
			sourceValue = iterator.next().getObject();
			if (svUriTVuriMap.containsKey(sourceValue)) {
				svWithItsTv = new TupleSourceValueTargetValue<Node, Node>(sourceValue, svUriTVuriMap.get(sourceValue));
				return svWithItsTv;
			}
		}

		return svWithItsTv;
	}

	/**
	 * @param statement
	 * @param triplePartURI
	 * @param triplePart
	 * @return
	 * @throws InsufficientMappingSpecificationException
	 */
	static protected Node determineTriplePart(Statement statement, URI triplePartURI)
			throws InsufficientMappingSpecificationException {
		
		Node triplePart;
		
		if (triplePartURI.equals(RDF.subject)) {
			triplePart = statement.getSubject(); // TODO ID actually only fine when URIs!
		} else if (triplePartURI.equals(RDF.predicate)) {
			triplePart = statement.getPredicate();
		} else if (triplePartURI.equals(RDF.object)) {
			triplePart = statement.getObject();
		} else {
			throw new InsufficientMappingSpecificationException(
					"Only subject/predicate/object allowed as triple part URI. Was " + triplePartURI);
		}
		return triplePart;
	}

	/**
	 * @param statement
	 * @param triplePartURI
	 * @return
	 * @throws InsufficientMappingSpecificationException
	 * @throws MappingException
	 */
	static protected Resource determineWorkResource(Statement statement, URI triplePartURI)
			throws InsufficientMappingSpecificationException, MappingException {
		
		Node triplePart = determineTriplePart(statement, triplePartURI);
		
		Resource newWorkResource;
		
		// object could also be a literal
		try {
			newWorkResource = triplePart.asResource();
		} catch (ClassCastException e) {
			throw new MappingException("Triple part " + triplePart + " cannot be used for submappings," +
					" since it is not a ressource: " + e.getMessage());
		}
		return newWorkResource;
	}

}
