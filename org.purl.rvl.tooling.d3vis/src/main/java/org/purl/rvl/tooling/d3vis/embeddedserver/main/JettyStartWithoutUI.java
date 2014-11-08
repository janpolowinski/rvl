package org.purl.rvl.tooling.d3vis.embeddedserver.main;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.purl.rvl.tooling.d3vis.embeddedserver.context.ContextUtils;
import org.purl.rvl.tooling.d3vis.embeddedserver.server.JettyServer;


/**
 * @author Jan
 * based on the Article http://www.codeproject.com/Articles/128145/Run-Jetty-Web-Server-Within-Your-Application which 
 * is published under the CPOL: http://www.codeproject.com/info/cpol10.aspx
 */
public class JettyStartWithoutUI {

	public static void main(String[] args) {
		
		ContextHandlerCollection contexts = ContextUtils.getContexts();

		final JettyServer jettyServer = new JettyServer();
		
		jettyServer.setHandler(contexts);
		
		try {
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}