package org.purl.rvl.tooling.d3vis.embeddedserver.context;

import org.eclipse.jetty.webapp.WebAppContext;

public class GenContextBuilder {

	private WebAppContext genAppContext;

	public WebAppContext buildWebAppContext() {

		// Create static context to serve static files
		genAppContext = new WebAppContext();
		// genAppContext.setDescriptor(genAppContext + "/WEB-INF/web.xml");
		genAppContext.setResourceBase("gen");
		genAppContext.setContextPath("/semvis/gen");
		return genAppContext;

	}
}