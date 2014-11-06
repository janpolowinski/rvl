#!/bin/bash

echo "Building only the d3vis module ... "

cd org.purl.rvl.tooling.d3vis
mvn install -DskipTests
cd ..

./jar.bash

echo "Start RVL tooling console from jar ... "

cd build

java -cp rvl-tooling.jar org.purl.rvl.tooling.d3vis.embeddedserver.main.JettyStart &