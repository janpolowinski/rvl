package org.purl.rvl.tooling.avm;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.java.viso.graphic.GraphicSpace;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class D3JacksonBinding extends D3GeneratorBase {

	private ObjectMapper m_mapper;

	public D3JacksonBinding() {
		// Create Jackson object mapper
		m_mapper = new ObjectMapper();
		
		m_mapper = new ObjectMapper();
		final JaxbAnnotationModule module = new JaxbAnnotationModule();
		m_mapper.registerModule(module)
				.setSerializationInclusion(Include.NON_NULL)
				.setSerializationInclusion(Include.NON_EMPTY);
	}


	/**
	 * @param model
	 * @param modelVISO
	 */
	public D3JacksonBinding(Model model, Model modelVISO) {
		super(model, modelVISO);
	}


	public void writeGraphicSpaceAsJSON(GraphicSpace graphicSpace, File file)
			throws IOException {
		//m_mapper.writeValue(file, graphicSpace);
		// pretty print instead using a writer
		ObjectWriter writer = m_mapper.writerWithDefaultPrettyPrinter();
		writer.writeValue(file, graphicSpace);
	}
}