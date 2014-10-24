package org.purl.rvl.tooling.d3vis.embeddedserver.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.purl.rvl.tooling.d3vis.embeddedserver.context.AppContextBuilder;
import org.purl.rvl.tooling.d3vis.embeddedserver.context.GenContextBuilder;
import org.purl.rvl.tooling.d3vis.embeddedserver.context.StaticContextBuilder;
import org.purl.rvl.tooling.d3vis.embeddedserver.server.JettyServer;


/**
 * @author Jan
 * based on the Article http://www.codeproject.com/Articles/128145/Run-Jetty-Web-Server-Within-Your-Application which 
 * is published under the CPOL: http://www.codeproject.com/info/cpol10.aspx
 */
public class JettyStartWithoutUI {

	public static void main(String[] args) {
		
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		contexts.setHandlers(new Handler[] { new StaticContextBuilder().buildWebAppContext() });
		contexts.addHandler(new GenContextBuilder().buildWebAppContext());
		contexts.addHandler(new AppContextBuilder().buildWebAppContext());

		final JettyServer jettyServer = new JettyServer();
		
		jettyServer.setHandler(contexts);
		
		try {
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (jettyServer.isStarted()) {
					try {
						jettyServer.stop();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}, "Stop Jetty Hook"));

	}
}