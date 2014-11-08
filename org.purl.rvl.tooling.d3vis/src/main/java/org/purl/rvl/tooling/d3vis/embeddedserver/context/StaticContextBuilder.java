package org.purl.rvl.tooling.d3vis.embeddedserver.context;

import org.eclipse.jetty.webapp.WebAppContext;

public class StaticContextBuilder {

	private WebAppContext staticAppContext;

	public WebAppContext buildWebAppContext() {

		String webDir = this.getClass().getResource("/web").toExternalForm();
		// seems to work even if not called from an object of the jar containing the "web" folder

		// Create static context to serve static files
		staticAppContext = new WebAppContext();
		staticAppContext.setResourceBase(webDir);
		staticAppContext.setContextPath("/semvis");
		return staticAppContext;

	}
}