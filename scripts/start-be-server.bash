#!/bin/bash

cd ../org.purl.rvl.server

export MAVEN_OPTS="-Xmx2048M"

# the tracing parameter may not yet work as desired
mvn -U exec:java  -Djersey.config.server.tracing.type=ALL
