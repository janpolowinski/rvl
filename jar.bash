#!/bin/bash

#cd "org.purl.rvl.tooling.process"
#echo "Build jar with dependencies"
#mvn clean compile assembly:single
#mv target/rvl-process-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../build/rvl-tooling.jar
#cd ../

cd "org.purl.rvl.example"

echo "Build jar with dependencies"

mvn clean compile assembly:single -DskipTests

mv target/rvl-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../build/rvl-tooling-with-examples.jar

cd ../