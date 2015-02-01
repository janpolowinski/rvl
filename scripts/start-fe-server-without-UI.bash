#!/bin/bash

echo "Start RVL Web-UI with Jetty ... "

cd ../build

java -cp rvl-tooling.jar org.purl.rvl.tooling.d3vis.embeddedserver.main.JettyStartWithoutUI