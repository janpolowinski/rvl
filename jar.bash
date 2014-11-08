#!/bin/bash

# build jar without examples
#cd "org.purl.rvl.tooling.process"

# build jar with examples
cd "org.purl.rvl.example"

echo "Build jar with dependencies"

mvn clean compile assembly:single -DskipTests

mv target/rvl-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../build/rvl-tooling.jar

cd ../