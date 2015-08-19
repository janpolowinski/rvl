package org.purl.rvl.server;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class RVLServerResourceConfiguration extends ResourceConfig {

    public RVLServerResourceConfiguration() {
        super(MultiPartFeature.class);
        this.packages("org.purl.rvl.server");
        this.register("com.production.resource.ResponseCorsFilter");
    }
}
