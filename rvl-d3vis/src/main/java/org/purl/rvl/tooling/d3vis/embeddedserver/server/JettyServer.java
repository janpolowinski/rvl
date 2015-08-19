package org.purl.rvl.tooling.d3vis.embeddedserver.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyServer {

	private Server server;
	
	public JettyServer() {
		this(8585);
	}
	public JettyServer(Integer port) {
		server = new Server(port);
	}
	
	public void setHandler(ContextHandlerCollection contexts) {
		server.setHandler(contexts);
	}
	
	public void start() throws Exception {
		server.start();
	}
	
	public void stop() throws Exception {
		server.stop();
		server.join();
	}
	
	public boolean isStarted() {
		return server.isStarted();
	}
	
	public boolean isStopped() {
		return server.isStopped();
	}
}