#!/bin/bash

cd "org.purl.rvl.tooling.rvl-interpreter"

echo "Build jar with dependencies"

mvn clean compile assembly:single

mv target/rvl-interpreter-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../build/rvl-tooling.jar

cd ../