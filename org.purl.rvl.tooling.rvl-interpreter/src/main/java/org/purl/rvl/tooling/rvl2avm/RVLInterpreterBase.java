package org.purl.rvl.tooling.rvl2avm;

import java.util.HashMap;
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
import org.purl.rvl.exception.UnsupportedMappingParameterValueException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Shape;
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
	protected Map<Resource,GraphicObjectX> resourceGraphicObjectMap; 
	protected Random random;
	
	protected OGVICProcess ogvicProcess = OGVICProcess.getInstance();

	
	private final static Logger LOGGER = Logger.getLogger(RVLInterpreterBase.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	public RVLInterpreterBase() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreter#init(org.ontoware.rdf2go.model.Model, org.ontoware.rdf2go.model.ModelSet)
	 */
	public void init(
		Model modelAVM, ModelSet modelSet) {
		this.modelAVM = modelAVM;
		this.modelSet = modelSet;
		this.modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);
		this.modelMappings = modelSet.getModel(OGVICProcess.GRAPH_MAPPING);
		this.modelVISO = modelSet.getModel(OGVICProcess.GRAPH_VISO);
		this.random = new Random();
		this.resourceGraphicObjectMap = new HashMap<org.ontoware.rdf2go.model.node.Resource, GraphicObjectX>();
	}


	/* (non-Javadoc)
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
	 * @param resource
	 * @return the GraphicObjectX representing the resource
	 */
	@Override
	public GraphicObjectX createOrGetGraphicObject(org.ontoware.rdf2go.model.node.Resource resource) {
		
		if (resourceGraphicObjectMap.containsKey(resource)) {
			
			LOGGER.finest("Found existing GO for " + resource);
			return resourceGraphicObjectMap.get(resource);
		} 
		else {
			
			GraphicObjectX go = new GraphicObjectX(modelAVM,"http://purl.org/rvl/example-avm/GO_" + random.nextInt(), true);
			
			// add to cache
			go = go.tryReplaceWithCashedInstanceForSameURI(go);
			
			go.setRepresents(resource);
			
			resourceGraphicObjectMap.put(resource, go);
			
			LOGGER.finer("Newly created GO for " + resource);
			
			return go;
		}
	}

	@Override
	public String createNewInternalID() {
		return random.nextInt()+"";
	}


	
	
	/*
	protected Set<PropertyToGO2ORMappingX> getAllMappingsToLinking() {
		
		Set<PropertyToGO2ORMappingX> mappingSet = new HashSet<PropertyToGO2ORMappingX>();

		String queryString = "" +
				"SELECT DISTINCT ?mapping " +
				"WHERE { " +
				"    ?mapping a <" + PropertyToGO2ORMappingX.RDFS_CLASS + "> . " +
				"    ?mapping <" + PropertyToGO2ORMappingX.TARGETOBJECT_TO_OBJECTRELATION + "> <" + DirectedLinking.RDFS_CLASS + "> . " +
				"} " ;
		
		LOGGER.finer("SPARQL: query all mappings to Directed Linking:" + NL + 
				     queryString);
		
		QueryResultTable results = model.sparqlSelect(queryString);
		//for(QueryRow row : results) {LOGGER.info(row); }
		//for(String var : results.getVariables()) { LOGGER.info(var); }
		
		for(QueryRow row : results) {
			Property_to_Graphic_Object_to_Object_RelationMapping mapping = Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(model, (URI)row.getValue("mapping"));
			mappingSet.add((PropertyToGO2ORMappingX)mapping.castTo(PropertyToGO2ORMappingX.class));
			//LOGGER.info("Found mapping to linking: " + row.getValue("mapping").toString());
		}
		
		return mappingSet;
	}
	
	*/
	
	/**
	 * Iterates through all GOs in the GO map and performs a default label mapping on them
	 */
	protected void interpretResourceLabelAsGOLabelForAllCreatedResources(){
		for (Map.Entry<org.ontoware.rdf2go.model.node.Resource,GraphicObjectX> entry : resourceGraphicObjectMap.entrySet()) {
			//LOGGER.info(entry.getKey() + " with value " + entry.getValue());
			// perform the default label mapping, when not already set
		    // TODO this is simply using rdfs:label of the GOs now, not the n-ary graphic labeling!
		    // only rdfreactor resources have labels ...
			GraphicObjectX go = entry.getValue();
			org.ontoware.rdf2go.model.node.Resource resource = entry.getKey();
			if(!go.hasLabels()) {
				performDefaultLabelMapping(go,resource);
			}
		}
	}
	
	/**
	 * Sets the label of a GO to the resources (first) label
	 * @param go
	 * @param resource
	 */
	private void performDefaultLabelMapping(GraphicObjectX go,
			org.ontoware.rdf2go.model.node.Resource resource) {
		
		//LOGGER.finest("Problems getting represented resource, no label generated for GO " + this.asURI());

		try {
			go.setLabel(AVMUtils.getOrGenerateDefaultLabelString(modelData, resource));
		} catch (Exception e) {
			LOGGER.finest("No label could be assigned for resource " + resource + " to GO " + go.asURI().toString() + e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Applies sub-mappings (if any exist) based on a "main" statement. An existing GO to
	 * append the sub-mapping, as well as a triple part (S,P,O) of the main
	 * statement (as a base for the mapping) is determined by the sub-mapping
	 * relation. TODO: linking-specific! only works on top of P2GOTORMs and 
	 * only for sub-mappings that are P2GAMs
	 * 
	 * @param p2go2orm
	 * @param mainStatement
	 * @param dlRel
	 * @throws  
	 */
	@Override
	public void applySubmappings(PropertyToGO2ORMappingX p2go2orm, Statement mainStatement, Resource dlRel) {
		
		// TODO derive GO by onRole settings and the mainStatement? or just check if correct?
	
		Set<SubMappingRelationX> subMappingRelations = p2go2orm.getSubMappings();
		
		for (Iterator<SubMappingRelationX> iterator = subMappingRelations.iterator(); iterator
				.hasNext();) {
	
			SubMappingRelationX smr = (SubMappingRelationX) iterator.next();
	
			URI roleURI = smr.getOnRole().asURI();
			URI triplePartURI = smr.getOnTriplePart().asURI();
						
			// modelAVM.findStatements(dlRel,role,Variable.ANY); does not work somehow -> Jena mapping problems
			
			MappingX subMapping = smr.getSubMapping();
			
			if (subMapping.isDisabled()) {
				//LOGGER.info("The referenced submapping was disabled. Will ignore it");
				LOGGER.info("The referenced submapping was disabled but will still be used." +
						" TODO: implement 3 status ENABLED (default), DISABLED, USE-ONLY-AS-SUBMAPPING");
				//continue;
			}
	
			// TODO can also be another P2GO2OR-mapping
			PropertyToGraphicAttributeMappingX p2gam = 
					(PropertyToGraphicAttributeMappingX) subMapping.castTo(PropertyToGraphicAttributeMappingX.class);
			
			// check if already cached in the extra java object cache for resource (rdf2go itself is stateless!)
			p2gam = p2gam.tryReplaceWithCashedInstanceForSameURI(p2gam);
			
			try {
				
				if (roleURI.toString().equals("http://purl.org/viso/graphic/this")) {
					
					// apply submapping to this relation itself?
					//
					// currently we also allow submapping on the relation itself to "parameterize" the n-ary relationship, 
					// e.g. to set the attachment type for labeling relations
					
					LOGGER.finer("Applying submapping to the GR itself (role: " + roleURI + 
							") based on triple part " + triplePartURI);
					
					applyMappingToNaryRelation(mainStatement, triplePartURI, dlRel, p2gam);
					
				} else {
					
					// otherwise apply it to a GO with the given role ...
					
					LOGGER.finer("Applying submapping to GO with the role " + roleURI + 
							" based on triple part " + triplePartURI);
	
					GraphicObjectX goToApplySubmapping = AVMUtils.getGOForRole(modelAVM, dlRel, roleURI); 
					// TODO this is a simplification: multiple GOs may be affected, not only one
	
					applyMappingToGraphicObject(mainStatement, triplePartURI, goToApplySubmapping, p2gam);
					// this does not use the cashed mappings somehow:
					//goToApplySubmapping.setLabel(roleURI + " with an applied submapping: " + smr.toStringSummary());
						
				}
			
			} catch (InsufficientMappingSpecificationException e) {
				
				LOGGER.warning("Submapping " + p2gam + " could not be applied. Reason: " + e.getMessage());
				
			} catch (UnsupportedMappingParameterValueException e) {
				
				LOGGER.warning("Submapping " + p2gam + " could not be applied. Reason: " + e.getMessage());
			}
			
		}
		
	}


	@Override
	public void applyGraphicValueToGO(GraphicAttribute tga, Node tv,
			Node sv, GraphicObjectX go) {
				
				if (null != tga && null != tv && null != sv && null != go ) {
					
					LOGGER.finest("Setting tv " + tv + " for sv " + sv);
					
					// if we are mapping to named colors
				    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
				    	Color color = Color.getInstance(modelVISO, tv.asURI());
				    	go.setColornamed(color);
				    	LOGGER.finer("Set color named to " + color + " for sv " + sv);
				    }
				    
					// if we are mapping to lightness
				    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_hsl_lightness")) {
				    	go.setColorhsllightness(tv);
				    	LOGGER.finer("Set color hsl lightness to " + tv.toString() + " for sv " + sv);
				    }
				    
					// if we are mapping to named shapes
				    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
				    	Shape shape = ShapeX.getInstance(modelVISO, tv.asURI());
				    	go.setShapenamed(shape);
				    	LOGGER.finer("Set shape to " + shape + " for sv " + sv + NL);
				    }
				    
					// if we are mapping to width
				    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/width")) {
				    	Float width = new Float(tv.asLiteral().getValue());
				    	go.setWidth(width);
				    	LOGGER.finer("Set width to float value " + width + " for sv " + sv + NL);
				    }
				    
					// if we are mapping to labeling_attachedBy
				    if(tga.asURI().equals(Labeling.LABELINGATTACHEDBY)) {
				    	GraphicObjectToObjectRelation attachementRelation = GraphicObjectToObjectRelation.getInstance(modelVISO, tv.asURI());
				    	Labeling nAryLabeling = new Labeling(modelAVM, true);
				    	nAryLabeling.setLabelingattachedBy(attachementRelation);
				    	go.setLabeledwith(nAryLabeling);
				    	LOGGER.finer("Set labeling attachment to " + attachementRelation + " for sv " + sv + NL);
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
	public void applyGraphicValueToGOsRepresentingNodesRelatedVia(
			GraphicAttribute tga, Node tv, Resource mappedNode, Property inheritedBy) {
				
					Set<Resource> relatedResources = DataQuery.getRelatedResources(modelSet, mappedNode, inheritedBy);
					
					LOGGER.finest("related resources " + relatedResources.toString() + " will receive same tv (" + tv + ")");
					
					for (Resource resource : relatedResources) {
						applyGraphicValueToGO(tga, tv, mappedNode, createOrGetGraphicObject(resource));
					}
			}


	/**
	 * Hack: Copied from applyMappingToGraphicObject() to allow for "parameterizing" n-ary-relations by submappings. 
	 * Incomplete. will always set setLabelingattachedBy and ignore the tga!
	 * 
	 * @param mainStatement
	 * @param triplePartURI
	 * @param rel
	 * @param p2gam
	 * @throws InsufficientMappingSpecificationException
	 * @throws UnsupportedMappingParameterValueException 
	 */
	public void applyMappingToNaryRelation(Statement mainStatement, URI triplePartURI,
			Resource rel, PropertyToGraphicAttributeMappingX p2gam)
			throws InsufficientMappingSpecificationException,
			UnsupportedMappingParameterValueException {
				
				GraphicAttribute tga = p2gam.getTargetAttribute();
				Property sp = p2gam.getSourceProperty();
			
				// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings instead
			
				if (null == tga) {
					throw new InsufficientMappingSpecificationException("no target graphic attribute set");
				}
				
				if (!p2gam.hasValuemapping()) {
					throw new InsufficientMappingSpecificationException("P2GAM with no value mappings at all are not yet supported (defaults needs to be implemented).");
				}
				
					Map<Node,Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
					
					if (null == svUriTVuriMap || svUriTVuriMap.isEmpty()) {
						LOGGER.severe("Could not apply submappings since (explicit or calculated) value mappings were null");
						return;
					} else {
						LOGGER.finer(p2gam.explicitlyMappedValuesToString());
					}
					
					Node sv = null,
						 tv = null;
					
					if (sp.asURI().equals(RDF.ID)) { // special treatment of rdf:ID
						
						if (triplePartURI.equals(RDF.subject)) {
							sv = mainStatement.getSubject(); // TODO ID actually only fine when URIs!
						}
						else if (triplePartURI.equals(RDF.predicate)) {
							sv = mainStatement.getPredicate();
						}
						else if (triplePartURI.equals(RDF.object)) {
							sv = mainStatement.getObject();
						} else {
							throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
						}
			
						tv = svUriTVuriMap.get(sv);
						
					} else { // other source properties than rdf:ID ...
						
						Resource superResource;
						TupleSourceValueTargetValue<Node,Node> svWithItsTv;
			
						if (triplePartURI.equals(RDF.subject)) {
							superResource = mainStatement.getSubject();
						} else if (triplePartURI.equals(RDF.predicate)) {
							superResource = mainStatement.getPredicate();					
						} else if (triplePartURI.equals(RDF.object)) {
							try { 
								// object could also be a literal
								superResource = (Resource) mainStatement.getObject().asResource();
							} catch (ClassCastException e) {
								throw new UnsupportedMappingParameterValueException("Cannot cast the object of the super-statement (" + mainStatement.getObject() + ") to a resource. Skipping query generation for sub-mapping. ");
							}
						} else {
							throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
						}
						ClosableIterator<Statement> stmtIterator = modelSet.findStatements(OGVICProcess.GRAPH_DATA, superResource.asResource(), sp.asURI(), Variable.ANY);		
								
						svWithItsTv = lookUpTvForSv(stmtIterator, svUriTVuriMap);
			
						if (null == svWithItsTv) {
							LOGGER.info("No target value mapped to the object values matching " + superResource + " --" + sp + "--> ?object ." + NL);
							return;
						} 
			
						sv = svWithItsTv.sourceValue;
						tv = svWithItsTv.targetValue; 
					}
			
					if (null == tv) {
						LOGGER.info("No target value found, parameter sub-mapping cannot be applied.");
					} else {
						// we found a tv for the sv
						((Labeling)rel).setLabelingattachedBy(tv); // TODO only works for this specific tga! 
					}	
			}


	public void applyMappingToGraphicObject(Statement mainStatement, URI triplePartURI,
			GraphicObjectX goToApplySubmapping, PropertyToGraphicAttributeMappingX p2gam)
			throws InsufficientMappingSpecificationException {
				
				GraphicAttribute tga = p2gam.getTargetAttribute();
				Property sp = p2gam.getSourceProperty();
			
				// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings instead
				
				if (null == tga) {
					throw new InsufficientMappingSpecificationException("no target graphic attribute set");
				}
				
				if (!p2gam.hasValuemapping()) {
					throw new InsufficientMappingSpecificationException("Parameter mappings with no value mappings at all are not supported.");
				}
			
				
				Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
				
				if (null == svUriTVuriMap || svUriTVuriMap.isEmpty()) {
					LOGGER.severe("Could not apply submappings since (explicit or calculated) value mappings were null");
					return;
				} else {
					LOGGER.finer(p2gam.explicitlyMappedValuesToString());
				}
			
				Node sv = null,
				     tv = null;
				
				if (sp.asURI().equals(RDF.ID)) { // special treatment of rdf:ID
					
					if (triplePartURI.equals(RDF.subject)) {
						sv = mainStatement.getSubject(); // TODO ID actually only fine when URIs!
					}
					else if (triplePartURI.equals(RDF.predicate)) {
						sv = mainStatement.getPredicate();
					}
					else if (triplePartURI.equals(RDF.object)) {
						sv = mainStatement.getObject();
					} else {
						throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
					}
			
					tv = svUriTVuriMap.get(sv);
					
				} else { // other source properties than rdf:ID ...
					
					TupleSourceValueTargetValue<Node,Node> svWithItsTv;
					ClosableIterator<Statement> it;
					
					if (triplePartURI.equals(RDF.subject)) {
						it = modelSet.findStatements(OGVICProcess.GRAPH_DATA, mainStatement.getSubject().asResource(), sp.asURI(), Variable.ANY);	
					} else if (triplePartURI.equals(RDF.object)) {
						it = modelSet.findStatements(OGVICProcess.GRAPH_DATA, mainStatement.getObject().asResource(), sp.asURI(), Variable.ANY);						
					} else if (triplePartURI.equals(RDF.predicate)) {
						it = modelSet.findStatements(OGVICProcess.GRAPH_DATA, mainStatement.getPredicate().asResource(), sp.asURI(), Variable.ANY);	
					} else {
						throw new InsufficientMappingSpecificationException("only subject/predicate/object allowed");
					}
			
					try {
						
						// TODO Multiple objects may match a mapped target value. For now only the first match will win!
						svWithItsTv = lookUpTvForSv(it, svUriTVuriMap);
						sv = svWithItsTv.sourceValue;
						tv = svWithItsTv.targetValue;
						
					} catch (Exception e) {
						LOGGER.severe("Could not get value for source property " + sp + "for object " + mainStatement.getObject() );
						return;
					}				
					
			}
			
				
				// if we found a tv for the sv
				if (null != tv && null != sv) {
					
					applyGraphicValueToGO(tga, tv, sv, goToApplySubmapping);
					
					applyInheritanceOfTargetValue(p2gam, mainStatement.getSubject(), tv); 
					
				} else {
					LOGGER.finest("Source or target value was null, couldn't apply graphic value " + tv + " to the sv " + sv);
				}
			
			}


	/**
	 * Handle inheritance of graphic values via arbitrary relations. All nodes
	 * related to the baseResource via the inheritedBy-relation are given the
	 * same tv (target value)
	 * 
	 * @param p2gam
	 * @param tga
	 * @param baseResource
	 * @param tv
	 * @throws InsufficientMappingSpecificationException
	 */
	public void applyInheritanceOfTargetValue(PropertyToGraphicAttributeMappingX p2gam, Resource baseResource,
			Node tv) throws InsufficientMappingSpecificationException {
			
				Property inheritedBy = ((PropertyMappingX)p2gam.castTo(PropertyMappingX.class)).getInheritedBy();
				
				// temp only support some and allValuesFrom ... // TODO these checks are also done in findRelationsOnClassLevel
				if (null!=inheritedBy && !(inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
						|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())	
						|| inheritedBy.toString().equals(RVL.TBOX_RESTRICTION)
						|| inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)	
						)) {
					LOGGER.fine("Mapped value " + tv + " will be inherited to GOs representing nodes related to " + 
						baseResource + "("+ AVMUtils.getGoodLabel(baseResource, modelSet.getModel(OGVICProcess.GRAPH_DATA)) +") via " + inheritedBy);
				
					GraphicAttribute tga = p2gam.getTargetAttribute();
					applyGraphicValueToGOsRepresentingNodesRelatedVia(tga, tv, baseResource, inheritedBy);
					
				}
			}


	/**
	 * Iterates through statement objects and returns a tuple 
	 * consisting of the source value and a corresponding target 
	 * value or null when none of the objects matched a source value 
	 * in the map.
	 * 
	 * @param it
	 * @param svUriTVuriMap
	 * @return a tuple of a source and target value or null
	 */
	public TupleSourceValueTargetValue<Node,Node> lookUpTvForSv(ClosableIterator<Statement> it, Map<Node, Node> svUriTVuriMap) {
		
		TupleSourceValueTargetValue<Node,Node> svWithItsTv = null;
		Node sv;
		
		// TODO Multiple objects may match a mapped target value. For now only the first match will win!
		// TODO: maybe not the most specific is mapped, therefore, 
		// we cannot simply check the map for the most specific results.
		// e.g. Europe could be mapped, but Germany not, still the mapping for
		// Europe should be applied (partOf). Or Animal may be mapped but not Cat.
		// TODO: It may also be the case that two most specific values exist, e.g. 
		// two classes as types, where one is not a subclass of the other!
		
		while (it.hasNext()) {
			sv = it.next().getObject();
			if (svUriTVuriMap.containsKey(sv)) { 
				svWithItsTv = new TupleSourceValueTargetValue<Node,Node>(sv, svUriTVuriMap.get(sv));
				return svWithItsTv;
			}
		}
		
		return svWithItsTv;
	}

}
