package org.purl.rvl.tooling.d3vis.embeddedserver.context;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class ContextUtils {

	public static ContextHandlerCollection getContexts() {

		ContextHandlerCollection contexts = new ContextHandlerCollection();

		contexts.setHandlers(new Handler[] { new StaticContextBuilder().buildWebAppContext() });
		contexts.addHandler(new GenContextBuilder().buildWebAppContext());
		contexts.addHandler(new AppContextBuilder().buildWebAppContext());

		return contexts;
	}

}
