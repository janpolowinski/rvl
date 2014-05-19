package org.purl.rvl.tooling.avm2d3;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.purl.rvl.tooling.process.OGVICProcess;


/**
 * @author Jan Polowinski
 *
 */
public abstract class D3GeneratorBase implements D3Generator {
	
	protected static final String NL = System.getProperty("line.separator");

	protected Model modelAVM;
	protected Model modelVISO;
	
	private final static Logger LOGGER = Logger.getLogger(D3GeneratorBase.class .getName()); 
	
	
	public D3GeneratorBase() {
		super();
	}

	
	/**
	 * @param modelAVM
	 * @param modelVISO
	 */
	public D3GeneratorBase(Model model, Model modelVISO) {
		super();
		this.modelAVM = model;
		this.modelVISO = modelVISO;
	}




	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#writeJSONToFile(java.lang.String)
	 */
	public void writeJSONToFile(String fileContent){
		try {
			String fileName = OGVICProcess.getInstance().getJsonFileNameRel();
			
			FileWriter writer = new FileWriter(fileName);
			writer.write(fileContent);
			writer.flush();
			writer.close();
			LOGGER.info("JSON written to " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#init(org.ontoware.rdf2go.model.Model)
	 */
	public void init(Model modelAVM) {
		this.modelAVM = modelAVM;
	}
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#generateJSONforD3()
	 */
	public abstract String generateJSONforD3();
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#getGenJSONFileName()
	 */
	public abstract String getGenJSONFileName();
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.avm2d3.D3Generator#getDefaultD3GraphicFile()
	 */
	public abstract String getDefaultD3GraphicFile();
}