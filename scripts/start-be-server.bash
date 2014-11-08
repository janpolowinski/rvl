#!/bin/bash

cd ../org.purl.rvl.server

# the tracing parameter may not yet work as desired
mvn exec:java  -Djersey.config.server.tracing.type=ALL